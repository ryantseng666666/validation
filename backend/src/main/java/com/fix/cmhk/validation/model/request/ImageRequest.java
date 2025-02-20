package com.fix.cmhk.validation.model.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 图片识别请求类
 */
@Data
public class ImageRequest {
    
    @NotBlank(message = "图片数据不能为空")
    @Size(max = 5242880, message = "图片大小不能超过5MB") 
    private String base64Image;
} 