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
			buf[now] = "";
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
			else if(parseCom[1].equals("BATTLE_ASK"))
			{
				if(userList.get(map.get(parseCom[2]).intValue()).getBattleStatus())//in battle
				{
					outToClient.writeBytes("BATTLEING\n");
				}
				else
				{
					outToClient.writeBytes("ACK\n");
					buf[map.get(parseCom[2]).intValue()] = clientString;
				}
			}
			else if(parseCom[1].equals("BATTLE_REPLY"))
			{
				if(parseCom[3].equals("N"))
				{
					String reply = new String();
					reply = reply.concat(parseCom[2] + "#" + "REJECT" + "#" + parseCom[0]);
					outToClient.writeBytes(reply+'\n');
				}
				else if(parseCom[3].equals("Y"))
				{
					String reply = new String();
					reply = reply.concat(parseCom[0] + "#" + "BATTLE" + "#" + parseCom[2]);
					outToClient.writeBytes(reply+'\n');
					
					userList.get(map.get(parseCom[0]).intValue()).switchBattleFlag();
					userList.get(map.get(parseCom[2]).intValue()).switchBattleFlag();//switch battle status
				}
			}
			else if(parseCom[1].equals("CARDSELECT"))
			{
				buf[map.get(parseCom[4]).intValue()] = clientString;
			}
			else if(parseCom[1].equals("BATTLE"))
			{
				buf[map.get(parseCom[7]).intValue()] = clientString;
			}
			else if(parseCom[1].equals("OVER"))
			{
				userList.get(map.get(parseCom[0]).intValue()).switchBattleFlag();
			}
			else if(parseCom[1].equals("TIMEOUT"))
			{
				outToClient.writeBytes("ACK\n");
				buf[map.get(parseCom[2]).intValue()] = clientString;
				
				if(userList.get(map.get(parseCom[0]).intValue()).getBattleStatus())
					userList.get(map.get(parseCom[0]).intValue()).switchBattleFlag();
				if(userList.get(map.get(parseCom[2]).intValue()).getBattleStatus())
					userList.get(map.get(parseCom[2]).intValue()).switchBattleFlag();
			}
			else if(parseCom[1].equals("ACK"))
			{
				System.out.println("ACK");
			}
			else if(parseCom[1].equals("LOGOUT"))
			{
				userList.remove(map.get(parseCom[0]).intValue());
				map.remove(parseCom[0]);
				outToClient.writeBytes("ACK\n");
			}
			
		}
	}
}
