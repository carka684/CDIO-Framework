import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.video.KalmanFilter;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.detection.impl.DefaultDetection;


public class TrackingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		

		IDetection detection = new DefaultDetection();
		detection.init();
		Mat blackim = Mat.ones(1152, 720, CvType.CV_16S); //black images for background modeling
		for (int i = 0; i < 5; i++)
		{
			detection.getObjInImage(blackim);
		}
		KalmanFilter kf = new KalmanFilter(4,2,0,CvType.CV_32F);
	
		
		for(int i = 1; i<6; i++){
			
			// Ladda in bild till Mat
			
			Mat img = Highgui.imread("/Users/annasoederroos/TSBB11/square"+i+".jpg");

			DetectionResult result = detection.getObjInImage(img);
			// ta fram mittpunkt som x och y plus halva width, height
			
			int x = result.regions.get(0).x + result.regions.get(0).width/2;
			int y = result.regions.get(0).y + result.regions.get(0).height/2;

			Point measPoint = new Point(x,y);
			Mat m = kf.predict();
			System.out.println("pred "+ m.dump());
			Point corrPoint = KalmanFilterCorrect(kf, measPoint);
			//System.out.println(measPoint +""+ corrPoint);
	
		}
		
	}

	
	private static Point KalmanFilterCorrect(KalmanFilter kf, Point p)
	{
		Mat measurement = new Mat(2,1, CvType.CV_32F);
		 measurement.put(0, 0, p.x);
		 measurement.put(1, 0, p.y);
		 Mat correction = kf.correct(measurement);
		 Point correctedPoint = new Point();
		 //System.out.println(correction.dump());
		 correctedPoint.x = correction.get(0,0)[0];
		 correctedPoint.y = correction.get(1,0)[0];
		return correctedPoint;
	}

}
