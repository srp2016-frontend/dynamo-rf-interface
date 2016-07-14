import java.util.*;

public class Tests 
{
	public static void main(String[] args)
	{
		ZoneManager zm = new ZoneManager("10.11.34.186", 6580, "10.70.2.221:8889/DYNAMO");
		zm.update();
		zm.updateDB();
		HashMap<String, RFCodeReader> readers = zm.getReaders();
		System.out.println("Readers:");
		for (String id : readers.keySet())
			System.out.println(readers.get(id));
		System.out.println("-----------------");
		HashMap<String, RFCodeTag> tags = zm.getTags();
		System.out.println("Tags:");
		for (String id : tags.keySet())
			System.out.println(tags.get(id));
		System.out.println("----------------------");
		System.out.println("Tags By Reader:");
		for (String rID : readers.keySet())
		{
			System.out.println(rID + ":");
			ArrayList<RFCodeTagRead> reads = zm.getReadsByReader(rID, 0);
			System.out.println("Channel A:");
			for (RFCodeTagRead read : reads)
				System.out.println("\tTag: " + read);
			System.out.println("Channel B:");
			reads = zm.getReadsByReader(rID, 1);
			for (RFCodeTagRead read : reads)
				System.out.println("\tTag: " + read);
		}
	}
}
