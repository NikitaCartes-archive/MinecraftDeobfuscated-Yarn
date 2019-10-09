package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_4667 extends Screen {
	protected final Screen field_21335;
	protected final GameOptions field_21336;

	public class_4667(Screen screen, GameOptions gameOptions, Text text) {
		super(text);
		this.field_21335 = screen;
		this.field_21336 = gameOptions;
	}

	@Override
	public void removed() {
		this.minecraft.options.write();
	}

	@Override
	public void onClose() {
		this.minecraft.openScreen(this.field_21335);
	}
}
