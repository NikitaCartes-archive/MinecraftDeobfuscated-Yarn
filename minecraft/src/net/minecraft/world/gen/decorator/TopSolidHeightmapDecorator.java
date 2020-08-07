package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public class TopSolidHeightmapDecorator extends HeightmapDecorator<NopeDecoratorConfig> {
	public TopSolidHeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected Heightmap.Type method_30470(NopeDecoratorConfig nopeDecoratorConfig) {
		return Heightmap.Type.field_13195;
	}
}
