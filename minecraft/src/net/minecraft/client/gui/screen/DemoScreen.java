package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen {
	private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");

	public DemoScreen() {
		super(new TranslatableText("demo.help.title"));
	}

	@Override
	protected void init() {
		int i = -16;
		this.addButton(new ButtonWidget(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, new TranslatableText("demo.help.buy"), buttonWidget -> {
			buttonWidget.active = false;
			Util.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, new TranslatableText("demo.help.later"), buttonWidget -> {
			this.client.openScreen(null);
			this.client.mouse.lockCursor();
		}));
	}

	@Override
	public void renderBackground(MatrixStack matrixStack) {
		super.renderBackground(matrixStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(DEMO_BG);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.drawTexture(matrixStack, i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int i = (this.width - 248) / 2 + 10;
		int j = (this.height - 166) / 2 + 8;
		this.textRenderer.draw(matrices, this.title, (float)i, (float)j, 2039583);
		j += 12;
		GameOptions gameOptions = this.client.options;
		this.textRenderer
			.draw(
				matrices,
				I18n.translate(
					"demo.help.movementShort",
					gameOptions.keyForward.getLocalizedName(),
					gameOptions.keyLeft.getLocalizedName(),
					gameOptions.keyBack.getLocalizedName(),
					gameOptions.keyRight.getLocalizedName()
				),
				(float)i,
				(float)j,
				5197647
			);
		this.textRenderer.draw(matrices, I18n.translate("demo.help.movementMouse"), (float)i, (float)(j + 12), 5197647);
		this.textRenderer.draw(matrices, I18n.translate("demo.help.jump", gameOptions.keyJump.getLocalizedName()), (float)i, (float)(j + 24), 5197647);
		this.textRenderer.draw(matrices, I18n.translate("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()), (float)i, (float)(j + 36), 5197647);
		this.textRenderer.drawTrimmed(new TranslatableText("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
