package net.minecraft.client.gui;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.menu.AccessibilityScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.SettingsScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.menu.options.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends Screen {
	public static final CubeMapRenderer panoramaCubeMap = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier panoramaOverlay = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private static final Identifier field_19102 = new Identifier("textures/gui/accessibility.png");
	private final boolean field_17776;
	@Nullable
	private String splashText;
	private ButtonWidget buttonResetDemo;
	private final Object mutex = new Object();
	public static final String OUTDATED_GL_TEXT = "Please click " + ChatFormat.field_1073 + "here" + ChatFormat.field_1070 + " for more information.";
	private int warningTextWidth;
	private int warningTitleWidth;
	private int warningAlignLeft;
	private int warningAlignTop;
	private int warningAlignRight;
	private int warningAlignBottom;
	private String warningTitle;
	private String warningText = OUTDATED_GL_TEXT;
	private String warningLink;
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private boolean realmsNotificationsInitialized;
	private Screen realmsNotificationGui;
	private int copyrightTextWidth;
	private int copyrightTextX;
	private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(panoramaCubeMap);
	private final boolean doBackgroundFade;
	private long backgroundFadeStart;

	public MainMenuScreen() {
		this(false);
	}

	public MainMenuScreen(boolean bl) {
		super(new TranslatableComponent("narrator.screen.title"));
		this.doBackgroundFade = bl;
		this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
		this.warningTitle = "";
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.warningTitle = I18n.translate("title.oldgl1");
			this.warningText = I18n.translate("title.oldgl2");
			this.warningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean areRealmsNotificationsEnabled() {
		return this.minecraft.options.realmsNotifications && this.realmsNotificationGui != null;
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
			textureManager.loadTextureAsync(panoramaOverlay, executor),
			panoramaCubeMap.loadTexturesAsync(textureManager, executor)
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
			this.splashText = this.minecraft.getSplashTextLoader().get();
		}

		this.copyrightTextWidth = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
		int i = 24;
		int j = this.height / 4 + 48;
		if (this.minecraft.isDemo()) {
			this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.addButton(
			new RecipeBookButtonWidget(
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
				buttonWidget -> this.minecraft.openScreen(new LanguageOptionsScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
				I18n.translate("narrator.button.language")
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				j + 72 + 12,
				98,
				20,
				I18n.translate("menu.options"),
				buttonWidget -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit"), buttonWidget -> this.minecraft.scheduleStop()));
		this.addButton(
			new RecipeBookButtonWidget(
				this.width / 2 + 104,
				j + 72 + 12,
				20,
				20,
				0,
				0,
				20,
				field_19102,
				32,
				64,
				buttonWidget -> this.minecraft.openScreen(new AccessibilityScreen(this, this.minecraft.options)),
				I18n.translate("narrator.button.accessibility")
			)
		);
		synchronized (this.mutex) {
			this.warningTitleWidth = this.font.getStringWidth(this.warningTitle);
			this.warningTextWidth = this.font.getStringWidth(this.warningText);
			int k = Math.max(this.warningTitleWidth, this.warningTextWidth);
			this.warningAlignLeft = (this.width - k) / 2;
			this.warningAlignTop = j - 24;
			this.warningAlignRight = this.warningAlignLeft + k;
			this.warningAlignBottom = this.warningAlignTop + 24;
		}

		this.minecraft.setConnectedToRealms(false);
		if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.realmsNotificationGui = realmsBridge.getNotificationScreen(this);
			this.realmsNotificationsInitialized = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.init(this.minecraft, this.width, this.height);
		}
	}

	private void initWidgetsNormal(int i, int j) {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, i, 200, 20, I18n.translate("menu.singleplayer"), buttonWidget -> this.minecraft.openScreen(new LevelSelectScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, i + j * 1, 200, 20, I18n.translate("menu.multiplayer"), buttonWidget -> this.minecraft.openScreen(new MultiplayerScreen(this))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, i + j * 2, 200, 20, I18n.translate("menu.online"), buttonWidget -> this.switchToRealms()));
	}

	private void initWidgetsDemo(int i, int j) {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				i,
				200,
				20,
				I18n.translate("menu.playdemo"),
				buttonWidget -> this.minecraft.startIntegratedServer("Demo_World", "Demo_World", MinecraftServer.WORLD_INFO)
			)
		);
		this.buttonResetDemo = this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				i + j * 1,
				200,
				20,
				I18n.translate("menu.resetdemo"),
				buttonWidget -> {
					LevelStorage levelStoragex = this.minecraft.getLevelStorage();
					LevelProperties levelPropertiesx = levelStoragex.getLevelProperties("Demo_World");
					if (levelPropertiesx != null) {
						this.minecraft
							.openScreen(
								new YesNoScreen(
									this::method_20375,
									new TranslatableComponent("selectWorld.deleteQuestion"),
									new TranslatableComponent("selectWorld.deleteWarning", levelPropertiesx.getLevelName()),
									I18n.translate("selectWorld.deleteButton"),
									I18n.translate("gui.cancel")
								)
							);
					}
				}
			)
		);
		LevelStorage levelStorage = this.minecraft.getLevelStorage();
		LevelProperties levelProperties = levelStorage.getLevelProperties("Demo_World");
		if (levelProperties == null) {
			this.buttonResetDemo.active = false;
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
			this.backgroundFadeStart = SystemUtil.getMeasuringTimeMs();
		}

		float g = this.doBackgroundFade ? (float)(SystemUtil.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
		fill(0, 0, this.width, this.height, -1);
		this.backgroundRenderer.render(f, MathHelper.clamp(g, 0.0F, 1.0F));
		int k = 274;
		int l = this.width / 2 - 137;
		int m = 30;
		this.minecraft.getTextureManager().bindTexture(panoramaOverlay);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(g, 0.0F, 1.0F)) : 1.0F);
		blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float h = this.doBackgroundFade ? MathHelper.clamp(g - 1.0F, 0.0F, 1.0F) : 1.0F;
		int n = MathHelper.ceil(h * 255.0F) << 24;
		if ((n & -67108864) != 0) {
			this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, h);
			if (this.field_17776) {
				this.blit(l + 0, 30, 0, 0, 99, 44);
				this.blit(l + 99, 30, 129, 0, 27, 44);
				this.blit(l + 99 + 26, 30, 126, 0, 3, 44);
				this.blit(l + 99 + 26 + 3, 30, 99, 0, 26, 44);
				this.blit(l + 155, 30, 0, 45, 155, 44);
			} else {
				this.blit(l + 0, 30, 0, 0, 155, 44);
				this.blit(l + 155, 30, 0, 45, 155, 44);
			}

			this.minecraft.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
			blit(l + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
			if (this.splashText != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
				GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
				float o = 1.8F - MathHelper.abs(MathHelper.sin((float)(SystemUtil.getMeasuringTimeMs() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
				o = o * 100.0F / (float)(this.font.getStringWidth(this.splashText) + 32);
				GlStateManager.scalef(o, o, o);
				this.drawCenteredString(this.font, this.splashText, 0, -8, 16776960 | n);
				GlStateManager.popMatrix();
			}

			String string = "Minecraft " + SharedConstants.getGameVersion().getName();
			if (this.minecraft.isDemo()) {
				string = string + " Demo";
			} else {
				string = string + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
			}

			this.drawString(this.font, string, 2, this.height - 10, 16777215 | n);
			this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 16777215 | n);
			if (i > this.copyrightTextX && i < this.copyrightTextX + this.copyrightTextWidth && j > this.height - 10 && j < this.height) {
				fill(this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 16777215 | n);
			}

			if (this.warningTitle != null && !this.warningTitle.isEmpty()) {
				fill(this.warningAlignLeft - 2, this.warningAlignTop - 2, this.warningAlignRight + 2, this.warningAlignBottom - 1, 1428160512);
				this.drawString(this.font, this.warningTitle, this.warningAlignLeft, this.warningAlignTop, 16777215 | n);
				this.drawString(this.font, this.warningText, (this.width - this.warningTextWidth) / 2, this.warningAlignTop + 12, 16777215 | n);
			}

			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.setAlpha(h);
			}

			super.render(i, j, f);
			if (this.areRealmsNotificationsEnabled() && h >= 1.0F) {
				this.realmsNotificationGui.render(i, j, f);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			synchronized (this.mutex) {
				if (!this.warningTitle.isEmpty()
					&& !ChatUtil.isEmpty(this.warningLink)
					&& d >= (double)this.warningAlignLeft
					&& d <= (double)this.warningAlignRight
					&& e >= (double)this.warningAlignTop
					&& e <= (double)this.warningAlignBottom) {
					ConfirmChatLinkScreen confirmChatLinkScreen = new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							SystemUtil.getOperatingSystem().open(this.warningLink);
						}

						this.minecraft.openScreen(this);
					}, this.warningLink, true);
					this.minecraft.openScreen(confirmChatLinkScreen);
					return true;
				}
			}

			if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(d, e, i)) {
				return true;
			} else {
				if (d > (double)this.copyrightTextX
					&& d < (double)(this.copyrightTextX + this.copyrightTextWidth)
					&& e > (double)(this.height - 10)
					&& e < (double)this.height) {
					this.minecraft.openScreen(new EndCreditsScreen(false, Runnables.doNothing()));
				}

				return false;
			}
		}
	}

	@Override
	public void removed() {
		if (this.realmsNotificationGui != null) {
			this.realmsNotificationGui.removed();
		}
	}

	private void method_20375(boolean bl) {
		if (bl) {
			LevelStorage levelStorage = this.minecraft.getLevelStorage();
			levelStorage.deleteLevel("Demo_World");
		}

		this.minecraft.openScreen(this);
	}
}
