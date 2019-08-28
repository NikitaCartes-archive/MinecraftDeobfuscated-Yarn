/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public class OverworldDimension
extends Dimension {
    public OverworldDimension(World world, DimensionType dimensionType) {
        super(world, dimensionType);
    }

    @Override
    public DimensionType getType() {
        return DimensionType.OVERWORLD;
    }

    public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
        LevelGeneratorType levelGeneratorType = this.world.getLevelProperties().getGeneratorType();
        ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> chunkGeneratorType = ChunkGeneratorType.FLAT;
        ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> chunkGeneratorType2 = ChunkGeneratorType.DEBUG;
        ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> chunkGeneratorType3 = ChunkGeneratorType.CAVES;
        ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> chunkGeneratorType4 = ChunkGeneratorType.FLOATING_ISLANDS;
        ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> chunkGeneratorType5 = ChunkGeneratorType.SURFACE;
        BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType = BiomeSourceType.FIXED;
        BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType2 = BiomeSourceType.VANILLA_LAYERED;
        BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> biomeSourceType3 = BiomeSourceType.CHECKERBOARD;
        if (levelGeneratorType == LevelGeneratorType.FLAT) {
            FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.fromDynamic(new Dynamic<CompoundTag>(NbtOps.INSTANCE, this.world.getLevelProperties().getGeneratorOptions()));
            FixedBiomeSourceConfig fixedBiomeSourceConfig = biomeSourceType.getConfig(this.world.getLevelProperties()).setBiome(flatChunkGeneratorConfig.getBiome());
            return chunkGeneratorType.create(this.world, biomeSourceType.applyConfig(fixedBiomeSourceConfig), flatChunkGeneratorConfig);
        }
        if (levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            FixedBiomeSourceConfig fixedBiomeSourceConfig2 = biomeSourceType.getConfig(this.world.getLevelProperties()).setBiome(Biomes.PLAINS);
            return chunkGeneratorType2.create(this.world, biomeSourceType.applyConfig(fixedBiomeSourceConfig2), chunkGeneratorType2.createSettings());
        }
        if (levelGeneratorType == LevelGeneratorType.BUFFET) {
            BiomeSource biomeSource = null;
            JsonElement jsonElement = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.world.getLevelProperties().getGeneratorOptions());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject jsonObject2 = jsonObject.getAsJsonObject("biome_source");
            if (jsonObject2 != null && jsonObject2.has("type") && jsonObject2.has("options")) {
                BiomeSourceType<?, ?> biomeSourceType4 = Registry.BIOME_SOURCE_TYPE.get(new Identifier(jsonObject2.getAsJsonPrimitive("type").getAsString()));
                JsonObject jsonObject3 = jsonObject2.getAsJsonObject("options");
                Biome[] biomes = new Biome[]{Biomes.OCEAN};
                if (jsonObject3.has("biomes")) {
                    Biome[] biomeArray;
                    JsonArray jsonArray = jsonObject3.getAsJsonArray("biomes");
                    if (jsonArray.size() > 0) {
                        biomeArray = new Biome[jsonArray.size()];
                    } else {
                        Biome[] biomeArray2 = new Biome[1];
                        biomeArray = biomeArray2;
                        biomeArray2[0] = Biomes.OCEAN;
                    }
                    biomes = biomeArray;
                    for (int i = 0; i < jsonArray.size(); ++i) {
                        biomes[i] = Registry.BIOME.getOrEmpty(new Identifier(jsonArray.get(i).getAsString())).orElse(Biomes.OCEAN);
                    }
                }
                if (BiomeSourceType.FIXED == biomeSourceType4) {
                    FixedBiomeSourceConfig fixedBiomeSourceConfig3 = biomeSourceType.getConfig(this.world.getLevelProperties()).setBiome(biomes[0]);
                    biomeSource = biomeSourceType.applyConfig(fixedBiomeSourceConfig3);
                }
                if (BiomeSourceType.CHECKERBOARD == biomeSourceType4) {
                    int j = jsonObject3.has("size") ? jsonObject3.getAsJsonPrimitive("size").getAsInt() : 2;
                    CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig = biomeSourceType3.getConfig(this.world.getLevelProperties()).method_8777(biomes).method_8780(j);
                    biomeSource = biomeSourceType3.applyConfig(checkerboardBiomeSourceConfig);
                }
                if (BiomeSourceType.VANILLA_LAYERED == biomeSourceType4) {
                    VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig = biomeSourceType2.getConfig(this.world.getLevelProperties());
                    biomeSource = biomeSourceType2.applyConfig(vanillaLayeredBiomeSourceConfig);
                }
            }
            if (biomeSource == null) {
                biomeSource = biomeSourceType.applyConfig(biomeSourceType.getConfig(this.world.getLevelProperties()).setBiome(Biomes.OCEAN));
            }
            BlockState blockState = Blocks.STONE.getDefaultState();
            BlockState blockState2 = Blocks.WATER.getDefaultState();
            JsonObject jsonObject4 = jsonObject.getAsJsonObject("chunk_generator");
            if (jsonObject4 != null && jsonObject4.has("options")) {
                JsonObject jsonObject5 = jsonObject4.getAsJsonObject("options");
                if (jsonObject5.has("default_block")) {
                    String string = jsonObject5.getAsJsonPrimitive("default_block").getAsString();
                    blockState = Registry.BLOCK.get(new Identifier(string)).getDefaultState();
                }
                if (jsonObject5.has("default_fluid")) {
                    String string = jsonObject5.getAsJsonPrimitive("default_fluid").getAsString();
                    blockState2 = Registry.BLOCK.get(new Identifier(string)).getDefaultState();
                }
            }
            if (jsonObject4 != null && jsonObject4.has("type")) {
                ChunkGeneratorType<?, ?> chunkGeneratorType6 = Registry.CHUNK_GENERATOR_TYPE.get(new Identifier(jsonObject4.getAsJsonPrimitive("type").getAsString()));
                if (ChunkGeneratorType.CAVES == chunkGeneratorType6) {
                    CavesChunkGeneratorConfig cavesChunkGeneratorConfig = chunkGeneratorType3.createSettings();
                    cavesChunkGeneratorConfig.setDefaultBlock(blockState);
                    cavesChunkGeneratorConfig.setDefaultFluid(blockState2);
                    return chunkGeneratorType3.create(this.world, biomeSource, cavesChunkGeneratorConfig);
                }
                if (ChunkGeneratorType.FLOATING_ISLANDS == chunkGeneratorType6) {
                    FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = chunkGeneratorType4.createSettings();
                    floatingIslandsChunkGeneratorConfig.withCenter(new BlockPos(0, 64, 0));
                    floatingIslandsChunkGeneratorConfig.setDefaultBlock(blockState);
                    floatingIslandsChunkGeneratorConfig.setDefaultFluid(blockState2);
                    return chunkGeneratorType4.create(this.world, biomeSource, floatingIslandsChunkGeneratorConfig);
                }
            }
            OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = chunkGeneratorType5.createSettings();
            overworldChunkGeneratorConfig.setDefaultBlock(blockState);
            overworldChunkGeneratorConfig.setDefaultFluid(blockState2);
            return chunkGeneratorType5.create(this.world, biomeSource, overworldChunkGeneratorConfig);
        }
        OverworldChunkGeneratorConfig overworldChunkGeneratorConfig2 = chunkGeneratorType5.createSettings();
        VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig2 = biomeSourceType2.getConfig(this.world.getLevelProperties()).setGeneratorSettings(overworldChunkGeneratorConfig2);
        return chunkGeneratorType5.create(this.world, biomeSourceType2.applyConfig(vanillaLayeredBiomeSourceConfig2), overworldChunkGeneratorConfig2);
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = this.getTopSpawningBlockPosition(i, j, bl);
                if (blockPos == null) continue;
                return blockPos;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
        Biome biome = this.world.getBiome(mutable);
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        if (bl && !blockState.getBlock().matches(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk worldChunk = this.world.method_8497(i >> 4, j >> 4);
        int k = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 0xF, j & 0xF);
        if (k < 0) {
            return null;
        }
        if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 0xF, j & 0xF) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 0xF, j & 0xF)) {
            return null;
        }
        for (int l = k + 1; l >= 0; --l) {
            mutable.set(i, l, j);
            BlockState blockState2 = this.world.getBlockState(mutable);
            if (!blockState2.getFluidState().isEmpty()) break;
            if (!blockState2.equals(blockState)) continue;
            return mutable.up().toImmutable();
        }
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        double d = MathHelper.fractionalPart((double)l / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    @Override
    public boolean hasVisibleSky() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d getFogColor(float f, float g) {
        float h = MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        float i = 0.7529412f;
        float j = 0.84705883f;
        float k = 1.0f;
        return new Vec3d(i *= h * 0.94f + 0.06f, j *= h * 0.94f + 0.06f, k *= h * 0.91f + 0.09f);
    }

    @Override
    public boolean canPlayersSleep() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderFog(int i, int j) {
        return false;
    }
}

