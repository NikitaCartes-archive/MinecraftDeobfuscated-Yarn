package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

@Environment(EnvType.CLIENT)
public class TitleScreen extends Screen {
	public static final CubeMapRenderer field_17774 = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
	private final boolean field_17776;
	@Nullable
	private String splashText;
	private ButtonWidget buttonResetDemo;
	@Nullable
	private TitleScreen.Warning warning;
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private boolean realmsNotificationsInitialized;
	private Screen realmsNotificationGui;
	private int copyrightTextWidth;
	private int copyrightTextX;
	private final RotatingCubeMapRenderer field_2585 = new RotatingCubeMapRenderer(field_17774);
	private final boolean doBackgroundFade;
	private long backgroundFadeStart;

	public TitleScreen() {
		this(false);
	}

	public TitleScreen(boolean bl) {
		super(new TranslatableText("narrator.screen.title"));
		this.doBackgroundFade = bl;
		this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.warning = new TitleScreen.Warning(
				new TranslatableText("title.oldgl.eol.line1").formatted(Formatting.field_1061).formatted(Formatting.field_1067),
				new TranslatableText("title.oldgl.eol.line2").formatted(Formatting.field_1061).formatted(Formatting.field_1067),
				"https://help.mojang.com/customer/portal/articles/325948?ref=game"
			);
		}
	}

	private boolean areRealmsNotificationsEnabled() {
		return this.minecraft.field_1690.realmsNotifications && this.realmsNotificationGui != null;
	}

	@Override
	public void tick() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.tick();
		}
	}

	public static CompletableFuture<Void> method_18105(TextureManager textureManager, Executor executor) {
		return CompletableFuture.allOf(
			textureManager.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor),
			textureManager.loadTextureAsync(EDITION_TITLE_TEXTURE, executor),
			textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor),
			field_17774.method_18143(textureManager, executor)
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
			this.splashText = this.minecraft.method_18095().get();
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
				buttonWidget -> this.minecraft.method_1507(new LanguageOptionsScreen(this, this.minecraft.field_1690, this.minecraft.method_1526())),
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
				buttonWidget -> this.minecraft.method_1507(new SettingsScreen(this, this.minecraft.field_1690))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit"), buttonWidget -> this.minecraft.scheduleStop()));
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
				buttonWidget -> this.minecraft.method_1507(new AccessibilityScreen(this, this.minecraft.field_1690)),
				I18n.translate("narrator.button.accessibility")
			)
		);
		if (this.warning != null) {
			this.warning.init(j);
		}

		this.minecraft.setConnectedToRealms(false);
		if (this.minecraft.field_1690.realmsNotifications && !this.realmsNotificationsInitialized) {
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
				this.width / 2 - 100, i, 200, 20, I18n.translate("menu.singleplayer"), buttonWidget -> this.minecraft.method_1507(new SelectWorldScreen(this))
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, i + j * 1, 200, 20, I18n.translate("menu.multiplayer"), buttonWidget -> this.minecraft.method_1507(new MultiplayerScreen(this))
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
							.method_1507(
								new ConfirmScreen(
									this::method_20375,
									new TranslatableText("selectWorld.deleteQuestion"),
									new TranslatableText("selectWorld.deleteWarning", levelPropertiesx.getLevelName()),
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
		this.field_2585.render(f, MathHelper.clamp(g, 0.0F, 1.0F));
		int k = 274;
		int l = this.width / 2 - 137;
		int m = 30;
		this.minecraft.method_1531().bindTexture(PANORAMA_OVERLAY);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(g, 0.0F, 1.0F)) : 1.0F);
		blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float h = this.doBackgroundFade ? MathHelper.clamp(g - 1.0F, 0.0F, 1.0F) : 1.0F;
		int n = MathHelper.ceil(h * 255.0F) << 24;
		if ((n & -67108864) != 0) {
			this.minecraft.method_1531().bindTexture(MINECRAFT_TITLE_TEXTURE);
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

			this.minecraft.method_1531().bindTexture(EDITION_TITLE_TEXTURE);
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

			if (this.warning != null) {
				this.warning.render(n);
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
		} else if (this.warning != null && this.warning.onClick(d, e)) {
			return true;
		} else if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(d, e, i)) {
			return true;
		} else {
			if (d > (double)this.copyrightTextX
				&& d < (double)(this.copyrightTextX + this.copyrightTextWidth)
				&& e > (double)(this.height - 10)
				&& e < (double)this.height) {
				this.minecraft.method_1507(new EndCreditsScreen(false, Runnables.doNothing()));
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

	private void method_20375(boolean bl) {
		if (bl) {
			LevelStorage levelStorage = this.minecraft.getLevelStorage();
			levelStorage.deleteLevel("Demo_World");
		}

		this.minecraft.method_1507(this);
	}

	@Environment(EnvType.CLIENT)
	class Warning {
		private int line2Width;
		private int startX;
		private int startY;
		private int endX;
		private int endY;
		private final Text line1;
		private final Text line2;
		private final String helpUrl;

		public Warning(Text text, Text text2, String string) {
			this.line1 = text;
			this.line2 = text2;
			this.helpUrl = string;
		}

		public void init(int i) {
			int j = TitleScreen.this.font.getStringWidth(this.line1.getString());
			this.line2Width = TitleScreen.this.font.getStringWidth(this.line2.getString());
			int k = Math.max(j, this.line2Width);
			this.startX = (TitleScreen.this.width - k) / 2;
			this.startY = i - 24;
			this.endX = this.startX + k;
			this.endY = this.startY + 24;
		}

		public void render(int i) {
			DrawableHelper.fill(this.startX - 2, this.startY - 2, this.endX + 2, this.endY - 1, 1428160512);
			TitleScreen.this.drawString(TitleScreen.this.font, this.line1.asFormattedString(), this.startX, this.startY, 16777215 | i);
			TitleScreen.this.drawString(
				TitleScreen.this.font, this.line2.asFormattedString(), (TitleScreen.this.width - this.line2Width) / 2, this.startY + 12, 16777215 | i
			);
		}

		public boolean onClick(double d, double e) {
			if (!ChatUtil.isEmpty(this.helpUrl) && d >= (double)this.startX && d <= (double)this.endX && e >= (double)this.startY && e <= (double)this.endY) {
				TitleScreen.this.minecraft.method_1507(new ConfirmChatLinkScreen(bl -> {
					if (bl) {
						SystemUtil.getOperatingSystem().open(this.helpUrl);
					}

					TitleScreen.this.minecraft.method_1507(TitleScreen.this);
				}, this.helpUrl, true));
				return true;
			} else {
				return false;
			}
		}
	}
}
