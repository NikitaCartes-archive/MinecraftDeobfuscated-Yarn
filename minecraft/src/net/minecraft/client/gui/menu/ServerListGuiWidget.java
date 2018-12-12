package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.options.ServerList;

@Environment(EnvType.CLIENT)
public class ServerListGuiWidget extends EntryListWidget<ServerListGuiWidget.Entry> {
	private final MultiplayerGui guiMultiplayer;
	private final List<RemoteServerListEntry> userServers = Lists.<RemoteServerListEntry>newArrayList();
	private final ServerListGuiWidget.Entry localScanWidget = new LocalScanProgressListEntry();
	private final List<LocalServerListEntry> lanServers = Lists.<LocalServerListEntry>newArrayList();
	private int selectedIndex = -1;

	private void method_2560() {
		this.clearEntries();
		this.userServers.forEach(this::addEntry);
		this.addEntry(this.localScanWidget);
		this.lanServers.forEach(this::addEntry);
	}

	public ServerListGuiWidget(MultiplayerGui multiplayerGui, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.guiMultiplayer = multiplayerGui;
	}

	public void setIndex(int i) {
		this.selectedIndex = i;
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return i == this.selectedIndex;
	}

	public int getIndex() {
		return this.selectedIndex;
	}

	public void method_2564(ServerList serverList) {
		this.userServers.clear();

		for (int i = 0; i < serverList.size(); i++) {
			this.userServers.add(new RemoteServerListEntry(this.guiMultiplayer, serverList.get(i)));
		}

		this.method_2560();
	}

	public void setLanServers(List<LanServerEntry> list) {
		this.lanServers.clear();

		for (LanServerEntry lanServerEntry : list) {
			this.lanServers.add(new LocalServerListEntry(this.guiMultiplayer, lanServerEntry));
		}

		this.method_2560();
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	@Override
	public int getEntryWidth() {
		return super.getEntryWidth() + 85;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends EntryListWidget.Entry<ServerListGuiWidget.Entry> {
	}
}
