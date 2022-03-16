/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public final class NoiseChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> NoiseChunkGenerator.method_41042(instance).and(instance.group(RegistryOps.createRegistryCodec(Registry.NOISE_WORLDGEN).forGetter(generator -> generator.noiseRegistry), ((MapCodec)BiomeSource.CODEC.fieldOf("biome_source")).forGetter(generator -> generator.populationSource), ((MapCodec)ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")).forGetter(generator -> generator.settings))).apply((Applicative<NoiseChunkGenerator, ?>)instance, instance.stable(NoiseChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected final BlockState defaultBlock;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    protected final RegistryEntry<ChunkGeneratorSettings> settings;
    private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

    public NoiseChunkGenerator(Registry<StructureSet> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> registryEntry) {
        this(noiseRegistry, structuresRegistry, biomeSource, biomeSource, registryEntry);
    }

    private NoiseChunkGenerator(Registry<StructureSet> noiseRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry, BiomeSource populationSource, BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> registryEntry) {
        super(noiseRegistry, Optional.empty(), populationSource, biomeSource);
        this.noiseRegistry = structuresRegistry;
        this.settings = registryEntry;
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
        this.defaultBlock = chunkGeneratorSettings.defaultBlock();
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
        int i = chunkGeneratorSettings.seaLevel();
        AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(i, chunkGeneratorSettings.defaultFluid());
        AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.generationShapeConfig().minimumY() - 1, Blocks.AIR.getDefaultState());
        this.fluidLevelSampler = (x, y, z) -> {
            if (y < Math.min(-54, i)) {
                return fluidLevel;
            }
            return fluidLevel2;
        };
    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Registry<Biome> biomeRegistry, Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
            this.populateBiomes(blender, noiseConfig, structureAccessor, chunk);
            return chunk;
        }), Util.getMainWorkerExecutor());
    }

    private void populateBiomes(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk2) {
        ChunkNoiseSampler chunkNoiseSampler = chunk2.getOrCreateChunkNoiseSampler(chunk -> this.method_41537((Chunk)chunk, structureAccessor, blender, noiseConfig));
        BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(blender.getBiomeSupplier(this.biomeSource), chunk2);
        chunk2.populateBiomes(biomeSupplier, chunkNoiseSampler.createMultiNoiseSampler(noiseConfig.router(), this.settings.value().spawnTarget()));
    }

    private ChunkNoiseSampler method_41537(Chunk chunk, StructureAccessor structureAccessor, Blender blender, NoiseConfig noiseConfig) {
        return ChunkNoiseSampler.create(chunk, noiseConfig.router(), new StructureWeightSampler(structureAccessor, chunk), this.settings.value(), this.fluidLevelSampler, blender, noiseConfig.aquiferRandom(), noiseConfig.oreRandom());
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    public RegistryEntry<ChunkGeneratorSettings> method_41541() {
        return this.settings;
    }

    public boolean matchesSettings(RegistryKey<ChunkGeneratorSettings> registryKey) {
        return this.settings.matchesKey(registryKey);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.sampleHeightmap(world, noiseConfig, x, z, null, heightmap.getBlockPredicate()).orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        MutableObject<VerticalBlockSample> mutableObject = new MutableObject<VerticalBlockSample>();
        this.sampleHeightmap(world, noiseConfig, x, z, mutableObject, null);
        return mutableObject.getValue();
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos blockPos) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        NoiseRouter noiseRouter = noiseConfig.router();
        DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double d = noiseRouter.ridges().sample(unblendedNoisePos);
        text.add("NoiseRouter T: " + decimalFormat.format(noiseRouter.temperature().sample(unblendedNoisePos)) + " V: " + decimalFormat.format(noiseRouter.vegetation().sample(unblendedNoisePos)) + " C: " + decimalFormat.format(noiseRouter.continents().sample(unblendedNoisePos)) + " E: " + decimalFormat.format(noiseRouter.erosion().sample(unblendedNoisePos)) + " D: " + decimalFormat.format(noiseRouter.depth().sample(unblendedNoisePos)) + " W: " + decimalFormat.format(d) + " PV: " + decimalFormat.format(DensityFunctions.method_41546((float)d)) + " AS: " + decimalFormat.format(noiseRouter.initialDensityWithoutJaggedness().sample(unblendedNoisePos)) + " N: " + decimalFormat.format(noiseRouter.finalDensity().sample(unblendedNoisePos)));
    }

    private OptionalInt sampleHeightmap(HeightLimitView heightLimitView, NoiseConfig noiseConfig, int i, int j, @Nullable MutableObject<VerticalBlockSample> mutableObject, @Nullable Predicate<BlockState> predicate) {
        BlockState[] blockStates;
        GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
        int k = Math.max(generationShapeConfig.minimumY(), heightLimitView.getBottomY());
        int l = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), heightLimitView.getTopY());
        int m = MathHelper.floorDiv(k, generationShapeConfig.verticalBlockSize());
        int n = MathHelper.floorDiv(l - k, generationShapeConfig.verticalBlockSize());
        if (n <= 0) {
            return OptionalInt.empty();
        }
        if (mutableObject == null) {
            blockStates = null;
        } else {
            blockStates = new BlockState[n * generationShapeConfig.verticalBlockSize()];
            mutableObject.setValue(new VerticalBlockSample(k, blockStates));
        }
        int o = generationShapeConfig.horizontalBlockSize();
        int p = generationShapeConfig.verticalBlockSize();
        int q = Math.floorDiv(i, o);
        int r = Math.floorDiv(j, o);
        int s = Math.floorMod(i, o);
        int t = Math.floorMod(j, o);
        int u = q * o;
        int v = r * o;
        double d = (double)s / (double)o;
        double e = (double)t / (double)o;
        ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(1, heightLimitView, noiseConfig.router(), u, v, DensityFunctionTypes.Beardifier.INSTANCE, this.settings.value(), this.fluidLevelSampler, Blender.getNoBlending(), noiseConfig.aquiferRandom(), noiseConfig.oreRandom());
        chunkNoiseSampler.sampleStartNoise();
        chunkNoiseSampler.sampleEndNoise(0);
        for (int w = n - 1; w >= 0; --w) {
            chunkNoiseSampler.sampleNoiseCorners(w, 0);
            for (int x = p - 1; x >= 0; --x) {
                BlockState blockState2;
                int y = (m + w) * p + x;
                double f = (double)x / (double)p;
                chunkNoiseSampler.sampleNoiseY(y, f);
                chunkNoiseSampler.sampleNoiseX(i, d);
                chunkNoiseSampler.sampleNoise(j, e);
                BlockState blockState = chunkNoiseSampler.sampleBlockState();
                BlockState blockState3 = blockState2 = blockState == null ? this.defaultBlock : blockState;
                if (blockStates != null) {
                    int z = w * p + x;
                    blockStates[z] = blockState2;
                }
                if (predicate == null || !predicate.test(blockState2)) continue;
                chunkNoiseSampler.method_40537();
                return OptionalInt.of(y + 1);
            }
        }
        chunkNoiseSampler.method_40537();
        return OptionalInt.empty();
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if (SharedConstants.method_37896(chunk.getPos())) {
            return;
        }
        HeightContext heightContext = new HeightContext(this, region);
        this.method_41538(chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), Blender.getBlender(region));
    }

    @VisibleForTesting
    public void method_41538(Chunk chunk2, HeightContext heightContext, NoiseConfig noiseConfig, StructureAccessor structureAccessor, BiomeAccess biomeAccess, Registry<Biome> registry, Blender blender) {
        ChunkNoiseSampler chunkNoiseSampler = chunk2.getOrCreateChunkNoiseSampler(chunk -> this.method_41537((Chunk)chunk, structureAccessor, blender, noiseConfig));
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
        noiseConfig.surfaceBuilder().buildSurface(noiseConfig, biomeAccess, registry, chunkGeneratorSettings.usesLegacyRandom(), heightContext, chunk2, chunkNoiseSampler, chunkGeneratorSettings.surfaceRule());
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk2, GenerationStep.Carver carver) {
        BiomeAccess biomeAccess2 = biomeAccess.withSource((i, j, k) -> this.populationSource.getBiome(i, j, k, noiseConfig.sampler()));
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        int i2 = 8;
        ChunkPos chunkPos = chunk2.getPos();
        ChunkNoiseSampler chunkNoiseSampler = chunk2.getOrCreateChunkNoiseSampler(chunk -> this.method_41537((Chunk)chunk, structureAccessor, Blender.getBlender(chunkRegion), noiseConfig));
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
        CarverContext carverContext = new CarverContext(this, chunkRegion.getRegistryManager(), chunk2.getHeightLimitView(), chunkNoiseSampler, noiseConfig, this.settings.value().surfaceRule());
        CarvingMask carvingMask = ((ProtoChunk)chunk2).getOrCreateCarvingMask(carver);
        for (int j2 = -8; j2 <= 8; ++j2) {
            for (int k2 = -8; k2 <= 8; ++k2) {
                ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j2, chunkPos.z + k2);
                Chunk chunk22 = chunkRegion.getChunk(chunkPos2.x, chunkPos2.z);
                GenerationSettings generationSettings = chunk22.setBiomeIfAbsent(() -> this.populationSource.getBiome(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()), noiseConfig.sampler())).value().getGenerationSettings();
                Iterable<RegistryEntry<ConfiguredCarver<?>>> iterable = generationSettings.getCarversForStep(carver);
                int l = 0;
                for (RegistryEntry<ConfiguredCarver<?>> registryEntry : iterable) {
                    ConfiguredCarver<?> configuredCarver = registryEntry.value();
                    chunkRandom.setCarverSeed(seed + (long)l, chunkPos2.x, chunkPos2.z);
                    if (configuredCarver.shouldCarve(chunkRandom)) {
                        configuredCarver.carve(carverContext, chunk2, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos2, carvingMask);
                    }
                    ++l;
                }
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk2) {
        GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
        HeightLimitView heightLimitView = chunk2.getHeightLimitView();
        int i = Math.max(generationShapeConfig.minimumY(), heightLimitView.getBottomY());
        int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), heightLimitView.getTopY());
        int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
        int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
        if (l <= 0) {
            return CompletableFuture.completedFuture(chunk2);
        }
        int m = chunk2.getSectionIndex(l * generationShapeConfig.verticalBlockSize() - 1 + i);
        int n = chunk2.getSectionIndex(i);
        HashSet<ChunkSection> set = Sets.newHashSet();
        for (int o = m; o >= n; --o) {
            ChunkSection chunkSection = chunk2.getSection(o);
            chunkSection.lock();
            set.add(chunkSection);
        }
        return CompletableFuture.supplyAsync(Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk2, k, l)), Util.getMainWorkerExecutor()).whenCompleteAsync((chunk, throwable) -> {
            for (ChunkSection chunkSection : set) {
                chunkSection.unlock();
            }
        }, executor);
    }

    private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk2, int i, int j) {
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
        ChunkNoiseSampler chunkNoiseSampler = chunk2.getOrCreateChunkNoiseSampler(chunk -> this.method_41537((Chunk)chunk, structureAccessor, blender, noiseConfig));
        Heightmap heightmap = chunk2.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk2.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunk2.getPos();
        int k = chunkPos.getStartX();
        int l = chunkPos.getStartZ();
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
        chunkNoiseSampler.sampleStartNoise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig();
        int m = generationShapeConfig.horizontalBlockSize();
        int n = generationShapeConfig.verticalBlockSize();
        int o = 16 / m;
        int p = 16 / m;
        for (int q = 0; q < o; ++q) {
            chunkNoiseSampler.sampleEndNoise(q);
            for (int r = 0; r < p; ++r) {
                ChunkSection chunkSection = chunk2.getSection(chunk2.countVerticalSections() - 1);
                for (int s = j - 1; s >= 0; --s) {
                    chunkNoiseSampler.sampleNoiseCorners(s, r);
                    for (int t = n - 1; t >= 0; --t) {
                        int u = (i + s) * n + t;
                        int v = u & 0xF;
                        int w = chunk2.getSectionIndex(u);
                        if (chunk2.getSectionIndex(chunkSection.getYOffset()) != w) {
                            chunkSection = chunk2.getSection(w);
                        }
                        double d = (double)t / (double)n;
                        chunkNoiseSampler.sampleNoiseY(u, d);
                        for (int x = 0; x < m; ++x) {
                            int y = k + q * m + x;
                            int z = y & 0xF;
                            double e = (double)x / (double)m;
                            chunkNoiseSampler.sampleNoiseX(y, e);
                            for (int aa = 0; aa < m; ++aa) {
                                int ab = l + r * m + aa;
                                int ac = ab & 0xF;
                                double f = (double)aa / (double)m;
                                chunkNoiseSampler.sampleNoise(ab, f);
                                BlockState blockState = chunkNoiseSampler.sampleBlockState();
                                if (blockState == null) {
                                    blockState = this.defaultBlock;
                                }
                                if ((blockState = this.getBlockState(chunkNoiseSampler, y, u, ab, blockState)) == AIR || SharedConstants.method_37896(chunk2.getPos())) continue;
                                if (blockState.getLuminance() != 0 && chunk2 instanceof ProtoChunk) {
                                    mutable.set(y, u, ab);
                                    ((ProtoChunk)chunk2).addLightSource(mutable);
                                }
                                chunkSection.setBlockState(z, v, ac, blockState, false);
                                heightmap.trackUpdate(z, u, ac, blockState);
                                heightmap2.trackUpdate(z, u, ac, blockState);
                                if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) continue;
                                mutable.set(y, u, ab);
                                chunk2.markBlockForPostProcessing(mutable);
                            }
                        }
                    }
                }
            }
            chunkNoiseSampler.swapBuffers();
        }
        chunkNoiseSampler.method_40537();
        return chunk2;
    }

    private BlockState getBlockState(ChunkNoiseSampler chunkNoiseSampler, int x, int y, int z, BlockState state) {
        return state;
    }

    @Override
    public int getWorldHeight() {
        return this.settings.value().generationShapeConfig().height();
    }

    @Override
    public int getSeaLevel() {
        return this.settings.value().seaLevel();
    }

    @Override
    public int getMinimumY() {
        return this.settings.value().generationShapeConfig().minimumY();
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        if (this.settings.value().mobGenerationDisabled()) {
            return;
        }
        ChunkPos chunkPos = region.getCenterPos();
        RegistryEntry<Biome> registryEntry = region.getBiome(chunkPos.getStartPos().withY(region.getTopY() - 1));
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
        SpawnHelper.populateEntities(region, registryEntry, chunkPos, chunkRandom);
    }
}

