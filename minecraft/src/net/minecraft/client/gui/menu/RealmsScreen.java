package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsAbstractButton;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsScreen extends Screen {
	private final net.minecraft.realms.RealmsScreen realmsScreen;
	private static final Logger LOGGER_REALMS = LogManager.getLogger();

	public RealmsScreen(net.minecraft.realms.RealmsScreen realmsScreen) {
		super(NarratorManager.field_18967);
		this.realmsScreen = realmsScreen;
	}

	public net.minecraft.realms.RealmsScreen getRealmsScreen() {
		return this.realmsScreen;
	}

	@Override
	public void initialize(MinecraftClient minecraftClient, int i, int j) {
		this.realmsScreen.init(minecraftClient, i, j);
		super.initialize(minecraftClient, i, j);
	}

	@Override
	public void onInitialized() {
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

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
		DrawableHelper.drawTexturedRect(i, j, f, g, k, l, m, n, h, o);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, float h, float m) {
		DrawableHelper.drawTexturedRect(i, j, f, g, k, l, h, m);
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
	public void render(int i, int j, float f) {
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

	public static void bindFace(String string) {
		Identifier identifier = AbstractClientPlayerEntity.getSkinId(string);
		if (identifier == null) {
			identifier = AbstractClientPlayerEntity.getSkinId("default");
		}

		AbstractClientPlayerEntity.loadSkin(identifier, string);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	public static void bind(String string) {
		Identifier identifier = new Identifier(string);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	@Override
	public void update() {
		this.realmsScreen.tick();
		super.update();
	}

	public int width() {
		return this.screenWidth;
	}

	public int height() {
		return this.screenHeight;
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

	public void addButton(RealmsAbstractButton<?> realmsAbstractButton) {
		this.addButton(realmsAbstractButton.getProxy());
	}

	public List<RealmsAbstractButton<?>> getButtons() {
		List<RealmsAbstractButton<?>> list = Lists.<RealmsAbstractButton<?>>newArrayListWithExpectedSize(this.buttons.size());

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			list.add(((RealmsButton)abstractButtonWidget).getRealmsProxy());
		}

		return list;
	}

	public void clearWidgets() {
		Set<InputListener> set = Sets.<InputListener>newHashSet(this.buttons);
		this.listeners.removeIf(set::contains);
		this.buttons.clear();
	}

	public void removeButton(net.minecraft.realms.RealmsButton realmsButton) {
		this.listeners.remove(realmsButton.getProxy());
		this.buttons.remove(realmsButton.getProxy());
	}

	public RealmsEditBox newEditBox(int i, int j, int k, int l, int m) {
		return new RealmsEditBox(i, j, k, l, m);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsScreen.mouseClicked(d, e, i) ? true : access$001(this, d, e, i);
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
	public void confirmResult(boolean bl, int i) {
		this.realmsScreen.confirmResult(bl, i);
	}

	@Override
	public void onClosed() {
		this.realmsScreen.removed();
		super.onClosed();
	}

	public static String getLocalizedString(String string) {
		return I18n.translate(string);
	}

	public static String getLocalizedString(String string, Object... objects) {
		return I18n.translate(string, objects);
	}

	public RealmsAnvilLevelStorageSource getLevelStorageSource() {
		return new RealmsAnvilLevelStorageSource(MinecraftClient.getInstance().getLevelStorage());
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return bl ? this.fontRenderer.drawWithShadow(string, (float)i, (float)j, k) : this.fontRenderer.draw(string, (float)i, (float)j, k);
	}
}
