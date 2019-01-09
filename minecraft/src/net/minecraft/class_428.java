package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_428 extends class_437 {
	@Override
	protected void method_2224() {
		this.method_2219(new class_349(0, this.field_2561 / 2 - 155, this.field_2559 / 4 + 120 + 12, class_1074.method_4662("gui.toTitle")) {
			@Override
			public void method_1826(double d, double e) {
				class_428.this.field_2563.method_1507(new class_442());
			}
		});
		this.method_2219(new class_349(1, this.field_2561 / 2 - 155 + 160, this.field_2559 / 4 + 120 + 12, class_1074.method_4662("menu.quit")) {
			@Override
			public void method_1826(double d, double e) {
				class_428.this.field_2563.method_1592();
			}
		});
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, "Out of memory!", this.field_2561 / 2, this.field_2559 / 4 - 60 + 20, 16777215);
		this.method_1780(this.field_2554, "Minecraft has run out of memory.", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 0, 10526880);
		this.method_1780(
			this.field_2554, "This could be caused by a bug in the game or by the", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 18, 10526880
		);
		this.method_1780(this.field_2554, "Java Virtual Machine not being allocated enough", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 27, 10526880);
		this.method_1780(this.field_2554, "memory.", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 36, 10526880);
		this.method_1780(
			this.field_2554, "To prevent level corruption, the current game has quit.", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 54, 10526880
		);
		this.method_1780(
			this.field_2554, "We've tried to free up enough memory to let you go back to", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 63, 10526880
		);
		this.method_1780(
			this.field_2554, "the main menu and back to playing, but this may not have worked.", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 72, 10526880
		);
		this.method_1780(
			this.field_2554, "Please restart the game if you see this message again.", this.field_2561 / 2 - 140, this.field_2559 / 4 - 60 + 60 + 81, 10526880
		);
		super.method_2214(i, j, f);
	}
}
