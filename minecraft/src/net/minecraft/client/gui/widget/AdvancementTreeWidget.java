package net.minecraft.client.gui.widget;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.menu.AdvancementsScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementTreeWidget extends Drawable {
	private final MinecraftClient client;
	private final AdvancementsScreen field_2687;
	private final AdvancementTabType tabType;
	private final int field_2681;
	private final SimpleAdvancement rootAdvancement;
	private final AdvancementDisplay field_2695;
	private final ItemStack field_2697;
	private final String field_2686;
	private final AdvancementWidget field_2696;
	private final Map<SimpleAdvancement, AdvancementWidget> widgets = Maps.<SimpleAdvancement, AdvancementWidget>newLinkedHashMap();
	private double field_2690;
	private double field_2689;
	private int field_2694 = Integer.MAX_VALUE;
	private int field_2693 = Integer.MAX_VALUE;
	private int field_2692 = Integer.MIN_VALUE;
	private int field_2691 = Integer.MIN_VALUE;
	private float field_2688;
	private boolean field_2683;

	public AdvancementTreeWidget(
		MinecraftClient minecraftClient,
		AdvancementsScreen advancementsScreen,
		AdvancementTabType advancementTabType,
		int i,
		SimpleAdvancement simpleAdvancement,
		AdvancementDisplay advancementDisplay
	) {
		this.client = minecraftClient;
		this.field_2687 = advancementsScreen;
		this.tabType = advancementTabType;
		this.field_2681 = i;
		this.rootAdvancement = simpleAdvancement;
		this.field_2695 = advancementDisplay;
		this.field_2697 = advancementDisplay.getIcon();
		this.field_2686 = advancementDisplay.getTitle().getFormattedText();
		this.field_2696 = new AdvancementWidget(this, minecraftClient, simpleAdvancement, advancementDisplay);
		this.method_2319(this.field_2696, simpleAdvancement);
	}

	public SimpleAdvancement method_2307() {
		return this.rootAdvancement;
	}

	public String method_2309() {
		return this.field_2686;
	}

	public void drawBackground(int i, int j, boolean bl) {
		this.tabType.drawBackground(this, i, j, bl, this.field_2681);
	}

	public void drawIcon(int i, int j, ItemRenderer itemRenderer) {
		this.tabType.drawIcon(i, j, this.field_2681, itemRenderer, this.field_2697);
	}

	public void method_2310() {
		if (!this.field_2683) {
			this.field_2690 = (double)(117 - (this.field_2692 + this.field_2694) / 2);
			this.field_2689 = (double)(56 - (this.field_2691 + this.field_2693) / 2);
			this.field_2683 = true;
		}

		GlStateManager.depthFunc(518);
		drawRect(0, 0, 234, 113, -16777216);
		GlStateManager.depthFunc(515);
		Identifier identifier = this.field_2695.getBackground();
		if (identifier != null) {
			this.client.getTextureManager().bindTexture(identifier);
		} else {
			this.client.getTextureManager().bindTexture(TextureManager.field_5285);
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = MathHelper.floor(this.field_2690);
		int j = MathHelper.floor(this.field_2689);
		int k = i % 16;
		int l = j % 16;

		for (int m = -1; m <= 15; m++) {
			for (int n = -1; n <= 8; n++) {
				drawTexturedRect(k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
			}
		}

		this.field_2696.method_2323(i, j, true);
		this.field_2696.method_2323(i, j, false);
		this.field_2696.method_2325(i, j);
	}

	public void method_2314(int i, int j, int k, int l) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 0.0F, 200.0F);
		drawRect(0, 0, 234, 113, MathHelper.floor(this.field_2688 * 255.0F) << 24);
		boolean bl = false;
		int m = MathHelper.floor(this.field_2690);
		int n = MathHelper.floor(this.field_2689);
		if (i > 0 && i < 234 && j > 0 && j < 113) {
			for (AdvancementWidget advancementWidget : this.widgets.values()) {
				if (advancementWidget.method_2329(m, n, i, j)) {
					bl = true;
					advancementWidget.method_2331(m, n, this.field_2688, k, l);
					break;
				}
			}
		}

		GlStateManager.popMatrix();
		if (bl) {
			this.field_2688 = MathHelper.clamp(this.field_2688 + 0.02F, 0.0F, 0.3F);
		} else {
			this.field_2688 = MathHelper.clamp(this.field_2688 - 0.04F, 0.0F, 1.0F);
		}
	}

	public boolean method_2316(int i, int j, double d, double e) {
		return this.tabType.method_2303(i, j, this.field_2681, d, e);
	}

	@Nullable
	public static AdvancementTreeWidget create(MinecraftClient minecraftClient, AdvancementsScreen advancementsScreen, int i, SimpleAdvancement simpleAdvancement) {
		if (simpleAdvancement.getDisplay() == null) {
			return null;
		} else {
			for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
				if (i < advancementTabType.method_2304()) {
					return new AdvancementTreeWidget(minecraftClient, advancementsScreen, advancementTabType, i, simpleAdvancement, simpleAdvancement.getDisplay());
				}

				i -= advancementTabType.method_2304();
			}

			return null;
		}
	}

	public void method_2313(double d, double e) {
		if (this.field_2692 - this.field_2694 > 234) {
			this.field_2690 = MathHelper.clamp(this.field_2690 + d, (double)(-(this.field_2692 - 234)), 0.0);
		}

		if (this.field_2691 - this.field_2693 > 113) {
			this.field_2689 = MathHelper.clamp(this.field_2689 + e, (double)(-(this.field_2691 - 113)), 0.0);
		}
	}

	public void method_2318(SimpleAdvancement simpleAdvancement) {
		if (simpleAdvancement.getDisplay() != null) {
			AdvancementWidget advancementWidget = new AdvancementWidget(this, this.client, simpleAdvancement, simpleAdvancement.getDisplay());
			this.method_2319(advancementWidget, simpleAdvancement);
		}
	}

	private void method_2319(AdvancementWidget advancementWidget, SimpleAdvancement simpleAdvancement) {
		this.widgets.put(simpleAdvancement, advancementWidget);
		int i = advancementWidget.method_2327();
		int j = i + 28;
		int k = advancementWidget.method_2326();
		int l = k + 27;
		this.field_2694 = Math.min(this.field_2694, i);
		this.field_2692 = Math.max(this.field_2692, j);
		this.field_2693 = Math.min(this.field_2693, k);
		this.field_2691 = Math.max(this.field_2691, l);

		for (AdvancementWidget advancementWidget2 : this.widgets.values()) {
			advancementWidget2.method_2332();
		}
	}

	@Nullable
	public AdvancementWidget getWidgetForAdvancement(SimpleAdvancement simpleAdvancement) {
		return (AdvancementWidget)this.widgets.get(simpleAdvancement);
	}

	public AdvancementsScreen method_2312() {
		return this.field_2687;
	}
}
