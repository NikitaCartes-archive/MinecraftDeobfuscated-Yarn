/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.obfuscate.DontObfuscate;

@Environment(value=EnvType.CLIENT)
public class ClientBrandRetriever {
    public static final String field_33204 = "vanilla";

    @DontObfuscate
    public static String getClientModName() {
        return field_33204;
    }
}

