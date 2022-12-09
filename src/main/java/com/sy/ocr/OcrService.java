package com.sy.ocr;

import ai.djl.modality.cv.Image;
import com.sy.common.ImageUtils;
import com.sy.common.ModelUrlUtils;
import io.github.greycode.OCRPredictResult;
import io.github.greycode.OcrDriver;
import io.github.greycode.PaddleOcr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sy
 * @date 2022/11/13 22:02
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {
    private final OcrProperties prop;

    /**
     * 初始化加载ocr模型
     */
    @PostConstruct
    private void init() throws IOException {
        System.out.println("加载Ocr执行文件...");
        loadOcrDriver();
    }

    /**
     * ocr
     * @param imgFile
     * @return
     */
    public List<OCRPredictResult> ocr(String imgFile) {
        List<List<OCRPredictResult>> OCRPredictResultList = OcrDriver.ocr(imgFile);
        return OCRPredictResultList.get(0);
    }

    /**
     * 结果图片
     * @param image 图片
     * @return image
     */
    public BufferedImage createResultImage(Image image, List<OCRPredictResult> ocrEntries) {
        return ImageUtils.drawDetectionResults((BufferedImage) image.getWrappedImage(), ocrEntries);
    }

    /**
     * 加载模型
     */
    public void loadOcrDriver() throws IOException {
        String jniLib = "PaddleOcrJni.dll";
        URL url = OcrService.class.getClassLoader().getResource(jniLib);
        System.setProperty("java.library.path", url.getPath().replace(jniLib, ""));
        PaddleOcr paddleOcr = new PaddleOcr();
        paddleOcr.setUseMkldnn(true);
        paddleOcr.setLabelPath(ModelUrlUtils.getRealUrl(prop.getPpocrKeys()).replaceFirst("file:/", ""));
        paddleOcr.initAll();
        OcrDriver.initializeDefaultModel(paddleOcr);
    }

}
