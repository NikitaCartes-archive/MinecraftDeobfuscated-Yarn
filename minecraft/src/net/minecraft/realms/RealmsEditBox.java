package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_364;

@Environment(EnvType.CLIENT)
public class RealmsEditBox extends RealmsGuiEventListener {
	private final class_342 editBox;

	public RealmsEditBox(int i, int j, int k, int l, int m) {
		this.editBox = new class_342(i, class_310.method_1551().field_1772, j, k, l, m);
	}

	public String getValue() {
		return this.editBox.method_1882();
	}

	public void tick() {
		this.editBox.method_1865();
	}

	public void setValue(String string) {
		this.editBox.method_1852(string);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.editBox.method_16806(c, i);
	}

	@Override
	public class_364 getProxy() {
		return this.editBox;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.editBox.method_16805(i, j, k);
	}

	public boolean isFocused() {
		return this.editBox.method_1871();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.editBox.method_16807(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.editBox.method_16804(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.editBox.method_16801(d, e, i, f, g);
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.editBox.method_16802(d);
	}

	public void render(int i, int j, float f) {
		this.editBox.method_1857(i, j, f);
	}

	public void setMaxLength(int i) {
		this.editBox.method_1880(i);
	}

	public void setIsEditable(boolean bl) {
		this.editBox.method_1888(bl);
	}
}
