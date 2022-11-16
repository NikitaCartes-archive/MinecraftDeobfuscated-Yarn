package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
	private static final Text INCOMPATIBLE_TITLE = Text.translatable("mco.client.incompatible.title");
	private static final Text[] INCOMPATIBLE_LINES_UNSTABLE = new Text[]{
		Text.translatable("mco.client.incompatible.msg.line1"),
		Text.translatable("mco.client.incompatible.msg.line2"),
		Text.translatable("mco.client.incompatible.msg.line3")
	};
	private static final Text[] INCOMPATIBLE_LINES = new Text[]{
		Text.translatable("mco.client.incompatible.msg.line1"), Text.translatable("mco.client.incompatible.msg.line2")
	};
	private final Screen parent;

	public RealmsClientOutdatedScreen(Screen parent) {
		super(INCOMPATIBLE_TITLE);
		this.parent = parent;
	}

	@Override
	public void init() {
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, row(12), 200, 20).build()
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, row(3), 16711680);
		Text[] texts = this.getLines();

		for (int i = 0; i < texts.length; i++) {
			drawCenteredText(matrices, this.textRenderer, texts[i], this.width / 2, row(5) + i * 12, 16777215);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private Text[] getLines() {
		return this.client.getGame().getVersion().isStable() ? INCOMPATIBLE_LINES : INCOMPATIBLE_LINES_UNSTABLE;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER && keyCode != GLFW.GLFW_KEY_ESCAPE) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			this.client.setScreen(this.parent);
			return true;
		}
	}
}
