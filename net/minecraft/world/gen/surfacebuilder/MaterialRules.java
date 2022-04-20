/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.RandomDeriver;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class MaterialRules {
    public static final MaterialCondition STONE_DEPTH_FLOOR = MaterialRules.stoneDepth(0, false, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH = MaterialRules.stoneDepth(0, true, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_6 = MaterialRules.stoneDepth(0, true, 6, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_30 = MaterialRules.stoneDepth(0, true, 30, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_CEILING = MaterialRules.stoneDepth(0, false, VerticalSurfaceType.CEILING);
    public static final MaterialCondition STONE_DEPTH_CEILING_WITH_SURFACE_DEPTH = MaterialRules.stoneDepth(0, true, VerticalSurfaceType.CEILING);

    public static MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, VerticalSurfaceType verticalSurfaceType) {
        return new StoneDepthMaterialCondition(offset, addSurfaceDepth, 0, verticalSurfaceType);
    }

    public static MaterialCondition stoneDepth(int offset, boolean addSurfaceDepth, int secondaryDepthRange, VerticalSurfaceType verticalSurfaceType) {
        return new StoneDepthMaterialCondition(offset, addSurfaceDepth, secondaryDepthRange, verticalSurfaceType);
    }

    public static MaterialCondition not(MaterialCondition target) {
        return new NotMaterialCondition(target);
    }

    public static MaterialCondition aboveY(YOffset anchor, int runDepthMultiplier) {
        return new AboveYMaterialCondition(anchor, runDepthMultiplier, false);
    }

    public static MaterialCondition aboveYWithStoneDepth(YOffset anchor, int runDepthMultiplier) {
        return new AboveYMaterialCondition(anchor, runDepthMultiplier, true);
    }

    public static MaterialCondition water(int offset, int runDepthMultiplier) {
        return new WaterMaterialCondition(offset, runDepthMultiplier, false);
    }

    public static MaterialCondition waterWithStoneDepth(int offset, int runDepthMultiplier) {
        return new WaterMaterialCondition(offset, runDepthMultiplier, true);
    }

    @SafeVarargs
    public static MaterialCondition biome(RegistryKey<Biome> ... biomes) {
        return MaterialRules.biome(List.of(biomes));
    }

    private static BiomeMaterialCondition biome(List<RegistryKey<Biome>> biomes) {
        return new BiomeMaterialCondition(biomes);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min) {
        return MaterialRules.noiseThreshold(noise, min, Double.MAX_VALUE);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double min, double max) {
        return new NoiseThresholdMaterialCondition(noise, min, max);
    }

    public static MaterialCondition verticalGradient(String id, YOffset trueAtAndBelow, YOffset falseAtAndAbove) {
        return new VerticalGradientMaterialCondition(new Identifier(id), trueAtAndBelow, falseAtAndAbove);
    }

    public static MaterialCondition steepSlope() {
        return SteepMaterialCondition.INSTANCE;
    }

    public static MaterialCondition hole() {
        return HoleMaterialCondition.INSTANCE;
    }

    public static MaterialCondition surface() {
        return SurfaceMaterialCondition.INSTANCE;
    }

    public static MaterialCondition temperature() {
        return TemperatureMaterialCondition.INSTANCE;
    }

    public static MaterialRule condition(MaterialCondition condition, MaterialRule rule) {
        return new ConditionMaterialRule(condition, rule);
    }

    public static MaterialRule sequence(MaterialRule ... rules) {
        if (rules.length == 0) {
            throw new IllegalArgumentException("Need at least 1 rule for a sequence");
        }
        return new SequenceMaterialRule(Arrays.asList(rules));
    }

    public static MaterialRule block(BlockState state) {
        return new BlockMaterialRule(state);
    }

    public static MaterialRule terracottaBands() {
        return TerracottaBandsMaterialRule.INSTANCE;
    }

    static <A> Codec<? extends A> register(Registry<Codec<? extends A>> registry, String id, CodecHolder<? extends A> codecHolder) {
        return Registry.register(registry, id, codecHolder.codec());
    }

    record StoneDepthMaterialCondition(int offset, boolean addSurfaceDepth, int secondaryDepthRange, VerticalSurfaceType surfaceType) implements MaterialCondition
    {
        static final CodecHolder<StoneDepthMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("offset")).forGetter(StoneDepthMaterialCondition::offset), ((MapCodec)Codec.BOOL.fieldOf("add_surface_depth")).forGetter(StoneDepthMaterialCondition::addSurfaceDepth), ((MapCodec)Codec.INT.fieldOf("secondary_depth_range")).forGetter(StoneDepthMaterialCondition::secondaryDepthRange), ((MapCodec)VerticalSurfaceType.CODEC.fieldOf("surface_type")).forGetter(StoneDepthMaterialCondition::surfaceType)).apply((Applicative<StoneDepthMaterialCondition, ?>)instance, StoneDepthMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;
            class StoneDepthPredicate
            extends FullLazyAbstractPredicate {
                StoneDepthPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    int i = bl ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
                    int j = StoneDepthMaterialCondition.this.addSurfaceDepth ? this.context.runDepth : 0;
                    int k = StoneDepthMaterialCondition.this.secondaryDepthRange == 0 ? 0 : (int)MathHelper.lerpFromProgress(this.context.method_39550(), -1.0, 1.0, 0.0, (double)StoneDepthMaterialCondition.this.secondaryDepthRange);
                    return i <= 1 + StoneDepthMaterialCondition.this.offset + j + k;
                }
            }
            return new StoneDepthPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record NotMaterialCondition(MaterialCondition target) implements MaterialCondition
    {
        static final CodecHolder<NotMaterialCondition> CODEC = CodecHolder.of(MaterialCondition.CODEC.xmap(NotMaterialCondition::new, NotMaterialCondition::target).fieldOf("invert"));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return new InvertedBooleanSupplier((BooleanSupplier)this.target.apply(materialRuleContext));
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    public static interface MaterialCondition
    extends Function<MaterialRuleContext, BooleanSupplier> {
        public static final Codec<MaterialCondition> CODEC = Registry.MATERIAL_CONDITION.getCodec().dispatch(materialCondition -> materialCondition.codec().codec(), Function.identity());

        public static Codec<? extends MaterialCondition> registerAndGetDefault(Registry<Codec<? extends MaterialCondition>> registry) {
            MaterialRules.register(registry, "biome", BiomeMaterialCondition.CODEC);
            MaterialRules.register(registry, "noise_threshold", NoiseThresholdMaterialCondition.CODEC);
            MaterialRules.register(registry, "vertical_gradient", VerticalGradientMaterialCondition.CODEC);
            MaterialRules.register(registry, "y_above", AboveYMaterialCondition.CODEC);
            MaterialRules.register(registry, "water", WaterMaterialCondition.CODEC);
            MaterialRules.register(registry, "temperature", TemperatureMaterialCondition.CODEC);
            MaterialRules.register(registry, "steep", SteepMaterialCondition.CODEC);
            MaterialRules.register(registry, "not", NotMaterialCondition.CODEC);
            MaterialRules.register(registry, "hole", HoleMaterialCondition.CODEC);
            MaterialRules.register(registry, "above_preliminary_surface", SurfaceMaterialCondition.CODEC);
            return MaterialRules.register(registry, "stone_depth", StoneDepthMaterialCondition.CODEC);
        }

        public CodecHolder<? extends MaterialCondition> codec();
    }

    record AboveYMaterialCondition(YOffset anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialCondition
    {
        static final CodecHolder<AboveYMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("anchor")).forGetter(AboveYMaterialCondition::anchor), ((MapCodec)Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier")).forGetter(AboveYMaterialCondition::surfaceDepthMultiplier), ((MapCodec)Codec.BOOL.fieldOf("add_stone_depth")).forGetter(AboveYMaterialCondition::addStoneDepth)).apply((Applicative<AboveYMaterialCondition, ?>)instance, AboveYMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class AboveYPredicate
            extends FullLazyAbstractPredicate {
                AboveYPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.y + (AboveYMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= AboveYMaterialCondition.this.anchor.getY(this.context.heightContext) + this.context.runDepth * AboveYMaterialCondition.this.surfaceDepthMultiplier;
                }
            }
            return new AboveYPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record WaterMaterialCondition(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialCondition
    {
        static final CodecHolder<WaterMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("offset")).forGetter(WaterMaterialCondition::offset), ((MapCodec)Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier")).forGetter(WaterMaterialCondition::surfaceDepthMultiplier), ((MapCodec)Codec.BOOL.fieldOf("add_stone_depth")).forGetter(WaterMaterialCondition::addStoneDepth)).apply((Applicative<WaterMaterialCondition, ?>)instance, WaterMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class WaterPredicate
            extends FullLazyAbstractPredicate {
                WaterPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.fluidHeight == Integer.MIN_VALUE || this.context.y + (WaterMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.fluidHeight + WaterMaterialCondition.this.offset + this.context.runDepth * WaterMaterialCondition.this.surfaceDepthMultiplier;
                }
            }
            return new WaterPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static final class BiomeMaterialCondition
    implements MaterialCondition {
        static final CodecHolder<BiomeMaterialCondition> CODEC = CodecHolder.of(((MapCodec)RegistryKey.createCodec(Registry.BIOME_KEY).listOf().fieldOf("biome_is")).xmap(MaterialRules::biome, biomeMaterialCondition -> biomeMaterialCondition.biomes));
        private final List<RegistryKey<Biome>> biomes;
        final Predicate<RegistryKey<Biome>> predicate;

        BiomeMaterialCondition(List<RegistryKey<Biome>> biomes) {
            this.biomes = biomes;
            this.predicate = Set.copyOf(biomes)::contains;
        }

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            class BiomePredicate
            extends FullLazyAbstractPredicate {
                BiomePredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return this.context.biomeSupplier.get().matches(BiomeMaterialCondition.this.predicate);
                }
            }
            return new BiomePredicate();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BiomeMaterialCondition) {
                BiomeMaterialCondition biomeMaterialCondition = (BiomeMaterialCondition)object;
                return this.biomes.equals(biomeMaterialCondition.biomes);
            }
            return false;
        }

        public int hashCode() {
            return this.biomes.hashCode();
        }

        public String toString() {
            return "BiomeConditionSource[biomes=" + this.biomes + "]";
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record NoiseThresholdMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double minThreshold, double maxThreshold) implements MaterialCondition
    {
        static final CodecHolder<NoiseThresholdMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)RegistryKey.createCodec(Registry.NOISE_WORLDGEN).fieldOf("noise")).forGetter(NoiseThresholdMaterialCondition::noise), ((MapCodec)Codec.DOUBLE.fieldOf("min_threshold")).forGetter(NoiseThresholdMaterialCondition::minThreshold), ((MapCodec)Codec.DOUBLE.fieldOf("max_threshold")).forGetter(NoiseThresholdMaterialCondition::maxThreshold)).apply((Applicative<NoiseThresholdMaterialCondition, ?>)instance, NoiseThresholdMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.noiseConfig.getOrCreateSampler(this.noise);
            class NoiseThresholdPredicate
            extends HorizontalLazyAbstractPredicate {
                NoiseThresholdPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    double d = doublePerlinNoiseSampler.sample(this.context.x, 0.0, this.context.z);
                    return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
                }
            }
            return new NoiseThresholdPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record VerticalGradientMaterialCondition(Identifier randomName, YOffset trueAtAndBelow, YOffset falseAtAndAbove) implements MaterialCondition
    {
        static final CodecHolder<VerticalGradientMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("random_name")).forGetter(VerticalGradientMaterialCondition::randomName), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("true_at_and_below")).forGetter(VerticalGradientMaterialCondition::trueAtAndBelow), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("false_at_and_above")).forGetter(VerticalGradientMaterialCondition::falseAtAndAbove)).apply((Applicative<VerticalGradientMaterialCondition, ?>)instance, VerticalGradientMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final int i = this.trueAtAndBelow().getY(materialRuleContext.heightContext);
            final int j = this.falseAtAndAbove().getY(materialRuleContext.heightContext);
            final RandomDeriver randomDeriver = materialRuleContext.noiseConfig.getOrCreateRandomDeriver(this.randomName());
            class VerticalGradientPredicate
            extends FullLazyAbstractPredicate {
                VerticalGradientPredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    int i2 = this.context.y;
                    if (i2 <= i) {
                        return true;
                    }
                    if (i2 >= j) {
                        return false;
                    }
                    double d = MathHelper.lerpFromProgress((double)i2, (double)i, (double)j, 1.0, 0.0);
                    AbstractRandom abstractRandom = randomDeriver.createRandom(this.context.x, i2, this.context.z);
                    return (double)abstractRandom.nextFloat() < d;
                }
            }
            return new VerticalGradientPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static enum SteepMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final CodecHolder<SteepMaterialCondition> CODEC;

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.steepSlopePredicate;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    static enum HoleMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final CodecHolder<HoleMaterialCondition> CODEC;

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.negativeRunDepthPredicate;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    static enum SurfaceMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final CodecHolder<SurfaceMaterialCondition> CODEC;

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.surfacePredicate;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    static enum TemperatureMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final CodecHolder<TemperatureMaterialCondition> CODEC;

        @Override
        public CodecHolder<? extends MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.biomeTemperaturePredicate;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    record ConditionMaterialRule(MaterialCondition ifTrue, MaterialRule thenRun) implements MaterialRule
    {
        static final CodecHolder<ConditionMaterialRule> CODEC = CodecHolder.of(RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)MaterialCondition.CODEC.fieldOf("if_true")).forGetter(ConditionMaterialRule::ifTrue), ((MapCodec)MaterialRule.CODEC.fieldOf("then_run")).forGetter(ConditionMaterialRule::thenRun)).apply((Applicative<ConditionMaterialRule, ?>)instance, ConditionMaterialRule::new)));

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return new ConditionalBlockStateRule((BooleanSupplier)this.ifTrue.apply(materialRuleContext), (BlockStateRule)this.thenRun.apply(materialRuleContext));
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    public static interface MaterialRule
    extends Function<MaterialRuleContext, BlockStateRule> {
        public static final Codec<MaterialRule> CODEC = Registry.MATERIAL_RULE.getCodec().dispatch(materialRule -> materialRule.codec().codec(), Function.identity());

        public static Codec<? extends MaterialRule> registerAndGetDefault(Registry<Codec<? extends MaterialRule>> registry) {
            MaterialRules.register(registry, "bandlands", TerracottaBandsMaterialRule.CODEC);
            MaterialRules.register(registry, "block", BlockMaterialRule.CODEC);
            MaterialRules.register(registry, "sequence", SequenceMaterialRule.CODEC);
            return MaterialRules.register(registry, "condition", ConditionMaterialRule.CODEC);
        }

        public CodecHolder<? extends MaterialRule> codec();
    }

    record SequenceMaterialRule(List<MaterialRule> sequence) implements MaterialRule
    {
        static final CodecHolder<SequenceMaterialRule> CODEC = CodecHolder.of(MaterialRule.CODEC.listOf().xmap(SequenceMaterialRule::new, SequenceMaterialRule::sequence).fieldOf("sequence"));

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            if (this.sequence.size() == 1) {
                return (BlockStateRule)this.sequence.get(0).apply(materialRuleContext);
            }
            ImmutableList.Builder builder = ImmutableList.builder();
            for (MaterialRule materialRule : this.sequence) {
                builder.add((BlockStateRule)materialRule.apply(materialRuleContext));
            }
            return new SequenceBlockStateRule((List<BlockStateRule>)((Object)builder.build()));
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record BlockMaterialRule(BlockState resultState, SimpleBlockStateRule rule) implements MaterialRule
    {
        static final CodecHolder<BlockMaterialRule> CODEC = CodecHolder.of(BlockState.CODEC.xmap(BlockMaterialRule::new, BlockMaterialRule::resultState).fieldOf("result_state"));

        BlockMaterialRule(BlockState resultState) {
            this(resultState, new SimpleBlockStateRule(resultState));
        }

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return this.rule;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    static enum TerracottaBandsMaterialRule implements MaterialRule
    {
        INSTANCE;

        static final CodecHolder<TerracottaBandsMaterialRule> CODEC;

        @Override
        public CodecHolder<? extends MaterialRule> codec() {
            return CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return materialRuleContext.surfaceBuilder::getTerracottaBlock;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }

        static {
            CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));
        }
    }

    record SequenceBlockStateRule(List<BlockStateRule> rules) implements BlockStateRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int i, int j, int k) {
            for (BlockStateRule blockStateRule : this.rules) {
                BlockState blockState = blockStateRule.tryApply(i, j, k);
                if (blockState == null) continue;
                return blockState;
            }
            return null;
        }
    }

    record ConditionalBlockStateRule(BooleanSupplier condition, BlockStateRule followup) implements BlockStateRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int i, int j, int k) {
            if (!this.condition.get()) {
                return null;
            }
            return this.followup.tryApply(i, j, k);
        }
    }

    record SimpleBlockStateRule(BlockState state) implements BlockStateRule
    {
        @Override
        public BlockState tryApply(int i, int j, int k) {
            return this.state;
        }
    }

    protected static interface BlockStateRule {
        @Nullable
        public BlockState tryApply(int var1, int var2, int var3);
    }

    record InvertedBooleanSupplier(BooleanSupplier target) implements BooleanSupplier
    {
        @Override
        public boolean get() {
            return !this.target.get();
        }
    }

    static abstract class FullLazyAbstractPredicate
    extends LazyAbstractPredicate {
        protected FullLazyAbstractPredicate(MaterialRuleContext materialRuleContext) {
            super(materialRuleContext);
        }

        @Override
        protected long getCurrentUniqueValue() {
            return this.context.uniquePosValue;
        }
    }

    static abstract class HorizontalLazyAbstractPredicate
    extends LazyAbstractPredicate {
        protected HorizontalLazyAbstractPredicate(MaterialRuleContext materialRuleContext) {
            super(materialRuleContext);
        }

        @Override
        protected long getCurrentUniqueValue() {
            return this.context.uniqueHorizontalPosValue;
        }
    }

    static abstract class LazyAbstractPredicate
    implements BooleanSupplier {
        protected final MaterialRuleContext context;
        private long uniqueValue;
        @Nullable
        Boolean result;

        protected LazyAbstractPredicate(MaterialRuleContext context) {
            this.context = context;
            this.uniqueValue = this.getCurrentUniqueValue() - 1L;
        }

        @Override
        public boolean get() {
            long l = this.getCurrentUniqueValue();
            if (l == this.uniqueValue) {
                if (this.result == null) {
                    throw new IllegalStateException("Update triggered but the result is null");
                }
                return this.result;
            }
            this.uniqueValue = l;
            this.result = this.test();
            return this.result;
        }

        protected abstract long getCurrentUniqueValue();

        protected abstract boolean test();
    }

    static interface BooleanSupplier {
        public boolean get();
    }

    protected static final class MaterialRuleContext {
        private static final int field_36274 = 8;
        private static final int field_36275 = 4;
        private static final int field_36276 = 16;
        private static final int field_36277 = 15;
        final SurfaceBuilder surfaceBuilder;
        final BooleanSupplier biomeTemperaturePredicate = new BiomeTemperaturePredicate(this);
        final BooleanSupplier steepSlopePredicate = new SteepSlopePredicate(this);
        final BooleanSupplier negativeRunDepthPredicate = new NegativeRunDepthPredicate(this);
        final BooleanSupplier surfacePredicate = new SurfacePredicate();
        final NoiseConfig noiseConfig;
        final Chunk chunk;
        private final ChunkNoiseSampler chunkNoiseSampler;
        private final Function<BlockPos, RegistryEntry<Biome>> posToBiome;
        final HeightContext heightContext;
        private long field_36278 = Long.MAX_VALUE;
        private final int[] field_36279 = new int[4];
        long uniqueHorizontalPosValue = -9223372036854775807L;
        int x;
        int z;
        int runDepth;
        private long field_35677 = this.uniqueHorizontalPosValue - 1L;
        private double field_35678;
        private long field_35679 = this.uniqueHorizontalPosValue - 1L;
        private int surfaceMinY;
        long uniquePosValue = -9223372036854775807L;
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        Supplier<RegistryEntry<Biome>> biomeSupplier;
        int y;
        int fluidHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected MaterialRuleContext(SurfaceBuilder surfaceBuilder, NoiseConfig noiseConfig, Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, Function<BlockPos, RegistryEntry<Biome>> posToBiome, Registry<Biome> registry, HeightContext heightContext) {
            this.surfaceBuilder = surfaceBuilder;
            this.noiseConfig = noiseConfig;
            this.chunk = chunk;
            this.chunkNoiseSampler = chunkNoiseSampler;
            this.posToBiome = posToBiome;
            this.heightContext = heightContext;
        }

        protected void initHorizontalContext(int x, int z) {
            ++this.uniqueHorizontalPosValue;
            ++this.uniquePosValue;
            this.x = x;
            this.z = z;
            this.runDepth = this.surfaceBuilder.method_39552(x, z);
        }

        protected void initVerticalContext(int stoneDepthAbove, int stoneDepthBelow, int fluidHeight, int x, int y, int z) {
            ++this.uniquePosValue;
            this.biomeSupplier = Suppliers.memoize(() -> this.posToBiome.apply(this.pos.set(x, y, z)));
            this.y = y;
            this.fluidHeight = fluidHeight;
            this.stoneDepthBelow = stoneDepthBelow;
            this.stoneDepthAbove = stoneDepthAbove;
        }

        protected double method_39550() {
            if (this.field_35677 != this.uniqueHorizontalPosValue) {
                this.field_35677 = this.uniqueHorizontalPosValue;
                this.field_35678 = this.surfaceBuilder.method_39555(this.x, this.z);
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
                int j;
                this.field_35679 = this.uniqueHorizontalPosValue;
                int i = MaterialRuleContext.method_39903(this.x);
                long l = ChunkPos.toLong(i, j = MaterialRuleContext.method_39903(this.z));
                if (this.field_36278 != l) {
                    this.field_36278 = l;
                    this.field_36279[0] = this.chunkNoiseSampler.method_39900(MaterialRuleContext.method_39904(i), MaterialRuleContext.method_39904(j));
                    this.field_36279[1] = this.chunkNoiseSampler.method_39900(MaterialRuleContext.method_39904(i + 1), MaterialRuleContext.method_39904(j));
                    this.field_36279[2] = this.chunkNoiseSampler.method_39900(MaterialRuleContext.method_39904(i), MaterialRuleContext.method_39904(j + 1));
                    this.field_36279[3] = this.chunkNoiseSampler.method_39900(MaterialRuleContext.method_39904(i + 1), MaterialRuleContext.method_39904(j + 1));
                }
                int k = MathHelper.floor(MathHelper.lerp2((float)(this.x & 0xF) / 16.0f, (float)(this.z & 0xF) / 16.0f, this.field_36279[0], this.field_36279[1], this.field_36279[2], this.field_36279[3]));
                this.surfaceMinY = k + this.runDepth - 8;
            }
            return this.surfaceMinY;
        }

        static class BiomeTemperaturePredicate
        extends FullLazyAbstractPredicate {
            BiomeTemperaturePredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                return this.context.biomeSupplier.get().value().isCold(this.context.pos.set(this.context.x, this.context.y, this.context.z));
            }
        }

        static class SteepSlopePredicate
        extends HorizontalLazyAbstractPredicate {
            SteepSlopePredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                int r;
                int i = this.context.x & 0xF;
                int j = this.context.z & 0xF;
                int k = Math.max(j - 1, 0);
                int l = Math.min(j + 1, 15);
                Chunk chunk = this.context.chunk;
                int m = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, k);
                int n = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, l);
                if (n >= m + 4) {
                    return true;
                }
                int o = Math.max(i - 1, 0);
                int p = Math.min(i + 1, 15);
                int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, o, j);
                return q >= (r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, p, j)) + 4;
            }
        }

        static final class NegativeRunDepthPredicate
        extends HorizontalLazyAbstractPredicate {
            NegativeRunDepthPredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                return this.context.runDepth <= 0;
            }
        }

        final class SurfacePredicate
        implements BooleanSupplier {
            SurfacePredicate() {
            }

            @Override
            public boolean get() {
                return MaterialRuleContext.this.y >= MaterialRuleContext.this.method_39551();
            }
        }
    }
}

