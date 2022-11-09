package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.Listener {
	private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
	private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
	public static final int WINDOW_WIDTH = 252;
	public static final int WINDOW_HEIGHT = 140;
	private static final int PAGE_OFFSET_X = 9;
	private static final int PAGE_OFFSET_Y = 18;
	public static final int PAGE_WIDTH = 234;
	public static final int PAGE_HEIGHT = 113;
	private static final int TITLE_OFFSET_X = 8;
	private static final int TITLE_OFFSET_Y = 6;
	public static final int field_32302 = 16;
	public static final int field_32303 = 16;
	public static final int field_32304 = 14;
	public static final int field_32305 = 7;
	private static final Text SAD_LABEL_TEXT = Text.translatable("advancements.sad_label");
	private static final Text EMPTY_TEXT = Text.translatable("advancements.empty");
	private static final Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");
	private final ClientAdvancementManager advancementHandler;
	private final Map<Advancement, AdvancementTab> tabs = Maps.<Advancement, AdvancementTab>newLinkedHashMap();
	@Nullable
	private AdvancementTab selectedTab;
	private boolean movingTab;

	public AdvancementsScreen(ClientAdvancementManager advancementHandler) {
		super(NarratorManager.EMPTY);
		this.advancementHandler = advancementHandler;
	}

	@Override
	protected void init() {
		this.tabs.clear();
		this.selectedTab = null;
		this.advancementHandler.setListener(this);
		if (this.selectedTab == null && !this.tabs.isEmpty()) {
			this.advancementHandler.selectTab(((AdvancementTab)this.tabs.values().iterator().next()).getRoot(), true);
		} else {
			this.advancementHandler.selectTab(this.selectedTab == null ? null : this.selectedTab.getRoot(), true);
		}
	}

	@Override
	public void removed() {
		this.advancementHandler.setListener(null);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int i = (this.width - 252) / 2;
			int j = (this.height - 140) / 2;

			for (AdvancementTab advancementTab : this.tabs.values()) {
				if (advancementTab.isClickOnTab(i, j, mouseX, mouseY)) {
					this.advancementHandler.selectTab(advancementTab.getRoot(), true);
					break;
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.client.options.advancementsKey.matchesKey(keyCode, scanCode)) {
			this.client.setScreen(null);
			this.client.mouse.lockCursor();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = (this.width - 252) / 2;
		int j = (this.height - 140) / 2;
		this.renderBackground(matrices);
		this.drawAdvancementTree(matrices, mouseX, mouseY, i, j);
		this.drawWindow(matrices, i, j);
		this.drawWidgetTooltip(matrices, mouseX, mouseY, i, j);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (button != 0) {
			this.movingTab = false;
			return false;
		} else {
			if (!this.movingTab) {
				this.movingTab = true;
			} else if (this.selectedTab != null) {
				this.selectedTab.move(deltaX, deltaY);
			}

			return true;
		}
	}

	private void drawAdvancementTree(MatrixStack matrices, int mouseX, int mouseY, int x, int y) {
		AdvancementTab advancementTab = this.selectedTab;
		if (advancementTab == null) {
			fill(matrices, x + 9, y + 18, x + 9 + 234, y + 18 + 113, -16777216);
			int i = x + 9 + 117;
			drawCenteredText(matrices, this.textRenderer, EMPTY_TEXT, i, y + 18 + 56 - 9 / 2, -1);
			drawCenteredText(matrices, this.textRenderer, SAD_LABEL_TEXT, i, y + 18 + 113 - 9, -1);
		} else {
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.push();
			matrixStack.translate((float)(x + 9), (float)(y + 18), 0.0F);
			RenderSystem.applyModelViewMatrix();
			advancementTab.render(matrices);
			matrixStack.pop();
			RenderSystem.applyModelViewMatrix();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
		}
	}

	public void drawWindow(MatrixStack matrices, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, WINDOW_TEXTURE);
		this.drawTexture(matrices, x, y, 0, 0, 252, 140);
		if (this.tabs.size() > 1) {
			RenderSystem.setShaderTexture(0, TABS_TEXTURE);

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawBackground(matrices, x, y, advancementTab == this.selectedTab);
			}

			RenderSystem.defaultBlendFunc();

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawIcon(x, y, this.itemRenderer);
			}

			RenderSystem.disableBlend();
		}

		this.textRenderer.draw(matrices, ADVANCEMENTS_TEXT, (float)(x + 8), (float)(y + 6), 4210752);
	}

	private void drawWidgetTooltip(MatrixStack matrices, int mouseX, int mouseY, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.selectedTab != null) {
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.push();
			matrixStack.translate((float)(x + 9), (float)(y + 18), 400.0F);
			RenderSystem.applyModelViewMatrix();
			RenderSystem.enableDepthTest();
			this.selectedTab.drawWidgetTooltip(matrices, mouseX - x - 9, mouseY - y - 18, x, y);
			RenderSystem.disableDepthTest();
			matrixStack.pop();
			RenderSystem.applyModelViewMatrix();
		}

		if (this.tabs.size() > 1) {
			for (AdvancementTab advancementTab : this.tabs.values()) {
				if (advancementTab.isClickOnTab(x, y, (double)mouseX, (double)mouseY)) {
					this.renderTooltip(matrices, advancementTab.getTitle(), mouseX, mouseY);
				}
			}
		}
	}

	@Override
	public void onRootAdded(Advancement root) {
		AdvancementTab advancementTab = AdvancementTab.create(this.client, this, this.tabs.size(), root);
		if (advancementTab != null) {
			this.tabs.put(root, advancementTab);
		}
	}

	@Override
	public void onRootRemoved(Advancement root) {
	}

	@Override
	public void onDependentAdded(Advancement dependent) {
		AdvancementTab advancementTab = this.getTab(dependent);
		if (advancementTab != null) {
			advancementTab.addAdvancement(dependent);
		}
	}

	@Override
	public void onDependentRemoved(Advancement dependent) {
	}

	@Override
	public void setProgress(Advancement advancement, AdvancementProgress progress) {
		AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
		if (advancementWidget != null) {
			advancementWidget.setProgress(progress);
		}
	}

	@Override
	public void selectTab(@Nullable Advancement advancement) {
		this.selectedTab = (AdvancementTab)this.tabs.get(advancement);
	}

	@Override
	public void onClear() {
		this.tabs.clear();
		this.selectedTab = null;
	}

	@Nullable
	public AdvancementWidget getAdvancementWidget(Advancement advancement) {
		AdvancementTab advancementTab = this.getTab(advancement);
		return advancementTab == null ? null : advancementTab.getWidget(advancement);
	}

	@Nullable
	private AdvancementTab getTab(Advancement advancement) {
		while (advancement.getParent() != null) {
			advancement = advancement.getParent();
		}

		return (AdvancementTab)this.tabs.get(advancement);
	}
}
