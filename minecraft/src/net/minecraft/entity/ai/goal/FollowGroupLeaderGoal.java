package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal extends Goal {
	private final SchoolingFishEntity field_6441;
	private int moveDelay;
	private int checkSurroundingDelay;

	public FollowGroupLeaderGoal(SchoolingFishEntity schoolingFishEntity) {
		this.field_6441 = schoolingFishEntity;
		this.checkSurroundingDelay = this.method_6261(schoolingFishEntity);
	}

	protected int method_6261(SchoolingFishEntity schoolingFishEntity) {
		return 200 + schoolingFishEntity.getRand().nextInt(200) % 20;
	}

	@Override
	public boolean canStart() {
		if (this.field_6441.hasOtherFishInGroup()) {
			return false;
		} else if (this.field_6441.hasLeader()) {
			return true;
		} else if (this.checkSurroundingDelay > 0) {
			this.checkSurroundingDelay--;
			return false;
		} else {
			this.checkSurroundingDelay = this.method_6261(this.field_6441);
			Predicate<SchoolingFishEntity> predicate = schoolingFishEntityx -> schoolingFishEntityx.canHaveMoreFishInGroup() || !schoolingFishEntityx.hasLeader();
			List<SchoolingFishEntity> list = this.field_6441
				.field_6002
				.method_8390(this.field_6441.getClass(), this.field_6441.method_5829().expand(8.0, 8.0, 8.0), predicate);
			SchoolingFishEntity schoolingFishEntity = (SchoolingFishEntity)list.stream()
				.filter(SchoolingFishEntity::canHaveMoreFishInGroup)
				.findAny()
				.orElse(this.field_6441);
			schoolingFishEntity.pullInOtherFish(list.stream().filter(schoolingFishEntityx -> !schoolingFishEntityx.hasLeader()));
			return this.field_6441.hasLeader();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6441.hasLeader() && this.field_6441.isCloseEnoughToLeader();
	}

	@Override
	public void start() {
		this.moveDelay = 0;
	}

	@Override
	public void onRemove() {
		this.field_6441.leaveGroup();
	}

	@Override
	public void tick() {
		if (--this.moveDelay <= 0) {
			this.moveDelay = 10;
			this.field_6441.moveTowardLeader();
		}
	}
}
