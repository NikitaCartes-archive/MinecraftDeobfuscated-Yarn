package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_421 extends class_437 {
	private final String field_2467;

	public class_421(class_2561 arg, String string) {
		super(arg);
		this.field_2467 = string;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new class_4185(this.width / 2 - 100, 140, 200, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(null)));
	}

	@Override
	public void render(int i, int j, float f) {
		this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 90, 16777215);
		this.drawCenteredString(this.font, this.field_2467, this.width / 2, 110, 16777215);
		super.render(i, j, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
