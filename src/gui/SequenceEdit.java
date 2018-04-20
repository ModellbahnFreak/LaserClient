package gui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;

import client.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class SequenceEdit extends Scene {

	private Runnable zurueck;
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
	public SequenceEdit(Parent root, boolean createScene) {
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
	public SequenceEdit(Parent root, double width, double height, boolean createScene) {
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
	public SequenceEdit(Parent root, double width, double height, boolean depthBuffer, boolean createScene) {
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
	public SequenceEdit(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing,
			boolean createScene) {
		// super(root, width, height, depthBuffer, antiAliasing);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/*public void setOnSeqPlay(Runnable r) {
		playSeq = r;
	}

	public void setOnSeqCreate(Runnable r) {
		createSeq = r;
	}*/

	public void setOnZurueck(Runnable r) {
		zurueck = r;
	}

	private ScrollPane scrollBalken = new ScrollPane();
	private GridPane grid;
	private Text scenetitle;
	/*
	 * private Button btnSeqCreate; private Button btnSeqPlay;
	 */
	private ToggleButton btnBO;
	private Button btnBack;
	private Button btnSeq;
	private Label lblStatus;
	private GridPane btnBox;
	public Thread checkStateT;
	public ImageView iv;
	private Slider opSlide;

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

		scenetitle = new Text("Sequenzen");
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
		
		grid.add(btnBox, 2, 2);
		super.setRoot(grid);
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
