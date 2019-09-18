package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementTab extends DrawableHelper {
	private final MinecraftClient client;
	private final AdvancementsScreen screen;
	private final AdvancementTabType type;
	private final int index;
	private final Advancement root;
	private final AdvancementDisplay display;
	private final ItemStack icon;
	private final String title;
	private final AdvancementWidget rootWidget;
	private final Map<Advancement, AdvancementWidget> widgets = Maps.<Advancement, AdvancementWidget>newLinkedHashMap();
	private double panX;
	private double panY;
	private int minPanX = Integer.MAX_VALUE;
	private int minPanY = Integer.MAX_VALUE;
	private int maxPanX = Integer.MIN_VALUE;
	private int maxPanY = Integer.MIN_VALUE;
	private float field_2688;
	private boolean field_2683;

	public AdvancementTab(
		MinecraftClient minecraftClient,
		AdvancementsScreen advancementsScreen,
		AdvancementTabType advancementTabType,
		int i,
		Advancement advancement,
		AdvancementDisplay advancementDisplay
	) {
		this.client = minecraftClient;
		this.screen = advancementsScreen;
		this.type = advancementTabType;
		this.index = i;
		this.root = advancement;
		this.display = advancementDisplay;
		this.icon = advancementDisplay.getIcon();
		this.title = advancementDisplay.getTitle().asFormattedString();
		this.rootWidget = new AdvancementWidget(this, minecraftClient, advancement, advancementDisplay);
		this.addWidget(this.rootWidget, advancement);
	}

	public Advancement getRoot() {
		return this.root;
	}

	public String getTitle() {
		return this.title;
	}

	public void drawBackground(int i, int j, boolean bl) {
		this.type.drawBackground(this, i, j, bl, this.index);
	}

	public void drawIcon(int i, int j, ItemRenderer itemRenderer) {
		this.type.drawIcon(i, j, this.index, itemRenderer, this.icon);
	}

	public void render() {
		if (!this.field_2683) {
			this.panX = (double)(117 - (this.maxPanX + this.minPanX) / 2);
			this.panY = (double)(56 - (this.maxPanY + this.minPanY) / 2);
			this.field_2683 = true;
		}

		RenderSystem.depthFunc(518);
		fill(0, 0, 234, 113, -16777216);
		RenderSystem.depthFunc(515);
		Identifier identifier = this.display.getBackground();
		if (identifier != null) {
			this.client.getTextureManager().method_22813(identifier);
		} else {
			this.client.getTextureManager().method_22813(TextureManager.MISSING_IDENTIFIER);
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = MathHelper.floor(this.panX);
		int j = MathHelper.floor(this.panY);
		int k = i % 16;
		int l = j % 16;

		for (int m = -1; m <= 15; m++) {
			for (int n = -1; n <= 8; n++) {
				blit(k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16, 16);
			}
		}

		this.rootWidget.renderLines(i, j, true);
		this.rootWidget.renderLines(i, j, false);
		this.rootWidget.renderWidgets(i, j);
	}

	public void drawWidgetTooltip(int i, int j, int k, int l) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.0F, 0.0F, 200.0F);
		fill(0, 0, 234, 113, MathHelper.floor(this.field_2688 * 255.0F) << 24);
		boolean bl = false;
		int m = MathHelper.floor(this.panX);
		int n = MathHelper.floor(this.panY);
		if (i > 0 && i < 234 && j > 0 && j < 113) {
			for (AdvancementWidget advancementWidget : this.widgets.values()) {
				if (advancementWidget.method_2329(m, n, i, j)) {
					bl = true;
					advancementWidget.method_2331(m, n, this.field_2688, k, l);
					break;
				}
			}
		}

		RenderSystem.popMatrix();
		if (bl) {
			this.field_2688 = MathHelper.clamp(this.field_2688 + 0.02F, 0.0F, 0.3F);
		} else {
			this.field_2688 = MathHelper.clamp(this.field_2688 - 0.04F, 0.0F, 1.0F);
		}
	}

	public boolean isClickOnTab(int i, int j, double d, double e) {
		return this.type.isClickOnTab(i, j, this.index, d, e);
	}

	@Nullable
	public static AdvancementTab create(MinecraftClient minecraftClient, AdvancementsScreen advancementsScreen, int i, Advancement advancement) {
		if (advancement.getDisplay() == null) {
			return null;
		} else {
			for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
				if (i < advancementTabType.getTabCount()) {
					return new AdvancementTab(minecraftClient, advancementsScreen, advancementTabType, i, advancement, advancement.getDisplay());
				}

				i -= advancementTabType.getTabCount();
			}

			return null;
		}
	}

	public void move(double d, double e) {
		if (this.maxPanX - this.minPanX > 234) {
			this.panX = MathHelper.clamp(this.panX + d, (double)(-(this.maxPanX - 234)), 0.0);
		}

		if (this.maxPanY - this.minPanY > 113) {
			this.panY = MathHelper.clamp(this.panY + e, (double)(-(this.maxPanY - 113)), 0.0);
		}
	}

	public void addAdvancement(Advancement advancement) {
		if (advancement.getDisplay() != null) {
			AdvancementWidget advancementWidget = new AdvancementWidget(this, this.client, advancement, advancement.getDisplay());
			this.addWidget(advancementWidget, advancement);
		}
	}

	private void addWidget(AdvancementWidget advancementWidget, Advancement advancement) {
		this.widgets.put(advancement, advancementWidget);
		int i = advancementWidget.getX();
		int j = i + 28;
		int k = advancementWidget.getY();
		int l = k + 27;
		this.minPanX = Math.min(this.minPanX, i);
		this.maxPanX = Math.max(this.maxPanX, j);
		this.minPanY = Math.min(this.minPanY, k);
		this.maxPanY = Math.max(this.maxPanY, l);

		for (AdvancementWidget advancementWidget2 : this.widgets.values()) {
			advancementWidget2.addToTree();
		}
	}

	@Nullable
	public AdvancementWidget getWidget(Advancement advancement) {
		return (AdvancementWidget)this.widgets.get(advancement);
	}

	public AdvancementsScreen getScreen() {
		return this.screen;
	}
}
