import com.thingmagic.*;

public abstract class TMReader implements ReadListener
{
	protected Reader reader;
	protected String hostname;
	
	/**
	 * Creates a reader from the given URI, adds a listener (itself), and connects to the physical reader.
	 * @param uri
	 * @throws ReaderException
	 */
	public TMReader (String uri) throws ReaderException
	{
		hostname = uri;
		reader = Reader.create(uri);
		reader.addReadListener(this);
		reader.connect();
	}
	
	public String getHostname ()
	{
		return hostname;
	}
	
	public void startReading()
	{
		reader.startReading();
	}
	
	public abstract void tagRead(Reader r, TagReadData data);
}
