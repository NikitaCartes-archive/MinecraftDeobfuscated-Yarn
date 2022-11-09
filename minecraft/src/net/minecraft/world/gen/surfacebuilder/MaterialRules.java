package net.minecraft.world.gen.surfacebuilder;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseConfig;

public class MaterialRules {
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR = stoneDepth(0, false, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH = stoneDepth(0, true, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_6 = stoneDepth(0, true, 6, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_30 = stoneDepth(0, true, 30, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_CEILING = stoneDepth(0, false, VerticalSurfaceType.CEILING);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_CEILING_WITH_SURFACE_DEPTH = stoneDepth(0, true, VerticalSurfaceType.CEILING);

	public static MaterialRules.MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, VerticalSurfaceType verticalSurfaceType) {
		return new MaterialRules.StoneDepthMaterialCondition(offset, addSurfaceDepth, 0, verticalSurfaceType);
	}

	public static MaterialRules.MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, int secondaryDepthRange, VerticalSurfaceType verticalSurfaceType) {
		return new MaterialRules.StoneDepthMaterialCondition(offset, addSurfaceDepth, secondaryDepthRange, verticalSurfaceType);
	}

	public static MaterialRules.MaterialCondition not(MaterialRules.MaterialCondition target) {
		return new MaterialRules.NotMaterialCondition(target);
	}

	public static MaterialRules.MaterialCondition aboveY(YOffset anchor, int runDepthMultiplier) {
		return new MaterialRules.AboveYMaterialCondition(anchor, runDepthMultiplier, false);
	}

	public static MaterialRules.MaterialCondition aboveYWithStoneDepth(YOffset anchor, int runDepthMultiplier) {
		return new MaterialRules.AboveYMaterialCondition(anchor, runDepthMultiplier, true);
	}

	public static MaterialRules.MaterialCondition water(int offset, int runDepthMultiplier) {
		return new MaterialRules.WaterMaterialCondition(offset, runDepthMultiplier, false);
	}

	public static MaterialRules.MaterialCondition waterWithStoneDepth(int offset, int runDepthMultiplier) {
		return new MaterialRules.WaterMaterialCondition(offset, runDepthMultiplier, true);
	}

	@SafeVarargs
	public static MaterialRules.MaterialCondition biome(RegistryKey<Biome>... biomes) {
		return biome(List.of(biomes));
	}

	private static MaterialRules.BiomeMaterialCondition biome(List<RegistryKey<Biome>> biomes) {
		return new MaterialRules.BiomeMaterialCondition(biomes);
	}

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min) {
		return noiseThreshold(noise, min, Double.MAX_VALUE);
	}

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min, double max) {
		return new MaterialRules.NoiseThresholdMaterialCondition(noise, min, max);
	}

	public static MaterialRules.MaterialCondition verticalGradient(String id, YOffset trueAtAndBelow, YOffset falseAtAndAbove) {
		return new MaterialRules.VerticalGradientMaterialCondition(new Identifier(id), trueAtAndBelow, falseAtAndAbove);
	}

