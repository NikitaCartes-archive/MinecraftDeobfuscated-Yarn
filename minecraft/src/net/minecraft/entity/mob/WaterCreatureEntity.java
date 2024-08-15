package net.minecraft.entity.mob;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class WaterCreatureEntity extends PathAwareEntity {
	public static final int MIN_AMBIENT_SOUND_DELAY = 120;

	protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.doesNotIntersectEntities(this);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected int getXpToDrop() {
		return 1 + this.getWorld().random.nextInt(3);
	}

	protected void tickWaterBreathingAir(int air) {
		if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
			this.setAir(air - 1);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.damage(this.getDamageSources().drown(), 2.0F);
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
	public boolean canBeLeashed() {
		return false;
	}

	public static boolean canSpawn(EntityType<? extends WaterCreatureEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		int i = world.getSeaLevel();
		int j = i - 13;
		return pos.getY() >= j && pos.getY() <= i && world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER);
	}
}
