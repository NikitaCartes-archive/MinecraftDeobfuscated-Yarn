package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_527 extends class_437 {
	private static final Object2IntMap<class_2874> field_3232 = class_156.method_654(
		new Object2IntOpenCustomHashMap<>(class_156.method_655()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(class_2874.field_13072, -13408734);
			object2IntOpenCustomHashMap.put(class_2874.field_13076, -10075085);
			object2IntOpenCustomHashMap.put(class_2874.field_13078, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final class_411 field_3233;
	private final class_1257 field_3234;

	public class_527(class_411 arg, String string, class_32 arg2) {
		super(new class_2588("optimizeWorld.title", arg2.method_231(string).method_150()));
		this.field_3233 = arg;
		this.field_3234 = new class_1257(string, arg2, arg2.method_231(string));
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 150, 200, 20, class_1074.method_4662("gui.cancel"), arg -> {
			this.field_3234.method_5402();
			this.field_3233.confirmResult(false, 0);
		}));
	}

	@Override
	public void tick() {
		if (this.field_3234.method_5403()) {
			this.field_3233.confirmResult(true, 0);
		}
	}

	@Override
	public void removed() {
		this.field_3234.method_5402();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
		int k = this.width / 2 - 150;
		int l = this.width / 2 + 150;
		int m = this.height / 4 + 100;
		int n = m + 10;
		this.drawCenteredString(this.font, this.field_3234.method_5394().method_10863(), this.width / 2, m - 9 - 2, 10526880);
		if (this.field_3234.method_5397() > 0) {
			fill(k - 1, m - 1, l + 1, n + 1, -16777216);
			this.drawString(this.font, class_1074.method_4662("optimizeWorld.info.converted", this.field_3234.method_5400()), k, 40, 10526880);
			this.drawString(this.font, class_1074.method_4662("optimizeWorld.info.skipped", this.field_3234.method_5399()), k, 40 + 9 + 3, 10526880);
			this.drawString(this.font, class_1074.method_4662("optimizeWorld.info.total", this.field_3234.method_5397()), k, 40 + (9 + 3) * 2, 10526880);
			int o = 0;

			for (class_2874 lv : class_2874.method_12482()) {
				int p = class_3532.method_15375(this.field_3234.method_5393(lv) * (float)(l - k));
				fill(k + o, m, k + o + p, n, field_3232.getInt(lv));
				o += p;
			}

			int q = this.field_3234.method_5400() + this.field_3234.method_5399();
			this.drawCenteredString(this.font, q + " / " + this.field_3234.method_5397(), this.width / 2, m + 2 * 9 + 2, 10526880);
			this.drawCenteredString(this.font, class_3532.method_15375(this.field_3234.method_5401() * 100.0F) + "%", this.width / 2, m + (n - m) / 2 - 9 / 2, 10526880);
		}

		super.render(i, j, f);
	}
}
