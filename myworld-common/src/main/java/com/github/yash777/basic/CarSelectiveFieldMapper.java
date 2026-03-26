package com.github.yash777.basic;

import java.util.*;

import lombok.ToString;

/**
 * A sample class demonstrating selective field mapping using a switch-based approach.
 * This simulates a use case where a Car has several spare parts and we only want to 
 * load/set specific fields based on what's requested.
 */
public class CarSelectiveFieldMapper {
	
	@lombok.Getter @lombok.Setter @ToString//(callSuper = true, includeFieldNames = true)
	public static class Car {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String ENGINE = "engine";
		public static final String TYRES = "tyres";
		public static final String BRAKES = "brakes";
		public static final String PRICE = "price";
		
		private String id;
		private String name;
		private SparePart engine;
		private SparePart tyres;
		private SparePart brakes;
		private double price;
		
		/**
		 * Builds a Car object by selectively setting fields based on the requested field names.
		 *
		 * @param source A simulated data source (could be from DB, API, etc.)
		 * @param fields The fields to populate
		 * @return A Car object with only requested fields populated
		 */
		public static Car fromSource(Source source, Collection<String> fields) {
			Car car = new Car();
			
			for (String field : fields) {
				switch (field) {
				case ID -> car.setId(source.getString("id"));
				case NAME -> car.setName(source.getString("name"));
				case ENGINE -> car.setEngine(source.getPart("engine"));
				case TYRES -> car.setTyres(source.getPart("tyres"));
				case BRAKES -> car.setBrakes(source.getPart("brakes"));
				case PRICE -> car.setPrice(source.getDouble("price"));
				default -> System.out.println("Unknown field: " + field);
				}
			}
			
			return car;
		}
	}
	
	/**
	 * Represents a car spare part.
	 */
	@lombok.Getter @lombok.Setter @ToString
	//@ToString(callSuper = true, includeFieldNames = true)
	public static class SparePart {
		private final String partId;
		private final double cost;
		
		public SparePart(String partId, double cost) {
			this.partId = partId;
			this.cost = cost;
		}
	}
	
	/**
	 * A fake data source simulating retrieval of field values (e.g., from a database or API).
	 */
	public static class Source {
		private final Map<String, Object> data;
		
		public Source(Map<String, Object> data) {
			this.data = data;
		}
		
		public String getString(String key) {
			return (String) data.getOrDefault(key, null);
		}
		
		public double getDouble(String key) {
			Object val = data.getOrDefault(key, 0.0);
			return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
		}
		
		public SparePart getPart(String key) {
			Map<String, Object> partData = (Map<String, Object>) data.get(key);
			if (partData != null) {
				return new SparePart((String) partData.get("partId"),
						((Number) partData.get("cost")).doubleValue());
			}
			return null;
		}
	}
	
	public static void main(String[] args) {
		// Example input
		Map<String, Object> inputData = new HashMap<>();
		inputData.put("id", "CAR123");
		inputData.put("name", "Tesla Model S");
		inputData.put("price", 79999.99);
		
		inputData.put("engine", Map.of("partId", "ENG456", "cost", 12000));
		inputData.put("tyres", Map.of("partId", "TYR789", "cost", 2000));
		inputData.put("brakes", Map.of("partId", "BRK321", "cost", 1500));
		
		Source source = new Source(inputData);
		
		// Example usage: only want ID, Name, and Tyres
		Car selectedCarFields = Car.fromSource(source, List.of(Car.ID, Car.NAME, Car.TYRES));
		System.out.println("Selected Car Data: " + selectedCarFields);
		System.out.println("Selected Car Data: " + JsonUtil.toPrettyJson(selectedCarFields));
		
		// Example usage: only want ID, Name, and Tyres
		Car selectedCarFields2 = Car.fromSource(source, List.of(Car.NAME));
		System.out.println("Selected Car Data: " + selectedCarFields2);
		System.out.println("Selected Car Data: " + JsonUtil.toPrettyJson(selectedCarFields2));
	}
}
