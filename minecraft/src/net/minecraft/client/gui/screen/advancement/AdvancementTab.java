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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
	private final Text title;
	private final AdvancementWidget rootWidget;
	private final Map<Advancement, AdvancementWidget> widgets = Maps.<Advancement, AdvancementWidget>newLinkedHashMap();
	private double originX;
	private double originY;
	private int minPanX = Integer.MAX_VALUE;
	private int minPanY = Integer.MAX_VALUE;
	private int maxPanX = Integer.MIN_VALUE;
	private int maxPanY = Integer.MIN_VALUE;
	private float alpha;
	private boolean initialized;

	public AdvancementTab(MinecraftClient client, AdvancementsScreen screen, AdvancementTabType type, int index, Advancement root, AdvancementDisplay display) {
		this.client = client;
		this.screen = screen;
		this.type = type;
		this.index = index;
		this.root = root;
		this.display = display;
		this.icon = display.getIcon();
		this.title = display.getTitle();
		this.rootWidget = new AdvancementWidget(this, client, root, display);
		this.addWidget(this.rootWidget, root);
	}

	public AdvancementTabType getType() {
		return this.type;
	}

	public int getIndex() {
		return this.index;
	}

	public Advancement getRoot() {
		return this.root;
	}

	public Text getTitle() {
		return this.title;
	}

	public AdvancementDisplay getDisplay() {
		return this.display;
	}

	public void drawBackground(MatrixStack matrices, int x, int y, boolean selected) {
		this.type.drawBackground(matrices, x, y, selected, this.index);
	}

	public void drawIcon(MatrixStack matrices, int x, int y, ItemRenderer itemRenderer) {
		this.type.drawIcon(matrices, x, y, this.index, itemRenderer, this.icon);
	}

	public void render(MatrixStack matrices, int x, int y) {
		if (!this.initialized) {
			this.originX = (double)(117 - (this.maxPanX + this.minPanX) / 2);
			this.originY = (double)(56 - (this.maxPanY + this.minPanY) / 2);
			this.initialized = true;
		}

		enableScissor(x, y, x + 234, y + 113);
		matrices.push();
		matrices.translate((float)x, (float)y, 0.0F);
		Identifier identifier = this.display.getBackground();
		if (identifier != null) {
			RenderSystem.setShaderTexture(0, identifier);
		} else {
			RenderSystem.setShaderTexture(0, TextureManager.MISSING_IDENTIFIER);
		}

		int i = MathHelper.floor(this.originX);
		int j = MathHelper.floor(this.originY);
		int k = i % 16;
		int l = j % 16;

		for (int m = -1; m <= 15; m++) {
			for (int n = -1; n <= 8; n++) {
				drawTexture(matrices, k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16, 16);
			}
		}

		this.rootWidget.renderLines(matrices, i, j, true);
		this.rootWidget.renderLines(matrices, i, j, false);
		this.rootWidget.renderWidgets(matrices, i, j);
		matrices.pop();
		disableScissor();
	}

	public void drawWidgetTooltip(MatrixStack matrices, int mouseX, int mouseY, int x, int y) {
		matrices.push();
		matrices.translate(0.0F, 0.0F, -200.0F);
		fill(matrices, 0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0F) << 24);
		boolean bl = false;
		int i = MathHelper.floor(this.originX);
		int j = MathHelper.floor(this.originY);
		if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
			for (AdvancementWidget advancementWidget : this.widgets.values()) {
				if (advancementWidget.shouldRender(i, j, mouseX, mouseY)) {
					bl = true;
					advancementWidget.drawTooltip(matrices, i, j, this.alpha, x, y);
					break;
				}
			}
		}

		matrices.pop();
		if (bl) {
			this.alpha = MathHelper.clamp(this.alpha + 0.02F, 0.0F, 0.3F);
		} else {
			this.alpha = MathHelper.clamp(this.alpha - 0.04F, 0.0F, 1.0F);
		}
	}

	public boolean isClickOnTab(int screenX, int screenY, double mouseX, double mouseY) {
		return this.type.isClickOnTab(screenX, screenY, this.index, mouseX, mouseY);
	}

	@Nullable
	public static AdvancementTab create(MinecraftClient client, AdvancementsScreen screen, int index, Advancement root) {
		if (root.getDisplay() == null) {
			return null;
		} else {
			for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
				if (index < advancementTabType.getTabCount()) {
					return new AdvancementTab(client, screen, advancementTabType, index, root, root.getDisplay());
				}

				index -= advancementTabType.getTabCount();
			}

			return null;
		}
	}

	public void move(double offsetX, double offsetY) {
		if (this.maxPanX - this.minPanX > 234) {
			this.originX = MathHelper.clamp(this.originX + offsetX, (double)(-(this.maxPanX - 234)), 0.0);
		}

		if (this.maxPanY - this.minPanY > 113) {
			this.originY = MathHelper.clamp(this.originY + offsetY, (double)(-(this.maxPanY - 113)), 0.0);
		}
	}

	public void addAdvancement(Advancement advancement) {
		if (advancement.getDisplay() != null) {
			AdvancementWidget advancementWidget = new AdvancementWidget(this, this.client, advancement, advancement.getDisplay());
			this.addWidget(advancementWidget, advancement);
		}
	}

	private void addWidget(AdvancementWidget widget, Advancement advancement) {
		this.widgets.put(advancement, widget);
		int i = widget.getX();
		int j = i + 28;
		int k = widget.getY();
		int l = k + 27;
		this.minPanX = Math.min(this.minPanX, i);
		this.maxPanX = Math.max(this.maxPanX, j);
		this.minPanY = Math.min(this.minPanY, k);
		this.maxPanY = Math.max(this.maxPanY, l);

		for (AdvancementWidget advancementWidget : this.widgets.values()) {
			advancementWidget.addToTree();
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
