package net.minecraft;

import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class class_6954 {
	private static final float field_36614 = 0.08F;
	private static final double field_36615 = 1.5;
	private static final double field_36616 = 1.5;
	private static final double field_36617 = 1.5625;
	private static final class_6910 field_36618 = class_6916.method_40480(10.0);
	private static final class_6910 field_36619 = class_6916.method_40479();

	public static class_6953 method_40544(
		GenerationShapeConfig generationShapeConfig,
		boolean bl,
		boolean bl2,
		long l,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		ChunkRandom.RandomProvider randomProvider
	) {
		RandomDeriver randomDeriver = randomProvider.create(l).createRandomDeriver();
		class_6910 lv = class_6916.method_40494(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.AQUIFER_BARRIER), 0.5);
		class_6910 lv2 = class_6916.method_40494(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67
		);
		class_6910 lv3 = class_6916.method_40493(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.AQUIFER_LAVA));
		class_6910 lv4 = class_6916.method_40494(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143
		);
		RandomDeriver randomDeriver2 = randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
		RandomDeriver randomDeriver3 = randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
		double d = 25.0;
		double e = 0.3;
		class_6910 lv5 = class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.PILLAR), 25.0, 0.3);
		class_6910 lv6 = class_6916.method_40495(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
		class_6910 lv7 = class_6916.method_40495(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
		class_6910 lv8 = class_6916.method_40486(class_6916.method_40500(lv5, class_6916.method_40480(2.0)), lv6);
		class_6910 lv9 = class_6916.method_40507(class_6916.method_40500(lv8, lv7.method_40473()));
		class_6910 lv10 = class_6916.method_40485(lv9, Double.NEGATIVE_INFINITY, 0.03, class_6916.method_40480(Double.NEGATIVE_INFINITY), lv9);
		class_6910 lv11 = class_6916.method_40502(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_MODULATOR), 2.0, 1.0
		);
		class_6910 lv12 = class_6916.method_40491(
			lv11, NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D), class_6954.CaveScaler::scaleCaves, 3.0
		);
		class_6910 lv13 = class_6916.method_40496(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_ELEVATION),
			0.0,
			(double)generationShapeConfig.minimumBlockY(),
			8.0
		);
		class_6910 lv14 = class_6916.method_40507(
			class_6916.method_40497(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)
		);
		class_6910 lv15 = class_6916.method_40486(lv13, class_6916.method_40481(-64, 320, 8.0, -40.0)).method_40471();
		class_6910 lv16 = class_6916.method_40486(lv15, lv14).method_40473();
		double f = 0.083;
		class_6910 lv17 = class_6916.method_40486(lv12, class_6916.method_40500(class_6916.method_40480(0.083), lv14));
		class_6910 lv18 = class_6916.method_40508(lv17, lv16).method_40468(-1.0, 1.0);
		class_6910 lv19 = class_6916.method_40507(
			class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_RARITY), 2.0, 1.0)
		);
		class_6910 lv20 = class_6916.method_40495(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_THICKNESS), -0.065, -0.088
		);
		class_6910 lv21 = class_6916.method_40491(
			lv19, NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_1), class_6954.CaveScaler::scaleTunnels, 2.0
		);
		class_6910 lv22 = class_6916.method_40491(
			lv19, NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_2), class_6954.CaveScaler::scaleTunnels, 2.0
		);
		class_6910 lv23 = class_6916.method_40486(class_6916.method_40508(lv21, lv22), lv20).method_40468(-1.0, 1.0);
		class_6910 lv24 = class_6916.method_40493(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
		class_6910 lv25 = class_6916.method_40495(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1
		);
		class_6910 lv26 = class_6916.method_40507(class_6916.method_40500(lv25, class_6916.method_40486(lv24.method_40471(), class_6916.method_40480(-0.4))));
		class_6910 lv27 = class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
		class_6910 lv28 = class_6916.method_40486(class_6916.method_40486(lv27, class_6916.method_40480(0.37)), class_6916.method_40481(-10, 30, 0.3, 0.0));
		class_6910 lv29 = class_6916.method_40507(class_6916.method_40505(lv28, class_6916.method_40486(lv26, lv23)));
		class_6910 lv30 = class_6916.method_40494(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.CAVE_LAYER), 8.0);
		class_6910 lv31 = class_6916.method_40500(class_6916.method_40480(4.0), lv30.method_40472());
		class_6910 lv32 = class_6916.method_40494(
			NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666
		);
		int i = DimensionType.MIN_HEIGHT * 2;
		int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
		class_6910 lv33 = class_6916.method_40481(i, j, (double)i, (double)j);
		int k = generationShapeConfig.minimumY();
		int m = Stream.of(class_6955.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(k);
		int n = Stream.of(class_6955.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(k);
		class_6910 lv34 = method_40539(
			lv33, class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5), m, n, 0
		);
		float g = 4.0F;
		class_6910 lv35 = method_40539(
				lv33, class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.ORE_VEIN_A), 4.0, 4.0), m, n, 0
			)
			.method_40471();
		class_6910 lv36 = method_40539(
				lv33, class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.ORE_VEIN_B), 4.0, 4.0), m, n, 0
			)
			.method_40471();
		class_6910 lv37 = class_6916.method_40486(class_6916.method_40480(-0.08F), class_6916.method_40508(lv35, lv36));
		class_6910 lv38 = class_6916.method_40493(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.ORE_GAP));
		int o = k + 4;
		int p = k + generationShapeConfig.height();
		class_6910 lv44;
		if (bl2) {
			class_6910 lv39 = method_40539(
				lv33, class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.NOODLE), 1.0, 1.0), o, p, -1
			);
			class_6910 lv40 = method_40539(
				lv33,
				class_6916.method_40497(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1),
				o,
				p,
				0
			);
			double h = 2.6666666666666665;
			class_6910 lv41 = method_40539(
				lv33,
				class_6916.method_40502(
					NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665
				),
				o,
				p,
				0
			);
			class_6910 lv42 = method_40539(
				lv33,
				class_6916.method_40502(
					NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665
				),
				o,
				p,
				0
			);
			class_6910 lv43 = class_6916.method_40500(class_6916.method_40480(1.5), class_6916.method_40508(lv41.method_40471(), lv42.method_40471()));
			lv44 = class_6916.method_40485(lv39, Double.NEGATIVE_INFINITY, 0.0, class_6916.method_40480(64.0), class_6916.method_40486(lv40, lv43));
		} else {
			lv44 = class_6916.method_40480(64.0);
		}

		boolean bl3 = generationShapeConfig.largeBiomes();
		DoublePerlinNoiseSampler doublePerlinNoiseSampler3;
		class_6910 lv39;
		DoublePerlinNoiseSampler doublePerlinNoiseSampler;
		DoublePerlinNoiseSampler doublePerlinNoiseSampler2;
		if (randomProvider != ChunkRandom.RandomProvider.LEGACY) {
			lv39 = new InterpolatedNoiseSampler(
				randomDeriver.createRandom(new Identifier("terrain")),
				generationShapeConfig.sampling(),
				generationShapeConfig.horizontalBlockSize(),
				generationShapeConfig.verticalBlockSize()
			);
			doublePerlinNoiseSampler = NoiseParametersKeys.createNoiseSampler(
				registry, randomDeriver, bl3 ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE
			);
			doublePerlinNoiseSampler2 = NoiseParametersKeys.createNoiseSampler(
				registry, randomDeriver, bl3 ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION
			);
			doublePerlinNoiseSampler3 = NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.OFFSET);
		} else {
			lv39 = new InterpolatedNoiseSampler(
				randomProvider.create(l), generationShapeConfig.sampling(), generationShapeConfig.horizontalBlockSize(), generationShapeConfig.verticalBlockSize()
			);
			doublePerlinNoiseSampler = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(l), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
			doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(l + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
			doublePerlinNoiseSampler3 = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
			);
		}

		class_6910 lv42 = class_6916.method_40499(class_6916.method_40504(class_6916.method_40501(doublePerlinNoiseSampler3)));
		class_6910 lv43 = class_6916.method_40499(class_6916.method_40504(class_6916.method_40506(doublePerlinNoiseSampler3)));
		class_6910 lv45 = class_6916.method_40487(lv42, lv43, 0.25, doublePerlinNoiseSampler);
		class_6910 lv46 = class_6916.method_40487(lv42, lv43, 0.25, doublePerlinNoiseSampler2);
		class_6910 lv47 = class_6916.method_40499(
			class_6916.method_40487(
				lv42,
				lv43,
				0.25,
				NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, bl3 ? NoiseParametersKeys.CONTINENTALNESS_LARGE : NoiseParametersKeys.CONTINENTALNESS)
			)
		);
		class_6910 lv48 = class_6916.method_40499(
			class_6916.method_40487(
				lv42, lv43, 0.25, NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, bl3 ? NoiseParametersKeys.EROSION_LARGE : NoiseParametersKeys.EROSION)
			)
		);
		class_6910 lv49 = class_6916.method_40499(
			class_6916.method_40487(lv42, lv43, 0.25, NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.RIDGE))
		);
		VanillaTerrainParameters vanillaTerrainParameters = generationShapeConfig.terrainParameters();
		class_6910 lv50 = method_40541(lv47, lv48, lv49, vanillaTerrainParameters::getOffset, -0.81, 2.5, class_6916.method_40503());
		class_6910 lv51 = method_40541(lv47, lv48, lv49, vanillaTerrainParameters::getFactor, 0.0, 8.0, field_36618);
		class_6910 lv52 = method_40541(lv47, lv48, lv49, vanillaTerrainParameters::getPeak, 0.0, 1.28, field_36619);
		class_6910 lv53 = class_6916.method_40486(class_6916.method_40481(-64, 320, 1.5, -1.5), lv50);
		class_6910 lv54 = class_6916.method_40502(NoiseParametersKeys.createNoiseSampler(registry, randomDeriver, NoiseParametersKeys.JAGGED), 1500.0, 0.0);
		class_6910 lv55 = class_6916.method_40500(lv52, lv54.method_40474());
		class_6910 lv56;
		class_6910 lv57;
		if (generationShapeConfig.islandNoiseOverride()) {
			lv56 = class_6916.method_40482(l);
			lv57 = class_6916.method_40504(lv56);
		} else {
			lv56 = method_40540(lv53, lv51, lv55);
			lv57 = method_40540(lv53, class_6916.method_40504(lv51), class_6916.method_40479());
		}

		boolean bl4 = !bl;
		class_6910 lv58 = class_6916.method_40507(class_6916.method_40486(lv56, lv39));
		class_6910 lv59 = class_6916.method_40486(
			class_6916.method_40486(class_6916.method_40480(0.27), lv32).method_40468(-1.0, 1.0),
			class_6916.method_40486(class_6916.method_40480(1.5), class_6916.method_40500(class_6916.method_40480(-0.64), lv58)).method_40468(0.0, 0.5)
		);
		class_6910 lv60 = class_6916.method_40486(lv31, lv59);
		class_6910 lv61 = class_6916.method_40505(class_6916.method_40505(lv60, lv29), class_6916.method_40486(lv18, lv26));
		class_6910 lv62 = class_6916.method_40508(lv61, lv10);
		class_6910 lv63;
		if (bl4) {
			lv63 = lv58;
		} else {
			class_6910 lv64 = class_6916.method_40505(lv58, class_6916.method_40500(class_6916.method_40480(5.0), lv29));
			lv63 = class_6916.method_40485(lv58, Double.NEGATIVE_INFINITY, 1.5625, lv64, lv62);
		}

		class_6910 lv64 = class_6916.method_40492(generationShapeConfig, lv63);
		class_6910 lv65 = class_6916.method_40483(class_6916.method_40512(lv64));
		class_6910 lv66 = class_6916.method_40500(lv65, class_6916.method_40480(0.64));
		class_6910 lv67 = class_6916.method_40505(lv66.method_40476(), lv44);
		return new class_6953(
			lv,
			lv2,
			lv4,
			lv3,
			randomDeriver2,
			randomDeriver3,
			lv45,
			lv46,
			lv47,
			lv48,
			lv53,
			lv49,
			lv57,
			lv67,
			lv34,
			lv37,
			lv38,
			new VanillaBiomeParameters().getSpawnSuitabilityNoises()
		);
	}

	private static class_6910 method_40541(
		class_6910 arg, class_6910 arg2, class_6910 arg3, ToFloatFunction<VanillaTerrainParameters.NoisePoint> toFloatFunction, double d, double e, class_6910 arg4
	) {
		class_6910 lv = class_6916.method_40489(arg, arg2, arg3, toFloatFunction, d, e);
		class_6910 lv2 = class_6916.method_40488(class_6916.method_40498(), arg4, lv);
		return class_6916.method_40499(class_6916.method_40504(lv2));
	}

	private static class_6910 method_40540(class_6910 arg, class_6910 arg2, class_6910 arg3) {
		class_6910 lv = class_6916.method_40500(class_6916.method_40486(arg, arg3), arg2);
		return class_6916.method_40500(class_6916.method_40480(4.0), lv.method_40475());
	}

	private static class_6910 method_40539(class_6910 arg, class_6910 arg2, int i, int j, int k) {
		return class_6916.method_40483(class_6916.method_40485(arg, (double)i, (double)(j + 1), arg2, class_6916.method_40480((double)k)));
	}

	protected static double method_40542(GenerationShapeConfig generationShapeConfig, double d, double e) {
		double f = (double)((int)e / generationShapeConfig.verticalBlockSize() - generationShapeConfig.minimumBlockY());
		d = generationShapeConfig.topSlide().method_38414(d, (double)generationShapeConfig.verticalBlockCount() - f);
		return generationShapeConfig.bottomSlide().method_38414(d, f);
	}

	public static double method_40543(GenerationShapeConfig generationShapeConfig, class_6910 arg, int i, int j) {
		for (int k = generationShapeConfig.minimumBlockY() + generationShapeConfig.verticalBlockCount(); k >= generationShapeConfig.minimumBlockY(); k--) {
			int l = k * generationShapeConfig.verticalBlockSize();
			double d = -0.703125;
			double e = arg.method_40464(new class_6910.class_6914(i, l, j)) + -0.703125;
			double f = MathHelper.clamp(e, -64.0, 64.0);
			f = method_40542(generationShapeConfig, f, (double)l);
			if (f > 0.390625) {
				return (double)l;
			}
		}

		return 2.147483647E9;
	}

	static final class CaveScaler {
		private CaveScaler() {
		}

		private static double scaleCaves(double value) {
			if (value < -0.75) {
				return 0.5;
			} else if (value < -0.5) {
				return 0.75;
			} else if (value < 0.5) {
				return 1.0;
			} else {
				return value < 0.75 ? 2.0 : 3.0;
			}
		}

		private static double scaleTunnels(double value) {
			if (value < -0.5) {
				return 0.75;
			} else if (value < 0.0) {
				return 1.0;
			} else {
				return value < 0.5 ? 1.5 : 2.0;
			}
		}
	}
}
