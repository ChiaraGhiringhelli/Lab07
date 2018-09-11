package it.polito.tdp.poweroutages.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.NercIdMap;
import it.polito.tdp.poweroutages.model.PowerOutageEvent;
import it.polito.tdp.poweroutages.model.PowerOutageIdMap;
import it.polito.tdp.poweroutages.model.PowerOutages;

public class PowerOutageDAO {

	public List<Nerc> getNercList( NercIdMap map) {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(map.get(n));
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	
	public List<PowerOutages> getListaBlackout(PowerOutageIdMap mappa) {
		
		String sql = "SELECT id, date_event_began, date_event_finished, nerc_id, customers_affected FROM PowerOutages ORDER BY date_event_began ";
		List<PowerOutages> blackList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				
				LocalDateTime dataInizio = res.getTimestamp("date_event_began").toLocalDateTime();
				LocalDateTime dataFine = res.getTimestamp("date_event_finished").toLocalDateTime();
				int idNerc = res.getInt("nerc_id");
				int numeroPersone = res.getInt("customers_affected");
				int id = res.getInt("id");
				PowerOutages p = new PowerOutages( dataInizio, dataFine, idNerc, id, numeroPersone);
		
				blackList.add(mappa.get(p));
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return blackList;
		
	}

	// Prendi tutti i blackout in sostanza
	public List<PowerOutageEvent> getPowerOutageEventList(NercIdMap mappaNerc) {
		
		String sql = "SELECT id, nerc_id, date_event_began, date_event_finished, customers_affected FROM poweroutages";
		List<PowerOutageEvent> powerOutageEventList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = mappaNerc.get(res.getInt("nerc_id"));
				if (n == null) {
					System.err.println("Database is not consistent. Missing corresponding NERC");
				} else {
					PowerOutageEvent poe = new PowerOutageEvent(res.getInt("id"), n,
							res.getTimestamp("date_event_began").toLocalDateTime(),
							res.getTimestamp("date_event_finished").toLocalDateTime(),
							res.getInt("customers_affected"));

					powerOutageEventList.add(poe);
				}
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return powerOutageEventList;
	}

	
}
