import jama.Matrix;
import jkalman.JKalman;

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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		

		IDetection detection = new DefaultDetection();
		detection.init();
		Mat blackim = Mat.ones(1152, 720, CvType.CV_16S); //black images for background modeling
		
		for (int i = 0; i < 5; i++)
		{
			detection.getObjInImage(blackim);
		}
		
		JKalman kf = new JKalman(4, 2);
		double[][] tr = { {1, 0, 1, 0},              // { {1, 1},   // x
                {0, 1, 0, 1},             //   {0, 1} }; // dx
                {0, 0, 1, 0}, 
                {0, 0, 0, 1} };
		kf.setTransition_matrix(new Matrix(tr));
						
	 boolean first = true;
		
		for(int i = 1; i<6; i++){
			
			// Ladda in bild till Mat
			
			Mat img = Highgui.imread("/Users/annasoederroos/TSBB11/square"+i+".jpg");
			
			DetectionResult result = detection.getObjInImage(img);
			// ta fram mittpunkt som x och y plus halva width, height
			
			int x = result.regions.get(0).x + result.regions.get(0).width/2;
			int y = result.regions.get(0).y + result.regions.get(0).height/2;
			;
			double[][] meas = {{x,y}};
			Matrix measMatrix = new Matrix(meas);
			if ( first == true)
			{
				double[][] m = {{x,y,0,0}};
				Matrix measMa = new Matrix(m);
				kf.setState_post(measMa.transpose());
				first = false;
			}
			
			Matrix pre = kf.Predict();
			Matrix corr = kf.Correct(measMatrix.transpose());
			
			System.out.println("point " + x + " " + y);
			System.out.println("predicted " + pre.transpose());
			System.out.println("corrected " + corr.transpose());

	
		}
		
	}


}
