package net.minecraft.world.gen;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerationStep {
	public static enum Carver {
		AIR("air"),
		LIQUID("liquid");

		private static final Map<String, GenerationStep.Carver> BY_NAME = (Map<String, GenerationStep.Carver>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Carver::getName, carver -> carver));
		private final String name;

		private Carver(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public static enum Feature {
		RAW_GENERATION("raw_generation"),
		LOCAL_MODIFICATIONS("local_modifications"),
		UNDERGROUND_STRUCTURES("underground_structures"),
		SURFACE_STRUCTURES("surface_structures"),
		UNDERGROUND_ORES("underground_ores"),
		UNDERGROUND_DECORATION("underground_decoration"),
		VEGETAL_DECORATION("vegetal_decoration"),
		TOP_LAYER_MODIFICATION("top_layer_modification");

		private static final Map<String, GenerationStep.Feature> BY_NAME = (Map<String, GenerationStep.Feature>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Feature::getName, feature -> feature));
		private final String name;

		private Feature(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
