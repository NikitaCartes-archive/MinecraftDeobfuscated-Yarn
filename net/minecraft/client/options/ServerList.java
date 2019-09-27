/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<ServerInfo> servers = Lists.newArrayList();

    public ServerList(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
        this.loadFile();
    }

    public void loadFile() {
        try {
            this.servers.clear();
            CompoundTag compoundTag = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
            if (compoundTag == null) {
                return;
            }
            ListTag listTag = compoundTag.getList("servers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                this.servers.add(ServerInfo.deserialize(listTag.getCompound(i)));
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void saveFile() {
        try {
            ListTag listTag = new ListTag();
            for (ServerInfo serverInfo : this.servers) {
                listTag.add(serverInfo.serialize());
            }
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("servers", listTag);
            NbtIo.safeWrite(compoundTag, new File(this.client.runDirectory, "servers.dat"));
        } catch (Exception exception) {
            LOGGER.error("Couldn't save server list", (Throwable)exception);
        }
    }

    public ServerInfo get(int i) {
        return this.servers.get(i);
    }

    public void remove(ServerInfo serverInfo) {
        this.servers.remove(serverInfo);
    }

    public void add(ServerInfo serverInfo) {
        this.servers.add(serverInfo);
    }

    public int size() {
        return this.servers.size();
    }

    public void swapEntries(int i, int j) {
        ServerInfo serverInfo = this.get(i);
        this.servers.set(i, this.get(j));
        this.servers.set(j, serverInfo);
        this.saveFile();
    }

    public void set(int i, ServerInfo serverInfo) {
        this.servers.set(i, serverInfo);
    }

    public static void updateServerListEntry(ServerInfo serverInfo) {
        ServerList serverList = new ServerList(MinecraftClient.getInstance());
        serverList.loadFile();
        for (int i = 0; i < serverList.size(); ++i) {
            ServerInfo serverInfo2 = serverList.get(i);
            if (!serverInfo2.name.equals(serverInfo.name) || !serverInfo2.address.equals(serverInfo.address)) continue;
            serverList.set(i, serverInfo);
            break;
        }
        serverList.saveFile();
    }
}

