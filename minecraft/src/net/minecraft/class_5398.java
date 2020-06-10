package net.minecraft;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;

public class class_5398<T extends MobEntity & Angerable> extends Goal {
	private final T field_25604;
	private final boolean field_25605;
	private int field_25606;

	public class_5398(T mobEntity, boolean bl) {
		this.field_25604 = mobEntity;
		this.field_25605 = bl;
	}

	@Override
	public boolean canStart() {
		return this.field_25604.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.method_29932();
	}

	private boolean method_29932() {
		return this.field_25604.getAttacker() != null
			&& this.field_25604.getAttacker().getType() == EntityType.PLAYER
			&& this.field_25604.getLastAttackedTime() > this.field_25606;
	}

	@Override
	public void start() {
		this.field_25606 = this.field_25604.getLastAttackedTime();
		this.field_25604.method_29921();
		if (this.field_25605) {
			this.method_29933().stream().filter(mobEntity -> mobEntity != this.field_25604).map(mobEntity -> (Angerable)mobEntity).forEach(Angerable::method_29921);
		}

		super.start();
	}

	private List<MobEntity> method_29933() {
		double d = this.field_25604.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
		Box box = Box.method_29968(this.field_25604.getPos()).expand(d, 10.0, d);
		return this.field_25604.world.getEntitiesIncludingUngeneratedChunks(this.field_25604.getClass(), box);
	}
}
