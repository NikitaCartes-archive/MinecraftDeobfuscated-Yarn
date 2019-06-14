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
		super(minecraftClient.field_1690, i, j, k, 20, (double)minecraftClient.field_1690.getSoundVolume(soundCategory));
		this.category = soundCategory;
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		String string = (float)this.value == (float)this.getYImage(false) ? I18n.translate("options.off") : (int)((float)this.value * 100.0F) + "%";
		this.setMessage(I18n.translate("soundCategory." + this.category.getName()) + ": " + string);
	}

	@Override
	protected void applyValue() {
		this.options.setSoundVolume(this.category, (float)this.value);
		this.options.write();
	}
}
