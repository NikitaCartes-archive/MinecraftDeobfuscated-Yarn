package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sortme.ServerEntryNetworkPart;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerEntryNetworkPart field_3037 = new ServerEntryNetworkPart();
	private final Screen parent;
	protected MultiplayerServerListWidget serverListWidget;
	private ServerList serverList;
	private ButtonWidget buttonEdit;
	private ButtonWidget buttonJoin;
	private ButtonWidget buttonDelete;
	private String tooltipText;
	private ServerEntry selectedEntry;
	private LanServerQueryManager.LanServerEntryList lanServers;
	private LanServerQueryManager.LanServerDetector lanServerDetector;
	private boolean initialized;

	public MultiplayerScreen(Screen screen) {
		super(new TranslatableTextComponent("multiplayer.title"));
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
			this.serverListWidget.method_20125(this.serverList);
		}

		this.children.add(this.serverListWidget);
		this.buttonJoin = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select"), buttonWidget -> this.connect())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct"), buttonWidget -> {
			this.selectedEntry = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new DirectConnectServerScreen(this::directConnect, this.selectedEntry));
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add"), buttonWidget -> {
			this.selectedEntry = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new AddServerScreen(this::addEntry, this.selectedEntry));
		}));
		this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).getServer();
				this.selectedEntry = new ServerEntry(serverEntry.name, serverEntry.address, false);
				this.selectedEntry.copyFrom(serverEntry);
				this.minecraft.openScreen(new AddServerScreen(this::editEntry, this.selectedEntry));
			}
		}));
		this.buttonDelete = this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				String string = ((MultiplayerServerListWidget.ServerItem)entry).getServer().name;
				if (string != null) {
					TextComponent textComponent = new TranslatableTextComponent("selectServer.deleteQuestion");
					TextComponent textComponent2 = new TranslatableTextComponent("selectServer.deleteWarning", string);
					String string2 = I18n.translate("selectServer.deleteButton");
					String string3 = I18n.translate("gui.cancel");
					this.minecraft.openScreen(new YesNoScreen(this::removeEntry, textComponent, textComponent2, string2, string3));
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
			List<LanServerEntry> list = this.lanServers.getServers();
			this.lanServers.markClean();
			this.serverListWidget.method_20126(list);
		}

		this.field_3037.method_3000();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.field_3037.method_3004();
	}

	private void refresh() {
		this.minecraft.openScreen(new MultiplayerScreen(this.parent));
	}

	private void removeEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.serverList.remove(((MultiplayerServerListWidget.ServerItem)entry).getServer());
			this.serverList.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.method_20125(this.serverList);
		}

		this.minecraft.openScreen(this);
	}

	private void editEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).getServer();
			serverEntry.name = this.selectedEntry.name;
			serverEntry.address = this.selectedEntry.address;
			serverEntry.copyFrom(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.method_20125(this.serverList);
		}

		this.minecraft.openScreen(this);
	}

	private void addEntry(boolean bl) {
		if (bl) {
			this.serverList.add(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.method_20125(this.serverList);
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
		} else if (this.serverListWidget.getSelectedItem() == null || i != 257 && i != 335) {
			return false;
		} else {
			this.connect();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.tooltipText = null;
		this.renderBackground();
		this.serverListWidget.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.tooltipText)), i, j);
		}
	}

	public void connect() {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.connect(((MultiplayerServerListWidget.ServerItem)entry).getServer());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerListEntry) {
			LanServerEntry lanServerEntry = ((MultiplayerServerListWidget.LanServerListEntry)entry).getLanServerEntry();
			this.connect(new ServerEntry(lanServerEntry.getMotd(), lanServerEntry.getAddressPort(), true));
		}
	}

	private void connect(ServerEntry serverEntry) {
		this.minecraft.openScreen(new ServerConnectingScreen(this, this.minecraft, serverEntry));
	}

	public void selectEntry(MultiplayerServerListWidget.Entry entry) {
		this.serverListWidget.method_20122(entry);
		this.updateButtonActivationStates();
	}

	protected void updateButtonActivationStates() {
		this.buttonJoin.active = false;
		this.buttonEdit.active = false;
		this.buttonDelete.active = false;
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (entry != null && !(entry instanceof MultiplayerServerListWidget.class_4268)) {
			this.buttonJoin.active = true;
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				this.buttonEdit.active = true;
				this.buttonDelete.active = true;
			}
		}
	}

	public ServerEntryNetworkPart method_2538() {
		return this.field_3037;
	}

	public void setTooltip(String string) {
		this.tooltipText = string;
	}

	public ServerList getServerList() {
		return this.serverList;
	}
}
