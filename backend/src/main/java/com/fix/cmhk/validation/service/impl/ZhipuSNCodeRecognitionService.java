package com.fix.cmhk.validation.service.impl;

import com.fix.cmhk.validation.model.SNCodeResponse;
import com.fix.cmhk.validation.service.SNCodeRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ZhipuSNCodeRecognitionService implements SNCodeRecognitionService {
    private static final Logger logger = LoggerFactory.getLogger(ZhipuSNCodeRecognitionService.class);

    @Value("${python.script.path:/Users/itadmin/cursor/validate_2.0/backend/scripts}")
    private String scriptPath;

    @Value("${python.command:python}")
    private String pythonCommand;

    /**
     * 尝试查找可用的 Python 命令
     */
    private String findPythonCommand() {
        List<String> possibleCommands = Arrays.asList("python3", "python", "python3.8", "python3.9", "python3.10", "python3.11");
        
        for (String cmd : possibleCommands) {
            try {
                Process process = new ProcessBuilder(cmd, "--version")
                    .redirectErrorStream(true)
                    .start();
                
                boolean completed = process.waitFor(5, TimeUnit.SECONDS);
                if (completed && process.exitValue() == 0) {
                    logger.info("找到可用的 Python 命令: {}", cmd);
                    return cmd;
                }
            } catch (Exception e) {
                logger.debug("Python 命令 {} 不可用", cmd);
            }
        }
        
        logger.warn("未找到可用的 Python 命令，将使用配置的默认命令: {}", pythonCommand);
        return pythonCommand;
    }

    @Override
    public SNCodeResponse recognizeSNCode(String base64Image) {
        Path tempFile = null;
        Process process = null;
        
        try {
            // 创建临时文件存储 base64 图片数据
            tempFile = Files.createTempFile("sn_image_", ".txt");
            Files.write(tempFile, base64Image.getBytes(StandardCharsets.UTF_8));

            // 检查脚本文件是否存在
            Path scriptFilePath = Paths.get(scriptPath, "aliQwen_funcall_v2_SN.py");
            if (!Files.exists(scriptFilePath)) {
                logger.error("Python 脚本文件不存在: {}", scriptFilePath);
                return SNCodeResponse.builder()
                    .success(false)
                    .message("Python 脚本文件不存在")
                    .snCode("NA")
                    .build();
            }

            // 查找可用的 Python 命令
            String pythonCmd = findPythonCommand();
            
            // 构建命令列表
            List<String> command = new ArrayList<>();
            command.add(pythonCmd);
            command.add(scriptFilePath.toString());
            command.add(tempFile.toString());

            // 构建 ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(scriptPath));
            processBuilder.redirectErrorStream(true);

            logger.info("开始执行 Python 脚本: {}", command);

            // 执行命令
            process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 等待进程完成，设置超时时间为 10 秒
            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            
            if (!completed) {
                logger.error("Python 脚本执行超时");
                process.destroyForcibly();
                return SNCodeResponse.builder()
                    .success(false)
                    .message("识别超时")
                    .snCode("NA")
                    .build();
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                logger.error("Python 脚本执行失败，退出码: {}, 输出: {}", exitCode, output);
                return SNCodeResponse.builder()
                    .success(false)
                    .message("识别失败: " + output)
                    .snCode("NA")
                    .build();
            }

            // 解析输出获取 SN 码
            String snCode = output.toString().trim();
            logger.info("识别到的 SN 码: {}", snCode);

            return SNCodeResponse.builder()
                .success(true)
                .message("识别成功")
                .snCode(snCode.isEmpty() ? "NA" : snCode)
                .build();

        } catch (Exception e) {
            logger.error("执行 Python 脚本异常", e);
            return SNCodeResponse.builder()
                .success(false)
                .message("识别异常: " + e.getMessage())
                .snCode("NA")
                .build();
        } finally {
            // 清理资源
            try {
                if (tempFile != null) {
                    Files.deleteIfExists(tempFile);
                }
                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }
            } catch (Exception e) {
                logger.warn("清理资源失败", e);
            }
        }
    }
} 