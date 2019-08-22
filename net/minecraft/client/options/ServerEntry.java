/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ServerEntry {
    public String name;
    public String address;
    public String playerCountLabel;
    public String label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public String version = SharedConstants.getGameVersion().getName();
    public boolean online;
    public String playerListSummary;
    private ResourcePackState resourcePackState = ResourcePackState.PROMPT;
    private String icon;
    private boolean local;

    public ServerEntry(String string, String string2, boolean bl) {
        this.name = string;
        this.address = string2;
        this.local = bl;
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

    public static ServerEntry deserialize(CompoundTag compoundTag) {
        ServerEntry serverEntry = new ServerEntry(compoundTag.getString("name"), compoundTag.getString("ip"), false);
        if (compoundTag.containsKey("icon", 8)) {
            serverEntry.setIcon(compoundTag.getString("icon"));
        }
        if (compoundTag.containsKey("acceptTextures", 1)) {
            if (compoundTag.getBoolean("acceptTextures")) {
                serverEntry.setResourcePackState(ResourcePackState.ENABLED);
            } else {
                serverEntry.setResourcePackState(ResourcePackState.DISABLED);
            }
        } else {
            serverEntry.setResourcePackState(ResourcePackState.PROMPT);
        }
        return serverEntry;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(@Nullable String string) {
        this.icon = string;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void copyFrom(ServerEntry serverEntry) {
        this.address = serverEntry.address;
        this.name = serverEntry.name;
        this.setResourcePackState(serverEntry.getResourcePack());
        this.icon = serverEntry.icon;
        this.local = serverEntry.local;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackState {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackState(String string2) {
            this.name = new TranslatableText("addServer.resourcePack." + string2, new Object[0]);
        }

        public Text getName() {
            return this.name;
        }
    }
}

