package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AmphibiousSwimNavigation extends EntityNavigation {
	public AmphibiousSwimNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createPathNodeNavigator(int range) {
		this.nodeMaker = new AmphibiousPathNodeMaker(false);
		this.nodeMaker.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.nodeMaker, range);
	}

	@Override
	protected boolean isAtValidPosition() {
		return true;
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
		return this.isInLiquid() ? doesNotCollide(this.entity, origin, target, false) : false;
	}

	@Override
	public boolean isValidPosition(BlockPos pos) {
		return !this.world.getBlockState(pos.down()).isAir();
	}

	@Override
	public void setCanSwim(boolean canSwim) {
	}
}
