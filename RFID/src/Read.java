import java.io.Serializable;

public class Read implements Serializable {
	public Read(int reader, int rssi) { this.reader = reader; this.rssi = rssi;}
	public String toString() { return reader + ": " + rssi; } 
	int reader, rssi;
}
