package it.polito.tdp.poweroutages.model;

import java.util.List;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Model {
	
	private List<Nerc> listaNerc;
	PowerOutageDAO podao;
	
	private List<PowerOutageEvent> soluzione;
	private List<PowerOutages> listaB;

	// tutti gli eventi
	private List<PowerOutageEvent> eventList;
	// prendo solo i peggiori 
	private List<PowerOutageEvent> eventListFiltered;
	
	private PowerOutageIdMap mappa;
	private NercIdMap mappaNerc;
	private int ORE_MAX;
	private int ANNI_MAX;
	private int maxPeople = 0;
	
	public Model() {
		
		podao = new PowerOutageDAO();
		mappa = new PowerOutageIdMap();
		mappaNerc = new NercIdMap();
		
		listaNerc = podao.getNercList(mappaNerc);
		listaB = podao.getListaBlackout(mappa);
		
		eventList = podao.getPowerOutageEventList(mappaNerc);
		//System.out.println(eventList);
		
	}
	
	public int getORE_MAX() {
		return ORE_MAX;
	}

	public void setORE_MAX(int Y) {
		ORE_MAX = Y;
	}

	public int getANNI_MAX() {
		return ANNI_MAX;
	}

	public void setANNI_MAX(int X) {
		ANNI_MAX = X;
	}

	public List<Nerc> getNercList() {
		return listaNerc;
	}
	
	
	public List<PowerOutageEvent> getSoluzione() {
		return soluzione;
	}
	
	// Add the nerc parameter
		public List<PowerOutageEvent> getWorstCase(Nerc nerc) {

			// Initialization phase
			soluzione = new ArrayList<>();
			maxPeople = 0;

			// Create new eventListFiltered = che sono blackout ma che rispettano X e Y 
			// inseriti dall'utente
			
			eventListFiltered = new ArrayList<>();
			
			for (PowerOutageEvent event : eventList ) {
				if (event.getNerc().equals(nerc)) {
					eventListFiltered.add(event);
				}
			}
			
			eventListFiltered.sort(new Comparator<PowerOutageEvent>() {
			
				public int compare(PowerOutageEvent o1, PowerOutageEvent o2) {
					return o1.getOutageStart().compareTo(o2.getOutageStart());
				}

			});

			System.out.println("Event list filtered: " + eventListFiltered);
			
			List<PowerOutageEvent> prova = new ArrayList<PowerOutageEvent>();
			
			recursive(prova, ANNI_MAX, ORE_MAX);

			return soluzione;
		}

	private void recursive( List<PowerOutageEvent> listaParziale, int maxYear, int maxHours) {
		
		// Condizione terminazione implicita nel filtro 
		// ma devo controllare se la soluzione è la migliore di quella di prima
		// cioè se ha più persone colpite rispetto alla soluzione prima 
		
		if( this.contaPersone(listaParziale) > this.contaPersone(soluzione)) {
			
			maxPeople = this.contaPersone(listaParziale);
			soluzione = new ArrayList<PowerOutageEvent>(listaParziale);
			System.out.println("Sono qui1");
			//return;
		}
		else {
			
			// controllo su tutti i blackout che ci sono nel DB ma senza duplicati ( mappa )
			// E QUA CONTROLLI SE l'aggiunta è valida
			for( PowerOutageEvent prova : eventListFiltered) {
				
				if (this.durataTotale(listaParziale, maxYear) && this.contaOre(listaParziale) > ORE_MAX) {
					System.out.println("Sono qui3");
					recursive(listaParziale, maxYear, maxHours);
				}

				listaParziale.remove(prova);

			}	
		}
	}

	/*private boolean aggiuntaValida(PowerOutageEvent prova, List<PowerOutageEvent> listaParziale) {
		
		if( listaParziale.size() == 0 || listaParziale.size() == 1)
			return true;
		
		
		for(PowerOutageEvent p: listaParziale) {
			
		if( p.equals(prova) == true) {
			// perchè è già presente nella lista
				return false;
		}
		// se non è presente e le condizioni sono soddisfatte allora torna true e lo aggiungi
		else if( p.equals(prova) == false && prova.getOutageDuration() <= ORE_MAX) {
			
			// controllo gli anni
			if(this.durataTotale(listaParziale, ANNI_MAX)) {
				
				listaParziale.add(prova);
				
				// devo controllare che tutte le ore sommate della mia lista
				// parziale sia minore del valore che ho messo di input --> listaParziale
				
				if( this.contaOre(listaParziale) <= ORE_MAX) {
					// lo levo comunque tanto lo aggiungo nel recursive
					listaParziale.remove(prova);
					return true;		
				} else {
					listaParziale.remove(prova);
					return false;
				}	
			}
		}	
		}
		
		return false;
	}
*/
	public int contaOre(List<PowerOutageEvent> listaParziale) {
		
		int ore = 0;
		
		for( PowerOutageEvent p : listaParziale) {
			ore += p.getOutageDuration();
		}
		
		return ore;
	}
	
	private boolean durataTotale(List<PowerOutageEvent> parziale, int anni) {
		
		if (parziale.size() >=2 ) {
			
			int y1 = parziale.get(0).getYear();
			int y2 = parziale.get(parziale.size() - 1).getYear();
			
			if ( (y2 - y1 + 1) > anni) {
				return false;
			}
		}
		return true;
	}

	public int contaPersone(List<PowerOutageEvent> listaParziale) {
		
		// conto tutte le persone affette dal blackout
		
		int persone = 0;
		
		for( PowerOutageEvent p: listaParziale) {
			persone += p.getAffectedPeople();
		}
		
		return persone;
	}

	public List<Integer> getYearList() {
		
		Set<Integer> yearSet = new HashSet<Integer>();
		for (PowerOutageEvent event : eventList) {
			yearSet.add(event.getYear());
		}
		List<Integer> yearList = new ArrayList<Integer>(yearSet);
		yearList.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
			
		});
		return yearList;
	}
}
