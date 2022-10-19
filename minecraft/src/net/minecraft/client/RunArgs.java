package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.Session;

@Environment(EnvType.CLIENT)
public class RunArgs {
	public final RunArgs.Network network;
	public final WindowSettings windowSettings;
	public final RunArgs.Directories directories;
	public final RunArgs.Game game;
	public final RunArgs.AutoConnect autoConnect;

	public RunArgs(RunArgs.Network network, WindowSettings windowSettings, RunArgs.Directories dirs, RunArgs.Game game, RunArgs.AutoConnect autoConnect) {
		this.network = network;
		this.windowSettings = windowSettings;
		this.directories = dirs;
		this.game = game;
		this.autoConnect = autoConnect;
	}

	@Environment(EnvType.CLIENT)
	public static class AutoConnect {
		@Nullable
		public final String serverAddress;
		public final int serverPort;

		public AutoConnect(@Nullable String serverAddress, int serverPort) {
			this.serverAddress = serverAddress;
			this.serverPort = serverPort;
		}
	}

	@Environment(EnvType.CLIENT)
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

		public Path getAssetDir() {
			return this.assetIndex == null ? this.assetDir.toPath() : ResourceIndex.buildFileSystem(this.assetDir.toPath(), this.assetIndex);
		}
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public static class Network {
		public final Session session;
		public final PropertyMap userProperties;
		public final PropertyMap profileProperties;
		public final Proxy netProxy;

		public Network(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
			this.session = session;
			this.userProperties = userProperties;
			this.profileProperties = profileProperties;
			this.netProxy = proxy;
		}
	}
}
