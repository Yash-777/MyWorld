package com.github.yash777.lombok;

import lombok.*;

import java.util.Arrays;
import java.util.List;

public class LombokRelationsDemo {
	
	public static void main(String[] args) {
		
		// Create objects
		LibraryDemo library = LibraryDemo.builder().id(1L).name("Central Library").build();
		PublisherDemo publisher = PublisherDemo.builder().id(10L).name("O'Reilly").library(library).build();
		PublisherDemo publisher2 = PublisherDemo.builder().id(10L).name("Reilly").build();
		BookDemo book = BookDemo.builder().id(100L).title("Java Deep Dive").build();
		AuthorDemo author = AuthorDemo.builder().id(5L).name("Jane Smith").books(Arrays.asList(book)).build();
		
		// Set backward references
		book.setAuthor(author);
		book.setPublisher(publisher);
		
		// Print objects
		System.out.println("== Author ==");
		System.out.println(author);
		
		System.out.println("\n== Book ==");
		System.out.println(book);
		
		System.out.println("\n== Publisher ==");
		System.out.println(publisher);
		
		System.out.println("\n== Library ==");
		System.out.println(library);
		
		// Equality checks
		AuthorDemo author2 = AuthorDemo.builder().id(5L).name("Jane Smith").build();
		System.out.println("\nAuthor equals author2: " + author.equals(author2)); // true
		
		BookDemo anotherBook = BookDemo.builder().id(100L).title("Java Deep Dive").build();
		System.out.println("Book equals anotherBook: " + book.equals(anotherBook)); // true
		
		System.out.println("\nPublisher equals Publish2: " + publisher.equals(publisher2)); // true
	}
}

// === FULL Combination: @EqualsAndHashCode + @ToString ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
class AuthorDemo {
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private String name;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<BookDemo> books;
}

// === FULL Combination ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
class BookDemo {
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@EqualsAndHashCode.Include
	@ToString.Include
	private String title;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AuthorDemo author;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private PublisherDemo publisher;
}

// === Only @EqualsAndHashCode ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
class PublisherDemo {
	
	@EqualsAndHashCode.Include
	private Long id;
	
	@EqualsAndHashCode.Exclude
	private String name;
	
	private LibraryDemo library;
}

// === Only @ToString ===
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
class LibraryDemo {
	
	@ToString.Include
	private Long id;
	
	//@ToString.Exclude
	private String name;
}
