package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.options.ServerList;

@Environment(EnvType.CLIENT)
public class ServerListWidget extends EntryListWidget<ServerListWidget.Entry> {
	private final MultiplayerScreen multiplayerScreen;
	private final List<RemoteServerListEntry> userServers = Lists.<RemoteServerListEntry>newArrayList();
	private final ServerListWidget.Entry localScanWidget = new LocalScanProgressListEntry();
	private final List<LocalServerListEntry> lanServers = Lists.<LocalServerListEntry>newArrayList();
	private int selectedIndex = -1;

	private void method_2560() {
		this.clearEntries();
		this.userServers.forEach(this::addEntry);
		this.addEntry(this.localScanWidget);
		this.lanServers.forEach(this::addEntry);
	}

	public ServerListWidget(MultiplayerScreen multiplayerScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.multiplayerScreen = multiplayerScreen;
	}

	public void setIndex(int i) {
		this.selectedIndex = i;
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return i == this.selectedIndex;
	}

	@Override
	protected void moveSelection(int i) {
		this.multiplayerScreen.method_19414(i);
		if (this.getInputListeners().get(this.getIndex()) instanceof LocalScanProgressListEntry) {
			this.multiplayerScreen.method_19414(i > 0 ? 1 : -1);
		}
	}

	public int getIndex() {
		return this.selectedIndex;
	}

	public void setUserServers(ServerList serverList) {
		this.userServers.clear();

		for (int i = 0; i < serverList.size(); i++) {
			this.userServers.add(new RemoteServerListEntry(this.multiplayerScreen, serverList.get(i)));
		}

		this.method_2560();
	}

	public void setLanServers(List<LanServerEntry> list) {
		this.lanServers.clear();

		for (LanServerEntry lanServerEntry : list) {
			this.lanServers.add(new LocalServerListEntry(this.multiplayerScreen, lanServerEntry));
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

	@Override
	public boolean isPartOfFocusCycle() {
		return true;
	}

	@Override
	protected boolean isFocused() {
		return this.multiplayerScreen.getFocused() == this;
	}

	@Override
	public void onFocusChanged(boolean bl) {
		if (bl && this.getIndex() < 0 && this.getEntryCount() > 0) {
			this.moveSelection(1);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends EntryListWidget.Entry<ServerListWidget.Entry> {
	}
}
