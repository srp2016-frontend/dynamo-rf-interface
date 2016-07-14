
public class RFCodeTagRead 
{
	private RFCodeReader reader;
	private RFCodeTag tag;
	private int ssi, channel;
	
	public RFCodeTagRead (RFCodeReader reader, int channel, RFCodeTag tag, int ssi)
	{
		this.reader = reader;
		this.channel = channel;
		this.tag = tag;
		this.ssi = ssi;
	}
	
	public RFCodeReader getReader()
	{
		return reader;
	}
	
	public RFCodeTag getTag ()
	{
		return tag;
	}
	
	public int getSsi ()
	{
		return ssi;
	}
	
	public int getChannel()
	{
		return channel;
	}
	
	public String toString ()
	{
		return reader.id() + " channel: " + (char) (channel + 'A') + "   " + tag.id() + " ssi: " + ssi; 
	}
}
