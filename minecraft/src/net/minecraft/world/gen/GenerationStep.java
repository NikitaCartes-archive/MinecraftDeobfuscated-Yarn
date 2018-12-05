package net.minecraft.world.gen;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerationStep {
	public static enum Carver {
		field_13169("air"),
		field_13166("liquid");

		private static final Map<String, GenerationStep.Carver> BY_NAME = (Map<String, GenerationStep.Carver>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Carver::getName, carver -> carver));
		private final String name;

		private Carver(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}

	public static enum Feature {
		field_13174("raw_generation"),
		field_13171("local_modifications"),
		field_13172("underground_structures"),
		field_13173("surface_structures"),
		field_13176("underground_ores"),
		field_13177("underground_decoration"),
		field_13178("vegetal_decoration"),
		field_13179("top_layer_modification");

		private static final Map<String, GenerationStep.Feature> BY_NAME = (Map<String, GenerationStep.Feature>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Feature::getName, feature -> feature));
		private final String name;

		private Feature(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}
}
