/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
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
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.jetbrains.annotations.Nullable;

public class MaterialRules {
    public static final MaterialCondition STONE_DEPTH_FLOOR = new StoneDepthMaterialCondition(false, VerticalSurfaceType.FLOOR);
    public static final MaterialCondition STONE_DEPTH_FLOOR_WITH_RUN_DEPTH = new StoneDepthMaterialCondition(true, VerticalSurfaceType.FLOOR);
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
        return MaterialRules.noiseThreshold(registryKey, d, Double.POSITIVE_INFINITY);
    }

    public static MaterialCondition noiseThreshold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, double d, double e) {
        return new NoiseThresholdMaterialCondition(registryKey, d, e);
    }

    public static MaterialCondition steepSlope() {
        return SteepMaterialCondition.INSTANCE;
    }

    public static MaterialCondition hole() {
        return HoleMaterialCondition.INSTANCE;
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
            Registry.register(Registry.MATERIAL_CONDITION, "y_above", AboveYMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "water", WaterMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "temperature", TemperatureMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "steep", SteepMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "not", NotMaterialCondition.CONDITION_CODEC);
            Registry.register(Registry.MATERIAL_CONDITION, "hole", HoleMaterialCondition.CONDITION_CODEC);
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
            extends AbstractPredicate<SurfaceContext> {
                AboveYPredicate() {
                }

                @Override
                protected boolean test(SurfaceContext surfaceContext) {
                    return surfaceContext.blockY + (AboveYMaterialCondition.this.addStoneDepth ? surfaceContext.stoneDepthAbove : 0) >= AboveYMaterialCondition.this.anchor.getY(materialRuleContext.heightContext) + surfaceContext.runDepth * AboveYMaterialCondition.this.runDepthMultiplier;
                }
            }
            AboveYPredicate aboveYPredicate = new AboveYPredicate();
            materialRuleContext.contextDependentPredicates.add(aboveYPredicate);
            return aboveYPredicate;
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
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            class WaterPredicate
            extends AbstractPredicate<SurfaceContext> {
                WaterPredicate() {
                }

                @Override
                protected boolean test(SurfaceContext surfaceContext) {
                    return surfaceContext.waterHeight == Integer.MIN_VALUE || surfaceContext.blockY + (WaterMaterialCondition.this.addStoneDepth ? surfaceContext.stoneDepthAbove : 0) >= surfaceContext.waterHeight + WaterMaterialCondition.this.offset + surfaceContext.runDepth * WaterMaterialCondition.this.runDepthMultiplier;
                }
            }
            WaterPredicate waterPredicate = new WaterPredicate();
            materialRuleContext.contextDependentPredicates.add(waterPredicate);
            return waterPredicate;
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
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            final Set<RegistryKey<Biome>> set = Set.copyOf(this.biomes);
            class BiomePredicate
            extends AbstractPredicate<RegistryKey<Biome>> {
                BiomePredicate() {
                }

                @Override
                protected boolean test(RegistryKey<Biome> registryKey) {
                    return set.contains(registryKey);
                }
            }
            BiomePredicate biomePredicate = new BiomePredicate();
            materialRuleContext.biomeDependentPredicates.add(biomePredicate);
            return biomePredicate;
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
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.surfaceBuilder.getNoiseSampler(this.noise);
            class NoiseThresholdPredicate
            extends LazyAbstractPredicate<MaterialRulePos> {
                NoiseThresholdPredicate() {
                }

                @Override
                protected boolean test(MaterialRulePos materialRulePos) {
                    double d = doublePerlinNoiseSampler.sample(materialRulePos.blockX, 0.0, materialRulePos.blockZ);
                    return d >= NoiseThresholdMaterialCondition.this.minThreshold && d <= NoiseThresholdMaterialCondition.this.maxThreshold;
                }
            }
            NoiseThresholdPredicate noiseThresholdPredicate = new NoiseThresholdPredicate();
            materialRuleContext.positionalPredicates.add(noiseThresholdPredicate);
            return noiseThresholdPredicate;
        }

        @Override
        public /* synthetic */ Object apply(Object context) {
            return this.apply((MaterialRuleContext)context);
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
        public /* synthetic */ Object apply(Object object) {
            return this.apply((MaterialRuleContext)object);
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
        public BooleanSupplier apply(MaterialRuleContext materialRuleContext) {
            final boolean bl = this.surfaceType == VerticalSurfaceType.CEILING;
            class StoneDepthPredicate
            extends AbstractPredicate<SurfaceContext> {
                StoneDepthPredicate() {
                }

                @Override
                protected boolean test(SurfaceContext surfaceContext) {
                    return (bl ? surfaceContext.stoneDepthBelow : surfaceContext.stoneDepthAbove) <= 1 + (StoneDepthMaterialCondition.this.addRunDepth ? surfaceContext.runDepth : 0);
                }
            }
            StoneDepthPredicate stoneDepthPredicate = new StoneDepthPredicate();
            materialRuleContext.contextDependentPredicates.add(stoneDepthPredicate);
            if (bl) {
                materialRuleContext.needsCeilingStoneDepth = true;
            }
            return stoneDepthPredicate;
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
            if (!this.condition.test()) {
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

    record MaterialRulePos(int blockX, int blockZ) {
    }

    record SurfaceContext(int blockY, int stoneDepthAbove, int stoneDepthBelow, int runDepth, int waterHeight) {
    }

    record InvertedBooleanSupplier(BooleanSupplier target) implements BooleanSupplier
    {
        @Override
        public boolean test() {
            return !this.target.test();
        }
    }

    static abstract class LazyAbstractPredicate<S>
    implements Predicate<S> {
        @Nullable
        private S context;
        @Nullable
        Boolean result;

        LazyAbstractPredicate() {
        }

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

        protected abstract boolean test(S var1);
    }

    static abstract class AbstractPredicate<S>
    implements Predicate<S> {
        boolean result = false;

        AbstractPredicate() {
        }

        @Override
        public void init(S context) {
            this.result = this.test(context);
        }

        @Override
        public boolean test() {
            return this.result;
        }

        protected abstract boolean test(S var1);
    }

    static interface Predicate<S>
    extends BooleanSupplier {
        public void init(S var1);
    }

    static interface BooleanSupplier {
        public boolean test();
    }

    protected static final class MaterialRuleContext {
        final SurfaceBuilder surfaceBuilder;
        final Predicate<BiomeTemperaturePredicate.BiomeTemperatureContext> biomeTemperaturePredicate = new BiomeTemperaturePredicate();
        final Predicate<SteepSlopePredicate.SteepSlopeContext> steepSlopePredicate = new SteepSlopePredicate();
        final Predicate<Integer> negativeRunDepthPredicate = new NegativePredicate();
        final List<Predicate<RegistryKey<Biome>>> biomeDependentPredicates = new ObjectArrayList<Predicate<RegistryKey<Biome>>>();
        final List<Predicate<MaterialRulePos>> positionalPredicates = new ObjectArrayList<Predicate<MaterialRulePos>>();
        final List<Predicate<SurfaceContext>> contextDependentPredicates = new ObjectArrayList<Predicate<SurfaceContext>>();
        boolean needsCeilingStoneDepth;
        final HeightContext heightContext;

        protected MaterialRuleContext(SurfaceBuilder surfaceBuilder, HeightContext heightContext) {
            this.surfaceBuilder = surfaceBuilder;
            this.heightContext = heightContext;
        }

        protected void initWorldDependentPredicates(Chunk chunk, int x, int z, int runDepth) {
            MaterialRulePos materialRulePos = new MaterialRulePos(x, z);
            for (Predicate<MaterialRulePos> predicate : this.positionalPredicates) {
                predicate.init(materialRulePos);
            }
            this.steepSlopePredicate.init(new SteepSlopePredicate.SteepSlopeContext(chunk, x, z));
            this.negativeRunDepthPredicate.init(runDepth);
        }

        protected void initContextDependentPredicates(RegistryKey<Biome> biomeKey, Biome biome, int runDepth, int stoneDepthAbove, int stoneDepthBelow, int waterHeight, int x, int y, int z) {
            for (Predicate<RegistryKey<Biome>> predicate : this.biomeDependentPredicates) {
                predicate.init(biomeKey);
            }
            SurfaceContext surfaceContext = new SurfaceContext(y, stoneDepthAbove, stoneDepthBelow, runDepth, waterHeight);
            for (Predicate<SurfaceContext> predicate2 : this.contextDependentPredicates) {
                predicate2.init(surfaceContext);
            }
            this.biomeTemperaturePredicate.init(new BiomeTemperaturePredicate.BiomeTemperatureContext(biome, x, y, z));
        }

        protected boolean needsCeilingStoneDepth() {
            return this.needsCeilingStoneDepth;
        }

        static class BiomeTemperaturePredicate
        extends LazyAbstractPredicate<BiomeTemperatureContext> {
            BiomeTemperaturePredicate() {
            }

            @Override
            protected boolean test(BiomeTemperatureContext biomeTemperatureContext) {
                return biomeTemperatureContext.biome.getTemperature(new BlockPos(biomeTemperatureContext.blockX, biomeTemperatureContext.blockY, biomeTemperatureContext.blockZ)) < 0.15f;
            }

            record BiomeTemperatureContext(Biome biome, int blockX, int blockY, int blockZ) {
            }
        }

        static class SteepSlopePredicate
        extends LazyAbstractPredicate<SteepSlopeContext> {
            SteepSlopePredicate() {
            }

            @Override
            protected boolean test(SteepSlopeContext steepSlopeContext) {
                int r;
                int i = steepSlopeContext.blockX & 0xF;
                int j = steepSlopeContext.blockZ & 0xF;
                int k = Math.max(j - 1, 0);
                int l = Math.min(j + 1, 15);
                int m = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, k);
                int n = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, i, l);
                if (n >= m + 4) {
                    return true;
                }
                int o = Math.max(i - 1, 0);
                int p = Math.min(i + 1, 15);
                int q = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, o, j);
                return q >= (r = steepSlopeContext.chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, p, j)) + 4;
            }

            record SteepSlopeContext(Chunk chunk, int blockX, int blockZ) {
            }
        }

        static final class NegativePredicate
        extends AbstractPredicate<Integer> {
            NegativePredicate() {
            }

            @Override
            protected boolean test(Integer integer) {
                return integer <= 0;
            }
        }
    }
}

