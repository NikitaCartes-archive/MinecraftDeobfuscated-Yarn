package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class class_444 extends SliderWidget {
	private final SoundCategory field_2622;

	public class_444(MinecraftClient minecraftClient, int i, int j, int k, SoundCategory soundCategory, int l) {
		super(i, j, k, l, 20, minecraftClient, minecraftClient.options.getSoundVolume(soundCategory));
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
		GameOptions gameOptions = this.client.options;
		gameOptions.setSoundVolume(this.field_2622, (float)this.progress);
		gameOptions.write();
	}
}
