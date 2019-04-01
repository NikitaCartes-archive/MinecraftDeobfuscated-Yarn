package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_407 extends class_410 {
	private final String field_2372;
	private final String field_2373;
	private final String field_2371;
	private final boolean field_2370;

	public class_407(class_411 arg, String string, int i, boolean bl) {
		super(arg, new class_2588(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), new class_2585(string), i);
		this.field_2402 = class_1074.method_4662(bl ? "chat.link.open" : "gui.yes");
		this.field_2399 = class_1074.method_4662(bl ? "gui.cancel" : "gui.no");
		this.field_2373 = class_1074.method_4662("chat.copy");
		this.field_2372 = class_1074.method_4662("chat.link.warning");
		this.field_2370 = !bl;
		this.field_2371 = string;
	}

	@Override
	protected void init() {
		super.init();
		this.buttons.clear();
		this.children.clear();
		this.addButton(
			new class_4185(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.field_2402, arg -> this.field_2403.confirmResult(true, this.field_2398))
		);
		this.addButton(new class_4185(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.field_2373, arg -> {
			this.method_2100();
			this.field_2403.confirmResult(false, this.field_2398);
		}));
		this.addButton(
			new class_4185(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.field_2399, arg -> this.field_2403.confirmResult(false, this.field_2398))
		);
	}

	public void method_2100() {
		this.minecraft.field_1774.method_1455(this.field_2371);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		if (this.field_2370) {
			this.drawCenteredString(this.font, this.field_2372, this.width / 2, 110, 16764108);
		}
	}
}
