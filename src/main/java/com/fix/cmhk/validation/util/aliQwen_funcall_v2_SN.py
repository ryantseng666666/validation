import base64
import csv
import datetime
import json
import logging
import os
from random import random

import dashscope
import requests
from dashscope import MultiModalConversation, Generation


#打印SN信息
def printSNInfo(SN:str):
    result = {}
    result['SN'] = SN
    # print(result)
    return result

# 配置日志
def setup_logging():
    current_time = datetime.datetime.now()
    formatted_date = current_time.strftime("%Y-%b-%d %H:%M:%S") 
    current_date = datetime.datetime.now().strftime("%Y%m%d")
    logging.basicConfig(filename='app.log', level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
    return current_date, formatted_date

# API配置
def setup_api():
    dashscope.api_key = ""
    api_url = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
    api_key = ""
    return api_url, api_key

# 工具定义
tools = [
    {
        "type": "function",
        "function": {
            "name": "printSNInfo",
            "description": "SN码,打印SN信息",
            "parameters": {
                "type": "object",
                "properties": {
                    "SN": {
                        "description": "SN",
                        "type": "String"
                    }
                },
                "required": ["SN"]
            }
        }
    }
]

# 提示词定义
prompt = '''
please extract SN from the input ONT images.

The SN value is typically a combination of letters and numbers, which may contain hyphens. You should always follow the instructions and output a valid JSON object.

only return the json format schema, which could be inferred by the following OpticalPowerInfo class

public class SNInfo {  
    String SN;  
}  

If you do not recognize any value or recognize any value with low confidence, 
set the corresponding value NA for SN.
'''

def get_sn_from_image(base64_image):
    """
    从ONT图片中提取SN码
    
    Args:
        base64_image: base64编码后的图片数据
        
    Returns:
        str: SN码，如果识别失败返回"NA"
    """
    try:
        # 调用通义千问模型
        qwen_response = qwenRecoV1(base64_image)
        
        # 构造消息
        messages = [{
            "role": "user",
            "content": qwen_response
        }]
        
        # 调用模型获取结果
        response = get_response(messages)
        
        if response is not None:
            assistant_output = response['output']['choices'][0]['message']
            
            # 如果有工具调用
            if 'tool_calls' in assistant_output:
                if assistant_output['tool_calls'][0]['function']['name'] == 'printSNInfo':
                    args = json.loads(assistant_output['tool_calls'][0]['function']['arguments'])
                    sn = args.get('SN', 'NA')
                    return sn
                    
        return "NA"
        
    except Exception as e:
        logging.error(f'处理图片失败: {str(e)}')
        return "NA"

def qwenRecoV1(base64_image):
    messages = [{
        'role': 'user',
        'content': [
            {
                'image': "data:image/jpeg;base64," + base64_image
            },
            {
                'text': prompt
            },
        ]
    }]
    response = MultiModalConversation.call(model='qwen-vl-max', messages=messages)
    content = response['output']['choices'][0]['message']['content'][0]["text"]
    return content

def get_response(messages):
    url = 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation'
    headers = {
        'Content-Type': 'application/json',
        'Authorization':f'Bearer {dashscope.api_key}'
    }
    body = {
        'model': 'qwen-max',
        "input": {
            "messages": messages
        },
        "parameters": {
            "result_format": "message",
            "tools": tools
        }
    }
    response = requests.post(url, headers=headers, json=body)
    return response.json()

def process_sn_image(image_path):
    """
    处理图片并识别SN码
    
    Args:
        image_path: 图片路径
    Returns:
        str: 识别到的SN码
    """
    # 设置API密钥
    setup_api()
    
    # 读取并编码图片
    with open(image_path, "rb") as image_file:
        base64_image = base64.b64encode(image_file.read()).decode('utf-8')
    
    # 识别SN码
    sn = get_sn_from_image(base64_image)
    print(sn)
    return sn

if __name__ == '__main__':
    # 示例用法
    image_path = "/Users/itadmin/cursor/validate_2.0/backend/data/sn/sn2.jpg"
    process_sn_image(image_path)