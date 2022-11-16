package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.widget.Positioner;
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
	private final boolean showMenu;
	private ButtonWidget exitButton;

	public GameMenuScreen(boolean showMenu) {
		super(showMenu ? Text.translatable("menu.game") : Text.translatable("menu.paused"));
		this.showMenu = showMenu;
	}

	@Override
	protected void init() {
		if (this.showMenu) {
			this.initWidgets();
		} else {
			SimplePositioningWidget simplePositioningWidget = this.addDrawableChild(SimplePositioningWidget.of(this.width, this.height));
			simplePositioningWidget.getMainPositioner().alignHorizontalCenter().alignTop().marginTop(10);
			simplePositioningWidget.add(new TextWidget(this.title, this.textRenderer));
			simplePositioningWidget.recalculateDimensions();
		}
	}

	private void initWidgets() {
		int i = 204;
		int j = 98;
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().margin(0, 2).alignHorizontalCenter();
		Positioner positioner = gridWidget.copyPositioner().alignLeft();
		Positioner positioner2 = gridWidget.copyPositioner().alignRight();
		int k = 0;
		gridWidget.add(new TextWidget(this.title, this.client.textRenderer), k, 0, 1, 2, gridWidget.copyPositioner().marginBottom(5));
		gridWidget.add(ButtonWidget.builder(Text.translatable("menu.returnToGame"), button -> {
			this.client.setScreen(null);
			this.client.mouse.lockCursor();
		}).width(204).build(), ++k, 0, 1, 2);
		gridWidget.add(
			ButtonWidget.builder(
					Text.translatable("gui.advancements"), button -> this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()))
				)
				.width(98)
				.build(),
			++k,
			0,
			positioner
		);
		gridWidget.add(
			ButtonWidget.builder(Text.translatable("gui.stats"), button -> this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler())))
				.width(98)
				.build(),
			k,
			1,
			positioner2
		);
		k++;
		String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
		gridWidget.add(ButtonWidget.builder(Text.translatable("menu.sendFeedback"), button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
				if (confirmed) {
					Util.getOperatingSystem().open(string);
				}

				this.client.setScreen(this);
			}, string, true))).width(98).build(), k, 0, positioner);
		ButtonWidget buttonWidget = gridWidget.add(
			ButtonWidget.builder(Text.translatable("menu.reportBugs"), button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
					if (confirmed) {
						Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
					}

					this.client.setScreen(this);
				}, "https://aka.ms/snapshotbugs?ref=game", true))).width(98).build(), k, 1, positioner2
		);
		buttonWidget.active = !SharedConstants.getGameVersion().getSaveVersion().isNotMainSeries();
		gridWidget.add(
			ButtonWidget.builder(Text.translatable("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options))).width(98).build(),
			++k,
			0,
			positioner
		);
		if (this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote()) {
			gridWidget.add(
				ButtonWidget.builder(Text.translatable("menu.shareToLan"), button -> this.client.setScreen(new OpenToLanScreen(this))).width(98).build(), k, 1, positioner2
			);
		} else {
			gridWidget.add(
				ButtonWidget.builder(Text.translatable("menu.playerReporting"), button -> this.client.setScreen(new SocialInteractionsScreen())).width(98).build(),
				k,
				1,
				positioner2
			);
		}

		k++;
		Text text = this.client.isInSingleplayer() ? Text.translatable("menu.returnToMenu") : Text.translatable("menu.disconnect");
		this.exitButton = gridWidget.add(ButtonWidget.builder(text, button -> {
			button.active = false;
			this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::disconnect, true);
		}).width(204).build(), k, 0, 1, 2);
		gridWidget.recalculateDimensions();
		SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height);
		this.addDrawableChild(gridWidget);
	}

	private void disconnect() {
		boolean bl = this.client.isInSingleplayer();
		boolean bl2 = this.client.isConnectedToRealms();
		this.client.world.disconnect();
		if (bl) {
			this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
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
		if (this.showMenu && this.client != null && this.client.getAbuseReportContext().hasDraft()) {
			RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(matrices, this.exitButton.getX() + this.exitButton.getWidth() - 17, this.exitButton.getY() + 3, 182, 24, 15, 15);
		}
	}
}
