
// �[�JIO��net
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

// Server��
public class Thread_Server
{
	// �{���i�J�I
	public static void main(String[] argv) throws Exception
	{
		// �w��ϥΪ̪�id�s��(�q0�}�l)
		int now, id = 0;
		String username[20];
		// �إ�Map-Hashing����
		Map<String, Integer> map = new HashMap<String, Integer>();

		// �x�sInput Thread��buffer�A�̤j�W��20�H
		String[] buf = new String[20];
		for(int i = 0; i < 20 ; ++i) buf[i]="";
		
		// �إ�ServerSocket����
		ServerSocket serverSocket = new ServerSocket(45321);
		
		// ���_���o�ϥΪ̳s�u
		while(true)
		{
			// �إ�Client�ݳs�u
			Socket connectionSocket = serverSocket.accept();
			
			// �qClient�ݨ��oBuffer
			BufferedReader inFromClient = 
				new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 

			// �����o�ϥΪ̪�ID
			String user = inFromClient.readLine();
			
			// ���oClient�ݪ�String
			String clientString = inFromClient.readLine();
			
			// �p�G�٨S���إߨϥΪ�ID
			if(!map.containsKey(user))
			{
				// �إߨϥΪ̰T��
				System.out.println("Create user: "+user+"->"+id);
				
				// �إߨϥΪ�ID��MAP
				map.put(user, id++);
			}
			
			// ���o�ϥΪ�ID
			now = map.get(user).intValue();
			
			// �p�G�OInput Thread
			if(clientString.charAt(0)=='I')
			{
				// �x�s���L�ϥΪ̪�buffer
				for(int i = 0; i < 20 ; ++i)
				{
					// ���F�ۤv�H�~���Ҧ��ϥΪ�
					if(i != now)
						buf[i] = buf[i].concat(user+" says: "+clientString.substring(1,clientString.length())+"#");
				}
				
				// ��X��Server�ݪ��e��
				System.out.println("Client("+connectionSocket.getInetAddress()+"): " + clientString);
			}
			// �p�G�ORequest Thread
			else
			{
				// �إ߿�X��Client�ݪ�Stream
				DataOutputStream  outToClient = 
						new DataOutputStream(connectionSocket.getOutputStream());
				
				// �^�Ц�Client�ݪ�String
				String reply;
				
				// �p�Gbuffer���F��
				if(!buf[now].equals("")) reply = "Y";
				else reply = "N";
				
				// �^�Ц�Client�ݪ�String
				reply = reply.concat(buf[now]);
				
				// �^�Ц�Client��
				outToClient.writeBytes(reply+'\n');
				
				// �M��buffer
				buf[now] = "";
			}
			
		}
	}
}
