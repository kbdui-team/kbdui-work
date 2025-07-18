#  视频转文字  
#  中文繁体、英文

import whisper

def transcribe_audio(audio_path, model_size="small"):
    #  强制使用CPU并加载模型
    model = whisper.load_model(model_size, device="cpu")
    
    #  禁用FP16（CPU不支持）
    result = model.transcribe(audio_path, fp16=False)
    
    #  返回文本和分段信息
    return {
        "text": result["text"],
        "segments": result["segments"]
    }

#  使用示例
output = transcribe_audio("Susan.mp3")
print(output["text"])