package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text INVITE_PROFILE_NAME_TEXT = Text.translatable("mco.configure.world.invite.profile.name").styled(style -> style.withColor(-6250336));
	private static final Text INVITING_TEXT = Text.translatable("mco.configure.world.players.inviting").styled(style -> style.withColor(-6250336));
	private static final Text PLAYER_ERROR_TEXT = Text.translatable("mco.configure.world.players.error").styled(style -> style.withColor(-65536));
	private TextFieldWidget nameWidget;
	private ButtonWidget inviteButton;
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
		this.nameWidget = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, row(2), 200, 20, null, Text.translatable("mco.configure.world.invite.profile.name")
		);
		this.addSelectableChild(this.nameWidget);
		this.setInitialFocus(this.nameWidget);
		this.inviteButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.invite"), button -> this.onInvite())
				.dimensions(this.width / 2 - 100, row(10), 200, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, row(12), 200, 20).build()
		);
	}

	private void onInvite() {
		if (Util.isBlank(this.nameWidget.getText())) {
			this.showError(PLAYER_ERROR_TEXT);
		} else {
			long l = this.serverData.id;
			String string = this.nameWidget.getText().trim();
			this.inviteButton.active = false;
			this.nameWidget.setEditable(false);
			this.showError(INVITING_TEXT);
			CompletableFuture.supplyAsync(() -> {
				try {
					return RealmsClient.create().invite(l, string);
				} catch (Exception var4) {
					LOGGER.error("Couldn't invite user");
					return null;
				}
			}, Util.getIoWorkerExecutor()).thenAcceptAsync(realmsServer -> {
				if (realmsServer != null) {
					this.serverData.players = realmsServer.players;
					this.client.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
				} else {
					this.showError(PLAYER_ERROR_TEXT);
				}

				this.nameWidget.setEditable(true);
				this.inviteButton.active = true;
			}, this.executor);
		}
	}

	private void showError(Text errorMessage) {
		this.errorMessage = errorMessage;
		this.client.getNarratorManager().narrate(errorMessage);
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
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.drawText(this.textRenderer, INVITE_PROFILE_NAME_TEXT, this.width / 2 - 100, row(1), -1, false);
		if (this.errorMessage != null) {
			context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessage, this.width / 2, row(5), -1);
		}

		this.nameWidget.render(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
	}
}
