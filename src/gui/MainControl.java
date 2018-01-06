package gui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;

import client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainControl extends Scene {

	private Runnable playSeq;
	private Runnable createSeq;
	private Runnable zurueck;
	private Runnable toggleScreen;
	private Runnable getScreen;
	private int vidNummer = 0;
	private int imgNummer = 0;

	/**
	 * Creates a main menu Scene with a specific root Node.
	 *
	 * @param root
	 *            The root node of the scene graph
	 * @param createScene
	 *            If the scene should already be filled with content
	 *
	 * @throws NullPointerException
	 *             if root is null
	 * @see javafx.scene.Scene
	 */
	public MainControl(Parent root, boolean createScene) {
		super(root, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
	 * Creates a main menu Scene with a specific root Node.
	 *
	 * @param root
	 *            The root node of the scene graph
	 * @param width
	 *            The width of the scene
	 * @param height
	 *            The height of the scene
	 * @param createScene
	 *            If the scene should already be filled with content
	 *
	 * @throws NullPointerException
	 *             if root is null
	 * @see javafx.scene.Scene
	 */
	public MainControl(Parent root, double width, double height, boolean createScene) {
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
	 * Creates a main menu Scene with a specific root Node.
	 *
	 * @param root
	 *            The root node of the scene graph
	 * @param width
	 *            The width of the scene
	 * @param height
	 *            The height of the scene
	 * @param depthBuffer
	 *            The depth buffer flag
	 * @param createScene
	 *            If the scene should already be filled with content
	 *
	 * @throws NullPointerException
	 *             if root is null
	 * @see javafx.scene.Scene
	 */
	public MainControl(Parent root, double width, double height, boolean depthBuffer, boolean createScene) {
		// super(root, width, height, depthBuffer);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
	 * Creates a main menu Scene with a specific root Node.
	 *
	 * @param root
	 *            The root node of the scene graph
	 * @param width
	 *            The width of the scene
	 * @param height
	 *            The height of the scene
	 * @param depthBuffer
	 *            The depth buffer flag
	 * @param antiAliasing
	 *            The scene anti-aliasing attribute. A value of {@code null} is
	 *            treated as DISABLED.
	 * @param createScene
	 *            If the scene should already be filled with content
	 *
	 * @throws NullPointerException
	 *             if root is null
	 * @see javafx.scene.Scene
	 */
	public MainControl(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing,
			boolean createScene) {
		// super(root, width, height, depthBuffer, antiAliasing);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	public void setOnSeqPlay(Runnable r) {
		playSeq = r;
	}

	public void setOnSeqCreate(Runnable r) {
		createSeq = r;
	}

	public void setOnZurueck(Runnable r) {
		zurueck = r;
	}

	public void setOnScreenshot(Runnable r) {
		toggleScreen = r;
	}

	public void setOnGetScreenshot(Runnable r) {
		getScreen = r;
	}

	private ScrollPane scrollBalken = new ScrollPane();
	private GridPane grid;
	private Text scenetitle;
	/*
	 * private Button btnSeqCreate; private Button btnSeqPlay;
	 */
	private ToggleButton btnBO;
	private Button btnBack;
	private Label lblStatus;
	private GridPane btnBox;
	public Thread checkStateT;
	public ImageView iv;

	private String _status = "Nicht verbunden";

	/**
	 * Creates the specific elements for the main menu
	 *
	 * @param
	 *
	 * @throws
	 */
	public void createMenu() {
		grid = new GridPane();
		scrollBalken.setContent(grid);
		grid.setBackground(null);
		grid.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		grid.setHgap(10);
		grid.setVgap(10);
		// Space around the whole grid
		grid.setPadding(new Insets(25, 25, 25, 25));

		btnBack = new Button();
		btnBack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (zurueck != null) {
					zurueck.run();
				}
			}
		});
		btnBack.setTextFill(Color.WHITE);
		btnBack.setStyle("-fx-base: #202020;");
		ImageView pfeilBild = new ImageView(new Image(getClass().getResource("/pfeil.png").toString()));
		pfeilBild.setPreserveRatio(true);
		pfeilBild.setFitHeight(10);
		btnBack.setGraphic(pfeilBild);
		grid.add(btnBack, 0, 0);

		scenetitle = new Text("Steuerung");
		scenetitle.setId("scenetitle");
		scenetitle.setFill(Color.WHITE);
		scenetitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		// Add Text to Cell 0-0 columnspan:2 rowspan:1
		grid.add(scenetitle, 1, 0, 3, 1);

		lblStatus = new Label("Status: " + _status);
		lblStatus.setTextFill(Color.GRAY);
		grid.add(lblStatus, 1, 1, 3, 1);
		Runnable checkState = new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					setStatus(Client.einst.getStatus());
					synchronized (Client.einst) {
						try {
							Client.einst.wait();
						} catch (InterruptedException e) {
							System.out.println("Canceled waiting for new State");
						}
					}
				}
			}
		};
		checkStateT = new Thread(checkState);
		checkStateT.setDaemon(true);
		checkStateT.start();

		// Rectangle rect = new Rectangle(super.getWidth() / 2.0, (super.getWidth() /
		// 2.0)*(9.0/16.0), Color.GRAY);
		iv = new ImageView();
		iv.setFitWidth(super.getWidth() / 2.0);
		iv.setFitHeight((super.getWidth() / 2.0) * (9.0 / 16.0));
		// grid.add(iv, 1, 2);

		btnBox = new GridPane();
		btnBox.setBackground(null);
		btnBox.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		btnBox.setHgap(10);
		btnBox.setVgap(10);
		// Space around the whole grid
		btnBox.setPadding(new Insets(10, 10, 10, 10));

		/*
		 * btnSeqCreate = new Button("Sequenz erstellen"); btnSeqCreate.setOnAction(new
		 * EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) { if (createSeq != null) {
		 * createSeq.run(); } } }); btnBox.add(btnSeqCreate, 0, 0);
		 * 
		 * btnSeqPlay = new Button("Sequenz abspielen"); btnSeqPlay.setOnAction(new
		 * EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) { if (playSeq != null) {
		 * playSeq.run(); } } }); btnBox.add(btnSeqPlay, 1, 0);
		 */

		btnBO = new ToggleButton("Blackout");
		btnBO.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (btnBO.isSelected()) {
					Client.SendComm.add("blackout;1");
				} else {
					Client.SendComm.add("blackout;0");
				}
			}
		});
		btnBox.add(btnBO, 0, 0);

		btnBox.add(createVidBox(), 0, 1);

		btnBox.add(createImgBox(), 0, 2);

		btnBox.add(createObjBox(), 0, 3);

		scrollBalken.setStyle("	.scroll-pane{\r\n" + "   				-fx-background-color:transparent;\r\n"
				+ "				}\r\n" + "				.scroll-pane > .viewport {\r\n"
				+ "   				-fx-background-color: transparent;\r\n" + "				}");
		scrollBalken.setBackground(null);
		grid.add(btnBox, 2, 2);
		super.setRoot(grid);
	}

	private GridPane createVidBox() {
		GridPane vidBox;
		Button btnPlayVid;
		// Button btnDelVid;
		Button btnRefreshVid;
		Label lblVid;
		ListView<String> fileList;

		vidBox = new GridPane();
		vidBox.setBackground(null);
		vidBox.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		vidBox.setHgap(5);
		vidBox.setVgap(5);
		// Space around the whole grid
		vidBox.setPadding(new Insets(10, 10, 10, 10));

		lblVid = new Label("Video: ");
		lblVid.setTextFill(Color.WHITE);
		vidBox.add(lblVid, 0, 0);

		fileList = new ListView<String>();
		ObservableList<String> filenames = FXCollections.observableArrayList();
		fileList.setItems(filenames);
		vidBox.add(fileList, 0, 1, 1, 4);

		btnRefreshVid = new Button("Aktualisieren");
		btnRefreshVid.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Client.SendComm.add("system;refresh;video");
				Thread checkReceive = new Thread(new Runnable() {

					@Override
					public void run() {
						synchronized (Client.einst.getVidList()) {
							try {
								Client.einst.getVidList().wait(2000);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										filenames.clear();
										for (String s : Client.einst.getVidList()) {
											filenames.add(s);
										}
									}
								});
							} catch (InterruptedException e) {
								System.out.println("Warten auf Videoliste unterbrochen");
								e.printStackTrace();
							}
						}
					}
				});
				checkReceive.setDaemon(true);
				checkReceive.start();
			}
		});
		vidBox.add(btnRefreshVid, 1, 1);

		btnPlayVid = new Button("Video erstellen");
		btnPlayVid.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/*
				 * TextInputDialog dialog = new TextInputDialog(filenameVid);
				 * dialog.setTitle("Video erstellen"); dialog.setHeaderText("Video Filname");
				 * dialog.setContentText("Dateiname:");
				 * 
				 * // Traditional way to get the response value. Optional<String> result =
				 * dialog.showAndWait(); if (result.isPresent()) { filenameVid = result.get();
				 * System.out.println("Neues Video: " + filenameVid);
				 * 
				 * }
				 */
				String filenameVid = fileList.getSelectionModel().getSelectedItem();
				if (filenameVid != null && filenameVid.length() > 0) {
					Client.SendComm.add("vid;vid" + vidNummer + ";" + filenameVid + ";loop;0;0;full;full");
					vidNummer++;
				}
				refreshObjList();
			}
		});
		vidBox.add(btnPlayVid, 1, 2);

		/*
		 * btnDelVid = new Button("Video löschen"); btnDelVid.setOnAction(new
		 * EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) {
		 * Client.SendComm.add("del;vid1"); } }); vidBox.add(btnDelVid, 1, 3);
		 */
		return vidBox;
	}

	private GridPane createImgBox() {
		GridPane imgBox;
		Label lblImg;
		Button btnShowImg;
		// Button btnDelImg;
		Button btnRefreshImg;
		ListView<String> fileList;
		imgBox = new GridPane();
		imgBox.setBackground(null);
		imgBox.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		imgBox.setHgap(5);
		imgBox.setVgap(5);
		// Space around the whole grid
		imgBox.setPadding(new Insets(10, 10, 10, 10));

		lblImg = new Label("Bild: ");
		lblImg.setTextFill(Color.WHITE);
		imgBox.add(lblImg, 0, 0);

		fileList = new ListView<String>();
		ObservableList<String> filenames = FXCollections.observableArrayList();
		fileList.setItems(filenames);
		imgBox.add(fileList, 0, 1, 1, 4);

		btnRefreshImg = new Button("Aktualisieren");
		btnRefreshImg.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Client.SendComm.add("system;refresh;img");
				Thread checkReceive = new Thread(new Runnable() {

					@Override
					public void run() {
						synchronized (Client.einst.getImgList()) {
							try {
								Client.einst.getImgList().wait(2000);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										filenames.clear();
										for (String s : Client.einst.getImgList()) {
											filenames.add(s);
										}
									}
								});
							} catch (InterruptedException e) {
								System.out.println("Warten auf Imageliste unterbrochen");
								e.printStackTrace();
							}
						}
					}
				});
				checkReceive.setDaemon(true);
				checkReceive.start();
			}
		});
		imgBox.add(btnRefreshImg, 1, 1);

		btnShowImg = new Button("Bild erstellen");
		btnShowImg.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/*
				 * TextInputDialog dialog = new TextInputDialog(filenameImg);
				 * dialog.setTitle("Bild erstellen"); dialog.setHeaderText("Bild Filname");
				 * dialog.setContentText("Dateiname:");
				 * 
				 * // Traditional way to get the response value. Optional<String> result =
				 * dialog.showAndWait(); if (result.isPresent()) { filenameImg = result.get();
				 * System.out.println("Neues Video: " + filenameImg);
				 * Client.SendComm.add("img;img1;" + filenameImg + ";0;0;full;full"); }
				 */
				String filenameImg = fileList.getSelectionModel().getSelectedItem();
				if (filenameImg != null && filenameImg.length() > 0) {
					Client.SendComm.add("img;img" + vidNummer + ";" + filenameImg + ";0;0;full;full");
					vidNummer++;
				}
				refreshObjList();
			}
		});
		imgBox.add(btnShowImg, 1, 2);

		/*
		 * btnDelImg = new Button("Bild löschen"); btnDelImg.setOnAction(new
		 * EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) {
		 * Client.SendComm.add("del;img1"); } }); imgBox.add(btnDelImg, 1, 3);
		 */
		return imgBox;
	}

	ObservableList<String> objekte;

	private GridPane createObjBox() {
		GridPane objBox;
		Label lblObj;
		Button btnDelObj;
		Button btnDelAll;
		Button btnRefreshObj;
		Button btnEditObj;
		ListView<String> objList;
		objBox = new GridPane();
		objBox.setBackground(null);
		objBox.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		objBox.setHgap(5);
		objBox.setVgap(5);
		// Space around the whole grid
		objBox.setPadding(new Insets(10, 10, 10, 10));

		lblObj = new Label("Bild: ");
		lblObj.setTextFill(Color.WHITE);
		objBox.add(lblObj, 0, 0);

		objList = new ListView<String>();
		objekte = FXCollections.observableArrayList();
		objList.setItems(objekte);
		objBox.add(objList, 0, 1, 1, 4);

		btnRefreshObj = new Button("Aktualisieren");
		btnRefreshObj.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				refreshObjList();
			}
		});
		objBox.add(btnRefreshObj, 1, 1);

		btnEditObj = new Button("Objekt bearbeiten");
		btnEditObj.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String editName = objList.getSelectionModel().getSelectedItem();
				String[] eingaben = new String[4];
				String[] ausg = {"X-Position:", "Y-Position:", "Breite:", "Höhe:"};
				for (int i = 0; i < 4; i++) {
					TextInputDialog dialog = new TextInputDialog(eingaben[i]);
					dialog.setTitle("Objekt bearbeiten");
					dialog.setHeaderText("Parameter eingeben");
					dialog.setContentText(ausg[i]);

					// Traditional way to get the response value.
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						eingaben[i] = result.get();
						System.out.println("Edit obj " + i + ": " + eingaben[i]);

					}
				}
				Client.SendComm.add("xPos;" + editName + ";" + eingaben[0]);
				Client.SendComm.add("yPos;" + editName + ";" + eingaben[1]);
				Client.SendComm.add("width;" + editName + ";" + eingaben[2]);
				Client.SendComm.add("height;" + editName + ";" + eingaben[3]);
			}
		});
		objBox.add(btnEditObj, 1, 2);

		btnDelObj = new Button("Objekt löschen");
		btnDelObj.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String loeschName = objList.getSelectionModel().getSelectedItem();
				Client.SendComm.add("del;" + loeschName);
				refreshObjList();
			}
		});
		objBox.add(btnDelObj, 1, 3);

		btnDelAll = new Button("Alle löschen");
		btnDelAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Client.SendComm.add("del;all");
				refreshObjList();
			}
		});
		objBox.add(btnDelAll, 1, 4);
		return objBox;
	}

	private void refreshObjList() {
		Client.SendComm.add("system;refresh;objs");
		Thread checkReceive = new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (Client.einst.getObjList()) {
					try {
						Client.einst.getObjList().wait(2000);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								objekte.clear();
								for (String s : Client.einst.getObjList()) {
									objekte.add(s);
								}
							}
						});
					} catch (InterruptedException e) {
						System.out.println("Warten auf Objektliste unterbrochen");
						e.printStackTrace();
					}
				}
			}
		});
		checkReceive.setDaemon(true);
		checkReceive.start();
	}

	public void setStatus(String status) {
		_status = status;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lblStatus.setText("Status: " + _status);
			}
		});
	}

	public String getStatus() {
		return _status;
	}
}
