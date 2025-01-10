from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
import easyocr
import io
from PIL import Image

app = FastAPI()

# 配置CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 初始化 EasyOCR
reader = easyocr.Reader(['ch_sim', 'en'])

@app.post("/ocr")
async def ocr(image: UploadFile = File(...)):
    # 读取上传的图片
    contents = await image.read()
    img = Image.open(io.BytesIO(contents))
    
    # 执行OCR识别
    result = reader.readtext(img)
    
    # 提取文本
    text = "\n".join([item[1] for item in result])
    
    return {"text": text} 