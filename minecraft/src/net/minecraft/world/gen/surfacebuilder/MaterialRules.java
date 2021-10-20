package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.runtime.ObjectMethods;
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

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d) {
		return noiseThreshold(registryKey, d, Double.POSITIVE_INFINITY);
	}

	public static MaterialRules.MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
		return new MaterialRules.NoiseThresholdMaterialCondition(registryKey, d, e);
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

	static final class AboveYMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.AboveYMaterialCondition,"anchor;runDepthMultiplier;addStoneDepth",MaterialRules.AboveYMaterialCondition::anchor,MaterialRules.AboveYMaterialCondition::runDepthMultiplier,MaterialRules.AboveYMaterialCondition::addStoneDepth>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.AboveYMaterialCondition,"anchor;runDepthMultiplier;addStoneDepth",MaterialRules.AboveYMaterialCondition::anchor,MaterialRules.AboveYMaterialCondition::runDepthMultiplier,MaterialRules.AboveYMaterialCondition::addStoneDepth>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.AboveYMaterialCondition,"anchor;runDepthMultiplier;addStoneDepth",MaterialRules.AboveYMaterialCondition::anchor,MaterialRules.AboveYMaterialCondition::runDepthMultiplier,MaterialRules.AboveYMaterialCondition::addStoneDepth>(
				this, object
			);
		}

		public YOffset anchor() {
			return this.anchor;
		}

		public int runDepthMultiplier() {
			return this.runDepthMultiplier;
		}

		public boolean addStoneDepth() {
			return this.addStoneDepth;
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

	static final class BiomeMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.BiomeMaterialCondition,"biomes",MaterialRules.BiomeMaterialCondition::biomes>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.BiomeMaterialCondition,"biomes",MaterialRules.BiomeMaterialCondition::biomes>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.BiomeMaterialCondition,"biomes",MaterialRules.BiomeMaterialCondition::biomes>(this, object);
		}

		public List<RegistryKey<Biome>> biomes() {
			return this.biomes;
		}
	}

	static final class BlockMaterialRule extends Record implements MaterialRules.MaterialRule {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.BlockMaterialRule,"resultState;rule",MaterialRules.BlockMaterialRule::resultState,MaterialRules.BlockMaterialRule::rule>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.BlockMaterialRule,"resultState;rule",MaterialRules.BlockMaterialRule::resultState,MaterialRules.BlockMaterialRule::rule>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.BlockMaterialRule,"resultState;rule",MaterialRules.BlockMaterialRule::resultState,MaterialRules.BlockMaterialRule::rule>(
				this, object
			);
		}

		public BlockState resultState() {
			return this.resultState;
		}

		public MaterialRules.SimpleBlockStateRule rule() {
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

	static final class ConditionMaterialRule extends Record implements MaterialRules.MaterialRule {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.ConditionMaterialRule,"ifTrue;thenRun",MaterialRules.ConditionMaterialRule::ifTrue,MaterialRules.ConditionMaterialRule::thenRun>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.ConditionMaterialRule,"ifTrue;thenRun",MaterialRules.ConditionMaterialRule::ifTrue,MaterialRules.ConditionMaterialRule::thenRun>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.ConditionMaterialRule,"ifTrue;thenRun",MaterialRules.ConditionMaterialRule::ifTrue,MaterialRules.ConditionMaterialRule::thenRun>(
				this, object
			);
		}

		public MaterialRules.MaterialCondition ifTrue() {
			return this.ifTrue;
		}

		public MaterialRules.MaterialRule thenRun() {
			return this.thenRun;
		}
	}

	/**
	 * Applies another block state rule if the given predicate matches, and returns
	 * {@code null} otherwise.
	 */
	static final class ConditionalBlockStateRule extends Record implements MaterialRules.BlockStateRule {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.ConditionalBlockStateRule,"condition;followup",MaterialRules.ConditionalBlockStateRule::condition,MaterialRules.ConditionalBlockStateRule::followup>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.ConditionalBlockStateRule,"condition;followup",MaterialRules.ConditionalBlockStateRule::condition,MaterialRules.ConditionalBlockStateRule::followup>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.ConditionalBlockStateRule,"condition;followup",MaterialRules.ConditionalBlockStateRule::condition,MaterialRules.ConditionalBlockStateRule::followup>(
				this, object
			);
		}

		public MaterialRules.BooleanSupplier condition() {
			return this.condition;
		}

		public MaterialRules.BlockStateRule followup() {
			return this.followup;
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

	static final class InvertedBooleanSupplier extends Record implements MaterialRules.BooleanSupplier {
		private final MaterialRules.BooleanSupplier target;

		InvertedBooleanSupplier(MaterialRules.BooleanSupplier booleanSupplier) {
			this.target = booleanSupplier;
		}

		@Override
		public boolean test() {
			return !this.target.test();
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.InvertedBooleanSupplier,"target",MaterialRules.InvertedBooleanSupplier::target>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.InvertedBooleanSupplier,"target",MaterialRules.InvertedBooleanSupplier::target>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.InvertedBooleanSupplier,"target",MaterialRules.InvertedBooleanSupplier::target>(this, object);
		}

		public MaterialRules.BooleanSupplier target() {
			return this.target;
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
		final MaterialRules.Predicate<MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext> biomeTemperaturePredicate = new MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate(
			
		);
		final MaterialRules.Predicate<MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext> steepSlopePredicate = new MaterialRules.MaterialRuleContext.SteepSlopePredicate(
			
		);
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

			for(MaterialRules.Predicate<MaterialRules.MaterialRulePos> predicate : this.positionalPredicates) {
				predicate.init(materialRulePos);
			}

			this.steepSlopePredicate.init(new MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext(chunk, x, z));
			this.negativeRunDepthPredicate.init(runDepth);
		}

		protected void initContextDependentPredicates(
			RegistryKey<Biome> biomeKey, Biome biome, int runDepth, int stoneDepthAbove, int stoneDepthBelow, int waterHeight, int x, int y, int z
		) {
			for(MaterialRules.Predicate<RegistryKey<Biome>> predicate : this.biomeDependentPredicates) {
				predicate.init(biomeKey);
			}

			MaterialRules.SurfaceContext surfaceContext = new MaterialRules.SurfaceContext(y, stoneDepthAbove, stoneDepthBelow, runDepth, waterHeight);

			for(MaterialRules.Predicate<MaterialRules.SurfaceContext> predicate2 : this.contextDependentPredicates) {
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

			static final class BiomeTemperatureContext extends Record {
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

				public final String toString() {
					return ObjectMethods.bootstrap<"toString",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext,"biome;blockX;blockY;blockZ",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::biome,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockX,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockY,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockZ>(
						this
					);
				}

				public final int hashCode() {
					return ObjectMethods.bootstrap<"hashCode",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext,"biome;blockX;blockY;blockZ",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::biome,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockX,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockY,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockZ>(
						this
					);
				}

				public final boolean equals(Object object) {
					return ObjectMethods.bootstrap<"equals",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext,"biome;blockX;blockY;blockZ",MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::biome,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockX,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockY,MaterialRules.MaterialRuleContext.BiomeTemperaturePredicate.BiomeTemperatureContext::blockZ>(
						this, object
					);
				}

				public Biome biome() {
					return this.biome;
				}

				public int blockX() {
					return this.blockX;
				}

				public int blockY() {
					return this.blockY;
				}

				public int blockZ() {
					return this.blockZ;
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

			static final class SteepSlopeContext extends Record {
				final Chunk chunk;
				final int blockX;
				final int blockZ;

				SteepSlopeContext(Chunk chunk, int i, int j) {
					this.chunk = chunk;
					this.blockX = i;
					this.blockZ = j;
				}

				public final String toString() {
					return ObjectMethods.bootstrap<"toString",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext,"chunk;blockX;blockZ",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::chunk,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockX,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockZ>(
						this
					);
				}

				public final int hashCode() {
					return ObjectMethods.bootstrap<"hashCode",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext,"chunk;blockX;blockZ",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::chunk,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockX,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockZ>(
						this
					);
				}

				public final boolean equals(Object object) {
					return ObjectMethods.bootstrap<"equals",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext,"chunk;blockX;blockZ",MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::chunk,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockX,MaterialRules.MaterialRuleContext.SteepSlopePredicate.SteepSlopeContext::blockZ>(
						this, object
					);
				}

				public Chunk chunk() {
					return this.chunk;
				}

				public int blockX() {
					return this.blockX;
				}

				public int blockZ() {
					return this.blockZ;
				}
			}
		}
	}

	static final class MaterialRulePos extends Record {
		final int blockX;
		final int blockZ;

		MaterialRulePos(int i, int j) {
			this.blockX = i;
			this.blockZ = j;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.MaterialRulePos,"blockX;blockZ",MaterialRules.MaterialRulePos::blockX,MaterialRules.MaterialRulePos::blockZ>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.MaterialRulePos,"blockX;blockZ",MaterialRules.MaterialRulePos::blockX,MaterialRules.MaterialRulePos::blockZ>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.MaterialRulePos,"blockX;blockZ",MaterialRules.MaterialRulePos::blockX,MaterialRules.MaterialRulePos::blockZ>(
				this, object
			);
		}

		public int blockX() {
			return this.blockX;
		}

		public int blockZ() {
			return this.blockZ;
		}
	}

	static final class NoiseThresholdMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.NoiseThresholdMaterialCondition,"noise;minThreshold;maxThreshold",MaterialRules.NoiseThresholdMaterialCondition::noise,MaterialRules.NoiseThresholdMaterialCondition::minThreshold,MaterialRules.NoiseThresholdMaterialCondition::maxThreshold>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.NoiseThresholdMaterialCondition,"noise;minThreshold;maxThreshold",MaterialRules.NoiseThresholdMaterialCondition::noise,MaterialRules.NoiseThresholdMaterialCondition::minThreshold,MaterialRules.NoiseThresholdMaterialCondition::maxThreshold>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.NoiseThresholdMaterialCondition,"noise;minThreshold;maxThreshold",MaterialRules.NoiseThresholdMaterialCondition::noise,MaterialRules.NoiseThresholdMaterialCondition::minThreshold,MaterialRules.NoiseThresholdMaterialCondition::maxThreshold>(
				this, object
			);
		}

		public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise() {
			return this.noise;
		}

		public double minThreshold() {
			return this.minThreshold;
		}

		public double maxThreshold() {
			return this.maxThreshold;
		}
	}

	static final class NotMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.NotMaterialCondition,"target",MaterialRules.NotMaterialCondition::target>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.NotMaterialCondition,"target",MaterialRules.NotMaterialCondition::target>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.NotMaterialCondition,"target",MaterialRules.NotMaterialCondition::target>(this, object);
		}

		public MaterialRules.MaterialCondition target() {
			return this.target;
		}
	}

	interface Predicate<S> extends MaterialRules.BooleanSupplier {
		void init(S context);
	}

	/**
	 * Applies the given block state rules in sequence, and returns the first result that
	 * isn't {@code null}. Returns {@code null} if none of the passed rules match.
	 */
	static final class SequenceBlockStateRule extends Record implements MaterialRules.BlockStateRule {
		private final List<MaterialRules.BlockStateRule> rules;

		SequenceBlockStateRule(List<MaterialRules.BlockStateRule> list) {
			this.rules = list;
		}

		@Nullable
		@Override
		public BlockState tryApply(int i, int j, int k) {
			for(MaterialRules.BlockStateRule blockStateRule : this.rules) {
				BlockState blockState = blockStateRule.tryApply(i, j, k);
				if (blockState != null) {
					return blockState;
				}
			}

			return null;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.SequenceBlockStateRule,"rules",MaterialRules.SequenceBlockStateRule::rules>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.SequenceBlockStateRule,"rules",MaterialRules.SequenceBlockStateRule::rules>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.SequenceBlockStateRule,"rules",MaterialRules.SequenceBlockStateRule::rules>(this, object);
		}

		public List<MaterialRules.BlockStateRule> rules() {
			return this.rules;
		}
	}

	static final class SequenceMaterialRule extends Record implements MaterialRules.MaterialRule {
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

				for(MaterialRules.MaterialRule materialRule : this.sequence) {
					builder.add((MaterialRules.BlockStateRule)materialRule.apply(materialRuleContext));
				}

				return new MaterialRules.SequenceBlockStateRule(builder.build());
			}
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.SequenceMaterialRule,"sequence",MaterialRules.SequenceMaterialRule::sequence>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.SequenceMaterialRule,"sequence",MaterialRules.SequenceMaterialRule::sequence>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.SequenceMaterialRule,"sequence",MaterialRules.SequenceMaterialRule::sequence>(this, object);
		}

		public List<MaterialRules.MaterialRule> sequence() {
			return this.sequence;
		}
	}

	/**
	 * Always returns the given {@link BlockState}.
	 */
	static final class SimpleBlockStateRule extends Record implements MaterialRules.BlockStateRule {
		private final BlockState state;

		SimpleBlockStateRule(BlockState blockState) {
			this.state = blockState;
		}

		@Override
		public BlockState tryApply(int i, int j, int k) {
			return this.state;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.SimpleBlockStateRule,"state",MaterialRules.SimpleBlockStateRule::state>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.SimpleBlockStateRule,"state",MaterialRules.SimpleBlockStateRule::state>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.SimpleBlockStateRule,"state",MaterialRules.SimpleBlockStateRule::state>(this, object);
		}

		public BlockState state() {
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

	static final class StoneDepthMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.StoneDepthMaterialCondition,"addRunDepth;surfaceType",MaterialRules.StoneDepthMaterialCondition::addRunDepth,MaterialRules.StoneDepthMaterialCondition::surfaceType>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.StoneDepthMaterialCondition,"addRunDepth;surfaceType",MaterialRules.StoneDepthMaterialCondition::addRunDepth,MaterialRules.StoneDepthMaterialCondition::surfaceType>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.StoneDepthMaterialCondition,"addRunDepth;surfaceType",MaterialRules.StoneDepthMaterialCondition::addRunDepth,MaterialRules.StoneDepthMaterialCondition::surfaceType>(
				this, object
			);
		}

		public boolean addRunDepth() {
			return this.addRunDepth;
		}

		public VerticalSurfaceType surfaceType() {
			return this.surfaceType;
		}
	}

	static final class SurfaceContext extends Record {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.SurfaceContext,"blockY;stoneDepthAbove;stoneDepthBelow;runDepth;waterHeight",MaterialRules.SurfaceContext::blockY,MaterialRules.SurfaceContext::stoneDepthAbove,MaterialRules.SurfaceContext::stoneDepthBelow,MaterialRules.SurfaceContext::runDepth,MaterialRules.SurfaceContext::waterHeight>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.SurfaceContext,"blockY;stoneDepthAbove;stoneDepthBelow;runDepth;waterHeight",MaterialRules.SurfaceContext::blockY,MaterialRules.SurfaceContext::stoneDepthAbove,MaterialRules.SurfaceContext::stoneDepthBelow,MaterialRules.SurfaceContext::runDepth,MaterialRules.SurfaceContext::waterHeight>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.SurfaceContext,"blockY;stoneDepthAbove;stoneDepthBelow;runDepth;waterHeight",MaterialRules.SurfaceContext::blockY,MaterialRules.SurfaceContext::stoneDepthAbove,MaterialRules.SurfaceContext::stoneDepthBelow,MaterialRules.SurfaceContext::runDepth,MaterialRules.SurfaceContext::waterHeight>(
				this, object
			);
		}

		public int blockY() {
			return this.blockY;
		}

		public int stoneDepthAbove() {
			return this.stoneDepthAbove;
		}

		public int stoneDepthBelow() {
			return this.stoneDepthBelow;
		}

		public int runDepth() {
			return this.runDepth;
		}

		public int waterHeight() {
			return this.waterHeight;
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

	static final class WaterMaterialCondition extends Record implements MaterialRules.MaterialCondition {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MaterialRules.WaterMaterialCondition,"offset;runDepthMultiplier;addStoneDepth",MaterialRules.WaterMaterialCondition::offset,MaterialRules.WaterMaterialCondition::runDepthMultiplier,MaterialRules.WaterMaterialCondition::addStoneDepth>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MaterialRules.WaterMaterialCondition,"offset;runDepthMultiplier;addStoneDepth",MaterialRules.WaterMaterialCondition::offset,MaterialRules.WaterMaterialCondition::runDepthMultiplier,MaterialRules.WaterMaterialCondition::addStoneDepth>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",MaterialRules.WaterMaterialCondition,"offset;runDepthMultiplier;addStoneDepth",MaterialRules.WaterMaterialCondition::offset,MaterialRules.WaterMaterialCondition::runDepthMultiplier,MaterialRules.WaterMaterialCondition::addStoneDepth>(
				this, object
			);
		}

		public int offset() {
			return this.offset;
		}

		public int runDepthMultiplier() {
			return this.runDepthMultiplier;
		}

		public boolean addStoneDepth() {
			return this.addStoneDepth;
		}
	}
}
