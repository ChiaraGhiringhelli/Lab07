package it.polito.tdp.poweroutages.model;
import java.util.*;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		model.setANNI_MAX(4);
		model.setORE_MAX(200);
		
		Nerc n = new Nerc(3, "MAAC");
		
		List<PowerOutageEvent> lista = model.getWorstCase(n);
		
		for(PowerOutageEvent p: lista ) {
		System.out.println(p.toString());
		}
		
		
	}
}
