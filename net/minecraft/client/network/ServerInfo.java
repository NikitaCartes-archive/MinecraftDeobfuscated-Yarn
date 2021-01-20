/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

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
    private ResourcePackState resourcePackState = ResourcePackState.PROMPT;
    @Nullable
    private String icon;
    private boolean local;

    public ServerInfo(String name, String address, boolean local) {
        this.name = name;
        this.address = address;
        this.local = local;
    }

    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("name", this.name);
        compoundTag.putString("ip", this.address);
        if (this.icon != null) {
            compoundTag.putString("icon", this.icon);
        }
        if (this.resourcePackState == ResourcePackState.ENABLED) {
            compoundTag.putBoolean("acceptTextures", true);
        } else if (this.resourcePackState == ResourcePackState.DISABLED) {
            compoundTag.putBoolean("acceptTextures", false);
        }
        return compoundTag;
    }

    public ResourcePackState getResourcePack() {
        return this.resourcePackState;
    }

    public void setResourcePackState(ResourcePackState resourcePackState) {
        this.resourcePackState = resourcePackState;
    }

    public static ServerInfo deserialize(CompoundTag tag) {
        ServerInfo serverInfo = new ServerInfo(tag.getString("name"), tag.getString("ip"), false);
        if (tag.contains("icon", 8)) {
            serverInfo.setIcon(tag.getString("icon"));
        }
        if (tag.contains("acceptTextures", 1)) {
            if (tag.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackState(ResourcePackState.ENABLED);
            } else {
                serverInfo.setResourcePackState(ResourcePackState.DISABLED);
            }
        } else {
            serverInfo.setResourcePackState(ResourcePackState.PROMPT);
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
        this.setResourcePackState(serverInfo.getResourcePack());
        this.icon = serverInfo.icon;
        this.local = serverInfo.local;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackState {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackState(String name) {
            this.name = new TranslatableText("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }
    }
}

