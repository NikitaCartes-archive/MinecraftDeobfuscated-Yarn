package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor field_19105 = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build()
	);
	private static final Identifier field_19106 = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier field_19107 = new Identifier("textures/gui/server_selection.png");
	private final MultiplayerScreen screen;
	private final List<MultiplayerServerListWidget.ServerItem> serverItems = Lists.<MultiplayerServerListWidget.ServerItem>newArrayList();
	private final MultiplayerServerListWidget.Entry scanningEntry = new MultiplayerServerListWidget.ScanningEntry();
	private final List<MultiplayerServerListWidget.LanServerListEntry> serverEntries = Lists.<MultiplayerServerListWidget.LanServerListEntry>newArrayList();

	public MultiplayerServerListWidget(MultiplayerScreen multiplayerScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.screen = multiplayerScreen;
	}

	private void method_20131() {
		this.clearEntries();
		this.serverItems.forEach(this::addEntry);
		this.addEntry(this.scanningEntry);
		this.serverEntries.forEach(this::addEntry);
	}

	public void method_20122(MultiplayerServerListWidget.Entry entry) {
		super.setSelected(entry);
		if (this.getSelected() instanceof MultiplayerServerListWidget.ServerItem) {
			NarratorManager.INSTANCE
				.narrate(new TranslatableText("narrator.select", ((MultiplayerServerListWidget.ServerItem)this.getSelected()).server.name).getString());
		}
	}

	@Override
	protected void moveSelection(int i) {
		int j = this.children().indexOf(this.getSelected());
		int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
		MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.children().get(k);
		super.setSelected(entry);
		if (entry instanceof MultiplayerServerListWidget.ScanningEntry) {
			if (i <= 0 || k != this.getItemCount() - 1) {
				if (i >= 0 || k != 0) {
					this.moveSelection(i);
				}
			}
		} else {
			this.ensureVisible(entry);
			this.screen.updateButtonActivationStates();
		}
	}

	public void method_20125(ServerList serverList) {
		this.serverItems.clear();

		for (int i = 0; i < serverList.size(); i++) {
			this.serverItems.add(new MultiplayerServerListWidget.ServerItem(this.screen, serverList.get(i)));
		}

		this.method_20131();
	}

	public void method_20126(List<LanServerEntry> list) {
		this.serverEntries.clear();

		for (LanServerEntry lanServerEntry : list) {
			this.serverEntries.add(new MultiplayerServerListWidget.LanServerListEntry(this.screen, lanServerEntry));
		}

		this.method_20131();
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	@Override
	protected boolean isFocused() {
		return this.screen.getFocused() == this;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<MultiplayerServerListWidget.Entry> {
	}

	@Environment(EnvType.CLIENT)
	public static class LanServerListEntry extends MultiplayerServerListWidget.Entry {
		private final MultiplayerScreen screen;
		protected final MinecraftClient client;
		protected final LanServerEntry server;
		private long time;

		protected LanServerListEntry(MultiplayerScreen multiplayerScreen, LanServerEntry lanServerEntry) {
			this.screen = multiplayerScreen;
			this.server = lanServerEntry;
			this.client = MinecraftClient.getInstance();
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.client.textRenderer.draw(I18n.translate("lanServer.title"), (float)(k + 32 + 3), (float)(j + 1), 16777215);
			this.client.textRenderer.draw(this.server.getMotd(), (float)(k + 32 + 3), (float)(j + 12), 8421504);
			if (this.client.options.hideServerAddress) {
				this.client.textRenderer.draw(I18n.translate("selectServer.hiddenAddress"), (float)(k + 32 + 3), (float)(j + 12 + 11), 3158064);
			} else {
				this.client.textRenderer.draw(this.server.getAddressPort(), (float)(k + 32 + 3), (float)(j + 12 + 11), 3158064);
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			this.screen.selectEntry(this);
			if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
				this.screen.connect();
			}

			this.time = SystemUtil.getMeasuringTimeMs();
			return false;
		}

		public LanServerEntry getLanServerEntry() {
			return this.server;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ScanningEntry extends MultiplayerServerListWidget.Entry {
		private final MinecraftClient client = MinecraftClient.getInstance();

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			int p = j + m / 2 - 9 / 2;
			this.client
				.textRenderer
				.draw(
					I18n.translate("lanServer.scanning"),
					(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
					(float)p,
					16777215
				);
			String string;
			switch ((int)(SystemUtil.getMeasuringTimeMs() / 300L % 4L)) {
				case 0:
				default:
					string = "O o o";
					break;
				case 1:
				case 3:
					string = "o O o";
					break;
				case 2:
					string = "o o O";
			}

			this.client
				.textRenderer
				.draw(string, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2), (float)(p + 9), 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	public class ServerItem extends MultiplayerServerListWidget.Entry {
		private final MultiplayerScreen screen;
		private final MinecraftClient client;
		private final ServerEntry server;
		private final Identifier iconLocation;
		private String field_19122;
		private NativeImageBackedTexture iconTexture;
		private long time;

		protected ServerItem(MultiplayerScreen multiplayerScreen, ServerEntry serverEntry) {
			this.screen = multiplayerScreen;
			this.server = serverEntry;
			this.client = MinecraftClient.getInstance();
			this.iconLocation = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverEntry.address) + "/icon");
			this.iconTexture = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconLocation);
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			if (!this.server.online) {
				this.server.online = true;
				this.server.ping = -2L;
				this.server.label = "";
				this.server.playerCountLabel = "";
				MultiplayerServerListWidget.field_19105.submit(() -> {
					try {
						this.screen.method_2538().method_3003(this.server);
					} catch (UnknownHostException var2) {
						this.server.ping = -1L;
						this.server.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_resolve");
					} catch (Exception var3) {
						this.server.ping = -1L;
						this.server.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
					}
				});
			}

			boolean bl2 = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
			boolean bl3 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
			boolean bl4 = bl2 || bl3;
			this.client.textRenderer.draw(this.server.name, (float)(k + 32 + 3), (float)(j + 1), 16777215);
			List<String> list = this.client.textRenderer.wrapStringToWidthAsList(this.server.label, l - 32 - 2);

			for (int p = 0; p < Math.min(list.size(), 2); p++) {
				this.client.textRenderer.draw((String)list.get(p), (float)(k + 32 + 3), (float)(j + 12 + 9 * p), 8421504);
			}

			String string = bl4 ? Formatting.DARK_RED + this.server.version : this.server.playerCountLabel;
			int q = this.client.textRenderer.getStringWidth(string);
			this.client.textRenderer.draw(string, (float)(k + l - q - 15 - 2), (float)(j + 1), 8421504);
			int r = 0;
			String string2 = null;
			int s;
			String string3;
			if (bl4) {
				s = 5;
				string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
				string2 = this.server.playerListSummary;
			} else if (this.server.online && this.server.ping != -2L) {
				if (this.server.ping < 0L) {
					s = 5;
				} else if (this.server.ping < 150L) {
					s = 0;
				} else if (this.server.ping < 300L) {
					s = 1;
				} else if (this.server.ping < 600L) {
					s = 2;
				} else if (this.server.ping < 1000L) {
					s = 3;
				} else {
					s = 4;
				}

				if (this.server.ping < 0L) {
					string3 = I18n.translate("multiplayer.status.no_connection");
				} else {
					string3 = this.server.ping + "ms";
					string2 = this.server.playerListSummary;
				}
			} else {
				r = 1;
				s = (int)(SystemUtil.getMeasuringTimeMs() / 100L + (long)(i * 2) & 7L);
				if (s > 4) {
					s = 8 - s;
				}

				string3 = I18n.translate("multiplayer.status.pinging");
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
			DrawableHelper.blit(k + l - 15, j, (float)(r * 10), (float)(176 + s * 8), 10, 8, 256, 256);
			if (this.server.getIcon() != null && !this.server.getIcon().equals(this.field_19122)) {
				this.field_19122 = this.server.getIcon();
				this.method_20137();
				this.screen.getServerList().saveFile();
			}

			if (this.iconTexture != null) {
				this.method_20134(k, j, this.iconLocation);
			} else {
				this.method_20134(k, j, MultiplayerServerListWidget.field_19106);
			}

			int t = n - k;
			int u = o - j;
			if (t >= l - 15 && t <= l - 5 && u >= 0 && u <= 8) {
				this.screen.setTooltip(string3);
			} else if (t >= l - q - 15 - 2 && t <= l - 15 - 2 && u >= 0 && u <= 8) {
				this.screen.setTooltip(string2);
			}

			if (this.client.options.touchscreen || bl) {
				this.client.getTextureManager().bindTexture(MultiplayerServerListWidget.field_19107);
				DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int v = n - k;
				int w = o - j;
				if (this.method_20136()) {
					if (v < 32 && v > 16) {
						DrawableHelper.blit(k, j, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (i > 0) {
					if (v < 16 && w < 16) {
						DrawableHelper.blit(k, j, 96.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.blit(k, j, 96.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (i < this.screen.getServerList().size() - 1) {
					if (v < 16 && w > 16) {
						DrawableHelper.blit(k, j, 64.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.blit(k, j, 64.0F, 0.0F, 32, 32, 256, 256);
					}
				}
			}
		}

		protected void method_20134(int i, int j, Identifier identifier) {
			this.client.getTextureManager().bindTexture(identifier);
			GlStateManager.enableBlend();
			DrawableHelper.blit(i, j, 0.0F, 0.0F, 32, 32, 32, 32);
			GlStateManager.disableBlend();
		}

		private boolean method_20136() {
			return true;
		}

		private void method_20137() {
			String string = this.server.getIcon();
			if (string == null) {
				this.client.getTextureManager().destroyTexture(this.iconLocation);
				if (this.iconTexture != null && this.iconTexture.getImage() != null) {
					this.iconTexture.getImage().close();
				}

				this.iconTexture = null;
			} else {
				try {
					NativeImage nativeImage = NativeImage.fromBase64(string);
					Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
					Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
					if (this.iconTexture == null) {
						this.iconTexture = new NativeImageBackedTexture(nativeImage);
					} else {
						this.iconTexture.setImage(nativeImage);
						this.iconTexture.upload();
					}

					this.client.getTextureManager().registerTexture(this.iconLocation, this.iconTexture);
				} catch (Throwable var3) {
					MultiplayerServerListWidget.LOGGER.error("Invalid icon for server {} ({})", this.server.name, this.server.address, var3);
					this.server.setIcon(null);
				}
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			double f = d - (double)MultiplayerServerListWidget.this.getRowLeft();
			double g = e - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
			if (f <= 32.0) {
				if (f < 32.0 && f > 16.0 && this.method_20136()) {
					this.screen.selectEntry(this);
					this.screen.connect();
					return true;
				}

				int j = this.screen.serverListWidget.children().indexOf(this);
				if (f < 16.0 && g < 16.0 && j > 0) {
					int k = Screen.hasShiftDown() ? 0 : j - 1;
					this.screen.getServerList().swapEntries(j, k);
					if (this.screen.serverListWidget.getSelected() == this) {
						this.screen.selectEntry(this);
					}

					this.screen.serverListWidget.method_20125(this.screen.getServerList());
					return true;
				}

				if (f < 16.0 && g > 16.0 && j < this.screen.getServerList().size() - 1) {
					ServerList serverList = this.screen.getServerList();
					int l = Screen.hasShiftDown() ? serverList.size() - 1 : j + 1;
					serverList.swapEntries(j, l);
					if (this.screen.serverListWidget.getSelected() == this) {
						this.screen.selectEntry(this);
					}

					this.screen.serverListWidget.method_20125(serverList);
					return true;
				}
			}

			this.screen.selectEntry(this);
			if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
				this.screen.connect();
			}

			this.time = SystemUtil.getMeasuringTimeMs();
			return false;
		}

		public ServerEntry getServer() {
			return this.server;
		}
	}
}
