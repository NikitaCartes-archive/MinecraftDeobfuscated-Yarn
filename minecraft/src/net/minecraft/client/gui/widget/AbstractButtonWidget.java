package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractButtonWidget extends DrawableHelper implements Drawable, InputListener {
	public static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private static final int NARRATE_DELAY_MOUSE = 750;
	private static final int NARRATE_DELAY_FOCUS = 200;
	protected int width;
	protected int height;
	public int x;
	public int y;
	private String message;
	private boolean wasHovered;
	private boolean isHovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private boolean focused;

	public AbstractButtonWidget(int i, int j, String string) {
		this(i, j, 200, 20, string);
	}

	public AbstractButtonWidget(int i, int j, int k, int l, String string) {
		this.x = i;
		this.y = j;
		this.width = k;
		this.height = l;
		this.message = string;
	}

	protected int getYImage(boolean bl) {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (bl) {
			i = 2;
		}

		return i;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.visible) {
			this.isHovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			if (this.wasHovered != this.isHovered()) {
				if (this.isHovered()) {
					if (this.focused) {
						this.nextNarration = SystemUtil.getMeasuringTimeMs() + 200L;
					} else {
						this.nextNarration = SystemUtil.getMeasuringTimeMs() + 750L;
					}
				} else {
					this.nextNarration = Long.MAX_VALUE;
				}
			}

			if (this.visible) {
				this.renderButton(i, j, f);
			}

			this.narrate();
			this.wasHovered = this.isHovered();
		}
	}

	protected void narrate() {
		if (this.active && this.isHovered() && SystemUtil.getMeasuringTimeMs() > this.nextNarration) {
			String string = this.getNarrationMessage();
			if (!string.isEmpty()) {
				NarratorManager.INSTANCE.method_19788(string);
				this.nextNarration = Long.MAX_VALUE;
			}
		}
	}

	protected String getNarrationMessage() {
		return this.message.isEmpty() ? "" : I18n.translate("gui.narrate.button", this.getMessage());
	}

	public void renderButton(int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int k = this.getYImage(this.isHovered());
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.drawTexturedRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
		this.drawTexturedRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
		this.renderBg(minecraftClient, i, j);
		int l = 14737632;
		if (!this.active) {
			l = 10526880;
		} else if (this.isHovered()) {
			l = 16777120;
		}

		this.drawStringCentered(textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	protected void renderBg(MinecraftClient minecraftClient, int i, int j) {
	}

	public void onClick(double d, double e) {
	}

	public void onRelease(double d, double e) {
	}

	protected void onDrag(double d, double e, double f, double g) {
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			this.onRelease(d, e);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i == 0) {
			this.onDrag(d, e, f, g);
			return true;
		} else {
			return false;
		}
	}

	protected boolean clicked(double d, double e) {
		return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.isHovered || this.focused;
	}

	@Override
	public void onFocusChanged(boolean bl) {
		this.focused = bl;
	}

	@Override
	public boolean isPartOfFocusCycle() {
		return this.active && this.visible;
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public void renderToolTip(int i, int j) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int i) {
		this.width = i;
	}

	public void setAlpha(float f) {
		this.alpha = f;
	}

	public void setMessage(String string) {
		if (!Objects.equals(string, this.message)) {
			this.nextNarration = SystemUtil.getMeasuringTimeMs() + 250L;
		}

		this.message = string;
	}

	public String getMessage() {
		return this.message;
	}
}
