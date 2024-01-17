package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
	static final Identifier INCOMPATIBLE_TEXTURE = new Identifier("server_list/incompatible");
	static final Identifier UNREACHABLE_TEXTURE = new Identifier("server_list/unreachable");
	static final Identifier PING_1_TEXTURE = new Identifier("server_list/ping_1");
	static final Identifier PING_2_TEXTURE = new Identifier("server_list/ping_2");
	static final Identifier PING_3_TEXTURE = new Identifier("server_list/ping_3");
	static final Identifier PING_4_TEXTURE = new Identifier("server_list/ping_4");
	static final Identifier PING_5_TEXTURE = new Identifier("server_list/ping_5");
	static final Identifier PINGING_1_TEXTURE = new Identifier("server_list/pinging_1");
	static final Identifier PINGING_2_TEXTURE = new Identifier("server_list/pinging_2");
	static final Identifier PINGING_3_TEXTURE = new Identifier("server_list/pinging_3");
	static final Identifier PINGING_4_TEXTURE = new Identifier("server_list/pinging_4");
	static final Identifier PINGING_5_TEXTURE = new Identifier("server_list/pinging_5");
	static final Identifier JOIN_HIGHLIGHTED_TEXTURE = new Identifier("server_list/join_highlighted");
	static final Identifier JOIN_TEXTURE = new Identifier("server_list/join");
	static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = new Identifier("server_list/move_up_highlighted");
	static final Identifier MOVE_UP_TEXTURE = new Identifier("server_list/move_up");
	static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = new Identifier("server_list/move_down_highlighted");
	static final Identifier MOVE_DOWN_TEXTURE = new Identifier("server_list/move_down");
	static final Logger LOGGER = LogUtils.getLogger();
	static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build()
	);
	static final Text LAN_SCANNING_TEXT = Text.translatable("lanServer.scanning");
	static final Text CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").withColor(Colors.RED);
	static final Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").withColor(Colors.RED);
	static final Text INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
	static final Text NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
	static final Text PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
	static final Text ONLINE_TEXT = Text.translatable("multiplayer.status.online");
	private final MultiplayerScreen screen;
	private final List<MultiplayerServerListWidget.ServerEntry> servers = Lists.<MultiplayerServerListWidget.ServerEntry>newArrayList();
	private final MultiplayerServerListWidget.Entry scanningEntry = new MultiplayerServerListWidget.ScanningEntry();
	private final List<MultiplayerServerListWidget.LanServerEntry> lanServers = Lists.<MultiplayerServerListWidget.LanServerEntry>newArrayList();

	public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
		super(client, width, height, top, bottom);
		this.screen = screen;
	}

	private void updateEntries() {
		this.clearEntries();
		this.servers.forEach(server -> this.addEntry(server));
		this.addEntry(this.scanningEntry);
		this.lanServers.forEach(lanServer -> this.addEntry(lanServer));
	}

	public void setSelected(@Nullable MultiplayerServerListWidget.Entry entry) {
		super.setSelected(entry);
		this.screen.updateButtonActivationStates();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		MultiplayerServerListWidget.Entry entry = this.getSelectedOrNull();
		return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void setServers(ServerList servers) {
		this.servers.clear();

		for (int i = 0; i < servers.size(); i++) {
			this.servers.add(new MultiplayerServerListWidget.ServerEntry(this.screen, servers.get(i)));
		}

		this.updateEntries();
	}

	public void setLanServers(List<LanServerInfo> lanServers) {
		int i = lanServers.size() - this.lanServers.size();
		this.lanServers.clear();

		for (LanServerInfo lanServerInfo : lanServers) {
			this.lanServers.add(new MultiplayerServerListWidget.LanServerEntry(this.screen, lanServerInfo));
		}

		this.updateEntries();

		for (int j = this.lanServers.size() - i; j < this.lanServers.size(); j++) {
			MultiplayerServerListWidget.LanServerEntry lanServerEntry = (MultiplayerServerListWidget.LanServerEntry)this.lanServers.get(j);
			int k = j - this.lanServers.size() + this.children().size();
			int l = this.getRowTop(k);
			int m = this.getRowBottom(k);
			if (m >= this.getY() && l <= this.getBottom()) {
				this.client.getNarratorManager().narrateSystemMessage(Text.translatable("multiplayer.lan.server_found", lanServerEntry.getMotdNarration()));
			}
		}
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 30;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	public void onRemoved() {
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<MultiplayerServerListWidget.Entry> implements AutoCloseable {
		public void close() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LanServerEntry extends MultiplayerServerListWidget.Entry {
		private static final int field_32386 = 32;
		private static final Text TITLE_TEXT = Text.translatable("lanServer.title");
		private static final Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
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
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawText(this.client.textRenderer, TITLE_TEXT, x + 32 + 3, y + 1, 16777215, false);
			context.drawText(this.client.textRenderer, this.server.getMotd(), x + 32 + 3, y + 12, Colors.GRAY, false);
			if (this.client.options.hideServerAddress) {
				context.drawText(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, x + 32 + 3, y + 12 + 11, 3158064, false);
			} else {
				context.drawText(this.client.textRenderer, this.server.getAddressPort(), x + 32 + 3, y + 12 + 11, 3158064, false);
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

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.getMotdNarration());
		}

		public Text getMotdNarration() {
			return Text.empty().append(TITLE_TEXT).append(ScreenTexts.SPACE).append(this.server.getMotd());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ScanningEntry extends MultiplayerServerListWidget.Entry {
		private final MinecraftClient client = MinecraftClient.getInstance();

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = y + entryHeight / 2 - 9 / 2;
			context.drawText(
				this.client.textRenderer,
				MultiplayerServerListWidget.LAN_SCANNING_TEXT,
				this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(MultiplayerServerListWidget.LAN_SCANNING_TEXT) / 2,
				i,
				16777215,
				false
			);
			String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
			context.drawText(
				this.client.textRenderer, string, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2, i + 9, Colors.GRAY, false
			);
		}

		@Override
		public Text getNarration() {
			return MultiplayerServerListWidget.LAN_SCANNING_TEXT;
		}
	}

	@Environment(EnvType.CLIENT)
	public class ServerEntry extends MultiplayerServerListWidget.Entry {
		private static final int field_32387 = 32;
		private static final int field_32388 = 32;
		private static final int field_47852 = 5;
		private static final int field_47853 = 10;
		private static final int field_47854 = 8;
		private final MultiplayerScreen screen;
		private final MinecraftClient client;
		private final ServerInfo server;
		private final WorldIcon icon;
		@Nullable
		private byte[] favicon;
		private long time;
		@Nullable
		private List<Text> playerListSummary;
		@Nullable
		private Identifier statusIconTexture;
		@Nullable
		private Text statusTooltipText;

		protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
			this.screen = screen;
			this.server = server;
			this.client = MinecraftClient.getInstance();
			this.icon = WorldIcon.forServer(this.client.getTextureManager(), server.address);
			this.update();
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (this.server.getStatus() == ServerInfo.Status.INITIAL) {
				this.server.setStatus(ServerInfo.Status.PINGING);
				this.server.label = ScreenTexts.EMPTY;
				this.server.playerCountLabel = ScreenTexts.EMPTY;
				MultiplayerServerListWidget.SERVER_PINGER_THREAD_POOL
					.submit(
						() -> {
							try {
								this.screen
									.getServerListPinger()
									.add(
										this.server,
										() -> this.client.execute(this::saveFile),
										() -> {
											this.server
												.setStatus(
													this.server.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion()
														? ServerInfo.Status.SUCCESSFUL
														: ServerInfo.Status.INCOMPATIBLE
												);
											this.client.execute(this::update);
										}
									);
							} catch (UnknownHostException var2) {
								this.server.setStatus(ServerInfo.Status.UNREACHABLE);
								this.server.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT;
								this.client.execute(this::update);
							} catch (Exception var3) {
								this.server.setStatus(ServerInfo.Status.UNREACHABLE);
								this.server.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT;
								this.client.execute(this::update);
							}
						}
					);
			}

			context.drawText(this.client.textRenderer, this.server.name, x + 32 + 3, y + 1, 16777215, false);
			List<OrderedText> list = this.client.textRenderer.wrapLines(this.server.label, entryWidth - 32 - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				context.drawText(this.client.textRenderer, (OrderedText)list.get(i), x + 32 + 3, y + 12 + 9 * i, -8355712, false);
			}

			this.draw(context, x, y, this.icon.getTextureId());
			if (this.server.getStatus() == ServerInfo.Status.PINGING) {
				int i = (int)(Util.getMeasuringTimeMs() / 100L + (long)(index * 2) & 7L);
				if (i > 4) {
					i = 8 - i;
				}
				this.statusIconTexture = switch (i) {
					case 1 -> MultiplayerServerListWidget.PINGING_2_TEXTURE;
					case 2 -> MultiplayerServerListWidget.PINGING_3_TEXTURE;
					case 3 -> MultiplayerServerListWidget.PINGING_4_TEXTURE;
					case 4 -> MultiplayerServerListWidget.PINGING_5_TEXTURE;
					default -> MultiplayerServerListWidget.PINGING_1_TEXTURE;
				};
			}

			int i = x + entryWidth - 10 - 5;
			if (this.statusIconTexture != null) {
				context.drawGuiTexture(this.statusIconTexture, i, y, 10, 8);
			}

			byte[] bs = this.server.getFavicon();
			if (!Arrays.equals(bs, this.favicon)) {
				if (this.uploadFavicon(bs)) {
					this.favicon = bs;
				} else {
					this.server.setFavicon(null);
					this.saveFile();
				}
			}

			Text text = (Text)(this.server.getStatus() == ServerInfo.Status.INCOMPATIBLE
				? this.server.version.copy().formatted(Formatting.RED)
				: this.server.playerCountLabel);
			int j = this.client.textRenderer.getWidth(text);
			int k = i - j - 5;
			context.drawText(this.client.textRenderer, text, k, y + 1, Colors.GRAY, false);
			if (this.statusTooltipText != null && mouseX >= i && mouseX <= i + 10 && mouseY >= y && mouseY <= y + 8) {
				this.screen.setTooltip(this.statusTooltipText);
			} else if (this.playerListSummary != null && mouseX >= k && mouseX <= k + j && mouseY >= y && mouseY <= y - 1 + 9) {
				this.screen.setTooltip(Lists.transform(this.playerListSummary, Text::asOrderedText));
			}

			if (this.client.options.getTouchscreen().getValue() || hovered) {
				context.fill(x, y, x + 32, y + 32, -1601138544);
				int l = mouseX - x;
				int m = mouseY - y;
				if (this.canConnect()) {
					if (l < 32 && l > 16) {
						context.drawGuiTexture(MultiplayerServerListWidget.JOIN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
					} else {
						context.drawGuiTexture(MultiplayerServerListWidget.JOIN_TEXTURE, x, y, 32, 32);
					}
				}

				if (index > 0) {
					if (l < 16 && m < 16) {
						context.drawGuiTexture(MultiplayerServerListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
					} else {
						context.drawGuiTexture(MultiplayerServerListWidget.MOVE_UP_TEXTURE, x, y, 32, 32);
					}
				}

				if (index < this.screen.getServerList().size() - 1) {
					if (l < 16 && m > 16) {
						context.drawGuiTexture(MultiplayerServerListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
					} else {
						context.drawGuiTexture(MultiplayerServerListWidget.MOVE_DOWN_TEXTURE, x, y, 32, 32);
					}
				}
			}
		}

		private void update() {
			this.playerListSummary = null;
			switch (this.server.getStatus()) {
				case INITIAL:
				case PINGING:
					this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
					this.statusTooltipText = MultiplayerServerListWidget.PINGING_TEXT;
					break;
				case INCOMPATIBLE:
					this.statusIconTexture = MultiplayerServerListWidget.INCOMPATIBLE_TEXTURE;
					this.statusTooltipText = MultiplayerServerListWidget.INCOMPATIBLE_TEXT;
					this.playerListSummary = this.server.playerListSummary;
					break;
				case UNREACHABLE:
					this.statusIconTexture = MultiplayerServerListWidget.UNREACHABLE_TEXTURE;
					this.statusTooltipText = MultiplayerServerListWidget.NO_CONNECTION_TEXT;
					break;
				case SUCCESSFUL:
					if (this.server.ping < 150L) {
						this.statusIconTexture = MultiplayerServerListWidget.PING_5_TEXTURE;
					} else if (this.server.ping < 300L) {
						this.statusIconTexture = MultiplayerServerListWidget.PING_4_TEXTURE;
					} else if (this.server.ping < 600L) {
						this.statusIconTexture = MultiplayerServerListWidget.PING_3_TEXTURE;
					} else if (this.server.ping < 1000L) {
						this.statusIconTexture = MultiplayerServerListWidget.PING_2_TEXTURE;
					} else {
						this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
					}

					this.statusTooltipText = Text.translatable("multiplayer.status.ping", this.server.ping);
					this.playerListSummary = this.server.playerListSummary;
			}
		}

		public void saveFile() {
			this.screen.getServerList().saveFile();
		}

		protected void draw(DrawContext context, int x, int y, Identifier textureId) {
			RenderSystem.enableBlend();
			context.drawTexture(textureId, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
		}

		private boolean canConnect() {
			return true;
		}

		private boolean uploadFavicon(@Nullable byte[] bytes) {
			if (bytes == null) {
				this.icon.destroy();
			} else {
				try {
					this.icon.load(NativeImage.read(bytes));
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
				if (i == -1) {
					return true;
				}

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
				if (d < 32.0 && d > 16.0 && this.canConnect()) {
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
			return true;
		}

		public ServerInfo getServer() {
			return this.server;
		}

		@Override
		public Text getNarration() {
			MutableText mutableText = Text.empty();
			mutableText.append(Text.translatable("narrator.select", this.server.name));
			mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
			switch (this.server.getStatus()) {
				case PINGING:
					mutableText.append(MultiplayerServerListWidget.PINGING_TEXT);
					break;
				case INCOMPATIBLE:
					mutableText.append(MultiplayerServerListWidget.INCOMPATIBLE_TEXT);
					mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.version.narration", this.server.version));
					mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
					break;
				case UNREACHABLE:
					mutableText.append(MultiplayerServerListWidget.NO_CONNECTION_TEXT);
					break;
				default:
					mutableText.append(MultiplayerServerListWidget.ONLINE_TEXT);
					mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.ping.narration", this.server.ping));
					mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
					if (this.server.players != null) {
						mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
						mutableText.append(Text.translatable("multiplayer.status.player_count.narration", this.server.players.online(), this.server.players.max()));
						mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
						mutableText.append(Texts.join(this.server.playerListSummary, Text.literal(", ")));
					}
			}

			return mutableText;
		}

		@Override
		public void close() {
			this.icon.close();
		}
	}
}
