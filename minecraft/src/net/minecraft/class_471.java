package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_471 extends class_465<class_1706> implements class_1712 {
	private static final class_2960 field_2819 = new class_2960("textures/gui/container/anvil.png");
	private class_342 field_2821;

	public class_471(class_1706 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void init() {
		super.init();
		this.minecraft.field_1774.method_1462(true);
		int i = (this.width - this.field_2792) / 2;
		int j = (this.height - this.field_2779) / 2;
		this.field_2821 = new class_342(this.font, i + 62, j + 24, 103, 12, class_1074.method_4662("container.repair"));
		this.field_2821.method_1856(false);
		this.field_2821.changeFocus(true);
		this.field_2821.method_1868(-1);
		this.field_2821.method_1860(-1);
		this.field_2821.method_1858(false);
		this.field_2821.method_1880(35);
		this.field_2821.method_1863(this::method_2403);
		this.children.add(this.field_2821);
		this.field_2797.method_7596(this);
		this.method_20085(this.field_2821);
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2821.method_1882();
		this.init(arg, i, j);
		this.field_2821.method_1852(string);
	}

	@Override
	public void removed() {
		super.removed();
		this.minecraft.field_1774.method_1462(false);
		this.field_2797.method_7603(this);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.minecraft.field_1724.method_7346();
		}

		return !this.field_2821.keyPressed(i, j, k) && !this.field_2821.method_20315() ? super.keyPressed(i, j, k) : true;
	}

	@Override
	protected void method_2388(int i, int j) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.font.method_1729(this.title.method_10863(), 60.0F, 6.0F, 4210752);
		int k = this.field_2797.method_17369();
		if (k > 0) {
			int l = 8453920;
			boolean bl = true;
			String string = class_1074.method_4662("container.repair.cost", k);
			if (k >= 40 && !this.minecraft.field_1724.field_7503.field_7477) {
				string = class_1074.method_4662("container.repair.expensive");
				l = 16736352;
			} else if (!this.field_2797.method_7611(2).method_7681()) {
				bl = false;
			} else if (!this.field_2797.method_7611(2).method_7674(this.field_17410.field_7546)) {
				l = 16736352;
			}

			if (bl) {
				int m = this.field_2792 - 8 - this.font.method_1727(string) - 2;
				int n = 69;
				fill(m - 2, 67, this.field_2792 - 8, 79, 1325400064);
				this.font.method_1720(string, (float)m, 69.0F, l);
			}
		}

		GlStateManager.enableLighting();
	}

	private void method_2403(String string) {
		if (!string.isEmpty()) {
			String string2 = string;
			class_1735 lv = this.field_2797.method_7611(0);
			if (lv != null && lv.method_7681() && !lv.method_7677().method_7938() && string.equals(lv.method_7677().method_7964().getString())) {
				string2 = "";
			}

			this.field_2797.method_7625(string2);
			this.minecraft.field_1724.field_3944.method_2883(new class_2855(string2));
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.method_2380(i, j);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.field_2821.render(i, j, f);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2819);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		this.blit(k + 59, l + 20, 0, this.field_2779 + (this.field_2797.method_7611(0).method_7681() ? 0 : 16), 110, 16);
		if ((this.field_2797.method_7611(0).method_7681() || this.field_2797.method_7611(1).method_7681()) && !this.field_2797.method_7611(2).method_7681()) {
			this.blit(k + 99, l + 45, this.field_2792, 0, 28, 21);
		}
	}

	@Override
	public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
		this.method_7635(arg, 0, arg.method_7611(0).method_7677());
	}

	@Override
	public void method_7635(class_1703 arg, int i, class_1799 arg2) {
		if (i == 0) {
			this.field_2821.method_1852(arg2.method_7960() ? "" : arg2.method_7964().getString());
			this.field_2821.method_1888(!arg2.method_7960());
		}
	}

	@Override
	public void method_7633(class_1703 arg, int i, int j) {
	}
}
