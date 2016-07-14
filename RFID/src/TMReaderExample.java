/*
 * Need API jar files. Right click on project > Properties > Java Build Path > Add External Jars
 */
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagReadData;

public class TMReaderExample extends TMReader
{ 
	public static void main (String[] args) throws Exception
	{
		/*
		 * Readers at llrp://10.1.17.16:5084/ and (NOT WORKING) llrp://10.1.17.26:5084/
		 */
		TMReaderExample reader = new TMReaderExample("llrp://10.1.17.16:5084/");
		System.out.println("Reader connected...");
		System.out.println("Waiting for reads...\n");
		reader.startReading();
	}
	
	public TMReaderExample(String uri) throws ReaderException 
	{
		super(uri);
	}

	public void tagRead(Reader r, TagReadData data)
	{
		if(data.epcString().equals("E200631696153D716537B8F5") && data.getAntenna() == 4)
		{
			System.out.println("Reader: " + r);
			System.out.println("Data: " + data);
			System.out.println("Other stuff:");
			System.out.println("EPC String: " + data.epcString());
			System.out.println("Antenna: " + data.getAntenna());
			System.out.println("Frequency: " + data.getFrequency());
			System.out.println("Phase: " + data.getPhase());
			System.out.println("Read count: " + data.getReadCount());
			System.out.println("Rssi: " + data.getRssi());
			System.out.println("Time: " + data.getTime());
			String s = "Data: [";
			for (byte b : data.getData())
				s += b + ", ";
			if (s.length() == 7)
				s += "  ";
			System.out.println(s.substring(0, s.length() - 2) + "]\n");
		}
	}
}
