
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * 
 */

/**
 * @author Donald Acton
 * This example is adapted from Kurose & Ross
 *
 */
public class DNSlookup {


	static final int MIN_PERMITTED_ARGUMENT_COUNT = 2;
	static boolean tracingOn = false;
	static InetAddress rootNameServer;
	static int timeoutCount = 0;
	static String fqdn;
	static int queryCount;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		int argCount = args.length;
		
		if (argCount < 2 || argCount > 3) {
			usage();
			return;
		}
        
        // get root DNS server IP args[0]
		rootNameServer = InetAddress.getByName(args[0]);
        
        // get hostname args[1]
		fqdn = args[1];
		
        // check if tracing is on args[2]
		if (argCount == 3 && args[2].equals("-t")) {
			tracingOn = true;
		}
		
		// call usage for incorrect flags
		if (argCount == 3 && !args[2].equals("-t")) {
			usage();
			return;
		}
        sendQuery(fqdn, rootNameServer);
	}

	private static void usage() {
		System.out.println("Usage: java -jar DNSlookup.jar rootDNS name [-t]");
		System.out.println("   where");
		System.out.println("       rootDNS - the IP address (in dotted form) of the root");
		System.out.println("                 DNS server you are to start your search at");
		System.out.println("       name    - fully qualified domain name to lookup");
		System.out.println("       -t      -trace the queries made and responses received");
	}
	
	public static void sendQuery(String fqdn, InetAddress rootNameServer) throws IOException {
		
		// check if query count exceeds fifty
		if (queryCount > 50) {
			System.out.println(DNSlookup.fqdn + " -3 0.0.0.0");
			return;
		}
		
        DatagramSocket socket;
		try {
			
			socket = new DatagramSocket();
			socket.setSoTimeout(5000);
	        byte[] buf = new byte[512];
            
	        DNSQuery newQuery = new DNSQuery(fqdn, rootNameServer);
	        
	        DatagramPacket packet = new DatagramPacket(newQuery.getByte(), newQuery.getByte().length, rootNameServer, 53);

	        socket.send(packet);
	        queryCount++;
	        
	        packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);

	        @SuppressWarnings("unused")
			DNSResponse response = new DNSResponse(packet.getData(), packet.getLength(), newQuery, tracingOn);
	        socket.close();
	        
		} catch (SocketException e) {
			System.out.println(DNSlookup.fqdn + " -4 0.0.0.0");
		} catch (SocketTimeoutException e) {
			// resend query for first timeout
			if (timeoutCount > 1) {
				System.out.println(DNSlookup.fqdn + " -2 0.0.0.0");
			} else {
				timeoutCount++;
				sendQuery(fqdn, rootNameServer);
			}
						
		}
	}
}


