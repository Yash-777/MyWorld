package com.github.yash777.basic;

/**
 * Demonstrates the difference between {@code instanceof} and {@code Class.isAssignableFrom(...)} in Java.
 *
 * <p><strong>Explanation & When to Use Which</strong></p>
 *
 * <p><strong>instanceof</strong></p>
 * <ul>
 *     <li>Simple, concise, safe with {@code null} (if object is {@code null}, {@code instanceof} returns {@code false}).</li>
 *     <li>But you must know the type (on the right side) at compile time.</li>
 *     <li>You cannot write {@code if (obj instanceof cls)} where {@code cls} is a {@code Class<?>} variable.</li>
 *     <li>Best used when you are working with known types.</li>
 * </ul>
 *
 * <p><strong>Class.isAssignableFrom(...)</strong></p>
 * <ul>
 *     <li>More dynamic. You call it on a {@code Class<?>} and pass another {@code Class<?>} argument, so you can decide types at runtime.</li>
 *     <li>{@code A.class.isAssignableFrom(B.class)} returns {@code true} if {@code B} is the same as {@code A}, or a subclass
 *     (or implements {@code A}, if {@code A} is an interface).</li>
 *     <li>But if you do {@code someClass.isAssignableFrom(obj.getClass())}, and {@code obj} is {@code null},
 *     that will throw a {@code NullPointerException} if you don’t guard it.</li>
 *     <li>Also, {@code cls.isInstance(obj)} is kind of the dynamic equivalent of {@code instanceof} (returns {@code false} for {@code null}).</li>
 * </ul>
 * 
 * @author 🔐 Yashwanth
 */
public class TypeCheckDemo {
	
	interface Animal { void speak(); }
	
	static class Dog implements Animal {
		@Override public void speak() { System.out.println("Woof"); }
	}
	
	static class Cat implements Animal {
		@Override public void speak() { System.out.println("Meow"); }
	}
	
	static class Bulldog extends Dog {
		@Override public void speak() { System.out.println("Gruff"); }
	}
	
	public static void main(String[] args) {
		Object obj1 = new Dog();
		Object obj2 = new Cat();
		Object obj3 = new Bulldog();
		
		// Using instanceof — you must know the type at compile time
		if (obj1 instanceof Dog) {
			System.out.println("obj1 is a Dog (instanceof)");
		}
		if (obj2 instanceof Animal) {
			System.out.println("obj2 is an Animal (instanceof)");
		}
		if (obj3 instanceof Dog) {
			System.out.println("obj3 is a Dog (instanceof) — true because Bulldog extends Dog");
		}
		
		// Null check with instanceof is safe
		Object objNull = null;
		if (objNull instanceof Dog) {
			System.out.println("this won’t print");
		} else {
			System.out.println("objNull instanceof Dog is false");
		}
		
		// Using Class.isAssignableFrom — useful when types are dynamic
		Class<?> dogClass = Dog.class;
		Class<?> bulldogClass = Bulldog.class;
		Class<?> catClass = Cat.class;
		
		// Check class hierarchy: can a Dog reference accept a Bulldog?
		if (dogClass.isAssignableFrom(bulldogClass)) {
			System.out.println("Dog.class.isAssignableFrom(Bulldog.class) => true");
		}
		if (dogClass.isAssignableFrom(catClass)) {
			System.out.println("Dog.class.isAssignableFrom(Cat.class) => true??");
		} else {
			System.out.println("Dog.class.isAssignableFrom(Cat.class) => false");
		}
		
		// Checking actual object’s class via isAssignableFrom
		if (Animal.class.isAssignableFrom(obj3.getClass())) {
			System.out.println("obj3's class is assignment-compatible with Animal");
		}
		
		// A more general dynamic version (when you don't know the type at compile time)
		testType(obj1, Dog.class);
		testType(obj2, Dog.class);
		testType(obj3, Animal.class);
	}
	
	/**
	 * Checks at runtime whether the passed object is an instance of the given class,
	 * using both {@code isAssignableFrom()} and {@code isInstance()}.
	 *
	 * @param obj   object to check
	 * @param cls   class to check against
	 */
	public static void testType(Object obj, Class<?> cls) {
		System.out.println("----");
		System.out.println("Testing obj = " + obj + " against " + cls.getSimpleName());
		
		if (obj != null && cls.isAssignableFrom(obj.getClass())) {
			System.out.println("  via isAssignableFrom: YES, obj is instance of " + cls.getSimpleName());
		} else {
			System.out.println("  via isAssignableFrom: NO");
		}
		
		// Alternative dynamic instanceof
		if (cls.isInstance(obj)) {
			System.out.println("  via cls.isInstance(obj): YES");
		} else {
			System.out.println("  via cls.isInstance(obj): NO");
		}
	}
}