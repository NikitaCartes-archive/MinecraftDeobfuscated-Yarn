package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Environment(EnvType.CLIENT)
public class RealmsEditBox extends RealmsGuiEventListener {
	private final TextFieldWidget editBox;

	public RealmsEditBox(int i, int j, int k, int l, int m) {
		this.editBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, j, k, l, m);
	}

	public String getValue() {
		return this.editBox.getText();
	}

	public void tick() {
		this.editBox.tick();
	}

	public void setValue(String string) {
		this.editBox.setText(string);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.editBox.charTyped(c, i);
	}

	@Override
	public GuiEventListener getProxy() {
		return this.editBox;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.editBox.keyPressed(i, j, k);
	}

	public boolean isFocused() {
		return this.editBox.isFocused();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.editBox.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.editBox.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.editBox.mouseDragged(d, e, i, f, g);
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.editBox.mouseScrolled(d);
	}

	public void render(int i, int j, float f) {
		this.editBox.method_18326(i, j, f);
	}

	public void setMaxLength(int i) {
		this.editBox.setMaxLength(i);
	}

	public void setIsEditable(boolean bl) {
		this.editBox.setIsEditable(bl);
	}
}
