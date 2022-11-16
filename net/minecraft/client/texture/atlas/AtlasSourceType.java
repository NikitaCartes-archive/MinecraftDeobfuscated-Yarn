/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;

@Environment(value=EnvType.CLIENT)
public record AtlasSourceType(Codec<? extends AtlasSource> codec) {
}

