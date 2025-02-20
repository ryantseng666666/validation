import sys
import json
import base64
import logging
import traceback
from datetime import datetime
from dashscope import ImageModel

# 配置日志
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('sn_recognition.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

def setup_api_key():
    try:
        # 这里替换为你的API key
        api_key = 'sk-9a005b5e8ae14d85ac230b3dbad58da9'
        logger.info("API key 配置成功")
        return api_key
    except Exception as e:
        logger.error(f"API key 配置失败: {str(e)}")
        raise

def read_image(file_path):
    try:
        logger.info(f"开始读取图片文件: {file_path}")
        with open(file_path, 'r') as f:
            base64_str = f.read().strip()
        
        # 验证base64字符串
        try:
            image_data = base64.b64decode(base64_str)
            logger.info(f"图片文件读取成功，大小: {len(image_data)} bytes")
            return base64_str
        except Exception as e:
            logger.error(f"Base64解码失败: {str(e)}")
            raise
    except Exception as e:
        logger.error(f"读取图片文件失败: {str(e)}")
        raise

def process_image(api_key, base64_str):
    try:
        logger.info("开始处理图片...")
        
        # 构建请求消息
        messages = [{
            'role': 'user',
            'content': '请识别图片中的SN码。SN码通常是一串字母和数字的组合，可能包含连字符。'
        }]
        
        # 调用API
        logger.info("调用通义千问API...")
        response = ImageModel.call(
            model='qwen-vl-plus',
            messages=messages,
            image=base64_str,
            api_key=api_key
        )
        
        logger.info(f"API响应: {response}")
        
        # 解析响应
        if response.status_code == 200:
            logger.info("API调用成功")
            content = response.output.choices[0].message.content
            logger.info(f"识别结果: {content}")
            
            # 尝试提取SN码
            sn_code = extract_sn_code(content)
            logger.info(f"提取的SN码: {sn_code}")
            
            return sn_code
        else:
            logger.error(f"API调用失败: {response.status_code}")
            raise Exception(f"API调用失败: {response.status_code}")
            
    except Exception as e:
        logger.error(f"处理图片失败: {str(e)}")
        logger.error(f"详细错误: {traceback.format_exc()}")
        raise

def extract_sn_code(content):
    try:
        logger.info("开始提取SN码...")
        # 如果返回的是JSON格式
        try:
            data = json.loads(content)
            if 'snCode' in data:
                return data['snCode']
        except json.JSONDecodeError:
            logger.info("响应不是JSON格式，尝试直接提取")
        
        # 直接从文本中提取
        import re
        # SN码模式：字母数字和连字符的组合，长度通常在8-20之间
        pattern = r'[A-Z0-9-]{8,20}'
        matches = re.findall(pattern, content.upper())
        
        if matches:
            logger.info(f"找到可能的SN码: {matches[0]}")
            return matches[0]
        
        logger.warning("未找到符合格式的SN码")
        return "NA"
        
    except Exception as e:
        logger.error(f"提取SN码失败: {str(e)}")
        return "NA"

def main():
    try:
        logger.info("开始执行SN码识别...")
        
        # 检查命令行参数
        if len(sys.argv) != 2:
            logger.error("使用方法: python script.py <image_file_path>")
            sys.exit(1)
            
        image_file = sys.argv[1]
        logger.info(f"输入文件: {image_file}")
        
        # 设置API key
        api_key = setup_api_key()
        
        # 读取图片
        base64_str = read_image(image_file)
        
        # 处理图片
        result = process_image(api_key, base64_str)
        
        # 输出结果
        print(result)
        logger.info(f"处理完成，结果: {result}")
        
    except Exception as e:
        logger.error(f"程序执行失败: {str(e)}")
        print(f"NA")
        sys.exit(1)

if __name__ == "__main__":
    main()