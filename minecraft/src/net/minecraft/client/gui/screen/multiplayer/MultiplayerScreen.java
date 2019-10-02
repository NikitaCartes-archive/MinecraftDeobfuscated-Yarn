package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private String tooltipText;
	private ServerInfo selectedEntry;
	private LanServerQueryManager.LanServerEntryList lanServers;
	private LanServerQueryManager.LanServerDetector lanServerDetector;
	private boolean initialized;

	public MultiplayerScreen(Screen screen) {
		super(new TranslatableText("multiplayer.title"));
		this.parent = screen;
	}

	@Override
	protected void init() {
		super.init();
		this.minecraft.keyboard.enableRepeatEvents(true);
		if (this.initialized) {
			this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
		} else {
			this.initialized = true;
			this.serverList = new ServerList(this.minecraft);
			this.serverList.loadFile();
			this.lanServers = new LanServerQueryManager.LanServerEntryList();

			try {
				this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
				this.lanServerDetector.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.setServers(this.serverList);
		}

		this.children.add(this.serverListWidget);
		this.buttonJoin = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select"), buttonWidget -> this.connect())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct"), buttonWidget -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new DirectConnectScreen(this::directConnect, this.selectedEntry));
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add"), buttonWidget -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new AddServerScreen(this::addEntry, this.selectedEntry));
		}));
		this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
				this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
				this.selectedEntry.copyFrom(serverInfo);
				this.minecraft.openScreen(new AddServerScreen(this::editEntry, this.selectedEntry));
			}
		}));
		this.buttonDelete = this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				String string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name;
				if (string != null) {
					Text text = new TranslatableText("selectServer.deleteQuestion");
					Text text2 = new TranslatableText("selectServer.deleteWarning", string);
					String string2 = I18n.translate("selectServer.deleteButton");
					String string3 = I18n.translate("gui.cancel");
					this.minecraft.openScreen(new ConfirmScreen(this::removeEntry, text, text2, string2, string3));
				}
			}
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh"), buttonWidget -> this.refresh()));
		this.addButton(
			new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
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
		this.minecraft.keyboard.enableRepeatEvents(false);
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.serverListPinger.cancel();
	}

	private void refresh() {
		this.minecraft.openScreen(new MultiplayerScreen(this.parent));
	}

	private void removeEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerEntry) {
			this.serverList.remove(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
			this.serverList.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.setServers(this.serverList);
		}

		this.minecraft.openScreen(this);
	}

	private void editEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerEntry) {
			ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
			serverInfo.name = this.selectedEntry.name;
			serverInfo.address = this.selectedEntry.address;
			serverInfo.copyFrom(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.setServers(this.serverList);
		}

		this.minecraft.openScreen(this);
	}

	private void addEntry(boolean bl) {
		if (bl) {
			this.serverList.add(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.setServers(this.serverList);
		}

		this.minecraft.openScreen(this);
	}

	private void directConnect(boolean bl) {
		if (bl) {
			this.connect(this.selectedEntry);
		} else {
			this.minecraft.openScreen(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 294) {
			this.refresh();
			return true;
		} else if (this.serverListWidget.getSelected() != null) {
			if (i != 257 && i != 335) {
				return this.serverListWidget.keyPressed(i, j, k);
			} else {
				this.connect();
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.tooltipText = null;
		this.renderBackground();
		this.serverListWidget.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.tooltipText)), i, j);
		}
	}

	public void connect() {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
		if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
			this.connect(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerEntry) {
			LanServerInfo lanServerInfo = ((MultiplayerServerListWidget.LanServerEntry)entry).getLanServerEntry();
			this.connect(new ServerInfo(lanServerInfo.getMotd(), lanServerInfo.getAddressPort(), true));
		}
	}

	private void connect(ServerInfo serverInfo) {
		this.minecraft.openScreen(new ConnectScreen(this, this.minecraft, serverInfo));
	}

	public void select(MultiplayerServerListWidget.Entry entry) {
		this.serverListWidget.method_20122(entry);
		this.updateButtonActivationStates();
	}

	protected void updateButtonActivationStates() {
		this.buttonJoin.active = false;
		this.buttonEdit.active = false;
		this.buttonDelete.active = false;
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelected();
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

	public void setTooltip(String string) {
		this.tooltipText = string;
	}

	public ServerList getServerList() {
		return this.serverList;
	}
}
