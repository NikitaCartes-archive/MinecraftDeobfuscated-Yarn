package net.minecraft;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.MergingLayer;
import net.minecraft.world.biome.layer.VoidCoordinateTransformer;

public enum class_3652 implements MergingLayer, VoidCoordinateTransformer {
	field_16161;

	private static final int field_16165 = Registry.BIOME.getRawId(Biomes.field_9463);
	private static final int field_16164 = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int field_16163 = Registry.BIOME.getRawId(Biomes.field_9462);
	private static final int field_16162 = Registry.BIOME.getRawId(Biomes.field_9407);
	private static final int field_16160 = Registry.BIOME.getRawId(Biomes.field_9438);

	@Override
	public int sample(class_3630 arg, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
		int k = layerSampler.sample(this.transformX(i), this.transformY(j));
		int l = layerSampler2.sample(this.transformX(i), this.transformY(j));
		if (BiomeLayers.isOcean(k)) {
			return k;
		} else if (l == field_16160) {
			if (k == field_16164) {
				return field_16165;
			} else {
				return k != field_16163 && k != field_16162 ? l & 0xFF : field_16162;
			}
		} else {
			return k;
		}
	}
}
