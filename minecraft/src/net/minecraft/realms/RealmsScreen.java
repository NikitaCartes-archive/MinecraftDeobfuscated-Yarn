package net.minecraft.realms;

import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RealmsScreen extends RealmsGuiEventListener {
	public static final int SKIN_HEAD_U = 8;
	public static final int SKIN_HEAD_V = 8;
	public static final int SKIN_HEAD_WIDTH = 8;
	public static final int SKIN_HEAD_HEIGHT = 8;
	public static final int SKIN_HAT_U = 40;
	public static final int SKIN_HAT_V = 8;
	public static final int SKIN_HAT_WIDTH = 8;
	public static final int SKIN_HAT_HEIGHT = 8;
	public static final int SKIN_TEX_WIDTH = 64;
	public static final int SKIN_TEX_HEIGHT = 64;
	private MinecraftClient minecraft;
	public int width;
	public int height;
	private final net.minecraft.client.gui.menu.RealmsScreen proxy = new net.minecraft.client.gui.menu.RealmsScreen(this);

	public net.minecraft.client.gui.menu.RealmsScreen getProxy() {
		return this.proxy;
	}

	public void init() {
	}

	public void init(MinecraftClient minecraftClient, int i, int j) {
		this.minecraft = minecraftClient;
	}

	public void drawCenteredString(String string, int i, int j, int k) {
		this.proxy.drawStringCentered(string, i, j, k);
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return this.proxy.draw(string, i, j, k, bl);
	}

	public void drawString(String string, int i, int j, int k) {
		this.drawString(string, i, j, k, true);
	}

	public void drawString(String string, int i, int j, int k, boolean bl) {
		this.proxy.drawString(string, i, j, k, false);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.proxy.drawTexturedRect(i, j, k, l, m, n);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
		DrawableHelper.drawTexturedRect(i, j, f, g, k, l, m, n, h, o);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, float h, float m) {
		DrawableHelper.drawTexturedRect(i, j, f, g, k, l, h, m);
	}

	public void fillGradient(int i, int j, int k, int l, int m, int n) {
		this.proxy.drawGradientRect(i, j, k, l, m, n);
	}

	public void renderBackground() {
		this.proxy.drawBackground();
	}

	public boolean isPauseScreen() {
		return this.proxy.isPauseScreen();
	}

	public void renderBackground(int i) {
		this.proxy.drawBackground(i);
	}

	public void render(int i, int j, float f) {
		for (int k = 0; k < this.proxy.getButtons().size(); k++) {
			((RealmsAbstractButton)this.proxy.getButtons().get(k)).render(i, j, f);
		}
	}

	public void renderTooltip(ItemStack itemStack, int i, int j) {
		this.proxy.drawStackTooltip(itemStack, i, j);
	}

	public void renderTooltip(String string, int i, int j) {
		this.proxy.drawTooltip(string, i, j);
	}

	public void renderTooltip(List<String> list, int i, int j) {
		this.proxy.drawTooltip(list, i, j);
	}

	public static void bindFace(String string, String string2) {
		Identifier identifier = AbstractClientPlayerEntity.getSkinId(string2);
		if (identifier == null) {
			identifier = DefaultSkinHelper.getTexture(UUIDTypeAdapter.fromString(string));
		}

		AbstractClientPlayerEntity.loadSkin(identifier, string2);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	public static void bind(String string) {
		Identifier identifier = new Identifier(string);
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
	}

	public void tick() {
	}

	public int width() {
		return this.proxy.screenWidth;
	}

	public int height() {
		return this.proxy.screenHeight;
	}

	public int fontLineHeight() {
		return this.proxy.getFontHeight();
	}

	public int fontWidth(String string) {
		return this.proxy.getStringWidth(string);
	}

	public void fontDrawShadow(String string, int i, int j, int k) {
		this.proxy.drawString(string, i, j, k);
	}

	public List<String> fontSplit(String string, int i) {
		return this.proxy.wrapStringToList(string, i);
	}

	public void childrenClear() {
		this.proxy.clearListeners();
	}

	public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.addWidget(realmsGuiEventListener);
	}

	public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.removeWidget(realmsGuiEventListener);
	}

	public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
		return this.proxy.containsWidget(realmsGuiEventListener);
	}

	public void buttonsAdd(RealmsAbstractButton<?> realmsAbstractButton) {
		this.proxy.addButton(realmsAbstractButton);
	}

	public List<RealmsAbstractButton<?>> buttons() {
		return this.proxy.getButtons();
	}

	protected void buttonsClear() {
		this.proxy.clearWidgets();
	}

	protected void focusOn(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.focusOn(realmsGuiEventListener.getProxy());
	}

	public RealmsEditBox newEditBox(int i, int j, int k, int l, int m) {
		return new RealmsEditBox(i, j, k, l, m);
	}

	public void confirmResult(boolean bl, int i) {
	}

	public static String getLocalizedString(String string) {
		return I18n.translate(string);
	}

	public static String getLocalizedString(String string, Object... objects) {
		return I18n.translate(string, objects);
	}

	public List<String> getLocalizedStringWithLineWidth(String string, int i) {
		return this.minecraft.textRenderer.wrapStringToWidthAsList(I18n.translate(string), i);
	}

	public RealmsAnvilLevelStorageSource getLevelStorageSource() {
		return new RealmsAnvilLevelStorageSource(MinecraftClient.getInstance().getLevelStorage());
	}

	public void removed() {
	}

	protected void removeButton(RealmsButton realmsButton) {
		this.proxy.removeButton(realmsButton);
	}

	protected void setKeyboardHandlerSendRepeatsToGui(boolean bl) {
		this.minecraft.keyboard.enableRepeatEvents(bl);
	}

	protected boolean isKeyDown(int i) {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), i);
	}
}
