package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private final TextComponent msg;

	public DeathScreen(@Nullable TextComponent textComponent) {
		this.msg = textComponent;
	}

	@Override
	protected void onInitialized() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.client.world.getLevelProperties().isHardcore()) {
			string = I18n.translate("deathScreen.spectate");
			string2 = I18n.translate("deathScreen." + (this.client.isInSingleplayer() ? "deleteWorld" : "leaveServer"));
		} else {
			string = I18n.translate("deathScreen.respawn");
			string2 = I18n.translate("deathScreen.titleScreen");
		}

		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, string) {
			@Override
			public void onPressed(double d, double e) {
				DeathScreen.this.client.player.requestRespawn();
				DeathScreen.this.client.openScreen(null);
			}
		});
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, string2) {
				@Override
				public void onPressed(double d, double e) {
					if (DeathScreen.this.client.world.getLevelProperties().isHardcore()) {
						DeathScreen.this.client.openScreen(new MainMenuScreen());
					} else {
						YesNoScreen yesNoScreen = new YesNoScreen(
							DeathScreen.this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
						);
						DeathScreen.this.client.openScreen(yesNoScreen);
						yesNoScreen.disableButtons(20);
					}
				}
			}
		);
		if (!this.client.world.getLevelProperties().isHardcore() && this.client.getSession() == null) {
			buttonWidget.enabled = false;
		}

		for (ButtonWidget buttonWidget2 : this.buttons) {
			buttonWidget2.enabled = false;
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
			if (this.client.world != null) {
				this.client.world.disconnect();
			}

			this.client.method_18096(new CloseWorldScreen(I18n.translate("menu.savingLevel")));
			this.client.openScreen(new MainMenuScreen());
		} else {
			this.client.player.requestRespawn();
			this.client.openScreen(null);
		}
	}

	@Override
	public void method_18326(int i, int j, float f) {
		boolean bl = this.client.world.getLevelProperties().isHardcore();
		this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.drawStringCentered(this.fontRenderer, I18n.translate(bl ? "deathScreen.title.hardcore" : "deathScreen.title"), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.msg != null) {
			this.drawStringCentered(this.fontRenderer, this.msg.getFormattedText(), this.width / 2, 85, 16777215);
		}

		this.drawStringCentered(
			this.fontRenderer, I18n.translate("deathScreen.score") + ": " + TextFormat.field_1054 + this.client.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.msg != null && j > 85 && j < 85 + 9) {
			TextComponent textComponent = this.method_2164(i);
			if (textComponent != null && textComponent.getStyle().getHoverEvent() != null) {
				this.drawTextComponentHover(textComponent, i, j);
			}
		}

		super.method_18326(i, j, f);
	}

	@Nullable
	public TextComponent method_2164(int i) {
		if (this.msg == null) {
			return null;
		} else {
			int j = this.client.textRenderer.getStringWidth(this.msg.getFormattedText());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (TextComponent textComponent : this.msg) {
					m += this.client.textRenderer.getStringWidth(TextComponentUtil.method_1849(textComponent.getText(), false));
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
		if (this.msg != null && e > 85.0 && e < (double)(85 + 9)) {
			TextComponent textComponent = this.method_2164((int)d);
			if (textComponent != null
				&& textComponent.getStyle().getClickEvent() != null
				&& textComponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleTextComponentClick(textComponent);
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
