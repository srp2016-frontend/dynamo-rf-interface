/*
 * Need API jar files. Right click on project > Properties > Java Build Path > Add External Jars
 */
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagReadData;

public class TMReaderExample extends TMReader
{
	public HashMap<Point, ArrayList<Read>> rssiStore;
	public Point current;
	
	public static void main (String[] args) throws Exception
	{
		/*
		 * Readers at llrp://10.1.17.16:5084/ and (NOT WORKING) llrp://10.1.17.26:5084/
		 */
		TMReaderExample reader = new TMReaderExample("llrp://10.1.17.16:5084/");
		System.out.println("Reader connected...");
		System.out.println("Waiting for reads...\n");
		Scanner scan = new Scanner(System.in);
		int x = scan.nextInt();
		int y = scan.nextInt();
		reader.current = new Point(x, y);
		reader.rssiStore.put(reader.current, new ArrayList<Read>());
		reader.startReading();
		scan.close();
		new Thread(() -> {
			try {
				Thread.sleep(25000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File("result.obj")));
				stream.writeObject(reader.rssiStore);
				stream.close();
			} catch(Exception e) {
				e.printStackTrace(); }
			System.out.println(reader.rssiStore);
			System.exit(0);
		}).start();
		
	}
	//{java.awt.Point[x=0,y=0]=[1: -57, 1: -55, 1: -54, 1: -55, 1: -53, 1: -57, 1: -55, 1: -54, 1: -54, 1: -56, 1: -57, 1: -57, 1: -56], java.awt.Point[x=300,y=0]=[], java.awt.Point[x=600,y=0]=[2: -59], java.awt.Point[x=300,y=100]=[1: -58], java.awt.Point[x=0,y=100]=[2: -57, 1: -57, 1: -49, 1: -50, 1: -49, 1: -49, 1: -50, 4: -57, 1: -49, 1: -49, 1: -49, 1: -47, 4: -62, 4: -58, 1: -48, 1: -49], java.awt.Point[x=100,y=0]=[1: -57, 1: -58, 1: -57, 1: -57, 1: -58, 1: -57, 1: -57, 4: -60, 1: -54, 1: -56, 1: -59, 1: -57], java.awt.Point[x=200,y=0]=[1: -57], java.awt.Point[x=400,y=0]=[2: -60], java.awt.Point[x=100,y=100]=[1: -57, 4: -69, 1: -61, 1: -62], java.awt.Point[x=200,y=100]=[1: -59], java.awt.Point[x=500,y=0]=[2: -57, 2: -56, 2: -57, 2: -57, 2: -55, 2: -55]}

	
	@SuppressWarnings("unchecked")
	public TMReaderExample(String uri) throws ReaderException, FileNotFoundException, IOException, ClassNotFoundException 
	{
		super(uri);
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("result.obj")));
		rssiStore = (HashMap<Point, ArrayList<Read>>)input.readObject();
		input.close();
		current = new Point();
	}

	public void tagRead(Reader r, TagReadData data)
	{
		if(data.epcString().equals("E200631696153D716537B8F5"))
		{
			rssiStore.get(current).add(new Read(data.getAntenna(), data.getRssi()));
		}
	}
}
