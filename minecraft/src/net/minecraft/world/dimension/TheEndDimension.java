package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_5217;
import net.minecraft.class_5268;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class TheEndDimension extends Dimension {
	public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
	private final EnderDragonFight enderDragonFight;

	public TheEndDimension(World world, DimensionType type) {
		super(world, type, 0.0F);
		if (world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld)world;
			class_5217 lv = serverWorld.getLevelProperties();
			if (lv instanceof class_5268) {
				CompoundTag compoundTag = ((class_5268)lv).getWorldData();
				this.enderDragonFight = new EnderDragonFight(serverWorld, compoundTag.getCompound("DragonFight"));
			} else {
				this.enderDragonFight = null;
			}
		} else {
			this.enderDragonFight = null;
		}
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl) {
		Random random = new Random(l);
		BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
		return this.world.getTopNonAirState(blockPos).getMaterial().blocksMovement() ? blockPos : null;
	}

	@Override
	public BlockPos getForcedSpawnPoint() {
		return SPAWN_POINT;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
		return this.getSpawningBlockInChunk(l, new ChunkPos(i >> 4, j >> 4), bl);
	}

	@Override
	public DimensionType getType() {
		return DimensionType.THE_END;
	}

	@Override
	public void saveWorldData(class_5268 arg) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.enderDragonFight != null) {
			compoundTag.put("DragonFight", this.enderDragonFight.toTag());
		}

		arg.setWorldData(compoundTag);
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
