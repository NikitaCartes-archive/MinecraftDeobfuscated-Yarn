package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderableGlyph {
	int getWidth();

	int getHeight();

	void upload(int x, int y);

	boolean hasColor();

	float getOversample();

	default float getXMin() {
		return this.method_56129();
	}

	default float getXMax() {
		return this.getXMin() + (float)this.getWidth() / this.getOversample();
	}

	default float getYMin() {
		return 7.0F - this.method_56130();
	}

	default float getYMax() {
		return this.getYMin() + (float)this.getHeight() / this.getOversample();
	}

	default float method_56129() {
		return 0.0F;
	}

	default float method_56130() {
		return 7.0F;
	}
}
