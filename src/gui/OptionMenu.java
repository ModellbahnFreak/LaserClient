package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class OptionMenu extends Scene {
	
	private Runnable speichern = null;
	private Runnable abbrechen = null;
	
	 /** Creates a main menu Scene with a specific root Node.
     *
     * @param root The root node of the scene graph
     * @param createScene If the scene should already be filled with content
     *
     * @throws NullPointerException if root is null
     * @see javafx.scene.Scene
     */
	public OptionMenu(Parent root, boolean createScene) {
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
	public OptionMenu(Parent root, double width, double height, boolean createScene) {
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
	public OptionMenu(Parent root, double width, double height, boolean depthBuffer, boolean createScene) {
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
	public OptionMenu(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing, boolean createScene) {
		//super(root, width, height, depthBuffer, antiAliasing);
		super(root, width, height, Color.BLACK);
		if (createScene) {
			createMenu();
		}
	}
	
	public void setOnSpeichern(Runnable r) {
		speichern = r;
	}
	
	public void setOnAbbruch(Runnable r) {
		abbrechen = r;
	}
	
	GridPane grid;
	Text scenetitle;
	Label hostName;
	TextField txtHostName;
	Label portNum;
	TextField txtPortNum;
	Button btnSave;
	Button btnCancel;
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

		scenetitle = new Text("Optionen");
		scenetitle.setId("scenetitle");
		scenetitle.setFill(Color.WHITE);
		scenetitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		// Add Text to Cell 0-0 columnspan:2 rowspan:1
		grid.add(scenetitle, 0, 0, 2, 1);
		
		hostName = new Label("Hostname:");
		hostName.setTextFill(Color.GRAY);
        grid.add(hostName, 0, 1);

        txtHostName = new TextField();
        grid.add(txtHostName, 1, 1);
        
        portNum = new Label("Port (0-65535):");
        portNum.setTextFill(Color.GRAY);
        grid.add(portNum, 0, 2);

        txtPortNum = new TextField();
        grid.add(txtPortNum, 1, 2);
        
        txtPortNum.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,5}?")) {
                	txtPortNum.setText(oldValue);
                } else {
                	if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > 65535) {
                		txtPortNum.setText(oldValue);
                	}
                }
            }
        });
		
		btnSave = new Button("Speichern");
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (speichern != null) {
            		speichern.run();
            	}
            }
        });
        btnCancel = new Button("Abbrechen");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (abbrechen != null) {
            		abbrechen.run();
            	}
            }
        });
        box = new HBox(10);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        box.getChildren().add(btnCancel);
        box.getChildren().add(btnSave);
        grid.add(box, 0, 4, 2, 1);
		super.setRoot(grid);
	}
	
	public void setHostName(String text) {
		txtHostName.setText(text);
	}
	
	public String getHostName() {
		return txtHostName.getText();
	}
	
	public void setPort(int port) {
		txtPortNum.setText(String.valueOf(port));
	}
	
	public int getPort() {
		return Integer.parseInt(txtPortNum.getText());
	}
}
