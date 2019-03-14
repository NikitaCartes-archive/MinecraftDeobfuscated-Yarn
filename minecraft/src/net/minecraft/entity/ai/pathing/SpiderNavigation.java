package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpiderNavigation extends MobNavigation {
	private BlockPos field_6687;

	public SpiderNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	public Path findPathTo(BlockPos blockPos) {
		this.field_6687 = blockPos;
		return super.findPathTo(blockPos);
	}

	@Override
	public Path findPathTo(Entity entity) {
		this.field_6687 = new BlockPos(entity);
		return super.findPathTo(entity);
	}

	@Override
	public boolean startMovingTo(Entity entity, double d) {
		Path path = this.findPathTo(entity);
		if (path != null) {
			return this.startMovingAlong(path, d);
		} else {
			this.field_6687 = new BlockPos(entity);
			this.speed = d;
			return true;
		}
	}

	@Override
	public void tick() {
		if (!this.isIdle()) {
			super.tick();
		} else {
			if (this.field_6687 != null) {
				double d = (double)(this.entity.getWidth() * this.entity.getWidth());
				if (!(this.entity.squaredDistanceToCenter(this.field_6687) < d)
					&& (
						!(this.entity.y > (double)this.field_6687.getY())
							|| !(this.entity.squaredDistanceToCenter(new BlockPos(this.field_6687.getX(), MathHelper.floor(this.entity.y), this.field_6687.getZ())) < d)
					)) {
					this.entity.getMoveControl().moveTo((double)this.field_6687.getX(), (double)this.field_6687.getY(), (double)this.field_6687.getZ(), this.speed);
				} else {
					this.field_6687 = null;
				}
			}
		}
	}
}
