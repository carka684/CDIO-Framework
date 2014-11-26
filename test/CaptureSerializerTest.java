import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.impl.SerializableCapture;


public class CaptureSerializerTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Capture capture = new Capture();
		capture.id = 20;
		capture.timeStamp = new Date();
		capture.trapDeviceId = 10;
		capture.regionImage = Highgui.imread("C:/Users/Tobias/Pictures/Pos/im10261.jpg");
		
		String message = "NEW_CAPTURE,";
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(new SerializableCapture(capture));
		
		message += new String(Base64.getEncoder().encode(os.toByteArray()));
		System.out.println("Message: " + message);
		
		String captureEncoded = message.split(",")[1];
		byte[] bytes = Base64.getDecoder().decode(captureEncoded);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput ois = new ObjectInputStream(bis);
		capture = ((SerializableCapture) ois.readObject()).getCapture();
		
		Imshow show = new Imshow("");
		show.showImage(capture.regionImage);
	}
	
}
