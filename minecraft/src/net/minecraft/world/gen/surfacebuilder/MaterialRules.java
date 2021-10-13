package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public class MaterialRules {
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR = new MaterialRules.StoneDepthMaterialCondition(false, VerticalSurfaceType.FLOOR);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_FLOOR_WITH_RUN_DEPTH = new MaterialRules.StoneDepthMaterialCondition(
		true, VerticalSurfaceType.FLOOR
	);
	public static final MaterialRules.MaterialCondition STONE_DEPTH_CEILING = new MaterialRules.StoneDepthMaterialCondition(true, VerticalSurfaceType.CEILING);

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

	public static MaterialRules.MaterialCondition noiseThreshold(String name, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, double minThreshold) {
		return noiseThreshold(name, noiseParameters, minThreshold, Double.POSITIVE_INFINITY);
	}

	public static MaterialRules.MaterialCondition noiseThreshold(
		String name, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, double minThreshold, double maxThreshold
	) {
		return new MaterialRules.NoiseThresholdMaterialCondition(name, noiseParameters, minThreshold, maxThreshold);
	}

	public static MaterialRules.MaterialCondition steepSlope() {
		return MaterialRules.SteepMaterialCondition.INSTANCE;
	}

	public static MaterialRules.MaterialCondition hole() {
		return MaterialRules.HoleMaterialCondition.INSTANCE;
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
		final int runDepthMultiplier;
		final boolean addStoneDepth;
		static final Codec<MaterialRules.AboveYMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						YOffset.OFFSET_CODEC.fieldOf("anchor").forGetter(MaterialRules.AboveYMaterialCondition::anchor),
						Codec.intRange(-20, 20).fieldOf("run_depth_multiplier").forGetter(MaterialRules.AboveYMaterialCondition::runDepthMultiplier),
						Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.AboveYMaterialCondition::addStoneDepth)
					)
					.apply(instance, MaterialRules.AboveYMaterialCondition::new)
		);

		AboveYMaterialCondition(YOffset yOffset, int i, boolean bl) {
			this.anchor = yOffset;
			this.runDepthMultiplier = i;
			this.addStoneDepth = bl;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class AboveYPredicate extends MaterialRules.AbstractPredicate<MaterialRules.SurfaceContext> {
				protected boolean test(MaterialRules.SurfaceContext surfaceContext) {
					return surfaceContext.blockY + (AboveYMaterialCondition.this.addStoneDepth ? surfaceContext.stoneDepthAbove : 0)
						>= AboveYMaterialCondition.this.anchor.getY(materialRuleContext.heightContext)
							+ surfaceContext.runDepth * AboveYMaterialCondition.this.runDepthMultiplier;
				}
			}

			AboveYPredicate aboveYPredicate = new AboveYPredicate();
			materialRuleContext.contextDependentPredicates.add(aboveYPredicate);
			return aboveYPredicate;
		}
	}

	abstract static class AbstractPredicate<S> implements MaterialRules.Predicate<S> {
		boolean result = false;

		@Override
		public void init(S context) {
			this.result = this.test(context);
		}

		@Override
		public boolean test() {
			return this.result;
		}

		protected abstract boolean test(S context);
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

			class BiomePredicate extends MaterialRules.AbstractPredicate<RegistryKey<Biome>> {
				protected boolean test(RegistryKey<Biome> registryKey) {
					return set.contains(registryKey);
				}
			}

			BiomePredicate biomePredicate = new BiomePredicate();
			materialRuleContext.biomeDependentPredicates.add(biomePredicate);
			return biomePredicate;
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
		boolean test();
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
			return !this.condition.test() ? null : this.followup.tryApply(i, j, k);
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

	static record InvertedBooleanSupplier() implements MaterialRules.BooleanSupplier {
		private final MaterialRules.BooleanSupplier target;

		InvertedBooleanSupplier(MaterialRules.BooleanSupplier booleanSupplier) {
			this.target = booleanSupplier;
		}

		@Override
		public boolean test() {
			return !this.target.test();
		}
	}

	abstract static class LazyAbstractPredicate<S> implements MaterialRules.Predicate<S> {
		@Nullable
		private S context;
		@Nullable
		Boolean result;

		@Override
		public void init(S context) {
			this.context = context;
			this.result = null;
		}

		@Override
		public boolean test() {
			if (this.result == null) {
				if (this.context == null) {
					throw new IllegalStateException("Calling test without update");
				}

				this.result = this.test(this.context);
			}

			return this.result;
		}

		protected abstract boolean test(S object);
	}

	public interface MaterialCondition extends Function<MaterialRules.MaterialRuleContext, MaterialRules.BooleanSupplier> {
		Codec<MaterialRules.MaterialCondition> CODEC = Registry.MATERIAL_CONDITION.dispatch(MaterialRules.MaterialCondition::codec, Function.identity());

		static Codec<? extends MaterialRules.MaterialCondition> registerAndGetDefault() {
			Registry.register(Registry.MATERIAL_CONDITION, "biome", MaterialRules.BiomeMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "noise_threshold", MaterialRules.NoiseThresholdMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "y_above", MaterialRules.AboveYMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "water", MaterialRules.WaterMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "temperature", MaterialRules.TemperatureMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "steep", MaterialRules.SteepMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "not", MaterialRules.NotMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "hole", MaterialRules.HoleMaterialCondition.CONDITION_CODEC);
			Registry.register(Registry.MATERIAL_CONDITION, "stone_depth", MaterialRules.StoneDepthMaterialCondition.CONDITION_CODEC);
			return (Codec<? extends MaterialRules.MaterialCondition>)Registry.MATERIAL_CONDITION.iterator().next();
		}

		Codec<? extends MaterialRules.MaterialCondition> codec();
	}

	public interface MaterialRule extends Function<MaterialRules.MaterialRuleContext, MaterialRules.BlockStateRule> {
		Codec<MaterialRules.MaterialRule> CODEC = Registry.MATERIAL_RULE.dispatch(MaterialRules.MaterialRule::codec, Function.identity());

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
		final MaterialRules.Predicate<MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext> biomeTemperaturePredicate = new MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate();
		final MaterialRules.Predicate<MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext> steepSlopePredicate = new MaterialRules.MaterialRuleContext.SteepSlopePredicate();
		final MaterialRules.Predicate<Integer> negativeRunDepthPredicate = new MaterialRules.MaterialRuleContext.NegativePredicate();
		final List<MaterialRules.Predicate<RegistryKey<Biome>>> biomeDependentPredicates = new ObjectArrayList<>();
		final List<MaterialRules.Predicate<MaterialRules.MaterialRulePos>> positionalPredicates = new ObjectArrayList<>();
		final List<MaterialRules.Predicate<MaterialRules.SurfaceContext>> contextDependentPredicates = new ObjectArrayList<>();
		boolean needsCeilingStoneDepth;
		final HeightContext heightContext;

		protected MaterialRuleContext(SurfaceBuilder surfaceBuilder, HeightContext heightContext) {
			this.surfaceBuilder = surfaceBuilder;
			this.heightContext = heightContext;
		}

		protected void initWorldDependentPredicates(Chunk chunk, int x, int z, int runDepth) {
			MaterialRules.MaterialRulePos materialRulePos = new MaterialRules.MaterialRulePos(x, z);

			for (MaterialRules.Predicate<MaterialRules.MaterialRulePos> predicate : this.positionalPredicates) {
				predicate.init(materialRulePos);
			}

			this.steepSlopePredicate.init(new MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext(chunk, x, z));
			this.negativeRunDepthPredicate.init(runDepth);
		}

		protected void initContextDependentPredicates(
			RegistryKey<Biome> biomeKey, Biome biome, int runDepth, int stoneDepthAbove, int stoneDepthBelow, int waterHeight, int x, int y, int z
		) {
			for (MaterialRules.Predicate<RegistryKey<Biome>> predicate : this.biomeDependentPredicates) {
				predicate.init(biomeKey);
			}

			MaterialRules.SurfaceContext surfaceContext = new MaterialRules.SurfaceContext(y, stoneDepthAbove, stoneDepthBelow, runDepth, waterHeight);

			for (MaterialRules.Predicate<MaterialRules.SurfaceContext> predicate2 : this.contextDependentPredicates) {
				predicate2.init(surfaceContext);
			}

			this.biomeTemperaturePredicate.init(new MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext(biome, x, y, z));
		}

		protected boolean needsCeilingStoneDepth() {
			return this.needsCeilingStoneDepth;
		}

		static class BiomeTemperaturePredicate
			extends MaterialRules.LazyAbstractPredicate<MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext> {
			protected boolean test(MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext biomeTemperatureContext) {
				return biomeTemperatureContext.biome
						.getTemperature(new BlockPos(biomeTemperatureContext.blockX, biomeTemperatureContext.blockY, biomeTemperatureContext.blockZ))
					< 0.15F;
			}

			static record BiomeTemperatureContext() {
				final Biome biome;
				final int blockX;
				final int blockY;
				final int blockZ;

				BiomeTemperatureContext(Biome biome, int i, int j, int k) {
					this.biome = biome;
					this.blockX = i;
					this.blockY = j;
					this.blockZ = k;
				}
			}
		}

		static final class NegativePredicate extends MaterialRules.AbstractPredicate<Integer> {
			protected boolean test(Integer integer) {
				return integer <= 0;
			}
		}

		static class SteepSlopePredicate extends MaterialRules.LazyAbstractPredicate<MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext> {
			protected boolean test(MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext steepSlopeContext) {
				int i = steepSlopeContext.blockX & 15;
				int j = steepSlopeContext.blockZ & 15;
				int k = Math.max(j - 1, 0);
				int l = Math.min(j + 1, 15);
				int m = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, k);
				int n = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, l);
				if (n >= m + 4) {
					return true;
				} else {
					int o = Math.max(i - 1, 0);
					int p = Math.min(i + 1, 15);
					int q = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, o, j);
					int r = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, p, j);
					return q >= r + 4;
				}
			}

			static record SteepSlopeContext() {
				final Chunk chunk;
				final int blockX;
				final int blockZ;

				SteepSlopeContext(Chunk chunk, int i, int j) {
					this.chunk = chunk;
					this.blockX = i;
					this.blockZ = j;
				}
			}
		}
	}

	static record MaterialRulePos() {
		final int blockX;
		final int blockZ;

		MaterialRulePos(int i, int j) {
			this.blockX = i;
			this.blockZ = j;
		}
	}

	static record NoiseThresholdMaterialCondition() implements MaterialRules.MaterialCondition {
		private final String name;
		private final DoublePerlinNoiseSampler.NoiseParameters noise;
		final double minThreshold;
		final double maxThreshold;
		static final Codec<MaterialRules.NoiseThresholdMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(MaterialRules.NoiseThresholdMaterialCondition::name),
						DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(MaterialRules.NoiseThresholdMaterialCondition::noise),
						Codec.DOUBLE.fieldOf("min_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::minThreshold),
						Codec.DOUBLE.fieldOf("max_threshold").forGetter(MaterialRules.NoiseThresholdMaterialCondition::maxThreshold)
					)
					.apply(instance, MaterialRules.NoiseThresholdMaterialCondition::new)
		);

		NoiseThresholdMaterialCondition(String string, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, double d, double e) {
			this.name = string;
			this.noise = noiseParameters;
			this.minThreshold = d;
			this.maxThreshold = e;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.surfaceBuilder.getNoiseSampler(this.name, this.noise);

			class NoiseThresholdPredicate extends MaterialRules.LazyAbstractPredicate<MaterialRules.MaterialRulePos> {
				protected boolean test(MaterialRules.MaterialRulePos materialRulePos) {
					double d = doublePerlinNoiseSampler.sample((double)materialRulePos.blockX, 0.0, (double)materialRulePos.blockZ);
					return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
				}
			}

			NoiseThresholdPredicate noiseThresholdPredicate = new NoiseThresholdPredicate();
			materialRuleContext.positionalPredicates.add(noiseThresholdPredicate);
			return noiseThresholdPredicate;
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

	interface Predicate<S> extends MaterialRules.BooleanSupplier {
		void init(S context);
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
		final boolean addRunDepth;
		private final VerticalSurfaceType surfaceType;
		static final Codec<MaterialRules.StoneDepthMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.BOOL.fieldOf("add_run_depth").forGetter(MaterialRules.StoneDepthMaterialCondition::addRunDepth),
						VerticalSurfaceType.CODEC.fieldOf("surface_type").forGetter(MaterialRules.StoneDepthMaterialCondition::surfaceType)
					)
					.apply(instance, MaterialRules.StoneDepthMaterialCondition::new)
		);

		StoneDepthMaterialCondition(boolean bl, VerticalSurfaceType verticalSurfaceType) {
			this.addRunDepth = bl;
			this.surfaceType = verticalSurfaceType;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;

			class StoneDepthPredicate extends MaterialRules.AbstractPredicate<MaterialRules.SurfaceContext> {
				protected boolean test(MaterialRules.SurfaceContext surfaceContext) {
					return (bl ? surfaceContext.stoneDepthBelow : surfaceContext.stoneDepthAbove)
						<= 1 + (StoneDepthMaterialCondition.this.addRunDepth ? surfaceContext.runDepth : 0);
				}
			}

			StoneDepthPredicate stoneDepthPredicate = new StoneDepthPredicate();
			materialRuleContext.contextDependentPredicates.add(stoneDepthPredicate);
			if (bl) {
				materialRuleContext.needsCeilingStoneDepth = true;
			}

			return stoneDepthPredicate;
		}
	}

	static record SurfaceContext() {
		final int blockY;
		final int stoneDepthAbove;
		final int stoneDepthBelow;
		final int runDepth;
		final int waterHeight;

		SurfaceContext(int i, int j, int k, int l, int m) {
			this.blockY = i;
			this.stoneDepthAbove = j;
			this.stoneDepthBelow = k;
			this.runDepth = l;
			this.waterHeight = m;
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

	static record WaterMaterialCondition() implements MaterialRules.MaterialCondition {
		final int offset;
		final int runDepthMultiplier;
		final boolean addStoneDepth;
		static final Codec<MaterialRules.WaterMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("offset").forGetter(MaterialRules.WaterMaterialCondition::offset),
						Codec.intRange(-20, 20).fieldOf("run_depth_multiplier").forGetter(MaterialRules.WaterMaterialCondition::runDepthMultiplier),
						Codec.BOOL.fieldOf("add_stone_depth").forGetter(MaterialRules.WaterMaterialCondition::addStoneDepth)
					)
					.apply(instance, MaterialRules.WaterMaterialCondition::new)
		);

		WaterMaterialCondition(int i, int j, boolean bl) {
			this.offset = i;
			this.runDepthMultiplier = j;
			this.addStoneDepth = bl;
		}

		@Override
		public Codec<? extends MaterialRules.MaterialCondition> codec() {
			return CONDITION_CODEC;
		}

		public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
			class WaterPredicate extends MaterialRules.AbstractPredicate<MaterialRules.SurfaceContext> {
				protected boolean test(MaterialRules.SurfaceContext surfaceContext) {
					return surfaceContext.waterHeight == Integer.MIN_VALUE
						|| surfaceContext.blockY + (WaterMaterialCondition.this.addStoneDepth ? surfaceContext.stoneDepthAbove : 0)
							>= surfaceContext.waterHeight + WaterMaterialCondition.this.offset + surfaceContext.runDepth * WaterMaterialCondition.this.runDepthMultiplier;
				}
			}

			WaterPredicate waterPredicate = new WaterPredicate();
			materialRuleContext.contextDependentPredicates.add(waterPredicate);
			return waterPredicate;
		}
	}
}
