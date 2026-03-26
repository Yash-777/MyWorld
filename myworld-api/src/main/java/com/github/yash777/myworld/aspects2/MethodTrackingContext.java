package com.github.yash777.myworld.aspects2;

public class MethodTrackingContext {
    private static final ThreadLocal<Boolean> isInController = ThreadLocal.withInitial(() -> false);

    public static void enterController() {
        isInController.set(true);
    }

    public static void exitController() {
        isInController.remove(); // avoid memory leak
    }

    public static boolean isInController() {
        return isInController.get();
    }
}
