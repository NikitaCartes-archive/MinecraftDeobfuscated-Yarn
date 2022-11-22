package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimNavigation extends EntityNavigation {
	private boolean canJumpOutOfWater;

	public SwimNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int range) {
		this.canJumpOutOfWater = this.entity.getType() == EntityType.DOLPHIN;
		this.nodeMaker = new WaterPathNodeMaker(this.canJumpOutOfWater);
		return new PathNodeNavigator(this.nodeMaker, range);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.canJumpOutOfWater || this.isInLiquid();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.entity.getX(), this.entity.getBodyY(0.5), this.entity.getZ());
	}

	@Override
	protected double adjustTargetY(Vec3d pos) {
		return pos.y;
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
		return doesNotCollide(this.entity, origin, target, false);
	}

	@Override
	public boolean isValidPosition(BlockPos pos) {
		return !this.world.getBlockState(pos).isOpaqueFullCube(this.world, pos);
	}

	@Override
	public void setCanSwim(boolean canSwim) {
	}
}
