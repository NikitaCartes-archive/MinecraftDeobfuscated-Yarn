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

	public static enum Feature {
		RAW_GENERATION,
		LAKES,
		LOCAL_MODIFICATIONS,
		UNDERGROUND_STRUCTURES,
		SURFACE_STRUCTURES,
		STRONGHOLDS,
		UNDERGROUND_ORES,
		UNDERGROUND_DECORATION,
		VEGETAL_DECORATION,
		TOP_LAYER_MODIFICATION;
	}
}
