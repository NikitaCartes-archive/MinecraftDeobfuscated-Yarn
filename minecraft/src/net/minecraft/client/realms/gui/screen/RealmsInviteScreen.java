package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text INVITE_TEXT = Text.translatable("mco.configure.world.buttons.invite");
	private static final Text INVITE_PROFILE_NAME_TEXT = Text.translatable("mco.configure.world.invite.profile.name").withColor(Colors.LIGHT_GRAY);
	private static final Text INVITING_TEXT = Text.translatable("mco.configure.world.players.inviting").withColor(Colors.LIGHT_GRAY);
	private static final Text PLAYER_ERROR_TEXT = Text.translatable("mco.configure.world.players.error").withColor(Colors.RED);
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private TextFieldWidget nameWidget;
	private ButtonWidget inviteButton;
	private final RealmsServer serverData;
	private final RealmsConfigureWorldScreen configureScreen;
	private final Screen parent;
	@Nullable
	private Text errorMessage;

	public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, Screen parent, RealmsServer serverData) {
		super(INVITE_TEXT);
		this.configureScreen = configureScreen;
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void init() {
		this.layout.addHeader(INVITE_TEXT, this.textRenderer);
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical().spacing(8));
		this.nameWidget = new TextFieldWidget(this.client.textRenderer, 200, 20, Text.translatable("mco.configure.world.invite.profile.name"));
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.nameWidget, INVITE_PROFILE_NAME_TEXT));
		this.inviteButton = directionalLayoutWidget.add(ButtonWidget.builder(INVITE_TEXT, button -> this.onInvite()).width(200).build());
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, buttonWidget -> this.close()).width(200).build());
		this.layout.forEachChild(element -> {
			ClickableWidget var10000 = this.addDrawableChild(element);
		});
		this.refreshWidgetPositions();
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nameWidget);
	}

	private void onInvite() {
		if (StringHelper.isBlank(this.nameWidget.getText())) {
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
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (this.errorMessage != null) {
			context.drawCenteredTextWithShadow(
				this.textRenderer, this.errorMessage, this.width / 2, this.inviteButton.getY() + this.inviteButton.getHeight() + 8, Colors.WHITE
			);
		}
	}
}
