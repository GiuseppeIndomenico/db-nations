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

			String sql = "SELECT c.country_id, c.name AS country_name, c2.name AS continent_name, r.name AS region_name "
					+ "FROM countries c " + "JOIN regions r ON c.region_id = r.region_id "
					+ "JOIN continents c2 ON r.continent_id = c2.continent_id " + "WHERE c.name LIKE ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, "%" + ricerca + "%");
				ResultSet resultSet = statement.executeQuery();

				while (resultSet.next()) {
					int countryId = resultSet.getInt("country_id");
					String countryName = resultSet.getString("country_name");
					String continentName = resultSet.getString("continent_name");
					String regionName = resultSet.getString("region_name");
					System.out.println("Country ID: " + countryId);
					System.out.println("Country: " + countryName);
					System.out.println("Continent: " + continentName);
					System.out.println("Region: " + regionName);
					System.out.println("\n---------------------------------\n");
				}

				System.out.print("Inserisci l'ID di una country: ");
				int selectedCountryId = sc.nextInt();

				String selectedCountryName = null;
				String countryNameSql = "SELECT name FROM countries WHERE country_id = ?";

				try (PreparedStatement countryNameStatement = conn.prepareStatement(countryNameSql)) {
					countryNameStatement.setInt(1, selectedCountryId);
					ResultSet countryNameResultSet = countryNameStatement.executeQuery();

					if (countryNameResultSet.next()) {
						selectedCountryName = countryNameResultSet.getString("name");
					}
				}

				if (selectedCountryName != null) {
					System.out.println("Nazione selezionata: " + selectedCountryName);

					// Recupera tutte le lingue parlate in quella country
					String languageSql = "SELECT l.`language` " + "FROM country_languages cl "
							+ "JOIN languages l ON cl.language_id = l.language_id " + "WHERE cl.country_id = ?";

					try (PreparedStatement languageStatement = conn.prepareStatement(languageSql)) {
						languageStatement.setInt(1, selectedCountryId);
						ResultSet languageResultSet = languageStatement.executeQuery();

						System.out.println("Lingue parlate in questa nazione:");
						while (languageResultSet.next()) {
							String language = languageResultSet.getString("language");
							System.out.println(language);
						}
					}

					// Recupera le statistiche più recenti per quella country
					String statsSql = "SELECT cs.gdp AS PIL, cs.population, cs.`year` " + "FROM country_stats cs "
							+ "WHERE cs.country_id = ? " + "ORDER BY cs.`year` DESC " + "LIMIT 1";

					try (PreparedStatement statsStatement = conn.prepareStatement(statsSql)) {
						statsStatement.setInt(1, selectedCountryId);
						ResultSet statsResultSet = statsStatement.executeQuery();

						System.out.println("\nStatistiche più recenti per questa nazione:");
						while (statsResultSet.next()) {
							double gdp = statsResultSet.getDouble("PIL");
							int population = statsResultSet.getInt("population");
							int year = statsResultSet.getInt("year");
							System.out.println("PIL: " + gdp);
							System.out.println("Popolazione: " + population);
							System.out.println("Anno: " + year);
						}
					}
				} else {
					System.out.println("Nazione con ID " + selectedCountryId + " non trovata.");
				}

				sc.close();
			}
		} catch (Exception e) {
			System.out.println("Errore di connessione o query: " + e.getMessage());
		}

		System.out.println("\n----------------------------------\n");
		System.out.println("The end");
	}
}