	public static MaterialRules.MaterialCondition steepSlope() {
		return MaterialRules.SteepMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition hole() {
		return MaterialRules.HoleMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition surface() {
		return MaterialRules.SurfaceMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition temperature() {
		return MaterialRules.TemperatureMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialRule condition(MaterialRules.MaterialCondition condition, MaterialRules.MaterialRule rule) {
		return new MaterialRules.ConditionMaterialRule(condition, rule);
	}

	public static MaterialRules.MaterialRule sequence(MaterialRules.MaterialRule... rules) {
		if (rules.length == 0) {
			throw new IllegalArgumentException("Need at least 1 rule for a sequence");
		} else {
			return new MaterialRules.SequenceMaterialRule(Arrays.asList(rules));
		}
	}

	public static MaterialRules.MaterialRule block(BlockState state) {
		return new MaterialRules.BlockMaterialRule(state);
	}

	public static MaterialRules.MaterialRule terracottaBands() {
		return MaterialRules.TerracottaBandsMaterialRule.INSTANCE;
	}

	static <A> Codec<? extends A> register(Registry<Codec<? extends A>> registry, String id, CodecHolder<? extends A> codecHolder) {
		return Registry.register(registry, id, codecHolder.codec());
	}

	static record AboveYMaterialCondition(YOffset anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.AboveYMaterialCondition> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							YOffset.OFFSET_CODEC.fieldOf("anchor").forGetter(MaterialRules.AboveYMaterialCondition::anchor),
							Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(MaterialRules.AboveYMaterialCondition::surfaceDepthMultiplier),
							Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.AboveYMaterialCondition::addStoneDepth)
						)
						.apply(instance, MaterialRules.AboveYMaterialCondition::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class AboveYPredicate extends MaterialRules.FullLazyAbstractPredicate {
				AboveYPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return this.context.blockY + (AboveYMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0)
						>= AboveYMaterialCondition.this.anchor.getY(this.context.heightContext) + this.context.runDepth * AboveYMaterialCondition.this.surfaceDepthMultiplier;
				}
			}

			return new AboveYPredicate();
		}
	}

	static final class BiomeMaterialCondition implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.BiomeMaterialCondition> CODEC = CodecHolder.of(
			RegistryKey.createCodec(RegistryKeys.BIOME_WORLDGEN)
				.listOf()
				.fieldOf("biome_is")
				.xmap(MaterialRules::biome, biomeMaterialCondition -> biomeMaterialCondition.biomes)
		);
		private final List<RegistryKey<Biome>> biomes;
		final Predicate<RegistryKey<Biome>> predicate;

		BiomeMaterialCondition(List<RegistryKey<Biome>> biomes) {
			this.biomes = biomes;
			this.predicate = Set.copyOf(biomes)::contains;
		}

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class BiomePredicate extends MaterialRules.FullLazyAbstractPredicate {
				BiomePredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return ((RegistryEntry)this.context.biomeSupplier.get()).matches(BiomeMaterialCondition.this.predicate);
				}
			}

