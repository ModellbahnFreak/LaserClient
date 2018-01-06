package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainMenu extends Scene {
	
	Runnable optionen = null;
	Runnable verbinden = null;

	/**
     * Creates a main menu Scene with a specific root Node.
     *
     * @param root The root node of the scene graph
     * @param createScene If the scene should already be filled with content
     *
     * @throws NullPointerException if root is null
     * @see javafx.scene.Scene
     */
	public MainMenu(Parent root, boolean createScene) {
		super(root, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
     * Creates a main menu Scene with a specific root Node.
     *
     * @param root The root node of the scene graph
     * @param width The width of the scene
     * @param height The height of the scene
     * @param createScene If the scene should already be filled with content
     *
     * @throws NullPointerException if root is null
     * @see javafx.scene.Scene
     */
	public MainMenu(Parent root, double width, double height, boolean createScene) {
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
     * Creates a main menu Scene with a specific root Node.
     *
     * @param root The root node of the scene graph
     * @param width The width of the scene
     * @param height The height of the scene
     * @param depthBuffer The depth buffer flag
     * @param createScene If the scene should already be filled with content
     *
     * @throws NullPointerException if root is null
     * @see javafx.scene.Scene
     */
	public MainMenu(Parent root, double width, double height, boolean depthBuffer, boolean createScene) {
		//super(root, width, height, depthBuffer);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}

	/**
     * Creates a main menu Scene with a specific root Node.
     *
     * @param root The root node of the scene graph
     * @param width The width of the scene
     * @param height The height of the scene
     * @param depthBuffer The depth buffer flag
     * @param antiAliasing The scene anti-aliasing attribute. A value of
     * {@code null} is treated as DISABLED.
     * @param createScene If the scene should already be filled with content
     *
     * @throws NullPointerException if root is null
     * @see javafx.scene.Scene
     */
	public MainMenu(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing, boolean createScene) {
		//super(root, width, height, depthBuffer, antiAliasing);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}
	
	public void setOnOptionen(Runnable r) {
		optionen = r;
	}
	
	public void setOnVerbinden(Runnable r) {
		verbinden = r;
	}
	
	GridPane grid;
	Text scenetitle;
	Text ueberschrift;
	Button btnOption;
	Button btnConnect;
	HBox box;

	/**
	 * Creates the specific elements for the main menu
	 *
	 * @param
	 *
	 * @throws
	 */
	public void createMenu() {
		grid = new GridPane();
		grid.setBackground(null);
		grid.setAlignment(Pos.TOP_CENTER);
		// Space between Rows/cols
		grid.setHgap(10);
		grid.setVgap(10);
		// Space around the whole grid
		grid.setPadding(new Insets(25, 25, 25, 25));
		ueberschrift = new Text("Laser control Panel");
		ueberschrift.setId("scenetitle");
		ueberschrift.setFill(Color.WHITE);
		ueberschrift.setFont(Font.font("Cooper Black", FontWeight.NORMAL, 30));
		// Add Text to Cell 0-0 columnspan:2 rowspan:1
		grid.add(ueberschrift, 0, 0);
		
		scenetitle = new Text("Hauptmenü");
		scenetitle.setId("scenetitle");
		scenetitle.setFill(Color.WHITE);
		scenetitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		// Add Text to Cell 0-0 columnspan:2 rowspan:1
		grid.add(scenetitle, 0, 1);
		
		btnConnect = new Button("Verbinden");
		btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (verbinden != null) {
            		verbinden.run();
            	}
            }
        });
		box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(btnConnect);
        grid.add(box, 0, 3);
		
		btnOption = new Button("Optionen");
		btnOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (optionen != null) {
            		optionen.run();
            	}
            }
        });
		box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(btnOption);
        grid.add(box, 0, 4);
		super.setRoot(grid);
	}

}
