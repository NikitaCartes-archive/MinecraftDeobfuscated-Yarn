package net.minecraft;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorSettings;
import net.minecraft.world.level.LevelGeneratorType;

public class class_3640 implements class_3661 {
	private static final int field_16074 = Registry.BIOME.getRawId(Biomes.field_9412);
	private static final int field_16073 = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int field_16072 = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int field_16071 = Registry.BIOME.getRawId(Biomes.field_9409);
	private static final int field_16069 = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int field_16067 = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int field_16065 = Registry.BIOME.getRawId(Biomes.field_9433);
	private static final int field_16063 = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int field_16061 = Registry.BIOME.getRawId(Biomes.field_9462);
	private static final int field_16083 = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int field_16081 = Registry.BIOME.getRawId(Biomes.field_9477);
	private static final int field_16080 = Registry.BIOME.getRawId(Biomes.field_9475);
	private static final int field_16079 = Registry.BIOME.getRawId(Biomes.field_9449);
	private static final int field_16078 = Registry.BIOME.getRawId(Biomes.field_9471);
	private static final int field_16077 = Registry.BIOME.getRawId(Biomes.field_9420);
	private static final int field_16076 = Registry.BIOME.getRawId(Biomes.field_9454);
	private static final int[] field_16082 = new int[]{field_16073, field_16071, field_16072, field_16078, field_16083, field_16077};
	private static final int[] field_16064 = new int[]{field_16073, field_16073, field_16073, field_16079, field_16079, field_16083};
	private static final int[] field_16062 = new int[]{field_16071, field_16080, field_16072, field_16083, field_16074, field_16078};
	private static final int[] field_16068 = new int[]{field_16071, field_16072, field_16077, field_16083};
	private static final int[] field_16066 = new int[]{field_16069, field_16069, field_16069, field_16076};
	private final OverworldChunkGeneratorSettings field_16075;
	private int[] field_16070 = field_16064;

	public class_3640(LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorSettings overworldChunkGeneratorSettings) {
		if (levelGeneratorType == LevelGeneratorType.DEFAULT_1_1) {
			this.field_16070 = field_16082;
			this.field_16075 = null;
		} else {
			this.field_16075 = overworldChunkGeneratorSettings;
		}
	}

	@Override
	public int method_15866(class_3630 arg, int i) {
		if (this.field_16075 != null && this.field_16075.method_12615() >= 0) {
			return this.field_16075.method_12615();
		} else {
			int j = (i & 3840) >> 8;
			i &= -3841;
			if (!BiomeLayers.isOcean(i) && i != field_16061) {
				switch (i) {
					case 1:
						if (j > 0) {
							return arg.nextInt(3) == 0 ? field_16065 : field_16063;
						}

						return this.field_16070[arg.nextInt(this.field_16070.length)];
					case 2:
						if (j > 0) {
							return field_16067;
						}

						return field_16062[arg.nextInt(field_16062.length)];
					case 3:
						if (j > 0) {
							return field_16081;
						}

						return field_16068[arg.nextInt(field_16068.length)];
					case 4:
						return field_16066[arg.nextInt(field_16066.length)];
					default:
						return field_16061;
				}
			} else {
				return i;
			}
		}
	}
}
