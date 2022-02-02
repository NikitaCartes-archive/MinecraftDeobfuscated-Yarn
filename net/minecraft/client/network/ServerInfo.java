/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

/**
 * The information of a server entry in the list of servers available in
 * the multiplayer screen from the menu. The list of these servers is
 * stored in the {@code servers.dat} file within the client game directory.
 */
@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = new LiteralText(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    @Nullable
    private String icon;
    private boolean local;

    public ServerInfo(String name, String address, boolean local) {
        this.name = name;
        this.address = address;
        this.local = local;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", this.name);
        nbtCompound.putString("ip", this.address);
        if (this.icon != null) {
            nbtCompound.putString("icon", this.icon);
        }
        if (this.resourcePackPolicy == ResourcePackPolicy.ENABLED) {
            nbtCompound.putBoolean("acceptTextures", true);
        } else if (this.resourcePackPolicy == ResourcePackPolicy.DISABLED) {
            nbtCompound.putBoolean("acceptTextures", false);
        }
        return nbtCompound;
    }

    /**
     * {@return the policy on resource packs sent by this server}
     */
    public ResourcePackPolicy getResourcePackPolicy() {
        return this.resourcePackPolicy;
    }

    /**
     * Sets the resource pack policy on this server.
     * 
     * <p>This is called when a user has responded to the prompt on whether to
     * accept server resource packs from this server in the future.
     */
    public void setResourcePackPolicy(ResourcePackPolicy resourcePackPolicy) {
        this.resourcePackPolicy = resourcePackPolicy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
        if (root.contains("icon", 8)) {
            serverInfo.setIcon(root.getString("icon"));
        }
        if (root.contains("acceptTextures", 1)) {
            if (root.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.ENABLED);
            } else {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.DISABLED);
            }
        } else {
            serverInfo.setResourcePackPolicy(ResourcePackPolicy.PROMPT);
        }
        return serverInfo;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void copyFrom(ServerInfo serverInfo) {
        this.address = serverInfo.address;
        this.name = serverInfo.name;
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.icon = serverInfo.icon;
        this.local = serverInfo.local;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackPolicy {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackPolicy(String name) {
            this.name = new TranslatableText("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }
    }
}

