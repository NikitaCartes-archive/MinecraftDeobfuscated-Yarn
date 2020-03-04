package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;

public class TheEndDimension extends Dimension {
	public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
	private final EnderDragonFight enderDragonFight;

	public TheEndDimension(World world, DimensionType type) {
		super(world, type, 0.0F);
		CompoundTag compoundTag = world.getLevelProperties().getWorldData(DimensionType.THE_END);
		this.enderDragonFight = world instanceof ServerWorld ? new EnderDragonFight((ServerWorld)world, compoundTag.getCompound("DragonFight")) : null;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
		floatingIslandsChunkGeneratorConfig.setDefaultBlock(Blocks.END_STONE.getDefaultState());
		floatingIslandsChunkGeneratorConfig.setDefaultFluid(Blocks.AIR.getDefaultState());
		floatingIslandsChunkGeneratorConfig.withCenter(this.getForcedSpawnPoint());
		return ChunkGeneratorType.FLOATING_ISLANDS
			.create(
				this.world, BiomeSourceType.THE_END.applyConfig(BiomeSourceType.THE_END.getConfig(this.world.getLevelProperties())), floatingIslandsChunkGeneratorConfig
			);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d.multiply(0.15F);
	}

	@Environment(EnvType.CLIENT)
	@Override
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

	@Environment(EnvType.CLIENT)
	@Override
	public float getCloudHeight() {
		return 8.0F;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
		Random random = new Random(this.world.getSeed());
		BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
		return this.world.getTopNonAirState(blockPos).getMaterial().blocksMovement() ? blockPos : null;
	}

	@Override
	public BlockPos getForcedSpawnPoint() {
		return SPAWN_POINT;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
		return this.getSpawningBlockInChunk(new ChunkPos(x >> 4, z >> 4), checkMobSpawnValidity);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
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
	public EnderDragonFight getEnderDragonFight() {
		return this.enderDragonFight;
	}
}
