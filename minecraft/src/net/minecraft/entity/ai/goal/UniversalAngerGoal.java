package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;

public class UniversalAngerGoal<T extends MobEntity & Angerable> extends Goal {
	private final T mob;
	private final boolean triggerOthers;
	private int field_25606;

	public UniversalAngerGoal(T mob, boolean triggerOthers) {
		this.mob = mob;
		this.triggerOthers = triggerOthers;
	}

	@Override
	public boolean canStart() {
		return this.mob.world.getGameRules().getBoolean(GameRules.field_25402) && this.method_29932();
	}

	private boolean method_29932() {
		return this.mob.getAttacker() != null && this.mob.getAttacker().getType() == EntityType.field_6097 && this.mob.getLastAttackedTime() > this.field_25606;
	}

	@Override
	public void start() {
		this.field_25606 = this.mob.getLastAttackedTime();
		this.mob.universallyAnger();
		if (this.triggerOthers) {
			this.getOthersInRange().stream().filter(entity -> entity != this.mob).map(entity -> (Angerable)entity).forEach(Angerable::universallyAnger);
		}

		super.start();
	}

	private List<MobEntity> getOthersInRange() {
		double d = this.mob.getAttributeValue(EntityAttributes.field_23717);
		Box box = Box.method_29968(this.mob.getPos()).expand(d, 10.0, d);
		return this.mob.world.getEntitiesIncludingUngeneratedChunks(this.mob.getClass(), box);
	}
}
