package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsScreenProxy extends Screen {
	private final RealmsScreen screen;
	private static final Logger LOGGER = LogManager.getLogger();

	public RealmsScreenProxy(RealmsScreen realmsScreen) {
		super(NarratorManager.EMPTY);
		this.screen = realmsScreen;
	}

	public RealmsScreen getScreen() {
		return this.screen;
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		this.screen.init(client, width, height);
		super.init(client, width, height);
	}

	@Override
	public void init() {
		this.screen.init();
		super.init();
	}

	public void drawCenteredString(String text, int x, int y, int i) {
		super.drawCenteredString(this.font, text, x, y, i);
	}

	public void drawString(String text, int x, int y, int color, boolean bl) {
		if (bl) {
			super.drawString(this.font, text, x, y, color);
		} else {
			this.font.draw(text, (float)x, (float)y, color);
		}
	}

	@Override
	public void blit(int i, int j, int k, int l, int m, int n) {
		this.screen.blit(i, j, k, l, m, n);
		super.blit(i, j, k, l, m, n);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, int o, int p) {
		DrawableHelper.blit(i, j, m, n, f, g, k, l, o, p);
	}

	public static void blit(int i, int j, float f, float g, int k, int l, int m, int n) {
		DrawableHelper.blit(i, j, f, g, k, l, m, n);
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
	public void renderBackground(int alpha) {
		super.renderBackground(alpha);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.screen.render(mouseX, mouseY, delta);
	}

	@Override
	public void renderTooltip(ItemStack stack, int x, int y) {
		super.renderTooltip(stack, x, y);
	}

	@Override
	public void renderTooltip(String text, int x, int y) {
		super.renderTooltip(text, x, y);
	}

	@Override
	public void renderTooltip(List<String> text, int x, int y) {
		super.renderTooltip(text, x, y);
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
		return this.font.getStringWidth(string);
	}

	public void fontDrawShadow(String text, int x, int y, int i) {
		this.font.drawWithShadow(text, (float)x, (float)y, i);
	}

	public List<String> fontSplit(String string, int i) {
		return this.font.wrapStringToWidthAsList(string, i);
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
			.filter(element -> element instanceof RealmsLabelProxy)
			.map(element -> ((RealmsLabelProxy)element).getLabel().getText())
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

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			list.add(((RealmsAbstractButtonProxy)abstractButtonWidget).getButton());
		}

		return list;
	}

	public void buttonsClear() {
		Set<Element> set = Sets.<Element>newHashSet(this.buttons);
		this.children.removeIf(set::contains);
		this.buttons.clear();
	}

	public void removeButton(RealmsButton realmsButton) {
		this.children.remove(realmsButton.getProxy());
		this.buttons.remove(realmsButton.getProxy());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.screen.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.screen.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.screen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ? true : super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.screen.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return this.screen.charTyped(chr, keyCode) ? true : super.charTyped(chr, keyCode);
	}

	@Override
	public void removed() {
		this.screen.removed();
		super.removed();
	}

	public int draw(String string, int i, int j, int k, boolean bl) {
		return bl ? this.font.drawWithShadow(string, (float)i, (float)j, k) : this.font.draw(string, (float)i, (float)j, k);
	}

	public TextRenderer getFont() {
		return this.font;
	}
}
