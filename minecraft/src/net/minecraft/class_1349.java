package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.Goal;

public class class_1349 extends Goal {
	private final class_1425 field_6441;
	private int field_6440;
	private int field_6439;

	public class_1349(class_1425 arg) {
		this.field_6441 = arg;
		this.field_6439 = this.method_6261(arg);
	}

	protected int method_6261(class_1425 arg) {
		return 200 + arg.getRand().nextInt(200) % 20;
	}

	@Override
	public boolean canStart() {
		if (this.field_6441.method_6467()) {
			return false;
		} else if (this.field_6441.method_6470()) {
			return true;
		} else if (this.field_6439 > 0) {
			this.field_6439--;
			return false;
		} else {
			this.field_6439 = this.method_6261(this.field_6441);
			Predicate<class_1425> predicate = arg -> arg.method_6469() || !arg.method_6470();
			List<class_1425> list = this.field_6441.world.getEntities(this.field_6441.getClass(), this.field_6441.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
			class_1425 lv = (class_1425)list.stream().filter(class_1425::method_6469).findAny().orElse(this.field_6441);
			lv.method_6468(list.stream().filter(arg -> !arg.method_6470()));
			return this.field_6441.method_6470();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6441.method_6470() && this.field_6441.method_6464();
	}

	@Override
	public void start() {
		this.field_6440 = 0;
	}

	@Override
	public void onRemove() {
		this.field_6441.method_6466();
	}

	@Override
	public void tick() {
		if (--this.field_6440 <= 0) {
			this.field_6440 = 10;
			this.field_6441.method_6463();
		}
	}
}
