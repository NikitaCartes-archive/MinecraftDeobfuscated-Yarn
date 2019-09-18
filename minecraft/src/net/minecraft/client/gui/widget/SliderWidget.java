package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class SliderWidget extends AbstractButtonWidget {
	protected final GameOptions options;
	protected double value;

	protected SliderWidget(int i, int j, int k, int l, double d) {
		this(MinecraftClient.getInstance().options, i, j, k, l, d);
	}

	protected SliderWidget(GameOptions gameOptions, int i, int j, int k, int l, double d) {
		super(i, j, k, l, "");
		this.options = gameOptions;
		this.value = d;
	}

	@Override
	protected int getYImage(boolean bl) {
		return 0;
	}

	@Override
	protected String getNarrationMessage() {
		return I18n.translate("gui.narrate.slider", this.getMessage());
	}

	@Override
	protected void renderBg(MinecraftClient minecraftClient, int i, int j) {
		minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = (this.isHovered() ? 2 : 1) * 20;
		this.blit(this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + k, 4, 20);
		this.blit(this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + k, 4, 20);
	}

	@Override
	public void onClick(double d, double e) {
		this.setValueFromMouse(d);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		boolean bl = i == 263;
		if (bl || i == 262) {
			float f = bl ? -1.0F : 1.0F;
			this.setValue(this.value + (double)(f / (float)(this.width - 8)));
		}

		return false;
	}

	private void setValueFromMouse(double d) {
		this.setValue((d - (double)(this.x + 4)) / (double)(this.width - 8));
	}

	private void setValue(double d) {
		double e = this.value;
		this.value = MathHelper.clamp(d, 0.0, 1.0);
		if (e != this.value) {
			this.applyValue();
		}

		this.updateMessage();
	}

	@Override
	protected void onDrag(double d, double e, double f, double g) {
		this.setValueFromMouse(d);
		super.onDrag(d, e, f, g);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	public void onRelease(double d, double e) {
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	protected abstract void updateMessage();

	protected abstract void applyValue();
}
