package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class class_444 extends SliderWidget {
	private final SoundCategory field_2622;

	public class_444(MinecraftClient minecraftClient, int i, int j, SoundCategory soundCategory, int k) {
		super(minecraftClient.options, i, j, k, 20, minecraftClient.options.getSoundVolume(soundCategory));
		this.field_2622 = soundCategory;
		this.updateText();
	}

	@Override
	protected void updateText() {
		String string = (float)this.progress == (float)this.getTextureId(false) ? I18n.translate("options.off") : (int)((float)this.progress * 100.0F) + "%";
		this.setText(I18n.translate("soundCategory." + this.field_2622.getName()) + ": " + string);
	}

	@Override
	protected void onProgressChanged() {
		this.field_18211.setSoundVolume(this.field_2622, (float)this.progress);
		this.field_18211.write();
	}
}
