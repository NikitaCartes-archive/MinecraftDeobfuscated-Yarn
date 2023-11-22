package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TexturedButtonWidget extends ButtonWidget {
	protected final ButtonTextures textures;

	public TexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, ButtonWidget.PressAction pressAction) {
		this(x, y, width, height, textures, pressAction, ScreenTexts.EMPTY);
	}

	public TexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, ButtonWidget.PressAction pressAction, Text text) {
		super(x, y, width, height, text, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.textures = textures;
	}

	public TexturedButtonWidget(int width, int height, ButtonTextures textures, ButtonWidget.PressAction pressAction, Text text) {
		this(0, 0, width, height, textures, pressAction, text);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected());
		context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
	}
}
