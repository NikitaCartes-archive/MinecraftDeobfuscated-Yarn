package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_446 extends class_437 {
	private final class_437 field_2637;
	private final class_315 field_2638;
	private class_353 field_2639;
	private static final class_316[] field_2640 = new class_316[]{
		class_316.field_1938,
		class_316.field_1933,
		class_316.field_1924,
		class_316.field_1935,
		class_316.field_1927,
		class_316.field_1934,
		class_316.field_1922,
		class_316.field_18192,
		class_316.field_1945,
		class_316.field_1937,
		class_316.field_1932,
		class_316.field_1919,
		class_316.field_18190,
		class_316.field_18184,
		class_316.field_18189
	};

	public class_446(class_437 arg, class_315 arg2) {
		super(new class_2588("options.videoTitle"));
		this.field_2637 = arg;
		this.field_2638 = arg2;
	}

	@Override
	protected void init() {
		this.field_2639 = new class_353(this.minecraft, this.width, this.height, 32, this.height - 32, 25, field_2640);
		this.children.add(this.field_2639);
		this.addButton(new class_4185(this.width / 2 - 100, this.height - 27, 200, 20, class_1074.method_4662("gui.done"), arg -> {
			this.minecraft.field_1690.method_1640();
			this.minecraft.field_1704.method_4475();
			this.minecraft.method_1507(this.field_2637);
		}));
	}

	@Override
	public void removed() {
		this.minecraft.field_1690.method_1640();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = this.field_2638.field_1868;
		if (super.mouseClicked(d, e, i)) {
			if (this.field_2638.field_1868 != j) {
				this.minecraft.method_15993();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		int j = this.field_2638.field_1868;
		if (super.mouseReleased(d, e, i)) {
			return true;
		} else if (this.field_2639.mouseReleased(d, e, i)) {
			if (this.field_2638.field_1868 != j) {
				this.minecraft.method_15993();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2639.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 5, 16777215);
		super.render(i, j, f);
	}
}
