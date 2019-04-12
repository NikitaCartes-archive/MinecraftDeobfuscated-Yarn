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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;

public class TheEndDimension extends Dimension {
	public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
	private final EnderDragonFight enderDragonFight;

	public TheEndDimension(World world, DimensionType dimensionType) {
		super(world, dimensionType);
		CompoundTag compoundTag = world.getLevelProperties().getWorldData(DimensionType.field_13078);
		this.enderDragonFight = world instanceof ServerWorld ? new EnderDragonFight((ServerWorld)world, compoundTag.getCompound("DragonFight")) : null;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig = ChunkGeneratorType.field_12770.createSettings();
		floatingIslandsChunkGeneratorConfig.setDefaultBlock(Blocks.field_10471.getDefaultState());
		floatingIslandsChunkGeneratorConfig.setDefaultFluid(Blocks.AIR.getDefaultState());
		floatingIslandsChunkGeneratorConfig.withCenter(this.getForcedSpawnPoint());
		return ChunkGeneratorType.field_12770
			.create(
				this.world, BiomeSourceType.THE_END.applyConfig(BiomeSourceType.THE_END.getConfig().method_9205(this.world.getSeed())), floatingIslandsChunkGeneratorConfig
			);
	}

	@Override
	public float getSkyAngle(long l, float f) {
		return 0.0F;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public float[] getBackgroundColor(float f, float g) {
		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d getFogColor(float f, float g) {
		int i = 10518688;
		float h = MathHelper.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		float j = 0.627451F;
		float k = 0.5019608F;
		float l = 0.627451F;
		j *= h * 0.0F + 0.15F;
		k *= h * 0.0F + 0.15F;
		l *= h * 0.0F + 0.15F;
		return new Vec3d((double)j, (double)k, (double)l);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12449() {
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
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
		Random random = new Random(this.world.getSeed());
		BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
		return this.world.getTopNonAirState(blockPos).getMaterial().suffocates() ? blockPos : null;
	}

	@Override
	public BlockPos getForcedSpawnPoint() {
		return SPAWN_POINT;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
		return this.getSpawningBlockInChunk(new ChunkPos(i >> 4, j >> 4), bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderFog(int i, int j) {
		return false;
	}

	@Override
	public DimensionType getType() {
		return DimensionType.field_13078;
	}

	@Override
	public void saveWorldData() {
		CompoundTag compoundTag = new CompoundTag();
		if (this.enderDragonFight != null) {
			compoundTag.put("DragonFight", this.enderDragonFight.toTag());
		}

		this.world.getLevelProperties().setWorldData(DimensionType.field_13078, compoundTag);
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
