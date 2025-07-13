#  视频转文字  
#  长视频 分段处理

import whisper
from whisper.utils import get_writer

def transcribe_long_audio(input_path, output_dir="output"):
    # 使用更节省内存的 base 模型
    model = whisper.load_model("base", device="cpu")
    
    # 分段处理（每5分钟一个chunk）
    result = model.transcribe(
        input_path,
        fp16=False,
        chunk_size=300,
        verbose=True
    )
    
    #  保存为TXT和SRT字幕
    txt_writer = get_writer("txt", output_dir)
    txt_writer(result, input_path)
    
    srt_writer = get_writer("srt", output_dir)
    srt_writer(result, input_path)
    
    return result["text"]

#  处理2小时的长音频
#  需要修改文件名
transcribe_long_audio("*")