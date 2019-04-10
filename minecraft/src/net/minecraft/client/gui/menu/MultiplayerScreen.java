package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sortme.ServerEntryNetworkPart;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerEntryNetworkPart field_3037 = new ServerEntryNetworkPart();
	private final Screen parent;
	public MultiplayerServerListWidget serverListWidget;
	private ServerList field_3040;
	private ButtonWidget buttonEdit;
	private ButtonWidget buttonSelect;
	private ButtonWidget buttonDelete;
	private String field_3042;
	private ServerEntry field_3051;
	private LanServerQueryManager.LanServerEntryList field_3046;
	private LanServerQueryManager.LanServerDetector field_3045;
	private boolean field_3048;

	public MultiplayerScreen(Screen screen) {
		super(new TranslatableTextComponent("multiplayer.title"));
		this.parent = screen;
	}

	@Override
	protected void init() {
		super.init();
		this.minecraft.keyboard.enableRepeatEvents(true);
		if (this.field_3048) {
			this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
		} else {
			this.field_3048 = true;
			this.field_3040 = new ServerList(this.minecraft);
			this.field_3040.loadFile();
			this.field_3046 = new LanServerQueryManager.LanServerEntryList();

			try {
				this.field_3045 = new LanServerQueryManager.LanServerDetector(this.field_3046);
				this.field_3045.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.method_20125(this.field_3040);
		}

		this.children.add(this.serverListWidget);
		this.buttonSelect = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select"), buttonWidget -> this.method_2536())
		);
		this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct"), buttonWidget -> {
			this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new DirectConnectServerScreen(this::method_20380, this.field_3051));
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add"), buttonWidget -> {
			this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new AddServerScreen(this::method_20379, this.field_3051));
		}));
		this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit"), buttonWidget -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).getServer();
				this.field_3051 = new ServerEntry(serverEntry.name, serverEntry.address, false);
				this.field_3051.copyFrom(serverEntry);
				this.minecraft.openScreen(new AddServerScreen(this::method_20378, this.field_3051));
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
					this.minecraft.openScreen(new YesNoScreen(this::method_20377, textComponent, textComponent2, string2, string3));
				}
			}
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh"), buttonWidget -> this.method_2534()));
		this.addButton(
			new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.method_20121();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.field_3046.needsUpdate()) {
			List<LanServerEntry> list = this.field_3046.getServers();
			this.field_3046.markClean();
			this.serverListWidget.method_20126(list);
		}

		this.field_3037.method_3000();
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		if (this.field_3045 != null) {
			this.field_3045.interrupt();
			this.field_3045 = null;
		}

		this.field_3037.method_3004();
	}

	private void method_2534() {
		this.minecraft.openScreen(new MultiplayerScreen(this.parent));
	}

	private void method_20377(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.field_3040.remove(((MultiplayerServerListWidget.ServerItem)entry).getServer());
			this.field_3040.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.method_20125(this.field_3040);
		}

		this.minecraft.openScreen(this);
	}

	private void method_20378(boolean bl) {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (bl && entry instanceof MultiplayerServerListWidget.ServerItem) {
			ServerEntry serverEntry = ((MultiplayerServerListWidget.ServerItem)entry).getServer();
			serverEntry.name = this.field_3051.name;
			serverEntry.address = this.field_3051.address;
			serverEntry.copyFrom(this.field_3051);
			this.field_3040.saveFile();
			this.serverListWidget.method_20125(this.field_3040);
		}

		this.minecraft.openScreen(this);
	}

	private void method_20379(boolean bl) {
		if (bl) {
			this.field_3040.add(this.field_3051);
			this.field_3040.saveFile();
			this.serverListWidget.method_20122(null);
			this.serverListWidget.method_20125(this.field_3040);
		}

		this.minecraft.openScreen(this);
	}

	private void method_20380(boolean bl) {
		if (bl) {
			this.method_2548(this.field_3051);
		} else {
			this.minecraft.openScreen(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 294) {
			this.method_2534();
			return true;
		} else if (this.serverListWidget.getSelectedItem() == null || i != 257 && i != 335) {
			return false;
		} else {
			this.method_2536();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_3042 = null;
		this.renderBackground();
		this.serverListWidget.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.field_3042 != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3042)), i, j);
		}
	}

	public void method_2536() {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (entry instanceof MultiplayerServerListWidget.ServerItem) {
			this.method_2548(((MultiplayerServerListWidget.ServerItem)entry).getServer());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerListEntry) {
			LanServerEntry lanServerEntry = ((MultiplayerServerListWidget.LanServerListEntry)entry).getLanServerEntry();
			this.method_2548(new ServerEntry(lanServerEntry.getMotd(), lanServerEntry.getAddressPort(), true));
		}
	}

	private void method_2548(ServerEntry serverEntry) {
		this.minecraft.openScreen(new ServerConnectingScreen(this, this.minecraft, serverEntry));
	}

	public void selectEntry(MultiplayerServerListWidget.Entry entry) {
		this.serverListWidget.method_20122(entry);
		this.method_20121();
	}

	public void method_20121() {
		this.buttonSelect.active = false;
		this.buttonEdit.active = false;
		this.buttonDelete.active = false;
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedItem();
		if (entry != null && !(entry instanceof MultiplayerServerListWidget.class_4268)) {
			this.buttonSelect.active = true;
			if (entry instanceof MultiplayerServerListWidget.ServerItem) {
				this.buttonEdit.active = true;
				this.buttonDelete.active = true;
			}
		}
	}

	public ServerEntryNetworkPart method_2538() {
		return this.field_3037;
	}

	public void method_2528(String string) {
		this.field_3042 = string;
	}

	public ServerList method_2529() {
		return this.field_3040;
	}
}
