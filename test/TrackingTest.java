import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.detection.impl.DefaultDetection;
import edu.wildlifesecurity.framework.identification.impl.ImageReader;
import edu.wildlifesecurity.framework.tracking.impl.KalmanTracking;


public class TrackingTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	static Vector<KalmanFilter_test> kalVec;
	static int nextID;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter_test>();
	}
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture vc = new VideoCapture("/Users/annasoederroos/TSBB11/Camera1_2.mp4");
		
		IDetection detection = new DefaultDetection();
		detection.init();		
		init();
		//ImageReader reader = new ImageReader();
		//reader.readImages("C:/Users/Calle/Documents/MATLAB/images/");
		//Vector<String> files = reader.getFilesVec();
		
		Imshow show = new Imshow("");
		KalmanTracking tracker = new KalmanTracking();
		tracker.init();
		Mat img = new Mat();
		int k = 0;

		System.out.println(vc.isOpened());
		//for(String file : files)
		//{
		for(int frameNr = 0; frameNr < vc.get(7) - 1; frameNr++)
		{
			vc.read(img);
			//img = Highgui.imread(file);
			DetectionResult detections = detection.getObjInImage(img);
			// Ladda in bild till Mat
			if(k++ > 2)	
			{
				tracker.trackRegions(detections, img);
				for(Detection dec : detections.getVector())
				{
					Core.rectangle(img, dec.getRegion().tl(), dec.getRegion().br(),dec.getColor(),5);
				}
				show.showImage(img);
			}
			Thread.sleep(0);
		}
	}
}

