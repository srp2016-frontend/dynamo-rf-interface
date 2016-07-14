import org.json.*;
import java.net.*;
import java.sql.*;
import java.io.*;
import java.util.*;

public class ZoneManager
{
	private String ip;
	private String dbURL;
	private Connection conn;
	private PreparedStatement clearTable, insertData;
	private int port;
	private HashMap<String, RFCodeReader> readers;
	private HashMap<String, RFCodeTag> tags;
	private ArrayList<RFCodeTagRead> reads;
	private long timestamp;

	/**
	 * Sets up the ZoneManager connection with an ip (no http://) and port number
	 * @param ip
	 * @param port
	 */
	public ZoneManager (String ip, int port, String dbURL)
	{
		this.ip = ip;
		this.port = port;
		this.dbURL = dbURL;
		readers = new HashMap<>();
		tags = new HashMap<>();
		reads = new ArrayList<>();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + dbURL;
			conn = DriverManager.getConnection(url, "root", "root");
			clearTable = conn.prepareStatement("DELETE FROM RFTAG");
			// table (time, RunnerID, SocratesA, SocratesB, AristotleA, AristotleB, tagID)
			insertData = conn.prepareStatement("INSERT INTO RFTAG VALUES(?, ?, ?, ?, ?, ?, ?);");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Couldn't find JDBC Driver class...");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Updates the entire system state (readers, tags, tag reads).
	 */
	public void update ()
	{
		JSONObject rList = new JSONObject(execute("readerlist.json"));
		readers.clear();
		JSONArray names = rList.names();
		for (Object id : names)
		{
			JSONObject r = rList.getJSONObject((String) id);
			if (r.getJSONObject("attributes").getString("state").equals("ACTIVE"))
				readers.put((String) id, new RFCodeReader(r));
		}
		JSONObject tagList = new JSONObject(execute("tagprintbygroup.json"));
		timestamp = System.currentTimeMillis();
		tags.clear();
		reads.clear();
		names = tagList.names();
		for (Object id : names)
		{
			JSONObject t = tagList.getJSONObject((String) id);
			RFCodeTag tag = new RFCodeTag(t);
			tags.put((String) id, tag);
			JSONArray tagLinks = t.getJSONArray("taglinks");
			for (int i = 0; i < tagLinks.length(); i++)
			{
				JSONObject link = tagLinks.getJSONObject(i);
				int channel = -1;
				String chan = link.getString("channelid");
				if (chan.contains("channel_A"))
					channel = 0;
				else if (chan.contains("channel_B"))
					channel = 1;
				RFCodeReader reader = readers.get(chan.substring(0, chan.indexOf("_channel_")));
				RFCodeTagRead read = new RFCodeTagRead(reader, channel, tag, link.getInt("ssi"));
				reads.add(read);
				tag.addRead(read);
				reader.addRead(read, channel);
			}
		}
	}

	/**
	 * Updates the database with tag reads.
	 */
	public void updateDB ()
	{
		try
		{
			// Clears the table
			clearTable.executeUpdate();
			
			for (String tagID : tags.keySet())
			{
				RFCodeTag tag = tags.get(tagID);
				ArrayList<RFCodeTagRead> reads = tag.getReads();
				insertData.setLong(1, timestamp);
				insertData.setInt(2, 0);
				insertData.setInt(3, 0);
				insertData.setInt(4, 0);
				insertData.setInt(5, 0);
				insertData.setInt(6, 0);
				for (RFCodeTagRead read : reads)
					if (read.getReader().id().equalsIgnoreCase("SOCRATES"))
					{
						if (read.getChannel() == 0)
							insertData.setInt(3, read.getSsi());
						else if (read.getChannel() == 1)
							insertData.setInt(4, read.getSsi());
					}
					else
					{
						if (read.getChannel() == 0)
							insertData.setInt(5, read.getSsi());
						else if (read.getChannel() == 1)
							insertData.setInt(6, read.getSsi());
					}
				insertData.setString(7, tagID);
				insertData.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public long getTimestamp ()
	{
		return timestamp;
	}

	/**
	 * Returns a map representing all current readers, as the string id mapped to the reader object. 
	 * Does not execute an update prior,
	 * the updateReaders() method must be called before calling this method to receive an updated list.
	 * @return
	 */
	public HashMap<String, RFCodeReader> getReaders ()
	{
		return readers;
	}

	/**
	 * Returns a map representing all current tags, as the string id mapped to the tag object. 
	 * Does not execute an update prior,
	 * the update() method must be called before calling this method to receive an updated list.
	 * @return
	 */
	public HashMap<String, RFCodeTag> getTags ()
	{
		return tags;
	}

	/**
	 * Returns a list of all current tag reads. 
	 * Does not execute an update prior,
	 * the update() method must be called before calling this method to receive an updated list.
	 * @return
	 */
	public ArrayList<RFCodeTagRead> getTagReads ()
	{
		return reads;
	}

	public ArrayList<RFCodeTagRead> getReadsByReader (String readerID)
	{
		return getReadsByReader(readerID, -1);
	}

	public ArrayList<RFCodeTagRead> getReadsByReader (String readerID, int channel)
	{
		return readers.get(readerID).getReads(channel);
	}

	public ArrayList<RFCodeTagRead> getReadsByTag (String tagID)
	{
		return tags.get(tagID).getReads();
	}

	/**
	 * Executes an http GET request to the server's api and returns the raw output (as an ASCII string)
	 * @param command
	 * @return
	 */
	public String execute (String command)
	{
		try
		{
			URL url = new URL("http://" + ip + ":" + port + "/rfcode_zonemgr/zonemgr/api/" + command);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			if (con.getResponseCode() != 200)
				throw new ConnectException("Connection failed. Response: " + con.getResponseCode() + " " + con.getResponseMessage());
			String response = "";
			BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while (r.ready())
				response += r.readLine() + "\n";
			r.close();
			return response;
		}
		catch (Exception e)
		{
			return e.toString();
		}
	}
}
