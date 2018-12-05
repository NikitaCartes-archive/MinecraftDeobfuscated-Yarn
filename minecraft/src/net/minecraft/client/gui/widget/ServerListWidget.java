package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.ListEntryLocalScanProgress;
import net.minecraft.client.gui.menu.ListEntryLocalServer;
import net.minecraft.client.gui.menu.ListEntryRemoteServer;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.settings.ServerList;

@Environment(EnvType.CLIENT)
public class ServerListWidget extends EntryListWidget<ServerListWidget.class_504> {
	private final MultiplayerGui guiMultiplayer;
	private final List<ListEntryRemoteServer> userServers = Lists.<ListEntryRemoteServer>newArrayList();
	private final ServerListWidget.class_504 localScanWidget = new ListEntryLocalScanProgress();
	private final List<ListEntryLocalServer> lanServers = Lists.<ListEntryLocalServer>newArrayList();
	private int selectedIndex = -1;

	private void method_2560() {
		this.method_1902();
		this.userServers.forEach(this::method_1901);
		this.method_1901(this.localScanWidget);
		this.lanServers.forEach(this::method_1901);
	}

	public ServerListWidget(MultiplayerGui multiplayerGui, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.guiMultiplayer = multiplayerGui;
	}

	public void setIndex(int i) {
		this.selectedIndex = i;
	}

	@Override
	protected boolean isSelected(int i) {
		return i == this.selectedIndex;
	}

	public int getIndex() {
		return this.selectedIndex;
	}

	public void setUserServers(ServerList serverList) {
		this.userServers.clear();

		for (int i = 0; i < serverList.size(); i++) {
			this.userServers.add(new ListEntryRemoteServer(this.guiMultiplayer, serverList.get(i)));
		}

		this.method_2560();
	}

	public void setLanServers(List<LanServerEntry> list) {
		this.lanServers.clear();

		for (LanServerEntry lanServerEntry : list) {
			this.lanServers.add(new ListEntryLocalServer(this.guiMultiplayer, lanServerEntry));
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
	public abstract static class class_504 extends EntryListWidget.Entry<ServerListWidget.class_504> {
	}
}
