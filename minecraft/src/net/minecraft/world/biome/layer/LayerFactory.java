package net.minecraft.world.biome.layer;

public interface LayerFactory<A extends LayerSampler> {
	A make();
}
