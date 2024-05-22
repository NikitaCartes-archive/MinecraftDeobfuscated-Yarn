package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TabButtonWidget extends ClickableWidget {
	private static final ButtonTextures TAB_BUTTON_TEXTURES = new ButtonTextures(
		Identifier.ofVanilla("widget/tab_selected"),
		Identifier.ofVanilla("widget/tab"),
		Identifier.ofVanilla("widget/tab_selected_highlighted"),
		Identifier.ofVanilla("widget/tab_highlighted")
	);
	private static final int field_43063 = 3;
	private static final int field_43064 = 1;
	private static final int field_43065 = 1;
	private static final int field_43066 = 4;
	private static final int field_43067 = 2;
	private final TabManager tabManager;
	private final Tab tab;

	public TabButtonWidget(TabManager tabManager, Tab tab, int width, int height) {
		super(0, 0, width, height, tab.getTitle());
		this.tabManager = tabManager;
		this.tab = tab;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		RenderSystem.enableBlend();
		context.drawGuiTexture(TAB_BUTTON_TEXTURES.get(this.isCurrentTab(), this.isSelected()), this.getX(), this.getY(), this.width, this.height);
		RenderSystem.disableBlend();
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int i = this.active ? -1 : -6250336;
		this.drawMessage(context, textRenderer, i);
		if (this.isCurrentTab()) {
			this.renderBackgroundTexture(context, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom());
			this.drawCurrentTabLine(context, textRenderer, i);
		}
	}

	protected void renderBackgroundTexture(DrawContext context, int left, int top, int right, int bottom) {
		Screen.renderBackgroundTexture(context, Screen.MENU_BACKGROUND_TEXTURE, left, top, 0.0F, 0.0F, right - left, bottom - top);
	}

	public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
		int i = this.getX() + 1;
		int j = this.getY() + (this.isCurrentTab() ? 0 : 3);
		int k = this.getX() + this.getWidth() - 1;
		int l = this.getY() + this.getHeight();
		drawScrollableText(context, textRenderer, this.getMessage(), i, j, k, l, color);
	}

	private void drawCurrentTabLine(DrawContext context, TextRenderer textRenderer, int color) {
		int i = Math.min(textRenderer.getWidth(this.getMessage()), this.getWidth() - 4);
		int j = this.getX() + (this.getWidth() - i) / 2;
		int k = this.getY() + this.getHeight() - 2;
		context.fill(j, k, j + i, k + 1, color);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, Text.translatable("gui.narrate.tab", this.tab.getTitle()));
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	public Tab getTab() {
		return this.tab;
	}

	public boolean isCurrentTab() {
		return this.tabManager.getCurrentTab() == this.tab;
	}
}
