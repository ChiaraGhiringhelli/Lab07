package it.polito.tdp.poweroutages;

import java.util.*;
import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.poweroutages.model.Model;
import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutageEvent;
import it.polito.tdp.poweroutages.model.PowerOutages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PowerOutagesController {

	Model model; 
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Nerc> menuNerc;

    @FXML
    private TextField txtYears;

    @FXML
    private TextField txtHours;

    @FXML
    private Button btnWCA;

    @FXML
    private TextArea txtResult;

    @FXML
    void doAnalisi(ActionEvent event) {
    	
		try {
			
			Nerc selectedNerc = menuNerc.getValue();
			if (selectedNerc == null) {
				txtResult.setText("Select a NERC (area identifier)");
				return;
			}

			int maxY = Integer.parseInt(txtYears.getText());
			int yearListSize = model.getYearList().size();
			
			if (maxY <= 0 || maxY > yearListSize) {
				txtResult.setText("Select a number of years in range [1, " + yearListSize + "]");
				return;
			}

			int maxH = Integer.parseInt(txtHours.getText());
			if (maxH <= 0) {
				txtResult.setText("Select a number of hours greater than 0");
				return;
			}

			txtResult.setText( String.format("Computing the worst case analysis... for %d hours and %d years", maxH, maxY));
			model.setANNI_MAX(maxY);
			model.setORE_MAX(maxH);
			System.out.println("Sono qui");
			List<PowerOutageEvent> worstCase = model.getWorstCase(selectedNerc);

			System.out.println("Sono quiiiii");
			txtResult.clear();
			txtResult.appendText("Tot people affected: " + model.contaPersone(worstCase) + "\n");
			txtResult.appendText("Tot hours of outage: " + model.contaOre(worstCase)+ "\n");

			for (PowerOutageEvent ee : worstCase) {
				txtResult.appendText(String.format("%d %s %s %d %d", ee.getYear(), ee.getOutageStart(),
						ee.getOutageEnd(), ee.getOutageDuration(), ee.getAffectedPeople()));
				txtResult.appendText("\n");
			}

		} catch (NumberFormatException e) {
			txtResult.setText("Insert a valid number of years and of hours");
		}
    }

    @FXML
    void initialize() {
        assert menuNerc != null : "fx:id=\"menuNerc\" was not injected: check your FXML file 'PowerOutages.fxml'.";
        assert txtYears != null : "fx:id=\"txtYears\" was not injected: check your FXML file 'PowerOutages.fxml'.";
        assert txtHours != null : "fx:id=\"txtHours\" was not injected: check your FXML file 'PowerOutages.fxml'.";
        assert btnWCA != null : "fx:id=\"btnWCA\" was not injected: check your FXML file 'PowerOutages.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'PowerOutages.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		
		menuNerc.getItems().addAll(model.getNercList());
	}
    
}
