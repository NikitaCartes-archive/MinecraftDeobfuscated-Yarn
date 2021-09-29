package net.minecraft.client.gui.screen.multiplayer;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
	private final Screen parent;
	protected MultiplayerServerListWidget serverListWidget;
	private ServerList serverList;
	private ButtonWidget buttonEdit;
	private ButtonWidget buttonJoin;
	private ButtonWidget buttonDelete;
	@Nullable
	private List<Text> tooltipText;
	private ServerInfo selectedEntry;
	private LanServerQueryManager.LanServerEntryList lanServers;
	@Nullable
	private LanServerQueryManager.LanServerDetector lanServerDetector;
	private boolean initialized;

	public MultiplayerScreen(Screen parent) {
		super(new TranslatableText("multiplayer.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.client.keyboard.setRepeatEvents(true);
		if (this.initialized) {
			this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
		} else {
			this.initialized = true;
			this.serverList = new ServerList(this.client);
			this.serverList.loadFile();
			this.lanServers = new LanServerQueryManager.LanServerEntryList();

			try {
				this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
				this.lanServerDetector.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.setServers(this.serverList);
		}

		this.addSelectableChild(this.serverListWidget);
		this.buttonJoin = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, new TranslatableText("selectServer.select"), button -> this.connect())
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, new TranslatableText("selectServer.direct"), button -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.client.setScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, new TranslatableText("selectServer.add"), button -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
		}));
		this.buttonEdit = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, new TranslatableText("selectServer.edit"), button -> {
				MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
				if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
					ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
					this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
					this.selectedEntry.copyFrom(serverInfo);
					this.client.setScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
				}
			})
		);
		this.buttonDelete = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, new TranslatableText("selectServer.delete"), button -> {
				MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
				if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
					String string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name;
					if (string != null) {
						Text text = new TranslatableText("selectServer.deleteQuestion");
						Text text2 = new TranslatableText("selectServer.deleteWarning", string);
						Text text3 = new TranslatableText("selectServer.deleteButton");
						Text text4 = ScreenTexts.CANCEL;
						this.client.setScreen(new ConfirmScreen(this::removeEntry, text, text2, text3, text4));
					}
				}
			})
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, new TranslatableText("selectServer.refresh"), button -> this.refresh()));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
		this.updateButtonActivationStates();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.lanServers.needsUpdate()) {
			List<LanServerInfo> list = this.lanServers.getServers();
			this.lanServers.markClean();
			this.serverListWidget.setLanServers(list);
		}

		this.serverListPinger.tick();
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.serverListPinger.cancel();
	}

	private void refresh() {
		this.client.setScreen(new MultiplayerScreen(this.parent));
	}

	private void removeEntry(boolean confirmedAction) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
			this.serverList.remove(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
			this.serverList.saveFile();
			this.serverListWidget.setSelected(null);
			this.serverListWidget.setServers(this.serverList);
		}

		this.client.setScreen(this);
	}

	private void editEntry(boolean confirmedAction) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
			ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
			serverInfo.name = this.selectedEntry.name;
			serverInfo.address = this.selectedEntry.address;
			serverInfo.copyFrom(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.setServers(this.serverList);
		}

		this.client.setScreen(this);
	}

	private void addEntry(boolean confirmedAction) {
		if (confirmedAction) {
			this.serverList.add(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.setSelected(null);
			this.serverListWidget.setServers(this.serverList);
		}

		this.client.setScreen(this);
	}

	private void directConnect(boolean confirmedAction) {
		if (confirmedAction) {
			this.connect(this.selectedEntry);
		} else {
			this.client.setScreen(this);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_F5) {
			this.refresh();
			return true;
		} else if (this.serverListWidget.getSelectedOrNull() != null) {
			if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
				return this.serverListWidget.keyPressed(keyCode, scanCode, modifiers);
			} else {
				this.connect();
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltipText = null;
		this.renderBackground(matrices);
		this.serverListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		if (this.tooltipText != null) {
			this.renderTooltip(matrices, this.tooltipText, mouseX, mouseY);
		}
	}

	public void connect() {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
			this.connect(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerEntry) {
			LanServerInfo lanServerInfo = ((MultiplayerServerListWidget.LanServerEntry)entry).getLanServerEntry();
			this.connect(new ServerInfo(lanServerInfo.getMotd(), lanServerInfo.getAddressPort(), true));
		}
	}

	private void connect(ServerInfo entry) {
		ConnectScreen.connect(this, this.client, ServerAddress.parse(entry.address), entry);
	}

	public void select(MultiplayerServerListWidget.Entry entry) {
		this.serverListWidget.setSelected(entry);
		this.updateButtonActivationStates();
	}

	protected void updateButtonActivationStates() {
		this.buttonJoin.active = false;
		this.buttonEdit.active = false;
		this.buttonDelete.active = false;
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry)) {
			this.buttonJoin.active = true;
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				this.buttonEdit.active = true;
				this.buttonDelete.active = true;
			}
		}
	}

	public MultiplayerServerListPinger getServerListPinger() {
		return this.serverListPinger;
	}

	public void setTooltip(List<Text> tooltipText) {
		this.tooltipText = tooltipText;
	}

	public ServerList getServerList() {
		return this.serverList;
	}
}
