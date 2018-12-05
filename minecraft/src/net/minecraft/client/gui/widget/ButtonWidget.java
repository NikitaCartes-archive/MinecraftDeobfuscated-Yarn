package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ButtonWidget extends Drawable implements GuiEventListener {
	protected static final Identifier WIDGET_TEX = new Identifier("textures/gui/widgets.png");
	protected int width = 200;
	protected int height = 20;
	public int x;
	public int y;
	public String text;
	public int id;
	public boolean enabled = true;
	public boolean visible = true;
	protected boolean hovered;
	private boolean field_2073;

	public ButtonWidget(int i, int j, int k, String string) {
		this(i, j, k, 200, 20, string);
	}

	public ButtonWidget(int i, int j, int k, int l, int m, String string) {
		this.id = i;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
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

	public void draw(int i, int j, float f) {
		if (this.visible) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			FontRenderer fontRenderer = minecraftClient.fontRenderer;
			minecraftClient.getTextureManager().bindTexture(WIDGET_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			int k = this.getTextureId(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexturedRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			this.drawBackground(minecraftClient, i, j);
			int l = 14737632;
			if (!this.enabled) {
				l = 10526880;
			} else if (this.hovered) {
				l = 16777120;
			}

			this.drawStringCentered(fontRenderer, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}

	protected void drawBackground(MinecraftClient minecraftClient, int i, int j) {
	}

	public void onPressed(double d, double e) {
		this.field_2073 = true;
	}

	public void onReleased(double d, double e) {
		this.field_2073 = false;
	}

	protected void method_1822(double d, double e, double f, double g) {
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			boolean bl = this.method_1829(d, e);
			if (bl) {
				this.onPressed(MinecraftClient.getInstance().getSoundLoader());
				this.onPressed(d, e);
				return true;
			}
		}

		return false;
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
			this.method_1822(d, e, f, g);
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_1829(double d, double e) {
		return this.enabled && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.hovered;
	}

	public void onHover(int i, int j) {
	}

	public void onPressed(SoundLoader soundLoader) {
		soundLoader.play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int i) {
		this.width = i;
	}
}
