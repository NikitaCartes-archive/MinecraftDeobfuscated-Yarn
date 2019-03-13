package net.minecraft.client.gui.menu;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AdvancementTreeWidget;
import net.minecraft.client.gui.widget.AdvancementWidget;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.class_633 {
	private static final Identifier field_2717 = new Identifier("textures/gui/advancements/window.png");
	private static final Identifier field_2716 = new Identifier("textures/gui/advancements/tabs.png");
	private final ClientAdvancementManager field_2721;
	private final Map<SimpleAdvancement, AdvancementTreeWidget> widgetMap = Maps.<SimpleAdvancement, AdvancementTreeWidget>newLinkedHashMap();
	private AdvancementTreeWidget selectedWidget;
	private boolean field_2718;

	public AdvancementsScreen(ClientAdvancementManager clientAdvancementManager) {
		this.field_2721 = clientAdvancementManager;
	}

	@Override
	protected void onInitialized() {
		this.widgetMap.clear();
		this.selectedWidget = null;
		this.field_2721.setGui(this);
		if (this.selectedWidget == null && !this.widgetMap.isEmpty()) {
			this.field_2721.method_2864(((AdvancementTreeWidget)this.widgetMap.values().iterator().next()).method_2307(), true);
		} else {
			this.field_2721.method_2864(this.selectedWidget == null ? null : this.selectedWidget.method_2307(), true);
		}
	}

	@Override
	public void onClosed() {
		this.field_2721.setGui(null);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.method_1562();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.method_2883(AdvancementTabC2SPacket.close());
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			int j = (this.screenWidth - 252) / 2;
			int k = (this.screenHeight - 140) / 2;

			for (AdvancementTreeWidget advancementTreeWidget : this.widgetMap.values()) {
				if (advancementTreeWidget.method_2316(j, k, d, e)) {
					this.field_2721.method_2864(advancementTreeWidget.method_2307(), true);
					break;
				}
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.client.field_1690.keyAdvancements.matchesKey(i, j)) {
			this.client.method_1507(null);
			this.client.field_1729.lockCursor();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		int k = (this.screenWidth - 252) / 2;
		int l = (this.screenHeight - 140) / 2;
		this.drawBackground();
		this.drawAdvancementTree(i, j, k, l);
		this.drawWidgets(k, l);
		this.drawWidgetTooltip(i, j, k, l);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i != 0) {
			this.field_2718 = false;
			return false;
		} else {
			if (!this.field_2718) {
				this.field_2718 = true;
			} else if (this.selectedWidget != null) {
				this.selectedWidget.method_2313(f, g);
			}

			return true;
		}
	}

	private void drawAdvancementTree(int i, int j, int k, int l) {
		AdvancementTreeWidget advancementTreeWidget = this.selectedWidget;
		if (advancementTreeWidget == null) {
			drawRect(k + 9, l + 18, k + 9 + 234, l + 18 + 113, -16777216);
			String string = I18n.translate("advancements.empty");
			int m = this.fontRenderer.getStringWidth(string);
			this.fontRenderer.draw(string, (float)(k + 9 + 117 - m / 2), (float)(l + 18 + 56 - 9 / 2), -1);
			this.fontRenderer.draw(":(", (float)(k + 9 + 117 - this.fontRenderer.getStringWidth(":(") / 2), (float)(l + 18 + 113 - 9), -1);
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(k + 9), (float)(l + 18), -400.0F);
			GlStateManager.enableDepthTest();
			advancementTreeWidget.method_2310();
			GlStateManager.popMatrix();
			GlStateManager.depthFunc(515);
			GlStateManager.disableDepthTest();
		}
	}

	public void drawWidgets(int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GuiLighting.disable();
		this.client.method_1531().method_4618(field_2717);
		this.drawTexturedRect(i, j, 0, 0, 252, 140);
		if (this.widgetMap.size() > 1) {
			this.client.method_1531().method_4618(field_2716);

			for (AdvancementTreeWidget advancementTreeWidget : this.widgetMap.values()) {
				advancementTreeWidget.drawBackground(i, j, advancementTreeWidget == this.selectedWidget);
			}

			GlStateManager.enableRescaleNormal();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GuiLighting.enableForItems();

			for (AdvancementTreeWidget advancementTreeWidget : this.widgetMap.values()) {
				advancementTreeWidget.method_2315(i, j, this.field_2560);
			}

			GlStateManager.disableBlend();
		}

		this.fontRenderer.draw(I18n.translate("gui.advancements"), (float)(i + 8), (float)(j + 6), 4210752);
	}

	private void drawWidgetTooltip(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.selectedWidget != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableDepthTest();
			GlStateManager.translatef((float)(k + 9), (float)(l + 18), 400.0F);
			this.selectedWidget.method_2314(i - k - 9, j - l - 18, k, l);
			GlStateManager.disableDepthTest();
			GlStateManager.popMatrix();
		}

		if (this.widgetMap.size() > 1) {
			for (AdvancementTreeWidget advancementTreeWidget : this.widgetMap.values()) {
				if (advancementTreeWidget.method_2316(k, l, (double)i, (double)j)) {
					this.drawTooltip(advancementTreeWidget.method_2309(), i, j);
				}
			}
		}
	}

	@Override
	public void onRootAdded(SimpleAdvancement simpleAdvancement) {
		AdvancementTreeWidget advancementTreeWidget = AdvancementTreeWidget.method_2317(this.client, this, this.widgetMap.size(), simpleAdvancement);
		if (advancementTreeWidget != null) {
			this.widgetMap.put(simpleAdvancement, advancementTreeWidget);
		}
	}

	@Override
	public void onRootRemoved(SimpleAdvancement simpleAdvancement) {
	}

	@Override
	public void onDependentAdded(SimpleAdvancement simpleAdvancement) {
		AdvancementTreeWidget advancementTreeWidget = this.method_2336(simpleAdvancement);
		if (advancementTreeWidget != null) {
			advancementTreeWidget.method_2318(simpleAdvancement);
		}
	}

	@Override
	public void onDependentRemoved(SimpleAdvancement simpleAdvancement) {
	}

	@Override
	public void method_2865(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress) {
		AdvancementWidget advancementWidget = this.method_2335(simpleAdvancement);
		if (advancementWidget != null) {
			advancementWidget.method_2333(advancementProgress);
		}
	}

	@Override
	public void method_2866(@Nullable SimpleAdvancement simpleAdvancement) {
		this.selectedWidget = (AdvancementTreeWidget)this.widgetMap.get(simpleAdvancement);
	}

	@Override
	public void onClear() {
		this.widgetMap.clear();
		this.selectedWidget = null;
	}

	@Nullable
	public AdvancementWidget method_2335(SimpleAdvancement simpleAdvancement) {
		AdvancementTreeWidget advancementTreeWidget = this.method_2336(simpleAdvancement);
		return advancementTreeWidget == null ? null : advancementTreeWidget.method_2308(simpleAdvancement);
	}

	@Nullable
	private AdvancementTreeWidget method_2336(SimpleAdvancement simpleAdvancement) {
		while (simpleAdvancement.getParent() != null) {
			simpleAdvancement = simpleAdvancement.getParent();
		}

		return (AdvancementTreeWidget)this.widgetMap.get(simpleAdvancement);
	}
}
