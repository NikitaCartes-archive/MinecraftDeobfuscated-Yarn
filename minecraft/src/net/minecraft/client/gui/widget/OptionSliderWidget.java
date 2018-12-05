package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OptionSliderWidget extends ButtonWidget {
	private double field_2161 = 1.0;
	public boolean dragging;
	private final GameOptions.Option option;
	private final double field_2160;
	private final double field_2159;

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option) {
		this(i, j, k, option, 0.0, 1.0);
	}

	public OptionSliderWidget(int i, int j, int k, GameOptions.Option option, double d, double e) {
		this(i, j, k, 150, 20, option, d, e);
	}

	public OptionSliderWidget(int i, int j, int k, int l, int m, GameOptions.Option option, double d, double e) {
		super(i, j, k, l, m, "");
		this.option = option;
		this.field_2160 = d;
		this.field_2159 = e;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.field_2161 = option.method_1651(minecraftClient.options.method_1637(option));
		this.text = minecraftClient.options.getTranslatedName(option);
	}

	@Override
	protected int getTextureId(boolean bl) {
		return 0;
	}

	@Override
	protected void drawBackground(MinecraftClient minecraftClient, int i, int j) {
		if (this.visible) {
			if (this.dragging) {
				this.field_2161 = (double)((float)(i - (this.x + 4)) / (float)(this.width - 8));
				this.field_2161 = MathHelper.clamp(this.field_2161, 0.0, 1.0);
			}

			if (this.dragging || this.option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
				double d = this.option.method_1645(this.field_2161);
				minecraftClient.options.method_1625(this.option, d);
				this.field_2161 = this.option.method_1651(d);
				this.text = minecraftClient.options.getTranslatedName(this.option);
			}

			minecraftClient.getTextureManager().bindTexture(WIDGET_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedRect(this.x + (int)(this.field_2161 * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexturedRect(this.x + (int)(this.field_2161 * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	@Override
	public final void onPressed(double d, double e) {
		this.field_2161 = (d - (double)(this.x + 4)) / (double)(this.width - 8);
		this.field_2161 = MathHelper.clamp(this.field_2161, 0.0, 1.0);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.options.method_1625(this.option, this.option.method_1645(this.field_2161));
		this.text = minecraftClient.options.getTranslatedName(this.option);
		this.dragging = true;
	}

	@Override
	public void onReleased(double d, double e) {
		this.dragging = false;
	}
}
