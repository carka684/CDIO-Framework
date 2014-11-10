import jama.Matrix;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
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
				for(int i = 0; i < result.regions.size();i++ )//Loopa över regioner
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
						for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
						//for(KalmanFilter kf : kalVec)
						{
							KalmanFilter kf = iterator.next();
							kf.predict(); //Ska detta ske här eller efter isMatch? Testa noga!
							kf.addUnseen();
							if(kf.isMatch(x, y) && !match)
							{
								//Set region ID to kf.getID();
								kf.seen();
								kf.correct(x, y);
								match = true;
							}
							if(kf.getNumOfUnseen() > 15)
							{
								iterator.remove();
								System.out.println("removed");
							}
							System.out.println(kf.getId());
							double[][] pos = kf.getPos();
							//System.out.println(pos[0][0] + " " + pos[1][0]);
							Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);
							
							show.showImage(img);
							
							
						}
						
						if(!match)
						{
							kalVec.add(new KalmanFilter(nextID,x,y,80));								
							//setRegionID = nextID;
							nextID++;
							break;
						}
					}



				}
				// ta fram mittpunkt som x och y plus halva width, height
				/*
				for(int i = 0; i < result.regions.size();i++ )
				{
					int x = result.regions.get(i).x + result.regions.get(i).width/2;
					int y = result.regions.get(i).y + result.regions.get(i).height/2;

					double[][] meas = {{x,y}};
					Matrix measMatrix = new Matrix(meas);

					Matrix pre = kf.Predict();
					Matrix corr = kf.Correct(measMatrix.transpose());
					int xPre = (int) pre.getArray()[0][0];
					int yPre = (int) pre.getArray()[1][0];
					Core.circle(img, new Point(xPre,yPre), 5, new Scalar(125, 125, 125),5);
					show.showImage(img);
					System.out.println("Error: " + Math.sqrt(Math.pow((x-xPre),2) + Math.pow((y-yPre),2)));

//					System.out.println("point " + x + " " + y);
//					System.out.println("predicted " + pre.transpose());
//					System.out.println("corrected " + corr.transpose());

				 */
			}
		}



	}

}

