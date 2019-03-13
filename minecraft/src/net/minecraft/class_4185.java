package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public abstract class class_4185 extends ButtonWidget {
	public class_4185(int i, int j, String string) {
		super(i, j, string);
	}

	public class_4185(int i, int j, int k, int l, String string) {
		super(i, j, k, l, string);
	}

	public abstract void method_1826();

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.enabled || !this.visible) {
			return false;
		} else if (i != 257 && i != 32 && i != 335) {
			return false;
		} else {
			this.method_1832(MinecraftClient.getInstance().method_1483());
			this.method_1826();
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.enabled && this.visible) {
			if (i == 0) {
				boolean bl = this.isSelected(d, e);
				if (bl) {
					this.method_1832(MinecraftClient.getInstance().method_1483());
					this.method_1826();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}
}
