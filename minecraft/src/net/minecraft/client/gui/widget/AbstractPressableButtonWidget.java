package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class AbstractPressableButtonWidget extends AbstractButtonWidget {
	public AbstractPressableButtonWidget(int i, int j, int k, int l, String string) {
		super(i, j, k, l, string);
	}

	public abstract void onPress();

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.onPress();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.active || !this.visible) {
			return false;
		} else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
			return false;
		} else {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			this.onPress();
			return true;
		}
	}
}
