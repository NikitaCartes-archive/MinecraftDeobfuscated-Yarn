package net.minecraft.client.texture.atlas;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record AtlasSourceType(MapCodec<? extends AtlasSource> codec) {
}
