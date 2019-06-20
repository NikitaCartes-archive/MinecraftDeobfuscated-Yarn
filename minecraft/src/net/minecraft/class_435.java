package net.minecraft;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_435 extends class_437 implements class_3536 {
	private String field_2541 = "";
	private String field_2544 = "";
	private int field_2542;
	private boolean field_2543;

	public class_435() {
		super(class_333.field_18967);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void method_15412(class_2561 arg) {
		this.method_15413(arg);
	}

	@Override
	public void method_15413(class_2561 arg) {
		this.field_2541 = arg.method_10863();
		this.method_15414(new class_2588("progress.working"));
	}

	@Override
	public void method_15414(class_2561 arg) {
		this.field_2544 = arg.method_10863();
		this.method_15410(0);
	}

	@Override
	public void method_15410(int i) {
		this.field_2542 = i;
	}

	@Override
	public void method_15411() {
		this.field_2543 = true;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.field_2543) {
			if (!this.minecraft.method_1589()) {
				this.minecraft.method_1507(null);
			}
		} else {
			this.renderBackground();
			this.drawCenteredString(this.font, this.field_2541, this.width / 2, 70, 16777215);
			if (!Objects.equals(this.field_2544, "") && this.field_2542 != 0) {
				this.drawCenteredString(this.font, this.field_2544 + " " + this.field_2542 + "%", this.width / 2, 90, 16777215);
			}

			super.render(i, j, f);
		}
	}
}
