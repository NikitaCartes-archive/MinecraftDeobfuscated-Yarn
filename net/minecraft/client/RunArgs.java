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

    public RunArgs(Network network, WindowSettings windowSettings, Directories directories, Game game, AutoConnect autoConnect) {
        this.network = network;
        this.windowSettings = windowSettings;
        this.directories = directories;
        this.game = game;
        this.autoConnect = autoConnect;
    }

    @Environment(value=EnvType.CLIENT)
    public static class AutoConnect {
        @Nullable
        public final String serverAddress;
        public final int serverPort;

        public AutoConnect(@Nullable String string, int i) {
            this.serverAddress = string;
            this.serverPort = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Directories {
        public final File runDir;
        public final File resourcePackDir;
        public final File assetDir;
        @Nullable
        public final String assetIndex;

        public Directories(File file, File file2, File file3, @Nullable String string) {
            this.runDir = file;
            this.resourcePackDir = file2;
            this.assetDir = file3;
            this.assetIndex = string;
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

        public Network(Session session, PropertyMap propertyMap, PropertyMap propertyMap2, Proxy proxy) {
            this.session = session;
            this.field_3298 = propertyMap;
            this.profileProperties = propertyMap2;
            this.netProxy = proxy;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Game {
        public final boolean demo;
        public final String version;
        public final String versionType;

        public Game(boolean bl, String string, String string2) {
            this.demo = bl;
            this.version = string;
            this.versionType = string2;
        }
    }
}

