import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.video.KalmanFilter;

import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.detection.impl.DefaultDetection;


public class TrackingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		IDetection detection = new DefaultDetection();
		detection.init();
		
		for(int i = 0; i<5; i++){
			
			// Ladda in bild till Mat
			
			
			DetectionResult result = detection.getObjInImage(Highgui.imread("/Users/annasoederroos/TSBB11/square"+i+".jpg"));
			// ta fram mittpunkt som x och y plus halva width, height
			
			int x = result.regions.get(i).x + result.regions.get(i).width;
			int y = result.regions.get(i).y + result.regions.get(i).height;
			Point p = new Point(x,y);
			KalmanFilter kf = initKalmanFilter(p);
			Point predPoint = KalmanFilterPredict(kf,p);
			
		}
		
	}
	
	private static KalmanFilter initKalmanFilter(Point p) 
	{
		
		KalmanFilter kf = new KalmanFilter(4,2,0,CvType.CV_32F);
		MatOfFloat state = new MatOfFloat(4,1);
		MatOfFloat measurement = new MatOfFloat(2,1);
		System.out.println("here");
		measurement.put(0, 0, p.x);
		measurement.put(0, 1, p.y);

		return kf;
	}
	
	private static Point KalmanFilterPredict(KalmanFilter kf, Point prePoint)
	{
		Mat predictions = kf.predict();
		Point predictedPoint = new Point();
		predictedPoint.x = predictions.get(0, 0)[0];
		predictedPoint.y = predictions.get(0, 1)[0];
		return predictedPoint;
	}
	
	/*private Point KalmanFilterCorrect(KalmanFilter kf, Point p)
	{
		 measurement.put(0, 0, p.x);
		 measurement.put(0, 1, p.y);
		 Mat correction = kf.correct(measurement);
		 Point correctedPoint = new Point();
		 correctedPoint.x = correction.get(0,0)[0];
		 correctedPoint.y = correction.get(0,1)[0];
		return correctedPoint;
	}*/

}
