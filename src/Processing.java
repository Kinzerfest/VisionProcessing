import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Processing {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static final int CAMERA_FOV = 100;

    static Pipeline tracker;
    public static VideoCapture videoCapture;

    public static void main(String[] args) {
        videoCapture = new VideoCapture();
        tracker = new Pipeline();
        videoCapture.open(1);
        Mat input = new Mat();
        while (videoCapture.isOpened()) {
            videoCapture.read(input);
            tracker.process(input);
            if (!tracker.filterContoursOutput().isEmpty()) {
                Rect boundingRect = Imgproc.boundingRect(tracker.filterContoursOutput().get(0));
                double offset = (CAMERA_FOV / videoCapture.get(3)) * ((boundingRect.x + (boundingRect.width / 2.0)) - (videoCapture.get(3) / 2.0));
                System.out.println("You are " + offset + " degrees from the target.");
            }
        }
    }

}


