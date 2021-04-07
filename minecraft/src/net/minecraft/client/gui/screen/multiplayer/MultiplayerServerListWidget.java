package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build()
	);
	private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
	private static final Text LAN_SCANNING_TEXT = new TranslatableText("lanServer.scanning");
	private static final Text CANNOT_RESOLVE_TEXT = new TranslatableText("multiplayer.status.cannot_resolve").formatted(Formatting.DARK_RED);
	private static final Text CANNOT_CONNECT_TEXT = new TranslatableText("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
	private static final Text INCOMPATIBLE_TEXT = new TranslatableText("multiplayer.status.incompatible");
	private static final Text NO_CONNECTION_TEXT = new TranslatableText("multiplayer.status.no_connection");
	private static final Text PINGING_TEXT = new TranslatableText("multiplayer.status.pinging");
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

	public void setSelected(@Nullable MultiplayerServerListWidget.Entry entry) {
		super.setSelected(entry);
		if (this.getSelected() instanceof MultiplayerServerListWidget.ServerEntry) {
			NarratorManager.INSTANCE
				.narrate(new TranslatableText("narrator.select", ((MultiplayerServerListWidget.ServerEntry)this.getSelected()).server.name).getString());
		}

		this.screen.updateButtonActivationStates();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		MultiplayerServerListWidget.Entry entry = this.getSelected();
		return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void moveSelection(EntryListWidget.MoveDirection direction) {
		this.moveSelectionIf(direction, entry -> !(entry instanceof MultiplayerServerListWidget.ScanningEntry));
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
		private static final int field_32386 = 32;
		private static final Text TITLE_TEXT = new TranslatableText("lanServer.title");
		private static final Text HIDDEN_ADDRESS_TEXT = new TranslatableText("selectServer.hiddenAddress");
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.client.textRenderer.draw(matrices, TITLE_TEXT, (float)(x + 32 + 3), (float)(y + 1), 16777215);
			this.client.textRenderer.draw(matrices, this.server.getMotd(), (float)(x + 32 + 3), (float)(y + 12), 8421504);
			if (this.client.options.hideServerAddress) {
				this.client.textRenderer.draw(matrices, HIDDEN_ADDRESS_TEXT, (float)(x + 32 + 3), (float)(y + 12 + 11), 3158064);
			} else {
				this.client.textRenderer.draw(matrices, this.server.getAddressPort(), (float)(x + 32 + 3), (float)(y + 12 + 11), 3158064);
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = y + entryHeight / 2 - 9 / 2;
			this.client
				.textRenderer
				.draw(
					matrices,
					MultiplayerServerListWidget.LAN_SCANNING_TEXT,
					(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(MultiplayerServerListWidget.LAN_SCANNING_TEXT) / 2),
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
				.draw(matrices, string, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2), (float)(i + 9), 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	public class ServerEntry extends MultiplayerServerListWidget.Entry {
		private static final int field_32387 = 32;
		private static final int field_32388 = 32;
		private static final int field_32389 = 0;
		private static final int field_32390 = 32;
		private static final int field_32391 = 64;
		private static final int field_32392 = 96;
		private static final int field_32393 = 0;
		private static final int field_32394 = 32;
		private final MultiplayerScreen screen;
		private final MinecraftClient client;
		private final ServerInfo server;
		private final Identifier iconTextureId;
		private String iconUri;
		@Nullable
		private NativeImageBackedTexture icon;
		private long time;

		protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
			this.screen = screen;
			this.server = server;
			this.client = MinecraftClient.getInstance();
			this.iconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(server.address) + "/icon");
			AbstractTexture abstractTexture = this.client.getTextureManager().getOrDefault(this.iconTextureId, MissingSprite.getMissingSpriteTexture());
			if (abstractTexture != MissingSprite.getMissingSpriteTexture() && abstractTexture instanceof NativeImageBackedTexture) {
				this.icon = (NativeImageBackedTexture)abstractTexture;
			}
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (!this.server.online) {
				this.server.online = true;
				this.server.ping = -2L;
				this.server.label = LiteralText.EMPTY;
				this.server.playerCountLabel = LiteralText.EMPTY;
				MultiplayerServerListWidget.SERVER_PINGER_THREAD_POOL.submit(() -> {
					try {
						this.screen.getServerListPinger().add(this.server, () -> this.client.execute(this::saveFile));
					} catch (UnknownHostException var2) {
						this.server.ping = -1L;
						this.server.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT;
					} catch (Exception var3) {
						this.server.ping = -1L;
						this.server.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT;
					}
				});
			}

			boolean bl = this.server.protocolVersion != SharedConstants.getGameVersion().getProtocolVersion();
			this.client.textRenderer.draw(matrices, this.server.name, (float)(x + 32 + 3), (float)(y + 1), 16777215);
			List<OrderedText> list = this.client.textRenderer.wrapLines(this.server.label, entryWidth - 32 - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				this.client.textRenderer.draw(matrices, (OrderedText)list.get(i), (float)(x + 32 + 3), (float)(y + 12 + 9 * i), 8421504);
			}

			Text text = (Text)(bl ? this.server.version.shallowCopy().formatted(Formatting.RED) : this.server.playerCountLabel);
			int j = this.client.textRenderer.getWidth(text);
			this.client.textRenderer.draw(matrices, text, (float)(x + entryWidth - j - 15 - 2), (float)(y + 1), 8421504);
			int k = 0;
			int l;
			List<Text> list2;
			Text text2;
			if (bl) {
				l = 5;
				text2 = MultiplayerServerListWidget.INCOMPATIBLE_TEXT;
				list2 = this.server.playerListSummary;
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
					text2 = MultiplayerServerListWidget.NO_CONNECTION_TEXT;
					list2 = Collections.emptyList();
				} else {
					text2 = new TranslatableText("multiplayer.status.ping", this.server.ping);
					list2 = this.server.playerListSummary;
				}
			} else {
				k = 1;
				l = (int)(Util.getMeasuringTimeMs() / 100L + (long)(index * 2) & 7L);
				if (l > 4) {
					l = 8 - l;
				}

				text2 = MultiplayerServerListWidget.PINGING_TEXT;
				list2 = Collections.emptyList();
			}

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
			DrawableHelper.drawTexture(matrices, x + entryWidth - 15, y, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
			String string = this.server.getIcon();
			if (!Objects.equals(string, this.iconUri)) {
				if (this.isNewIconValid(string)) {
					this.iconUri = string;
				} else {
					this.server.setIcon(null);
					this.saveFile();
				}
			}

			if (this.icon == null) {
				this.draw(matrices, x, y, MultiplayerServerListWidget.UNKNOWN_SERVER_TEXTURE);
			} else {
				this.draw(matrices, x, y, this.iconTextureId);
			}

			int m = mouseX - x;
			int n = mouseY - y;
			if (m >= entryWidth - 15 && m <= entryWidth - 5 && n >= 0 && n <= 8) {
				this.screen.setTooltip(Collections.singletonList(text2));
			} else if (m >= entryWidth - j - 15 - 2 && m <= entryWidth - 15 - 2 && n >= 0 && n <= 8) {
				this.screen.setTooltip(list2);
			}

			if (this.client.options.touchscreen || hovered) {
				RenderSystem.setShaderTexture(0, MultiplayerServerListWidget.SERVER_SELECTION_TEXTURE);
				DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				int o = mouseX - x;
				int p = mouseY - y;
				if (this.method_20136()) {
					if (o < 32 && o > 16) {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (index > 0) {
					if (o < 16 && p < 16) {
						DrawableHelper.drawTexture(matrices, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, x, y, 96.0F, 0.0F, 32, 32, 256, 256);
					}
				}

				if (index < this.screen.getServerList().size() - 1) {
					if (o < 16 && p > 16) {
						DrawableHelper.drawTexture(matrices, x, y, 64.0F, 32.0F, 32, 32, 256, 256);
					} else {
						DrawableHelper.drawTexture(matrices, x, y, 64.0F, 0.0F, 32, 32, 256, 256);
					}
				}
			}
		}

		public void saveFile() {
			this.screen.getServerList().saveFile();
		}

		protected void draw(MatrixStack matrices, int x, int y, Identifier textureId) {
			RenderSystem.setShaderTexture(0, textureId);
			RenderSystem.enableBlend();
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
		}

		private boolean method_20136() {
			return true;
		}

		private boolean isNewIconValid(@Nullable String newIconUri) {
			if (newIconUri == null) {
				this.client.getTextureManager().destroyTexture(this.iconTextureId);
				if (this.icon != null && this.icon.getImage() != null) {
					this.icon.getImage().close();
				}

				this.icon = null;
			} else {
				try {
					NativeImage nativeImage = NativeImage.read(newIconUri);
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
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (Screen.hasShiftDown()) {
				MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
				int i = multiplayerServerListWidget.children().indexOf(this);
				if (keyCode == GLFW.GLFW_KEY_DOWN && i < this.screen.getServerList().size() - 1 || keyCode == GLFW.GLFW_KEY_UP && i > 0) {
					this.swapEntries(i, keyCode == GLFW.GLFW_KEY_DOWN ? i + 1 : i - 1);
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
