package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderNavigation extends MobNavigation {
	private BlockPos field_6687;

	public SpiderNavigation(MobEntity entity, World world) {
		super(entity, world);
	}

	@Override
	public Path findPathTo(BlockPos target, int distance) {
		this.field_6687 = target;
		return super.findPathTo(target, distance);
	}

	@Override
	public Path findPathTo(Entity entity, int distance) {
		this.field_6687 = new BlockPos(entity);
		return super.findPathTo(entity, distance);
	}

	@Override
	public boolean startMovingTo(Entity entity, double speed) {
		Path path = this.findPathTo(entity, 0);
		if (path != null) {
			return this.startMovingAlong(path, speed);
		} else {
			this.field_6687 = new BlockPos(entity);
			this.speed = speed;
			return true;
		}
	}

	@Override
	public void tick() {
		if (!this.isIdle()) {
			super.tick();
		} else {
			if (this.field_6687 != null) {
				if (!this.field_6687.isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth())
					&& (
						!(this.entity.getY() > (double)this.field_6687.getY())
							|| !new BlockPos((double)this.field_6687.getX(), this.entity.getY(), (double)this.field_6687.getZ())
								.isWithinDistance(this.entity.getPos(), (double)this.entity.getWidth())
					)) {
					this.entity.getMoveControl().moveTo((double)this.field_6687.getX(), (double)this.field_6687.getY(), (double)this.field_6687.getZ(), this.speed);
				} else {
					this.field_6687 = null;
				}
			}
		}
	}
}
