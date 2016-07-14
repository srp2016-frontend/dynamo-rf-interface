import org.json.*;
import java.util.*;

public class RFCodeReader
{
	private String id;
	private JSONObject attributes;
	private ArrayList<RFCodeTagRead> readsA;
	private ArrayList<RFCodeTagRead> readsB;
	
	public RFCodeReader (JSONObject json)
	{
		id = json.getString("id");
		attributes = json.getJSONObject("attributes");
		readsA = new ArrayList<>();
		readsB = new ArrayList<>();
	}
	
	public Object get (String id)
	{
		return attributes.get(id);
	}
	
	public JSONObject attributes ()
	{
		return attributes;
	}
	
	public String id ()
	{
		return id;
	}
	
	public void addRead (RFCodeTagRead r, int channel)
	{
		if (channel == 0)
			readsA.add(r);
		else if (channel == 1)
			readsB.add(r);
	}
	
	/**
	 * Returns tag reads from given channel. Providing -1 will return all tag reads from both channels.
	 * @param channel
	 * @return
	 */
	public ArrayList<RFCodeTagRead> getReads (int channel)
	{
		if (channel == 0)
			return readsA;
		else if (channel == 1)
			return readsB;
		else if (channel == -1)
		{
			ArrayList<RFCodeTagRead> all = new ArrayList<>(readsA);
			all.addAll(readsB);
			return all;
		}
		return null;
	}
	
	public String toString ()
	{
		return id + " " + attributes;
	}
}
