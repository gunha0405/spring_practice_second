package com.example.util;

public final class TenantContext {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(String tenantId) { HOLDER.set(tenantId); }
    public static String get() { return HOLDER.get(); }
    public static void clear() { HOLDER.remove(); }
}

