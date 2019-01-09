package net.minecraft.realms;

import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1068;
import net.minecraft.class_1074;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_399;
import net.minecraft.class_742;

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
	private class_310 minecraft;
	public int width;
	public int height;
	private final class_399 proxy = new class_399(this);

	public class_399 getProxy() {
		return this.proxy;
	}

	public void init() {
	}

	public void init(class_310 arg, int i, int j) {
		this.minecraft = arg;
	}

	public void drawCenteredString(String string, int i, int j, int k) {
		this.proxy.method_2075(string, i, j, k);
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return this.proxy.method_2082(string, i, j, k, bl);
	}

	public void drawString(String string, int i, int j, int k) {
		this.drawString(string, i, j, k, true);
	}

	public void drawString(String string, int i, int j, int k, boolean bl) {
		this.proxy.method_2081(string, i, j, k, false);
	}

	public void blit(int i, int j, int k, int l, int m, int n) {
		this.proxy.method_1788(i, j, k, l, m, n);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
		class_332.method_1786(i, j, f, g, k, l, m, n, h, o);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, float h, float m) {
		class_332.method_1781(i, j, f, g, k, l, h, m);
	}

	public void fillGradient(int i, int j, int k, int l, int m, int n) {
		this.proxy.method_1782(i, j, k, l, m, n);
	}

	public void renderBackground() {
		this.proxy.method_2240();
	}

	public boolean isPauseScreen() {
		return this.proxy.method_2222();
	}

	public void renderBackground(int i) {
		this.proxy.method_2236(i);
	}

	public void render(int i, int j, float f) {
		for (int k = 0; k < this.proxy.method_2074().size(); k++) {
			((RealmsButton)this.proxy.method_2074().get(k)).render(i, j, f);
		}
	}

	public void renderTooltip(class_1799 arg, int i, int j) {
		this.proxy.method_2218(arg, i, j);
	}

	public void renderTooltip(String string, int i, int j) {
		this.proxy.method_2215(string, i, j);
	}

	public void renderTooltip(List<String> list, int i, int j) {
		this.proxy.method_2211(list, i, j);
	}

	public static void bindFace(String string, String string2) {
		class_2960 lv = class_742.method_3124(string2);
		if (lv == null) {
			lv = class_1068.method_4648(UUIDTypeAdapter.fromString(string));
		}

		class_742.method_3120(lv, string2);
		class_310.method_1551().method_1531().method_4618(lv);
	}

	public static void bind(String string) {
		class_2960 lv = new class_2960(string);
		class_310.method_1551().method_1531().method_4618(lv);
	}

	public void tick() {
	}

	public int width() {
		return this.proxy.field_2561;
	}

	public int height() {
		return this.proxy.field_2559;
	}

	public CompletableFuture<Object> threadSafeSetScreen(RealmsScreen realmsScreen) {
		return this.minecraft.method_5382(() -> Realms.setScreen(realmsScreen));
	}

	public int fontLineHeight() {
		return this.proxy.method_2080();
	}

	public int fontWidth(String string) {
		return this.proxy.method_2076(string);
	}

	public void fontDrawShadow(String string, int i, int j, int k) {
		this.proxy.method_2070(string, i, j, k);
	}

	public List<String> fontSplit(String string, int i) {
		return this.proxy.method_2072(string, i);
	}

	public void childrenClear() {
		this.proxy.method_2084();
	}

	public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_2073(realmsGuiEventListener);
	}

	public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_2079(realmsGuiEventListener);
	}

	public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
		return this.proxy.method_2083(realmsGuiEventListener);
	}

	public void buttonsAdd(RealmsButton realmsButton) {
		this.proxy.method_2077(realmsButton);
	}

	public List<RealmsButton> buttons() {
		return this.proxy.method_2074();
	}

	protected void buttonsClear() {
		this.proxy.method_2071();
	}

	protected void focusOn(RealmsGuiEventListener realmsGuiEventListener) {
		this.proxy.method_1973(realmsGuiEventListener.getProxy());
	}

	public void focusNext() {
		this.proxy.method_1971();
	}

	public RealmsEditBox newEditBox(int i, int j, int k, int l, int m) {
		return new RealmsEditBox(i, j, k, l, m);
	}

	public void confirmResult(boolean bl, int i) {
	}

	public static String getLocalizedString(String string) {
		return class_1074.method_4662(string);
	}

	public static String getLocalizedString(String string, Object... objects) {
		return class_1074.method_4662(string, objects);
	}

	public List<String> getLocalizedStringWithLineWidth(String string, int i) {
		return this.minecraft.field_1772.method_1728(class_1074.method_4662(string), i);
	}

	public RealmsAnvilLevelStorageSource getLevelStorageSource() {
		return new RealmsAnvilLevelStorageSource(class_310.method_1551().method_1586());
	}

	public void removed() {
	}

	protected void removeButton(RealmsButton realmsButton) {
		this.proxy.method_2078(realmsButton);
	}

	protected void setKeyboardHandlerSendRepeatsToGui(boolean bl) {
		this.minecraft.field_1774.method_1462(bl);
	}

	protected boolean isKeyDown(int i) {
		return class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), i);
	}
}
