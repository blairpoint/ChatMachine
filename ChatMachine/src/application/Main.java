package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Main extends Application {
	char[] alphabet = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j',
			'J', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T',
			'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z', ' ' };

	Stage window;
	Button button;
	Scene scene;
	TextField msgfield;
	TextArea output;
	TextField portnum;
	TextField ipaddress;
	TextField username;
	TextField password;
	private TableView tableView = new TableView();
	ObservableList<DataItem> items = FXCollections.<DataItem>observableArrayList();
	int portint;
	int key = 5;
	ServerSocket listener = null;
	Statement s;
	Connection connection = null;

	// ServerSocket listener;
	@Override
	public void start(Stage primaryStage) {

		window = primaryStage;
		window.setTitle("TaskMachine");

		// layout
		Button view = new Button("Retrieve");
		view.setOnAction(e -> view());

		Button send = new Button("Save");
		send.setOnAction(e -> setext());

		output = new TextArea();
		// namefield = new TextField();
		Label msglabel = new Label("  Task:");
		msgfield = new TextField();

		VBox mainwindow = new VBox(20);

		HBox login = new HBox(20);
		Label usernamelabel = new Label("  Username:");
		username = new TextField();

		Label passwordlabel = new Label("Password:");
		password = new TextField();
		String cryptarget = password.getText();

		Button loginbtn = new Button("Login");
		loginbtn.setOnAction(e -> login());

		login.getChildren().addAll(usernamelabel, username, passwordlabel, password, loginbtn);

		// Crate the table view
		TableColumn header1 = new TableColumn<>("Name");
		header1.setCellValueFactory(new PropertyValueFactory<DataItem, String>("name"));

		TableColumn header2 = new TableColumn<>("Task");
		header2.setCellValueFactory(new PropertyValueFactory<DataItem, String>("ip"));

		tableView.setItems(items);
		tableView.getColumns().addAll(header1, header2);

		HBox connectwin = new HBox(20);

		Label ipaddresslabel = new Label("  IPv4 Address");
		ipaddress = new TextField("127.0.0.1");
		// ComboBox<TextField> ipaddrescombo = new ComboBox<TextField>();

		Label portnumlabel = new Label("  Port");
		portnum = new TextField("9090");

		Button connect = new Button("Connect");
		connect.setOnAction(e -> {
			try {
				connect();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		connectwin.getChildren().addAll(ipaddresslabel, ipaddress, portnumlabel, portnum, connect);

		HBox layout = new HBox(20);
		layout.getChildren().addAll(msglabel, msgfield, send, view);
		mainwindow.getChildren().addAll(connectwin, login, tableView, layout);
		Scene scene = new Scene(mainwindow, 550, 520);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void login() {

		try {
			String databaseUser = "blairuser";
			String databaseUserPass = "password!";
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			String url = "jdbc:postgresql://13.210.214.176/test";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			Statement s = connection.createStatement();
			s = connection.createStatement();
			ResultSet rs = s.executeQuery("select * from account;");
			while (rs.next()) {
				Main m = new Main();
				String localpass = password.getText();
				String remotepass = rs.getString("password");
				m.dencryptMessage(remotepass, key);
				if ((rs.getString("name").equals(username.getText()))
						&& m.dencryptMessage(remotepass, key).equals(localpass)) {
					System.out.println("Access Granted");
				} else {
					System.out.println("username/password is incorrect");
				}

				output.setText(rs.getString("name") + " " + rs.getString("password"));

				System.out.println(rs.getString("name") + " " + rs.getString("password"));

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void view() {
		// TODO Auto-generated method stub

		try {
			String databaseUser = "blairuser";
			String databaseUserPass = "password!";
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			String url = "jdbc:postgresql://13.210.214.176/test";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
//			Statement s = connection.createStatement();
			s = connection.createStatement();
			ResultSet rs = s.executeQuery("select * from account;");
			// while (rs.next()) {
			String someobject = rs.getString("name") + " " + rs.getString("password");
			System.out.println(someobject);
			output.setText(someobject);
			// output.setText(rs);
			// System.out.println(rs.getString("name")+" "+rs.getString("message"));
			// rs.getStatement();
			// }

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		PostgresJDBC();
	}

	private void connect() throws IOException {
		// TODO Auto-generated method stub
		// String serverAddress = "127.0.0.1";
		String serverAddress = ipaddress.getText();

		// int port = portnum.getText();
		portint = Integer.parseInt(portnum.getText());

		Socket s = new Socket(serverAddress, portint);

		// Pass data from the client to server
		OutputStream out = s.getOutputStream();
		PrintWriter writer = new PrintWriter(out, true);
		writer.println("task:meetting at the uni");

		BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String answer = input.readLine();
		// JOptionPane.showMessageDialog(null, answer);
		System.out.println("Blairs server: " + answer);
		output.setText("The date is : " + answer);
		items.add(new DataItem(answer, answer));
		// PostgresJDBC();

	}

	private void setext() {
		// TODO Auto-generated method stub

		try {
			String databaseUser = "blairuser";
			String databaseUserPass = "password!";
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			String url = "jdbc:postgresql://13.210.214.176/test";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
//			Statement s = connection.createStatement();
			s = connection.createStatement();
			s.executeQuery("insert into chatmachine (name,message,ip,time) values ('testuser2','" + msgfield.getText()
					+ "','9.9.9.9',CURRENT_TIMESTAMP);");

//			while (rs.next()) {
//				output.setText(rs.getString("name")+":  "+rs.getString("message"));
			// System.out.println(rs.getString("name")+" "+rs.getString("message"));
//			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public void PostgresJDBC() {

		try {
			String databaseUser = "blairuser";
			String databaseUserPass = "password!";
			Class.forName("org.postgresql.Driver");
			Connection connection = null;
			String url = "jdbc:postgresql://13.210.214.176/test";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			Statement s = connection.createStatement();
			s = connection.createStatement();
			ResultSet rs = s.executeQuery("select * from chatmachine;");

			while (rs.next()) {
				items.add(new DataItem(rs.getString("name"), rs.getString("message")));

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	int search(char c) {
		for (int i = 0; i < alphabet.length; i++)
			if (c == alphabet[i]) {
				return i;
			}
		return -1;
	}

	public String encryptMessage(String plainText, int key) {
		String encryptedMsg = "";
		for (int i = 0; i < plainText.length(); i++) {
			char c = plainText.charAt(i);
			int pos = search(c);
			pos = (pos + key) % alphabet.length; // encr pos
			encryptedMsg += alphabet[pos];
		}
		return encryptedMsg;
	}

	public String dencryptMessage(String plainText, int key) {
		String encryptedMsg = "";
		for (int i = 0; i < plainText.length(); i++) {
			char c = plainText.charAt(i);
			int pos = search(c);
			pos = (pos - key) % alphabet.length; // encr pos
			if (pos < 0)
				pos = alphabet.length + pos;
			encryptedMsg += alphabet[pos];
		}
		return encryptedMsg;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
