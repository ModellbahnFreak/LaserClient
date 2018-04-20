package client;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import gui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ControlGui extends Application implements Runnable {
	public static ControlGui INSTANCE = null;
	public Group root;
	public Scene scene;
	public ArrayList<Node> addNodeList = new ArrayList<Node>();
	public ArrayList<Node> delNodeList = new ArrayList<Node>();

	@Override
	public void init() {
		try {
			super.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		INSTANCE = this;
	}

	@Override
	public void start(Stage primaryStage) {
		root = new Group();
		// scene = new Scene(root, 800, 600, Color.BLACK);
		MainMenu hauptmenue = new MainMenu(root, 800, 600, true);
		OptionMenu optionen = new OptionMenu(new Group(), 800, 600, true);
		MainControl steuer = new MainControl(new Group(), 800, 600, true);
		SequenceEdit seqEdit = new SequenceEdit(new Group(), 800, 600, true);
		hauptmenue.setOnOptionen(new Runnable() {

			@Override
			public void run() {
				optionen.setHostName(Client.einst.getHostName());
				optionen.setPort(Client.einst.getPort());
				primaryStage.setScene(optionen);
			}
		});
		hauptmenue.setOnVerbinden(new Runnable() {

			@Override
			public void run() {
				Client.connectToBeamer();
				primaryStage.setScene(steuer);
			}
		});
		optionen.setOnSpeichern(new Runnable() {

			@Override
			public void run() {
				Client.einst.setHostName(optionen.getHostName());
				Client.einst.setPort(optionen.getPort());
				primaryStage.setScene(hauptmenue);
			}
		});
		optionen.setOnAbbruch(new Runnable() {

			@Override
			public void run() {
				primaryStage.setScene(hauptmenue);
			}
		});
		steuer.setOnZurueck(new Runnable() {

			@Override
			public void run() {
				steuer.checkStateT.interrupt();
				Client.closeBeamerConnection();
				primaryStage.setScene(hauptmenue);
			}
		});
		steuer.setOnGoSequence(new Runnable() {

			@Override
			public void run() {
				steuer.checkStateT.interrupt();
				primaryStage.setScene(seqEdit);
			}
		});
		seqEdit.setOnZurueck(new Runnable() {

			@Override
			public void run() {
				//steuer.checkStateT.interrupt();
				primaryStage.setScene(steuer);
			}
		});
		/*steuer.setOnSeqCreate(new Runnable() {

			@Override
			public void run() {
				Client.sendData.add(
						"sequence;Seq1\r\n" + "img;Bild1;E:\\Georg\\Bilder\\Martin_und_Regine.JPG;0;0;full;full\r\n"
								+ "txt;txt1;500;500;'Hallo;Welt';#FF00FF\r\n" + "cue;1000;1000;0;1000\r\n"
								+ "move;txt1;100;100\r\n" + "move;Bild1;500;500\r\n" + "cueEnd\r\n"
								+ "cue;0;1000;0;1000\r\n" + "scale;Bild1;2;2\r\n" + "cueEnd\r\n"
								+ "cue;0;1000;1000;1000\r\n" + "opacity;txt1;0\r\n" + "cueEnd\r\n" + "SeqEnd");
			}
		});
		steuer.setOnSeqPlay(new Runnable() {

			@Override
			public void run() {
				Client.sendData.add("playSeq;Seq1");
			}
		});
		Runnable checkScreensh = new Runnable() {

			@Override
			public void run() {
				while (Client.einst.getScreenshActive()) {
					BufferedImage bildBuff = Client.einst.getScreenshot();
					if (bildBuff != null) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Image bild = SwingFXUtils.toFXImage(bildBuff, null);
								steuer.iv.setImage(bild);
							}
						});
					}
				}
			}
		};
		
		steuer.setOnScreenshot(new Runnable() {

			@Override
			public void run() {
				if (Client.einst.getScreenshActive()) {
					Client.sendData.add("system;screenshot;0");
					Client.einst.setScreenshActive(false);
				} else {
					Client.sendData.add("system;screenshot;1");
					Client.einst.setScreenshActive(true);
					Thread ScreenshT = new Thread(checkScreensh);
					ScreenshT.setDaemon(true);
					ScreenshT.start();
				}
			}
		});
		steuer.setOnGetScreenshot(new Runnable() {

			@Override
			public void run() {
				BufferedImage bildBuff = Client.einst.getScreenshot();
				if (bildBuff != null) {
					Image bild = SwingFXUtils.toFXImage(bildBuff, null);
					steuer.iv.setImage(bild);
				}
			}
		});*/

		primaryStage.setScene(hauptmenue);

		System.out.println("Gui started");
		primaryStage.setTitle("Laser control Panel");
		URL iconPath;
		iconPath = getClass().getResource("/icon.png");
		primaryStage.getIcons().add(new Image(iconPath.toString()));
		primaryStage.show();
		// primaryStage.setFullScreen(true);

		primaryStage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case F:
					primaryStage.setFullScreen(true);
					break;
				default:
					break;
				}
			}
		});

		Task<Void> addTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				while (true) {
					if (!addNodeList.isEmpty()) {
						final Node addNode = addNodeList.get(0);
						addNodeList.remove(0);
						System.out.println("adding " + addNode.getId());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								root.getChildren().add(addNode);
							}
						});
					}
					Thread.sleep(100);
				}
			}
		};
		Thread addTh = new Thread(addTask);
		addTh.setDaemon(true);
		addTh.start();

		Task<Void> delTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				while (true) {
					if (!delNodeList.isEmpty()) {
						final Node delNode = delNodeList.get(0);
						delNodeList.remove(0);
						System.out.println("removing " + delNode.getId());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if (delNode instanceof MediaView) {
									((MediaView) delNode).getMediaPlayer().stop();
								}
								root.getChildren().remove(delNode);
							}
						});
					}
					Thread.sleep(100);
				}
			}
		};
		Thread delTh = new Thread(delTask);
		delTh.setDaemon(true);
		delTh.start();

	}

	@Override
	public void run() {
		System.out.println("Gui start");
		launch("");
	}
}
