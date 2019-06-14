package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderNavigation extends MobNavigation {
	private BlockPos field_6687;

	public SpiderNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	public Path method_6348(BlockPos blockPos) {
		this.field_6687 = blockPos;
		return super.method_6348(blockPos);
	}

	@Override
	public Path method_6349(Entity entity) {
		this.field_6687 = new BlockPos(entity);
		return super.method_6349(entity);
	}

	@Override
	public boolean startMovingTo(Entity entity, double d) {
		Path path = this.method_6349(entity);
		if (path != null) {
			return this.method_6334(path, d);
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
				if (!this.field_6687.isWithinDistance(this.entity.method_19538(), (double)this.entity.getWidth())
					&& (
						!(this.entity.y > (double)this.field_6687.getY())
							|| !new BlockPos((double)this.field_6687.getX(), this.entity.y, (double)this.field_6687.getZ())
								.isWithinDistance(this.entity.method_19538(), (double)this.entity.getWidth())
					)) {
					this.entity.getMoveControl().moveTo((double)this.field_6687.getX(), (double)this.field_6687.getY(), (double)this.field_6687.getZ(), this.speed);
				} else {
					this.field_6687 = null;
				}
			}
		}
	}
}
