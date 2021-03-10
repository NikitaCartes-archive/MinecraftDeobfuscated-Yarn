package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Heightmap;

public class HeightmapDecoratorConfig implements DecoratorConfig {
	public static final Codec<HeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(heightmapDecoratorConfig -> heightmapDecoratorConfig.heightmap))
				.apply(instance, HeightmapDecoratorConfig::new)
	);
	public final Heightmap.Type heightmap;

	public HeightmapDecoratorConfig(Heightmap.Type heightmap) {
		this.heightmap = heightmap;
	}
}
