package controller;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import SqlServer.QueryDataAccessObject;
import cards.Card;
import cards.Deck;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import reminder_streak_checker.DailyCheck;
import javafx.scene.control.ListCell;

public class ManageDecksController implements Initializable {

	@FXML
	private Button newDeckButton, deleteButton, viewButton, testButton, importButton, exportButton;
	@FXML
	private ListView<Deck> deckListView;
	@FXML
	private Label deckListLabel, streakLabel;

	static Deck selectedDeck;

	private void setDisableActionButton(boolean bool) {
		viewButton.setDisable(bool);
		deleteButton.setDisable(bool);
		//newDeckButton.setDisable(bool);
		testButton.setDisable(bool);
		
		importButton.setDisable(bool);
		exportButton.setDisable(bool);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		ObservableList<Deck> deckList = QueryDataAccessObject.getDeckList();
		deckListView.setItems(deckList);
		deckListView.setCellFactory(param -> new ListCell<Deck>() {
			@Override
			protected void updateItem(Deck item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null)
					setText(null);
				else
					setText(item.getName());
			}
		});
		setDisableActionButton(true);
		DailyCheck dcheck = new DailyCheck();
		streakLabel.setText("Bạn đã điểm danh "+ dcheck.getDayStreaks()+ " ngày liên tiếp!");
		deckListLabel.setStyle("-fx-font-family: 'Montserrat ExtraBold'; -fx-font-size: 24");;
	}

	@FXML
	void setSelectedDeck(MouseEvent event) {
		if (event.getClickCount() == 1) // Checking click
		{
			System.out.print("Clicked on deckListView\n");
			selectedDeck = deckListView.getSelectionModel().getSelectedItem();
			if (selectedDeck != null)
			{
				System.out.println("selectedDeck info: id = "+ selectedDeck.getId() + ", name = " +selectedDeck.getName());
				setDisableActionButton(false);
			}
			else
				setDisableActionButton(true);
		}
	}

	@FXML
	void createNewDeck() throws IOException {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/new_deck.fxml"));
		Parent root = loader.load();
		Stage newDeckDialog = new Stage();
		((Initializable) loader.getController()).initialize(null, null);
		newDeckDialog.setScene(new Scene(root));
		newDeckDialog.setResizable(false);
		newDeckDialog.setTitle("Super Flashcard - Nhập tên bộ thẻ mới");
		newDeckDialog.showAndWait();
		
		this.initialize(null, null);
	}

	@FXML
	void deleteDeck() {
		QueryDataAccessObject.deleteDeck(selectedDeck);
		this.initialize(null, null);
	}

	@FXML
	void viewDeck() throws IOException {
		Stage currentStage = (Stage) viewButton.getScene().getWindow();
		Parent root = FXMLLoader.load(this.getClass().getResource("/managecards2.fxml"));
		double w = currentStage.getWidth(), h = currentStage.getHeight();
		currentStage.setScene(new Scene(root));
		currentStage.setHeight(h); currentStage.setWidth(w);
		currentStage.show();
	}
	
	@FXML void startTest() throws IOException {
		Stage currentStage = (Stage) viewButton.getScene().getWindow();
		Parent root = FXMLLoader.load(this.getClass().getResource("/test.fxml"));
		double w = currentStage.getWidth(), h = currentStage.getHeight();
		currentStage.setScene(new Scene(root));
		currentStage.setHeight(h); currentStage.setWidth(w);
		currentStage.show();
	}
	
	@FXML void goImport() {
		ObservableList<Card> list = FXCollections.observableArrayList();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Nhập từ file...");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"),
				new FileChooser.ExtensionFilter("All files", "*"));
		File file = fileChooser.showOpenDialog((Stage) importButton.getScene().getWindow());
		if (file != null)
		{
			list = importExport.ImportExport.Import(file.getAbsolutePath());
			for (Card i : list) {
				QueryDataAccessObject.mergeCard(i, selectedDeck);
			}
		}
		
	}
	
	@FXML void goExport() {
		ObservableList<Card> list = QueryDataAccessObject.getCardsFromDeck(selectedDeck);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Xuất thành file...");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"),
				new FileChooser.ExtensionFilter("All files", "*"));
		File file = fileChooser.showSaveDialog((Stage) exportButton.getScene().getWindow());
		if (file != null)
		{
			importExport.ImportExport.Export(list, file.getAbsolutePath());
		}
		
	}
}
