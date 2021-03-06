import java.io.IOException;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Processing {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static final boolean PUSH_TO_HTTP = true;
    static final double DISTANCE_CONSTANT = 1;
    static final int CAMERA_FOV = 100;

    public static void main(String[] args) throws IOException {
        HttpStreamServer httpStreamService;
        VideoCapture videoCapture = new VideoCapture();
        Pipeline tracker = new Pipeline();
        videoCapture.open(1);
        Mat input = new Mat();
        videoCapture.read(input);
        if (PUSH_TO_HTTP) {
            httpStreamService = new HttpStreamServer(input);
            httpStreamService.startStreamingServer();
        }
        while (videoCapture.isOpened()) {
            videoCapture.read(input);
            tracker.process(input);
            if (!tracker.filterContoursOutput().isEmpty()) {
                Rect boundingRect = Imgproc.boundingRect(tracker.filterContoursOutput().get(0));
                Point contourCenter = new Point(boundingRect.x + (boundingRect.width / 2.0), boundingRect.y + (boundingRect.height / 2.0));
                Imgproc.drawMarker(input, contourCenter, new Scalar(0, 0, 255));
                //Imgproc.rectangle(input,boundingRect.br(), boundingRect.tl(), new Scalar(0,0,255), 4);
                //Imgproc.drawContours(input, tracker.filterContoursOutput(), -1, new Scalar(0, 0, 255), 4);
                double offset = (CAMERA_FOV / videoCapture.get(3)) * ((boundingRect.x + (boundingRect.width / 2.0)) - (videoCapture.get(3) / 2.0));
                Imgproc.putText(input, Integer.toString((int)offset) + " deg", contourCenter, 0, 0.65, new Scalar(0,0,255), 2);
                //System.out.println("You are " + offset + " degrees from the target.");
                //System.out.println("Area of contour: " + Imgproc.contourArea(tracker.filterContoursOutput().get(0)));
                //System.out.println("Area of bounding box: " + boundingRect.area());
            }
            if (PUSH_TO_HTTP) httpStreamService.pushImage(input);
        }
    }
}


