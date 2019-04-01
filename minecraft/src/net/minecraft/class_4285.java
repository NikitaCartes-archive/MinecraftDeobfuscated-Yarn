package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4285 extends class_437 {
	public class_4285() {
		super(new class_2585("Select episode"));
	}

	@Override
	protected void init() {
		super.init();
		int i = 24;
		int j = this.height / 4;
		this.addButton(new class_4185(this.width / 2 - 100, j, 200, 20, "The player is you!", arg -> this.minecraft.method_1507(new class_4283())));
		this.addButton(new class_4285.class_4286(this.width / 2 - 100, j + 24, "Knee-deep in lava"));
		this.addButton(new class_4285.class_4286(this.width / 2 - 100, j + 48, "Not just the endermen"));
		this.addButton(new class_4285.class_4286(this.width / 2 - 100, j + 72, "Removing Herobrine"));
		this.addButton(new class_4285.class_4286(this.width / 2 - 100, j + 96, "All these worlds are yours except..."));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.getTitle().method_10863(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		super.render(i, j, f);

		for (class_339 lv : this.buttons) {
			if (lv.isHovered() && lv instanceof class_4285.class_4286) {
				this.renderTooltip("Available in registered version", i, j);
				break;
			}
		}
	}

	@Override
	public void onClose() {
		this.minecraft.method_1507(new class_442());
	}

	@Environment(EnvType.CLIENT)
	static class class_4286 extends class_4185 {
		public class_4286(int i, int j, String string) {
			super(i, j, 200, 20, string, arg -> {
			});
			this.active = false;
		}
	}
}
