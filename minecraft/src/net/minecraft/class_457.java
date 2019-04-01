package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_457 extends class_437 implements class_632.class_633 {
	private static final class_2960 field_2717 = new class_2960("textures/gui/advancements/window.png");
	private static final class_2960 field_2716 = new class_2960("textures/gui/advancements/tabs.png");
	private final class_632 field_2721;
	private final Map<class_161, class_454> field_2719 = Maps.<class_161, class_454>newLinkedHashMap();
	private class_454 field_2720;
	private boolean field_2718;

	public class_457(class_632 arg) {
		super(class_333.field_18967);
		this.field_2721 = arg;
	}

	@Override
	protected void init() {
		this.field_2719.clear();
		this.field_2720 = null;
		this.field_2721.method_2862(this);
		if (this.field_2720 == null && !this.field_2719.isEmpty()) {
			this.field_2721.method_2864(((class_454)this.field_2719.values().iterator().next()).method_2307(), true);
		} else {
			this.field_2721.method_2864(this.field_2720 == null ? null : this.field_2720.method_2307(), true);
		}
	}

	@Override
	public void removed() {
		this.field_2721.method_2862(null);
		class_634 lv = this.minecraft.method_1562();
		if (lv != null) {
			lv.method_2883(class_2859.method_12414());
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			int j = (this.width - 252) / 2;
			int k = (this.height - 140) / 2;

			for (class_454 lv : this.field_2719.values()) {
				if (lv.method_2316(j, k, d, e)) {
					this.field_2721.method_2864(lv.method_2307(), true);
					break;
				}
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.minecraft.field_1690.field_1844.method_1417(i, j)) {
			this.minecraft.method_1507(null);
			this.minecraft.field_1729.method_1612();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		int k = (this.width - 252) / 2;
		int l = (this.height - 140) / 2;
		this.renderBackground();
		this.method_2337(i, j, k, l);
		this.method_2334(k, l);
		this.method_2338(i, j, k, l);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i != 0) {
			this.field_2718 = false;
			return false;
		} else {
			if (!this.field_2718) {
				this.field_2718 = true;
			} else if (this.field_2720 != null) {
				this.field_2720.method_2313(f, g);
			}

			return true;
		}
	}

	private void method_2337(int i, int j, int k, int l) {
		class_454 lv = this.field_2720;
		if (lv == null) {
			fill(k + 9, l + 18, k + 9 + 234, l + 18 + 113, -16777216);
			String string = class_1074.method_4662("advancements.empty");
			int m = this.font.method_1727(string);
			this.font.method_1729(string, (float)(k + 9 + 117 - m / 2), (float)(l + 18 + 56 - 9 / 2), -1);
			this.font.method_1729(":(", (float)(k + 9 + 117 - this.font.method_1727(":(") / 2), (float)(l + 18 + 113 - 9), -1);
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(k + 9), (float)(l + 18), -400.0F);
			GlStateManager.enableDepthTest();
			lv.method_2310();
			GlStateManager.popMatrix();
			GlStateManager.depthFunc(515);
			GlStateManager.disableDepthTest();
		}
	}

	public void method_2334(int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		class_308.method_1450();
		this.minecraft.method_1531().method_4618(field_2717);
		this.blit(i, j, 0, 0, 252, 140);
		if (this.field_2719.size() > 1) {
			this.minecraft.method_1531().method_4618(field_2716);

			for (class_454 lv : this.field_2719.values()) {
				lv.method_2311(i, j, lv == this.field_2720);
			}

			GlStateManager.enableRescaleNormal();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			class_308.method_1453();

			for (class_454 lv : this.field_2719.values()) {
				lv.method_2315(i, j, this.itemRenderer);
			}

			GlStateManager.disableBlend();
		}

		this.font.method_1729(class_1074.method_4662("gui.advancements"), (float)(i + 8), (float)(j + 6), 4210752);
	}

	private void method_2338(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.field_2720 != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableDepthTest();
			GlStateManager.translatef((float)(k + 9), (float)(l + 18), 400.0F);
			this.field_2720.method_2314(i - k - 9, j - l - 18, k, l);
			GlStateManager.disableDepthTest();
			GlStateManager.popMatrix();
		}

		if (this.field_2719.size() > 1) {
			for (class_454 lv : this.field_2719.values()) {
				if (lv.method_2316(k, l, (double)i, (double)j)) {
					this.renderTooltip(lv.method_2309(), i, j);
				}
			}
		}
	}

	@Override
	public void method_723(class_161 arg) {
		class_454 lv = class_454.method_2317(this.minecraft, this, this.field_2719.size(), arg);
		if (lv != null) {
			this.field_2719.put(arg, lv);
		}
	}

	@Override
	public void method_720(class_161 arg) {
	}

	@Override
	public void method_721(class_161 arg) {
		class_454 lv = this.method_2336(arg);
		if (lv != null) {
			lv.method_2318(arg);
		}
	}

	@Override
	public void method_719(class_161 arg) {
	}

	@Override
	public void method_2865(class_161 arg, class_167 arg2) {
		class_456 lv = this.method_2335(arg);
		if (lv != null) {
			lv.method_2333(arg2);
		}
	}

	@Override
	public void method_2866(@Nullable class_161 arg) {
		this.field_2720 = (class_454)this.field_2719.get(arg);
	}

	@Override
	public void method_722() {
		this.field_2719.clear();
		this.field_2720 = null;
	}

	@Nullable
	public class_456 method_2335(class_161 arg) {
		class_454 lv = this.method_2336(arg);
		return lv == null ? null : lv.method_2308(arg);
	}

	@Nullable
	private class_454 method_2336(class_161 arg) {
		while (arg.method_687() != null) {
			arg = arg.method_687();
		}

		return (class_454)this.field_2719.get(arg);
	}
}
