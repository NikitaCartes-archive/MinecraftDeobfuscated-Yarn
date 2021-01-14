package net.minecraft.client.realms.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text field_26489 = new TranslatableText("mco.configure.world.invite.profile.name");
	private static final Text field_26490 = new TranslatableText("mco.configure.world.players.error");
	private TextFieldWidget field_22696;
	private final RealmsServer serverData;
	private final RealmsConfigureWorldScreen configureScreen;
	private final Screen parent;
	@Nullable
	private Text errorMessage;

	public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, Screen parent, RealmsServer serverData) {
		this.configureScreen = configureScreen;
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void tick() {
		this.field_22696.tick();
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.field_22696 = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, new TranslatableText("mco.configure.world.invite.profile.name")
		);
		this.addChild(this.field_22696);
		this.setInitialFocus(this.field_22696);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, row(10), 200, 20, new TranslatableText("mco.configure.world.buttons.invite"), buttonWidget -> this.onInvite())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	private void onInvite() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		if (this.field_22696.getText() != null && !this.field_22696.getText().isEmpty()) {
			try {
				RealmsServer realmsServer = realmsClient.invite(this.serverData.id, this.field_22696.getText().trim());
				if (realmsServer != null) {
					this.serverData.players = realmsServer.players;
					this.client.openScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
				} else {
					this.showError(field_26490);
				}
			} catch (Exception var3) {
				LOGGER.error("Couldn't invite user");
				this.showError(field_26490);
			}
		} else {
			this.showError(field_26490);
		}
	}

	private void showError(Text errorMessage) {
		this.errorMessage = errorMessage;
		Realms.narrateNow(errorMessage.getString());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.textRenderer.draw(matrices, field_26489, (float)(this.width / 2 - 100), (float)row(1), 10526880);
		if (this.errorMessage != null) {
			drawCenteredText(matrices, this.textRenderer, this.errorMessage, this.width / 2, row(5), 16711680);
		}

		this.field_22696.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
