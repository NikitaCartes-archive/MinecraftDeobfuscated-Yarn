package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4293 extends class_437 {
	public class_4293() {
		super(class_333.field_18967);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, class_1074.method_4662("feedback.line1"), this.width / 2, this.height / 2 - 15, 16777215);
		this.drawCenteredString(this.font, class_1074.method_4662("feedback.line2"), this.width / 2, this.height / 2 + 15, 16777215);
		super.render(i, j, f);
	}
}
