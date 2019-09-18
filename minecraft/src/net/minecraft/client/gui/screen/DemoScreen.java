package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen {
	private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");

	public DemoScreen() {
		super(new TranslatableText("demo.help.title"));
	}

	@Override
	protected void init() {
		int i = -16;
		this.addButton(new ButtonWidget(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy"), buttonWidget -> {
			buttonWidget.active = false;
			SystemUtil.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later"), buttonWidget -> {
			this.minecraft.openScreen(null);
			this.minecraft.mouse.lockCursor();
		}));
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().method_22813(DEMO_BG);
		int i = (this.width - 248) / 2;
		int j = (this.height - 166) / 2;
		this.blit(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = (this.width - 248) / 2 + 10;
		int l = (this.height - 166) / 2 + 8;
		this.font.draw(this.title.asFormattedString(), (float)k, (float)l, 2039583);
		l += 12;
		GameOptions gameOptions = this.minecraft.options;
		this.font
			.draw(
				I18n.translate(
					"demo.help.movementShort",
					gameOptions.keyForward.getLocalizedName(),
					gameOptions.keyLeft.getLocalizedName(),
					gameOptions.keyBack.getLocalizedName(),
					gameOptions.keyRight.getLocalizedName()
				),
				(float)k,
				(float)l,
				5197647
			);
		this.font.draw(I18n.translate("demo.help.movementMouse"), (float)k, (float)(l + 12), 5197647);
		this.font.draw(I18n.translate("demo.help.jump", gameOptions.keyJump.getLocalizedName()), (float)k, (float)(l + 24), 5197647);
		this.font.draw(I18n.translate("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()), (float)k, (float)(l + 36), 5197647);
		this.font.drawStringBounded(I18n.translate("demo.help.fullWrapped"), k, l + 68, 218, 2039583);
		super.render(i, j, f);
	}
}
