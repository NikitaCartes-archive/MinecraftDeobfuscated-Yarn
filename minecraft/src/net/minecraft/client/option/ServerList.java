package net.minecraft.client.option;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.slf4j.Logger;

/**
 * A list of {@link ServerInfo}. The list can contain an unlimited amount of
 * {@linkplain #servers server entries that are displayed on the multiplayer screen},
 * and up to {@value #MAX_HIDDEN_ENTRIES} {@linkplain #hiddenServers entries of servers}
 * that are created when using "Direct Connection" and is hidden from the screen.
 */
@Environment(EnvType.CLIENT)
public class ServerList {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final TaskExecutor<Runnable> IO_EXECUTOR = TaskExecutor.create(Util.getMainWorkerExecutor(), "server-list-io");
	private static final int MAX_HIDDEN_ENTRIES = 16;
	private final MinecraftClient client;
	private final List<ServerInfo> servers = Lists.<ServerInfo>newArrayList();
	private final List<ServerInfo> hiddenServers = Lists.<ServerInfo>newArrayList();

	public ServerList(MinecraftClient client) {
		this.client = client;
		this.loadFile();
	}

	public void loadFile() {
		try {
			this.servers.clear();
			this.hiddenServers.clear();
			NbtCompound nbtCompound = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
			if (nbtCompound == null) {
				return;
			}

			NbtList nbtList = nbtCompound.getList("servers", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				ServerInfo serverInfo = ServerInfo.fromNbt(nbtCompound2);
				if (nbtCompound2.getBoolean("hidden")) {
					this.hiddenServers.add(serverInfo);
				} else {
					this.servers.add(serverInfo);
				}
			}
		} catch (Exception var6) {
			LOGGER.error("Couldn't load server list", (Throwable)var6);
		}
	}

	public void saveFile() {
		try {
			NbtList nbtList = new NbtList();

			for (ServerInfo serverInfo : this.servers) {
				NbtCompound nbtCompound = serverInfo.toNbt();
				nbtCompound.putBoolean("hidden", false);
				nbtList.add(nbtCompound);
			}

			for (ServerInfo serverInfo : this.hiddenServers) {
				NbtCompound nbtCompound = serverInfo.toNbt();
				nbtCompound.putBoolean("hidden", true);
				nbtList.add(nbtCompound);
			}

			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("servers", nbtList);
			File file = File.createTempFile("servers", ".dat", this.client.runDirectory);
			NbtIo.write(nbtCompound2, file);
			File file2 = new File(this.client.runDirectory, "servers.dat_old");
			File file3 = new File(this.client.runDirectory, "servers.dat");
			Util.backupAndReplace(file3, file, file2);
		} catch (Exception var6) {
			LOGGER.error("Couldn't save server list", (Throwable)var6);
		}
	}

	public ServerInfo get(int index) {
		return (ServerInfo)this.servers.get(index);
	}

	/**
	 * {@return the server info for {@code address}, or {@code null} if there is no such one}
	 */
	@Nullable
	public ServerInfo get(String address) {
		for (ServerInfo serverInfo : this.servers) {
			if (serverInfo.address.equals(address)) {
				return serverInfo;
			}
		}

		for (ServerInfo serverInfox : this.hiddenServers) {
			if (serverInfox.address.equals(address)) {
				return serverInfox;
			}
		}

		return null;
	}

	/**
	 * {@return the previously hidden server info for the address {@code address}, or
	 * {@code null} if there is no such info}
	 * 
	 * <p>This "unhides" the server info and is used when adding the entry to the
	 * multiplayer screen to unhide any existing server info created when connecting
	 * directly.
	 */
	@Nullable
	public ServerInfo tryUnhide(String address) {
		for (int i = 0; i < this.hiddenServers.size(); i++) {
			ServerInfo serverInfo = (ServerInfo)this.hiddenServers.get(i);
			if (serverInfo.address.equals(address)) {
				this.hiddenServers.remove(i);
				this.servers.add(serverInfo);
				return serverInfo;
			}
		}

		return null;
	}

	public void remove(ServerInfo serverInfo) {
		if (!this.servers.remove(serverInfo)) {
			this.hiddenServers.remove(serverInfo);
		}
	}

	/**
	 * Adds a server info to this list.
	 * 
	 * @param hidden whether the info should not be listed in the multiplayer screen (also called
	 * "direct connection")
	 */
	public void add(ServerInfo serverInfo, boolean hidden) {
		if (hidden) {
			this.hiddenServers.add(0, serverInfo);

			while (this.hiddenServers.size() > 16) {
				this.hiddenServers.remove(this.hiddenServers.size() - 1);
			}
		} else {
			this.servers.add(serverInfo);
		}
	}

	public int size() {
		return this.servers.size();
	}

	public void swapEntries(int index1, int index2) {
		ServerInfo serverInfo = this.get(index1);
		this.servers.set(index1, this.get(index2));
		this.servers.set(index2, serverInfo);
		this.saveFile();
	}

	public void set(int index, ServerInfo serverInfo) {
		this.servers.set(index, serverInfo);
	}

	/**
	 * Replaces the server info in {@code serverInfos} whose name and address match
	 * {@code serverInfo}'s with {@code serverInfo}.
	 */
	private static boolean replace(ServerInfo serverInfo, List<ServerInfo> serverInfos) {
		for (int i = 0; i < serverInfos.size(); i++) {
			ServerInfo serverInfo2 = (ServerInfo)serverInfos.get(i);
			if (serverInfo2.name.equals(serverInfo.name) && serverInfo2.address.equals(serverInfo.address)) {
				serverInfos.set(i, serverInfo);
				return true;
			}
		}

		return false;
	}

	public static void updateServerListEntry(ServerInfo serverInfo) {
		IO_EXECUTOR.send(() -> {
			ServerList serverList = new ServerList(MinecraftClient.getInstance());
			serverList.loadFile();
			if (!replace(serverInfo, serverList.servers)) {
				replace(serverInfo, serverList.hiddenServers);
			}

			serverList.saveFile();
		});
	}
}
