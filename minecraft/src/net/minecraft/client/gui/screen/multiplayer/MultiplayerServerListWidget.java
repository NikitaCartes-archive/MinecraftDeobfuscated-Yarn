package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build()
	);
	private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
	private final MultiplayerScreen screen;
	private final List<MultiplayerServerListWidget.ServerEntry> servers = Lists.<MultiplayerServerListWidget.ServerEntry>newArrayList();
	private final MultiplayerServerListWidget.Entry scanningEntry = new MultiplayerServerListWidget.ScanningEntry();
	private final List<MultiplayerServerListWidget.LanServerEntry> lanServers = Lists.<MultiplayerServerListWidget.LanServerEntry>newArrayList();

	public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
		this.screen = screen;
	}

	private void updateEntries() {
		this.clearEntries();
		this.servers.forEach(this::addEntry);
		this.addEntry(this.scanningEntry);
		this.lanServers.forEach(this::addEntry);
	}

	public void setSelected(MultiplayerServerListWidget.Entry entry) {
		super.setSelected(entry);
		if (this.getSelected() instanceof MultiplayerServerListWidget.ServerEntry) {
			NarratorManager.INSTANCE
				.narrate(new TranslatableText("narrator.select", ((MultiplayerServerListWidget.ServerEntry)this.getSelected()).server.name).getString());
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		MultiplayerServerListWidget.Entry entry = this.getSelected();
		return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void moveSelection(int amount) {
		int i = this.children().indexOf(this.getSelected());
		int j = MathHelper.clamp(i + amount, 0, this.getItemCount() - 1);
		MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.children().get(j);
		if (entry instanceof MultiplayerServerListWidget.ScanningEntry) {
			j = MathHelper.clamp(j + (amount > 0 ? 1 : -1), 0, this.getItemCount() - 1);
			entry = (MultiplayerServerListWidget.Entry)this.children().get(j);
		}

		super.setSelected(entry);
		this.ensureVisible(entry);
		this.screen.updateButtonActivationStates();
	}

	public void setServers(ServerList servers) {
		this.servers.clear();

		for (int i = 0; i < servers.size(); i++) {
			this.servers.add(new MultiplayerServerListWidget.ServerEntry(this.screen, servers.get(i)));
		}

		this.updateEntries();
	}

	public void setLanServers(List<LanServerInfo> lanServers) {
		this.lanServers.clear();

		for (LanServerInfo lanServerInfo : lanServers) {
			this.lanServers.add(new MultiplayerServerListWidget.LanServerEntry(this.screen, lanServerInfo));
		}

		this.updateEntries();
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 30;
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
	public static class LanServerEntry extends MultiplayerServerListWidget.Entry {
		private final MultiplayerScreen screen;
		protected final MinecraftClient client;
		protected final LanServerInfo server;
		private long time;

		protected LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
			this.screen = screen;
			this.server = server;
			this.client = MinecraftClient.getInstance();
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.client.textRenderer.draw(I18n.translate("lanServer.title"), (float)(x + 32 + 3), (float)(y + 1), 16777215);
			this.client.textRenderer.draw(this.server.getMotd(), (float)(x + 32 + 3), (float)(y + 12), 8421504);
			if (this.client.options.hideServerAddress) {
				this.client.textRenderer.draw(I18n.translate("selectServer.hiddenAddress"), (float)(x + 32 + 3), (float)(y + 12 + 11), 3158064);
			} else {
				this.client.textRenderer.draw(this.server.getAddressPort(), (float)(x + 32 + 3), (float)(y + 12 + 11), 3158064);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.screen.select(this);
			if (Util.getMeasuringTimeMs() - this.time < 250L) {
				this.screen.connect();
			}

			this.time = Util.getMeasuringTimeMs();
			return false;
		}

		public LanServerInfo getLanServerEntry() {
			return this.server;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ScanningEntry extends MultiplayerServerListWidget.Entry {
		private final MinecraftClient client = MinecraftClient.getInstance();

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			int i = y + height / 2 - 9 / 2;
			this.client
				.textRenderer
				.draw(
					I18n.translate("lanServer.scanning"),
					(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
					(float)i,
					16777215
				);
			String string;
			switch ((int)(Util.getMeasuringTimeMs() / 300L % 4L)) {
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
				.draw(string, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2), (float)(i + 9), 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	public class ServerEntry extends MultiplayerServerListWidget.Entry {
		private final MultiplayerScreen screen;
		private final MinecraftClient client;
		private final ServerInfo server;
		private final Identifier iconTextureId;
		private String iconUri;
		private NativeImageBackedTexture icon;
		private long time;

		protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
			this.screen = screen;
			this.server = server;
			this.client = MinecraftClient.getInstance();
			this.iconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(server.address) + "/icon");
			this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			if (!this.server.online) {
				this.server.online = true;
				this.server.ping = -2L;
				this.server.label = "";
				this.server.playerCountLabel = "";
				MultiplayerServerListWidget.SERVER_PINGER_THREAD_POOL.submit(() -> {
					try {
						this.screen.getServerListPinger().add(this.server);
					} catch (UnknownHostException var2) {
						this.server.ping = -1L;
						this.server.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_resolve");
					} catch (Exception var3) {
						this.server.ping = -1L;
						this.server.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
					}
				});
			}

			boolean bl = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
			boolean bl2 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
			boolean bl3 = bl || bl2;
			this.client.textRenderer.draw(this.server.name, (float)(x + 32 + 3), (float)(y + 1), 16777215);
			List<String> list = this.client.textRenderer.wrapStringToWidthAsList(this.server.label, width - 32 - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				this.client.textRenderer.draw((String)list.get(i), (float)(x + 32 + 3), (float)(y + 12 + 9 * i), 8421504);
			}

			String string = bl3 ? Formatting.DARK_RED + this.server.version : this.server.playerCountLabel;
			int j = this.client.textRenderer.getStringWidth(string);
			this.client.textRenderer.draw(string, (float)(x + width - j - 15 - 2), (float)(y + 1), 8421504);
			int k = 0;
			String string2 = null;
			int l;
			String string3;
			if (bl3) {
				l = 5;
				string3 = I18n.translate(bl ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
				string2 = this.server.playerListSummary;
			} else if (this.server.online && this.server.ping != -2L) {
				if (this.server.ping < 0L) {
					l = 5;
				} else if (this.server.ping < 150L) {
					l = 0;
				} else if (this.server.ping < 300L) {
					l = 1;
				} else if (this.server.ping < 600L) {
					l = 2;
				} else if (this.server.ping < 1000L) {
					l = 3;
				} else {
					l = 4;
				}

				if (this.server.ping < 0L) {
					string3 = I18n.translate("multiplayer.status.no_connection");
				} else {
					string3 = this.server.ping + "ms";
					string2 = this.server.playerListSummary;
				}
			} else {
				k = 1;
				l = (int)(Util.getMeasuringTimeMs() / 100L + (long)(index * 2) & 7L);
				if (l > 4) {
					l = 8 - l;
				}

				string3 = I18n.translate("multiplayer.status.pinging");
			}

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
			DrawableHelper.drawTexture(x + width - 15, y, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
			if (this.server.getIcon() != null && !this.server.getIcon().equals(this.iconUri)) {
				this.iconUri = this.server.getIcon();
				this.updateIcon();
				this.screen.getServerList().saveFile();
			}

			if (this.icon != null) {
				this.draw(x, y, this.iconTextureId);
			} else {
				this.draw(x, y, MultiplayerServerListWidget.UNKNOWN_SERVER_TEXTURE);
			}

			int m = mouseX - x;
			int n = mouseY - y;
			if (m >= width - 15 && m <= width - 5 && n >= 0 && n <= 8) {
				this.screen.setTooltip(string3);
			} else if (m >= width - j - 15 - 2 && m <= width - 15 - 2 && n >= 0 && n <= 8) {
				this.screen.setTooltip(string2);
			}

			if (this.client.options.touchscreen || hovering) {
				this.client.getTextureManager().bindTexture(MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE);
				DrawableHelper.fill(x, y, x + 32, y + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int o = mouseX - x;
				int p = mouseY - y;
				if (this.method_20136()) {
					if (o < 32 && o > 16) {
						DrawableHelper.drawTexture(x, y, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (index > 0) {
					if (o < 16 && p < 16) {
						DrawableHelper.drawTexture(x, y, 96.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(x, y, 96.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (index < this.screen.getServerList().size() - 1) {
					if (o < 16 && p > 16) {
						DrawableHelper.drawTexture(x, y, 64.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(x, y, 64.0F, 0.0F, 32, 32, 256, 256);
					}
				}
			}
		}

		protected void draw(int x, int y, Identifier textureId) {
			this.client.getTextureManager().bindTexture(textureId);
			RenderSystem.enableBlend();
			DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
		}

		private boolean method_20136() {
			return true;
		}

		private void updateIcon() {
			String string = this.server.getIcon();
			if (string == null) {
				this.client.getTextureManager().destroyTexture(this.iconTextureId);
				if (this.icon != null && this.icon.getImage() != null) {
					this.icon.getImage().close();
				}

				this.icon = null;
			} else {
				try {
					NativeImage nativeImage = NativeImage.read(string);
					Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
					Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
					if (this.icon == null) {
						this.icon = new NativeImageBackedTexture(nativeImage);
					} else {
						this.icon.setImage(nativeImage);
						this.icon.upload();
					}

					this.client.getTextureManager().registerTexture(this.iconTextureId, this.icon);
				} catch (Throwable var3) {
					MultiplayerServerListWidget.LOGGER.error("Invalid icon for server {} ({})", this.server.name, this.server.address, var3);
					this.server.setIcon(null);
				}
			}
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (Screen.hasShiftDown()) {
				MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
				int i = multiplayerServerListWidget.children().indexOf(this);
				if (keyCode == 264 && i < this.screen.getServerList().size() - 1 || keyCode == 265 && i > 0) {
					this.swapEntries(i, keyCode == 264 ? i + 1 : i - 1);
					return true;
				}
			}

			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		private void swapEntries(int i, int j) {
			this.screen.getServerList().swapEntries(i, j);
			this.screen.serverListWidget.setServers(this.screen.getServerList());
			MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.screen.serverListWidget.children().get(j);
			this.screen.serverListWidget.setSelected(entry);
			MultiplayerServerListWidget.this.ensureVisible(entry);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d = mouseX - (double)MultiplayerServerListWidget.this.getRowLeft();
			double e = mouseY - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
			if (d <= 32.0) {
				if (d < 32.0 && d > 16.0 && this.method_20136()) {
					this.screen.select(this);
					this.screen.connect();
					return true;
				}

				int i = this.screen.serverListWidget.children().indexOf(this);
				if (d < 16.0 && e < 16.0 && i > 0) {
					this.swapEntries(i, i - 1);
					return true;
				}

				if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size() - 1) {
					this.swapEntries(i, i + 1);
					return true;
				}
			}

			this.screen.select(this);
			if (Util.getMeasuringTimeMs() - this.time < 250L) {
				this.screen.connect();
			}

			this.time = Util.getMeasuringTimeMs();
			return false;
		}

		public ServerInfo getServer() {
			return this.server;
		}
	}
}
