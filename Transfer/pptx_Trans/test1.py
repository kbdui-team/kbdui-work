#  依次获取各张幻灯片

import collections.abc
from pptx import Presentation

pre1 = Presentation('pptx_Trans\\RT-Thread.pptx')

print(pre1)
slide_list = pre1.slides
for slide in slide_list:
    print(slide)

