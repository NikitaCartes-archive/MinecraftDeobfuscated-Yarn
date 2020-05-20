/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.resource.DirectResourceIndex;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.Session;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RunArgs {
    public final Network network;
    public final WindowSettings windowSettings;
    public final Directories directories;
    public final Game game;
    public final AutoConnect autoConnect;

    public RunArgs(Network network, WindowSettings windowSettings, Directories dirs, Game game, AutoConnect autoConnect) {
        this.network = network;
        this.windowSettings = windowSettings;
        this.directories = dirs;
        this.game = game;
        this.autoConnect = autoConnect;
    }

    @Environment(value=EnvType.CLIENT)
    public static class AutoConnect {
        @Nullable
        public final String serverAddress;
        public final int serverPort;

        public AutoConnect(@Nullable String serverAddress, int serverPort) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Directories {
        public final File runDir;
        public final File resourcePackDir;
        public final File assetDir;
        @Nullable
        public final String assetIndex;

        public Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
            this.runDir = runDir;
            this.resourcePackDir = resPackDir;
            this.assetDir = assetDir;
            this.assetIndex = assetIndex;
        }

        public ResourceIndex getResourceIndex() {
            return this.assetIndex == null ? new DirectResourceIndex(this.assetDir) : new ResourceIndex(this.assetDir, this.assetIndex);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Network {
        public final Session session;
        public final PropertyMap field_3298;
        public final PropertyMap profileProperties;
        public final Proxy netProxy;

        public Network(Session session, PropertyMap propertyMap, PropertyMap profileProperties, Proxy proxy) {
            this.session = session;
            this.field_3298 = propertyMap;
            this.profileProperties = profileProperties;
            this.netProxy = proxy;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Game {
        public final boolean demo;
        public final String version;
        public final String versionType;
        public final boolean multiplayerDisabled;
        public final boolean onlineChatDisabled;

        public Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled) {
            this.demo = demo;
            this.version = version;
            this.versionType = versionType;
            this.multiplayerDisabled = multiplayerDisabled;
            this.onlineChatDisabled = onlineChatDisabled;
        }
    }
}

