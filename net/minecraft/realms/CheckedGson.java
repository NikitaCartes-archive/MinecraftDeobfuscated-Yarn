/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

/**
 * Checks so that only intended pojos are passed to the GSON (handles
 * serialization after obfuscation).
 */
@Environment(value=EnvType.CLIENT)
public class CheckedGson {
    private final Gson gson = new Gson();

    public String toJson(RealmsSerializable serializable) {
        return this.gson.toJson(serializable);
    }

    public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
        return (T)((RealmsSerializable)this.gson.fromJson(json, type));
    }
}

