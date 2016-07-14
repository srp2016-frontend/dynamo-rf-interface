import java.sql.*;
import com.thingmagic.*;

public class DBUpdater extends TMReader
{
	private static String dbURL = "10.70.2.221:8889/DYNAMO";
	private static Connection conn;
	private static PreparedStatement insertData;

	public static void main(String[] args) throws ReaderException
	{
		DBUpdater updater1 = new DBUpdater("llrp://10.1.17.16:5084/");
		DBUpdater updater2 = new DBUpdater("llrp://10.1.17.26:5084/");
		//updater1.reader.paramSet("/reader/currentTime", updater2.reader.paramGet("/reader/currentTime"));
		//System.out.println(updater1.reader.paramGet("/reader/currentTime"));
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + dbURL;
			conn = DriverManager.getConnection(url, "root", "root");

			// Clears table
			PreparedStatement clearTable = conn.prepareStatement("DELETE FROM TMTAG");
			clearTable.executeUpdate();
			clearTable.close();

			// table (tagID, runnerID, readerHostname, antenna, rssi, time)
			insertData = conn.prepareStatement("INSERT INTO TMTAG VALUES(?, ?, ?, ?, ?, ?);");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Couldn't find JDBC Driver class...");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		updater1.startReading();
		updater2.startReading();
	}

	public DBUpdater (String uri) throws ReaderException
	{
		super(uri);
	}

	public void tagRead (Reader r, TagReadData data)
	{
		try
		{
			insertData.setString(1, data.epcString());
			insertData.setInt(2, 0);
			insertData.setString(3, hostname);
			insertData.setInt(4, data.getAntenna());
			insertData.setInt(5, data.getRssi());
			insertData.setLong(6, data.getTime());
			System.out.println(insertData + " " + System.currentTimeMillis());
			insertData.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
