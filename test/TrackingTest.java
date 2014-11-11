import java.util.Iterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.Detections;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.detection.impl.DefaultDetection;
import edu.wildlifesecurity.framework.identification.impl.ImageReader;
import edu.wildlifesecurity.framework.tracking.impl.KalmanTracking;


public class TrackingTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	static Vector<KalmanFilter> kalVec;
	static int nextID;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
	}
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture vc = new VideoCapture("C:/Camera1_2.avi");
		
		IDetection detection = new DefaultDetection();
		detection.init();		
		init();
		//ImageReader reader = new ImageReader();
		//reader.readImages("C:/Users/Calle/Documents/MATLAB/images/");
		//Vector<String> files = reader.getFilesVec();
		
		Imshow show = new Imshow("");
		KalmanTracking tracker = new KalmanTracking();
		KalmanTracking.init();
		Mat img = new Mat();
		int k = 0;

		System.out.println(vc.isOpened());
		for(int frameNr = 0; frameNr < vc.get(7) - 1; frameNr++)
		{
			vc.read(img);
			Detections detections = detection.getObjInImage(img);
			// Ladda in bild till Mat
			if(k++ > 10)	
			{
				tracker.trackRegions(detections, img, show);
			}
		}
	}
}

