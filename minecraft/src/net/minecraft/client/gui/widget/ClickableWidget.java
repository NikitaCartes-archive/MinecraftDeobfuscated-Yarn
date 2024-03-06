package net.minecraft.client.gui.widget;

import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

/**
 * A clickable widget is a GUI element that has many methods to handle different
 * mouse actions. In addition, it allows a message to be rendered on the widget
 * and narrated when the widget is selected.
 */
@Environment(EnvType.CLIENT)
public abstract class ClickableWidget implements Drawable, Element, Widget, Selectable {
	private static final double field_43055 = 0.5;
	private static final double field_43056 = 3.0;
	protected int width;
	protected int height;
	private int x;
	private int y;
	private Text message;
	protected boolean hovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	private int navigationOrder;
	private boolean focused;
	private final TooltipState tooltip = new TooltipState();

	public ClickableWidget(int x, int y, int width, int height, Text message) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.message = message;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = context.scissorContains(mouseX, mouseY)
				&& mouseX >= this.getX()
				&& mouseY >= this.getY()
				&& mouseX < this.getX() + this.width
				&& mouseY < this.getY() + this.height;
			this.renderWidget(context, mouseX, mouseY, delta);
			this.tooltip.render(this.isHovered(), this.isFocused(), this.getNavigationFocus());
		}
	}

	public void setTooltip(@Nullable Tooltip tooltip) {
		this.tooltip.setTooltip(tooltip);
	}

	@Nullable
	public Tooltip getTooltip() {
		return this.tooltip.getTooltip();
	}

	public void setTooltipDelay(Duration tooltipDelay) {
		this.tooltip.setDelay(tooltipDelay);
	}

	protected MutableText getNarrationMessage() {
		return getNarrationMessage(this.getMessage());
	}

	public static MutableText getNarrationMessage(Text message) {
		return Text.translatable("gui.narrate.button", message);
	}

	protected abstract void renderWidget(DrawContext context, int mouseX, int mouseY, float delta);

	protected static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
		drawScrollableText(context, textRenderer, text, (startX + endX) / 2, startX, startY, endX, endY, color);
	}

	protected static void drawScrollableText(
		DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color
	) {
		int i = textRenderer.getWidth(text);
		int j = (startY + endY - 9) / 2 + 1;
		int k = endX - startX;
		if (i > k) {
			int l = i - k;
			double d = (double)Util.getMeasuringTimeMs() / 1000.0;
			double e = Math.max((double)l * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = MathHelper.lerp(f, 0.0, (double)l);
			context.enableScissor(startX, startY, endX, endY);
			context.drawTextWithShadow(textRenderer, text, startX - (int)g, j, color);
			context.disableScissor();
		} else {
			int l = MathHelper.clamp(centerX, startX + i / 2, endX - i / 2);
			context.drawCenteredTextWithShadow(textRenderer, text, l, j, color);
		}
	}

	protected void drawScrollableText(DrawContext context, TextRenderer textRenderer, int xMargin, int color) {
		int i = this.getX() + xMargin;
		int j = this.getX() + this.getWidth() - xMargin;
		drawScrollableText(context, textRenderer, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
	}

	protected void drawBackground(DrawContext context, int startX, int startY, int endX, int endY) {
		Screen.renderBackgroundTexture(context, startX, startY, endX - startX, endY - startY);
	}

	public void onClick(double mouseX, double mouseY) {
	}

	public void onRelease(double mouseX, double mouseY) {
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.clicked(mouseX, mouseY);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onClick(mouseX, mouseY);
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isValidClickButton(button)) {
			this.onRelease(mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean isValidClickButton(int button) {
		return button == 0;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isValidClickButton(button)) {
			this.onDrag(mouseX, mouseY, deltaX, deltaY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean clicked(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.getX()
			&& mouseY >= (double)this.getY()
			&& mouseX < (double)(this.getX() + this.getWidth())
			&& mouseY < (double)(this.getY() + this.getHeight());
	}

	@Nullable
	@Override
	public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		if (!this.active || !this.visible) {
			return null;
		} else {
			return !this.isFocused() ? GuiNavigationPath.of(this) : null;
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.getX()
			&& mouseY >= (double)this.getY()
			&& mouseX < (double)(this.getX() + this.width)
			&& mouseY < (double)(this.getY() + this.height);
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setMessage(Text message) {
		this.message = message;
	}

	public Text getMessage() {
		return this.message;
	}

	@Override
	public boolean isFocused() {
		return this.focused;
	}

	public boolean isHovered() {
		return this.hovered;
	}

	public boolean isSelected() {
		return this.isHovered() || this.isFocused();
	}

	@Override
	public boolean isNarratable() {
		return this.visible && this.active;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public Selectable.SelectionType getType() {
		if (this.isFocused()) {
			return Selectable.SelectionType.FOCUSED;
		} else {
			return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}

	@Override
	public final void appendNarrations(NarrationMessageBuilder builder) {
		this.appendClickableNarrations(builder);
		this.tooltip.appendNarrations(builder);
	}

	protected abstract void appendClickableNarrations(NarrationMessageBuilder builder);

	protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.focused"));
			} else {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"));
			}
		}
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	public int getRight() {
		return this.getX() + this.getWidth();
	}

	public int getBottom() {
		return this.getY() + this.getHeight();
	}

	@Override
	public void forEachChild(Consumer<ClickableWidget> consumer) {
		consumer.accept(this);
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public ScreenRect getNavigationFocus() {
		return Widget.super.getNavigationFocus();
	}

	public void setDimensionsAndPosition(int width, int height, int x, int y) {
		this.setDimensions(width, height);
		this.setPosition(x, y);
	}

	@Override
	public int getNavigationOrder() {
		return this.navigationOrder;
	}

	public void setNavigationOrder(int navigationOrder) {
		this.navigationOrder = navigationOrder;
	}
}
