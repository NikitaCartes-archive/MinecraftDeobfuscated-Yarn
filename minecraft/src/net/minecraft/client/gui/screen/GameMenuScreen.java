package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class GameMenuScreen extends Screen {
	private static final String SNAPSHOT_FEEDBACK_URL = "https://aka.ms/snapshotfeedback?ref=game";
	private static final String JAVA_FEEDBACK_URL = "https://aka.ms/javafeedback?ref=game";
	private static final String SNAPSHOT_BUGS_URL = "https://aka.ms/snapshotbugs?ref=game";
	private static final int field_41616 = 2;
	private static final int field_41617 = 50;
	private static final int field_41618 = 4;
	private static final int field_41619 = 204;
	private static final int field_41620 = 98;
	private static final Text RETURN_TO_GAME_TEXT = Text.translatable("menu.returnToGame");
	private static final Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");
	private static final Text STATS_TEXT = Text.translatable("gui.stats");
	private static final Text SEND_FEEDBACK_TEXT = Text.translatable("menu.sendFeedback");
	private static final Text REPORT_BUGS_TEXT = Text.translatable("menu.reportBugs");
	private static final Text OPTIONS_TEXT = Text.translatable("menu.options");
	private static final Text SHARE_TO_LAN_TEXT = Text.translatable("menu.shareToLan");
	private static final Text PLAYER_REPORTING_TEXT = Text.translatable("menu.playerReporting");
	private static final Text RETURN_TO_MENU_TEXT = Text.translatable("menu.returnToMenu");
	private static final Text DISCONNECT_TEXT = Text.translatable("menu.disconnect");
	private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
	private static final Text GAME_TEXT = Text.translatable("menu.game");
	private static final Text PAUSED_TEXT = Text.translatable("menu.paused");
	private final boolean showMenu;
	@Nullable
	private ButtonWidget exitButton;

	public GameMenuScreen(boolean showMenu) {
		super(showMenu ? GAME_TEXT : PAUSED_TEXT);
		this.showMenu = showMenu;
	}

	@Override
	protected void init() {
		if (this.showMenu) {
			this.initWidgets();
		}

		this.addDrawableChild(new TextWidget(0, this.showMenu ? 40 : 10, this.width, 9, this.title, this.textRenderer));
	}

	private void initWidgets() {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().margin(4, 4, 4, 0);
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(ButtonWidget.builder(RETURN_TO_GAME_TEXT, button -> {
			this.client.setScreen(null);
			this.client.mouse.lockCursor();
		}).width(204).build(), 2, gridWidget.copyPositioner().marginTop(50));
		adder.add(this.createButton(ADVANCEMENTS_TEXT, () -> new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler())));
		adder.add(this.createButton(STATS_TEXT, () -> new StatsScreen(this, this.client.player.getStatHandler())));
		adder.add(
			this.createUrlButton(
				SEND_FEEDBACK_TEXT, SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game"
			)
		);
		adder.add(this.createUrlButton(REPORT_BUGS_TEXT, "https://aka.ms/snapshotbugs?ref=game")).active = !SharedConstants.getGameVersion()
			.getSaveVersion()
			.isNotMainSeries();
		adder.add(this.createButton(OPTIONS_TEXT, () -> new OptionsScreen(this, this.client.options)));
		if (this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote()) {
			adder.add(this.createButton(SHARE_TO_LAN_TEXT, () -> new OpenToLanScreen(this)));
		} else {
			adder.add(this.createButton(PLAYER_REPORTING_TEXT, SocialInteractionsScreen::new));
		}

		Text text = this.client.isInSingleplayer() ? RETURN_TO_MENU_TEXT : DISCONNECT_TEXT;
		this.exitButton = adder.add(ButtonWidget.builder(text, button -> {
			button.active = false;
			this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::disconnect, true);
		}).width(204).build(), 2);
		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.25F);
		gridWidget.forEachChild(this::addDrawableChild);
	}

	private void disconnect() {
		boolean bl = this.client.isInSingleplayer();
		boolean bl2 = this.client.isConnectedToRealms();
		this.client.world.disconnect();
		if (bl) {
			this.client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
		} else {
			this.client.disconnect();
		}

		TitleScreen titleScreen = new TitleScreen();
		if (bl) {
			this.client.setScreen(titleScreen);
		} else if (bl2) {
			this.client.setScreen(new RealmsMainScreen(titleScreen));
		} else {
			this.client.setScreen(new MultiplayerScreen(titleScreen));
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.showMenu) {
			this.renderBackground(matrices);
		}

		super.render(matrices, mouseX, mouseY, delta);
		if (this.showMenu && this.client != null && this.client.getAbuseReportContext().hasDraft() && this.exitButton != null) {
			RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
			this.drawTexture(matrices, this.exitButton.getX() + this.exitButton.getWidth() - 17, this.exitButton.getY() + 3, 182, 24, 15, 15);
		}
	}

	private ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier) {
		return ButtonWidget.builder(text, button -> this.client.setScreen((Screen)screenSupplier.get())).width(98).build();
	}

	private ButtonWidget createUrlButton(Text text, String url) {
		return this.createButton(text, () -> new ConfirmLinkScreen(confirmed -> {
				if (confirmed) {
					Util.getOperatingSystem().open(url);
				}

				this.client.setScreen(this);
			}, url, true));
	}
}