			return new BiomePredicate();
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else {
				return object instanceof MaterialRules.BiomeMaterialCondition biomeMaterialCondition ? this.biomes.equals(biomeMaterialCondition.biomes) : false;
			}
		}

		public int hashCode() {
			return this.biomes.hashCode();
		}

		public String toString() {
			return "BiomeConditionSource[biomes=" + this.biomes + "]";
		}
	}

	static record BlockMaterialRule(BlockState resultState, MaterialRules.SimpleBlockStateRule rule) implements MaterialRules.MaterialRule {
		static final CodecHolder<MaterialRules.BlockMaterialRule> CODEC = CodecHolder.of(
			BlockState.CODEC
				.<MaterialRules.BlockMaterialRule>xmap(MaterialRules.BlockMaterialRule::new, MaterialRules.BlockMaterialRule::resultState)
				.fieldOf("result_state")
		);

		BlockMaterialRule(BlockState resultState) {
			this(resultState, new MaterialRules.SimpleBlockStateRule(resultState));
		}

		@Override
		public CodecHolder<? extends MaterialRules.MaterialRule> codec() {
			return CODEC;
		}

		public MaterialRules.BlockStateRule apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return this.rule;
		}
	}

	/**
	 * Returns a {@link BlockState} to generate at a given position, or {@code null}.
	 */
	protected interface BlockStateRule {
		@Nullable
		BlockState tryApply(int x, int y, int z);
	}

	interface BooleanSupplier {
		boolean get();
	}

	static record ConditionMaterialRule(MaterialRules.MaterialCondition ifTrue, MaterialRules.MaterialRule thenRun) implements MaterialRules.MaterialRule {
		static final CodecHolder<MaterialRules.ConditionMaterialRule> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							MaterialRules.MaterialCondition.CODEC.fieldOf("if_true").forGetter(MaterialRules.ConditionMaterialRule::ifTrue),
							MaterialRules.MaterialRule.CODEC.fieldOf("then_run").forGetter(MaterialRules.ConditionMaterialRule::thenRun)
						)
						.apply(instance, MaterialRules.ConditionMaterialRule::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialRule> codec() {
			return CODEC;
		}

		public MaterialRules.BlockStateRule apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return new MaterialRules.ConditionalBlockStateRule(
				(MaterialRules.BooleanSupplier)this.ifTrue.apply(materialRuleContext), (MaterialRules.BlockStateRule)this.thenRun.apply(materialRuleContext)
			);
		}
	}

	/**
	 * Applies another block state rule if the given predicate matches, and returns
	 * {@code null} otherwise.
	 */
	static record ConditionalBlockStateRule(MaterialRules.BooleanSupplier condition, MaterialRules.BlockStateRule followup)
		implements MaterialRules.BlockStateRule {
		@Nullable
		@Override
		public BlockState tryApply(int i, int j, int k) {
			return !this.condition.get() ? null : this.followup.tryApply(i, j, k);
		}
	}

	abstract static class FullLazyAbstractPredicate extends MaterialRules.LazyAbstractPredicate {
		protected FullLazyAbstractPredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
			super(materialRuleContext);
		}

		@Override
		protected long getCurrentUniqueValue() {
			return this.context.uniquePosValue;
		}
	}

	static enum HoleMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final CodecHolder<MaterialRules.HoleMaterialCondition> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.negativeRunDepthPredicate;
		}
	}

	abstract static class HorizontalLazyAbstractPredicate extends MaterialRules.LazyAbstractPredicate {
		protected HorizontalLazyAbstractPredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
			super(materialRuleContext);
		}

		@Override
		protected long getCurrentUniqueValue() {
			return this.context.uniqueHorizontalPosValue;
		}
	}

	static record InvertedBooleanSupplier(MaterialRules.BooleanSupplier target) implements MaterialRules.BooleanSupplier {
		@Override
		public boolean get() {
			return !this.target.get();
		}
	}

	abstract static class LazyAbstractPredicate implements MaterialRules.BooleanSupplier {
		protected final MaterialRules.MaterialRuleContext context;
		private long uniqueValue;
		@Nullable
		Boolean result;

		protected LazyAbstractPredicate(MaterialRules.MaterialRuleContext context) {
			this.context = context;
			this.uniqueValue = this.getCurrentUniqueValue() - 1L;
		}

		@Override
		public boolean get() {
			long l = this.getCurrentUniqueValue();
			if (l == this.uniqueValue) {
				if (this.result == null) {
					throw new IllegalStateException("Update triggered but the result is null");
				} else {
					return this.result;
				}
			} else {
				this.uniqueValue = l;
				this.result = this.test();
				return this.result;
			}
		}

		/**
		 * Returns a unique value for each block position. The result of this predicate
		 * will not be recalculated until this value changes.
		 * 
		 * @return the unique value for this position
		 */
		protected abstract long getCurrentUniqueValue();

		protected abstract boolean test();
	}

	public interface MaterialCondition extends Function<MaterialRules.MaterialRuleContext, MaterialRules.BooleanSupplier> {
		Codec<MaterialRules.MaterialCondition> CODEC = Registries.MATERIAL_CONDITION
			.getCodec()
			.dispatch(materialCondition -> materialCondition.codec().codec(), Function.identity());

		static Codec<? extends MaterialRules.MaterialCondition> registerAndGetDefault(Registry<Codec<? extends MaterialRules.MaterialCondition>> registry) {
			MaterialRules.register(registry, "biome", MaterialRules.BiomeMaterialCondition.CODEC);
			MaterialRules.register(registry, "noise_threshold", MaterialRules.NoiseThresholdMaterialCondition.CODEC);
			MaterialRules.register(registry, "vertical_gradient", MaterialRules.VerticalGradientMaterialCondition.CODEC);
			MaterialRules.register(registry, "y_above", MaterialRules.AboveYMaterialCondition.CODEC);
			MaterialRules.register(registry, "water", MaterialRules.WaterMaterialCondition.CODEC);
			MaterialRules.register(registry, "temperature", MaterialRules.TemperatureMaterialCondition.CODEC);
			MaterialRules.register(registry, "steep", MaterialRules.SteepMaterialCondition.CODEC);
			MaterialRules.register(registry, "not", MaterialRules.NotMaterialCondition.CODEC);
			MaterialRules.register(registry, "hole", MaterialRules.HoleMaterialCondition.CODEC);
			MaterialRules.register(registry, "above_preliminary_surface", MaterialRules.SurfaceMaterialCondition.CODEC);
			return MaterialRules.register(registry, "stone_depth", MaterialRules.StoneDepthMaterialCondition.CODEC);
		}

		CodecHolder<? extends MaterialRules.MaterialCondition> codec();
	}

	public interface MaterialRule extends Function<MaterialRules.MaterialRuleContext, MaterialRules.BlockStateRule> {
		Codec<MaterialRules.MaterialRule> CODEC = Registries.MATERIAL_RULE.getCodec().dispatch(materialRule -> materialRule.codec().codec(), Function.identity());

		static Codec<? extends MaterialRules.MaterialRule> registerAndGetDefault(Registry<Codec<? extends MaterialRules.MaterialRule>> registry) {
			MaterialRules.register(registry, "bandlands", MaterialRules.TerracottaBandsMaterialRule.CODEC);
			MaterialRules.register(registry, "block", MaterialRules.BlockMaterialRule.CODEC);
			MaterialRules.register(registry, "sequence", MaterialRules.SequenceMaterialRule.CODEC);
			return MaterialRules.register(registry, "condition", MaterialRules.ConditionMaterialRule.CODEC);
		}

		CodecHolder<? extends MaterialRules.MaterialRule> codec();
	}

	protected static final class MaterialRuleContext {
		private static final int field_36274 = 8;
		private static final int field_36275 = 4;
		private static final int field_36276 = 16;
		private static final int field_36277 = 15;
		final SurfaceBuilder surfaceBuilder;
		final MaterialRules.BooleanSupplier biomeTemperaturePredicate = new MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate(this);
		final MaterialRules.BooleanSupplier steepSlopePredicate = new MaterialRules.MaterialRuleContext.SteepSlopePredicate(this);
		final MaterialRules.BooleanSupplier negativeRunDepthPredicate = new MaterialRules.MaterialRuleContext.NegativeRunDepthPredicate(this);
		final MaterialRules.BooleanSupplier surfacePredicate = new MaterialRules.MaterialRuleContext.SurfacePredicate();
		final NoiseConfig noiseConfig;
		final Chunk chunk;
		private final ChunkNoiseSampler chunkNoiseSampler;
		private final Function<BlockPos, RegistryEntry<Biome>> posToBiome;
		final HeightContext heightContext;
		private long field_36278 = Long.MAX_VALUE;
		private final int[] field_36279 = new int[4];
		long uniqueHorizontalPosValue = -9223372036854775807L;
		int blockX;
		int blockZ;
		int runDepth;
		private long field_35677 = this.uniqueHorizontalPosValue - 1L;
		private double field_35678;
		private long field_35679 = this.uniqueHorizontalPosValue - 1L;
		private int surfaceMinY;
		long uniquePosValue = -9223372036854775807L;
		final BlockPos.Mutable pos = new BlockPos.Mutable();
		Supplier<RegistryEntry<Biome>> biomeSupplier;
		int blockY;
		int fluidHeight;
		int stoneDepthBelow;
		int stoneDepthAbove;

		protected MaterialRuleContext(
			SurfaceBuilder surfaceBuilder,
			NoiseConfig noiseConfig,
			Chunk chunk,
			ChunkNoiseSampler chunkNoiseSampler,
			Function<BlockPos, RegistryEntry<Biome>> posToBiome,
			Registry<Biome> registry,
			HeightContext heightContext
		) {
			this.surfaceBuilder = surfaceBuilder;
			this.noiseConfig = noiseConfig;
			this.chunk = chunk;
			this.chunkNoiseSampler = chunkNoiseSampler;
			this.posToBiome = posToBiome;
			this.heightContext = heightContext;
		}

		protected void initHorizontalContext(int blockX, int blockZ) {
			this.uniqueHorizontalPosValue++;
			this.uniquePosValue++;
			this.blockX = blockX;
			this.blockZ = blockZ;
			this.runDepth = this.surfaceBuilder.method_39552(blockX, blockZ);
		}

		protected void initVerticalContext(int stoneDepthAbove, int stoneDepthBelow, int fluidHeight, int blockX, int blockY, int blockZ) {
			this.uniquePosValue++;
			this.biomeSupplier = Suppliers.memoize(() -> (RegistryEntry<Biome>)this.posToBiome.apply(this.pos.set(blockX, blockY, blockZ)));
			this.blockY = blockY;
			this.fluidHeight = fluidHeight;
			this.stoneDepthBelow = stoneDepthBelow;
			this.stoneDepthAbove = stoneDepthAbove;
		}

		protected double method_39550() {
			if (this.field_35677 != this.uniqueHorizontalPosValue) {
				this.field_35677 = this.uniqueHorizontalPosValue;
				this.field_35678 = this.surfaceBuilder.method_39555(this.blockX, this.blockZ);
			}

			return this.field_35678;
		}

		private static int method_39903(int i) {
			return i >> 4;
		}

		private static int method_39904(int i) {
			return i << 4;
		}

		protected int method_39551() {
			if (this.field_35679 != this.uniqueHorizontalPosValue) {
				this.field_35679 = this.uniqueHorizontalPosValue;
				int i = method_39903(this.blockX);
				int j = method_39903(this.blockZ);
				long l = ChunkPos.toLong(i, j);
				if (this.field_36278 != l) {
					this.field_36278 = l;
					this.field_36279[0] = this.chunkNoiseSampler.estimateSurfaceHeight(method_39904(i), method_39904(j));
					this.field_36279[1] = this.chunkNoiseSampler.estimateSurfaceHeight(method_39904(i + 1), method_39904(j));
					this.field_36279[2] = this.chunkNoiseSampler.estimateSurfaceHeight(method_39904(i), method_39904(j + 1));
					this.field_36279[3] = this.chunkNoiseSampler.estimateSurfaceHeight(method_39904(i + 1), method_39904(j + 1));
				}

				int k = MathHelper.floor(
					MathHelper.lerp2(
						(double)((float)(this.blockX & 15) / 16.0F),
						(double)((float)(this.blockZ & 15) / 16.0F),
						(double)this.field_36279[0],
						(double)this.field_36279[1],
						(double)this.field_36279[2],
						(double)this.field_36279[3]
					)
				);
				this.surfaceMinY = k + this.runDepth - 8;
			}

			return this.surfaceMinY;
		}

		static class BiomeTemperaturePredicate extends MaterialRules.FullLazyAbstractPredicate {
			BiomeTemperaturePredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
				super(materialRuleContext);
			}

			@Override
			protected boolean test() {
				return ((Biome)((RegistryEntry)this.context.biomeSupplier.get()).value())
					.isCold(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
			}
		}

		static final class NegativeRunDepthPredicate extends MaterialRules.HorizontalLazyAbstractPredicate {
			NegativeRunDepthPredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
				super(materialRuleContext);
			}

			@Override
			protected boolean test() {
				return this.context.runDepth <= 0;
			}
		}

		static class SteepSlopePredicate extends MaterialRules.HorizontalLazyAbstractPredicate {
			SteepSlopePredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
				super(materialRuleContext);
			}

			@Override
			protected boolean test() {
				int i = this.context.blockX & 15;
				int j = this.context.blockZ & 15;
				int k = Math.max(j - 1, 0);
				int l = Math.min(j + 1, 15);
				Chunk chunk = this.context.chunk;
				int m = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, k);
				int n = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, l);
				if (n >= m + 4) {
					return true;
				} else {
					int o = Math.max(i - 1, 0);
					int p = Math.min(i + 1, 15);
					int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, o, j);
					int r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, p, j);
					return q >= r + 4;
				}
			}
		}

		final class SurfacePredicate implements MaterialRules.BooleanSupplier {
			@Override
			public boolean get() {
				return MaterialRuleContext.this.blockY >= MaterialRuleContext.this.method_39551();
			}
		}
	}

	static record NoiseThresholdMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double minThreshold, double maxThreshold)
		implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.NoiseThresholdMaterialCondition> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							RegistryKey.createCodec(RegistryKeys.NOISE_WORLDGEN).fieldOf("noise").forGetter(MaterialRules.NoiseThresholdMaterialCondition::noise),
							Codec.DOUBLE.fieldOf("min_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::minThreshold),
							Codec.DOUBLE.fieldOf("max_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::maxThreshold)
						)
						.apply(instance, MaterialRules.NoiseThresholdMaterialCondition::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.noiseConfig.getOrCreateSampler(this.noise);

			class NoiseThresholdPredicate extends MaterialRules.HorizontalLazyAbstractPredicate {
				NoiseThresholdPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					double d = doublePerlinNoiseSampler.sample((double)this.context.blockX, 0.0, (double)this.context.blockZ);
					return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
				}
			}

			return new NoiseThresholdPredicate();
		}
	}

	static record NotMaterialCondition(MaterialRules.MaterialCondition target) implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.NotMaterialCondition> CODEC = CodecHolder.of(
			MaterialRules.MaterialCondition.CODEC
				.<MaterialRules.NotMaterialCondition>xmap(MaterialRules.NotMaterialCondition::new, MaterialRules.NotMaterialCondition::target)
				.fieldOf("invert")
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return new MaterialRules.InvertedBooleanSupplier((MaterialRules.BooleanSupplier)this.target.apply(materialRuleContext));
		}
	}

	/**
	 * Applies the given block state rules in sequence, and returns the first result that
	 * isn't {@code null}. Returns {@code null} if none of the passed rules match.
	 */
	static record SequenceBlockStateRule(List<MaterialRules.BlockStateRule> rules) implements MaterialRules.BlockStateRule {
		@Nullable
		@Override
		public BlockState tryApply(int i, int j, int k) {
			for (MaterialRules.BlockStateRule blockStateRule : this.rules) {
				BlockState blockState = blockStateRule.tryApply(i, j, k);
				if (blockState != null) {
					return blockState;
				}
			}

			return null;
		}
	}

	static record SequenceMaterialRule(List<MaterialRules.MaterialRule> sequence) implements MaterialRules.MaterialRule {
		static final CodecHolder<MaterialRules.SequenceMaterialRule> CODEC = CodecHolder.of(
			MaterialRules.MaterialRule.CODEC
				.listOf()
				.<MaterialRules.SequenceMaterialRule>xmap(MaterialRules.SequenceMaterialRule::new, MaterialRules.SequenceMaterialRule::sequence)
				.fieldOf("sequence")
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialRule> codec() {
			return CODEC;
		}

		public MaterialRules.BlockStateRule apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			if (this.sequence.size() == 1) {
				return (MaterialRules.BlockStateRule)((MaterialRules.MaterialRule)this.sequence.get(0)).apply(materialRuleContext);
			} else {
				Builder<MaterialRules.BlockStateRule> builder = ImmutableList.builder();

				for (MaterialRules.MaterialRule materialRule : this.sequence) {
					builder.add((MaterialRules.BlockStateRule)materialRule.apply(materialRuleContext));
				}

				return new MaterialRules.SequenceBlockStateRule(builder.build());
			}
		}
	}

	/**
	 * Always returns the given {@link BlockState}.
	 */
	static record SimpleBlockStateRule(BlockState state) implements MaterialRules.BlockStateRule {
		@Override
		public BlockState tryApply(int i, int j, int k) {
			return this.state;
		}
	}

	static enum SteepMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final CodecHolder<MaterialRules.SteepMaterialCondition> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.steepSlopePredicate;
		}
	}

	static record StoneDepthMaterialCondition(int offset, boolean addSurfaceDepth, int secondaryDepthRange, VerticalSurfaceType surfaceType)
		implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.StoneDepthMaterialCondition> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Codec.INT.fieldOf("offset").forGetter(MaterialRules.StoneDepthMaterialCondition::offset),
							Codec.BOOL.fieldOf("add_surface_depth").forGetter(MaterialRules.StoneDepthMaterialCondition::addSurfaceDepth),
							Codec.INT.fieldOf("secondary_depth_range").forGetter(MaterialRules.StoneDepthMaterialCondition::secondaryDepthRange),
							VerticalSurfaceType.CODEC.fieldOf("surface_type").forGetter(MaterialRules.StoneDepthMaterialCondition::surfaceType)
						)
						.apply(instance, MaterialRules.StoneDepthMaterialCondition::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;

			class StoneDepthPredicate extends MaterialRules.FullLazyAbstractPredicate {
				StoneDepthPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					int i = bl ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
					int j = StoneDepthMaterialCondition.this.addSurfaceDepth ? this.context.runDepth : 0;
					int k = StoneDepthMaterialCondition.this.secondaryDepthRange == 0
						? 0
						: (int)MathHelper.map(this.context.method_39550(), -1.0, 1.0, 0.0, (double)StoneDepthMaterialCondition.this.secondaryDepthRange);
					return i <= 1 + StoneDepthMaterialCondition.this.offset + j + k;
				}
			}

			return new StoneDepthPredicate();
		}
	}

	static enum SurfaceMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final CodecHolder<MaterialRules.SurfaceMaterialCondition> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.surfacePredicate;
		}
	}

	static enum TemperatureMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final CodecHolder<MaterialRules.TemperatureMaterialCondition> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.biomeTemperaturePredicate;
		}
	}

	static enum TerracottaBandsMaterialRule implements MaterialRules.MaterialRule {
		INSTANCE;

		static final CodecHolder<MaterialRules.TerracottaBandsMaterialRule> CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

		@Override
		public CodecHolder<? extends MaterialRules.MaterialRule> codec() {
			return CODEC;
		}

		public MaterialRules.BlockStateRule apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.surfaceBuilder::getTerracottaBlock;
		}
	}

	static record VerticalGradientMaterialCondition(Identifier randomName, YOffset trueAtAndBelow, YOffset falseAtAndAbove)
		implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.VerticalGradientMaterialCondition> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Identifier.CODEC.fieldOf("random_name").forGetter(MaterialRules.VerticalGradientMaterialCondition::randomName),
							YOffset.OFFSET_CODEC.fieldOf("true_at_and_below").forGetter(MaterialRules.VerticalGradientMaterialCondition::trueAtAndBelow),
							YOffset.OFFSET_CODEC.fieldOf("false_at_and_above").forGetter(MaterialRules.VerticalGradientMaterialCondition::falseAtAndAbove)
						)
						.apply(instance, MaterialRules.VerticalGradientMaterialCondition::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final int i = this.trueAtAndBelow().getY(materialRuleContext.heightContext);
			final int j = this.falseAtAndAbove().getY(materialRuleContext.heightContext);
			final RandomSplitter randomSplitter = materialRuleContext.noiseConfig.getOrCreateRandomDeriver(this.randomName());

			class VerticalGradientPredicate extends MaterialRules.FullLazyAbstractPredicate {
				VerticalGradientPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					int i = this.context.blockY;
					if (i <= i) {
						return true;
					} else if (i >= j) {
						return false;
					} else {
						double d = MathHelper.map((double)i, (double)i, (double)j, 1.0, 0.0);
						Random random = randomSplitter.split(this.context.blockX, i, this.context.blockZ);
						return (double)random.nextFloat() < d;
					}
				}
			}

			return new VerticalGradientPredicate();
		}
	}

	static record WaterMaterialCondition(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialRules.MaterialCondition {
		static final CodecHolder<MaterialRules.WaterMaterialCondition> CODEC = CodecHolder.of(
			RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Codec.INT.fieldOf("offset").forGetter(MaterialRules.WaterMaterialCondition::offset),
							Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(MaterialRules.WaterMaterialCondition::surfaceDepthMultiplier),
							Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.WaterMaterialCondition::addStoneDepth)
						)
						.apply(instance, MaterialRules.WaterMaterialCondition::new)
			)
		);

		@Override
		public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
			return CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class WaterPredicate extends MaterialRules.FullLazyAbstractPredicate {
				WaterPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return this.context.fluidHeight == Integer.MIN_VALUE
						|| this.context.blockY + (WaterMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0)
							>= this.context.fluidHeight + WaterMaterialCondition.this.offset + this.context.runDepth * WaterMaterialCondition.this.surfaceDepthMultiplier;
				}
			}

			return new WaterPredicate();
		}
	}
}
