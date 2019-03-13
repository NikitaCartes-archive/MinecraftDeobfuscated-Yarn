package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.ClickEvent;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final TextComponent field_2450;

	public DeathScreen(@Nullable TextComponent textComponent) {
		this.field_2450 = textComponent;
	}

	@Override
	protected void onInitialized() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.client.field_1687.method_8401().isHardcore()) {
			string = I18n.translate("deathScreen.spectate");
			string2 = I18n.translate("deathScreen." + (this.client.isInSingleplayer() ? "deleteWorld" : "leaveServer"));
		} else {
			string = I18n.translate("deathScreen.respawn");
			string2 = I18n.translate("deathScreen.titleScreen");
		}

		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 72, string) {
			@Override
			public void method_1826() {
				DeathScreen.this.client.field_1724.requestRespawn();
				DeathScreen.this.client.method_1507(null);
			}
		});
		class_4185 lv = this.addButton(
			new class_4185(this.screenWidth / 2 - 100, this.screenHeight / 4 + 96, string2) {
				@Override
				public void method_1826() {
					if (DeathScreen.this.client.field_1687.method_8401().isHardcore()) {
						DeathScreen.this.client.method_1507(new MainMenuScreen());
					} else {
						YesNoScreen yesNoScreen = new YesNoScreen(
							DeathScreen.this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
						);
						DeathScreen.this.client.method_1507(yesNoScreen);
						yesNoScreen.disableButtons(20);
					}
				}
			}
		);
		if (!this.client.field_1687.method_8401().isHardcore() && this.client.method_1548() == null) {
			lv.enabled = false;
		}

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.enabled = false;
		}
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			super.confirmResult(bl, i);
		} else if (bl) {
			if (this.client.field_1687 != null) {
				this.client.field_1687.disconnect();
			}

			this.client.method_18096(new CloseWorldScreen(I18n.translate("menu.savingLevel")));
			this.client.method_1507(new MainMenuScreen());
		} else {
			this.client.field_1724.requestRespawn();
			this.client.method_1507(null);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		boolean bl = this.client.field_1687.method_8401().isHardcore();
		this.drawGradientRect(0, 0, this.screenWidth, this.screenHeight, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.drawStringCentered(this.fontRenderer, I18n.translate(bl ? "deathScreen.title.hardcore" : "deathScreen.title"), this.screenWidth / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.field_2450 != null) {
			this.drawStringCentered(this.fontRenderer, this.field_2450.getFormattedText(), this.screenWidth / 2, 85, 16777215);
		}

		this.drawStringCentered(
			this.fontRenderer,
			I18n.translate("deathScreen.score") + ": " + TextFormat.field_1054 + this.client.field_1724.getScore(),
			this.screenWidth / 2,
			100,
			16777215
		);
		if (this.field_2450 != null && j > 85 && j < 85 + 9) {
			TextComponent textComponent = this.method_2164(i);
			if (textComponent != null && textComponent.method_10866().getHoverEvent() != null) {
				this.method_2229(textComponent, i, j);
			}
		}

		super.draw(i, j, f);
	}

	@Nullable
	public TextComponent method_2164(int i) {
		if (this.field_2450 == null) {
			return null;
		} else {
			int j = this.client.field_1772.getStringWidth(this.field_2450.getFormattedText());
			int k = this.screenWidth / 2 - j / 2;
			int l = this.screenWidth / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (TextComponent textComponent : this.field_2450) {
					m += this.client.field_1772.getStringWidth(TextComponentUtil.method_1849(textComponent.getText(), false));
					if (m > i) {
						return textComponent;
					}
				}

				return null;
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2450 != null && e > 85.0 && e < (double)(85 + 9)) {
			TextComponent textComponent = this.method_2164((int)d);
			if (textComponent != null
				&& textComponent.method_10866().getClickEvent() != null
				&& textComponent.method_10866().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.method_2216(textComponent);
				return false;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void update() {
		super.update();
		this.ticksSinceDeath++;
		if (this.ticksSinceDeath == 20) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.enabled = true;
			}
		}
	}
}
