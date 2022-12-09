package com.sy.common;

import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.alibaba.fastjson2.JSON;
import io.github.greycode.OCRPredictResult;
import lombok.experimental.UtilityClass;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.awt.Image.SCALE_DEFAULT;

/**
 * @author sy
 * @date 2022/9/13 21:24
 */
@UtilityClass
public class ImageUtils {

    private static final Map<Integer, Color> colorMap = Map.of(
            0, new Color(200, 0, 0),
            1, new Color(0, 200, 0),
            2, new Color(0, 0, 200),
            3, new Color(200, 200, 0),
            4, new Color(200, 0, 200),
            5, new Color(0, 200, 200)
    );

    private static void drawText(Graphics2D g, String className, double probability, int x, int y, int width) {
        //设置水印的坐标
        String showText = String.format("%s %.0f%%", className, probability * 100);
        g.fillRect(x, y - 30, width, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 25));//设置字体
        g.drawString(showText, x, y - 10);
    }

    /**
     * 对ocr的结果在图片中绘制
     * @param image
     * @param ocrEntries
     * @return
     */
    public static BufferedImage drawDetectionResults(BufferedImage image, List<OCRPredictResult> ocrEntries) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        int stroke = 2;
        g.setStroke(new BasicStroke(stroke));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(OCRPredictResult ocrEntry:ocrEntries) {
            String className = ocrEntry.getText();
            double probability = ocrEntry.getScore();
            List<List<Integer>> box = ocrEntry.getBox();
            box = (List<List<Integer>>) JSON.parse(JSON.toJSONString(box));
            Color color = colorMap.get(Math.abs(className.hashCode() % 6));
            g.setPaint(color);
            Integer x = box.get(0).get(0);
            Integer y = box.get(0).get(1);
            int width = box.get(1).get(0) - x;
            int height = box.get(3).get(1) - y;
            g.drawRect(x, y, width, height);
            drawText(g, className, probability, x, y, width);
        }
        g.dispose();
        return image;
    }

}

