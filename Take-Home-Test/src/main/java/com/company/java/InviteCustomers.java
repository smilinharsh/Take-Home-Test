package com.company.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.company.exception.ApplicationException;

/**
 * Invite any customer within 100km of our Dublin office (GPS coordinates
 * 53.3381985, -6.2592576) for some food and drinks on us. Program that will
 * read the full list of customers and output the names and user ids of matching
 * customers (within 100km), sorted by user id (ascending).
 */
public class InviteCustomers {

	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String USER_ID = "user_id";
	private static final String NAME = "name";
	private static final String UTF_8 = "UTF-8";

	private static final double EARTH_RADIUS = 6371.01;
	private static final double BASE_LATITUDE = 53.3381985;
	private static final double BASE_LONGITUDE = -6.2592576;

	private static final String OUTPUT_FILE = "resources/output.txt";

	/**
	 * Retrieves the customer distance from the specified location, based on the
	 * latitude and longitude coordinates
	 *
	 * @param latitude
	 *            - coordinates
	 * @param longitude
	 *            - coordinates
	 * @return customer distance
	 */
	private static double getCustomerDistance(double latitude, double longitude) {
		double baseLat = degreesToRadians(BASE_LATITUDE);
		double userLat = degreesToRadians(latitude);
		double baseLon = degreesToRadians(BASE_LONGITUDE);
		double userLon = degreesToRadians(longitude);

		return Math.acos(Math.sin(baseLat) * Math.sin(userLat)
				+ Math.cos(baseLat) * Math.cos(userLat) * Math.cos(baseLon - userLon)) * EARTH_RADIUS;
	}

	/**
	 * Convert degree to radians
	 *
	 * @param latOrLon
	 * @return
	 */
	private static double degreesToRadians(double latOrLon) {
		return latOrLon * (Math.PI / 180);
	}

	/**
	 * This method reads the JSON file and retrieves the customers within the
	 * specified range.
	 *
	 * @param fileLocation
	 *            - Path for the JSON file
	 * @throws Exception
	 */
	public static void readJsonFile(File fileLocation) throws ApplicationException {
		try {
			Map<Long, String> finalResult = new TreeMap<Long, String>();

			@SuppressWarnings("resource")
			Scanner scn = new Scanner(fileLocation, UTF_8);
			while (scn.hasNext()) {
				JSONObject obj = (JSONObject) new JSONParser().parse(scn.nextLine());
				if (getCustomerDistance(Double.valueOf((String) obj.get(LATITUDE)),
						Double.valueOf((String) obj.get(LONGITUDE))) <= 100) {
					finalResult.put((Long) obj.get(USER_ID), (String) obj.get(NAME));
				}
			}
			FileWriter fstream = new FileWriter(OUTPUT_FILE);
			BufferedWriter out = new BufferedWriter(fstream);
			for (Map.Entry<Long, String> each : finalResult.entrySet()) {
				out.write(each.getKey() + "\t" + each.getValue() + "\n");
				out.flush();
			}
			fstream.close();
			out.close();
		} catch (FileNotFoundException e) {
			throw new ApplicationException("File does not exist");
		} catch (ParseException e) {
			throw new ApplicationException("Invalid JSON format");
		} catch (IOException e) {
			throw new ApplicationException("Not able to write in output file");
		}
	}

}