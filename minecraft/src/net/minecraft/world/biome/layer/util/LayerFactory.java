package net.minecraft.world.biome.layer.util;

public interface LayerFactory<A extends LayerSampler> {
	A make();
}
