package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public class GenerationStep {
	public static enum Carver implements StringIdentifiable {
		AIR("air"),
		LIQUID("liquid");

		public static final Codec<GenerationStep.Carver> CODEC = StringIdentifiable.createCodec(GenerationStep.Carver::values);
		private final String name;

		private Carver(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
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

		public static final Codec<GenerationStep.Feature> CODEC = StringIdentifiable.createCodec(GenerationStep.Feature::values);
		private final String name;

		private Feature(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
