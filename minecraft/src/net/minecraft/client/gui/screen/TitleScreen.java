package net.minecraft.client.gui.screen;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class TitleScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text NARRATOR_SCREEN_TITLE = Text.translatable("narrator.screen.title");
	private static final Text COPYRIGHT = Text.translatable("title.credits");
	private static final String DEMO_WORLD_NAME = "Demo_World";
	private static final float field_49900 = 2000.0F;
	@Nullable
	private SplashTextRenderer splashText;
	private ButtonWidget buttonResetDemo;
	@Nullable
	private RealmsNotificationsScreen realmsNotificationGui;
	private float backgroundAlpha = 1.0F;
	private boolean doBackgroundFade;
	private long backgroundFadeStart;
	@Nullable
	private TitleScreen.DeprecationNotice deprecationNotice;
	private final LogoDrawer logoDrawer;

	public TitleScreen() {
		this(false);
	}

	public TitleScreen(boolean doBackgroundFade) {
		this(doBackgroundFade, null);
	}

	public TitleScreen(boolean doBackgroundFade, @Nullable LogoDrawer logoDrawer) {
		super(NARRATOR_SCREEN_TITLE);
		this.doBackgroundFade = doBackgroundFade;
		this.logoDrawer = (LogoDrawer)Objects.requireNonNullElseGet(logoDrawer, () -> new LogoDrawer(false));
	}

	private boolean isRealmsNotificationsGuiDisplayed() {
		return this.realmsNotificationGui != null;
	}

	@Override
	public void tick() {
		if (this.isRealmsNotificationsGuiDisplayed()) {
			this.realmsNotificationGui.tick();
		}

		this.client.getRealms32BitWarningChecker().showWarningIfNeeded(this);
	}

	public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
		return CompletableFuture.allOf(
			textureManager.loadTextureAsync(LogoDrawer.LOGO_TEXTURE, executor),
			textureManager.loadTextureAsync(LogoDrawer.EDITION_TEXTURE, executor),
			textureManager.loadTextureAsync(RotatingCubeMapRenderer.OVERLAY_TEXTURE, executor),
			PANORAMA_RENDERER.loadTexturesAsync(textureManager, executor)
		);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		if (this.splashText == null) {
			this.splashText = this.client.getSplashTextLoader().get();
		}

		int i = this.textRenderer.getWidth(COPYRIGHT);
		int j = this.width - i - 2;
		int k = 24;
		int l = this.height / 4 + 48;
		if (this.client.isDemo()) {
			this.initWidgetsDemo(l, 24);
		} else {
			this.initWidgetsNormal(l, 24);
		}

		TextIconButtonWidget textIconButtonWidget = this.addDrawableChild(
			AccessibilityOnboardingButtons.createLanguageButton(
				20, button -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())), true
			)
		);
		textIconButtonWidget.setPosition(this.width / 2 - 124, l + 72 + 12);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options)))
				.dimensions(this.width / 2 - 100, l + 72 + 12, 98, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("menu.quit"), button -> this.client.scheduleStop()).dimensions(this.width / 2 + 2, l + 72 + 12, 98, 20).build()
		);
		TextIconButtonWidget textIconButtonWidget2 = this.addDrawableChild(
			AccessibilityOnboardingButtons.createAccessibilityButton(
				20, button -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), true
			)
		);
		textIconButtonWidget2.setPosition(this.width / 2 + 104, l + 72 + 12);
		this.addDrawableChild(
			new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, button -> this.client.setScreen(new CreditsAndAttributionScreen(this)), this.textRenderer)
		);
		if (this.realmsNotificationGui == null) {
			this.realmsNotificationGui = new RealmsNotificationsScreen();
		}

		if (this.isRealmsNotificationsGuiDisplayed()) {
			this.realmsNotificationGui.init(this.client, this.width, this.height);
		}

		if (!this.client.is64Bit()) {
			this.deprecationNotice = new TitleScreen.DeprecationNotice(
				this.textRenderer, MultilineText.create(this.textRenderer, Text.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24
			);
		}
	}

	private void initWidgetsNormal(int y, int spacingY) {
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("menu.singleplayer"), button -> this.client.setScreen(new SelectWorldScreen(this)))
				.dimensions(this.width / 2 - 100, y, 200, 20)
				.build()
		);
		Text text = this.getMultiplayerDisabledText();
		boolean bl = text == null;
		Tooltip tooltip = text != null ? Tooltip.of(text) : null;
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), button -> {
			Screen screen = (Screen)(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
			this.client.setScreen(screen);
		}).dimensions(this.width / 2 - 100, y + spacingY * 1, 200, 20).tooltip(tooltip).build()).active = bl;
		this.addDrawableChild(
				ButtonWidget.builder(Text.translatable("menu.online"), buttonWidget -> this.client.setScreen(new RealmsMainScreen(this)))
					.dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20)
					.tooltip(tooltip)
					.build()
			)
			.active = bl;
	}

	@Nullable
	private Text getMultiplayerDisabledText() {
		if (this.client.isMultiplayerEnabled()) {
			return null;
		} else if (this.client.isUsernameBanned()) {
			return Text.translatable("title.multiplayer.disabled.banned.name");
		} else {
			BanDetails banDetails = this.client.getMultiplayerBanDetails();
			if (banDetails != null) {
				return banDetails.expires() != null
					? Text.translatable("title.multiplayer.disabled.banned.temporary")
					: Text.translatable("title.multiplayer.disabled.banned.permanent");
			} else {
				return Text.translatable("title.multiplayer.disabled");
			}
		}
	}

	private void initWidgetsDemo(int y, int spacingY) {
		boolean bl = this.canReadDemoWorldData();
		this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("menu.playdemo"),
					button -> {
						if (bl) {
							this.client.createIntegratedServerLoader().start("Demo_World", () -> this.client.setScreen(this));
						} else {
							this.client
								.createIntegratedServerLoader()
								.createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions, this);
						}
					}
				)
				.dimensions(this.width / 2 - 100, y, 200, 20)
				.build()
		);
		this.buttonResetDemo = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("menu.resetdemo"),
					button -> {
						LevelStorage levelStorage = this.client.getLevelStorage();
			
						try (LevelStorage.Session session = levelStorage.createSessionWithoutSymlinkCheck("Demo_World")) {
							if (session.levelDatExists()) {
								this.client
									.setScreen(
										new ConfirmScreen(
											this::onDemoDeletionConfirmed,
											Text.translatable("selectWorld.deleteQuestion"),
											Text.translatable("selectWorld.deleteWarning", MinecraftServer.DEMO_LEVEL_INFO.getLevelName()),
											Text.translatable("selectWorld.deleteButton"),
											ScreenTexts.CANCEL
										)
									);
							}
						} catch (IOException var8) {
							SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
							LOGGER.warn("Failed to access demo world", var8);
						}
					}
				)
				.dimensions(this.width / 2 - 100, y + spacingY * 1, 200, 20)
				.build()
		);
		this.buttonResetDemo.active = bl;
	}

	private boolean canReadDemoWorldData() {
		try {
			boolean var2;
			try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World")) {
				var2 = session.levelDatExists();
			}

			return var2;
		} catch (IOException var6) {
			SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
			LOGGER.warn("Failed to read demo world data", var6);
			return false;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
			this.backgroundFadeStart = Util.getMeasuringTimeMs();
		}

		float f = 1.0F;
		if (this.doBackgroundFade) {
			float g = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
			if (g > 1.0F) {
				this.doBackgroundFade = false;
				this.backgroundAlpha = 1.0F;
			} else {
				g = MathHelper.clamp(g, 0.0F, 1.0F);
				f = MathHelper.clampedMap(g, 0.5F, 1.0F, 0.0F, 1.0F);
				this.backgroundAlpha = MathHelper.clampedMap(g, 0.0F, 0.5F, 0.0F, 1.0F);

				for(Element element : this.children()) {
					if (element instanceof ClickableWidget clickableWidget) {
						clickableWidget.setAlpha(f);
					}
				}
			}
		}

		this.renderPanoramaBackground(context, delta);
		int i = MathHelper.ceil(f * 255.0F) << 24;
		if ((i & -67108864) != 0) {
			super.render(context, mouseX, mouseY, delta);
			this.logoDrawer.draw(context, this.width, f);
			if (this.deprecationNotice != null) {
				this.deprecationNotice.render(context, i);
			}

			if (this.splashText != null && !this.client.options.getHideSplashTexts().getValue()) {
				this.splashText.render(context, this.width, this.textRenderer, i);
			}

			String string = "Minecraft " + SharedConstants.getGameVersion().getName();
			if (this.client.isDemo()) {
				string = string + " Demo";
			} else {
				string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
			}

			if (MinecraftClient.getModStatus().isModded()) {
				string = string + I18n.translate("menu.modded");
			}

			context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | i);
			if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0F) {
				RenderSystem.enableDepthTest();
				this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
	}

	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, this.backgroundAlpha, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.isRealmsNotificationsGuiDisplayed() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void removed() {
		if (this.realmsNotificationGui != null) {
			this.realmsNotificationGui.removed();
		}
	}

	@Override
	public void onDisplayed() {
		super.onDisplayed();
		if (this.realmsNotificationGui != null) {
			this.realmsNotificationGui.onDisplayed();
		}
	}

	private void onDemoDeletionConfirmed(boolean delete) {
		if (delete) {
			try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World")) {
				session.deleteSessionLock();
			} catch (IOException var7) {
				SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
				LOGGER.warn("Failed to delete demo world", var7);
			}
		}

		this.client.setScreen(this);
	}

	@Environment(EnvType.CLIENT)
	static record DeprecationNotice(TextRenderer textRenderer, MultilineText label, int x, int y) {
		public void render(DrawContext context, int color) {
			this.label.fillBackground(context, this.x, this.y, 9, 2, 2097152 | Math.min(color, 1426063360));
			this.label.drawCenterWithShadow(context, this.x, this.y, 9, 16777215 | color);
		}
	}
}
