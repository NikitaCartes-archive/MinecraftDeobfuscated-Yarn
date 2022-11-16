package net.minecraft.client.texture.atlas;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record AtlasSourceType(Codec<? extends AtlasSource> codec) {
}
