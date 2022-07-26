package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class TitleScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String DEMO_WORLD_NAME = "Demo_World";
	public static final Text COPYRIGHT = Text.literal("Copyright Mojang AB. Do not distribute!");
	public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
	private final boolean isMinceraft;
	@Nullable
	private String splashText;
	private ButtonWidget buttonResetDemo;
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	@Nullable
	private RealmsNotificationsScreen realmsNotificationGui;
	private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
	private final boolean doBackgroundFade;
	private long backgroundFadeStart;
	@Nullable
	private TitleScreen.DeprecationNotice deprecationNotice;

	public TitleScreen() {
		this(false);
	}

	public TitleScreen(boolean doBackgroundFade) {
		super(Text.translatable("narrator.screen.title"));
		this.doBackgroundFade = doBackgroundFade;
		this.isMinceraft = (double)Random.create().nextFloat() < 1.0E-4;
	}

	private boolean areRealmsNotificationsEnabled() {
		return this.client.options.getRealmsNotifications().getValue() && this.realmsNotificationGui != null;
	}

	@Override
	public void tick() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.tick();
		}

		this.client.getRealms32BitWarningChecker().showWarningIfNeeded(this);
	}

	public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
		return CompletableFuture.allOf(
			textureManager.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor),
			textureManager.loadTextureAsync(EDITION_TITLE_TEXTURE, executor),
			textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor),
			PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor)
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

		this.addDrawableChild(
			new TexturedButtonWidget(
				this.width / 2 - 124,
				l + 72 + 12,
				20,
				20,
				0,
				106,
				20,
				ButtonWidget.WIDGETS_TEXTURE,
				256,
				256,
				button -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())),
				Text.translatable("narrator.button.language")
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100, l + 72 + 12, 98, 20, Text.translatable("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options))
			)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20, Text.translatable("menu.quit"), button -> this.client.scheduleStop()));
		this.addDrawableChild(
			new TexturedButtonWidget(
				this.width / 2 + 104,
				l + 72 + 12,
				20,
				20,
				0,
				0,
				20,
				ACCESSIBILITY_ICON_TEXTURE,
				32,
				64,
				button -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)),
				Text.translatable("narrator.button.accessibility")
			)
		);
		this.addDrawableChild(
			new PressableTextWidget(
				j, this.height - 10, i, 10, COPYRIGHT, button -> this.client.setScreen(new CreditsScreen(false, Runnables.doNothing())), this.textRenderer
			)
		);
		this.client.setConnectedToRealms(false);
		if (this.client.options.getRealmsNotifications().getValue() && this.realmsNotificationGui == null) {
			this.realmsNotificationGui = new RealmsNotificationsScreen();
		}

		if (this.areRealmsNotificationsEnabled()) {
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
			new ButtonWidget(this.width / 2 - 100, y, 200, 20, Text.translatable("menu.singleplayer"), button -> this.client.setScreen(new SelectWorldScreen(this)))
		);
		final Text text = this.getMultiplayerDisabledText();
		boolean bl = text == null;
		ButtonWidget.TooltipSupplier tooltipSupplier = text == null
			? ButtonWidget.EMPTY
			: new ButtonWidget.TooltipSupplier() {
				@Override
				public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
					TitleScreen.this.renderOrderedTooltip(
						matrixStack, TitleScreen.this.client.textRenderer.wrapLines(text, Math.max(TitleScreen.this.width / 2 - 43, 170)), i, j
					);
				}

				@Override
				public void supply(Consumer<Text> consumer) {
					consumer.accept(text);
				}
			};
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, Text.translatable("menu.multiplayer"), button -> {
			Screen screen = (Screen)(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
			this.client.setScreen(screen);
		}, tooltipSupplier)).active = bl;
		this.addDrawableChild(
				new ButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, Text.translatable("menu.online"), button -> this.switchToRealms(), tooltipSupplier)
			)
			.active = bl;
	}

	@Nullable
	private Text getMultiplayerDisabledText() {
		if (this.client.isMultiplayerEnabled()) {
			return null;
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
			new ButtonWidget(
				this.width / 2 - 100,
				y,
				200,
				20,
				Text.translatable("menu.playdemo"),
				button -> {
					if (bl) {
						this.client.createIntegratedServerLoader().start(this, "Demo_World");
					} else {
						DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.createAndLoad().toImmutable();
						this.client
							.createIntegratedServerLoader()
							.createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, dynamicRegistryManager, WorldPresets.createDemoOptions(dynamicRegistryManager));
					}
				}
			)
		);
		this.buttonResetDemo = this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				y + spacingY * 1,
				200,
				20,
				Text.translatable("menu.resetdemo"),
				button -> {
					LevelStorage levelStorage = this.client.getLevelStorage();

					try (LevelStorage.Session session = levelStorage.createSession("Demo_World")) {
						LevelSummary levelSummary = session.getLevelSummary();
						if (levelSummary != null) {
							this.client
								.setScreen(
									new ConfirmScreen(
										this::onDemoDeletionConfirmed,
										Text.translatable("selectWorld.deleteQuestion"),
										Text.translatable("selectWorld.deleteWarning", levelSummary.getDisplayName()),
										Text.translatable("selectWorld.deleteButton"),
										ScreenTexts.CANCEL
									)
								);
						}
					} catch (IOException var8) {
						SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
						LOGGER.warn("Failed to access demo world", (Throwable)var8);
					}
				}
			)
		);
		this.buttonResetDemo.active = bl;
	}

	private boolean canReadDemoWorldData() {
		try {
			boolean var2;
			try (LevelStorage.Session session = this.client.getLevelStorage().createSession("Demo_World")) {
				var2 = session.getLevelSummary() != null;
			}

			return var2;
		} catch (IOException var6) {
			SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
			LOGGER.warn("Failed to read demo world data", (Throwable)var6);
			return false;
		}
	}

	private void switchToRealms() {
		this.client.setScreen(new RealmsMainScreen(this));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
			this.backgroundFadeStart = Util.getMeasuringTimeMs();
		}

		float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
		this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
		int i = 274;
		int j = this.width / 2 - 137;
		int k = 30;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
		drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(g * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
			if (this.isMinceraft) {
				this.drawWithOutline(j, 30, (x, y) -> {
					this.drawTexture(matrices, x + 0, y, 0, 0, 99, 44);
					this.drawTexture(matrices, x + 99, y, 129, 0, 27, 44);
					this.drawTexture(matrices, x + 99 + 26, y, 126, 0, 3, 44);
					this.drawTexture(matrices, x + 99 + 26 + 3, y, 99, 0, 26, 44);
					this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
				});
			} else {
				this.drawWithOutline(j, 30, (x, y) -> {
					this.drawTexture(matrices, x + 0, y, 0, 0, 155, 44);
					this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
				});
			}

			RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
			drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
			if (this.deprecationNotice != null) {
				this.deprecationNotice.render(matrices, l);
			}

			if (this.splashText != null) {
				matrices.push();
				matrices.translate((double)(this.width / 2 + 90), 70.0, 0.0);
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0F));
				float h = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
				h = h * 100.0F / (float)(this.textRenderer.getWidth(this.splashText) + 32);
				matrices.scale(h, h, h);
				drawCenteredText(matrices, this.textRenderer, this.splashText, 0, -8, 16776960 | l);
				matrices.pop();
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

			drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);

			for (Element element : this.children()) {
				if (element instanceof ClickableWidget) {
					((ClickableWidget)element).setAlpha(g);
				}
			}

			super.render(matrices, mouseX, mouseY, delta);
			if (this.areRealmsNotificationsEnabled() && g >= 1.0F) {
				RenderSystem.enableDepthTest();
				this.realmsNotificationGui.render(matrices, mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button)
			? true
			: this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void removed() {
		if (this.realmsNotificationGui != null) {
			this.realmsNotificationGui.removed();
		}
	}

	private void onDemoDeletionConfirmed(boolean delete) {
		if (delete) {
			try (LevelStorage.Session session = this.client.getLevelStorage().createSession("Demo_World")) {
				session.deleteSessionLock();
			} catch (IOException var7) {
				SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
				LOGGER.warn("Failed to delete demo world", (Throwable)var7);
			}
		}

		this.client.setScreen(this);
	}

	@Environment(EnvType.CLIENT)
	static record DeprecationNotice(TextRenderer textRenderer, MultilineText label, int x, int y) {
		public void render(MatrixStack matrices, int color) {
			this.label.fillBackground(matrices, this.x, this.y, 9, 2, 1428160512);
			this.label.drawCenterWithShadow(matrices, this.x, this.y, 9, 16777215 | color);
		}
	}
}
