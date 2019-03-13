package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ButtonWidget extends DrawableHelper implements Drawable, InputListener {
	public static final Identifier field_2072 = new Identifier("textures/gui/widgets.png");
	protected int width;
	protected int height;
	public int x;
	public int y;
	private String text;
	private boolean field_18727;
	private boolean hovered;
	public boolean enabled = true;
	public boolean visible = true;
	protected float opacity = 1.0F;
	protected long nextNarrationTime = Long.MAX_VALUE;
	private boolean field_18728;

	public ButtonWidget(int i, int j, String string) {
		this(i, j, 200, 20, string);
	}

	public ButtonWidget(int i, int j, int k, int l, String string) {
		this.x = i;
		this.y = j;
		this.width = k;
		this.height = l;
		this.text = string;
	}

	protected int getTextureId(boolean bl) {
		int i = 1;
		if (!this.enabled) {
			i = 0;
		} else if (bl) {
			i = 2;
		}

		return i;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			if (this.field_18727 != this.isHovered()) {
				if (this.isHovered()) {
					if (this.field_18728) {
						this.nextNarrationTime = SystemUtil.getMeasuringTimeMs() + 200L;
					} else {
						this.nextNarrationTime = SystemUtil.getMeasuringTimeMs() + 750L;
					}
				} else {
					this.nextNarrationTime = Long.MAX_VALUE;
				}
			}

			if (this.visible) {
				this.drawButton(i, j, f);
			}

			this.narrateIfNecessary();
			this.field_18727 = this.isHovered();
		}
	}

	protected void narrateIfNecessary() {
		if (this.enabled && this.isHovered() && SystemUtil.getMeasuringTimeMs() > this.nextNarrationTime) {
			String string = this.getNarrationString();
			if (!string.isEmpty()) {
				NarratorManager narratorManager = NarratorManager.INSTANCE;
				narratorManager.clear();
				narratorManager.method_1794(ChatMessageType.field_11735, new StringTextComponent(string));
				this.nextNarrationTime = Long.MAX_VALUE;
			}
		}
	}

	protected String getNarrationString() {
		return this.text.isEmpty() ? "" : I18n.translate("gui.narrate.button", this.getText());
	}

	public void drawButton(int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.field_1772;
		minecraftClient.method_1531().method_4618(field_2072);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.opacity);
		int k = this.getTextureId(this.isHovered());
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.drawTexturedRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
		this.drawTexturedRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
		this.drawBackground(minecraftClient, i, j);
		int l = 14737632;
		if (!this.enabled) {
			l = 10526880;
		} else if (this.isHovered()) {
			l = 16777120;
		}

		this.drawStringCentered(textRenderer, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.opacity * 255.0F) << 24);
	}

	protected void drawBackground(MinecraftClient minecraftClient, int i, int j) {
	}

	public void method_19347(double d, double e) {
	}

	public void onReleased(double d, double e) {
	}

	protected void onDragged(double d, double e, double f, double g) {
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			this.onReleased(d, e);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i == 0) {
			this.onDragged(d, e, f, g);
			return true;
		} else {
			return false;
		}
	}

	protected boolean isSelected(double d, double e) {
		return this.enabled && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.hovered || this.field_18728;
	}

	@Override
	public void setHasFocus(boolean bl) {
		this.field_18728 = bl;
	}

	@Override
	public boolean hasFocus() {
		return this.enabled && this.visible;
	}

	@Override
	public boolean method_19356(double d, double e) {
		return this.enabled && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public void onHover(int i, int j) {
	}

	public void method_1832(SoundLoader soundLoader) {
		soundLoader.play(PositionedSoundInstance.method_4758(SoundEvents.field_15015, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int i) {
		this.width = i;
	}

	public void setOpacity(float f) {
		this.opacity = f;
	}

	public void setText(String string) {
		if (!Objects.equals(string, this.text)) {
			this.nextNarrationTime = SystemUtil.getMeasuringTimeMs() + 250L;
		}

		this.text = string;
	}

	public String getText() {
		return this.text;
	}
}
