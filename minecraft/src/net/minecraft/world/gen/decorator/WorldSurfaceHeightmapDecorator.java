package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public class WorldSurfaceHeightmapDecorator extends HeightmapDecorator<NopeDecoratorConfig> {
	public WorldSurfaceHeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected Heightmap.Type getHeightmapType(NopeDecoratorConfig nopeDecoratorConfig) {
		return Heightmap.Type.WORLD_SURFACE_WG;
	}
}
