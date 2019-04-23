/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

public enum ResourceType {
    CLIENT_RESOURCES("assets"),
    SERVER_DATA("data");

    private final String name;

    private ResourceType(String string2) {
        this.name = string2;
    }

    public String getName() {
        return this.name;
    }
}

