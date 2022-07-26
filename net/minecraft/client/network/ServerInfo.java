/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * The information of a server entry in the list of servers available in
 * the multiplayer screen, or that of the servers connected directly.
 * The information for directly-connected servers are also saved (although
 * hidden from the multiplayer screen) so that chat preview acknowledgements
 * and other settings are saved. The list of these servers is stored in the
 * {@code servers.dat} file within the client game directory.
 * 
 * @see net.minecraft.client.option.ServerList
 */
@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = Text.literal(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    @Nullable
    private String icon;
    private boolean local;
    @Nullable
    private ChatPreview chatPreview;
    private boolean temporaryChatPreviewState = true;
    private boolean secureChatEnforced;

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
        if (this.chatPreview != null) {
            ChatPreview.CODEC.encodeStart(NbtOps.INSTANCE, this.chatPreview).result().ifPresent(chatPreview -> nbtCompound.put("chatPreview", (NbtElement)chatPreview));
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
        if (root.contains("icon", NbtElement.STRING_TYPE)) {
            serverInfo.setIcon(root.getString("icon"));
        }
        if (root.contains("acceptTextures", NbtElement.BYTE_TYPE)) {
            if (root.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.ENABLED);
            } else {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.DISABLED);
            }
        } else {
            serverInfo.setResourcePackPolicy(ResourcePackPolicy.PROMPT);
        }
        if (root.contains("chatPreview", NbtElement.COMPOUND_TYPE)) {
            ChatPreview.CODEC.parse(NbtOps.INSTANCE, root.getCompound("chatPreview")).resultOrPartial(LOGGER::error).ifPresent(chatPreview -> {
                serverInfo.chatPreview = chatPreview;
            });
        }
        return serverInfo;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public static String parseFavicon(String favicon) throws ParseException {
        if (favicon.startsWith("data:image/png;base64,")) {
            return favicon.substring("data:image/png;base64,".length());
        }
        throw new ParseException("Unknown format", 0);
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public boolean isLocal() {
        return this.local;
    }

    /**
     * Sets whether the chat preview is enabled. This affects the saved server info;
     * to disable the chat preview temporarily use {@link #setTemporaryChatPreviewState}.
     */
    public void setPreviewsChat(boolean enabled) {
        if (enabled && this.chatPreview == null) {
            this.chatPreview = new ChatPreview(false, false);
        } else if (!enabled && this.chatPreview != null) {
            this.chatPreview = null;
        }
    }

    @Nullable
    public ChatPreview getChatPreview() {
        return this.chatPreview;
    }

    /**
     * Sets the temporary chat preview state. Unlike {@link #setPreviewsChat}, this
     * does not affect the saved server info.
     */
    public void setTemporaryChatPreviewState(boolean temporaryChatPreviewState) {
        this.temporaryChatPreviewState = temporaryChatPreviewState;
    }

    public boolean shouldPreviewChat() {
        return this.temporaryChatPreviewState && this.chatPreview != null;
    }

    public void setSecureChatEnforced(boolean secureChatEnforced) {
        this.secureChatEnforced = secureChatEnforced;
    }

    public boolean isSecureChatEnforced() {
        return this.secureChatEnforced;
    }

    public void copyFrom(ServerInfo serverInfo) {
        this.address = serverInfo.address;
        this.name = serverInfo.name;
        this.icon = serverInfo.icon;
    }

    public void copyWithSettingsFrom(ServerInfo serverInfo) {
        this.copyFrom(serverInfo);
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.local = serverInfo.local;
        this.chatPreview = Util.map(serverInfo.chatPreview, ChatPreview::copy);
        this.secureChatEnforced = serverInfo.secureChatEnforced;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackPolicy {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackPolicy(String name) {
            this.name = Text.translatable("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChatPreview {
        public static final Codec<ChatPreview> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.BOOL.optionalFieldOf("acknowledged", false).forGetter(chatPreview -> chatPreview.acknowledged), Codec.BOOL.optionalFieldOf("toastShown", false).forGetter(chatPreview -> chatPreview.toastShown)).apply((Applicative<ChatPreview, ?>)instance, ChatPreview::new));
        private boolean acknowledged;
        private boolean toastShown;

        ChatPreview(boolean acknowledged, boolean toastShown) {
            this.acknowledged = acknowledged;
            this.toastShown = toastShown;
        }

        public void setAcknowledged() {
            this.acknowledged = true;
        }

        public boolean showToast() {
            if (!this.toastShown) {
                this.toastShown = true;
                return true;
            }
            return false;
        }

        public boolean isAcknowledged() {
            return this.acknowledged;
        }

        private ChatPreview copy() {
            return new ChatPreview(this.acknowledged, this.toastShown);
        }
    }
}

