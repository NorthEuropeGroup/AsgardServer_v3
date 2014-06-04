import java.io.*;
import java.net.*;
import java.util.*;
//import java.util.HashMap;
//import java.util.Map;

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
		User temp_user;
		ArrayList<User> userList = new ArrayList<User>(20);
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
			String userName = inFromClient.readLine();
			
			// the user is first time log in
			if(!map.containsKey(userName))
			{
				// the message of construct user
				System.out.println("Create user: "+userName+"->"+id);
				
				//put the ID into ArrayList
				temp_user = new User(userName);
				userList.add(temp_user);
				
				// build the map of user ID and index
				map.put(userName, id++);
			}
			
			// get user Index
			now = map.get(userName).intValue();
			
			System.out.println("the user: " + userList.get(now).getID() + "is logging.");// test
			
			///////////////////////////////////////
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
						
			if(!buf[now].equals(""))
				outToClient.writeBytes(buf[now]+'\n');
			else
				outToClient.writeBytes("none\n");
			//////////////////////////////////////
			
			// second get user's action
			String clientString = inFromClient.readLine();
			
			parseCom = clientString.split("#");
			
			if(parseCom[1].equals("PREBATTLE"))
			{
				String list = new String();
				for(int i = 0;i < userList.size(); i++)
				{
					list = list.concat(userList.get(i).getID());
					if(i != userList.size()-1)
						list = list.concat("#");
				}
				outToClient.writeBytes(list+'\n');
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
