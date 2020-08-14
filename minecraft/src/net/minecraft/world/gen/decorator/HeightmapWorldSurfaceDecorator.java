package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public class HeightmapWorldSurfaceDecorator extends HeightmapDecorator<NopeDecoratorConfig> {
	public HeightmapWorldSurfaceDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected Heightmap.Type getHeightmapType(NopeDecoratorConfig nopeDecoratorConfig) {
		return Heightmap.Type.WORLD_SURFACE_WG;
	}
}
