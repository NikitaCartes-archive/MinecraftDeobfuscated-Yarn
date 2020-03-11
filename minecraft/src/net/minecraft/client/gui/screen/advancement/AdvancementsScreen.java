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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.Listener {
	private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
	private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
	private final ClientAdvancementManager advancementHandler;
	private final Map<Advancement, AdvancementTab> tabs = Maps.<Advancement, AdvancementTab>newLinkedHashMap();
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
		if (this.client.options.keyAdvancements.matchesKey(keyCode, scanCode)) {
			this.client.openScreen(null);
			this.client.mouse.lockCursor();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int i = (this.width - 252) / 2;
		int j = (this.height - 140) / 2;
		this.renderBackground();
		this.drawAdvancementTree(mouseX, mouseY, i, j);
		this.drawWidgets(i, j);
		this.drawWidgetTooltip(mouseX, mouseY, i, j);
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

	private void drawAdvancementTree(int mouseX, int mouseY, int x, int y) {
		AdvancementTab advancementTab = this.selectedTab;
		if (advancementTab == null) {
			fill(x + 9, y + 18, x + 9 + 234, y + 18 + 113, -16777216);
			String string = I18n.translate("advancements.empty");
			int i = this.textRenderer.getStringWidth(string);
			this.textRenderer.draw(string, (float)(x + 9 + 117 - i / 2), (float)(y + 18 + 56 - 9 / 2), -1);
			this.textRenderer.draw(":(", (float)(x + 9 + 117 - this.textRenderer.getStringWidth(":(") / 2), (float)(y + 18 + 113 - 9), -1);
		} else {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(x + 9), (float)(y + 18), 0.0F);
			advancementTab.render();
			RenderSystem.popMatrix();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
		}
	}

	public void drawWidgets(int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		this.client.getTextureManager().bindTexture(WINDOW_TEXTURE);
		this.drawTexture(x, y, 0, 0, 252, 140);
		if (this.tabs.size() > 1) {
			this.client.getTextureManager().bindTexture(TABS_TEXTURE);

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawBackground(x, y, advancementTab == this.selectedTab);
			}

			RenderSystem.enableRescaleNormal();
			RenderSystem.defaultBlendFunc();

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawIcon(x, y, this.itemRenderer);
			}

			RenderSystem.disableBlend();
		}

		this.textRenderer.draw(I18n.translate("gui.advancements"), (float)(x + 8), (float)(y + 6), 4210752);
	}

	private void drawWidgetTooltip(int mouseX, int mouseY, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.selectedTab != null) {
			RenderSystem.pushMatrix();
			RenderSystem.enableDepthTest();
			RenderSystem.translatef((float)(x + 9), (float)(y + 18), 400.0F);
			this.selectedTab.drawWidgetTooltip(mouseX - x - 9, mouseY - y - 18, x, y);
			RenderSystem.disableDepthTest();
			RenderSystem.popMatrix();
		}

		if (this.tabs.size() > 1) {
			for (AdvancementTab advancementTab : this.tabs.values()) {
				if (advancementTab.isClickOnTab(x, y, (double)mouseX, (double)mouseY)) {
					this.renderTooltip(advancementTab.getTitle(), mouseX, mouseY);
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
