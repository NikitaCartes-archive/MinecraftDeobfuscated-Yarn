package net.minecraft.client.gui.widget;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RemoteServerListEntry extends ServerListWidget.Entry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor PING_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build()
	);
	private static final Identifier field_3060 = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier field_3059 = new Identifier("textures/gui/server_selection.png");
	private final MultiplayerScreen guiMultiplayer;
	private final MinecraftClient client;
	private final ServerEntry field_3061;
	private final Identifier field_3065;
	private String field_3062;
	private NativeImageBackedTexture field_3063;
	private long field_3067;

	protected RemoteServerListEntry(MultiplayerScreen multiplayerScreen, ServerEntry serverEntry) {
		this.guiMultiplayer = multiplayerScreen;
		this.field_3061 = serverEntry;
		this.client = MinecraftClient.getInstance();
		this.field_3065 = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverEntry.address) + "/icon");
		this.field_3063 = (NativeImageBackedTexture)this.client.method_1531().method_4619(this.field_3065);
	}

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY();
		int n = this.getX();
		if (!this.field_3061.field_3754) {
			this.field_3061.field_3754 = true;
			this.field_3061.ping = -2L;
			this.field_3061.label = "";
			this.field_3061.playerCountLabel = "";
			PING_THREAD_POOL.submit(() -> {
				try {
					this.guiMultiplayer.method_2538().method_3003(this.field_3061);
				} catch (UnknownHostException var2) {
					this.field_3061.ping = -1L;
					this.field_3061.label = TextFormat.field_1079 + I18n.translate("multiplayer.status.cannot_resolve");
				} catch (Exception var3) {
					this.field_3061.ping = -1L;
					this.field_3061.label = TextFormat.field_1079 + I18n.translate("multiplayer.status.cannot_connect");
				}
			});
		}

		boolean bl2 = this.field_3061.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
		boolean bl3 = this.field_3061.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
		boolean bl4 = bl2 || bl3;
		this.client.field_1772.draw(this.field_3061.name, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		List<String> list = this.client.field_1772.wrapStringToWidthAsList(this.field_3061.label, i - 32 - 2);

		for (int o = 0; o < Math.min(list.size(), 2); o++) {
			this.client.field_1772.draw((String)list.get(o), (float)(n + 32 + 3), (float)(m + 12 + 9 * o), 8421504);
		}

		String string = bl4 ? TextFormat.field_1079 + this.field_3061.version : this.field_3061.playerCountLabel;
		int p = this.client.field_1772.getStringWidth(string);
		this.client.field_1772.draw(string, (float)(n + i - p - 15 - 2), (float)(m + 1), 8421504);
		int q = 0;
		String string2 = null;
		int r;
		String string3;
		if (bl4) {
			r = 5;
			string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			string2 = this.field_3061.playerListSummary;
		} else if (this.field_3061.field_3754 && this.field_3061.ping != -2L) {
			if (this.field_3061.ping < 0L) {
				r = 5;
			} else if (this.field_3061.ping < 150L) {
				r = 0;
			} else if (this.field_3061.ping < 300L) {
				r = 1;
			} else if (this.field_3061.ping < 600L) {
				r = 2;
			} else if (this.field_3061.ping < 1000L) {
				r = 3;
			} else {
				r = 4;
			}

			if (this.field_3061.ping < 0L) {
				string3 = I18n.translate("multiplayer.status.no_connection");
			} else {
				string3 = this.field_3061.ping + "ms";
				string2 = this.field_3061.playerListSummary;
			}
		} else {
			q = 1;
			r = (int)(SystemUtil.getMeasuringTimeMs() / 100L + (long)(this.method_1908() * 2) & 7L);
			if (r > 4) {
				r = 8 - r;
			}

			string3 = I18n.translate("multiplayer.status.pinging");
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(DrawableHelper.field_2053);
		DrawableHelper.drawTexturedRect(n + i - 15, m, (float)(q * 10), (float)(176 + r * 8), 10, 8, 256.0F, 256.0F);
		if (this.field_3061.getIcon() != null && !this.field_3061.getIcon().equals(this.field_3062)) {
			this.field_3062 = this.field_3061.getIcon();
			this.method_2554();
			this.guiMultiplayer.method_2529().saveFile();
		}

		if (this.field_3063 != null) {
			this.method_2557(n, m, this.field_3065);
		} else {
			this.method_2557(n, m, field_3060);
		}

		int s = k - n;
		int t = l - m;
		if (s >= i - 15 && s <= i - 5 && t >= 0 && t <= 8) {
			this.guiMultiplayer.method_2528(string3);
		} else if (s >= i - p - 15 - 2 && s <= i - 15 - 2 && t >= 0 && t <= 8) {
			this.guiMultiplayer.method_2528(string2);
		}

		if (this.client.field_1690.touchscreen || bl) {
			this.client.method_1531().method_4618(field_3059);
			DrawableHelper.drawRect(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int u = k - n;
			int v = l - m;
			if (this.method_2558()) {
				if (u < 32 && u > 16) {
					DrawableHelper.drawTexturedRect(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexturedRect(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.guiMultiplayer.method_2533(this, this.method_1908())) {
				if (u < 16 && v < 16) {
					DrawableHelper.drawTexturedRect(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexturedRect(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.guiMultiplayer.method_2547(this, this.method_1908())) {
				if (u < 16 && v > 16) {
					DrawableHelper.drawTexturedRect(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					DrawableHelper.drawTexturedRect(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}
		}
	}

	protected void method_2557(int i, int j, Identifier identifier) {
		this.client.method_1531().method_4618(identifier);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexturedRect(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private boolean method_2558() {
		return true;
	}

	private void method_2554() {
		String string = this.field_3061.getIcon();
		if (string == null) {
			this.client.method_1531().method_4615(this.field_3065);
			if (this.field_3063 != null && this.field_3063.getImage() != null) {
				this.field_3063.getImage().close();
			}

			this.field_3063 = null;
		} else {
			try {
				NativeImage nativeImage = NativeImage.fromBase64(string);
				Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
				if (this.field_3063 == null) {
					this.field_3063 = new NativeImageBackedTexture(nativeImage);
				} else {
					this.field_3063.setImage(nativeImage);
					this.field_3063.upload();
				}

				this.client.method_1531().method_4616(this.field_3065, this.field_3063);
			} catch (Throwable var3) {
				LOGGER.error("Invalid icon for server {} ({})", this.field_3061.name, this.field_3061.address, var3);
				this.field_3061.setIcon(null);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		double f = d - (double)this.getX();
		double g = e - (double)this.getY();
		if (f <= 32.0) {
			if (f < 32.0 && f > 16.0 && this.method_2558()) {
				this.guiMultiplayer.setIndex(this.method_1908());
				this.guiMultiplayer.method_2536();
				return true;
			}

			if (f < 16.0 && g < 16.0 && this.guiMultiplayer.method_2533(this, this.method_1908())) {
				this.guiMultiplayer.method_2531(this, this.method_1908(), Screen.isShiftPressed());
				return true;
			}

			if (f < 16.0 && g > 16.0 && this.guiMultiplayer.method_2547(this, this.method_1908())) {
				this.guiMultiplayer.method_2553(this, this.method_1908(), Screen.isShiftPressed());
				return true;
			}
		}

		this.guiMultiplayer.setIndex(this.method_1908());
		if (SystemUtil.getMeasuringTimeMs() - this.field_3067 < 250L) {
			this.guiMultiplayer.method_2536();
		}

		this.field_3067 = SystemUtil.getMeasuringTimeMs();
		return false;
	}

	public ServerEntry method_2556() {
		return this.field_3061;
	}
}
