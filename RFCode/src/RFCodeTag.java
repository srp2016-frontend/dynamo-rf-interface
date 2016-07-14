import org.json.*;
import java.util.*;

public class RFCodeTag
{
	private String id;
	private JSONObject attributes;
	private ArrayList<RFCodeTagRead> reads;
	
	public RFCodeTag (JSONObject json)
	{
		id = json.getString("id");
		attributes = json.getJSONObject("attributes");
		reads = new ArrayList<>();
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
	
	public void addRead (RFCodeTagRead r)
	{
		reads.add(r);
	}
	
	public ArrayList<RFCodeTagRead> getReads ()
	{
		return reads;
	}
	
	public String toString ()
	{
		return id + " " + attributes;
	}
}
