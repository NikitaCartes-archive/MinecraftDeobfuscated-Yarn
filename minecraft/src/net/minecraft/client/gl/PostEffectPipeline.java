package net.minecraft.client.gl;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record PostEffectPipeline(Map<Identifier, PostEffectPipeline.Targets> internalTargets, List<PostEffectPipeline.Pass> passes) {
	public static final Codec<PostEffectPipeline> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.unboundedMap(Identifier.CODEC, PostEffectPipeline.Targets.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostEffectPipeline::internalTargets),
					PostEffectPipeline.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostEffectPipeline::passes)
				)
				.apply(instance, PostEffectPipeline::new)
	);

	@Environment(EnvType.CLIENT)
	public static record CustomSized(int width, int height) implements PostEffectPipeline.Targets {
		public static final Codec<PostEffectPipeline.CustomSized> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.POSITIVE_INT.fieldOf("width").forGetter(PostEffectPipeline.CustomSized::width),
						Codecs.POSITIVE_INT.fieldOf("height").forGetter(PostEffectPipeline.CustomSized::height)
					)
					.apply(instance, PostEffectPipeline.CustomSized::new)
		);
	}

	@Environment(EnvType.CLIENT)
	public sealed interface Input permits PostEffectPipeline.TextureSampler, PostEffectPipeline.TargetSampler {
		Codec<PostEffectPipeline.Input> field_53114 = Codec.xor(PostEffectPipeline.TextureSampler.CODEC, PostEffectPipeline.TargetSampler.CODEC)
			.xmap(either -> either.map(Function.identity(), Function.identity()), input -> {
				Objects.requireNonNull(input);

				return switch (input) {
					case PostEffectPipeline.TextureSampler textureSampler -> Either.left(textureSampler);
					case PostEffectPipeline.TargetSampler targetSampler -> Either.right(targetSampler);
					default -> throw new MatchException(null, null);
				};
			});

		String samplerName();

		Set<Identifier> getTargetId();
	}

	@Environment(EnvType.CLIENT)
	public static record Pass(String name, List<PostEffectPipeline.Input> inputs, Identifier outputTarget, List<PostEffectPipeline.Uniform> uniforms) {
		private static final Codec<List<PostEffectPipeline.Input>> field_53117 = PostEffectPipeline.Input.field_53114.listOf().validate(list -> {
			Set<String> set = new ObjectArraySet<>(list.size());

			for (PostEffectPipeline.Input input : list) {
				if (!set.add(input.samplerName())) {
					return DataResult.error(() -> "Encountered repeated sampler name: " + input.samplerName());
				}
			}

			return DataResult.success(list);
		});
		public static final Codec<PostEffectPipeline.Pass> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(PostEffectPipeline.Pass::name),
						field_53117.optionalFieldOf("inputs", List.of()).forGetter(PostEffectPipeline.Pass::inputs),
						Identifier.CODEC.fieldOf("output").forGetter(PostEffectPipeline.Pass::outputTarget),
						PostEffectPipeline.Uniform.field_53120.listOf().optionalFieldOf("uniforms", List.of()).forGetter(PostEffectPipeline.Pass::uniforms)
					)
					.apply(instance, PostEffectPipeline.Pass::new)
		);
	}

	@Environment(EnvType.CLIENT)
	public static record ScreenSized() implements PostEffectPipeline.Targets {
		public static final Codec<PostEffectPipeline.ScreenSized> CODEC = Codec.unit(PostEffectPipeline.ScreenSized::new);
	}

	@Environment(EnvType.CLIENT)
	public static record TargetSampler(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements PostEffectPipeline.Input {
		public static final Codec<PostEffectPipeline.TargetSampler> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("sampler_name").forGetter(PostEffectPipeline.TargetSampler::samplerName),
						Identifier.CODEC.fieldOf("target").forGetter(PostEffectPipeline.TargetSampler::targetId),
						Codec.BOOL.optionalFieldOf("use_depth_buffer", Boolean.valueOf(false)).forGetter(PostEffectPipeline.TargetSampler::useDepthBuffer),
						Codec.BOOL.optionalFieldOf("bilinear", Boolean.valueOf(false)).forGetter(PostEffectPipeline.TargetSampler::bilinear)
					)
					.apply(instance, PostEffectPipeline.TargetSampler::new)
		);

		@Override
		public Set<Identifier> getTargetId() {
			return Set.of(this.targetId);
		}
	}

	@Environment(EnvType.CLIENT)
	public sealed interface Targets permits PostEffectPipeline.ScreenSized, PostEffectPipeline.CustomSized {
		Codec<PostEffectPipeline.Targets> CODEC = Codec.xor(PostEffectPipeline.ScreenSized.CODEC, PostEffectPipeline.CustomSized.CODEC)
			.xmap(either -> either.map(Function.identity(), Function.identity()), targets -> {
				Objects.requireNonNull(targets);

				return switch (targets) {
					case PostEffectPipeline.ScreenSized screenSized -> Either.left(screenSized);
					case PostEffectPipeline.CustomSized customSized -> Either.right(customSized);
					default -> throw new MatchException(null, null);
				};
			});
	}

	@Environment(EnvType.CLIENT)
	public static record TextureSampler(String samplerName, Identifier location, int width, int height, boolean bilinear) implements PostEffectPipeline.Input {
		public static final Codec<PostEffectPipeline.TextureSampler> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("sampler_name").forGetter(PostEffectPipeline.TextureSampler::samplerName),
						Identifier.CODEC.fieldOf("location").forGetter(PostEffectPipeline.TextureSampler::location),
						Codecs.POSITIVE_INT.fieldOf("width").forGetter(PostEffectPipeline.TextureSampler::width),
						Codecs.POSITIVE_INT.fieldOf("height").forGetter(PostEffectPipeline.TextureSampler::height),
						Codec.BOOL.optionalFieldOf("bilinear", Boolean.valueOf(false)).forGetter(PostEffectPipeline.TextureSampler::bilinear)
					)
					.apply(instance, PostEffectPipeline.TextureSampler::new)
		);

		@Override
		public Set<Identifier> getTargetId() {
			return Set.of();
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Uniform(String name, List<Float> values) {
		public static final Codec<PostEffectPipeline.Uniform> field_53120 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(PostEffectPipeline.Uniform::name),
						Codec.FLOAT.sizeLimitedListOf(4).fieldOf("values").forGetter(PostEffectPipeline.Uniform::values)
					)
					.apply(instance, PostEffectPipeline.Uniform::new)
		);
	}
}
