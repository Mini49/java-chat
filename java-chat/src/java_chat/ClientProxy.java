package java_chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;

public class ClientProxy implements Runnable
{

	private Socket socket;
	private Server server;
	private String nickname;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Thread t;
	private HashMap<String, Timestamp> messageBuffer;

	public ClientProxy(Socket socket, Server server)
	{
		messageBuffer = new HashMap<String, Timestamp>();
		this.socket = socket;
		this.server = server;
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run()
	{
		while (!t.isInterrupted())
		{
			try
			{
				if (in == null)
				{
					in = new ObjectInputStream(socket.getInputStream());
				}

				read(in.readUTF());
			}
			catch (IOException e)
			{
				System.out.println("Failed reading request!");
				e.printStackTrace();
				t.interrupt();
			}
		}
	}

	private void read(Object obj)
	{
		String request = (String) obj;

		String[] protocol = request.split("\u001e");

		if (protocol[0].substring(0, 3).equals("TSP") && protocol[1].substring(0, 3).equals("CHN") && protocol[2].substring(0, 3).equals("MSG"))
		{
			Timestamp tsp = Timestamp.valueOf(protocol[0].substring(3, protocol[0].length()));
			String chn = protocol[1].substring(3, protocol[2].length());
			String msg = protocol[2].substring(3, protocol[3].length());

			String nikmsg = nickname + ": " + msg;
			
<<<<<<< HEAD
			String fullmessage = tsp + "\u001e" + chn + "\u001e" + nikmsg; //Message zusammengesetzt nach Protokoll
			
=======
			String fullmessage = "TSP" + tsp + "\u001eCHN" + chn + "\u001eMSG" + nikmsg; //Message zusammengesetzt nach Protokoll		
>>>>>>> branch 'master' of https://github.com/fi2016/java-chat.git
			
			if (checkSpam(msg, tsp))
			{
				System.out.println("Diese Nachricht war SPAM!");
			}
			else
<<<<<<< HEAD
			{
				server.verteileNachricht(nikmsg, chn); //WIESO GENAU JETZT EIGENTLICH NUR DAS UND NICHT PROTOKOLL? -> IM CLIENT GEHTS' IN DIE ELSE BEIM IF.
				// An Server schicken und er verteilt an ClientPRoxys im Raum
				// andere Nachrichtentypen abfangen CMD usw.
				server.verteileNachricht(fullmessage, chn);
=======
			{
				server.verteileNachricht(fullmessage, chn);
>>>>>>> branch 'master' of https://github.com/fi2016/java-chat.git
			}
		}
		else
		{
			System.out.println(request);
			
			//NIK = null
<<<<<<< HEAD
			System.out.println("Protocol: im Else-Zweig" + request);
=======
			System.out.println("Protocol: im Else-Zweig" + request);
>>>>>>> branch 'master' of https://github.com/fi2016/java-chat.git
			//System.out.println(protocol[0].substring(0, 3));
			System.out.println("TSP: " + protocol[0] + " CHN: " + protocol[1]);
			System.out.println("Protokoll ungültig!");
		}
	}
	// überprüfen des timestamps & channels (spam)
	// überprüfen ob immer das selbe geschickt wird

	private boolean checkSpam(String msg, Timestamp tsp)
	{
		for (Timestamp t : messageBuffer.values())
		{
			if (t.getNanos() >= tsp.getNanos() - 100)
			{
				return true;
			}
			else
			{
				if (messageBuffer.containsKey(msg))
				{
					return true;
				}
				else
				{
					messageBuffer.put(msg, tsp);
					return false;
				}
			}
		}
		return false;
	}

	protected void sendMessage(String message)
	{
		try
		{
			if (out == null)
			{
				out = new ObjectOutputStream(socket.getOutputStream());
			}
			out.writeUTF(message);
			out.flush();

		}
		catch (IOException e)
		{
			System.out.println("Failed sending response!");
			e.printStackTrace();
		}
	}

	protected void closeClient()
	{
		try
		{
			t.interrupt();
			out.close();
			socket.close();
		}
		catch (IOException e)
		{
			System.out.println("Failed closing client!");
			e.printStackTrace();
		}
	}

	public String getNickname()
	{
		return nickname;
	}
}
