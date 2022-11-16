package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
	private final RealmsLongConfirmationScreen.Type type;
	private final Text line2;
	private final Text line3;
	protected final BooleanConsumer callback;
	private final boolean yesNoQuestion;

	public RealmsLongConfirmationScreen(BooleanConsumer callback, RealmsLongConfirmationScreen.Type type, Text line2, Text line3, boolean yesNoQuestion) {
		super(NarratorManager.EMPTY);
		this.callback = callback;
		this.type = type;
		this.line2 = line2;
		this.line3 = line3;
		this.yesNoQuestion = yesNoQuestion;
	}

	@Override
	public void init() {
		if (this.yesNoQuestion) {
			this.addDrawableChild(ButtonWidget.builder(ScreenTexts.YES, button -> this.callback.accept(true)).dimensions(this.width / 2 - 105, row(8), 100, 20).build());
			this.addDrawableChild(ButtonWidget.builder(ScreenTexts.NO, button -> this.callback.accept(false)).dimensions(this.width / 2 + 5, row(8), 100, 20).build());
		} else {
			this.addDrawableChild(
				ButtonWidget.builder(Text.translatable("mco.gui.ok"), button -> this.callback.accept(true)).dimensions(this.width / 2 - 50, row(8), 100, 20).build()
			);
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinLines(this.type.text, this.line2, this.line3);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.callback.accept(false);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.type.text, this.width / 2, row(2), this.type.colorCode);
		drawCenteredText(matrices, this.textRenderer, this.line2, this.width / 2, row(4), 16777215);
		drawCenteredText(matrices, this.textRenderer, this.line3, this.width / 2, row(6), 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		WARNING("Warning!", 16711680),
		INFO("Info!", 8226750);

		public final int colorCode;
		public final Text text;

		private Type(String text, int colorCode) {
			this.text = Text.literal(text);
			this.colorCode = colorCode;
		}
	}
}
