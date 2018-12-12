package util;

import org.opencv.core.Mat;

public interface VisionPipeline {
    void process(Mat image);
}
