package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_341;
import net.minecraft.class_424;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.MainMenuGui;
import net.minecraft.client.gui.menu.YesNoGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.ClickEvent;

@Environment(EnvType.CLIENT)
public class DeathGui extends Gui {
	private int ticksSinceDeath;
	private final TextComponent msg;

	public DeathGui(@Nullable TextComponent textComponent) {
		this.msg = textComponent;
	}

	@Override
	protected void onInitialized() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.client.world.getLevelProperties().isHardcore()) {
			string = I18n.translate("deathScreen.spectate");
			string2 = I18n.translate("deathScreen." + (this.client.method_1542() ? "deleteWorld" : "leaveServer"));
		} else {
			string = I18n.translate("deathScreen.respawn");
			string2 = I18n.translate("deathScreen.titleScreen");
		}

		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 72, string) {
			@Override
			public void onPressed(double d, double e) {
				DeathGui.this.client.player.method_7331();
				DeathGui.this.client.openGui(null);
			}
		});
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 96, string2) {
				@Override
				public void onPressed(double d, double e) {
					if (DeathGui.this.client.world.getLevelProperties().isHardcore()) {
						DeathGui.this.client.openGui(new MainMenuGui());
					} else {
						YesNoGui yesNoGui = new YesNoGui(
							DeathGui.this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
						);
						DeathGui.this.client.openGui(yesNoGui);
						yesNoGui.disableButtons(20);
					}
				}
			}
		);
		if (!this.client.world.getLevelProperties().isHardcore() && this.client.getSession() == null) {
			buttonWidget.enabled = false;
		}

		for (ButtonWidget buttonWidget2 : this.buttonWidgets) {
			buttonWidget2.enabled = false;
		}
	}

	@Override
	public boolean canClose() {
		return false;
	}

	@Override
	public void handle(boolean bl, int i) {
		if (i == 31102009) {
			super.handle(bl, i);
		} else if (bl) {
			if (this.client.world != null) {
				this.client.world.method_8525();
			}

			this.client.method_1550(null, new class_424(I18n.translate("menu.savingLevel")));
			this.client.openGui(new MainMenuGui());
		} else {
			this.client.player.method_7331();
			this.client.openGui(null);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
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
			this.fontRenderer, I18n.translate("deathScreen.score") + ": " + TextFormat.YELLOW + this.client.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.msg != null && j > 85 && j < 85 + this.fontRenderer.FONT_HEIGHT) {
			TextComponent textComponent = this.method_2164(i);
			if (textComponent != null && textComponent.getStyle().getHoverEvent() != null) {
				this.drawTextComponentHover(textComponent, i, j);
			}
		}

		super.draw(i, j, f);
	}

	@Nullable
	public TextComponent method_2164(int i) {
		if (this.msg == null) {
			return null;
		} else {
			int j = this.client.fontRenderer.getStringWidth(this.msg.getFormattedText());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (TextComponent textComponent : this.msg) {
					m += this.client.fontRenderer.getStringWidth(class_341.method_1849(textComponent.getText(), false));
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
		if (this.msg != null && e > 85.0 && e < (double)(85 + this.fontRenderer.FONT_HEIGHT)) {
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
			for (ButtonWidget buttonWidget : this.buttonWidgets) {
				buttonWidget.enabled = true;
			}
		}
	}
}
