package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BoundingBox;

public class class_1399 extends TrackTargetGoal {
	private static final class_4051 field_18091 = new class_4051().method_18422().method_18424();
	private boolean field_6639;
	private int field_6638;
	private final Class<?>[] field_6637;
	private Class<?>[] field_6640;

	public class_1399(MobEntityWithAi mobEntityWithAi, Class<?>... classs) {
		super(mobEntityWithAi, true);
		this.field_6637 = classs;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		int i = this.entity.getLastAttackedTime();
		LivingEntity livingEntity = this.entity.getAttacker();
		if (i != this.field_6638 && livingEntity != null) {
			for (Class<?> class_ : this.field_6637) {
				if (class_.isAssignableFrom(livingEntity.getClass())) {
					return false;
				}
			}

			return this.canTrack(livingEntity, field_18091);
		} else {
			return false;
		}
	}

	public class_1399 method_6318(Class<?>... classs) {
		this.field_6639 = true;
		this.field_6640 = classs;
		return this;
	}

	@Override
	public void start() {
		this.entity.setTarget(this.entity.getAttacker());
		this.field_6664 = this.entity.getTarget();
		this.field_6638 = this.entity.getLastAttackedTime();
		this.maxTimeWithoutVisibility = 300;
		if (this.field_6639) {
			this.method_6317();
		}

		super.start();
	}

	protected void method_6317() {
		double d = this.getFollowRange();

		for (MobEntity mobEntity : this.entity
			.world
			.method_18467(
				this.entity.getClass(),
				new BoundingBox(this.entity.x, this.entity.y, this.entity.z, this.entity.x + 1.0, this.entity.y + 1.0, this.entity.z + 1.0).expand(d, 10.0, d)
			)) {
			if (this.entity != mobEntity
				&& mobEntity.getTarget() == null
				&& (!(this.entity instanceof TameableEntity) || ((TameableEntity)this.entity).getOwner() == ((TameableEntity)mobEntity).getOwner())
				&& !mobEntity.isTeammate(this.entity.getAttacker())) {
				boolean bl = false;

				for (Class<?> class_ : this.field_6640) {
					if (mobEntity.getClass() == class_) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.method_6319(mobEntity, this.entity.getAttacker());
				}
			}
		}
	}

	protected void method_6319(MobEntity mobEntity, LivingEntity livingEntity) {
		mobEntity.setTarget(livingEntity);
	}
}
