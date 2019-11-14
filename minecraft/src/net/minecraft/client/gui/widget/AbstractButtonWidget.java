package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractButtonWidget extends DrawableHelper implements Drawable, Element {
	public static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private static final int NARRATE_DELAY_MOUSE = 750;
	private static final int NARRATE_DELAY_FOCUS = 200;
	protected int width;
	protected int height;
	public int x;
	public int y;
	private String message;
	private boolean wasHovered;
	protected boolean isHovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private boolean focused;

	public AbstractButtonWidget(int x, int y, String text) {
		this(x, y, 200, 20, text);
	}

	public AbstractButtonWidget(int x, int y, int width, int height, String message) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.message = message;
	}

	protected int getYImage(boolean isHovered) {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (isHovered) {
			i = 2;
		}

		return i;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			if (this.wasHovered != this.isHovered()) {
				if (this.isHovered()) {
					if (this.focused) {
						this.queueNarration(200);
					} else {
						this.queueNarration(750);
					}
				} else {
					this.nextNarration = Long.MAX_VALUE;
				}
			}

			if (this.visible) {
				this.renderButton(mouseX, mouseY, delta);
			}

			this.narrate();
			this.wasHovered = this.isHovered();
		}
	}

	protected void narrate() {
		if (this.active && this.isHovered() && Util.getMeasuringTimeMs() > this.nextNarration) {
			String string = this.getNarrationMessage();
			if (!string.isEmpty()) {
				NarratorManager.INSTANCE.narrate(string);
				this.nextNarration = Long.MAX_VALUE;
			}
		}
	}

	protected String getNarrationMessage() {
		return this.getMessage().isEmpty() ? "" : I18n.translate("gui.narrate.button", this.getMessage());
	}

	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.renderBg(minecraftClient, mouseX, mouseY);
		int j = 16777215;
		if (!this.active) {
			j = 10526880;
		} else if (this.isHovered()) {
			j = 16777120;
		}

		this.drawCenteredString(
			textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24
		);
	}

	protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
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

	protected boolean isValidClickButton(int i) {
		return i == 0;
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
			&& mouseX >= (double)this.x
			&& mouseY >= (double)this.y
			&& mouseX < (double)(this.x + this.width)
			&& mouseY < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.isHovered || this.focused;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		if (this.active && this.visible) {
			this.focused = !this.focused;
			this.onFocusedChanged(this.focused);
			return this.focused;
		} else {
			return false;
		}
	}

	protected void onFocusedChanged(boolean bl) {
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.x
			&& mouseY >= (double)this.y
			&& mouseX < (double)(this.x + this.width)
			&& mouseY < (double)(this.y + this.height);
	}

	public void renderToolTip(int mouseX, int mouseY) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int value) {
		this.width = value;
	}

	public void setAlpha(float value) {
		this.alpha = value;
	}

	public void setMessage(String value) {
		if (!Objects.equals(value, this.message)) {
			this.queueNarration(250);
		}

		this.message = value;
	}

	public void queueNarration(int i) {
		this.nextNarration = Util.getMeasuringTimeMs() + (long)i;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean isFocused() {
		return this.focused;
	}

	protected void setFocused(boolean focused) {
		this.focused = focused;
	}
}
