package net.minecraft.client.gui.menu;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LocalScanProgressListEntry;
import net.minecraft.client.gui.widget.LocalServerListEntry;
import net.minecraft.client.gui.widget.RemoteServerListEntry;
import net.minecraft.client.gui.widget.ServerListWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sortme.ServerEntryNetworkPart;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerEntryNetworkPart field_3037 = new ServerEntryNetworkPart();
	private final Screen parent;
	private ServerListWidget field_3043;
	private ServerList field_3040;
	private class_4185 field_3041;
	private class_4185 field_3050;
	private class_4185 field_3047;
	private boolean field_3039;
	private boolean field_3038;
	private boolean field_3036;
	private boolean field_3035;
	private String field_3042;
	private ServerEntry field_3051;
	private LanServerQueryManager.LanServerEntryList field_3046;
	private LanServerQueryManager.LanServerDetector field_3045;
	private boolean field_3048;

	public MultiplayerScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.client.keyboard.enableRepeatEvents(true);
		if (this.field_3048) {
			this.field_3043.method_1953(this.screenWidth, this.screenHeight, 32, this.screenHeight - 64);
		} else {
			this.field_3048 = true;
			this.field_3040 = new ServerList(this.client);
			this.field_3040.loadFile();
			this.field_3046 = new LanServerQueryManager.LanServerEntryList();

			try {
				this.field_3045 = new LanServerQueryManager.LanServerDetector(this.field_3046);
				this.field_3045.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.field_3043 = new ServerListWidget(this, this.client, this.screenWidth, this.screenHeight, 32, this.screenHeight - 64, 36);
			this.field_3043.method_2564(this.field_3040);
		}

		this.method_2540();
	}

	public void method_2540() {
		this.listeners.add(this.field_3043);
		this.field_3050 = this.addButton(new class_4185(this.screenWidth / 2 - 154, this.screenHeight - 52, 100, 20, I18n.translate("selectServer.select")) {
			@Override
			public void method_1826() {
				MultiplayerScreen.this.method_2536();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 50, this.screenHeight - 52, 100, 20, I18n.translate("selectServer.direct")) {
			@Override
			public void method_1826() {
				MultiplayerScreen.this.field_3035 = true;
				MultiplayerScreen.this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
				MultiplayerScreen.this.client.method_1507(new DirectConnectServerScreen(MultiplayerScreen.this, MultiplayerScreen.this.field_3051));
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4 + 50, this.screenHeight - 52, 100, 20, I18n.translate("selectServer.add")) {
			@Override
			public void method_1826() {
				MultiplayerScreen.this.field_3038 = true;
				MultiplayerScreen.this.field_3051 = new ServerEntry(I18n.translate("selectServer.defaultName"), "", false);
				MultiplayerScreen.this.client.method_1507(new AddServerScreen(MultiplayerScreen.this, MultiplayerScreen.this.field_3051));
			}
		});
		this.field_3041 = this.addButton(
			new class_4185(this.screenWidth / 2 - 154, this.screenHeight - 28, 70, 20, I18n.translate("selectServer.edit")) {
				@Override
				public void method_1826() {
					EntryListWidget.Entry<?> entry = MultiplayerScreen.this.field_3043.getIndex() < 0
						? null
						: (EntryListWidget.Entry)MultiplayerScreen.this.field_3043.getInputListeners().get(MultiplayerScreen.this.field_3043.getIndex());
					MultiplayerScreen.this.field_3036 = true;
					if (entry instanceof RemoteServerListEntry) {
						ServerEntry serverEntry = ((RemoteServerListEntry)entry).method_2556();
						MultiplayerScreen.this.field_3051 = new ServerEntry(serverEntry.name, serverEntry.address, false);
						MultiplayerScreen.this.field_3051.copyFrom(serverEntry);
						MultiplayerScreen.this.client.method_1507(new AddServerScreen(MultiplayerScreen.this, MultiplayerScreen.this.field_3051));
					}
				}
			}
		);
		this.field_3047 = this.addButton(
			new class_4185(this.screenWidth / 2 - 74, this.screenHeight - 28, 70, 20, I18n.translate("selectServer.delete")) {
				@Override
				public void method_1826() {
					EntryListWidget.Entry<?> entry = MultiplayerScreen.this.field_3043.getIndex() < 0
						? null
						: (EntryListWidget.Entry)MultiplayerScreen.this.field_3043.getInputListeners().get(MultiplayerScreen.this.field_3043.getIndex());
					if (entry instanceof RemoteServerListEntry) {
						String string = ((RemoteServerListEntry)entry).method_2556().name;
						if (string != null) {
							MultiplayerScreen.this.field_3039 = true;
							String string2 = I18n.translate("selectServer.deleteQuestion");
							String string3 = I18n.translate("selectServer.deleteWarning", string);
							String string4 = I18n.translate("selectServer.deleteButton");
							String string5 = I18n.translate("gui.cancel");
							YesNoScreen yesNoScreen = new YesNoScreen(MultiplayerScreen.this, string2, string3, string4, string5, MultiplayerScreen.this.field_3043.getIndex());
							MultiplayerScreen.this.client.method_1507(yesNoScreen);
						}
					}
				}
			}
		);
		this.addButton(new class_4185(this.screenWidth / 2 + 4, this.screenHeight - 28, 70, 20, I18n.translate("selectServer.refresh")) {
			@Override
			public void method_1826() {
				MultiplayerScreen.this.method_2534();
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4 + 76, this.screenHeight - 28, 75, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				MultiplayerScreen.this.client.method_1507(MultiplayerScreen.this.parent);
			}
		});
		this.setIndex(this.field_3043.getIndex());
	}

	@Override
	public void update() {
		super.update();
		if (this.field_3046.needsUpdate()) {
			List<LanServerEntry> list = this.field_3046.getServers();
			this.field_3046.markClean();
			this.field_3043.setLanServers(list);
		}

		this.field_3037.method_3000();
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
		if (this.field_3045 != null) {
			this.field_3045.interrupt();
			this.field_3045 = null;
		}

		this.field_3037.method_3004();
	}

	private void method_2534() {
		this.client.method_1507(new MultiplayerScreen(this.parent));
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		EntryListWidget.Entry<?> entry = this.field_3043.getIndex() < 0
			? null
			: (EntryListWidget.Entry)this.field_3043.getInputListeners().get(this.field_3043.getIndex());
		if (this.field_3039) {
			this.field_3039 = false;
			if (bl && entry instanceof RemoteServerListEntry) {
				this.field_3040.remove(this.field_3043.getIndex());
				this.field_3040.saveFile();
				this.field_3043.setIndex(-1);
				this.field_3043.method_2564(this.field_3040);
			}

			this.client.method_1507(this);
		} else if (this.field_3035) {
			this.field_3035 = false;
			if (bl) {
				this.method_2548(this.field_3051);
			} else {
				this.client.method_1507(this);
			}
		} else if (this.field_3038) {
			this.field_3038 = false;
			if (bl) {
				this.field_3040.add(this.field_3051);
				this.field_3040.saveFile();
				this.field_3043.setIndex(-1);
				this.field_3043.method_2564(this.field_3040);
			}

			this.client.method_1507(this);
		} else if (this.field_3036) {
			this.field_3036 = false;
			if (bl && entry instanceof RemoteServerListEntry) {
				ServerEntry serverEntry = ((RemoteServerListEntry)entry).method_2556();
				serverEntry.name = this.field_3051.name;
				serverEntry.address = this.field_3051.address;
				serverEntry.copyFrom(this.field_3051);
				this.field_3040.saveFile();
				this.field_3043.method_2564(this.field_3040);
			}

			this.client.method_1507(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			int l = this.field_3043.getIndex();
			if (i == 294) {
				this.method_2534();
				return true;
			} else if (l < 0 || i != 257 && i != 335) {
				return false;
			} else {
				this.method_2536();
				return true;
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.field_3042 = null;
		this.drawBackground();
		this.field_3043.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("multiplayer.title"), this.screenWidth / 2, 20, 16777215);
		super.draw(i, j, f);
		if (this.field_3042 != null) {
			this.drawTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3042)), i, j);
		}
	}

	public void method_2536() {
		EntryListWidget.Entry<?> entry = this.field_3043.getIndex() < 0
			? null
			: (EntryListWidget.Entry)this.field_3043.getInputListeners().get(this.field_3043.getIndex());
		if (entry instanceof RemoteServerListEntry) {
			this.method_2548(((RemoteServerListEntry)entry).method_2556());
		} else if (entry instanceof LocalServerListEntry) {
			LanServerEntry lanServerEntry = ((LocalServerListEntry)entry).method_2559();
			this.method_2548(new ServerEntry(lanServerEntry.getMotd(), lanServerEntry.getAddressPort(), true));
		}
	}

	private void method_2548(ServerEntry serverEntry) {
		this.client.method_1507(new ServerConnectingScreen(this, this.client, serverEntry));
	}

	public void setIndex(int i) {
		this.field_3043.setIndex(i);
		EntryListWidget.Entry<?> entry = i < 0 ? null : (EntryListWidget.Entry)this.field_3043.getInputListeners().get(i);
		this.field_3050.enabled = false;
		this.field_3041.enabled = false;
		this.field_3047.enabled = false;
		if (entry != null && !(entry instanceof LocalScanProgressListEntry)) {
			this.field_3050.enabled = true;
			if (entry instanceof RemoteServerListEntry) {
				this.field_3041.enabled = true;
				this.field_3047.enabled = true;
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

	public boolean method_2533(RemoteServerListEntry remoteServerListEntry, int i) {
		return i > 0;
	}

	public boolean method_2547(RemoteServerListEntry remoteServerListEntry, int i) {
		return i < this.field_3040.size() - 1;
	}

	public void method_2531(RemoteServerListEntry remoteServerListEntry, int i, boolean bl) {
		int j = bl ? 0 : i - 1;
		this.field_3040.swapEntries(i, j);
		if (this.field_3043.getIndex() == i) {
			this.setIndex(j);
		}

		this.field_3043.method_2564(this.field_3040);
	}

	public void method_2553(RemoteServerListEntry remoteServerListEntry, int i, boolean bl) {
		int j = bl ? this.field_3040.size() - 1 : i + 1;
		this.field_3040.swapEntries(i, j);
		if (this.field_3043.getIndex() == i) {
			this.setIndex(j);
		}

		this.field_3043.method_2564(this.field_3040);
	}

	public void method_19414(int i) {
		this.setIndex(MathHelper.clamp(this.field_3043.getIndex() + i, 0, this.field_3043.getInputListeners().size() - 1));
		this.field_3043.scroll(this.field_3043.getEntryHeight() * i);
	}
}
