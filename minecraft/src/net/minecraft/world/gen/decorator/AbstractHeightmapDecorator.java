package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;

public abstract class AbstractHeightmapDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public AbstractHeightmapDecorator(Codec<DC> codec) {
		super(codec);
	}

	protected abstract Heightmap.Type getHeightmapType(DC config);
}
