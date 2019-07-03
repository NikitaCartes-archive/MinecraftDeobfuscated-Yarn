package net.minecraft;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4449 {
	private int[] field_20263;
	private int field_20264;
	private int field_20265;

	@Nullable
	public BufferedImage method_21573(BufferedImage bufferedImage) {
		if (bufferedImage == null) {
			return null;
		} else {
			this.field_20264 = 64;
			this.field_20265 = 64;
			BufferedImage bufferedImage2 = new BufferedImage(this.field_20264, this.field_20265, 2);
			Graphics graphics = bufferedImage2.getGraphics();
			graphics.drawImage(bufferedImage, 0, 0, null);
			boolean bl = bufferedImage.getHeight() == 32;
			if (bl) {
				graphics.setColor(new Color(0, 0, 0, 0));
				graphics.fillRect(0, 32, 64, 32);
				graphics.drawImage(bufferedImage2, 24, 48, 20, 52, 4, 16, 8, 20, null);
				graphics.drawImage(bufferedImage2, 28, 48, 24, 52, 8, 16, 12, 20, null);
				graphics.drawImage(bufferedImage2, 20, 52, 16, 64, 8, 20, 12, 32, null);
				graphics.drawImage(bufferedImage2, 24, 52, 20, 64, 4, 20, 8, 32, null);
				graphics.drawImage(bufferedImage2, 28, 52, 24, 64, 0, 20, 4, 32, null);
				graphics.drawImage(bufferedImage2, 32, 52, 28, 64, 12, 20, 16, 32, null);
				graphics.drawImage(bufferedImage2, 40, 48, 36, 52, 44, 16, 48, 20, null);
				graphics.drawImage(bufferedImage2, 44, 48, 40, 52, 48, 16, 52, 20, null);
				graphics.drawImage(bufferedImage2, 36, 52, 32, 64, 48, 20, 52, 32, null);
				graphics.drawImage(bufferedImage2, 40, 52, 36, 64, 44, 20, 48, 32, null);
				graphics.drawImage(bufferedImage2, 44, 52, 40, 64, 40, 20, 44, 32, null);
				graphics.drawImage(bufferedImage2, 48, 52, 44, 64, 52, 20, 56, 32, null);
			}

			graphics.dispose();
			this.field_20263 = ((DataBufferInt)bufferedImage2.getRaster().getDataBuffer()).getData();
			this.method_21574(0, 0, 32, 16);
			if (bl) {
				this.method_21572(32, 0, 64, 32);
			}

			this.method_21574(0, 16, 64, 32);
			this.method_21574(16, 48, 48, 64);
			return bufferedImage2;
		}
	}

	private void method_21572(int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				int o = this.field_20263[m + n * this.field_20264];
				if ((o >> 24 & 0xFF) < 128) {
					return;
				}
			}
		}

		for (int m = i; m < k; m++) {
			for (int nx = j; nx < l; nx++) {
				this.field_20263[m + nx * this.field_20264] = this.field_20263[m + nx * this.field_20264] & 16777215;
			}
		}
	}

	private void method_21574(int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				this.field_20263[m + n * this.field_20264] = this.field_20263[m + n * this.field_20264] | 0xFF000000;
			}
		}
	}
}
