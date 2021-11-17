package net.minecraft.world.gen.surfacebuilder;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class MaterialRules {
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR = method_39549(0, false, false, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR_WITH_RUN_DEPTH = method_39549(0, true, false, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition field_35494 = method_39549(0, false, false, VerticalSurfaceType.CEILING);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_CEILING = method_39549(0, true, false, VerticalSurfaceType.CEILING);

	public static MaterialRules.MaterialCondition method_39549(int i, boolean bl, boolean bl2, VerticalSurfaceType verticalSurfaceType) {
		return new MaterialRules.StoneDepthMaterialCondition(i, bl, bl2, verticalSurfaceType);
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

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d) {
		return noiseThreshold(registryKey, d, Double.MAX_VALUE);
	}

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
		return new MaterialRules.NoiseThresholdMaterialCondition(registryKey, d, e);
	}

	public static MaterialRules.MaterialCondition method_39472(String string, YOffset yOffset, YOffset yOffset2) {
		return new MaterialRules.VerticalGradientMaterialCondition(new Identifier(string), yOffset, yOffset2);
	}

	public static MaterialRules.MaterialCondition steepSlope() {
		return MaterialRules.SteepMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition hole() {
		return MaterialRules.HoleMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition method_39473() {
		return MaterialRules.SurfaceMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition temperature() {
		return MaterialRules.TemperatureMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialRule condition(MaterialRules.MaterialCondition condition, MaterialRules.MaterialRule rule) {
		return new MaterialRules.ConditionMaterialRule(condition, rule);
	}

	public static MaterialRules.MaterialRule sequence(MaterialRules.MaterialRule firstRule, MaterialRules.MaterialRule... rules) {
		return new MaterialRules.SequenceMaterialRule(Stream.concat(Stream.of(firstRule), Arrays.stream(rules)).toList());
	}

	public static MaterialRules.MaterialRule block(BlockState state) {
		return new MaterialRules.BlockMaterialRule(state);
	}

	public static MaterialRules.MaterialRule terracottaBands() {
		return MaterialRules.TerracottaBandsMaterialRule.INSTANCE;
	}

	static record AboveYMaterialCondition() implements MaterialRules.MaterialCondition {
		final YOffset anchor;
		final int surfaceDepthMultiplier;
		final boolean addStoneDepth;
		static final Codec<MaterialRules.AboveYMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						YOffset.OFFSET_CODEC.fieldOf("anchor").forGetter(MaterialRules.AboveYMaterialCondition::anchor),
						Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(MaterialRules.AboveYMaterialCondition::surfaceDepthMultiplier),
						Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.AboveYMaterialCondition::addStoneDepth)
					)
					.apply(instance, MaterialRules.AboveYMaterialCondition::new)
		);

		AboveYMaterialCondition(YOffset yOffset, int i, boolean bl) {
			this.anchor = yOffset;
			this.surfaceDepthMultiplier = i;
			this.addStoneDepth = bl;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class AboveYPredicate extends MaterialRules.FullLazyAbstractPredicate {
				AboveYPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return this.context.y + (AboveYMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0)
						>= AboveYMaterialCondition.this.anchor.getY(this.context.heightContext) + this.context.runDepth * AboveYMaterialCondition.this.surfaceDepthMultiplier;
				}
			}

			return new AboveYPredicate();
		}
	}

	static record BiomeMaterialCondition() implements MaterialRules.MaterialCondition {
		private final List<RegistryKey<Biome>> biomes;
		static final Codec<MaterialRules.BiomeMaterialCondition> CONDITION_CODEC = RegistryKey.createCodec(Registry.BIOME_KEY)
			.listOf()
			.fieldOf("biome_is")
			.<MaterialRules.BiomeMaterialCondition>xmap(MaterialRules::biome, MaterialRules.BiomeMaterialCondition::biomes)
			.codec();

		BiomeMaterialCondition(List<RegistryKey<Biome>> list) {
			this.biomes = list;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final Set<RegistryKey<Biome>> set = Set.copyOf(this.biomes);

			class BiomePredicate extends MaterialRules.FullLazyAbstractPredicate {
				BiomePredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return set.contains(this.context.biomeKeySupplier.get());
				}
			}

			return new BiomePredicate();
		}
	}

	static record BlockMaterialRule() implements MaterialRules.MaterialRule {
		private final BlockState resultState;
		private final MaterialRules.SimpleBlockStateRule rule;
		static final Codec<MaterialRules.BlockMaterialRule> RULE_CODEC = BlockState.CODEC
			.<MaterialRules.BlockMaterialRule>xmap(MaterialRules.BlockMaterialRule::new, MaterialRules.BlockMaterialRule::resultState)
			.fieldOf("result_state")
			.codec();

		BlockMaterialRule(BlockState resultState) {
			this(resultState, new MaterialRules.SimpleBlockStateRule(resultState));
		}

		private BlockMaterialRule(BlockState blockState, MaterialRules.SimpleBlockStateRule simpleBlockStateRule) {
			this.resultState = blockState;
			this.rule = simpleBlockStateRule;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialRule> codec() {
			return RULE_CODEC;
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

	static record ConditionMaterialRule() implements MaterialRules.MaterialRule {
		private final MaterialRules.MaterialCondition ifTrue;
		private final MaterialRules.MaterialRule thenRun;
		static final Codec<MaterialRules.ConditionMaterialRule> RULE_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						MaterialRules.MaterialCondition.CODEC.fieldOf("if_true").forGetter(MaterialRules.ConditionMaterialRule::ifTrue),
						MaterialRules.MaterialRule.CODEC.fieldOf("then_run").forGetter(MaterialRules.ConditionMaterialRule::thenRun)
					)
					.apply(instance, MaterialRules.ConditionMaterialRule::new)
		);

		ConditionMaterialRule(MaterialRules.MaterialCondition materialCondition, MaterialRules.MaterialRule materialRule) {
			this.ifTrue = materialCondition;
			this.thenRun = materialRule;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialRule> codec() {
			return RULE_CODEC;
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
	static record ConditionalBlockStateRule() implements MaterialRules.BlockStateRule {
		private final MaterialRules.BooleanSupplier condition;
		private final MaterialRules.BlockStateRule followup;

		ConditionalBlockStateRule(MaterialRules.BooleanSupplier booleanSupplier, MaterialRules.BlockStateRule blockStateRule) {
			this.condition = booleanSupplier;
			this.followup = blockStateRule;
		}

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

		static final Codec<MaterialRules.HoleMaterialCondition> CONDITION_CODEC = Codec.unit(INSTANCE);

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
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

	static record InvertedBooleanSupplier() implements MaterialRules.BooleanSupplier {
		private final MaterialRules.BooleanSupplier target;

		InvertedBooleanSupplier(MaterialRules.BooleanSupplier booleanSupplier) {
			this.target = booleanSupplier;
		}

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
		Codec<MaterialRules.MaterialCondition> CODEC = Registry.MATERIAL_CONDITION.getCodec().dispatch(MaterialRules.MaterialCondition::codec, Function.identity());

		static Codec<? extends MaterialRules.MaterialCondition> registerAndGetDefault() {
			Registry.register(Registry.MATERIAL_CONDITION, "biome", MaterialRules.BiomeMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "noise_threshold", MaterialRules.NoiseThresholdMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "vertical_gradient", MaterialRules.VerticalGradientMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "y_above", MaterialRules.AboveYMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "water", MaterialRules.WaterMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "temperature", MaterialRules.TemperatureMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "steep", MaterialRules.SteepMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "not", MaterialRules.NotMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "hole", MaterialRules.HoleMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "above_preliminary_surface", MaterialRules.SurfaceMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "stone_depth", MaterialRules.StoneDepthMaterialCondition.CONDITION_CODEC);
			return (Codec<? extends MaterialRules.MaterialCondition>)Registry.MATERIAL_CONDITION.iterator().next();
		}

		Codec<? extends MaterialRules.MaterialCondition> codec();
	}

	public interface MaterialRule extends Function<MaterialRules.MaterialRuleContext, MaterialRules.BlockStateRule> {
		Codec<MaterialRules.MaterialRule> CODEC = Registry.MATERIAL_RULE.getCodec().dispatch(MaterialRules.MaterialRule::codec, Function.identity());

		static Codec<? extends MaterialRules.MaterialRule> registerAndGetDefault() {
			Registry.register(Registry.MATERIAL_RULE, "bandlands", MaterialRules.TerracottaBandsMaterialRule.RULE_CODEC);
			Registry.register(Registry.MATERIAL_RULE, "block", MaterialRules.BlockMaterialRule.RULE_CODEC);
			Registry.register(Registry.MATERIAL_RULE, "sequence", MaterialRules.SequenceMaterialRule.RULE_CODEC);
			Registry.register(Registry.MATERIAL_RULE, "condition", MaterialRules.ConditionMaterialRule.RULE_CODEC);
			return (Codec<? extends MaterialRules.MaterialRule>)Registry.MATERIAL_RULE.iterator().next();
		}

		Codec<? extends MaterialRules.MaterialRule> codec();
	}

	protected static final class MaterialRuleContext {
		final SurfaceBuilder surfaceBuilder;
		final MaterialRules.BooleanSupplier biomeTemperaturePredicate = new MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate(this);
		final MaterialRules.BooleanSupplier steepSlopePredicate = new MaterialRules.MaterialRuleContext.SteepSlopePredicate(this);
		final MaterialRules.BooleanSupplier negativeRunDepthPredicate = new MaterialRules.MaterialRuleContext.NegativeRunDepthPredicate(this);
		final MaterialRules.BooleanSupplier surfacePredicate = new MaterialRules.MaterialRuleContext.SurfacePredicate();
		final Chunk chunk;
		private final ChunkNoiseSampler field_35676;
		private final Function<BlockPos, Biome> posToBiome;
		private final Registry<Biome> biomeRegistry;
		final HeightContext heightContext;
		long uniqueHorizontalPosValue = -9223372036854775807L;
		int x;
		int z;
		int runDepth;
		private long field_35677 = this.uniqueHorizontalPosValue - 1L;
		private int field_35678;
		private long field_35679 = this.uniqueHorizontalPosValue - 1L;
		private int surfaceMinY;
		long uniquePosValue = -9223372036854775807L;
		final BlockPos.Mutable pos = new BlockPos.Mutable();
		Supplier<Biome> biomeSupplier;
		Supplier<RegistryKey<Biome>> biomeKeySupplier;
		int y;
		int fluidHeight;
		int stoneDepthBelow;
		int stoneDepthAbove;

		protected MaterialRuleContext(
			SurfaceBuilder surfaceBuilder,
			Chunk chunk,
			ChunkNoiseSampler chunkNoiseSampler,
			Function<BlockPos, Biome> function,
			Registry<Biome> registry,
			HeightContext heightContext
		) {
			this.surfaceBuilder = surfaceBuilder;
			this.chunk = chunk;
			this.field_35676 = chunkNoiseSampler;
			this.posToBiome = function;
			this.biomeRegistry = registry;
			this.heightContext = heightContext;
		}

		protected void initHorizontalContext(int x, int z) {
			this.uniqueHorizontalPosValue++;
			this.uniquePosValue++;
			this.x = x;
			this.z = z;
			this.runDepth = this.surfaceBuilder.method_39552(x, z);
		}

		protected void initVerticalContext(int i, int j, int k, int l, int m, int n) {
			this.uniquePosValue++;
			this.biomeSupplier = Suppliers.memoize(() -> (Biome)this.posToBiome.apply(this.pos.set(l, m, n)));
			this.biomeKeySupplier = Suppliers.memoize(
				() -> (RegistryKey<Biome>)this.biomeRegistry
						.getKey((Biome)this.biomeSupplier.get())
						.orElseThrow(() -> new IllegalStateException("Unregistered biome: " + this.biomeSupplier))
			);
			this.y = m;
			this.fluidHeight = k;
			this.stoneDepthBelow = j;
			this.stoneDepthAbove = i;
		}

		protected int method_39550() {
			if (this.field_35677 != this.uniqueHorizontalPosValue) {
				this.field_35677 = this.uniqueHorizontalPosValue;
				this.field_35678 = this.surfaceBuilder.method_39555(this.x, this.z);
			}

			return this.field_35678;
		}

		protected int method_39551() {
			if (this.field_35679 != this.uniqueHorizontalPosValue) {
				this.field_35679 = this.uniqueHorizontalPosValue;
				this.surfaceMinY = this.surfaceBuilder.method_39553(this.field_35676, this.x, this.z);
			}

			return this.surfaceMinY;
		}

		static class BiomeTemperaturePredicate extends MaterialRules.FullLazyAbstractPredicate {
			BiomeTemperaturePredicate(MaterialRules.MaterialRuleContext materialRuleContext) {
				super(materialRuleContext);
			}

			@Override
			protected boolean test() {
				return ((Biome)this.context.biomeSupplier.get()).getTemperature(this.context.pos.set(this.context.x, this.context.y, this.context.z)) < 0.15F;
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
				int i = this.context.x & 15;
				int j = this.context.z & 15;
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
				return MaterialRuleContext.this.y >= MaterialRuleContext.this.method_39551();
			}
		}
	}

	static record NoiseThresholdMaterialCondition() implements MaterialRules.MaterialCondition {
		private final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise;
		final double minThreshold;
		final double maxThreshold;
		static final Codec<MaterialRules.NoiseThresholdMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryKey.createCodec(Registry.NOISE_WORLDGEN).fieldOf("noise").forGetter(MaterialRules.NoiseThresholdMaterialCondition::noise),
						Codec.DOUBLE.fieldOf("min_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::minThreshold),
						Codec.DOUBLE.fieldOf("max_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::maxThreshold)
					)
					.apply(instance, MaterialRules.NoiseThresholdMaterialCondition::new)
		);

		NoiseThresholdMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
			this.noise = registryKey;
			this.minThreshold = d;
			this.maxThreshold = e;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.surfaceBuilder.getNoiseSampler(this.noise);

			class NoiseThresholdPredicate extends MaterialRules.HorizontalLazyAbstractPredicate {
				NoiseThresholdPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					double d = doublePerlinNoiseSampler.sample((double)this.context.x, 0.0, (double)this.context.z);
					return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
				}
			}

			return new NoiseThresholdPredicate();
		}
	}

	static record NotMaterialCondition() implements MaterialRules.MaterialCondition {
		private final MaterialRules.MaterialCondition target;
		static final Codec<MaterialRules.NotMaterialCondition> CONDITION_CODEC = MaterialRules.MaterialCondition.CODEC
			.<MaterialRules.NotMaterialCondition>xmap(MaterialRules.NotMaterialCondition::new, MaterialRules.NotMaterialCondition::target)
			.fieldOf("invert")
			.codec();

		NotMaterialCondition(MaterialRules.MaterialCondition materialCondition) {
			this.target = materialCondition;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return new MaterialRules.InvertedBooleanSupplier((MaterialRules.BooleanSupplier)this.target.apply(materialRuleContext));
		}
	}

	/**
	 * Applies the given block state rules in sequence, and returns the first result that
	 * isn't {@code null}. Returns {@code null} if none of the passed rules match.
	 */
	static record SequenceBlockStateRule() implements MaterialRules.BlockStateRule {
		private final List<MaterialRules.BlockStateRule> rules;

		SequenceBlockStateRule(List<MaterialRules.BlockStateRule> list) {
			this.rules = list;
		}

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

	static record SequenceMaterialRule() implements MaterialRules.MaterialRule {
		private final List<MaterialRules.MaterialRule> sequence;
		static final Codec<MaterialRules.SequenceMaterialRule> RULE_CODEC = MaterialRules.MaterialRule.CODEC
			.listOf()
			.<MaterialRules.SequenceMaterialRule>xmap(MaterialRules.SequenceMaterialRule::new, MaterialRules.SequenceMaterialRule::sequence)
			.fieldOf("sequence")
			.codec();

		SequenceMaterialRule(List<MaterialRules.MaterialRule> list) {
			this.sequence = list;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialRule> codec() {
			return RULE_CODEC;
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
	static record SimpleBlockStateRule() implements MaterialRules.BlockStateRule {
		private final BlockState state;

		SimpleBlockStateRule(BlockState blockState) {
			this.state = blockState;
		}

		@Override
		public BlockState tryApply(int i, int j, int k) {
			return this.state;
		}
	}

	static enum SteepMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final Codec<MaterialRules.SteepMaterialCondition> CONDITION_CODEC = Codec.unit(INSTANCE);

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.steepSlopePredicate;
		}
	}

	static record StoneDepthMaterialCondition() implements MaterialRules.MaterialCondition {
		final int offset;
		final boolean addSurfaceDepth;
		final boolean addSurfaceSecondaryDepth;
		private final VerticalSurfaceType surfaceType;
		static final Codec<MaterialRules.StoneDepthMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("offset").forGetter(MaterialRules.StoneDepthMaterialCondition::offset),
						Codec.BOOL.fieldOf("add_surface_depth").forGetter(MaterialRules.StoneDepthMaterialCondition::addSurfaceDepth),
						Codec.BOOL.fieldOf("add_surface_secondary_depth").forGetter(MaterialRules.StoneDepthMaterialCondition::addSurfaceSecondaryDepth),
						VerticalSurfaceType.CODEC.fieldOf("surface_type").forGetter(MaterialRules.StoneDepthMaterialCondition::surfaceType)
					)
					.apply(instance, MaterialRules.StoneDepthMaterialCondition::new)
		);

		StoneDepthMaterialCondition(int i, boolean bl, boolean bl2, VerticalSurfaceType verticalSurfaceType) {
			this.offset = i;
			this.addSurfaceDepth = bl;
			this.addSurfaceSecondaryDepth = bl2;
			this.surfaceType = verticalSurfaceType;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;

			class StoneDepthPredicate extends MaterialRules.FullLazyAbstractPredicate {
				StoneDepthPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return (bl ? this.context.stoneDepthBelow : this.context.stoneDepthAbove)
						<= 1
							+ StoneDepthMaterialCondition.this.offset
							+ (StoneDepthMaterialCondition.this.addSurfaceDepth ? this.context.runDepth : 0)
							+ (StoneDepthMaterialCondition.this.addSurfaceSecondaryDepth ? this.context.method_39550() : 0);
				}
			}

			return new StoneDepthPredicate();
		}
	}

	static enum SurfaceMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final Codec<MaterialRules.SurfaceMaterialCondition> CONDITION_CODEC = Codec.unit(INSTANCE);

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.surfacePredicate;
		}
	}

	static enum TemperatureMaterialCondition implements MaterialRules.MaterialCondition {
		INSTANCE;

		static final Codec<MaterialRules.TemperatureMaterialCondition> CONDITION_CODEC = Codec.unit(INSTANCE);

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.biomeTemperaturePredicate;
		}
	}

	static enum TerracottaBandsMaterialRule implements MaterialRules.MaterialRule {
		INSTANCE;

		static final Codec<MaterialRules.TerracottaBandsMaterialRule> RULE_CODEC = Codec.unit(INSTANCE);

		@Override
		public Codec<? extends MaterialRules.MaterialRule> codec() {
			return RULE_CODEC;
		}

		public MaterialRules.BlockStateRule apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			return materialRuleContext.surfaceBuilder::getTerracottaBlock;
		}
	}

	static record VerticalGradientMaterialCondition() implements MaterialRules.MaterialCondition {
		private final Identifier randomName;
		private final YOffset trueAtAndBelow;
		private final YOffset falseAtAndAbove;
		static final Codec<MaterialRules.VerticalGradientMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Identifier.CODEC.fieldOf("random_name").forGetter(MaterialRules.VerticalGradientMaterialCondition::randomName),
						YOffset.OFFSET_CODEC.fieldOf("true_at_and_below").forGetter(MaterialRules.VerticalGradientMaterialCondition::trueAtAndBelow),
						YOffset.OFFSET_CODEC.fieldOf("false_at_and_above").forGetter(MaterialRules.VerticalGradientMaterialCondition::falseAtAndAbove)
					)
					.apply(instance, MaterialRules.VerticalGradientMaterialCondition::new)
		);

		VerticalGradientMaterialCondition(Identifier identifier, YOffset yOffset, YOffset yOffset2) {
			this.randomName = identifier;
			this.trueAtAndBelow = yOffset;
			this.falseAtAndAbove = yOffset2;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final int i = this.trueAtAndBelow().getY(materialRuleContext.heightContext);
			final int j = this.falseAtAndAbove().getY(materialRuleContext.heightContext);
			final RandomDeriver randomDeriver = materialRuleContext.surfaceBuilder.method_39482(this.randomName());

			class VerticalGradientPredicate extends MaterialRules.FullLazyAbstractPredicate {
				VerticalGradientPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					int i = this.context.y;
					if (i <= i) {
						return true;
					} else if (i >= j) {
						return false;
					} else {
						double d = MathHelper.lerpFromProgress((double)i, (double)i, (double)j, 1.0, 0.0);
						AbstractRandom abstractRandom = randomDeriver.createRandom(this.context.x, i, this.context.z);
						return (double)abstractRandom.nextFloat() < d;
					}
				}
			}

			return new VerticalGradientPredicate();
		}
	}

	static record WaterMaterialCondition() implements MaterialRules.MaterialCondition {
		final int offset;
		final int surfaceDepthMultiplier;
		final boolean addStoneDepth;
		static final Codec<MaterialRules.WaterMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("offset").forGetter(MaterialRules.WaterMaterialCondition::offset),
						Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(MaterialRules.WaterMaterialCondition::surfaceDepthMultiplier),
						Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.WaterMaterialCondition::addStoneDepth)
					)
					.apply(instance, MaterialRules.WaterMaterialCondition::new)
		);

		WaterMaterialCondition(int i, int j, boolean bl) {
			this.offset = i;
			this.surfaceDepthMultiplier = j;
			this.addStoneDepth = bl;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class WaterPredicate extends MaterialRules.FullLazyAbstractPredicate {
				WaterPredicate() {
					super(materialRuleContext);
				}

				@Override
				protected boolean test() {
					return this.context.fluidHeight == Integer.MIN_VALUE
						|| this.context.y + (WaterMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0)
							>= this.context.fluidHeight + WaterMaterialCondition.this.offset + this.context.runDepth * WaterMaterialCondition.this.surfaceDepthMultiplier;
				}
			}

			return new WaterPredicate();
		}
	}
}
