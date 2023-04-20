package net.minecraft.client;

import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourceReload;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class QuickPlay {
	public static final Text ERROR_TITLE = Text.translatable("quickplay.error.title");
	private static final Text ERROR_INVALID_IDENTIFIER = Text.translatable("quickplay.error.invalid_identifier");
	private static final Text ERROR_REALM_CONNECT = Text.translatable("quickplay.error.realm_connect");
	private static final Text ERROR_REALM_PERMISSION = Text.translatable("quickplay.error.realm_permission");
	private static final Text TO_TITLE = Text.translatable("gui.toTitle");
	private static final Text TO_WORLD = Text.translatable("gui.toWorld");
	private static final Text TO_REALMS = Text.translatable("gui.toRealms");

	public static void startQuickPlay(MinecraftClient client, RunArgs.QuickPlay quickPlay, ResourceReload resourceReload, RealmsClient realmsClient) {
		String string = quickPlay.singleplayer();
		String string2 = quickPlay.multiplayer();
		String string3 = quickPlay.realms();
		resourceReload.whenComplete().thenRunAsync(() -> {
			if (!StringUtils.isBlank(string)) {
				startSingleplayer(client, string);
			} else if (!StringUtils.isBlank(string2)) {
				startMultiplayer(client, string2);
			} else if (!StringUtils.isBlank(string3)) {
				startRealms(client, realmsClient, string3);
			}
		}, client);
	}

	private static void startSingleplayer(MinecraftClient client, String levelName) {
		if (!client.getLevelStorage().levelExists(levelName)) {
			Screen screen = new SelectWorldScreen(new TitleScreen());
			client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_WORLD));
		} else {
			client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
			client.createIntegratedServerLoader().start(new TitleScreen(), levelName);
		}
	}

	private static void startMultiplayer(MinecraftClient client, String serverAddress) {
		ServerList serverList = new ServerList(client);
		serverList.loadFile();
		ServerInfo serverInfo = serverList.get(serverAddress);
		if (serverInfo == null) {
			serverInfo = new ServerInfo(I18n.translate("selectServer.defaultName"), serverAddress, false);
			serverList.add(serverInfo, true);
			serverList.saveFile();
		}

		ServerAddress serverAddress2 = ServerAddress.parse(serverAddress);
		ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, serverAddress2, serverInfo, true);
	}

	private static void startRealms(MinecraftClient client, RealmsClient realmsClient, String realmId) {
		long l;
		RealmsServerList realmsServerList;
		try {
			l = Long.parseLong(realmId);
			realmsServerList = realmsClient.listWorlds();
		} catch (NumberFormatException var9) {
			Screen screen = new RealmsMainScreen(new TitleScreen());
			client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_REALMS));
			return;
		} catch (RealmsServiceException var10) {
			Screen screenx = new TitleScreen();
			client.setScreen(new DisconnectedScreen(screenx, ERROR_TITLE, ERROR_REALM_CONNECT, TO_TITLE));
			return;
		}

		RealmsServer realmsServer = (RealmsServer)realmsServerList.servers.stream().filter(server -> server.id == l).findFirst().orElse(null);
		if (realmsServer == null) {
			Screen screen = new RealmsMainScreen(new TitleScreen());
			client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_REALM_PERMISSION, TO_REALMS));
		} else {
			TitleScreen titleScreen = new TitleScreen();
			RealmsGetServerDetailsTask realmsGetServerDetailsTask = new RealmsGetServerDetailsTask(
				new RealmsMainScreen(titleScreen), titleScreen, realmsServer, new ReentrantLock()
			);
			client.setScreen(new RealmsLongRunningMcoTaskScreen(titleScreen, realmsGetServerDetailsTask));
		}
	}
}
