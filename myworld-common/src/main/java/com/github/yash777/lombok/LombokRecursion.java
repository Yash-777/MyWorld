package com.github.yash777.lombok;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class LombokRecursion {

	public static void main(String[] args) {
		triggerStackOverflow(); // Comment out to avoid crashing
	}
	
	// ❌ Demo of recursive equals that leads to StackOverflowError
		static void triggerStackOverflow() {
			System.out.println("\n== Trigger StackOverflowError ==");
			
			// RecursiveTest - Recursive reference - bad design
			RecursiveNode nodeA = new RecursiveNode("A");
			RecursiveNode nodeB = new RecursiveNode("B");
			
			nodeA.setNext(nodeB);
			System.out.println("nodeA: " + nodeA); // ✔ Safe
			System.out.println("nodeA equals nodeB: " + nodeA.equals(nodeB)); // false
			
			nodeB.setNext(nodeA); // Circular reference
			System.out.println("nodeB: " + nodeB); // Creates a cycle
			System.out.println("nodeA equals nodeB: " + nodeA.equals(nodeB)); // false
			
			System.out.println("\n== Trigger StackOverflowError ==");
			// RecursiveTest - Recursive reference - bad design
			RecursiveNode2 nodeA2 = new RecursiveNode2("A");
			RecursiveNode2 nodeB2 = new RecursiveNode2("B");
			
			nodeA2.setNext(nodeB2);
			System.out.println("nodeA: " + nodeA2); // ✔ Safe
			System.out.println("nodeA equals nodeB: " + nodeA2.equals(nodeB2)); // false
			
			nodeB2.setNext(nodeA2); // Circular reference
			System.out.println("nodeB: " + nodeB2); // Creates a cycle
			System.out.println("nodeA equals nodeB: " + nodeA2.equals(nodeB2)); // false
		}
}

//=== ❌ BAD example: causes StackOverflowError ===
@Getter @Setter
//@NoArgsConstructor
@RequiredArgsConstructor // Generates constructor with required (final or @NonNull) fields
@AllArgsConstructor
	//System.out.println("nodeB: " + nodeB); // Creates a cycle
	//Exception in thread "main" java.lang.StackOverflowError
	//at com.github.yash777.lombok.RecursiveNode.hashCode(LombokEqualsToStringTest.java:154)
@EqualsAndHashCode // <-- ❌ Includes all fields by default – dangerous!/unsafe!
class RecursiveNode2 {
	@NonNull
	private String name;
	private RecursiveNode2 next;
}

@Getter @Setter
@RequiredArgsConstructor // Generates constructor with required (final or @NonNull) fields
@ToString  (onlyExplicitlyIncluded = true)
@EqualsAndHashCode // (onlyExplicitlyIncluded = true)
class RecursiveNode {
	
	/* On comment :
	 * @ToString  // (onlyExplicitlyIncluded = true)
	 * // @ToString.Include @EqualsAndHashCode.Include
	Exception in thread "main" java.lang.StackOverflowError
	at java.base/java.lang.AbstractStringBuilder.putStringAt(AbstractStringBuilder.java:1720)
	at java.base/java.lang.AbstractStringBuilder.putStringAt(AbstractStringBuilder.java:1724)
	at java.base/java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:583)
	at java.base/java.lang.StringBuilder.append(StringBuilder.java:179)
	at java.base/java.lang.StringBuilder.append(StringBuilder.java:91)
	at java.base/java.lang.AbstractStringBuilder.<init>(AbstractStringBuilder.java:112)
	at java.base/java.lang.StringBuilder.<init>(StringBuilder.java:131)
	at com.github.yash777.lombok.RecursiveNode.toString(LombokEqualsToStringTest.java:165)
	 */
	@NonNull
	@ToString.Include @EqualsAndHashCode.Include
	private String name;
	
	//@ToString.Exclude @EqualsAndHashCode.Exclude
	private RecursiveNode next;
}