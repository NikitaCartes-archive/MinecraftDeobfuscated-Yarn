package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
	private final RealmsLongConfirmationScreen.Type type;
	private final Text line2;
	private final Text line3;
	protected final BooleanConsumer field_22697;
	private final boolean yesNoQuestion;

	public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, RealmsLongConfirmationScreen.Type type, Text line2, Text line3, boolean yesNoQuestion) {
		super(NarratorManager.EMPTY);
		this.field_22697 = booleanConsumer;
		this.type = type;
		this.line2 = line2;
		this.line3 = line3;
		this.yesNoQuestion = yesNoQuestion;
	}

	@Override
	public void init() {
		if (this.yesNoQuestion) {
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 105, row(8), 100, 20, ScreenTexts.YES, button -> this.field_22697.accept(true)));
			this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, row(8), 100, 20, ScreenTexts.NO, button -> this.field_22697.accept(false)));
		} else {
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, row(8), 100, 20, new TranslatableText("mco.gui.ok"), button -> this.field_22697.accept(true)));
		}
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinLines(this.type.text, this.line2, this.line3);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.field_22697.accept(false);
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
		Warning("Warning!", 16711680),
		Info("Info!", 8226750);

		public final int colorCode;
		public final Text text;

		private Type(String text, int colorCode) {
			this.text = new LiteralText(text);
			this.colorCode = colorCode;
		}
	}
}
