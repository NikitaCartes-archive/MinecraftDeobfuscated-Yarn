package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsClientIncompatibleScreen extends RealmsScreen {
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

	public RealmsClientIncompatibleScreen(Screen parent) {
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
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, row(3), -65536);
		Text[] texts = this.getLines();

		for (int i = 0; i < texts.length; i++) {
			context.drawCenteredTextWithShadow(this.textRenderer, texts[i], this.width / 2, row(5) + i * 12, -1);
		}
	}

	private Text[] getLines() {
		return SharedConstants.getGameVersion().isStable() ? INCOMPATIBLE_LINES : INCOMPATIBLE_LINES_UNSTABLE;
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
