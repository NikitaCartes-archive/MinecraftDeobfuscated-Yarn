package net.minecraft.client.gui.screen.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	public static final int field_41849 = 308;
	public static final int field_41850 = 100;
	public static final int field_41851 = 74;
	public static final int field_41852 = 64;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
	private final Screen parent;
	protected MultiplayerServerListWidget serverListWidget;
	private ServerList serverList;
	private ButtonWidget buttonEdit;
	private ButtonWidget buttonJoin;
	private ButtonWidget buttonDelete;
	@Nullable
	private List<Text> multiplayerScreenTooltip;
	private ServerInfo selectedEntry;
	private LanServerQueryManager.LanServerEntryList lanServers;
	@Nullable
	private LanServerQueryManager.LanServerDetector lanServerDetector;
	private boolean initialized;

	public MultiplayerScreen(Screen parent) {
		super(Text.translatable("multiplayer.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (this.initialized) {
			this.serverListWidget.setDimensionsAndPosition(this.width, this.height - 64 - 32, 0, 32);
		} else {
			this.initialized = true;
			this.serverList = new ServerList(this.client);
			this.serverList.loadFile();
			this.lanServers = new LanServerQueryManager.LanServerEntryList();

			try {
				this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
				this.lanServerDetector.start();
			} catch (Exception var8) {
				LOGGER.warn("Unable to start LAN server detection: {}", var8.getMessage());
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height - 64 - 32, 32, 36);
			this.serverListWidget.setServers(this.serverList);
		}

		this.addSelectableChild(this.serverListWidget);
		this.buttonJoin = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.select"), button -> this.connect()).width(100).build());
		ButtonWidget buttonWidget = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.direct"), button -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", ServerInfo.ServerType.OTHER);
			this.client.setScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
		}).width(100).build());
		ButtonWidget buttonWidget2 = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.add"), button -> {
			this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", ServerInfo.ServerType.OTHER);
			this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
		}).width(100).build());
		this.buttonEdit = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.edit"), button -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
				this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, ServerInfo.ServerType.OTHER);
				this.selectedEntry.copyWithSettingsFrom(serverInfo);
				this.client.setScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
			}
		}).width(74).build());
		this.buttonDelete = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.delete"), button -> {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				String string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name;
				if (string != null) {
					Text text = Text.translatable("selectServer.deleteQuestion");
					Text text2 = Text.translatable("selectServer.deleteWarning", string);
					Text text3 = Text.translatable("selectServer.deleteButton");
					Text text4 = ScreenTexts.CANCEL;
					this.client.setScreen(new ConfirmScreen(this::removeEntry, text, text2, text3, text4));
				}
			}
		}).width(74).build());
		ButtonWidget buttonWidget3 = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectServer.refresh"), button -> this.refresh()).width(74).build()
		);
		ButtonWidget buttonWidget4 = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).width(74).build());
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical();
		AxisGridWidget axisGridWidget = directionalLayoutWidget.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));
		axisGridWidget.add(this.buttonJoin);
		axisGridWidget.add(buttonWidget);
		axisGridWidget.add(buttonWidget2);
		directionalLayoutWidget.add(EmptyWidget.ofHeight(4));
		AxisGridWidget axisGridWidget2 = directionalLayoutWidget.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));
		axisGridWidget2.add(this.buttonEdit);
		axisGridWidget2.add(this.buttonDelete);
		axisGridWidget2.add(buttonWidget3);
		axisGridWidget2.add(buttonWidget4);
		directionalLayoutWidget.refreshPositions();
		SimplePositioningWidget.setPos(directionalLayoutWidget, 0, this.height - 64, this.width, 64);
		this.updateButtonActivationStates();
	}

	@Override
	public void tick() {
		super.tick();
		List<LanServerInfo> list = this.lanServers.getEntriesIfUpdated();
		if (list != null) {
			this.serverListWidget.setLanServers(list);
		}

		this.serverListPinger.tick();
	}

	@Override
	public void removed() {
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.serverListPinger.cancel();
		this.serverListWidget.onRemoved();
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
			serverInfo.copyWithSettingsFrom(this.selectedEntry);
			this.serverList.saveFile();
			this.serverListWidget.setServers(this.serverList);
		}

		this.client.setScreen(this);
	}

	private void addEntry(boolean confirmedAction) {
		if (confirmedAction) {
			ServerInfo serverInfo = this.serverList.tryUnhide(this.selectedEntry.address);
			if (serverInfo != null) {
				serverInfo.copyFrom(this.selectedEntry);
				this.serverList.saveFile();
			} else {
				this.serverList.add(this.selectedEntry, false);
				this.serverList.saveFile();
			}

			this.serverListWidget.setSelected(null);
			this.serverListWidget.setServers(this.serverList);
		}

		this.client.setScreen(this);
	}

	private void directConnect(boolean confirmedAction) {
		if (confirmedAction) {
			ServerInfo serverInfo = this.serverList.get(this.selectedEntry.address);
			if (serverInfo == null) {
				this.serverList.add(this.selectedEntry, true);
				this.serverList.saveFile();
				this.connect(this.selectedEntry);
			} else {
				this.connect(serverInfo);
			}
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
			if (KeyCodes.isToggle(keyCode)) {
				this.connect();
				return true;
			} else {
				return this.serverListWidget.keyPressed(keyCode, scanCode, modifiers);
			}
		} else {
			return false;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.multiplayerScreenTooltip = null;
		this.serverListWidget.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		if (this.multiplayerScreenTooltip != null) {
			context.drawTooltip(this.textRenderer, this.multiplayerScreenTooltip, mouseX, mouseY);
		}
	}

	public void connect() {
		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
			this.connect(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
		} else if (entry instanceof MultiplayerServerListWidget.LanServerEntry) {
			LanServerInfo lanServerInfo = ((MultiplayerServerListWidget.LanServerEntry)entry).getLanServerEntry();
			this.connect(new ServerInfo(lanServerInfo.getMotd(), lanServerInfo.getAddressPort(), ServerInfo.ServerType.LAN));
		}
	}

	private void connect(ServerInfo entry) {
		ConnectScreen.connect(this, this.client, ServerAddress.parse(entry.address), entry, false);
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

	public void setMultiplayerScreenTooltip(List<Text> tooltip) {
		this.multiplayerScreenTooltip = tooltip;
	}

	public ServerList getServerList() {
		return this.serverList;
	}
}
