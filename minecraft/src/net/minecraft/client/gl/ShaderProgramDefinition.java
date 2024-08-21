package net.minecraft.client.gl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ShaderProgramDefinition(
	Identifier vertex, Identifier fragment, List<ShaderProgramDefinition.Sampler> samplers, List<ShaderProgramDefinition.Uniform> uniforms, Defines defines
) {
	public static final Codec<ShaderProgramDefinition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("vertex").forGetter(ShaderProgramDefinition::vertex),
					Identifier.CODEC.fieldOf("fragment").forGetter(ShaderProgramDefinition::fragment),
					ShaderProgramDefinition.Sampler.CODEC.listOf().optionalFieldOf("samplers", List.of()).forGetter(ShaderProgramDefinition::samplers),
					ShaderProgramDefinition.Uniform.CODEC.listOf().optionalFieldOf("uniforms", List.of()).forGetter(ShaderProgramDefinition::uniforms),
					Defines.CODEC.optionalFieldOf("defines", Defines.EMPTY).forGetter(ShaderProgramDefinition::defines)
				)
				.apply(instance, ShaderProgramDefinition::new)
	);

	@Environment(EnvType.CLIENT)
	public static record Sampler(String name) {
		public static final Codec<ShaderProgramDefinition.Sampler> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codec.STRING.fieldOf("name").forGetter(ShaderProgramDefinition.Sampler::name))
					.apply(instance, ShaderProgramDefinition.Sampler::new)
		);
	}

	@Environment(EnvType.CLIENT)
	public static record Uniform(String name, String type, int count, List<Float> values) {
		public static final Codec<ShaderProgramDefinition.Uniform> CODEC = RecordCodecBuilder.<ShaderProgramDefinition.Uniform>create(
				instance -> instance.group(
							Codec.STRING.fieldOf("name").forGetter(ShaderProgramDefinition.Uniform::name),
							Codec.STRING.fieldOf("type").forGetter(ShaderProgramDefinition.Uniform::type),
							Codec.INT.fieldOf("count").forGetter(ShaderProgramDefinition.Uniform::count),
							Codec.FLOAT.listOf().fieldOf("values").forGetter(ShaderProgramDefinition.Uniform::values)
						)
						.apply(instance, ShaderProgramDefinition.Uniform::new)
			)
			.validate(ShaderProgramDefinition.Uniform::validate);

		private static DataResult<ShaderProgramDefinition.Uniform> validate(ShaderProgramDefinition.Uniform uniform) {
			int i = uniform.count;
			int j = uniform.values.size();
			return j != i && j > 1
				? DataResult.error(() -> "Invalid amount of uniform values specified (expected " + i + ", found " + j + ")")
				: DataResult.success(uniform);
		}
	}
}
