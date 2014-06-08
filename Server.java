import java.io.*;
import java.net.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

// Server
public class Server
{
	// program entry
	public static void main(String[] argv) throws Exception
	{
		// ID map to index(start from 0)
		int now, id = 0;
		int [] used = new int[20];
		int [] ready  = new int[20];
		String parseCom[] = new String[10];
		// construct Map-Hashing Object
		Map<String, Integer> map = new HashMap<String, Integer>();
		String [] UL = new String[20];
		// the buffer for store Input Thread, 20 people max
		String[] buf = new String[20];
		for(int i = 0; i < 20 ; ++i)
		{
			buf[i]="";
			used[i] = 0;
			ready[i] = 0;
		}
		User temp_user;
		ArrayList<User> userList = new ArrayList<User>(20);
		// ServerSocket Object
		ServerSocket serverSocket = new ServerSocket(5000);
		int cn = 0;
		
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
				//System.out.println("Create user: "+userName+"->"+id);
				/*
				//put the ID into ArrayList
				temp_user = new User(userName);
				userList.add(temp_user);
				UL[id] = userName;
				// build the map of user ID and index
				map.put(userName, id++);
				*/
			}
			
			
			
			
			
			///////////////////////////////////////
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					
			
			
			// second get user's action
			String clientString = inFromClient.readLine();
			
			parseCom = clientString.split("#");
			if(clientString.equals("LogIn"))
			{
				outToClient.writeBytes("OK\n");
				for(int i = 0;i<20;i++)
				if(used[i]==0)
				{
					used[i] = 1;
					UL[i] = userName;
					map.put(userName, i);
					break;
				}
				now = map.get(userName).intValue();
				System.out.println("the user: " + UL[now]+ " is logging.");// test
			}
			else if(clientString.equals("PREBATTLE"))
			{
				String tmp = "";
				// get user Index
				now = map.get(userName).intValue();
				ready[now] = 1;
				cn++;
				System.out.println("the user: " + UL[now]+ " is Ready to Battle.");// test
				int t=0;
				if(cn>0)
				{
					for(int i = 0;i<20;i++)
					if(ready[i]==1)
					{
						tmp = UL[i];
						t = i;
						break;
					}
					for(int j = t+1;j<20;j++)
					if(ready[j]==1)
					{
						tmp +="#"+UL[j];
					}
					
				}
				outToClient.writeBytes(tmp+"\n");
			}
			else if(clientString.equals("LogOut"))
			{
				now = map.get(userName).intValue();
				ready[now] = 0;
				outToClient.writeBytes("OK\n");
			}
			else if(clientString.equals("BATTLEASK"))
			{
				now = map.get(userName).intValue();
				String rival = inFromClient.readLine();
				int rn = map.get(rival).intValue();
				System.out.println("the user: " + UL[now]+ " want to PK "+UL[rn]);// test
				String m = inFromClient.readLine();
				String m1 = inFromClient.readLine();
				if(rn==now||ready[rn]==0)
				{
					System.out.println("NO");
					outToClient.writeBytes("NO\n");
				}
				else
				{
					outToClient.writeBytes("OK\n");
					buf[rn] = "BATTLEASK#"+UL[now]+"#"+m+"#"+m1;
				}
				
				
			}
			else if(clientString.equals("BATTLERES"))
			{
				now = map.get(userName).intValue();
				String rival = inFromClient.readLine();
				int rn = map.get(rival).intValue();
				String res = inFromClient.readLine();
				String m = inFromClient.readLine();
				String m1 = inFromClient.readLine();
				System.out.println("the user: " + UL[now]+ " agree to PK "+UL[rn]);// test
				if(res.equals("Y"))
				{
					buf[rn] = "BATTLERES"+"#Y#"+UL[now]+"#"+m+"#"+m1;
				}
				else
				{
					buf[rn] = "BATTLERES"+"#N#"+UL[now]+"#"+m+"#"+m1;;
				}
				
			}
			else if(clientString.equals("Request"))
			{
				now = map.get(userName).intValue();
				if(buf[now].equals(""))
				{
					outToClient.writeBytes("no\n");
					System.out.println("no buf");
				}
				else if(buf[now].contains("BATTLEASK"))
				{
					System.out.println(buf[now]);
					String [] par = buf[now].split("#");
					buf[now] = "";
					outToClient.writeBytes("BATTLEASK\n");
					outToClient.writeBytes(par[1]+"\n");
					outToClient.writeBytes(par[2]+"\n");
					outToClient.writeBytes(par[3]+"\n");
					
				}
				else if(buf[now].contains("BATTLERES"))
				{
					System.out.println("buf have BATTLERES");
					System.out.println(buf[now]);
					String [] par = buf[now].split("#");
					buf[now] = "";
					
					outToClient.writeBytes("BATTLERES\n");
					outToClient.writeBytes(par[1]+"\n");//YN
					outToClient.writeBytes(par[2]+"\n");//username
					outToClient.writeBytes(par[3]+"\n");
					outToClient.writeBytes(par[4]+"\n");
				}
				else
				{
				}
			}
			
			
		}
	}
}
