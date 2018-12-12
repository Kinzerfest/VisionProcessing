package util;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.VideoSource;
import org.opencv.core.Mat;

public class VisionRunner<P extends VisionPipeline> {
    private final CvSink m_cvSink = new CvSink("VisionRunner CvSink");
    private final P m_pipeline;
    private final Mat m_image = new Mat();
    private final Listener<? super P> m_listener;
    private volatile boolean m_enabled = true;

    /**
     * Listener interface for a callback that should run after a pipeline has processed its input.
     *
     * @param <P> the type of the pipeline this listener is for
     */
    @FunctionalInterface
    public interface Listener<P extends VisionPipeline> {

        /**
         * Called when the pipeline has run. This shouldn't take much time to run because it will delay
         * later calls to the pipeline's {@link VisionPipeline#process process} method. Copying the
         * outputs and code that uses the copies should be <i>synchronized</i> on the same mutex to
         * prevent multiple threads from reading and writing to the same memory at the same time.
         *
         * @param pipeline the vision pipeline that ran
         */
        void copyPipelineOutputs(P pipeline);

    }

    /**
     * Creates a new vision runner. It will take images from the {@code videoSource}, send them to
     * the {@code pipeline}, and call the {@code listener} when the pipeline has finished to alert
     * user code when it is safe to access the pipeline's outputs.
     *
     * @param videoSource the video source to use to supply images for the pipeline
     * @param pipeline    the vision pipeline to run
     * @param listener    a function to call after the pipeline has finished running
     */
    public VisionRunner(VideoSource videoSource, P pipeline, Listener<? super P> listener) {
        this.m_pipeline = pipeline;
        this.m_listener = listener;
        m_cvSink.setSource(videoSource);
    }

    /**
     * Runs the pipeline one time, giving it the next image from the video source specified
     * in the constructor. This will block until the source either has an image or throws an error.
     * If the source successfully supplied a frame, the pipeline's image input will be set,
     * the pipeline will run, and the listener specified in the constructor will be called to notify
     * it that the pipeline ran.
     *
     * <p>This method is exposed to allow teams to add additional functionality or have their own
     * ways to run the pipeline. Most teams, however, should just use {@link #runForever} in its own
     * thread using a {@link VisionThread}.</p>
     */
    public void runOnce() {
        long frameTime = m_cvSink.grabFrame(m_image);
            m_pipeline.process(m_image);
            m_listener.copyPipelineOutputs(m_pipeline);
    }

    /**
     * A convenience method that calls {@link #runOnce()} in an infinite loop. This must
     * be run in a dedicated thread, and cannot be used in the main robot thread because
     * it will freeze the robot program.
     *
     * <p><strong>Do not call this method directly from the main thread.</strong></p>
     *
     * @throws IllegalStateException if this is called from the main robot thread
     * @see VisionThread
     */
    public void runForever() {
        while (m_enabled && !Thread.interrupted()) {
            runOnce();
        }
    }

    /**
     * Stop a RunForever() loop.
     */
    public void stop() {
        m_enabled = false;
    }
}

