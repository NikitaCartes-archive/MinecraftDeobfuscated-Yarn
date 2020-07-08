package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public class MotionBlockingHeightmapDecorator<DC extends DecoratorConfig> extends HeightmapDecorator<DC> {
	public MotionBlockingHeightmapDecorator(Codec<DC> codec) {
		super(codec);
	}

	@Override
	protected Heightmap.Type getHeightmapType(DC config) {
		return Heightmap.Type.MOTION_BLOCKING;
	}
}
