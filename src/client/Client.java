package client;

import java.util.LinkedList;
import java.util.Queue;

public class Client {
	
	public static Settings einst = new Settings();
	private static Network netw = null;
	private static NetwProcess verarb;
	private static ControlGui gui = null;
	private static Thread netwT;
	public static Queue<String> recvData = new LinkedList<String>();
	public static Queue<String> sendData = new LinkedList<String>();

	public static void main(String[] args) {
		if (args.length >= 2) {
			einst.setHostName(args[0]);
			einst.setPort(Integer.parseInt(args[1]));
		}
		gui = new ControlGui();
		netw = new Network(recvData, sendData);
		verarb = new NetwProcess(recvData, sendData);
		Thread verarbT = new Thread(verarb);
		verarbT.setDaemon(true);
		verarbT.start();
		Thread guiT = new Thread(gui);
		guiT.start();

		/*
		 * synchronized (SendComm) { SendComm.add("Text"); }
		 */
	}
	
	public static void connectToBeamer() {
		netwT = new Thread(netw);
		netwT.setDaemon(true);
		netwT.start();
	}
	
	public static void closeBeamerConnection() {
		//netw.cancel();
		 netwT.interrupt();
	}

}
