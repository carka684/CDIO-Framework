import java.util.Iterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

import com.atul.JavaOpenCV.Imshow;

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
	static Vector<KalmanFilter> kalVec;
	static int nextID;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
	}
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


		IDetection detection = new DefaultDetection();
		detection.init();		
		init();
		ImageReader reader = new ImageReader();
		reader.readImages("C:/Users/Calle/Documents/MATLAB/images/");
		Vector<String> files = reader.getFilesVec();
		int k = 0;
		Imshow show = new Imshow("");
		KalmanTracking tracker = new KalmanTracking();
		KalmanTracking.init();
		for(String file : files){
			Mat img = Highgui.imread(file);
			DetectionResult result = detection.getObjInImage(img);
			// Ladda in bild till Mat
			if(k++ > 2)	
			{
				tracker.trackRegions(result, img, show);
			}
		}
	}
}

