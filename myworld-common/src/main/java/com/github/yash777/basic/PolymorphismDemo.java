package com.github.yash777.basic;

/**
 * Comprehensive Polymorphism Demo
 * Covers:
 * - Static method hiding vs Instance method overriding
 * - Static method call via null object
 * - Method overloading with null (String vs Object)
 * - Ambiguous overloading (Integer vs Long)
 * - Covariant return type in overriding
 * - Access modifier rules in override/overload
 */
// ======================== PARENT CLASS ========================
class Animal {

    // 1. STATIC METHOD — will be HIDDEN (not overridden) in child
    static void staticSound() {
        System.out.println("Animal: static sound (Animal class)");
    }

    // 2. INSTANCE METHOD — will be OVERRIDDEN in child
    void instanceSound() {
        System.out.println("Animal: instance sound");
    }

    // 3. OVERLOADING: null call — Object vs String
    void greet(Object o) {
        System.out.println("greet(Object) called");
    }
    void greet(String s) {
        System.out.println("greet(String) called");
    }

    // 4. OVERLOADING: null call — Integer vs Long (AMBIGUOUS)
    void calculate(Integer i) {
        System.out.println("calculate(Integer) called");
    }
    void calculate(Long l) {
        System.out.println("calculate(Long) called");
    }

    // 5. COVARIANT RETURN TYPE — parent returns Animal
    Animal getInstance() {
        System.out.println("Animal getInstance()");
        return new Animal();
    }

    // 6. ACCESS MODIFIER — protected in parent (child can widen it to public)
    protected void accessDemo() {
        System.out.println("Animal: protected accessDemo()");
    }
}

// ======================== CHILD CLASS ========================
class Dog extends Animal {

    // 1a. STATIC METHOD HIDING — not overriding, just hiding
    //     Resolved at COMPILE TIME based on reference type
    static void staticSound() {
        System.out.println("Dog: static sound (Dog class)");
    }

    // 1b. INSTANCE METHOD OVERRIDING — resolved at RUNTIME based on object type
    @Override
    void instanceSound() {
        System.out.println("Dog: instance sound (overridden)");
    }

    // 5. COVARIANT RETURN TYPE — child can return Dog (subtype of Animal)
    //    Valid override — return type is narrowed down
    @Override
    Dog getInstance() {
        System.out.println("Dog getInstance() — covariant return");
        return new Dog();
    }

    // 6. ACCESS MODIFIER — widening protected → public is ALLOWED in override
    //    Narrowing (public → protected) is NOT allowed — compile error
    @Override
    public void accessDemo() {
        System.out.println("Dog: public accessDemo() — widened access modifier");
    }
}

// ======================== MAIN CLASS ========================
public class PolymorphismDemo {

    public static void main(String[] args) {
        System.out.println("========== 1. Static Hiding vs Instance Overriding ==========");
        Animal ref = new Dog(); // Animal reference, Dog object

        // Static method — resolved at COMPILE TIME → Animal's method called (HIDING)
        ref.staticSound();          // Output: Animal: static sound
        Animal.staticSound();       // Output: Animal: static sound
        Dog.staticSound();          // Output: Dog: static sound

        // Instance method — resolved at RUNTIME → Dog's method called (OVERRIDING)
        ref.instanceSound();        // Output: Dog: instance sound


        System.out.println("\n========== 2. Static Method Call via NULL Object ==========");
        Animal nullAnimal = null;
        // No NullPointerException — static methods belong to CLASS not object
        // Compiler replaces nullAnimal.staticSound() → Animal.staticSound()
        nullAnimal.staticSound();   // Output: Animal: static sound ✅


        System.out.println("\n========== 3. Overloading with NULL — Object vs String ==========");
        Animal a = new Animal();
        // String is more specific than Object → compiler picks greet(String)
        a.greet(null);              // Output: greet(String) called ✅
        a.greet((Object) null);     // Output: greet(Object) called — explicit cast

        System.out.println("\n========== 4. Overloading NULL — Integer vs Long (AMBIGUOUS) ==========");
        // a.calculate(null); ❌ COMPILE ERROR
        // Both calculate(Integer) and calculate(Long) match null
        // Neither Integer nor Long is more specific than the other
        // Fix: explicit cast
        a.calculate((Integer) null); // Output: calculate(Integer) called ✅
        a.calculate((Long) null);    // Output: calculate(Long) called ✅
        System.out.println("Note: a.calculate(null) → COMPILE ERROR (ambiguous method call)");

        System.out.println("\n========== 5. Covariant Return Type ==========");
        // Parent reference — getInstance() returns Animal type at compile time
        Animal animalObj = ref.getInstance();   // Runs Dog's getInstance() at runtime
        // Dog reference — can directly get Dog type
        Dog dog = new Dog();
        Dog dogObj = dog.getInstance();         // Dog's covariant return ✅

        System.out.println("\n========== 6. Access Modifier in Override ==========");
        // Dog widened accessDemo() from protected → public
        // Calling via Animal reference still works ✅
        ref.accessDemo();           // Output: Dog: public accessDemo()

        System.out.println("\n========== Summary of Rules ==========");
        System.out.println("Static method    → HIDDEN   (compile-time, reference type decides)");
        System.out.println("Instance method  → OVERRIDDEN (runtime, object type decides)");
        System.out.println("null + static    → No NPE, resolved by compiler to class");
        System.out.println("null + overload  → Most specific type wins; ambiguous = compile error");
        System.out.println("Covariant return → Child can narrow return type (Dog extends Animal)");
        System.out.println("Access modifier  → Override can WIDEN (protected→public) ✅");
        System.out.println("                   Override can NOT NARROW (public→protected) ❌");
        System.out.println("Overload         → Access modifier can be anything, no restriction ✅");
    }
}