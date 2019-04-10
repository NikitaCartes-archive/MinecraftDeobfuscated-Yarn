package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final TextComponent message;
	private final boolean field_18974;

	public DeathScreen(@Nullable TextComponent textComponent, boolean bl) {
		super(new TranslatableTextComponent(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = textComponent;
		this.field_18974 = bl;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.field_18974) {
			string = I18n.translate("deathScreen.spectate");
			string2 = I18n.translate("deathScreen." + (this.minecraft.isInSingleplayer() ? "deleteWorld" : "leaveServer"));
		} else {
			string = I18n.translate("deathScreen.respawn");
			string2 = I18n.translate("deathScreen.titleScreen");
		}

		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, string, buttonWidgetx -> {
			this.minecraft.player.requestRespawn();
			this.minecraft.openScreen(null);
		}));
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 96,
				200,
				20,
				string2,
				buttonWidgetx -> {
					if (this.field_18974) {
						this.minecraft.openScreen(new MainMenuScreen());
					} else {
						YesNoScreen yesNoScreen = new YesNoScreen(
							this::method_20373,
							new TranslatableTextComponent("deathScreen.quit.confirm"),
							new StringTextComponent(""),
							I18n.translate("deathScreen.titleScreen"),
							I18n.translate("deathScreen.respawn")
						);
						this.minecraft.openScreen(yesNoScreen);
						yesNoScreen.disableButtons(20);
					}
				}
			)
		);
		if (!this.field_18974 && this.minecraft.getSession() == null) {
			buttonWidget.active = false;
		}

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			abstractButtonWidget.active = false;
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void method_20373(boolean bl) {
		if (bl) {
			if (this.minecraft.world != null) {
				this.minecraft.world.disconnect();
			}

			this.minecraft.disconnect(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel")));
			this.minecraft.openScreen(new MainMenuScreen());
		} else {
			this.minecraft.player.requestRespawn();
			this.minecraft.openScreen(null);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.message != null) {
			this.drawCenteredString(this.font, this.message.getFormattedText(), this.width / 2, 85, 16777215);
		}

		this.drawCenteredString(
			this.font, I18n.translate("deathScreen.score") + ": " + TextFormat.field_1054 + this.minecraft.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.message != null && j > 85 && j < 85 + 9) {
			TextComponent textComponent = this.method_2164(i);
			if (textComponent != null && textComponent.getStyle().getHoverEvent() != null) {
				this.renderComponentHoverEffect(textComponent, i, j);
			}
		}

		super.render(i, j, f);
	}

	@Nullable
	public TextComponent method_2164(int i) {
		if (this.message == null) {
			return null;
		} else {
			int j = this.minecraft.textRenderer.getStringWidth(this.message.getFormattedText());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (TextComponent textComponent : this.message) {
					m += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.method_1849(textComponent.getText(), false));
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
		if (this.message != null && e > 85.0 && e < (double)(85 + 9)) {
			TextComponent textComponent = this.method_2164((int)d);
			if (textComponent != null
				&& textComponent.getStyle().getClickEvent() != null
				&& textComponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleComponentClicked(textComponent);
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
	public void tick() {
		super.tick();
		this.ticksSinceDeath++;
		if (this.ticksSinceDeath == 20) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.active = true;
			}
		}
	}
}
