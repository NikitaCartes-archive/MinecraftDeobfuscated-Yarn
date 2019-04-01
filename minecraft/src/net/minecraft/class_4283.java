package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4283 extends class_437 {
	private static final class_1940 field_19196 = new class_1940((long)"North Carolina".hashCode(), class_1934.field_9215, true, false, class_1942.field_9265)
		.method_8575();
	private class_4283.class_4284 field_19197;
	private int field_19198;

	public class_4283() {
		super(new class_2585("Select difficulty"));
	}

	@Override
	protected void init() {
		super.init();
		int i = 24;
		int j = this.height / 4;
		this.addButton(new class_4283.class_4284(this.width / 2 - 100, j + 0, "Hello, NoobVille", class_1267.field_5801));
		this.addButton(new class_4283.class_4284(this.width / 2 - 100, j + 24, "Filthy casual!", class_1267.field_5805));
		this.addButton(new class_4283.class_4284(this.width / 2 - 100, j + 48, "Lemon curry?", class_1267.field_5802));
		this.addButton(new class_4283.class_4284(this.width / 2 - 100, j + 72, "eXtreme to the MaXxXxX!", class_1267.field_5807));
		this.field_19197 = this.addButton(new class_4283.class_4284(this.width / 2 - 100, j + 96, "Obligatory nightmare mode", class_1267.field_5807, true));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.getTitle().method_10863(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		super.render(i, j, f);
		if (this.field_19197 != null && this.field_19197.isHovered()) {
			this.field_19198 = Math.min(255, this.field_19198 + 3);
		} else {
			this.field_19198 = Math.max(0, this.field_19198 - 3);
		}

		if (this.field_19198 != 0) {
			this.fillGradient(0, 0, this.width, this.height, 0, this.field_19198 << 24 | 0xFF0000);
		}
	}

	@Override
	public void onClose() {
		this.minecraft.method_1507(new class_4285());
	}

	@Environment(EnvType.CLIENT)
	class class_4284 extends class_4185 {
		private final class_1267 field_19200;
		private boolean field_19201;

		public class_4284(int i, int j, String string, class_1267 arg2) {
			this(i, j, string, arg2, false);
		}

		public class_4284(int i, int j, String string, class_1267 arg2, boolean bl) {
			super(i, j, 200, 20, string, arg3 -> {
				class_4283.this.minecraft.method_1559("shareware", "MoJang Presents", class_4283.field_19196);
				class_1132 lv = class_4283.this.minecraft.method_1576();
				lv.method_20231(() -> {
					lv.method_3776(arg2, true);
					lv.method_19467(true);
					lv.method_20288(bl);
				});
			});
			this.field_19200 = arg2;
			this.field_19201 = bl;
		}
	}
}
