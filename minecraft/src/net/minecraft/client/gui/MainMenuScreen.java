package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_751;
import net.minecraft.class_766;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.SettingsScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.menu.settings.LanguageSettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LanguageButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resource.Resource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends Screen {
	private static final Random RANDOM = new Random();
	private final float field_2605;
	private String splashText;
	private ButtonWidget field_2602;
	private ButtonWidget buttonResetDemo;
	private final Object mutex = new Object();
	public static final String OUTDATED_GL_TEXT = "Please click " + TextFormat.UNDERLINE + "here" + TextFormat.RESET + " for more information.";
	private int warningTextWidth;
	private int warningTitleWidth;
	private int warningAlignLeft;
	private int warningAlignTop;
	private int warningAlignRight;
	private int warningAlignBottom;
	private String warningTitle;
	private String warningText = OUTDATED_GL_TEXT;
	private String warningLink;
	private static final Identifier SPLASHES_LOC = new Identifier("texts/splashes.txt");
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private boolean field_2599;
	private Screen realmsNotificationGui;
	private int field_2584;
	private int field_2606;
	private final class_766 field_2585 = new class_766(new class_751(new Identifier("textures/gui/title/background/panorama")));

	public MainMenuScreen() {
		this.splashText = "missingno";
		Resource resource = null;

		try {
			List<String> list = Lists.<String>newArrayList();
			resource = MinecraftClient.getInstance().getResourceManager().getResource(SPLASHES_LOC);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

			String string;
			while ((string = bufferedReader.readLine()) != null) {
				string = string.trim();
				if (!string.isEmpty()) {
					list.add(string);
				}
			}

			if (!list.isEmpty()) {
				do {
					this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
				} while (this.splashText.hashCode() == 125780783);
			}
		} catch (IOException var8) {
		} finally {
			IOUtils.closeQuietly(resource);
		}

		this.field_2605 = RANDOM.nextFloat();
		this.warningTitle = "";
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.warningTitle = I18n.translate("title.oldgl1");
			this.warningText = I18n.translate("title.oldgl2");
			this.warningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean areRealmsNotificationsEnabled() {
		return MinecraftClient.getInstance().options.isEnabled(GameOptions.Option.REALMS_NOTIFICATIONS) && this.realmsNotificationGui != null;
	}

	@Override
	public void update() {
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.update();
		}
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
		this.field_2584 = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
		this.field_2606 = this.width - this.field_2584 - 2;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int i = 24;
		int j = this.height / 4 + 48;
		if (this.client.isDemo()) {
			this.initWidgetsDemo(j, 24);
		} else {
			this.initWidgetsNormal(j, 24);
		}

		this.field_2602 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new SettingsScreen(MainMenuScreen.this, MainMenuScreen.this.client.options));
			}
		});
		this.addButton(new ButtonWidget(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.translate("menu.quit")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.scheduleStop();
			}
		});
		this.addButton(
			new LanguageButtonWidget(5, this.width / 2 - 124, j + 72 + 12) {
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
			this.warningAlignLeft = (this.width - k) / 2;
			this.warningAlignTop = j - 24;
			this.warningAlignRight = this.warningAlignLeft + k;
			this.warningAlignBottom = this.warningAlignTop + 24;
		}

		this.client.setConnectedToRealms(false);
		if (MinecraftClient.getInstance().options.isEnabled(GameOptions.Option.REALMS_NOTIFICATIONS) && !this.field_2599) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.realmsNotificationGui = realmsBridge.getNotificationScreen(this);
			this.field_2599 = true;
		}

		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.initialize(this.client, this.width, this.height);
		}
	}

	private void initWidgetsNormal(int i, int j) {
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, i, I18n.translate("menu.singleplayer")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new LevelSelectScreen(MainMenuScreen.this));
			}
		});
		this.addButton(new ButtonWidget(2, this.width / 2 - 100, i + j * 1, I18n.translate("menu.multiplayer")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.openScreen(new MultiplayerScreen(MainMenuScreen.this));
			}
		});
		this.addButton(new ButtonWidget(14, this.width / 2 - 100, i + j * 2, I18n.translate("menu.online")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.switchToRealms();
			}
		});
	}

	private void initWidgetsDemo(int i, int j) {
		this.addButton(new ButtonWidget(11, this.width / 2 - 100, i, I18n.translate("menu.playdemo")) {
			@Override
			public void onPressed(double d, double e) {
				MainMenuScreen.this.client.startIntegratedServer("Demo_World", "Demo_World", MinecraftServer.field_17704);
			}
		});
		this.buttonResetDemo = this.addButton(
			new ButtonWidget(12, this.width / 2 - 100, i + j * 1, I18n.translate("menu.resetdemo")) {
				@Override
				public void onPressed(double d, double e) {
					LevelStorage levelStorage = MainMenuScreen.this.client.getLevelStorage();
					LevelProperties levelProperties = levelStorage.requiresConversion("Demo_World");
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
		LevelProperties levelProperties = levelStorage.requiresConversion("Demo_World");
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
			levelStorage.delete("Demo_World");
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
		this.field_2585.method_3317(f);
		int k = 274;
		int l = this.width / 2 - 137;
		int m = 30;
		this.client.getTextureManager().bindTexture(new Identifier("textures/gui/title/background/panorama_overlay.png"));
		drawTexturedRect(0, 0, 0.0F, 0.0F, 16, 128, this.width, this.height, 16.0F, 128.0F);
		this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if ((double)this.field_2605 < 1.0E-4) {
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
		GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
		GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float g = 1.8F - MathHelper.abs(MathHelper.sin((float)(SystemUtil.getMeasuringTimeMs() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
		g = g * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
		GlStateManager.scalef(g, g, g);
		this.drawStringCentered(this.fontRenderer, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();
		String string = "Minecraft " + SharedConstants.getGameVersion().getName();
		if (this.client.isDemo()) {
			string = string + " Demo";
		} else {
			string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
		}

		this.drawString(this.fontRenderer, string, 2, this.height - 10, -1);
		this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.field_2606, this.height - 10, -1);
		if (i > this.field_2606 && i < this.field_2606 + this.field_2584 && j > this.height - 10 && j < this.height) {
			drawRect(this.field_2606, this.height - 1, this.field_2606 + this.field_2584, this.height, -1);
		}

		if (this.warningTitle != null && !this.warningTitle.isEmpty()) {
			drawRect(this.warningAlignLeft - 2, this.warningAlignTop - 2, this.warningAlignRight + 2, this.warningAlignBottom - 1, 1428160512);
			this.drawString(this.fontRenderer, this.warningTitle, this.warningAlignLeft, this.warningAlignTop, -1);
			this.drawString(this.fontRenderer, this.warningText, (this.width - this.warningTextWidth) / 2, this.warningAlignTop + 12, -1);
		}

		super.draw(i, j, f);
		if (this.areRealmsNotificationsEnabled()) {
			this.realmsNotificationGui.draw(i, j, f);
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
				if (d > (double)this.field_2606 && d < (double)(this.field_2606 + this.field_2584) && e > (double)(this.height - 10) && e < (double)this.height) {
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
