package client;

import java.util.Queue;

public class NetwProcess implements Runnable {

	private final Queue<String> _recvData;
	private final Queue<String> _sendData;
	
	public NetwProcess(Queue<String> recvData, Queue<String> sendData) {
		_recvData = recvData;
		_sendData = sendData;
	}
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			String recvText = null;
			synchronized (_recvData) {
				recvText = _recvData.poll();
			}
			if (recvText != null) {
				switch (recvText) {
				case "421:":
					getVidList();
					break;
				case "423:":
					getImgList();
					break;
				case "425:":
					getObjList();
					break;
				default:
					System.out.println("Empfangen: " + recvText);
					break;
				}
			}
		}
	}
	
	private void getVidList() {
		Client.einst.getVidList().clear();
		String recvText = _recvData.poll();
		while(!"422:".equals(recvText)) {
			if (recvText != null) {
				synchronized (Client.einst.getVidList()) {
					Client.einst.getVidList().add(recvText);
				}
			}
			recvText = _recvData.poll();
		}
		synchronized (Client.einst.getVidList()) {
			Client.einst.getVidList().notifyAll();
		}
	}
	
	private void getImgList() {
		Client.einst.getImgList().clear();
		String recvText = _recvData.poll();
		while(!"424:".equals(recvText)) {
			if (recvText != null) {
				synchronized (Client.einst.getImgList()) {
					Client.einst.getImgList().add(recvText);
				}
			}
			recvText = _recvData.poll();
		}
		synchronized (Client.einst.getImgList()) {
			Client.einst.getImgList().notifyAll();
		}
	}

	private void getObjList() {
		Client.einst.getObjList().clear();
		String recvText = _recvData.poll();
		while(!"426:".equals(recvText)) {
			if (recvText != null) {
				synchronized (Client.einst.getObjList()) {
					Client.einst.getObjList().add(recvText);
				}
			}
			recvText = _recvData.poll();
		}
		synchronized (Client.einst.getObjList()) {
			Client.einst.getObjList().notifyAll();
		}
	}
	
}
