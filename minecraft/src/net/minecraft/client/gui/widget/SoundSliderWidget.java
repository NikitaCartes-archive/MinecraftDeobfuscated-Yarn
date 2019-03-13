package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class SoundSliderWidget extends SliderWidget {
	private final SoundCategory field_2622;

	public SoundSliderWidget(MinecraftClient minecraftClient, int i, int j, SoundCategory soundCategory, int k) {
		super(minecraftClient.field_1690, i, j, k, 20, (double)minecraftClient.field_1690.method_1630(soundCategory));
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
		this.gameOptions.method_1624(this.field_2622, (float)this.progress);
		this.gameOptions.write();
	}
}
