package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_434 extends class_437 {
	public class_434() {
		super(class_333.field_18967);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, class_1074.method_4662("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
