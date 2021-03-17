/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class class_5951 {
    private final String field_29557;

    public class_5951(String string) {
        this.field_29557 = string;
    }

    public String method_34704() {
        return this.field_29557;
    }

    public String toString() {
        return "Metric{name='" + this.field_29557 + '\'' + '}';
    }
}

