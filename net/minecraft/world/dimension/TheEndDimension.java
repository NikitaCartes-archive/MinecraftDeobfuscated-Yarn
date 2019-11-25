/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import org.jetbrains.annotations.Nullable;

public class TheEndDimension
extends Dimension {
    public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
    private final EnderDragonFight enderDragonFight;

    public TheEndDimension(World world, DimensionType dimensionType) {
        super(world, dimensionType, 0.0f);
        CompoundTag compoundTag = world.getLevelProperties().getWorldData(DimensionType.THE_END);
        this.enderDragonFight = world instanceof ServerWorld ? new EnderDragonFight((ServerWorld)world, compoundTag.getCompound("DragonFight")) : null;
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
        floatingIslandsChunkGeneratorConfig.setDefaultBlock(Blocks.END_STONE.getDefaultState());
        floatingIslandsChunkGeneratorConfig.setDefaultFluid(Blocks.AIR.getDefaultState());
        floatingIslandsChunkGeneratorConfig.withCenter(this.getForcedSpawnPoint());
        return ChunkGeneratorType.FLOATING_ISLANDS.create(this.world, BiomeSourceType.THE_END.applyConfig(BiomeSourceType.THE_END.getConfig(this.world.getLevelProperties())), floatingIslandsChunkGeneratorConfig);
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.0f;
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public float[] getBackgroundColor(float f, float g) {
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d getFogColor(float f, float g) {
        int i = 0xA080A0;
        float h = MathHelper.cos(f * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        float j = 0.627451f;
        float k = 0.5019608f;
        float l = 0.627451f;
        return new Vec3d(j *= h * 0.0f + 0.15f, k *= h * 0.0f + 0.15f, l *= h * 0.0f + 0.15f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasGround() {
        return false;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getCloudHeight() {
        return 8.0f;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
        Random random = new Random(this.world.getSeed());
        BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
        return this.world.getTopNonAirState(blockPos).getMaterial().blocksMovement() ? blockPos : null;
    }

    @Override
    public BlockPos getForcedSpawnPoint() {
        return SPAWN_POINT;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return this.getSpawningBlockInChunk(new ChunkPos(i >> 4, j >> 4), bl);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isFogThick(int i, int j) {
        return false;
    }

    @Override
    public DimensionType getType() {
        return DimensionType.THE_END;
    }

    @Override
    public void saveWorldData() {
        CompoundTag compoundTag = new CompoundTag();
        if (this.enderDragonFight != null) {
            compoundTag.put("DragonFight", this.enderDragonFight.toTag());
        }
        this.world.getLevelProperties().setWorldData(DimensionType.THE_END, compoundTag);
    }

    @Override
    public void update() {
        if (this.enderDragonFight != null) {
            this.enderDragonFight.tick();
        }
    }

    @Nullable
    public EnderDragonFight method_12513() {
        return this.enderDragonFight;
    }
}

