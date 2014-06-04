import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

// Server
public class Thread_Server
{
	// program entry
	public static void main(String[] argv) throws Exception
	{
		// ID map to index(start from 0)
		int now, id = 0;
		
		String parseCom[] = new String[10];
		// construct Map-Hashing Object
		Map<String, Integer> map = new HashMap<String, Integer>();

		// the buffer for store Input Thread, 20 people max
		String[] buf = new String[20];
		for(int i = 0; i < 20 ; ++i) buf[i]="";
		String username[] = new String[20];
		// ServerSocket Object
		ServerSocket serverSocket = new ServerSocket(45321);
		
		
		while(true)
		{
			// Client connection
			Socket connectionSocket = serverSocket.accept();
			
			// get buffer from client
			BufferedReader inFromClient = 
				new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 

			// first get user's ID
			String user = inFromClient.readLine();
			
			// the user is first time log in
			if(!map.containsKey(user))
			{
				// the message of construct user
				System.out.println("Create user: "+user+"->"+id);
				
				//put the ID into string array
				username[id] = user;
				
				// build the map of user ID and index
				map.put(user, id++);
			}
			
			// get user Index
			now = map.get(user).intValue();
			
			System.out.println("the user: " + username[now] + "is logging.");// test
			
			///////////////////////////////////////
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
						
			if(!buf[now].equals(""))
				outToClient.writeBytes(buf[now]);
			else
				outToClient.writeBytes("none");
			//////////////////////////////////////
			
			// second get user's action
			String clientString = inFromClient.readLine();
			
			parseCom = clientString.split("#");
			if(clientString.charAt(0)=='I')
			{
				
				for(int i = 0; i < 20 ; ++i)
				{
				
					if(i != now)
						buf[i] = buf[i].concat(user+" says: "+clientString.substring(1,clientString.length())+"#");
				}
				
			
				System.out.println("Client("+connectionSocket.getInetAddress()+"): " + clientString);
			}
			// if Request Thread
			else
			{
				// The stream output to client
				//DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
						
				
				// the string reply to client
				String reply;
				
				// if the buffer not empty
				if(!buf[now].equals("")) reply = "Y";
				else reply = "N";
				
				// the string reply to client
				reply = reply.concat(buf[now]);
				
				// reply to client
				outToClient.writeBytes(reply+'\n');
				
				// erase buffer
				buf[now] = "";
			}
			
		}
	}
}
