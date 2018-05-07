package java_chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Client implements Runnable
{
	private ClientGUI client;
	private Socket socket;
	private ObjectOutputStream out;
	private String nickname;
	private ObjectInputStream in;
	private Thread t;
	private String request;
	
	public Client() throws UnknownHostException, IOException
	{
		t = new Thread(this);
		t.setName("ClientReadingThread");
	}
	
	protected void connectServer(String server) throws UnknownHostException, IOException
	{
		
		socket = new Socket(server, 8008);
		t.start();

	}
	
	protected void sendMessage(String message) throws IOException
	{
		if(out == null)
		{
			out = new ObjectOutputStream(socket.getOutputStream());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String currenttime = sdf.format(time);

		message = "TSP" + currenttime + "\u001eMSG" /*nickname fehlt, channel fehlt*/ + message;
		out.writeUTF(message);
		out.flush();
	}
	
	public void run()
	{
		while(!Thread.currentThread().isInterrupted())
		{
			try
			{
				read();
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			catch (NullPointerException e)
			{
				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void showNotification(String message)
	{
		client.showNotification(message);
	}
	
	
	protected void closeClient()
	{
		t.interrupt();
		try
		{
			out.close();
			in.close();
			socket.close();
		}
		catch(IOException e)
		{
			System.out.println("Fehler beim schlie�en!");
		}
	}
	protected String read() throws IOException
	{
		if (in == null) 
		{
			in = new ObjectInputStream(socket.getInputStream());
		}
		request = in.readUTF();
		
		String[] protocol = request.split("\u001e");
		if (protocol.length == 2) 
		{
			if (protocol[0].substring(0, 2).equals("TSP") && protocol[1].substring(0, 2).equals("MSG")) 
			{
				Timestamp tsp = Timestamp.valueOf(protocol[0].substring(2, protocol[0].length()));
				String msg = protocol[1].substring(2, protocol[0].length());
				System.out.println(tsp);
				System.out.println(msg);
			} else 
			{
				System.out.println("Protokoll ung�ltig!");
			}
		} else 
		{
			System.out.println("Protokoll ung�ltig!");
		}
		
		return request;
	}
	
	protected void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
}
