/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A marker interface for Gson serializable pojos; the implementers make
 * sure that they have {@code SerializedName} annotation on all their
 * data fields so serialization works after obfuscation, and save/load of
 * such objects are controlled through another serializer.
 */
@Environment(value=EnvType.CLIENT)
public interface RealmsSerializable {
}

