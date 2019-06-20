package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_333;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_437;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsScreenProxy extends class_437 {
	private final RealmsScreen screen;
	private static final Logger LOGGER = LogManager.getLogger();

	public RealmsScreenProxy(RealmsScreen realmsScreen) {
		super(class_333.field_18967);
		this.screen = realmsScreen;
	}

	public RealmsScreen getScreen() {
		return this.screen;
	}

	@Override
	public void init(class_310 arg, int i, int j) {
		this.screen.init(arg, i, j);
		super.init(arg, i, j);
	}

	@Override
	public void init() {
		this.screen.init();
		super.init();
	}

	public void drawCenteredString(String string, int i, int j, int k) {
		super.drawCenteredString(this.font, string, i, j, k);
	}

	public void drawString(String string, int i, int j, int k, boolean bl) {
		if (bl) {
			super.drawString(this.font, string, i, j, k);
		} else {
			this.font.method_1729(string, (float)i, (float)j, k);
		}
	}

	@Override
	public void blit(int i, int j, int k, int l, int m, int n) {
		this.screen.blit(i, j, k, l, m, n);
		super.blit(i, j, k, l, m, n);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, int o, int p) {
		class_332.blit(i, j, m, n, f, g, k, l, o, p);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n) {
		class_332.blit(i, j, f, g, k, l, m, n);
	}

	@Override
	public void fillGradient(int i, int j, int k, int l, int m, int n) {
		super.fillGradient(i, j, k, l, m, n);
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
	}

	@Override
	public boolean isPauseScreen() {
		return super.isPauseScreen();
	}

	@Override
	public void renderBackground(int i) {
		super.renderBackground(i);
	}

	@Override
	public void render(int i, int j, float f) {
		this.screen.render(i, j, f);
	}

	@Override
	public void renderTooltip(class_1799 arg, int i, int j) {
		super.renderTooltip(arg, i, j);
	}

	@Override
	public void renderTooltip(String string, int i, int j) {
		super.renderTooltip(string, i, j);
	}

	@Override
	public void renderTooltip(List<String> list, int i, int j) {
		super.renderTooltip(list, i, j);
	}

	@Override
	public void tick() {
		this.screen.tick();
		super.tick();
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	public int fontLineHeight() {
		return 9;
	}

	public int fontWidth(String string) {
		return this.font.method_1727(string);
	}

	public void fontDrawShadow(String string, int i, int j, int k) {
		this.font.method_1720(string, (float)i, (float)j, k);
	}

	public List<String> fontSplit(String string, int i) {
		return this.font.method_1728(string, i);
	}

	public void childrenClear() {
		this.children.clear();
	}

	public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
		if (this.hasWidget(realmsGuiEventListener) || !this.children.add(realmsGuiEventListener.getProxy())) {
			LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public void narrateLabels() {
		List<String> list = (List<String>)this.children
			.stream()
			.filter(arg -> arg instanceof RealmsLabelProxy)
			.map(arg -> ((RealmsLabelProxy)arg).getLabel().getText())
			.collect(Collectors.toList());
		Realms.narrateNow(list);
	}

	public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
		if (!this.hasWidget(realmsGuiEventListener) || !this.children.remove(realmsGuiEventListener.getProxy())) {
			LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
		return this.children.contains(realmsGuiEventListener.getProxy());
	}

	public void buttonsAdd(AbstractRealmsButton<?> abstractRealmsButton) {
		this.addButton(abstractRealmsButton.getProxy());
	}

	public List<AbstractRealmsButton<?>> buttons() {
		List<AbstractRealmsButton<?>> list = Lists.<AbstractRealmsButton<?>>newArrayListWithExpectedSize(this.buttons.size());

		for (class_339 lv : this.buttons) {
			list.add(((RealmsAbstractButtonProxy)lv).getButton());
		}

		return list;
	}

	public void buttonsClear() {
		Set<class_364> set = Sets.<class_364>newHashSet(this.buttons);
		this.children.removeIf(set::contains);
		this.buttons.clear();
	}

	public void removeButton(RealmsButton realmsButton) {
		this.children.remove(realmsButton.getProxy());
		this.buttons.remove(realmsButton.getProxy());
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.screen.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.screen.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.screen.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.screen.keyPressed(i, j, k) ? true : super.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.screen.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void removed() {
		this.screen.removed();
		super.removed();
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return bl ? this.font.method_1720(string, (float)i, (float)j, k) : this.font.method_1729(string, (float)i, (float)j, k);
	}

	public class_327 getFont() {
		return this.font;
	}
}
