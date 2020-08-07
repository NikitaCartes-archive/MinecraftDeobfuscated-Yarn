package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public class HeightmapWorldSurfaceDecorator extends HeightmapDecorator<NopeDecoratorConfig> {
	public HeightmapWorldSurfaceDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected Heightmap.Type method_30464(NopeDecoratorConfig nopeDecoratorConfig) {
		return Heightmap.Type.field_13194;
	}
}
