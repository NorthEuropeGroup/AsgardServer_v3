
// 加入IO及net
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

// Server端
public class Thread_Server
{
	// 程式進入點
	public static void main(String[] argv) throws Exception
	{
		// 針對使用者的id編號(從0開始)
		int now, id = 0;
		String username[20];
		// 建立Map-Hashing物件
		Map<String, Integer> map = new HashMap<String, Integer>();

		// 儲存Input Thread的buffer，最大上限20人
		String[] buf = new String[20];
		for(int i = 0; i < 20 ; ++i) buf[i]="";
		
		// 建立ServerSocket物件
		ServerSocket serverSocket = new ServerSocket(45321);
		
		// 不斷取得使用者連線
		while(true)
		{
			// 建立Client端連線
			Socket connectionSocket = serverSocket.accept();
			
			// 從Client端取得Buffer
			BufferedReader inFromClient = 
				new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 

			// 先取得使用者的ID
			String user = inFromClient.readLine();
			
			// 取得Client端的String
			String clientString = inFromClient.readLine();
			
			// 如果還沒有建立使用者ID
			if(!map.containsKey(user))
			{
				// 建立使用者訊息
				System.out.println("Create user: "+user+"->"+id);
				
				// 建立使用者ID的MAP
				map.put(user, id++);
			}
			
			// 取得使用者ID
			now = map.get(user).intValue();
			
			// 如果是Input Thread
			if(clientString.charAt(0)=='I')
			{
				// 儲存到其他使用者的buffer
				for(int i = 0; i < 20 ; ++i)
				{
					// 除了自己以外的所有使用者
					if(i != now)
						buf[i] = buf[i].concat(user+" says: "+clientString.substring(1,clientString.length())+"#");
				}
				
				// 輸出到Server端的畫面
				System.out.println("Client("+connectionSocket.getInetAddress()+"): " + clientString);
			}
			// 如果是Request Thread
			else
			{
				// 建立輸出至Client端的Stream
				DataOutputStream  outToClient = 
						new DataOutputStream(connectionSocket.getOutputStream());
				
				// 回覆至Client端的String
				String reply;
				
				// 如果buffer有東西
				if(!buf[now].equals("")) reply = "Y";
				else reply = "N";
				
				// 回覆至Client端的String
				reply = reply.concat(buf[now]);
				
				// 回覆至Client端
				outToClient.writeBytes(reply+'\n');
				
				// 清空buffer
				buf[now] = "";
			}
			
		}
	}
}
