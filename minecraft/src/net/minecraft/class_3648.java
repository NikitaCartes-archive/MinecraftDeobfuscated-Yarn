package net.minecraft;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.MergingLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum class_3648 implements MergingLayer, class_3739 {
	field_16134;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_16126 = Registry.BIOME.getRawId(Biomes.field_9412);
	private static final int field_16142 = Registry.BIOME.getRawId(Biomes.field_9421);
	private static final int field_16125 = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int field_16140 = Registry.BIOME.getRawId(Biomes.field_9466);
	private static final int field_16124 = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int field_16139 = Registry.BIOME.getRawId(Biomes.field_9460);
	private static final int field_16123 = Registry.BIOME.getRawId(Biomes.field_9409);
	private static final int field_16138 = Registry.BIOME.getRawId(Biomes.field_9459);
	private static final int field_16151 = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int field_16137 = Registry.BIOME.getRawId(Biomes.field_9444);
	private static final int field_16150 = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int field_16136 = Registry.BIOME.getRawId(Biomes.field_9432);
	private static final int field_16149 = Registry.BIOME.getRawId(Biomes.field_9440);
	private static final int field_16135 = Registry.BIOME.getRawId(Biomes.field_9468);
	private static final int field_16148 = Registry.BIOME.getRawId(Biomes.field_9415);
	private static final int field_16133 = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int field_16147 = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int field_16132 = Registry.BIOME.getRawId(Biomes.field_9477);
	private static final int field_16146 = Registry.BIOME.getRawId(Biomes.field_9429);
	private static final int field_16131 = Registry.BIOME.getRawId(Biomes.field_9475);
	private static final int field_16145 = Registry.BIOME.getRawId(Biomes.field_9449);
	private static final int field_16130 = Registry.BIOME.getRawId(Biomes.field_9430);
	private static final int field_16144 = Registry.BIOME.getRawId(Biomes.field_9420);
	private static final int field_16129 = Registry.BIOME.getRawId(Biomes.field_9454);
	private static final int field_16143 = Registry.BIOME.getRawId(Biomes.field_9425);
	private static final int field_16127 = Registry.BIOME.getRawId(Biomes.field_9428);

	@Override
	public int sample(class_3630 arg, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
		int k = layerSampler.sample(this.transformX(i + 1), this.transformY(j + 1));
		int l = layerSampler2.sample(this.transformX(i + 1), this.transformY(j + 1));
		if (k > 255) {
			LOGGER.debug("old! {}", k);
		}

		int m = (l - 2) % 29;
		if (!BiomeLayers.isShallowOcean(k) && l >= 2 && m == 1) {
			Biome biome = Registry.BIOME.getInt(k);
			if (biome == null || !biome.hasParent()) {
				Biome biome2 = Biome.getParentBiome(biome);
				return biome2 == null ? k : Registry.BIOME.getRawId(biome2);
			}
		}

		if (arg.nextInt(3) == 0 || m == 0) {
			int n = k;
			if (k == field_16125) {
				n = field_16140;
			} else if (k == field_16123) {
				n = field_16138;
			} else if (k == field_16126) {
				n = field_16142;
			} else if (k == field_16131) {
				n = field_16147;
			} else if (k == field_16144) {
				n = field_16127;
			} else if (k == field_16132) {
				n = field_16146;
			} else if (k == field_16129) {
				n = field_16143;
			} else if (k == field_16147) {
				n = arg.nextInt(3) == 0 ? field_16138 : field_16123;
			} else if (k == field_16151) {
				n = field_16137;
			} else if (k == field_16150) {
				n = field_16136;
			} else if (k == field_16149) {
				n = field_16135;
			} else if (k == BiomeLayers.OCEAN_ID) {
				n = BiomeLayers.DEEP_OCEAN_ID;
			} else if (k == BiomeLayers.LUKEWARM_OCEAN_ID) {
				n = BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
			} else if (k == BiomeLayers.COLD_OCEAN_ID) {
				n = BiomeLayers.DEEP_COLD_OCEAN_ID;
			} else if (k == BiomeLayers.FROZEN_OCEAN_ID) {
				n = BiomeLayers.DEEP_FROZEN_OCEAN_ID;
			} else if (k == field_16124) {
				n = field_16139;
			} else if (k == field_16145) {
				n = field_16130;
			} else if (BiomeLayers.areSimilar(k, field_16133)) {
				n = field_16148;
			} else if ((
					k == BiomeLayers.DEEP_OCEAN_ID || k == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || k == BiomeLayers.DEEP_COLD_OCEAN_ID || k == BiomeLayers.DEEP_FROZEN_OCEAN_ID
				)
				&& arg.nextInt(3) == 0) {
				n = arg.nextInt(2) == 0 ? field_16147 : field_16123;
			}

			if (m == 0 && n != k) {
				Biome biome2 = Biome.getParentBiome(Registry.BIOME.getInt(n));
				n = biome2 == null ? k : Registry.BIOME.getRawId(biome2);
			}

			if (n != k) {
				int o = 0;
				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformY(j + 0)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 2), this.transformY(j + 1)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 0), this.transformY(j + 1)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformY(j + 2)), k)) {
					o++;
				}

				if (o >= 3) {
					return n;
				}
			}
		}

		return k;
	}
}
