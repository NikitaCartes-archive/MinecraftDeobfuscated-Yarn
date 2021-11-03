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
import java.util.function.Supplier;
import java.util.stream.Stream;
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
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class MaterialRules {
    public static final MaterialCondition STONE_DEPTH_FLOOR = new StoneDepthMaterialCondition(false, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_FLOOR_WITH_RUN_DEPTH = new StoneDepthMaterialCondition(true, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition field_35494 = new StoneDepthMaterialCondition(false, VerticalSurfaceType.CEILING);
    public static final MaterialCondition STONE_DEPTH_CEILING = new StoneDepthMaterialCondition(true, VerticalSurfaceType.CEILING);

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

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d) {
        return MaterialRules.noiseThreshold(registryKey, d, Double.MAX_VALUE);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
        return new NoiseThresholdMaterialCondition(registryKey, d, e);
    }

    public static MaterialCondition method_39472(String string, YOffset yOffset, YOffset yOffset2) {
        return new VerticalGradientMaterialCondition(new Identifier(string), yOffset, yOffset2);
    }

    public static MaterialCondition steepSlope() {
        return SteepMaterialCondition.INSTANCE;
    }

    public static MaterialCondition hole() {
        return HoleMaterialCondition.INSTANCE;
    }

    public static MaterialCondition method_39473() {
        return SurfaceMaterialCondition.INSTANCE;
    }

    public static MaterialCondition temperature() {
        return TemperatureMaterialCondition.INSTANCE;
    }

    public static MaterialRule condition(MaterialCondition condition, MaterialRule rule) {
        return new ConditionMaterialRule(condition, rule);
    }

    public static MaterialRule sequence(MaterialRule firstRule, MaterialRule ... rules) {
        return new SequenceMaterialRule(Stream.concat(Stream.of(firstRule), Arrays.stream(rules)).toList());
    }

    public static MaterialRule block(BlockState state) {
        return new BlockMaterialRule(state);
    }

    public static MaterialRule terracottaBands() {
        return TerracottaBandsMaterialRule.INSTANCE;
    }

    record NotMaterialCondition(MaterialCondition target) implements MaterialCondition
    {
        static final Codec<NotMaterialCondition> CONDITION_CODEC = ((MapCodec)MaterialCondition.CODEC.xmap(NotMaterialCondition::new, NotMaterialCondition::target).fieldOf("invert")).codec();

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
        public static final Codec<MaterialCondition> CODEC = Registry.MATERIAL_CONDITION.dispatch(MaterialCondition::codec, Function.identity());

        public static Codec<? extends MaterialCondition> registerAndGetDefault() {
            Registry.register(Registry.MATERIAL_CONDITION, "biome", BiomeMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "noise_threshold", NoiseThresholdMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "vertical_gradient", VerticalGradientMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "y_above", AboveYMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "water", WaterMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "temperature", TemperatureMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "steep", SteepMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "not", NotMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "hole", HoleMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "above_preliminary_surface", SurfaceMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "stone_depth", StoneDepthMaterialCondition.CONDITION_CODEC);
            return (Codec)Registry.MATERIAL_CONDITION.iterator().next();
        }

        public Codec<? extends MaterialCondition> codec();
    }

    record AboveYMaterialCondition(YOffset anchor, int runDepthMultiplier, boolean addStoneDepth) implements MaterialCondition
    {
        static final Codec<AboveYMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)YOffset.OFFSET_CODEC.fieldOf("anchor")).forGetter(AboveYMaterialCondition::anchor), ((MapCodec)Codec.intRange(-20, 20).fieldOf("run_depth_multiplier")).forGetter(AboveYMaterialCondition::runDepthMultiplier), ((MapCodec)Codec.BOOL.fieldOf("add_stone_depth")).forGetter(AboveYMaterialCondition::addStoneDepth)).apply((Applicative<AboveYMaterialCondition, ?>)instance, AboveYMaterialCondition::new));

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
                    return this.context.y + (AboveYMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= AboveYMaterialCondition.this.anchor.getY(this.context.heightContext) + this.context.runDepth * AboveYMaterialCondition.this.runDepthMultiplier;
                }
            }
            return new AboveYPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record WaterMaterialCondition(int offset, int runDepthMultiplier, boolean addStoneDepth) implements MaterialCondition
    {
        static final Codec<WaterMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("offset")).forGetter(WaterMaterialCondition::offset), ((MapCodec)Codec.intRange(-20, 20).fieldOf("run_depth_multiplier")).forGetter(WaterMaterialCondition::runDepthMultiplier), ((MapCodec)Codec.BOOL.fieldOf("add_stone_depth")).forGetter(WaterMaterialCondition::addStoneDepth)).apply((Applicative<WaterMaterialCondition, ?>)instance, WaterMaterialCondition::new));

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
                    return this.context.fluidHeight == Integer.MIN_VALUE || this.context.y + (WaterMaterialCondition.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.fluidHeight + WaterMaterialCondition.this.offset + this.context.runDepth * WaterMaterialCondition.this.runDepthMultiplier;
                }
            }
            return new WaterPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record BiomeMaterialCondition(List<RegistryKey<Biome>> biomes) implements MaterialCondition
    {
        static final Codec<BiomeMaterialCondition> CONDITION_CODEC = ((MapCodec)RegistryKey.createCodec(Registry.BIOME_KEY).listOf().fieldOf("biome_is")).xmap(MaterialRules::biome, BiomeMaterialCondition::biomes).codec();

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final Set<RegistryKey<Biome>> set = Set.copyOf(this.biomes);
            class BiomePredicate
            extends FullLazyAbstractPredicate {
                BiomePredicate() {
                    super(materialRuleContext2);
                }

                @Override
                protected boolean test() {
                    return set.contains(this.context.biomeKeySupplier.get());
                }
            }
            return new BiomePredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
        }
    }

    record NoiseThresholdMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double minThreshold, double maxThreshold) implements MaterialCondition
    {
        static final Codec<NoiseThresholdMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryKey.createCodec(Registry.NOISE_WORLDGEN).fieldOf("noise")).forGetter(NoiseThresholdMaterialCondition::noise), ((MapCodec)Codec.DOUBLE.fieldOf("min_threshold")).forGetter(NoiseThresholdMaterialCondition::minThreshold), ((MapCodec)Codec.DOUBLE.fieldOf("max_threshold")).forGetter(NoiseThresholdMaterialCondition::maxThreshold)).apply((Applicative<NoiseThresholdMaterialCondition, ?>)instance, NoiseThresholdMaterialCondition::new));

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.surfaceBuilder.getNoiseSampler(this.noise);
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
        static final Codec<VerticalGradientMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("random_name")).forGetter(VerticalGradientMaterialCondition::randomName), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("true_at_and_below")).forGetter(VerticalGradientMaterialCondition::trueAtAndBelow), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("false_at_and_above")).forGetter(VerticalGradientMaterialCondition::falseAtAndAbove)).apply((Applicative<VerticalGradientMaterialCondition, ?>)instance, VerticalGradientMaterialCondition::new));

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
        }

        @Override
        public BooleanSupplier apply(final MaterialRuleContext materialRuleContext) {
            final int i = this.trueAtAndBelow().getY(materialRuleContext.heightContext);
            final int j = this.falseAtAndAbove().getY(materialRuleContext.heightContext);
            final RandomDeriver randomDeriver = materialRuleContext.surfaceBuilder.method_39482(this.randomName());
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
        public /* synthetic */ Object apply(Object object) {
            return this.apply((MaterialRuleContext)object);
        }
    }

    static enum SteepMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final Codec<SteepMaterialCondition> CONDITION_CODEC;

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
            CONDITION_CODEC = Codec.unit(INSTANCE);
        }
    }

    static enum HoleMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final Codec<HoleMaterialCondition> CONDITION_CODEC;

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
            CONDITION_CODEC = Codec.unit(INSTANCE);
        }
    }

    static enum SurfaceMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final Codec<SurfaceMaterialCondition> CONDITION_CODEC;

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
            CONDITION_CODEC = Codec.unit(INSTANCE);
        }
    }

    static enum TemperatureMaterialCondition implements MaterialCondition
    {
        INSTANCE;

        static final Codec<TemperatureMaterialCondition> CONDITION_CODEC;

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
            CONDITION_CODEC = Codec.unit(INSTANCE);
        }
    }

    record ConditionMaterialRule(MaterialCondition ifTrue, MaterialRule thenRun) implements MaterialRule
    {
        static final Codec<ConditionMaterialRule> RULE_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)MaterialCondition.CODEC.fieldOf("if_true")).forGetter(ConditionMaterialRule::ifTrue), ((MapCodec)MaterialRule.CODEC.fieldOf("then_run")).forGetter(ConditionMaterialRule::thenRun)).apply((Applicative<ConditionMaterialRule, ?>)instance, ConditionMaterialRule::new));

        @Override
        public Codec<? extends MaterialRule> codec() {
            return RULE_CODEC;
        }

        @Override
        public BlockStateRule apply(MaterialRuleContext materialRuleContext) {
            return new ConditionalBlockStateRule((BooleanSupplier)this.ifTrue.apply(materialRuleContext), (BlockStateRule)this.thenRun.apply(materialRuleContext));
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((MaterialRuleContext)object);
        }
    }

    public static interface MaterialRule
    extends Function<MaterialRuleContext, BlockStateRule> {
        public static final Codec<MaterialRule> CODEC = Registry.MATERIAL_RULE.dispatch(MaterialRule::codec, Function.identity());

        public static Codec<? extends MaterialRule> registerAndGetDefault() {
            Registry.register(Registry.MATERIAL_RULE, "bandlands", TerracottaBandsMaterialRule.RULE_CODEC);
            Registry.register(Registry.MATERIAL_RULE, "block", BlockMaterialRule.RULE_CODEC);
            Registry.register(Registry.MATERIAL_RULE, "sequence", SequenceMaterialRule.RULE_CODEC);
            Registry.register(Registry.MATERIAL_RULE, "condition", ConditionMaterialRule.RULE_CODEC);
            return (Codec)Registry.MATERIAL_RULE.iterator().next();
        }

        public Codec<? extends MaterialRule> codec();
    }

    record SequenceMaterialRule(List<MaterialRule> sequence) implements MaterialRule
    {
        static final Codec<SequenceMaterialRule> RULE_CODEC = ((MapCodec)MaterialRule.CODEC.listOf().xmap(SequenceMaterialRule::new, SequenceMaterialRule::sequence).fieldOf("sequence")).codec();

        @Override
        public Codec<? extends MaterialRule> codec() {
            return RULE_CODEC;
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
        public /* synthetic */ Object apply(Object object) {
            return this.apply((MaterialRuleContext)object);
        }
    }

    record BlockMaterialRule(BlockState resultState, SimpleBlockStateRule rule) implements MaterialRule
    {
        static final Codec<BlockMaterialRule> RULE_CODEC = ((MapCodec)BlockState.CODEC.xmap(BlockMaterialRule::new, BlockMaterialRule::resultState).fieldOf("result_state")).codec();

        BlockMaterialRule(BlockState resultState) {
            this(resultState, new SimpleBlockStateRule(resultState));
        }

        @Override
        public Codec<? extends MaterialRule> codec() {
            return RULE_CODEC;
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

        static final Codec<TerracottaBandsMaterialRule> RULE_CODEC;

        @Override
        public Codec<? extends MaterialRule> codec() {
            return RULE_CODEC;
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
            RULE_CODEC = Codec.unit(INSTANCE);
        }
    }

    record StoneDepthMaterialCondition(boolean addRunDepth, VerticalSurfaceType surfaceType) implements MaterialCondition
    {
        static final Codec<StoneDepthMaterialCondition> CONDITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("add_run_depth")).forGetter(StoneDepthMaterialCondition::addRunDepth), ((MapCodec)VerticalSurfaceType.CODEC.fieldOf("surface_type")).forGetter(StoneDepthMaterialCondition::surfaceType)).apply((Applicative<StoneDepthMaterialCondition, ?>)instance, StoneDepthMaterialCondition::new));

        @Override
        public Codec<? extends MaterialCondition> codec() {
            return CONDITION_CODEC;
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
                    return (bl ? this.context.stoneDepthBelow : this.context.stoneDepthAbove) <= 1 + (StoneDepthMaterialCondition.this.addRunDepth ? this.context.runDepth : 0);
                }
            }
            return new StoneDepthPredicate();
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
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
        final SurfaceBuilder surfaceBuilder;
        final BooleanSupplier biomeTemperaturePredicate = new BiomeTemperaturePredicate(this);
        final BooleanSupplier steepSlopePredicate = new SteepSlopePredicate(this);
        final BooleanSupplier negativeRunDepthPredicate = new NegativeRunDepthPredicate(this);
        final BooleanSupplier surfacePredicate = new SurfacePredicate();
        final Chunk chunk;
        private final Function<BlockPos, Biome> posToBiome;
        private final Registry<Biome> biomeRegistry;
        final HeightContext heightContext;
        long uniqueHorizontalPosValue = -9223372036854775807L;
        int x;
        int z;
        int runDepth;
        long uniquePosValue = -9223372036854775807L;
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        Supplier<Biome> biomeSupplier;
        Supplier<RegistryKey<Biome>> biomeKeySupplier;
        int surfaceMinY;
        int y;
        int fluidHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected MaterialRuleContext(SurfaceBuilder surfaceBuilder, Chunk chunk, Function<BlockPos, Biome> posToBiome, Registry<Biome> biomeRegistry, HeightContext heightContext) {
            this.surfaceBuilder = surfaceBuilder;
            this.chunk = chunk;
            this.posToBiome = posToBiome;
            this.biomeRegistry = biomeRegistry;
            this.heightContext = heightContext;
        }

        protected void initHorizontalContext(int x, int z, int runDepth) {
            ++this.uniqueHorizontalPosValue;
            ++this.uniquePosValue;
            this.x = x;
            this.z = z;
            this.runDepth = runDepth;
        }

        protected void initVerticalContext(int surfaceMinY, int stoneDepthAbove, int stoneDepthBelow, int fluidHeight, int x, int y, int z) {
            ++this.uniquePosValue;
            this.biomeSupplier = Suppliers.memoize(() -> this.posToBiome.apply(this.pos.set(x, y, z)));
            this.biomeKeySupplier = Suppliers.memoize(() -> this.biomeRegistry.getKey(this.biomeSupplier.get()).orElseThrow(() -> new IllegalStateException("Unregistered biome: " + this.biomeSupplier)));
            this.surfaceMinY = surfaceMinY;
            this.y = y;
            this.fluidHeight = fluidHeight;
            this.stoneDepthBelow = stoneDepthBelow;
            this.stoneDepthAbove = stoneDepthAbove;
        }

        static class BiomeTemperaturePredicate
        extends FullLazyAbstractPredicate {
            BiomeTemperaturePredicate(MaterialRuleContext materialRuleContext) {
                super(materialRuleContext);
            }

            @Override
            protected boolean test() {
                return this.context.biomeSupplier.get().getTemperature(this.context.pos.set(this.context.x, this.context.y, this.context.z)) < 0.15f;
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
                return MaterialRuleContext.this.y >= MaterialRuleContext.this.surfaceMinY;
            }
        }
    }
}

