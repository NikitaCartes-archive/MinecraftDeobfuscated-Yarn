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
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.Listener {
	private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
	private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
	private final ClientAdvancementManager advancementHandler;
	private final Map<Advancement, AdvancementTab> tabs = Maps.<Advancement, AdvancementTab>newLinkedHashMap();
	private AdvancementTab selectedTab;
	private boolean movingTab;

	public AdvancementsScreen(ClientAdvancementManager clientAdvancementManager) {
		super(NarratorManager.EMPTY);
		this.advancementHandler = clientAdvancementManager;
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
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			int j = (this.width - 252) / 2;
			int k = (this.height - 140) / 2;

			for (AdvancementTab advancementTab : this.tabs.values()) {
				if (advancementTab.isClickOnTab(j, k, d, e)) {
					this.advancementHandler.selectTab(advancementTab.getRoot(), true);
					break;
				}
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.minecraft.options.keyAdvancements.matchesKey(i, j)) {
			this.minecraft.openScreen(null);
			this.minecraft.mouse.lockCursor();
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
		this.drawAdvancementTree(i, j, k, l);
		this.drawWidgets(k, l);
		this.drawWidgetTooltip(i, j, k, l);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i != 0) {
			this.movingTab = false;
			return false;
		} else {
			if (!this.movingTab) {
				this.movingTab = true;
			} else if (this.selectedTab != null) {
				this.selectedTab.move(f, g);
			}

			return true;
		}
	}

	private void drawAdvancementTree(int i, int j, int k, int l) {
		AdvancementTab advancementTab = this.selectedTab;
		if (advancementTab == null) {
			fill(k + 9, l + 18, k + 9 + 234, l + 18 + 113, -16777216);
			String string = I18n.translate("advancements.empty");
			int m = this.font.getStringWidth(string);
			this.font.draw(string, (float)(k + 9 + 117 - m / 2), (float)(l + 18 + 56 - 9 / 2), -1);
			this.font.draw(":(", (float)(k + 9 + 117 - this.font.getStringWidth(":(") / 2), (float)(l + 18 + 113 - 9), -1);
		} else {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(k + 9), (float)(l + 18), 0.0F);
			advancementTab.render();
			RenderSystem.popMatrix();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
		}
	}

	public void drawWidgets(int i, int j) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		this.minecraft.getTextureManager().bindTexture(WINDOW_TEXTURE);
		this.blit(i, j, 0, 0, 252, 140);
		if (this.tabs.size() > 1) {
			this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawBackground(i, j, advancementTab == this.selectedTab);
			}

			RenderSystem.enableRescaleNormal();
			RenderSystem.defaultBlendFunc();

			for (AdvancementTab advancementTab : this.tabs.values()) {
				advancementTab.drawIcon(i, j, this.itemRenderer);
			}

			RenderSystem.disableBlend();
		}

		this.font.draw(I18n.translate("gui.advancements"), (float)(i + 8), (float)(j + 6), 4210752);
	}

	private void drawWidgetTooltip(int i, int j, int k, int l) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.selectedTab != null) {
			RenderSystem.pushMatrix();
			RenderSystem.enableDepthTest();
			RenderSystem.translatef((float)(k + 9), (float)(l + 18), 400.0F);
			this.selectedTab.drawWidgetTooltip(i - k - 9, j - l - 18, k, l);
			RenderSystem.disableDepthTest();
			RenderSystem.popMatrix();
		}

		if (this.tabs.size() > 1) {
			for (AdvancementTab advancementTab : this.tabs.values()) {
				if (advancementTab.isClickOnTab(k, l, (double)i, (double)j)) {
					this.renderTooltip(advancementTab.getTitle(), i, j);
				}
			}
		}
	}

	@Override
	public void onRootAdded(Advancement advancement) {
		AdvancementTab advancementTab = AdvancementTab.create(this.minecraft, this, this.tabs.size(), advancement);
		if (advancementTab != null) {
			this.tabs.put(advancement, advancementTab);
		}
	}

	@Override
	public void onRootRemoved(Advancement advancement) {
	}

	@Override
	public void onDependentAdded(Advancement advancement) {
		AdvancementTab advancementTab = this.getTab(advancement);
		if (advancementTab != null) {
			advancementTab.addAdvancement(advancement);
		}
	}

	@Override
	public void onDependentRemoved(Advancement advancement) {
	}

	@Override
	public void setProgress(Advancement advancement, AdvancementProgress advancementProgress) {
		AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
		if (advancementWidget != null) {
			advancementWidget.setProgress(advancementProgress);
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
