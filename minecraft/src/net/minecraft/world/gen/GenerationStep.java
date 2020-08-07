package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;

public class GenerationStep {
	public static enum Carver implements StringIdentifiable {
		field_13169("air"),
		field_13166("liquid");

		public static final Codec<GenerationStep.Carver> field_24770 = StringIdentifiable.createCodec(
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

	public static enum Feature {
		field_13174,
		field_25186,
		field_13171,
		field_13172,
		field_13173,
		field_25187,
		field_13176,
		field_13177,
		field_13178,
		field_13179;
	}
}
