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
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.ServerEntryNetworkPart;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerEntryNetworkPart field_3037 = new ServerEntryNetworkPart();
	private final Screen parent;
	protected MultiplayerServerListWidget field_3043;
	private ServerList field_3040;
	private ButtonWidget buttonEdit;
	private ButtonWidget buttonJoin;
	private ButtonWidget buttonDelete;
	private String tooltipText;
	private ServerEntry field_3051;
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
			this.field_3043.updateSize(this.width, this.height, 32, this.height - 64);
		} else {
			this.initialized = true;
			this.field_3040 = new ServerList(this.minecraft);
			this.field_3040.loadFile();
			this.lanServers = new LanServerQueryManager.LanServerEntryList();

			try {
				this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
				this.lanServerDetector.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.field_3043 = new MultiplayerServerListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
			this.field_3043.method_20125(this.field_3040);
		}

		this.children.add(this.field_3043);
		this.buttonJoin = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select"), buttonWidget -> this.connect())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct"), buttonWidget -> {
			this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.method_1507(new DirectConnectScreen(this::directConnect, this.field_3051));
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add"), buttonWidget -> {
			this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.method_1507(new AddServerScreen(this::addEntry, this.field_3051));
		}));
		this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).method_20133();
				this.field_3051 = new ServerEntry(serverEntry.name, serverEntry.address, false);
				this.field_3051.copyFrom(serverEntry);
				this.minecraft.method_1507(new AddServerScreen(this::editEntry, this.field_3051));
			}
		}));
		this.buttonDelete = this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				String string = ((MultiplayerServerListWidget.ServerItem)entry).method_20133().name;
				if (string != null) {
					Text text = new TranslatableText("selectServer.deleteQuestion");
					Text text2 = new TranslatableText("selectServer.deleteWarning", string);
					String string2 = I18n.translate("selectServer.deleteButton");
					String string3 = I18n.translate("gui.cancel");
					this.minecraft.method_1507(new ConfirmScreen(this::removeEntry, text, text2, string2, string3));
				}
			}
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh"), buttonWidget -> this.refresh()));
		this.addButton(
			new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.method_1507(this.parent))
		);
		this.updateButtonActivationStates();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.lanServers.needsUpdate()) {
			List<LanServerEntry> list = this.lanServers.getServers();
			this.lanServers.markClean();
			this.field_3043.method_20126(list);
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
		this.minecraft.method_1507(new MultiplayerScreen(this.parent));
	}

	private void removeEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.field_3040.remove(((MultiplayerServerListWidget.ServerItem)entry).method_20133());
			this.field_3040.saveFile();
			this.field_3043.method_20122(null);
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void editEntry(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).method_20133();
			serverEntry.name = this.field_3051.name;
			serverEntry.address = this.field_3051.address;
			serverEntry.copyFrom(this.field_3051);
			this.field_3040.saveFile();
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void addEntry(boolean bl) {
		if (bl) {
			this.field_3040.add(this.field_3051);
			this.field_3040.saveFile();
			this.field_3043.method_20122(null);
			this.field_3043.method_20125(this.field_3040);
		}

		this.minecraft.method_1507(this);
	}

	private void directConnect(boolean bl) {
		if (bl) {
			this.method_2548(this.field_3051);
		} else {
			this.minecraft.method_1507(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 294) {
			this.refresh();
			return true;
		} else if (this.field_3043.getSelected() == null || i != 257 && i != 335) {
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
		this.field_3043.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.tooltipText)), i, j);
		}
	}

	public void connect() {
		MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
		if (entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.method_2548(((MultiplayerServerListWidget.ServerItem)entry).method_20133());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerListEntry) {
			LanServerEntry lanServerEntry = ((MultiplayerServerListWidget.LanServerListEntry)entry).method_20132();
			this.method_2548(new ServerEntry(lanServerEntry.getMotd(), lanServerEntry.getAddressPort(), true));
		}
	}

	private void method_2548(ServerEntry serverEntry) {
		this.minecraft.method_1507(new ConnectScreen(this, this.minecraft, serverEntry));
	}

	public void selectEntry(MultiplayerServerListWidget.Entry entry) {
		this.field_3043.method_20122(entry);
		this.updateButtonActivationStates();
	}

	protected void updateButtonActivationStates() {
		this.buttonJoin.active = false;
		this.buttonEdit.active = false;
		this.buttonDelete.active = false;
		MultiplayerServerListWidget.Entry entry = this.field_3043.getSelected();
		if (entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry)) {
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

	public ServerList method_2529() {
		return this.field_3040;
	}
}
