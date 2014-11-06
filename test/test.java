
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.video.KalmanFilter;


import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.highgui.Highgui;

public class test {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Vector<Mat> Img = new Vector<Mat>();
		System.out.println(Img.size());
		Img.add(0, Highgui.imread("/Users/annasoederroos/TSBB11/square1.jpg"));
		System.out.println(Img.get(0).type());
		Img.add(1, Highgui.imread("/Users/annasoederroos/TSBB11/square2.jpg"));
		List <MatOfPoint> contours = new ArrayList <MatOfPoint>();
		Mat contourHierarchy = new Mat();
		Img.get(0).convertTo(Img.get(0), CvType.CV_32SC1);
		System.out.println(Img.get(0).type());
		

		Highgui.imwrite("/Users/annasoederroos/TSBB11/test.jpg", Img.get(0));
		
		Imgproc.findContours(Img.get(0), contours, contourHierarchy, 3, 1);
		

	
		//Rect boundBox = Imgproc.boundingRect((MatOfPoint) Img.get(0));
		//System.out.println(boundBox.height);
	//	boundBox = squarify(boundBox, img.width(), img.height());
	//	result.add(img.submat(boundBox).clone());

	//MatOfFloat measurement = new MatOfFloat(2,1);
	Point test = new Point(1,2);
	KalmanFilter k1; // = new KalmanFilter(2,4,0,CvType.CV_32F);
	k1 = initKalmanFilter(test);
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

private Point KalmanFilterPredict(KalmanFilter kf, Point prePoint)
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