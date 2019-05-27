package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final Component message;
	private final boolean isHardcore;

	public DeathScreen(@Nullable Component component, boolean bl) {
		super(new TranslatableComponent(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = component;
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
							new TranslatableComponent("deathScreen.quit.confirm"),
							new TextComponent(""),
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

			this.minecraft.disconnect(new SaveLevelScreen(new TranslatableComponent("menu.savingLevel")));
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
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.message != null) {
			this.drawCenteredString(this.font, this.message.getFormattedText(), this.width / 2, 85, 16777215);
		}

		this.drawCenteredString(
			this.font, I18n.translate("deathScreen.score") + ": " + ChatFormat.field_1054 + this.minecraft.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.message != null && j > 85 && j < 85 + 9) {
			Component component = this.method_2164(i);
			if (component != null && component.getStyle().getHoverEvent() != null) {
				this.renderComponentHoverEffect(component, i, j);
			}
		}

		super.render(i, j, f);
	}

	@Nullable
	public Component method_2164(int i) {
		if (this.message == null) {
			return null;
		} else {
			int j = this.minecraft.textRenderer.getStringWidth(this.message.getFormattedText());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (Component component : this.message) {
					m += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.method_1849(component.getText(), false));
					if (m > i) {
						return component;
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
			Component component = this.method_2164((int)d);
			if (component != null && component.getStyle().getClickEvent() != null && component.getStyle().getClickEvent().getAction() == ClickEvent.Action.field_11749) {
				this.handleComponentClicked(component);
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
