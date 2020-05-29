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

		public static final Codec<GenerationStep.Carver> field_24770 = StringIdentifiable.method_28140(
			GenerationStep.Carver::values, GenerationStep.Carver::method_28546
		);
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
		public static GenerationStep.Carver method_28546(String string) {
			return (GenerationStep.Carver)BY_NAME.get(string);
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
		VEGETAL_DECORATION("vegetal_decoration"),
		TOP_LAYER_MODIFICATION("top_layer_modification");

		public static final Codec<GenerationStep.Feature> field_24771 = StringIdentifiable.method_28140(
			GenerationStep.Feature::values, GenerationStep.Feature::method_28547
		);
		private static final Map<String, GenerationStep.Feature> BY_NAME = (Map<String, GenerationStep.Feature>)Arrays.stream(values())
			.collect(Collectors.toMap(GenerationStep.Feature::getName, feature -> feature));
		private final String name;

		private Feature(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static GenerationStep.Feature method_28547(String string) {
			return (GenerationStep.Feature)BY_NAME.get(string);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
