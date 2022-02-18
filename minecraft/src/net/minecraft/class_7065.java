package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class class_7065 extends Screen {
	private final Text field_37218;
	private final Text field_37219;
	private final Text field_37220;
	private final Text field_37221;
	protected final Screen field_37216;
	@Nullable
	protected CheckboxWidget field_37217;
	private MultilineText field_37222 = MultilineText.EMPTY;

	protected class_7065(Text text, Text text2, Text text3, Text text4, Screen screen) {
		super(NarratorManager.EMPTY);
		this.field_37218 = text;
		this.field_37219 = text2;
		this.field_37220 = text3;
		this.field_37221 = text4;
		this.field_37216 = screen;
	}

	protected abstract void method_41160(int i);

	@Override
	protected void init() {
		super.init();
		this.field_37222 = MultilineText.create(this.textRenderer, this.field_37219, this.width - 50);
		int i = (this.field_37222.count() + 1) * 9 * 2;
		this.field_37217 = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.field_37220, false);
		this.addDrawableChild(this.field_37217);
		this.method_41160(i);
	}

	@Override
	public Text getNarratedTitle() {
		return this.field_37221;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawTextWithShadow(matrices, this.textRenderer, this.field_37218, 25, 30, 16777215);
		this.field_37222.drawWithShadow(matrices, 25, 70, 9 * 2, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
