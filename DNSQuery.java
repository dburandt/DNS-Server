
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;


public class DNSQuery {
	
	String queryAsString;
	InetAddress hostAddress;
	String[] queryAsArray;
	byte[] myQuery;
	byte[] a2;
	Integer qida;
	Integer qidb;
	int count = 0;
	int queryIDRequest;
	
	public DNSQuery(String query, InetAddress host) {
		
		queryAsString = query;
		hostAddress = host;
		
		queryAsArray = query.split(Pattern.quote("."));
		
		Random r = new Random();
		qida = r.nextInt(255);
		qidb = r.nextInt(255);
		
		queryIDRequest = (int) (((qida.byteValue() << 8) & 0xff00) | (qidb.byteValue() & 0xff));
		
		ArrayList<Byte> myQueryList = new ArrayList<Byte>(Arrays.asList(
			qida.byteValue(), qidb.byteValue(),
			(byte)0,
			(byte)0,
			(byte)0, (byte)1, 
			(byte)0, (byte)0, 	
			(byte)0, (byte)0, 	
			(byte)0, (byte)0
		));
				
		// get length of each section of the hostname
		int[] qsl = new int[100];
		for (int i = 0; i < queryAsArray.length; i++) {
			qsl[i] = queryAsArray[i].length();
		}
		
		for (int i = 0; i < queryAsArray.length; i++) {
			myQueryList.add((byte)qsl[i]);
		
			a2 = new byte[qsl[i]];
			a2 = queryAsArray[i].getBytes();
			for (int j = 0; j < qsl[i]; j++) {
				myQueryList.add(a2[j]);
			}
		
		}
		myQueryList.add((byte)0);
		myQueryList.add((byte)0);
		myQueryList.add((byte)1);
		myQueryList.add((byte)0);
		myQueryList.add((byte)1);
		
		myQuery = toByteArray(myQueryList);
		
	}
	
	public String getQueryAsString() {
		return queryAsString;
	}
	
	public InetAddress getServerAddress() {
		return getHostAddress();
	}
	
	public InetAddress getHostAddress() {
		return hostAddress;
	}
	
	public byte[] getByte() {
		return myQuery;
	}
	
	public int getQueryID() {
		return queryIDRequest;
	}
	
	public static byte[] toByteArray(ArrayList<Byte> in) {
	    final int n = in.size();
	    byte ret[] = new byte[n];
	    for (int i = 0; i < n; i++) {
	        ret[i] = in.get(i);
	    }
	    return ret;
	}
}
