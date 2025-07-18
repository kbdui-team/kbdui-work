#  test1.py  的优化版本

import whisper
from pydub import AudioSegment
import zhconv  #  简繁转换库

def transcribe_with_preprocess(input_path, model_size="small", language="zh"):
    """
    优化版转录流程（预处理+CPU推理+简繁转换）
    
    参数：
        input_path: 输入音频/视频文件路径
        model_size: 模型大小(tiny/base/small/medium/large)
        language: 指定语言(zh表示中文)
    
    返回：
        {
            "text": "简体中文文本",
            "segments": [分段信息],
            "audio_info": 音频参数
        }
    """
    # === 1.  音频预处理 ===
    print("⏳ 正在预处理音频...")
    audio = AudioSegment.from_file(input_path)
    
    #  统一转换为：16kHz采样率，单声道，pcm_s16le编码（Whisper最佳兼容格式）
    processed_audio = audio.set_frame_rate(16000) \
                         .set_channels(1) \
                         .set_sample_width(2)  # 16bit
    
    #  生成临时文件（避免内存占用）
    temp_path = "temp_16k.wav"
    processed_audio.export(temp_path, 
                          format="wav",
                          codec="pcm_s16le")
    
    # === 2.  加载模型（显式使用CPU） ===
    print("⏳ 正在加载模型...")
    model = whisper.load_model(model_size, device="cpu")
    
    # === 3.  执行转录 ===
    print("⏳ 正在识别内容...")
    result = model.transcribe(
        temp_path,
        language=language,  #  强制指定中文
        fp16=False,         #  CPU必须关闭FP16
        verbose=True        #  显示进度
    )
    
    # === 4.  简繁转换 ===
    print("⏳ 正在转换简体中文...")
    simplified_text = zhconv.convert(result["text"], 'zh-cn')
    
    #  转换分段文本
    for seg in result["segments"]:
        seg["text"] = zhconv.convert(seg["text"], 'zh-cn')
    
    # === 5.  清理临时文件 ===
    import os
    os.remove(temp_path)
    
    return {
        "text": simplified_text,
        "segments": result["segments"],
        "audio_info": {
            "length": len(audio) / 1000,  #  秒
            "sample_rate": processed_audio.frame_rate,
            "channels": processed_audio.channels
        }
    }

#  使用示例
if __name__ == "__main__":

    result = transcribe_with_preprocess(
        input_path="vv.mp4",
        model_size="medium"
    )
    
    print("\n最终结果:")
    print(result["text"])
    
    # 保存为简体中文文件
    with open("result.txt", "w", encoding="utf-8") as f:
        f.write(result["text"])
    print("结果已保存为 result.txt")