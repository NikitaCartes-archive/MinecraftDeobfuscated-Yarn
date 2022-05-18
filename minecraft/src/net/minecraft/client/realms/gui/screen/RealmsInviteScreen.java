package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text INVITE_PROFILE_NAME_TEXT = Text.translatable("mco.configure.world.invite.profile.name");
	private static final Text PLAYER_ERROR_TEXT = Text.translatable("mco.configure.world.players.error");
	private TextFieldWidget nameWidget;
	private final RealmsServer serverData;
	private final RealmsConfigureWorldScreen configureScreen;
	private final Screen parent;
	@Nullable
	private Text errorMessage;

	public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, Screen parent, RealmsServer serverData) {
		super(NarratorManager.EMPTY);
		this.configureScreen = configureScreen;
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void tick() {
		this.nameWidget.tick();
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.nameWidget = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, Text.translatable("mco.configure.world.invite.profile.name")
		);
		this.addSelectableChild(this.nameWidget);
		this.setInitialFocus(this.nameWidget);
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, row(10), 200, 20, Text.translatable("mco.configure.world.buttons.invite"), button -> this.onInvite())
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	private void onInvite() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		if (this.nameWidget.getText() != null && !this.nameWidget.getText().isEmpty()) {
			try {
				RealmsServer realmsServer = realmsClient.invite(this.serverData.id, this.nameWidget.getText().trim());
				if (realmsServer != null) {
					this.serverData.players = realmsServer.players;
					this.client.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
				} else {
					this.showError(PLAYER_ERROR_TEXT);
				}
			} catch (Exception var3) {
				LOGGER.error("Couldn't invite user");
				this.showError(PLAYER_ERROR_TEXT);
			}
		} else {
			this.showError(PLAYER_ERROR_TEXT);
		}
	}

	private void showError(Text errorMessage) {
		this.errorMessage = errorMessage;
		NarratorManager.INSTANCE.narrate(errorMessage);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.textRenderer.draw(matrices, INVITE_PROFILE_NAME_TEXT, (float)(this.width / 2 - 100), (float)row(1), 10526880);
		if (this.errorMessage != null) {
			drawCenteredText(matrices, this.textRenderer, this.errorMessage, this.width / 2, row(5), 16711680);
		}

		this.nameWidget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
