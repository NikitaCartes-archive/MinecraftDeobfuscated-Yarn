package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal extends TrackTargetGoal {
	private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
	private boolean groupRevenge;
	private int lastAttackedTime;
	private final Class<?>[] noRevengeTypes;
	private Class<?>[] noHelpTypes;

	public RevengeGoal(MobEntityWithAi mobEntityWithAi, Class<?>... classs) {
		super(mobEntityWithAi, true);
		this.noRevengeTypes = classs;
		this.setControls(EnumSet.of(Goal.Control.field_18408));
	}

	@Override
	public boolean canStart() {
		int i = this.mob.getLastAttackedTime();
		LivingEntity livingEntity = this.mob.getAttacker();
		if (i != this.lastAttackedTime && livingEntity != null) {
			for (Class<?> class_ : this.noRevengeTypes) {
				if (class_.isAssignableFrom(livingEntity.getClass())) {
					return false;
				}
			}

			return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
		} else {
			return false;
		}
	}

	public RevengeGoal setGroupRevenge(Class<?>... classs) {
		this.groupRevenge = true;
		this.noHelpTypes = classs;
		return this;
	}

	@Override
	public void start() {
		this.mob.setTarget(this.mob.getAttacker());
		this.target = this.mob.getTarget();
		this.lastAttackedTime = this.mob.getLastAttackedTime();
		this.maxTimeWithoutVisibility = 300;
		if (this.groupRevenge) {
			this.callSameTypeForRevenge();
		}

		super.start();
	}

	protected void callSameTypeForRevenge() {
		double d = this.getFollowRange();
		List<MobEntity> list = this.mob
			.world
			.method_21728(this.mob.getClass(), new Box(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0, this.mob.y + 1.0, this.mob.z + 1.0).expand(d, 10.0, d));
		Iterator var4 = list.iterator();

		while (true) {
			MobEntity mobEntity;
			while (true) {
				if (!var4.hasNext()) {
					return;
				}

				mobEntity = (MobEntity)var4.next();
				if (this.mob != mobEntity
					&& mobEntity.getTarget() == null
					&& (!(this.mob instanceof TameableEntity) || ((TameableEntity)this.mob).getOwner() == ((TameableEntity)mobEntity).getOwner())
					&& !mobEntity.isTeammate(this.mob.getAttacker())) {
					if (this.noHelpTypes == null) {
						break;
					}

					boolean bl = false;

					for (Class<?> class_ : this.noHelpTypes) {
						if (mobEntity.getClass() == class_) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						break;
					}
				}
			}

			this.setMobEntityTarget(mobEntity, this.mob.getAttacker());
		}
	}

	protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
		mobEntity.setTarget(livingEntity);
	}
}
