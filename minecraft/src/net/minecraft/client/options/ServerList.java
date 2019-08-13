package net.minecraft.client.options;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerList {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<ServerEntry> serverEntries = Lists.<ServerEntry>newArrayList();

	public ServerList(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.loadFile();
	}

	public void loadFile() {
		try {
			this.serverEntries.clear();
			CompoundTag compoundTag = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
			if (compoundTag == null) {
				return;
			}

			ListTag listTag = compoundTag.getList("servers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				this.serverEntries.add(ServerEntry.deserialize(listTag.getCompoundTag(i)));
			}
		} catch (Exception var4) {
			LOGGER.error("Couldn't load server list", (Throwable)var4);
		}
	}

	public void saveFile() {
		try {
			ListTag listTag = new ListTag();

			for (ServerEntry serverEntry : this.serverEntries) {
				listTag.add(serverEntry.serialize());
			}

			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("servers", listTag);
			NbtIo.safeWrite(compoundTag, new File(this.client.runDirectory, "servers.dat"));
		} catch (Exception var4) {
			LOGGER.error("Couldn't save server list", (Throwable)var4);
		}
	}

	public ServerEntry get(int i) {
		return (ServerEntry)this.serverEntries.get(i);
	}

	public void remove(ServerEntry serverEntry) {
		this.serverEntries.remove(serverEntry);
	}

	public void add(ServerEntry serverEntry) {
		this.serverEntries.add(serverEntry);
	}

	public int size() {
		return this.serverEntries.size();
	}

	public void swapEntries(int i, int j) {
		ServerEntry serverEntry = this.get(i);
		this.serverEntries.set(i, this.get(j));
		this.serverEntries.set(j, serverEntry);
		this.saveFile();
	}

	public void set(int i, ServerEntry serverEntry) {
		this.serverEntries.set(i, serverEntry);
	}

	public static void updateServerListEntry(ServerEntry serverEntry) {
		ServerList serverList = new ServerList(MinecraftClient.getInstance());
		serverList.loadFile();

		for (int i = 0; i < serverList.size(); i++) {
			ServerEntry serverEntry2 = serverList.get(i);
			if (serverEntry2.name.equals(serverEntry.name) && serverEntry2.address.equals(serverEntry.address)) {
				serverList.set(i, serverEntry);
				break;
			}
		}

		serverList.saveFile();
	}
}
