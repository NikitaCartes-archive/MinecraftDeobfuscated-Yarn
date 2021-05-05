package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TitleScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String field_32272 = "Demo_World";
	public static final String COPYRIGHT = "Copyright Mojang AB. Do not distribute!";
	public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
	private final boolean isMinceraft;
	@Nullable
	private String splashText;
	private ButtonWidget buttonResetDemo;
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private boolean realmsNotificationsInitialized;
	private Screen realmsNotificationGui;
	private int copyrightTextWidth;
	private int copyrightTextX;
	private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
	private final boolean doBackgroundFade;
	private long backgroundFadeStart;

	public TitleScreen() {
		this(false);
	}

	public TitleScreen(boolean doBackgroundFade) {
		super(new TranslatableText("narrator.screen.title"));
		this.doBackgroundFade = doBackgroundFade;
		this.isMinceraft = (double)new Random().nextFloat() < 1.0E-4;
	}

	private boolean areRealmsNotificationsEnabled() {
		return this.client.options.realmsNotifications && this.realmsNotificationGui != null;
	}

	@Override
	public void tick() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.tick();
		}
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
	public boolean isPauseScreen() {
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

		this.copyrightTextWidth = this.textRenderer.getWidth("Copyright Mojang AB. Do not distribute!");
		this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
		int i = 24;
		int j = this.height / 4 + 48;
		if (this.client.isDemo()) {
			this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.addButton(
			new TexturedButtonWidget(
				this.width / 2 - 124,
				j + 72 + 12,
				20,
				20,
				0,
				106,
				20,
				ButtonWidget.WIDGETS_LOCATION,
				256,
				256,
				buttonWidget -> this.client.openScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())),
				new TranslatableText("narrator.button.language")
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				j + 72 + 12,
				98,
				20,
				new TranslatableText("menu.options"),
				buttonWidget -> this.client.openScreen(new OptionsScreen(this, this.client.options))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslatableText("menu.quit"), buttonWidget -> this.client.scheduleStop()));
		this.addButton(
			new TexturedButtonWidget(
				this.width / 2 + 104,
				j + 72 + 12,
				20,
				20,
				0,
				0,
				20,
				ACCESSIBILITY_ICON_TEXTURE,
				32,
				64,
				buttonWidget -> this.client.openScreen(new AccessibilityOptionsScreen(this, this.client.options)),
				new TranslatableText("narrator.button.accessibility")
			)
		);
		this.client.setConnectedToRealms(false);
		if (this.client.options.realmsNotifications && !this.realmsNotificationsInitialized) {
			RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
			this.realmsNotificationGui = realmsBridgeScreen.getNotificationScreen(this);
			this.realmsNotificationsInitialized = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.init(this.client, this.width, this.height);
		}
	}

	private void initWidgetsNormal(int y, int spacingY) {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), buttonWidget -> this.client.openScreen(new SelectWorldScreen(this))
			)
		);
		boolean bl = this.client.isMultiplayerEnabled();
		ButtonWidget.TooltipSupplier tooltipSupplier = bl
			? ButtonWidget.EMPTY
			: (buttonWidget, matrixStack, i, j) -> {
				if (!buttonWidget.active) {
					this.renderOrderedTooltip(
						matrixStack, this.client.textRenderer.wrapLines(new TranslatableText("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), i, j
					);
				}
			};
		this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, new TranslatableText("menu.multiplayer"), buttonWidget -> {
			Screen screen = (Screen)(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
			this.client.openScreen(screen);
		}, tooltipSupplier)).active = bl;
		this.addButton(
				new ButtonWidget(
					this.width / 2 - 100, y + spacingY * 2, 200, 20, new TranslatableText("menu.online"), buttonWidget -> this.switchToRealms(), tooltipSupplier
				)
			)
			.active = bl;
	}

	private void initWidgetsDemo(int y, int spacingY) {
		boolean bl = this.canReadDemoWorldData();
		this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.playdemo"), buttonWidget -> {
			if (bl) {
				this.client.startIntegratedServer("Demo_World");
			} else {
				DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
				this.client.createWorld("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, impl, GeneratorOptions.createDemo(impl));
			}
		}));
		this.buttonResetDemo = this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				y + spacingY * 1,
				200,
				20,
				new TranslatableText("menu.resetdemo"),
				buttonWidget -> {
					LevelStorage levelStorage = this.client.getLevelStorage();

					try (LevelStorage.Session session = levelStorage.createSession("Demo_World")) {
						LevelSummary levelSummary = session.getLevelSummary();
						if (levelSummary != null) {
							this.client
								.openScreen(
									new ConfirmScreen(
										this::onDemoDeletionConfirmed,
										new TranslatableText("selectWorld.deleteQuestion"),
										new TranslatableText("selectWorld.deleteWarning", levelSummary.getDisplayName()),
										new TranslatableText("selectWorld.deleteButton"),
										ScreenTexts.CANCEL
									)
								);
						}
					} catch (IOException var16) {
						SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
						LOGGER.warn("Failed to access demo world", (Throwable)var16);
					}
				}
			)
		);
		this.buttonResetDemo.active = bl;
	}

	private boolean canReadDemoWorldData() {
		try (LevelStorage.Session session = this.client.getLevelStorage().createSession("Demo_World")) {
			return session.getLevelSummary() != null;
		} catch (IOException var15) {
			SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
			LOGGER.warn("Failed to read demo world data", (Throwable)var15);
			return false;
		}
	}

	private void switchToRealms() {
		RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
		realmsBridgeScreen.switchToRealms(this);
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
				this.method_29343(j, 30, (integer, integer2) -> {
					this.drawTexture(matrices, integer + 0, integer2, 0, 0, 99, 44);
					this.drawTexture(matrices, integer + 99, integer2, 129, 0, 27, 44);
					this.drawTexture(matrices, integer + 99 + 26, integer2, 126, 0, 3, 44);
					this.drawTexture(matrices, integer + 99 + 26 + 3, integer2, 99, 0, 26, 44);
					this.drawTexture(matrices, integer + 155, integer2, 0, 45, 155, 44);
				});
			} else {
				this.method_29343(j, 30, (integer, integer2) -> {
					this.drawTexture(matrices, integer + 0, integer2, 0, 0, 155, 44);
					this.drawTexture(matrices, integer + 155, integer2, 0, 45, 155, 44);
				});
			}

			RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
			drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
			if (this.splashText != null) {
				matrices.push();
				matrices.translate((double)(this.width / 2 + 90), 70.0, 0.0);
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0F));
				float h = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
				h = h * 100.0F / (float)(this.textRenderer.getWidth(this.splashText) + 32);
				matrices.scale(h, h, h);
				drawCenteredString(matrices, this.textRenderer, this.splashText, 0, -8, 16776960 | l);
				matrices.pop();
			}

			String string = "Minecraft " + SharedConstants.getGameVersion().getName();
			if (this.client.isDemo()) {
				string = string + " Demo";
			} else {
				string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
			}

			if (this.client.isModded()) {
				string = string + I18n.translate("menu.modded");
			}

			drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);
			drawStringWithShadow(matrices, this.textRenderer, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 16777215 | l);
			if (mouseX > this.copyrightTextX && mouseX < this.copyrightTextX + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
				fill(matrices, this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 16777215 | l);
			}

			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.setAlpha(g);
			}

			super.render(matrices, mouseX, mouseY, delta);
			if (this.areRealmsNotificationsEnabled() && g >= 1.0F) {
				this.realmsNotificationGui.render(matrices, mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			if (mouseX > (double)this.copyrightTextX
				&& mouseX < (double)(this.copyrightTextX + this.copyrightTextWidth)
				&& mouseY > (double)(this.height - 10)
				&& mouseY < (double)this.height) {
				this.client.openScreen(new CreditsScreen(false, Runnables.doNothing()));
			}

			return false;
		}
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
			} catch (IOException var15) {
				SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
				LOGGER.warn("Failed to delete demo world", (Throwable)var15);
			}
		}

		this.client.openScreen(this);
	}
}
