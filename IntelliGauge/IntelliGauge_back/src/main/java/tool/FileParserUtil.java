package tool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileParserUtil {

    // 定义题目生成要求（可在实际使用中根据需求调整）
    public static final String QUESTION_GENERATION_PROMPT =
            "作为教育专家，请根据文档内容生成%d道选择题：\n\n" +
                    "要求：\n" +
                    "1. 题目必须基于文档核心内容\n" +
                    "2. 每道题4个选项，其中1个正确，3个合理干扰项\n" +
                    "3. 答案使用单个大写字母标识（如：\"A\"）\n" +
                    "4. 输出格式：严格JSON数组：[{\"question\": \"\", \"options\": [\"A.选项1\", \"B.选项2\", ...], \"answer\": \"A\"}]";

    public static String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        try (InputStream inputStream = file.getInputStream()) {
            switch (extension) {
                case "pdf":
                    return parsePdf(inputStream);
                case "ppt":
                    return parseLegacyPpt(inputStream);
                case "pptx":
                    return parsePptx(inputStream);
                case "txt":
                case "md":
                case "html":
                    return parseText(inputStream);
                default:
                    throw new IllegalArgumentException("不支持的文件格式: " + extension);
            }
        }
    }

    // 文本文件解析
    private static String parseText(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static String parsePdf(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                content.append(stripper.getText(document)).append("\n");
            }
        }
        return content.toString();
    }

    private static String parseLegacyPpt(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (HSLFSlideShow slideShow = new HSLFSlideShow(inputStream)) {
            for (var slide : slideShow.getSlides()) {
                for (var shape : slide.getShapes()) {
                    if (shape instanceof HSLFTextShape) {
                        content.append(((HSLFTextShape) shape).getText()).append("\n");
                    }
                }
            }
        }
        return content.toString();
    }

    private static String parsePptx(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (XMLSlideShow slideShow = new XMLSlideShow(inputStream)) {
            for (var slide : slideShow.getSlides()) {
                for (var shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        content.append(((XSLFTextShape) shape).getText()).append("\n");
                    }
                }
            }
        }
        return content.toString();
    }

    // 构建题目生成Prompt
    public static String buildQuestionPrompt(String content, int questionCount) {
        return String.format(QUESTION_GENERATION_PROMPT, questionCount) +
                "\n\n文档内容：\n" + content;
    }
}
