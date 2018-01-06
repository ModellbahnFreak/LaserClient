package client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Queue;

import javax.imageio.ImageIO;

public class Network implements Runnable {
	private final Queue<String> _comm;
	private final Queue<String> _SendComm;
	/*
	 * private String _host = "localhost"; private int _port = 8082;
	 */
	Socket socket = null;
	// private OutputStream raus;
	private PrintStream raus;
	private BufferedReader rein;
	private InputStream reinStream;
	private boolean canceled = false;

	public Network(Queue<String> comm, Queue<String> SendComm) {
		this._comm = comm;
		this._SendComm = SendComm;
	}

	@Override
	public void run() {
		socket = null;
		int ScreenSize = 0;
		int ReceivingSpecial = 0; // 0: normal, 1: VideoList, 2: ImgList, 3: ObjList
		try {
			socket = new Socket(Client.einst.getHostName(), Client.einst.getPort());
			raus = new PrintStream(socket.getOutputStream());
			reinStream = socket.getInputStream();
			rein = new BufferedReader(new InputStreamReader(reinStream));
			Client.einst.setStatus("Verbunden");
			System.out.println("Connected");
			while (socket.isConnected() && !canceled) {
				String senden = null;
				synchronized (_SendComm) {
					senden = _SendComm.poll();
				}
				if (senden != null) {
					raus.println(senden);
					System.out.println("Sended");
				}
				while (rein.ready()) {
					String empf = rein.readLine();
					switch (ReceivingSpecial) {
					case 1:
						if (empf.substring(0, 3).equals("422")) {
							synchronized (Client.einst.getVidList()) {
								Client.einst.getVidList().notifyAll();
							}
							ReceivingSpecial = 0;
						} else {
							synchronized (Client.einst.getVidList()) {
								Client.einst.getVidList().add(empf);
							}
						}
						break;
					case 2:
						if (empf.substring(0, 3).equals("424")) {
							synchronized (Client.einst.getImgList()) {
								Client.einst.getImgList().notifyAll();
							}
							ReceivingSpecial = 0;
						} else {
							synchronized (Client.einst.getImgList()) {
								Client.einst.getImgList().add(empf);
							}
						}
						break;
					case 3:
						if (empf.substring(0, 3).equals("426")) {
							synchronized (Client.einst.getObjList()) {
								Client.einst.getObjList().notifyAll();
							}
							ReceivingSpecial = 0;
						} else {
							synchronized (Client.einst.getObjList()) {
								Client.einst.getObjList().add(empf);
							}
						}
						break;
					default:
						switch (empf.substring(0, 3)) {
						case "421":
							Client.einst.getVidList().clear();
							System.out.println("Video list");
							ReceivingSpecial = 1;
							break;
						case "423":
							Client.einst.getImgList().clear();
							System.out.println("Img list");
							ReceivingSpecial = 2;
							break;
						case "425":
							Client.einst.getObjList().clear();
							System.out.println("Obj list");
							ReceivingSpecial = 3;
							break;
						default:
							synchronized (_comm) {
								_comm.add(empf);
							}
							System.out.println("Empfangen: '" + empf + "'");
							break;
						}
						break;
					}
				}
			}
		} catch (

		UnknownHostException e) {
			Client.einst.setStatus("Host nicht bekannt!");
			System.out.println("Unknown Host");
			e.printStackTrace();
		} catch (IOException e) {
			Client.einst.setStatus("Verbindungsprobleme (vmtl. läuft der Server nicht)");
			System.out.println("IOProbleme (vmtl. läuft der Server nicht)");
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
					Client.einst.setStatus("Verbindung getrennt");
					System.out.println("Closed");
				} catch (IOException e) {
					Client.einst.setStatus("Fehler beim Trennen der Verbindung");
					System.out.println("Fehler beim Schliessen");
					e.printStackTrace();
				}
			}
			System.out.println("Connection closed");
		}
		canceled = false;
	}

	public void cancel() {
		canceled = true;
	}
}
