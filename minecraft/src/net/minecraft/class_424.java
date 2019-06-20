package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_424 extends class_437 {
	public class_424(class_2561 arg) {
		super(arg);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 70, 16777215);
		super.render(i, j, f);
	}
}
