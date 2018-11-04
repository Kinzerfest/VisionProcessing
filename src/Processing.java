import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import javax.swing.*;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.rectangle;

public class Processing {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static final double DISTANCE_CONSTANT = 1;

    static PipelineWPI tracker;
    public static VideoCapture videoCapture;
    private static HttpStreamServer httpStreamService;
    static double centerX;

    public static void main(String[] args) throws IOException {
        videoCapture = new VideoCapture();
        tracker = new PipelineWPI();
        videoCapture.open(1);
        Mat input = new Mat();
        videoCapture.read(input);
        httpStreamService = new HttpStreamServer(input);
        httpStreamService.startStreamingServer();
        //videoCapture.get(4);

        while (videoCapture.isOpened()) {
            videoCapture.read(input);
            tracker.process(input);
            if (!tracker.filterContoursOutput().isEmpty()) {
                Rect r = Imgproc.boundingRect(tracker.filterContoursOutput().get(0));
                System.out.println(r.x + (r.width / 2));
                rectangle(input, r.tl(), r.br(), new Scalar(0, 0, 255),10, 8,0);

            }

            httpStreamService.pushImage(input);
        }
    }

}
//videoCapture.release();
//System.exit(0);

