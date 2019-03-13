package net.minecraft.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen {
	private static final Identifier field_2447 = new Identifier("textures/gui/demo_background.png");

	@Override
	protected void onInitialized() {
		int i = -16;
		this.addButton(new class_4185(this.screenWidth / 2 - 116, this.screenHeight / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.buy")) {
			@Override
			public void method_1826() {
				this.enabled = false;
				SystemUtil.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 2, this.screenHeight / 2 + 62 + -16, 114, 20, I18n.translate("demo.help.later")) {
			@Override
			public void method_1826() {
				DemoScreen.this.client.method_1507(null);
				DemoScreen.this.client.field_1729.lockCursor();
			}
		});
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(field_2447);
		int i = (this.screenWidth - 248) / 2;
		int j = (this.screenHeight - 166) / 2;
		this.drawTexturedRect(i, j, 0, 0, 248, 166);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		int k = (this.screenWidth - 248) / 2 + 10;
		int l = (this.screenHeight - 166) / 2 + 8;
		this.fontRenderer.draw(I18n.translate("demo.help.title"), (float)k, (float)l, 2039583);
		l += 12;
		GameOptions gameOptions = this.client.field_1690;
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
		super.draw(i, j, f);
	}
}
