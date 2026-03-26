package com.github.yash777.lombok;

import lombok.*;

import java.util.Arrays;
import java.util.List;

public class LombokEqualsToStringTest {
	
	public static void main(String[] args) {
		safeComparisons();
	}
	
	// ✅ Safe comparisons using onlyExplicitlyIncluded = true
	static void safeComparisons() {
		System.out.println("== Safe Object Comparisons ==");
		
		Library library1 = Library.builder().id(1L).name("Main Library").build();
		Library library2 = Library.builder().id(2L).name("Branch Library").build();
		
		Publisher pub1 = Publisher.builder().id(100L).name("O'Reilly").library(library1).build();
		Publisher pub2 = Publisher.builder().id(101L).name("Manning").library(library2).build();
		
		Author author = new Author(1L, "John Smith");
		
		Book b1 = Book.builder().id(10L).title("Java Basics").author(author).publisher(pub1).build();
		Book b2 = Book.builder().id(10L).title("Java Basics").author(author).publisher(pub2).build(); // Equal by id & title only
		Book b3 = Book.builder().id(11L).title("Advanced Java").author(author).publisher(pub1).build();
		
		System.out.println("b1 equals b2: " + b1.equals(b2)); // ✅ true (same id, title)
		System.out.println("b1 equals b3: " + b1.equals(b3)); // ❌ false
		
		System.out.println("pub1: " + pub1); // uses @ToString (full)
		System.out.println("library1: " + library1); // uses onlyExplicitlyIncluded
		
		// Bulk compare list
		List<Book> books = Arrays.asList(b1, b2, b3);
		for (int i = 0; i < books.size(); i++) {
			for (int j = i + 1; j < books.size(); j++) {
				System.out.println("Compare books[" + i + "] and books[" + j + "]: " + books.get(i).equals(books.get(j)));
			}
		}
	}
	
}

// === Correct usage of @EqualsAndHashCode(onlyExplicitlyIncluded = true) ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
class Book {
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private String title;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Author author;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Publisher publisher;
}

// === Only @EqualsAndHashCode, but includes full @ToString ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
class Publisher {
	
	@EqualsAndHashCode.Include
	private Long id;
	
	@EqualsAndHashCode.Exclude
	private String name;
	
	private Library library;
}

// === Only @ToString with included fields ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
class Library {
	
	@ToString.Include
	private Long id;
	
	private String name; // not included in toString
}

// === Author class used in Book ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Author {
	private Long id;
	private String name;
}


