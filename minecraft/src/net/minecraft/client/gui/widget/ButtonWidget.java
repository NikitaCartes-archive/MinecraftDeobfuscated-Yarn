package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractButtonWidget {
	protected final ButtonWidget.class_4241 onPress;

	public ButtonWidget(int i, int j, int k, int l, String string, ButtonWidget.class_4241 arg) {
		super(i, j, k, l, string);
		this.onPress = arg;
	}

	public void onPressed() {
		this.onPress.onPress(this);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.active || !this.visible) {
			return false;
		} else if (i != 257 && i != 32 && i != 335) {
			return false;
		} else {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			this.onPressed();
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.active && this.visible) {
			if (i == 0) {
				boolean bl = this.clicked(d, e);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onPressed();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_4241 {
		void onPress(ButtonWidget buttonWidget);
	}
}
