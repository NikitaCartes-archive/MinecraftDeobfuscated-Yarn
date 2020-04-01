package net.minecraft;

import java.util.Random;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class class_5003 {
	public static Biome method_26461(int i) {
		Random random = new Random((long)i);
		return new class_5003.class_5004(random);
	}

	static class class_5004 extends Biome {
		public class_5004(Random random) {
			super(Biome.method_26453(random));
			Util.method_26718(random, 4, Registry.CARVER)
				.forEach(carver -> this.addCarver(Util.method_26715(GenerationStep.Carver.class, random), Biome.method_26447(carver, random)));
			Util.method_26718(random, 32, Registry.ENTITY_TYPE).forEach(entityType -> {
				int ix = random.nextInt(4);
				int j = ix + random.nextInt(4);
				this.addSpawn(entityType.getCategory(), new Biome.SpawnEntry(entityType, random.nextInt(20) + 1, ix, j));
			});
			Util.method_26718(random, 5, Registry.STRUCTURE_FEATURE)
				.forEach(structureFeature -> this.method_26465(Util.method_26715(GenerationStep.Feature.class, random), structureFeature.method_26593(random)));

			for (int i = 0; i < 32; i++) {
				this.addFeature(
					Util.method_26715(GenerationStep.Feature.class, random),
					Registry.FEATURE.getRandom(random).method_26588(random).createDecoratedFeature(Registry.DECORATOR.getRandom(random).method_26672(random))
				);
			}
		}

		private <C extends FeatureConfig> void method_26465(GenerationStep.Feature feature, ConfiguredFeature<C, ? extends StructureFeature<C>> configuredFeature) {
			this.addStructureFeature(configuredFeature);
			this.addFeature(feature, configuredFeature);
		}
	}
}
