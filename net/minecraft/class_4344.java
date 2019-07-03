/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.net.Proxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class class_4344 {
    private static Proxy field_19592;

    public static Proxy method_21034() {
        return field_19592;
    }

    public static void method_21035(Proxy proxy) {
        if (field_19592 == null) {
            field_19592 = proxy;
        }
    }
}

