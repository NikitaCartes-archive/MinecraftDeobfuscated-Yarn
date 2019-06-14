package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.DirectResourceIndex;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.Session;

@Environment(EnvType.CLIENT)
public class RunArgs {
	public final RunArgs.Network network;
	public final WindowSettings windowSettings;
	public final RunArgs.Directories directories;
	public final RunArgs.Game game;
	public final RunArgs.AutoConnect autoConnect;

	public RunArgs(RunArgs.Network network, WindowSettings windowSettings, RunArgs.Directories directories, RunArgs.Game game, RunArgs.AutoConnect autoConnect) {
		this.network = network;
		this.windowSettings = windowSettings;
		this.directories = directories;
		this.game = game;
		this.autoConnect = autoConnect;
	}

	@Environment(EnvType.CLIENT)
	public static class AutoConnect {
		public final String serverIP;
		public final int serverPort;

		public AutoConnect(String string, int i) {
			this.serverIP = string;
			this.serverPort = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Directories {
		public final File runDir;
		public final File resourcePackDir;
		public final File assetDir;
		public final String assetIndex;

		public Directories(File file, File file2, File file3, @Nullable String string) {
			this.runDir = file;
			this.resourcePackDir = file2;
			this.assetDir = file3;
			this.assetIndex = string;
		}

		public ResourceIndex method_2788() {
			return (ResourceIndex)(this.assetIndex == null ? new DirectResourceIndex(this.assetDir) : new ResourceIndex(this.assetDir, this.assetIndex));
		}
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
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
}
