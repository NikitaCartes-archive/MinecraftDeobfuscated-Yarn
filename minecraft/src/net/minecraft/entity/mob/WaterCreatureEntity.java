package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class WaterCreatureEntity extends PathAwareEntity {
	protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return 1 + this.world.random.nextInt(3);
	}

	protected void tickWaterBreathingAir(int air) {
		if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
			this.setAir(air - 1);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.damage(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setAir(300);
		}
	}

	@Override
	public void baseTick() {
		int i = this.getAir();
		super.baseTick();
		this.tickWaterBreathingAir(i);
	}

	@Override
	public boolean isPushedByFluids() {
		return false;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	public static boolean canSpawnUnderground(
		EntityType<? extends LivingEntity> entityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random
	) {
		return pos.getY() < world.getSeaLevel()
			&& pos.getY() < world.getTopY(Heightmap.Type.OCEAN_FLOOR, pos.getX(), pos.getZ())
			&& hasNoLight(world, pos)
			&& hasStoneFloor(pos, world);
	}

	/**
	 * Returns if the position has a stone floor.
	 * 
	 * @implNote This method checks if a block in the {@link net.minecraft.tag.BlockTags#BASE_STONE_OVERWORLD}
	 * tag exists within 5 blocks, and all blocks in between are water.
	 */
	public static boolean hasStoneFloor(BlockPos pos, ServerWorldAccess world) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 0; i < 5; i++) {
			mutable.move(Direction.DOWN);
			BlockState blockState = world.getBlockState(mutable);
			if (blockState.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
				return true;
			}

			if (!blockState.isOf(Blocks.WATER)) {
				return false;
			}
		}

		return false;
	}

	public static boolean hasNoLight(ServerWorldAccess world, BlockPos pos) {
		int i = world.toServerWorld().isThundering() ? world.getLightLevel(pos, 10) : world.getLightLevel(pos);
		return i == 0;
	}
}
