package net.minecraft.client.gui;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_751;
import net.minecraft.class_766;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.SettingsScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.menu.settings.LanguageSettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LanguageButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends Screen {
	public static final class_751 field_17774 = new class_751(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier field_17775 = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private final boolean field_17776;
	private String splashText;
	private ButtonWidget field_2602;
	private ButtonWidget buttonResetDemo;
	private final Object mutex = new Object();
	public static final String OUTDATED_GL_TEXT = "Please click " + TextFormat.field_1073 + "here" + TextFormat.field_1070 + " for more information.";
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
	private boolean field_2599;
	private Screen realmsNotificationGui;
	private int field_2584;
	private int field_2606;
	private final class_766 field_2585 = new class_766(field_17774);
	private boolean field_18222;
	private long field_17772;

	public MainMenuScreen() {
		this(false);
	}

	public MainMenuScreen(boolean bl) {
		this.field_18222 = bl;
		this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
		this.warningTitle = "";
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.warningTitle = I18n.translate("title.oldgl1");
			this.warningText = I18n.translate("title.oldgl2");
			this.warningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean areRealmsNotificationsEnabled() {
		return this.client.options.realmsNotifications && this.realmsNotificationGui != null;
	}

	@Override
	public void update() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.update();
		}
	}

	public static CompletableFuture<Void> method_18105(TextureManager textureManager, Executor executor) {
		return CompletableFuture.allOf(
			textureManager.method_18168(MINECRAFT_TITLE_TEXTURE, executor),
			textureManager.method_18168(EDITION_TITLE_TEXTURE, executor),
			textureManager.method_18168(field_17775, executor),
			field_17774.method_18143(textureManager, executor)
		);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	protected void onInitialized() {
		this.splashText = this.client.method_18095().get();
		this.field_2584 = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.field_2606 = this.screenWidth - this.field_2584 - 2;
		int i = 24;
		int j = this.screenHeight / 4 + 48;
		if (this.client.isDemo()) {
			this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.field_2602 = this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, j + 72 + 12, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new SettingsScreen(MainMenuScreen.this, MainMenuScreen.this.client.options));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.scheduleStop();
			}
		});
		this.addButton(
			new LanguageButtonWidget(this.screenWidth / 2 - 124, j + 72 + 12) {
				@Override
				public void onPressed(double d, double e) {
					MainMenuScreen.this.client
						.openScreen(new LanguageSettingsScreen(MainMenuScreen.this, MainMenuScreen.this.client.options, MainMenuScreen.this.client.getLanguageManager()));
				}
			}
		);
		synchronized (this.mutex) {
			this.warningTitleWidth = this.fontRenderer.getStringWidth(this.warningTitle);
			this.warningTextWidth = this.fontRenderer.getStringWidth(this.warningText);
			int k = Math.max(this.warningTitleWidth, this.warningTextWidth);
			this.warningAlignLeft = (this.screenWidth - k) / 2;
			this.warningAlignTop = j - 24;
			this.warningAlignRight = this.warningAlignLeft + k;
			this.warningAlignBottom = this.warningAlignTop + 24;
		}

		this.client.setConnectedToRealms(false);
		if (this.client.options.realmsNotifications && !this.field_2599) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.realmsNotificationGui = realmsBridge.getNotificationScreen(this);
			this.field_2599 = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.initialize(this.client, this.screenWidth, this.screenHeight);
		}
	}

	private void initWidgetsNormal(int i, int j) {
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, i, I18n.translate("menu.singleplayer")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new LevelSelectScreen(MainMenuScreen.this));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, i + j * 1, I18n.translate("menu.multiplayer")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new MultiplayerScreen(MainMenuScreen.this));
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, i + j * 2, I18n.translate("menu.online")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.switchToRealms();
			}
		});
	}

	private void initWidgetsDemo(int i, int j) {
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, i, I18n.translate("menu.playdemo")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.startIntegratedServer("Demo_World", "Demo_World", MinecraftServer.field_17704);
			}
		});
		this.buttonResetDemo = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, i + j * 1, I18n.translate("menu.resetdemo")) {
				@Override
				public void onPressed(double d, double e) {
					LevelStorage levelStorage = MainMenuScreen.this.client.getLevelStorage();
					LevelProperties levelProperties = levelStorage.getLevelProperties("Demo_World");
					if (levelProperties != null) {
						MainMenuScreen.this.client
							.openScreen(
								new YesNoScreen(
									MainMenuScreen.this,
									I18n.translate("selectWorld.deleteQuestion"),
									I18n.translate("selectWorld.deleteWarning", levelProperties.getLevelName()),
									I18n.translate("selectWorld.deleteButton"),
									I18n.translate("gui.cancel"),
									12
								)
							);
					}
				}
			}
		);
		LevelStorage levelStorage = this.client.getLevelStorage();
		LevelProperties levelProperties = levelStorage.getLevelProperties("Demo_World");
		if (levelProperties == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && i == 12) {
			LevelStorage levelStorage = this.client.getLevelStorage();
			levelStorage.deleteLevel("Demo_World");
			this.client.openScreen(this);
		} else if (i == 12) {
			this.client.openScreen(this);
		} else if (i == 13) {
			if (bl) {
				SystemUtil.getOperatingSystem().open(this.warningLink);
			}

			this.client.openScreen(this);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.field_17772 == 0L && this.field_18222) {
			this.field_17772 = SystemUtil.getMeasuringTimeMs();
		}

		float g = this.field_18222 ? (float)(SystemUtil.getMeasuringTimeMs() - this.field_17772) / 1000.0F : 1.0F;
		drawRect(0, 0, this.screenWidth, this.screenHeight, -1);
		this.field_2585.method_3317(f, MathHelper.clamp(g, 0.0F, 1.0F));
		int k = 274;
		int l = this.screenWidth / 2 - 137;
		int m = 30;
		this.client.getTextureManager().bindTexture(field_17775);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.field_18222 ? (float)MathHelper.ceil(MathHelper.clamp(g, 0.0F, 1.0F)) : 1.0F);
		drawTexturedRect(0, 0, 0.0F, 0.0F, 16, 128, this.screenWidth, this.screenHeight, 16.0F, 128.0F);
		float h = this.field_18222 ? MathHelper.clamp(g - 1.0F, 0.0F, 1.0F) : 1.0F;
		int n = MathHelper.ceil(h * 255.0F) << 24;
		if ((n & -67108864) != 0) {
			this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, h);
			if (this.field_17776) {
				this.drawTexturedRect(l + 0, 30, 0, 0, 99, 44);
				this.drawTexturedRect(l + 99, 30, 129, 0, 27, 44);
				this.drawTexturedRect(l + 99 + 26, 30, 126, 0, 3, 44);
				this.drawTexturedRect(l + 99 + 26 + 3, 30, 99, 0, 26, 44);
				this.drawTexturedRect(l + 155, 30, 0, 45, 155, 44);
			} else {
				this.drawTexturedRect(l + 0, 30, 0, 0, 155, 44);
				this.drawTexturedRect(l + 155, 30, 0, 45, 155, 44);
			}

			this.client.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
			drawTexturedRect(l + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(this.screenWidth / 2 + 90), 70.0F, 0.0F);
			GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
			float o = 1.8F - MathHelper.abs(MathHelper.sin((float)(SystemUtil.getMeasuringTimeMs() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
			o = o * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
			GlStateManager.scalef(o, o, o);
			this.drawStringCentered(this.fontRenderer, this.splashText, 0, -8, 16776960 | n);
			GlStateManager.popMatrix();
			String string = "Minecraft " + SharedConstants.getGameVersion().getName();
			if (this.client.isDemo()) {
				string = string + " Demo";
			} else {
				string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
			}

			this.drawString(this.fontRenderer, string, 2, this.screenHeight - 10, 16777215 | n);
			this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.field_2606, this.screenHeight - 10, 16777215 | n);
			if (i > this.field_2606 && i < this.field_2606 + this.field_2584 && j > this.screenHeight - 10 && j < this.screenHeight) {
				drawRect(this.field_2606, this.screenHeight - 1, this.field_2606 + this.field_2584, this.screenHeight, 16777215 | n);
			}

			if (this.warningTitle != null && !this.warningTitle.isEmpty()) {
				drawRect(this.warningAlignLeft - 2, this.warningAlignTop - 2, this.warningAlignRight + 2, this.warningAlignBottom - 1, 1428160512);
				this.drawString(this.fontRenderer, this.warningTitle, this.warningAlignLeft, this.warningAlignTop, 16777215 | n);
				this.drawString(this.fontRenderer, this.warningText, (this.screenWidth - this.warningTextWidth) / 2, this.warningAlignTop + 12, 16777215 | n);
			}

			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.setOpacity(h);
			}

			super.draw(i, j, f);
			if (this.areRealmsNotificationsEnabled() && h >= 1.0F) {
				this.realmsNotificationGui.draw(i, j, f);
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
					ConfirmChatLinkScreen confirmChatLinkScreen = new ConfirmChatLinkScreen(this, this.warningLink, 13, true);
					this.client.openScreen(confirmChatLinkScreen);
					return true;
				}
			}

			if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(d, e, i)) {
				return true;
			} else {
				if (d > (double)this.field_2606 && d < (double)(this.field_2606 + this.field_2584) && e > (double)(this.screenHeight - 10) && e < (double)this.screenHeight
					)
				 {
					this.client.openScreen(new EndCreditsScreen(false, Runnables.doNothing()));
				}

				return false;
			}
		}
	}

	@Override
	public void onClosed() {
		if (this.realmsNotificationGui != null) {
			this.realmsNotificationGui.onClosed();
		}
	}
}
