package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_458 extends class_437 {
	private static final class_316[] field_2726 = new class_316[]{class_316.field_1963, class_316.field_1944, class_316.field_1930, class_316.field_18195};
	private final class_437 field_2729;
	private final class_315 field_2724;
	public class_304 field_2727;
	public long field_2723;
	private class_459 field_2728;
	private class_4185 field_2725;

	public class_458(class_437 arg, class_315 arg2) {
		super(new class_2588("controls.title"));
		this.field_2729 = arg;
		this.field_2724 = arg2;
	}

	@Override
	protected void init() {
		int i = 0;

		for (class_316 lv : field_2726) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = 18 + 24 * (i >> 1);
			this.addButton(lv.method_18520(this.minecraft.field_1690, j, k, 150));
			i++;
		}

		this.field_2728 = new class_459(this, this.minecraft);
		this.children.add(this.field_2728);
		this.field_2725 = this.addButton(new class_4185(this.width / 2 - 155, this.height - 29, 150, 20, class_1074.method_4662("controls.resetAll"), arg -> {
			for (class_304 lvx : this.minecraft.field_1690.field_1839) {
				lvx.method_1422(lvx.method_1429());
			}

			class_304.method_1426();
		}));
		this.addButton(
			new class_4185(this.width / 2 - 155 + 160, this.height - 29, 150, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2729))
		);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2727 != null) {
			this.field_2724.method_1641(this.field_2727, class_3675.class_307.field_1672.method_1447(i));
			this.field_2727 = null;
			class_304.method_1426();
			return true;
		} else if (i == 0 && this.field_2728.mouseClicked(d, e, i)) {
			this.setDragging(true);
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0 && this.field_2728.mouseReleased(d, e, i)) {
			this.setDragging(false);
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2727 != null) {
			if (i == 256) {
				this.field_2724.method_1641(this.field_2727, class_3675.field_16237);
			} else {
				this.field_2724.method_1641(this.field_2727, class_3675.method_15985(i, j));
			}

			this.field_2727 = null;
			this.field_2723 = class_156.method_658();
			class_304.method_1426();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2728.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 8, 16777215);
		boolean bl = false;

		for (class_304 lv : this.field_2724.field_1839) {
			if (!lv.method_1427()) {
				bl = true;
				break;
			}
		}

		this.field_2725.active = bl;
		super.render(i, j, f);
	}
}
