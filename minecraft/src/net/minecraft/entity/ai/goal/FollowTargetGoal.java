package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_4051;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BoundingBox;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> field_6643;
	protected final int reciprocalChance;
	protected LivingEntity field_6644;
	protected class_4051 field_6642;

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, boolean bl) {
		this(mobEntity, class_, bl, false);
	}

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, boolean bl, boolean bl2) {
		this(mobEntity, class_, 10, bl, bl2, null);
	}

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
		super(mobEntity, bl, bl2);
		this.field_6643 = class_;
		this.reciprocalChance = i;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18408));
		this.field_6642 = new class_4051().method_18418(this.getFollowRange()).method_18420(predicate);
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.entity.getRand().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else {
			this.method_18415();
			return this.field_6644 != null;
		}
	}

	protected BoundingBox method_6321(double d) {
		return this.entity.method_5829().expand(d, 4.0, d);
	}

	protected void method_18415() {
		if (this.field_6643 != PlayerEntity.class && this.field_6643 != ServerPlayerEntity.class) {
			this.field_6644 = this.entity
				.field_6002
				.method_18465(
					this.field_6643,
					this.field_6642,
					this.entity,
					this.entity.x,
					this.entity.y + (double)this.entity.getStandingEyeHeight(),
					this.entity.z,
					this.method_6321(this.getFollowRange())
				);
		} else {
			this.field_6644 = this.entity
				.field_6002
				.method_18463(this.field_6642, this.entity, this.entity.x, this.entity.y + (double)this.entity.getStandingEyeHeight(), this.entity.z);
		}
	}

	@Override
	public void start() {
		this.entity.setTarget(this.field_6644);
		super.start();
	}
}
