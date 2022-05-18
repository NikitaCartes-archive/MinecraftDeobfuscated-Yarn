package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SoundSliderWidget extends OptionSliderWidget {
	private final SoundCategory category;

	public SoundSliderWidget(MinecraftClient client, int x, int y, SoundCategory category, int width) {
		super(client.options, x, y, width, 20, (double)client.options.getSoundVolume(category));
		this.category = category;
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		Text text = (Text)((float)this.value == (float)this.getYImage(false) ? ScreenTexts.OFF : Text.literal((int)(this.value * 100.0) + "%"));
		this.setMessage(Text.translatable("soundCategory." + this.category.getName()).append(": ").append(text));
	}

	@Override
	protected void applyValue() {
		this.options.setSoundVolume(this.category, (float)this.value);
		this.options.write();
	}
}
