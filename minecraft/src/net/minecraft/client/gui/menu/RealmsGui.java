package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.RealmsButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsGui extends Gui {
	private final RealmsScreen realmsScreen;
	private static final Logger LOGGER_REALMS = LogManager.getLogger();

	public RealmsGui(RealmsScreen realmsScreen) {
		this.realmsScreen = realmsScreen;
	}

	public RealmsScreen getRealmsScreen() {
		return this.realmsScreen;
	}

	@Override
	public void initialize(MinecraftClient minecraftClient, int i, int j) {
		this.realmsScreen.init(minecraftClient, i, j);
		super.initialize(minecraftClient, i, j);
	}

	@Override
	protected void onInitialized() {
		this.realmsScreen.init();
		super.onInitialized();
	}

	public void drawStringCentered(String string, int i, int j, int k) {
		super.drawStringCentered(this.fontRenderer, string, i, j, k);
	}

	public void drawString(String string, int i, int j, int k, boolean bl) {
		if (bl) {
			super.drawString(this.fontRenderer, string, i, j, k);
		} else {
			this.fontRenderer.draw(string, (float)i, (float)j, k);
		}
	}

	@Override
	public void drawTexturedRect(int i, int j, int k, int l, int m, int n) {
		this.realmsScreen.blit(i, j, k, l, m, n);
		super.drawTexturedRect(i, j, k, l, m, n);
	}

	@Override
	public void drawGradientRect(int i, int j, int k, int l, int m, int n) {
		super.drawGradientRect(i, j, k, l, m, n);
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
	}

	@Override
	public boolean isPauseScreen() {
		return super.isPauseScreen();
	}

	@Override
	public void drawBackground(int i) {
		super.drawBackground(i);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.realmsScreen.render(i, j, f);
	}

	@Override
	public void drawStackTooltip(ItemStack itemStack, int i, int j) {
		super.drawStackTooltip(itemStack, i, j);
	}

	@Override
	public void drawTooltip(String string, int i, int j) {
		super.drawTooltip(string, i, j);
	}

	@Override
	public void drawTooltip(List<String> list, int i, int j) {
		super.drawTooltip(list, i, j);
	}

	@Override
	public void update() {
		this.realmsScreen.tick();
		super.update();
	}

	public int getFontHeight() {
		return 9;
	}

	public int getStringWidth(String string) {
		return this.fontRenderer.getStringWidth(string);
	}

	public void drawString(String string, int i, int j, int k) {
		this.fontRenderer.drawWithShadow(string, (float)i, (float)j, k);
	}

	public List<String> wrapStringToList(String string, int i) {
		return this.fontRenderer.wrapStringToWidthAsList(string, i);
	}

	public void clearListeners() {
		this.listeners.clear();
	}

	public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
		if (this.containsWidget(realmsGuiEventListener) || !this.listeners.add(realmsGuiEventListener.getProxy())) {
			LOGGER_REALMS.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
		if (!this.containsWidget(realmsGuiEventListener) || !this.listeners.remove(realmsGuiEventListener.getProxy())) {
			LOGGER_REALMS.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public boolean containsWidget(RealmsGuiEventListener realmsGuiEventListener) {
		return this.listeners.contains(realmsGuiEventListener.getProxy());
	}

	public void addButton(RealmsButton realmsButton) {
		this.addButton(realmsButton.getProxy());
	}

	public List<RealmsButton> getButtons() {
		List<RealmsButton> list = Lists.<RealmsButton>newArrayListWithExpectedSize(this.buttons.size());

		for (ButtonWidget buttonWidget : this.buttons) {
			list.add(((RealmsButtonWidget)buttonWidget).getRealmsButton());
		}

		return list;
	}

	public void clearWidgets() {
		Set<GuiEventListener> set = Sets.<GuiEventListener>newHashSet(this.buttons);
		this.listeners.removeIf(set::contains);
		this.buttons.clear();
	}

	public void removeButton(RealmsButton realmsButton) {
		this.listeners.remove(realmsButton.getProxy());
		this.buttons.remove(realmsButton.getProxy());
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsScreen.mouseClicked(d, e, i) ? true : method_2068(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsScreen.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsScreen.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.realmsScreen.keyPressed(i, j, k) ? true : super.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.realmsScreen.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void handle(boolean bl, int i) {
		this.realmsScreen.confirmResult(bl, i);
	}

	@Override
	public void onClosed() {
		this.realmsScreen.removed();
		super.onClosed();
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return bl ? this.fontRenderer.drawWithShadow(string, (float)i, (float)j, k) : this.fontRenderer.draw(string, (float)i, (float)j, k);
	}
}
