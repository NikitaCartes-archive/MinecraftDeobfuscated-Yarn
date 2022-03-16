package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;

public class GenerationStep {
	public static enum Carver implements StringIdentifiable {
		AIR("air"),
		LIQUID("liquid");

		public static final Codec<GenerationStep.Carver> CODEC = StringIdentifiable.createCodec(GenerationStep.Carver::values, GenerationStep.Carver::byName);
		private static final Map<String, GenerationStep.Carver> BY_NAME = (Map<String, GenerationStep.Carver>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Carver::getName, carver -> carver));
		private final String name;

		private Carver(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Nullable
		public static GenerationStep.Carver byName(String name) {
			return (GenerationStep.Carver)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static enum Feature implements StringIdentifiable {
		RAW_GENERATION("raw_generation"),
		LAKES("lakes"),
		LOCAL_MODIFICATIONS("local_modifications"),
		UNDERGROUND_STRUCTURES("underground_structures"),
		SURFACE_STRUCTURES("surface_structures"),
		STRONGHOLDS("strongholds"),
		UNDERGROUND_ORES("underground_ores"),
		UNDERGROUND_DECORATION("underground_decoration"),
		FLUID_SPRINGS("fluid_springs"),
		VEGETAL_DECORATION("vegetal_decoration"),
		TOP_LAYER_MODIFICATION("top_layer_modification");

		public static final Codec<GenerationStep.Feature> CODEC = StringIdentifiable.createCodec(GenerationStep.Feature::values, GenerationStep.Feature::get);
		private static final Map<String, GenerationStep.Feature> VALUES = (Map<String, GenerationStep.Feature>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Feature::getName, feature -> feature));
		private final String name;

		private Feature(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Nullable
		public static GenerationStep.Feature get(String name) {
			return (GenerationStep.Feature)VALUES.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
