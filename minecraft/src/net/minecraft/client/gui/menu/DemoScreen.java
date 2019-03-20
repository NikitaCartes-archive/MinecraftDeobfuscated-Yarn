package net.minecraft.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen {
	private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");

	@Override
	protected void onInitialized() {
		int i = -16;
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 116, this.screenHeight / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy")) {
			@Override
			public void onPressed() {
				this.active = false;
				SystemUtil.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, this.screenHeight / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later")) {
			@Override
			public void onPressed() {
				DemoScreen.this.client.openScreen(null);
				DemoScreen.this.client.mouse.lockCursor();
			}
		});
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(DEMO_BG);
		int i = (this.screenWidth - 248) / 2;
		int j = (this.screenHeight - 166) / 2;
		this.drawTexturedRect(i, j, 0, 0, 248, 166);
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		int k = (this.screenWidth - 248) / 2 + 10;
		int l = (this.screenHeight - 166) / 2 + 8;
		this.fontRenderer.draw(I18n.translate("demo.help.title"), (float)k, (float)l, 2039583);
		l += 12;
		GameOptions gameOptions = this.client.options;
		this.fontRenderer
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
		this.fontRenderer.draw(I18n.translate("demo.help.movementMouse"), (float)k, (float)(l + 12), 5197647);
		this.fontRenderer.draw(I18n.translate("demo.help.jump", gameOptions.keyJump.getLocalizedName()), (float)k, (float)(l + 24), 5197647);
		this.fontRenderer.draw(I18n.translate("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()), (float)k, (float)(l + 36), 5197647);
		this.fontRenderer.drawStringBounded(I18n.translate("demo.help.fullWrapped"), k, l + 68, 218, 2039583);
		super.render(i, j, f);
	}
}
