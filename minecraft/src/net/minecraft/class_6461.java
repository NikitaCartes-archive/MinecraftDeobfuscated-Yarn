package net.minecraft;

import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class class_6461 {
	private final class_6452.class_6454 field_34198 = class_6452.method_37621(-1.0F, 1.0F);
	private final class_6452.class_6454[] field_34199 = new class_6452.class_6454[]{
		class_6452.method_37621(-1.0F, -0.45F),
		class_6452.method_37621(-0.45F, -0.15F),
		class_6452.method_37621(-0.15F, 0.15F),
		class_6452.method_37621(0.15F, 0.45F),
		class_6452.method_37621(0.45F, 1.0F)
	};
	private final class_6452.class_6454[] field_34200 = new class_6452.class_6454[]{
		class_6452.method_37621(-1.0F, -0.3F),
		class_6452.method_37621(-0.3F, -0.1F),
		class_6452.method_37621(-0.1F, 0.1F),
		class_6452.method_37621(0.1F, 0.3F),
		class_6452.method_37621(0.3F, 1.0F)
	};
	private final class_6452.class_6454[] field_34201 = new class_6452.class_6454[]{
		class_6452.method_37621(-1.0F, -0.375F),
		class_6452.method_37621(-0.375F, -0.2225F),
		class_6452.method_37621(-0.2225F, 0.05F),
		class_6452.method_37621(0.05F, 0.51F),
		class_6452.method_37621(0.51F, 0.6F),
		class_6452.method_37621(0.6F, 1.0F)
	};
	private final class_6452.class_6454 field_34202 = this.field_34199[0];
	private final class_6452.class_6454 field_34203 = class_6452.method_37624(this.field_34199[1], this.field_34199[4]);
	private final class_6452.class_6454 field_34204 = class_6452.method_37621(-1.05F, -1.05F);
	private final class_6452.class_6454 field_34205 = class_6452.method_37621(-1.05F, -0.455F);
	private final class_6452.class_6454 field_34206 = class_6452.method_37621(-0.455F, -0.17F);
	private final class_6452.class_6454 field_34207 = class_6452.method_37621(-0.17F, -0.13F);
	private final class_6452.class_6454 field_34208 = class_6452.method_37621(-0.13F, 0.55F);
	private final class_6452.class_6454 field_34209 = class_6452.method_37621(-0.13F, 0.03F);
	private final class_6452.class_6454 field_34210 = class_6452.method_37621(0.03F, 0.55F);
	private final class_6452.class_6454 field_34211 = class_6452.method_37621(0.55F, 1.0F);
	private final RegistryKey<Biome>[][] OCEAN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN},
		{BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}
	};
	private final RegistryKey<Biome>[][] field_34213 = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GIANT_TREE_TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST},
		{BiomeKeys.DESERT, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE},
		{BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.JUNGLE}
	};
	private final RegistryKey<Biome>[][] field_34214 = new RegistryKey[][]{
		{null, null, null, BiomeKeys.GIANT_SPRUCE_TAIGA, null},
		{null, null, null, null, null},
		{BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TALL_BIRCH_FOREST, null},
		{null, null, null, null, BiomeKeys.BAMBOO_JUNGLE},
		{null, null, null, null, null}
	};
	private final RegistryKey<Biome>[][] MOUNTAIN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS},
		{BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS}
	};

	public void method_37705(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder) {
		if (SharedConstants.field_34061) {
			this.method_37717(builder);
		} else {
			this.method_37709(builder);
			this.method_37713(builder);
			this.method_37715(builder);
		}
	}

	private void method_37709(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder) {
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34204, this.field_34198, this.field_34198, 0.0F, BiomeKeys.MUSHROOM_FIELDS);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv = this.field_34199[i];
			this.method_37707(builder, lv, this.field_34198, this.field_34205, this.field_34198, this.field_34198, 0.0F, this.OCEAN_BIOMES[0][i]);
			this.method_37707(builder, lv, this.field_34198, this.field_34206, this.field_34198, this.field_34198, 0.0F, this.OCEAN_BIOMES[1][i]);
		}
	}

	private void method_37713(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder) {
		float f = 0.05F;
		float g = 0.06666667F;
		float h = 0.6F;
		float i = 0.73333335F;
		this.method_37714(builder, class_6452.method_37621(-1.0F, -0.93333334F));
		this.method_37710(builder, class_6452.method_37621(-0.93333334F, -0.73333335F));
		this.method_37706(builder, class_6452.method_37621(-0.73333335F, -0.6F));
		this.method_37710(builder, class_6452.method_37621(-0.6F, -0.4F));
		this.method_37714(builder, class_6452.method_37621(-0.4F, -0.26666668F));
		this.method_37716(builder, class_6452.method_37621(-0.26666668F, -0.05F));
		this.method_37718(builder, class_6452.method_37621(-0.05F, 0.05F));
		this.method_37716(builder, class_6452.method_37621(0.05F, 0.26666668F));
		this.method_37714(builder, class_6452.method_37621(0.26666668F, 0.4F));
		this.method_37710(builder, class_6452.method_37621(0.4F, 0.6F));
		this.method_37706(builder, class_6452.method_37621(0.6F, 0.73333335F));
		this.method_37710(builder, class_6452.method_37621(0.73333335F, 0.93333334F));
		this.method_37714(builder, class_6452.method_37621(0.93333334F, 1.0F));
	}

	private void method_37706(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder, class_6452.class_6454 arg) {
		class_6452.class_6454 lv = class_6452.method_37624(this.field_34208, this.field_34211);
		class_6452.class_6454 lv2 = class_6452.method_37624(this.field_34210, this.field_34211);
		this.method_37707(builder, class_6452.method_37621(-1.0F, -0.15F), this.field_34198, lv2, this.field_34201[0], arg, 0.0F, BiomeKeys.SNOWCAPPED_PEAKS);
		this.method_37707(builder, class_6452.method_37621(-0.15F, 1.0F), this.field_34198, lv2, this.field_34201[0], arg, 0.0F, BiomeKeys.LOFTY_PEAKS);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv3 = this.field_34199[i];

			for (int j = 0; j < this.field_34200.length; j++) {
				class_6452.class_6454 lv4 = this.field_34200[j];
				RegistryKey<Biome> registryKey = this.method_37708(i, j, arg);
				RegistryKey<Biome> registryKey2 = this.method_37702(i, j);
				this.method_37707(builder, lv3, lv4, lv2, class_6452.method_37624(this.field_34201[1], this.field_34201[2]), arg, 0.0F, registryKey);
				this.method_37707(builder, lv3, lv4, lv, this.field_34201[3], arg, 0.0F, registryKey2);
				this.method_37707(builder, lv3, lv4, this.field_34210, this.field_34201[4], arg, 0.0F, registryKey2);
				this.method_37707(builder, lv3, lv4, lv, this.field_34201[5], arg, 0.0F, registryKey2);
				this.method_37707(builder, lv3, lv4, this.field_34209, class_6452.method_37624(this.field_34201[0], this.field_34201[2]), arg, 0.0F, registryKey);
			}
		}

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv3 = this.field_34199[i];
			RegistryKey<Biome> registryKey3 = this.method_37704(i, BiomeKeys.MOUNTAINS);
			this.method_37707(builder, lv3, this.field_34198, this.field_34209, this.field_34201[4], arg, 0.0F, registryKey3);
			if (i > 1) {
				this.method_37707(builder, lv3, this.field_34198, this.field_34207, this.field_34201[4], arg, 0.0F, BiomeKeys.SHATTERED_SAVANNA);
			}
		}
	}

	private void method_37710(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder, class_6452.class_6454 arg) {
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34207, this.field_34201[3], arg, 0.0F, BiomeKeys.STONE_SHORE);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34207, this.field_34201[5], arg, 0.0F, BiomeKeys.STONE_SHORE);
		this.method_37707(
			builder,
			this.field_34198,
			this.field_34198,
			class_6452.method_37624(this.field_34210, this.field_34211),
			this.field_34201[0],
			arg,
			0.0F,
			BiomeKeys.SNOWY_SLOPES
		);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv = this.field_34199[i];

			for (int j = 0; j < this.field_34200.length; j++) {
				class_6452.class_6454 lv2 = this.field_34200[j];
				RegistryKey<Biome> registryKey = this.method_37703(i, j, arg);
				RegistryKey<Biome> registryKey2 = this.method_37708(i, j, arg);
				RegistryKey<Biome> registryKey3 = this.method_37704(i, BiomeKeys.STONE_SHORE);
				RegistryKey<Biome> registryKey4 = this.method_37704(i, registryKey);
				this.method_37707(
					builder,
					lv,
					lv2,
					class_6452.method_37624(this.field_34207, this.field_34209),
					class_6452.method_37624(this.field_34201[0], this.field_34201[2]),
					arg,
					0.0F,
					registryKey
				);
				this.method_37707(
					builder,
					lv,
					lv2,
					class_6452.method_37624(this.field_34210, this.field_34211),
					class_6452.method_37624(this.field_34201[1], this.field_34201[2]),
					arg,
					0.0F,
					registryKey2
				);
				this.method_37707(builder, lv, lv2, class_6452.method_37624(this.field_34208, this.field_34211), this.field_34201[3], arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, class_6452.method_37624(this.field_34210, this.field_34211), this.field_34201[4], arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, class_6452.method_37624(this.field_34208, this.field_34211), this.field_34201[5], arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34207, this.field_34201[4], arg, 0.0F, registryKey3);
				this.method_37707(builder, lv, lv2, this.field_34209, this.field_34201[4], arg, 0.0F, registryKey4);
			}
		}
	}

	private void method_37714(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder, class_6452.class_6454 arg) {
		this.method_37707(
			builder,
			this.field_34198,
			this.field_34198,
			this.field_34207,
			class_6452.method_37624(this.field_34201[0], this.field_34201[1]),
			arg,
			0.0F,
			BiomeKeys.STONE_SHORE
		);
		this.method_37707(
			builder,
			this.field_34202,
			this.field_34198,
			this.field_34207,
			class_6452.method_37624(this.field_34201[2], this.field_34201[3]),
			arg,
			0.0F,
			BiomeKeys.SNOWY_BEACH
		);
		this.method_37707(
			builder, this.field_34203, this.field_34198, this.field_34207, class_6452.method_37624(this.field_34201[2], this.field_34201[3]), arg, 0.0F, BiomeKeys.BEACH
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37624(this.field_34207, this.field_34208), this.field_34201[5], arg, 0.0F, BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv = this.field_34199[i];

			for (int j = 0; j < this.field_34200.length; j++) {
				class_6452.class_6454 lv2 = this.field_34200[j];
				RegistryKey<Biome> registryKey = this.method_37708(i, j, arg);
				RegistryKey<Biome> registryKey2 = this.method_37703(i, j, arg);
				RegistryKey<Biome> registryKey3 = this.method_37712(i, j, arg);
				RegistryKey<Biome> registryKey4 = i == 0 ? this.method_37704(i, BiomeKeys.SNOWY_BEACH) : this.method_37704(i, BiomeKeys.BEACH);
				RegistryKey<Biome> registryKey5 = this.method_37704(i, registryKey2);
				this.method_37707(builder, lv, lv2, class_6452.method_37624(this.field_34210, this.field_34211), this.field_34201[0], arg, 0.0F, registryKey3);
				this.method_37707(builder, lv, lv2, this.field_34209, class_6452.method_37624(this.field_34201[0], this.field_34201[1]), arg, 0.0F, registryKey2);
				this.method_37707(builder, lv, lv2, this.field_34210, this.field_34201[1], arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34208, class_6452.method_37624(this.field_34201[2], this.field_34201[3]), arg, 0.0F, registryKey2);
				this.method_37707(builder, lv, lv2, this.field_34210, this.field_34201[4], arg, 0.0F, registryKey2);
				this.method_37707(builder, lv, lv2, this.field_34211, class_6452.method_37624(this.field_34201[1], this.field_34201[5]), arg, 0.0F, registryKey2);
				this.method_37707(builder, lv, lv2, this.field_34207, this.field_34201[4], arg, 0.0F, registryKey4);
				this.method_37707(builder, lv, lv2, this.field_34209, this.field_34201[4], arg, 0.0F, registryKey5);
			}
		}
	}

	private void method_37716(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder, class_6452.class_6454 arg) {
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34207, this.field_34201[0], arg, 0.0F, BiomeKeys.STONE_SHORE);
		this.method_37707(
			builder,
			this.field_34202,
			this.field_34198,
			this.field_34207,
			class_6452.method_37624(this.field_34201[1], this.field_34201[3]),
			arg,
			0.0F,
			BiomeKeys.SNOWY_BEACH
		);
		this.method_37707(
			builder, this.field_34203, this.field_34198, this.field_34207, class_6452.method_37624(this.field_34201[1], this.field_34201[3]), arg, 0.0F, BiomeKeys.BEACH
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37624(this.field_34207, this.field_34208), this.field_34201[5], arg, 0.0F, BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv = this.field_34199[i];

			for (int j = 0; j < this.field_34200.length; j++) {
				class_6452.class_6454 lv2 = this.field_34200[j];
				RegistryKey<Biome> registryKey = this.method_37703(i, j, arg);
				RegistryKey<Biome> registryKey2 = this.method_37712(i, j, arg);
				RegistryKey<Biome> registryKey3 = i == 0 ? this.method_37704(i, BiomeKeys.SNOWY_BEACH) : this.method_37704(i, BiomeKeys.BEACH);
				RegistryKey<Biome> registryKey4 = this.method_37704(i, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34208, class_6452.method_37624(this.field_34201[1], this.field_34201[3]), arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34210, this.field_34201[4], arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34211, class_6452.method_37624(this.field_34201[1], this.field_34201[5]), arg, 0.0F, registryKey);
				this.method_37707(builder, lv, lv2, this.field_34207, this.field_34201[4], arg, 0.0F, registryKey3);
				this.method_37707(builder, lv, lv2, this.field_34209, this.field_34201[4], arg, 0.0F, registryKey4);
				this.method_37707(builder, lv, lv2, class_6452.method_37624(this.field_34208, this.field_34211), this.field_34201[0], arg, 0.0F, registryKey2);
			}
		}
	}

	private void method_37718(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder, class_6452.class_6454 arg) {
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34207, this.field_34201[0], arg, 0.0F, BiomeKeys.SWAMP);
		this.method_37707(
			builder,
			this.field_34202,
			this.field_34198,
			this.field_34207,
			class_6452.method_37624(this.field_34201[1], this.field_34201[5]),
			arg,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.method_37707(
			builder, this.field_34203, this.field_34198, this.field_34207, class_6452.method_37624(this.field_34201[1], this.field_34201[5]), arg, 0.0F, BiomeKeys.RIVER
		);
		this.method_37707(builder, this.field_34202, this.field_34198, this.field_34208, this.field_34198, arg, 0.0F, BiomeKeys.FROZEN_RIVER);
		this.method_37707(builder, this.field_34203, this.field_34198, this.field_34208, this.field_34198, arg, 0.0F, BiomeKeys.RIVER);

		for (int i = 0; i < this.field_34199.length; i++) {
			class_6452.class_6454 lv = this.field_34199[i];

			for (int j = 0; j < this.field_34200.length; j++) {
				class_6452.class_6454 lv2 = this.field_34200[j];
				this.method_37707(builder, lv, lv2, this.field_34211, this.field_34198, arg, 0.0F, this.method_37703(i, j, arg));
			}
		}
	}

	private void method_37715(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder) {
		this.method_37711(
			builder, this.field_34198, this.field_34198, class_6452.method_37621(0.8F, 1.0F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.DRIPSTONE_CAVES
		);
		this.method_37711(
			builder, this.field_34198, class_6452.method_37621(0.6F, 1.0F), this.field_34198, this.field_34198, this.field_34198, 0.0F, BiomeKeys.LUSH_CAVES
		);
	}

	private RegistryKey<Biome> method_37703(int i, int j, class_6452.class_6454 arg) {
		if (arg.method_37632() < 0.0F) {
			return this.field_34213[i][j];
		} else {
			RegistryKey<Biome> registryKey = this.field_34214[i][j];
			return registryKey == null ? this.field_34213[i][j] : registryKey;
		}
	}

	private RegistryKey<Biome> method_37704(int i, RegistryKey<Biome> registryKey) {
		return i > 2 ? BiomeKeys.SHATTERED_SAVANNA : registryKey;
	}

	private RegistryKey<Biome> method_37708(int i, int j, class_6452.class_6454 arg) {
		if (i < 2) {
			return this.method_37703(0, j, arg);
		} else if (i > 2) {
			return j < 2 ? BiomeKeys.BADLANDS : BiomeKeys.WOODED_BADLANDS_PLATEAU;
		} else {
			return BiomeKeys.MEADOW;
		}
	}

	private RegistryKey<Biome> method_37712(int i, int j, class_6452.class_6454 arg) {
		if (i >= 2) {
			return this.method_37703(i, j, arg);
		} else {
			return j <= 1 ? BiomeKeys.SNOWY_SLOPES : BiomeKeys.GROVE;
		}
	}

	private RegistryKey<Biome> method_37702(int i, int j) {
		return this.MOUNTAIN_BIOMES[i][j];
	}

	private void method_37707(
		Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder,
		class_6452.class_6454 arg,
		class_6452.class_6454 arg2,
		class_6452.class_6454 arg3,
		class_6452.class_6454 arg4,
		class_6452.class_6454 arg5,
		float f,
		RegistryKey<Biome> registryKey
	) {
		builder.add(Pair.of(class_6452.method_37625(arg, arg2, arg3, arg4, class_6452.method_37620(0.0F), arg5, f), registryKey));
		builder.add(Pair.of(class_6452.method_37625(arg, arg2, arg3, arg4, class_6452.method_37620(1.0F), arg5, f), registryKey));
	}

	private void method_37711(
		Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder,
		class_6452.class_6454 arg,
		class_6452.class_6454 arg2,
		class_6452.class_6454 arg3,
		class_6452.class_6454 arg4,
		class_6452.class_6454 arg5,
		float f,
		RegistryKey<Biome> registryKey
	) {
		builder.add(Pair.of(class_6452.method_37625(arg, arg2, arg3, arg4, class_6452.method_37621(0.1F, 0.9F), arg5, f), registryKey));
	}

	private void method_37717(Builder<Pair<class_6452.MixedNoisePoint, RegistryKey<Biome>>> builder) {
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, this.field_34198, this.field_34198, 0.01F, BiomeKeys.PLAINS);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(-0.9F), this.field_34198, 0.0F, BiomeKeys.DESERT);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(-0.4F), this.field_34198, 0.0F, BiomeKeys.BADLANDS);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(-0.35F), this.field_34198, 0.0F, BiomeKeys.DESERT);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(-0.1F), this.field_34198, 0.0F, BiomeKeys.BADLANDS);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(0.2F), this.field_34198, 0.0F, BiomeKeys.DESERT);
		this.method_37707(builder, this.field_34198, this.field_34198, this.field_34198, class_6452.method_37620(1.0F), this.field_34198, 0.0F, BiomeKeys.BADLANDS);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-1.1F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-1.005F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.51F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.44F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.18F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.16F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.15F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(-0.1F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(
			builder, this.field_34198, this.field_34198, class_6452.method_37620(0.25F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA
		);
		this.method_37707(builder, this.field_34198, this.field_34198, class_6452.method_37620(1.0F), this.field_34198, this.field_34198, 0.0F, BiomeKeys.SNOWY_TAIGA);
	}
}
