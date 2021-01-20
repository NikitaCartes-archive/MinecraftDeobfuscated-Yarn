/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;

/**
 * Checks so that only intended pojos are passed to the GSON (handles
 * serialization after obfuscation).
 */
@Environment(value=EnvType.CLIENT)
public class CheckedGson {
    private final Gson GSON = new Gson();

    public String toJson(RealmsSerializable serializable) {
        return this.GSON.toJson(serializable);
    }

    @Nullable
    public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
        return (T)((RealmsSerializable)this.GSON.fromJson(json, type));
    }
}

