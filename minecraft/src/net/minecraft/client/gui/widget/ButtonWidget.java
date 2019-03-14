package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class ButtonWidget extends AbstractButtonWidget {
	public ButtonWidget(int i, int j, String string) {
		super(i, j, string);
	}

	public ButtonWidget(int i, int j, int k, int l, String string) {
		super(i, j, k, l, string);
	}

	public abstract void onPressed();

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.enabled || !this.visible) {
			return false;
		} else if (i != 257 && i != 32 && i != 335) {
			return false;
		} else {
			this.playPressedSound(MinecraftClient.getInstance().getSoundLoader());
			this.onPressed();
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.enabled && this.visible) {
			if (i == 0) {
				boolean bl = this.isSelected(d, e);
				if (bl) {
					this.playPressedSound(MinecraftClient.getInstance().getSoundLoader());
					this.onPressed();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}
}
