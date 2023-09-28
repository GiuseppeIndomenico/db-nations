package org.java.nations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		final String url = "jdbc:mysql://localhost:3306/db_nations";
		final String user = "root";
		final String password = "root";

		try (Connection conn = DriverManager.getConnection(url, user, password)) {

			System.out.println("Connessione stabilita correttamente");

			Scanner sc = new Scanner(System.in);
			System.out.print("Inserisci una stringa di ricerca: ");
			String ricerca = sc.nextLine();

			sc.close();
			String sql = "SELECT c.name, c2.name AS continent_name, r.name AS region_name " + "FROM countries c "
					+ "JOIN regions r ON c.region_id = r.region_id "
					+ "JOIN continents c2 ON r.continent_id = c2.continent_id "
					+ "WHERE c.name LIKE ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
		
				
				statement.setString(1, "%" + ricerca + "%");
	
				ResultSet resultSet = statement.executeQuery();

				while (resultSet.next()) {
					String countryName = resultSet.getString("name");
					String continentName = resultSet.getString("continent_name");
					String regionName = resultSet.getString("region_name");
					System.out.println(
							"Country: " + countryName + "\nContinent: " + continentName + "\nRegion: " + regionName);
					System.out.println("\n---------------------------------\n");
				}
			}
		} catch (Exception e) {
			System.out.println("Errore di connessione o query: " + e.getMessage());
		}

		System.out.println("\n----------------------------------\n");
		System.out.println("The end");

	}
}
