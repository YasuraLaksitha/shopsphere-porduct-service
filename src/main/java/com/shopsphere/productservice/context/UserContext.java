package com.shopsphere.productservice.context;

public class UserContext {

    private static final ThreadLocal<String> currectUserContext = new ThreadLocal<>();

    public static void set(final String userId) {
        currectUserContext.set(userId);
    }

    public static String get() {
        return currectUserContext.get();
    }

    public static void clear() {
        currectUserContext.remove();
    }
}
