package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_419 extends class_437 {
	private final class_2561 field_2457;
	private List<String> field_2458;
	private final class_437 field_2456;
	private int field_2454;

	public class_419(class_437 arg, String string, class_2561 arg2) {
		super(new class_2588(string));
		this.field_2456 = arg;
		this.field_2457 = arg2;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.field_2458 = this.font.method_1728(this.field_2457.method_10863(), this.width - 50);
		this.field_2454 = this.field_2458.size() * 9;
		this.addButton(
			new class_4185(
				this.width / 2 - 100,
				Math.min(this.height / 2 + this.field_2454 / 2 + 9, this.height - 30),
				200,
				20,
				class_1074.method_4662("gui.toMenu"),
				arg -> this.minecraft.method_1507(this.field_2456)
			)
		);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, this.height / 2 - this.field_2454 / 2 - 9 * 2, 11184810);
		int k = this.height / 2 - this.field_2454 / 2;
		if (this.field_2458 != null) {
			for (String string : this.field_2458) {
				this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
				k += 9;
			}
		}

		super.render(i, j, f);
	}
}
