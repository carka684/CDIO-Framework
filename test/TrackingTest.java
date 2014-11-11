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

				Thread.sleep(100);
				for(int i = 0; i < result.regions.size();i++ )//Loopa �ver regioner
				{
					//System.out.println("new image");
					int x = result.regions.get(i).x + result.regions.get(i).width/2;
					int y = result.regions.get(i).y + result.regions.get(i).height/2;
					if(kalVec.isEmpty())
					{
						kalVec.add(new KalmanFilter(nextID,x,y,80));
						//setRegionID = nextID++;
						nextID++;
					}	
					else
					{
						boolean match = false;
						double minError = 9999;
						KalmanFilter bestKalman = null;
						HashMap<Double, KalmanFilter> hashMap = new HashMap<Double, KalmanFilter>();
						for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
						{
							KalmanFilter kf = iterator.next();
							kf.predict(); //Ska detta ske h�r eller efter isMatch? Testa noga!
							kf.addUnseen();
							double error = kf.getError(x,y);
							if (error < minError)
							{
								minError = error;
								bestKalman = kf;
							}   
							
							if(kf.getNumOfUnseen() > 15)
							{
								iterator.remove();
								System.out.println(kf.getId() + " was removed");
								double[][] pos = kf.getPos();
								Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,255),5);
							}
							//System.out.println(kf.getId());
							//double[][] pos = kf.getPos();
							//System.out.println(pos[0][0] + " " + pos[1][0]);
							//Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);
						}
						KalmanFilter kf = bestKalman;
						kf.seen();
						kf.correct(x, y);
						match = true;
						Core.circle(img, new Point(x,y), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);
						if(!match)
						{
							kalVec.add(new KalmanFilter(nextID,x,y,80));								
							//setRegionID = nextID;
							nextID++;
							break;
						}
					}
				}
				if(result.regions.isEmpty())
				{
					for(KalmanFilter kf : kalVec)
					{
						kf.predict();
						kf.addUnseen();
						double[][] pos = kf.getPos();
						System.out.println(pos[0][0] + " " + pos[1][0]);
						Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,Math.abs(Math.random())*255),5);

					}
				}
				show.showImage(img);

			}
		}
	}

}

