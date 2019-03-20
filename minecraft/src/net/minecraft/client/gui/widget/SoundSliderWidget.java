package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class SoundSliderWidget extends SliderWidget {
	private final SoundCategory category;

	public SoundSliderWidget(MinecraftClient minecraftClient, int i, int j, SoundCategory soundCategory, int k) {
		super(minecraftClient.options, i, j, k, 20, (double)minecraftClient.options.getSoundVolume(soundCategory));
		this.category = soundCategory;
		this.updateText();
	}

	@Override
	protected void updateText() {
		String string = (float)this.progress == (float)this.getYImage(false) ? I18n.translate("options.off") : (int)((float)this.progress * 100.0F) + "%";
		this.setMessage(I18n.translate("soundCategory." + this.category.getName()) + ": " + string);
	}

	@Override
	protected void onProgressChanged() {
		this.gameOptions.setSoundVolume(this.category, (float)this.progress);
		this.gameOptions.write();
	}
}
