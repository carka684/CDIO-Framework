import jama.Matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jkalman.JKalman;

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


public class TrackingTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


		Vector<KalmanFilter> kalVec = new Vector<KalmanFilter>();
		IDetection detection = new DefaultDetection();
		detection.init();		

		boolean first = true;
		ImageReader reader = new ImageReader();
		reader.readImages("C:/Users/Calle/Documents/MATLAB/images/");
		Vector<String> files = reader.getFilesVec();
		int k = 0;
		Imshow show = new Imshow("");
		int nextID = 0;
		for(String file : files){
			Mat img = Highgui.imread(file);
			DetectionResult result = detection.getObjInImage(img);
			// Ladda in bild till Mat
			if(k++ > 2)	
			{
				for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
				{
					KalmanFilter kf = iterator.next();
					kf.predict(); //Ska detta ske h�r eller efter isMatch? Testa noga!
					kf.addUnseen();
					double[][] pos = kf.getPos();
					Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);	

					if(kf.getNumOfUnseen() > 15)
					{
						iterator.remove();
						System.out.println(kf.getId() + " was removed");
					}
				}				
				Thread.sleep(100);
				for(int i = 0; i < result.regions.size();i++ )//Loopa �ver regioner
				{
					int x = result.regions.get(i).x + result.regions.get(i).width/2;
					int y = result.regions.get(i).y + result.regions.get(i).height/2;
					if(kalVec.isEmpty())
					{
						kalVec.add(new KalmanFilter(nextID,x,y));
						//setRegionID = nextID++;
						nextID++;
					}	
					else
					{
						double minError = 80;
						KalmanFilter bestKalman = null;
						for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
						{
							KalmanFilter kf = iterator.next();
							double error = kf.getError(x,y);
							if (error < minError)
							{
								minError = error;
								bestKalman = kf;
							}   
						}
						if(bestKalman != null)
						{
							bestKalman.seen();
							bestKalman.correct(x, y);
						}
						else
						{
							kalVec.add(new KalmanFilter(nextID,x,y));								
							//setRegionID = nextID;
							nextID++;
							break;
						}
					}
				}
				show.showImage(img);
			}
		}
	}

}

