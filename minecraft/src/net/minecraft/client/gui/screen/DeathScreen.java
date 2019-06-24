package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final Text message;
	private final boolean isHardcore;

	public DeathScreen(@Nullable Text text, boolean bl) {
		super(new TranslatableText(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = text;
		this.isHardcore = bl;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.isHardcore) {
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
					if (this.isHardcore) {
						this.minecraft.openScreen(new TitleScreen());
					} else {
						ConfirmScreen confirmScreen = new ConfirmScreen(
							this::method_20373,
							new TranslatableText("deathScreen.quit.confirm"),
							new LiteralText(""),
							I18n.translate("deathScreen.titleScreen"),
							I18n.translate("deathScreen.respawn")
						);
						this.minecraft.openScreen(confirmScreen);
						confirmScreen.disableButtons(20);
					}
				}
			)
		);
		if (!this.isHardcore && this.minecraft.getSession() == null) {
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

			this.minecraft.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
			this.minecraft.openScreen(new TitleScreen());
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
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.message != null) {
			this.drawCenteredString(this.font, this.message.asFormattedString(), this.width / 2, 85, 16777215);
		}

		this.drawCenteredString(
			this.font, I18n.translate("deathScreen.score") + ": " + Formatting.YELLOW + this.minecraft.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.message != null && j > 85 && j < 85 + 9) {
			Text text = this.method_2164(i);
			if (text != null && text.getStyle().getHoverEvent() != null) {
				this.renderComponentHoverEffect(text, i, j);
			}
		}

		super.render(i, j, f);
	}

	@Nullable
	public Text method_2164(int i) {
		if (this.message == null) {
			return null;
		} else {
			int j = this.minecraft.textRenderer.getStringWidth(this.message.asFormattedString());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (Text text : this.message) {
					m += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.getRenderChatMessage(text.asString(), false));
					if (m > i) {
						return text;
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
			Text text = this.method_2164((int)d);
			if (text != null && text.getStyle().getClickEvent() != null && text.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleComponentClicked(text);
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
