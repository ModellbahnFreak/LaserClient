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
	private final Queue<String> _recvData;
	private final Queue<String> _sendData;
	Socket socket = null;
	private boolean canceled = false;

	public Network(Queue<String> recvData, Queue<String> sendData) {
		this._recvData = recvData;
		this._sendData = sendData;
	}
	
	@Override
	public void run() {
		if (connect()) {
			System.out.println("Connected");
			Client.einst.setStatus("Verbunden");
			Thread sendT = new Thread(senden);
			Thread recvT = new Thread(empfangen);
			sendT.setDaemon(true);
			recvT.setDaemon(true);
			sendT.start();
			recvT.start();
			System.out.println("Daemons started - Working");
			while (sendT.getState()!=Thread.State.TERMINATED && recvT.getState()!=Thread.State.TERMINATED && !canceled && !Thread.currentThread().isInterrupted()) {
				
			}
			sendT.interrupt();
			recvT.interrupt();
			System.out.println("Closing");
			if (!closeConn()) {
				Thread.currentThread().interrupt();
				Client.einst.setStatus("Verbindung fehlerhaft getrennt");
				System.out.println("Closing error - Exiting");
			} else {
				Client.einst.setStatus("Verbindung getrennt");
				System.out.println("Completed");
			}
		} else {
			System.out.println("Connecting unsuccessful");
		}
		canceled = false;
	}
	
	private boolean connect() {
		socket = null;
		try {
			socket = new Socket(Client.einst.getHostName(), Client.einst.getPort());
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Client.einst.setStatus("Host nicht bekannt!");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Client.einst.setStatus("Verbindungsprobleme (vmtl. läuft der Server nicht)");
			return false;
		} finally {
		}
	}
	
	private Runnable senden = new Runnable() {
		
		@Override
		public void run() {
			PrintStream raus = null;
			try {
				synchronized (socket) {
					raus = new PrintStream(socket.getOutputStream());
				}
				String toSend;
				while (!Thread.currentThread().isInterrupted() && socket != null && socket.isConnected()) { 
					toSend = null;
					synchronized (_sendData) {
						toSend = _sendData.poll();
					}
					if (toSend != null) {
						System.out.println("Sending: " + toSend);
						raus.print(toSend + "\r\n");
					}
				}
			} catch (IOException e) {
				
			} finally {
				if (raus != null) {
					raus.close();
				}
			}
		}
	};
	
	private Runnable empfangen = new Runnable() {
		
		@Override
		public void run() {
			BufferedReader rein = null;
			try {
				synchronized (socket) {
					rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				}
				String s;
				boolean cancel = false;
				while (!Thread.currentThread().isInterrupted() && !cancel && (s = rein.readLine()) != null) {
					if ("exit".equals(s) || "connection;close".equals(s)) {
						cancel = true;
					} else {
						//System.out.println("Received cmd: " + s);
						synchronized (_recvData) {
							_recvData.add(s);
						}
					}
				}
			} catch (IOException e) {
				
			} finally {
				if (rein != null) {
					try {
						rein.close();
					} catch (IOException e) {
						
					}
				}
			}
		}
	};
	
	private boolean closeConn() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
			synchronized (_recvData) {
				_recvData.add("connection;close");
			}
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/*public void runAlt() {
		int ScreenSize = 0;
		int ReceivingSpecial = 0; // 0: normal, 1: VideoList, 2: ImgList, 3: ObjList
		try {
			
			raus = new PrintStream(socket.getOutputStream());
			reinStream = socket.getInputStream();
			rein = new BufferedReader(new InputStreamReader(reinStream));
			
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
						if ("422".equals(empf.substring(0, 3))) {
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
						if ("424".equals(empf.substring(0, 3))) {
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
						if ("426".equals(empf.substring(0, 3))) {
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
			e.printStackTrace();
		} catch (IOException e) {
			
			System.out.println("IOProbleme (vmtl. läuft der Server nicht)");
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
					
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
	}*/

	public void cancel() {
		canceled = true;
	}
}
