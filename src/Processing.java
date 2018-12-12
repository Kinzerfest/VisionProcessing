import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera;
import java.io.IOException;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import util.VisionThread;

public class Processing {
    static final int IMG_WIDTH = 640;
    static double CAMERA_FOV = 100;
    static double angleError;

    public static void main(String[] args){
        map();
        UsbCamera camera = new UsbCamera("usb", 0);
        /*
        final Object imgLock = new Object();
        new VisionThread(new UsbCamera("Back Camera", 1), new Pipeline(), pipeline -> {
            if (!pipeline.filterContoursOutput().isEmpty()) {
                Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    angleError = (CAMERA_FOV / IMG_WIDTH) * ((r.x + (r.width / 2.0)) - (IMG_WIDTH / 2.0));
                    System.out.println("Object detected at " + angleError + " degrees" + ", or " + (r.x + (r.width / 2.0) + " pixels"));
                }
            } else {
                synchronized (imgLock) {
                    angleError = 0;
                    System.out.println("Object not found");
                }
            }
        }).start();
        */
        }

        static void map(){
            String javaLibPath = System.getProperty("java.library.path");
            Map<String, String> envVars = System.getenv();
            System.out.println("env vars path: " + envVars.get("Path"));
            System.out.println("Java lib path: " + javaLibPath);
            for (String var : envVars.keySet()) {
                System.err.println("examining " + var);
                if (envVars.get(var).equals(javaLibPath)) {
                    System.out.println(var);
                }
            }
        }
        }

