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
	private static final Identifier UNKNOWN_SERVER = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier SERVER_SELECTION = new Identifier("textures/gui/server_selection.png");
	private final MultiplayerScreen guiMultiplayer;
	private final MinecraftClient client;
	private final ServerEntry serverEntry;
	private final Identifier serverIconTextureId;
	private String field_3062;
	private NativeImageBackedTexture serverIconTexture;
	private long field_3067;

	protected RemoteServerListEntry(MultiplayerScreen multiplayerScreen, ServerEntry serverEntry) {
		this.guiMultiplayer = multiplayerScreen;
		this.serverEntry = serverEntry;
		this.client = MinecraftClient.getInstance();
		this.serverIconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverEntry.address) + "/icon");
		this.serverIconTexture = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.serverIconTextureId);
	}

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY();
		int n = this.getX();
		if (!this.serverEntry.field_3754) {
			this.serverEntry.field_3754 = true;
			this.serverEntry.ping = -2L;
			this.serverEntry.label = "";
			this.serverEntry.playerCountLabel = "";
			PING_THREAD_POOL.submit(() -> {
				try {
					this.guiMultiplayer.method_2538().method_3003(this.serverEntry);
				} catch (UnknownHostException var2) {
					this.serverEntry.ping = -1L;
					this.serverEntry.label = TextFormat.field_1079 + I18n.translate("multiplayer.status.cannot_resolve");
				} catch (Exception var3) {
					this.serverEntry.ping = -1L;
					this.serverEntry.label = TextFormat.field_1079 + I18n.translate("multiplayer.status.cannot_connect");
				}
			});
		}

		boolean bl2 = this.serverEntry.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
		boolean bl3 = this.serverEntry.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
		boolean bl4 = bl2 || bl3;
		this.client.textRenderer.draw(this.serverEntry.name, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		List<String> list = this.client.textRenderer.wrapStringToWidthAsList(this.serverEntry.label, i - 32 - 2);

		for (int o = 0; o < Math.min(list.size(), 2); o++) {
			this.client.textRenderer.draw((String)list.get(o), (float)(n + 32 + 3), (float)(m + 12 + 9 * o), 8421504);
		}

		String string = bl4 ? TextFormat.field_1079 + this.serverEntry.version : this.serverEntry.playerCountLabel;
		int p = this.client.textRenderer.getStringWidth(string);
		this.client.textRenderer.draw(string, (float)(n + i - p - 15 - 2), (float)(m + 1), 8421504);
		int q = 0;
		String string2 = null;
		int r;
		String string3;
		if (bl4) {
			r = 5;
			string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			string2 = this.serverEntry.playerListSummary;
		} else if (this.serverEntry.field_3754 && this.serverEntry.ping != -2L) {
			if (this.serverEntry.ping < 0L) {
				r = 5;
			} else if (this.serverEntry.ping < 150L) {
				r = 0;
			} else if (this.serverEntry.ping < 300L) {
				r = 1;
			} else if (this.serverEntry.ping < 600L) {
				r = 2;
			} else if (this.serverEntry.ping < 1000L) {
				r = 3;
			} else {
				r = 4;
			}

			if (this.serverEntry.ping < 0L) {
				string3 = I18n.translate("multiplayer.status.no_connection");
			} else {
				string3 = this.serverEntry.ping + "ms";
				string2 = this.serverEntry.playerListSummary;
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
		this.client.getTextureManager().bindTexture(DrawableHelper.ICONS);
		DrawableHelper.drawTexturedRect(n + i - 15, m, (float)(q * 10), (float)(176 + r * 8), 10, 8, 256.0F, 256.0F);
		if (this.serverEntry.getIcon() != null && !this.serverEntry.getIcon().equals(this.field_3062)) {
			this.field_3062 = this.serverEntry.getIcon();
			this.method_2554();
			this.guiMultiplayer.method_2529().saveFile();
		}

		if (this.serverIconTexture != null) {
			this.drawIcon(n, m, this.serverIconTextureId);
		} else {
			this.drawIcon(n, m, UNKNOWN_SERVER);
		}

		int s = k - n;
		int t = l - m;
		if (s >= i - 15 && s <= i - 5 && t >= 0 && t <= 8) {
			this.guiMultiplayer.method_2528(string3);
		} else if (s >= i - p - 15 - 2 && s <= i - 15 - 2 && t >= 0 && t <= 8) {
			this.guiMultiplayer.method_2528(string2);
		}

		if (this.client.options.touchscreen || bl) {
			this.client.getTextureManager().bindTexture(SERVER_SELECTION);
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

	protected void drawIcon(int i, int j, Identifier identifier) {
		this.client.getTextureManager().bindTexture(identifier);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexturedRect(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private boolean method_2558() {
		return true;
	}

	private void method_2554() {
		String string = this.serverEntry.getIcon();
		if (string == null) {
			this.client.getTextureManager().destroyTexture(this.serverIconTextureId);
			if (this.serverIconTexture != null && this.serverIconTexture.getImage() != null) {
				this.serverIconTexture.getImage().close();
			}

			this.serverIconTexture = null;
		} else {
			try {
				NativeImage nativeImage = NativeImage.fromBase64(string);
				Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
				if (this.serverIconTexture == null) {
					this.serverIconTexture = new NativeImageBackedTexture(nativeImage);
				} else {
					this.serverIconTexture.setImage(nativeImage);
					this.serverIconTexture.upload();
				}

				this.client.getTextureManager().registerTexture(this.serverIconTextureId, this.serverIconTexture);
			} catch (Throwable var3) {
				LOGGER.error("Invalid icon for server {} ({})", this.serverEntry.name, this.serverEntry.address, var3);
				this.serverEntry.setIcon(null);
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

	public ServerEntry getServerEntry() {
		return this.serverEntry;
	}
}
