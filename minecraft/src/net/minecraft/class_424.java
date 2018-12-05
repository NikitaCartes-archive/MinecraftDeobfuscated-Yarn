package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;

@Environment(EnvType.CLIENT)
public class class_424 extends Gui {
	private final String field_2482;

	public class_424(String string) {
		this.field_2482 = string;
	}

	@Override
	public boolean canClose() {
		return false;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.drawStringCentered(this.fontRenderer, this.field_2482, this.width / 2, 70, 16777215);
		super.draw(i, j, f);
	}
}
