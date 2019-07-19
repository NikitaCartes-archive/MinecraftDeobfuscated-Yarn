package com.mojang.realmsclient.util;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsResourcePackScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTasks {
	private static final Logger LOGGER = LogManager.getLogger();

	private static void pause(int seconds) {
		try {
			Thread.sleep((long)(seconds * 1000));
		} catch (InterruptedException var2) {
			LOGGER.error("", (Throwable)var2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CloseServerTask extends LongRunningTask {
		private final RealmsServer serverData;
		private final RealmsConfigureWorldScreen configureScreen;

		public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen) {
			this.serverData = realmsServer;
			this.configureScreen = configureWorldScreen;
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			for (int i = 0; i < 25; i++) {
				if (this.aborted()) {
					return;
				}

				try {
					boolean bl = realmsClient.close(this.serverData.id);
					if (bl) {
						this.configureScreen.stateChanged();
						this.serverData.state = RealmsServer.State.CLOSED;
						Realms.setScreen(this.configureScreen);
						break;
					}
				} catch (RetryCallException var4) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var4.delaySeconds);
				} catch (Exception var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Failed to close server", (Throwable)var5);
					this.error("Failed to close the server");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DownloadTask extends LongRunningTask {
		private final long worldId;
		private final int slot;
		private final RealmsScreen lastScreen;
		private final String downloadName;

		public DownloadTask(long worldId, int slot, String downloadName, RealmsScreen lastScreen) {
			this.worldId = worldId;
			this.slot = slot;
			this.lastScreen = lastScreen;
			this.downloadName = downloadName;
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.download.preparing"));
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			int i = 0;

			while (i < 25) {
				try {
					if (this.aborted()) {
						return;
					}

					WorldDownload worldDownload = realmsClient.download(this.worldId, this.slot);
					RealmsTasks.pause(1);
					if (this.aborted()) {
						return;
					}

					Realms.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worldDownload, this.downloadName));
					return;
				} catch (RetryCallException var4) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var4.delaySeconds);
					i++;
				} catch (RealmsServiceException var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't download world data");
					Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
					return;
				} catch (Exception var6) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't download world data", (Throwable)var6);
					this.error(var6.getLocalizedMessage());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class OpenServerTask extends LongRunningTask {
		private final RealmsServer serverData;
		private final RealmsScreen returnScreen;
		private final boolean join;
		private final RealmsScreen mainScreen;

		public OpenServerTask(RealmsServer realmsServer, RealmsScreen returnScreen, RealmsScreen mainScreen, boolean join) {
			this.serverData = realmsServer;
			this.returnScreen = returnScreen;
			this.join = join;
			this.mainScreen = mainScreen;
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			for (int i = 0; i < 25; i++) {
				if (this.aborted()) {
					return;
				}

				try {
					boolean bl = realmsClient.open(this.serverData.id);
					if (bl) {
						if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
							((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
						}

						this.serverData.state = RealmsServer.State.OPEN;
						if (this.join) {
							((RealmsMainScreen)this.mainScreen).play(this.serverData, this.returnScreen);
						} else {
							Realms.setScreen(this.returnScreen);
						}
						break;
					}
				} catch (RetryCallException var4) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var4.delaySeconds);
				} catch (Exception var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Failed to open server", (Throwable)var5);
					this.error("Failed to open the server");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsConnectTask extends LongRunningTask {
		private final RealmsConnect realmsConnect;
		private final RealmsServerAddress a;

		public RealmsConnectTask(RealmsScreen lastScreen, RealmsServerAddress address) {
			this.a = address;
			this.realmsConnect = new RealmsConnect(lastScreen);
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
			net.minecraft.realms.RealmsServerAddress realmsServerAddress = net.minecraft.realms.RealmsServerAddress.parseString(this.a.address);
			this.realmsConnect.connect(realmsServerAddress.getHost(), realmsServerAddress.getPort());
		}

		@Override
		public void abortTask() {
			this.realmsConnect.abort();
			Realms.clearResourcePack();
		}

		@Override
		public void tick() {
			this.realmsConnect.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsGetServerDetailsTask extends LongRunningTask {
		private final RealmsServer server;
		private final RealmsScreen lastScreen;
		private final RealmsMainScreen mainScreen;
		private final ReentrantLock connectLock;

		public RealmsGetServerDetailsTask(RealmsMainScreen mainScreen, RealmsScreen lastScreen, RealmsServer server, ReentrantLock connectLock) {
			this.lastScreen = lastScreen;
			this.mainScreen = mainScreen;
			this.server = server;
			this.connectLock = connectLock;
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			boolean bl = false;
			boolean bl2 = false;
			int i = 5;
			RealmsServerAddress realmsServerAddress = null;
			boolean bl3 = false;
			boolean bl4 = false;

			for (int j = 0; j < 40 && !this.aborted(); j++) {
				try {
					realmsServerAddress = realmsClient.join(this.server.id);
					bl = true;
				} catch (RetryCallException var10) {
					i = var10.delaySeconds;
				} catch (RealmsServiceException var11) {
					if (var11.errorCode == 6002) {
						bl3 = true;
					} else if (var11.errorCode == 6006) {
						bl4 = true;
					} else {
						bl2 = true;
						this.error(var11.toString());
						RealmsTasks.LOGGER.error("Couldn't connect to world", (Throwable)var11);
					}
					break;
				} catch (IOException var12) {
					RealmsTasks.LOGGER.error("Couldn't parse response connecting to world", (Throwable)var12);
				} catch (Exception var13) {
					bl2 = true;
					RealmsTasks.LOGGER.error("Couldn't connect to world", (Throwable)var13);
					this.error(var13.getLocalizedMessage());
					break;
				}

				if (bl) {
					break;
				}

				this.sleep(i);
			}

			if (bl3) {
				Realms.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
			} else if (bl4) {
				if (this.server.ownerUUID.equals(Realms.getUUID())) {
					RealmsBrokenWorldScreen realmsBrokenWorldScreen = new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id);
					if (this.server.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
						realmsBrokenWorldScreen.setTitle(RealmsScreen.getLocalizedString("mco.brokenworld.minigame.title"));
					}

					Realms.setScreen(realmsBrokenWorldScreen);
				} else {
					Realms.setScreen(
						new RealmsGenericErrorScreen(
							RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.title"), RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.error"), this.lastScreen
						)
					);
				}
			} else if (!this.aborted() && !bl2) {
				if (bl) {
					if (realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null) {
						String string = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
						String string2 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
						Realms.setScreen(
							new RealmsLongConfirmationScreen(
								new RealmsResourcePackScreen(this.lastScreen, realmsServerAddress, this.connectLock),
								RealmsLongConfirmationScreen.Type.Info,
								string,
								string2,
								true,
								100
							)
						);
					} else {
						RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(
							this.lastScreen, new RealmsTasks.RealmsConnectTask(this.lastScreen, realmsServerAddress)
						);
						realmsLongRunningMcoTaskScreen.start();
						Realms.setScreen(realmsLongRunningMcoTaskScreen);
					}
				} else {
					this.error(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
				}
			}
		}

		private void sleep(int sleepTimeSeconds) {
			try {
				Thread.sleep((long)(sleepTimeSeconds * 1000));
			} catch (InterruptedException var3) {
				RealmsTasks.LOGGER.warn(var3.getLocalizedMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ResettingWorldTask extends LongRunningTask {
		private final String seed;
		private final WorldTemplate worldTemplate;
		private final int levelType;
		private final boolean generateStructures;
		private final long serverId;
		private final RealmsScreen lastScreen;
		private int confirmationId = -1;
		private String title = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");

		public ResettingWorldTask(long serverId, RealmsScreen lastScreen, WorldTemplate worldTemplate) {
			this.seed = null;
			this.worldTemplate = worldTemplate;
			this.levelType = -1;
			this.generateStructures = true;
			this.serverId = serverId;
			this.lastScreen = lastScreen;
		}

		public ResettingWorldTask(long serverId, RealmsScreen lastScreen, String seed, int levelType, boolean generateStructures) {
			this.seed = seed;
			this.worldTemplate = null;
			this.levelType = levelType;
			this.generateStructures = generateStructures;
			this.serverId = serverId;
			this.lastScreen = lastScreen;
		}

		public void setConfirmationId(int confirmationId) {
			this.confirmationId = confirmationId;
		}

		public void setResetTitle(String title) {
			this.title = title;
		}

		public void run() {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			this.setTitle(this.title);
			int i = 0;

			while (i < 25) {
				try {
					if (this.aborted()) {
						return;
					}

					if (this.worldTemplate != null) {
						realmsClient.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
					} else {
						realmsClient.resetWorldWithSeed(this.serverId, this.seed, this.levelType, this.generateStructures);
					}

					if (this.aborted()) {
						return;
					}

					if (this.confirmationId == -1) {
						Realms.setScreen(this.lastScreen);
					} else {
						this.lastScreen.confirmResult(true, this.confirmationId);
					}

					return;
				} catch (RetryCallException var4) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var4.delaySeconds);
					i++;
				} catch (Exception var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't reset world");
					this.error(var5.toString());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class RestoreTask extends LongRunningTask {
		private final Backup backup;
		private final long worldId;
		private final RealmsConfigureWorldScreen lastScreen;

		public RestoreTask(Backup backup, long worldId, RealmsConfigureWorldScreen lastScreen) {
			this.backup = backup;
			this.worldId = worldId;
			this.lastScreen = lastScreen;
		}

		public void run() {
			this.setTitle(RealmsScreen.getLocalizedString("mco.backup.restoring"));
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			int i = 0;

			while (i < 25) {
				try {
					if (this.aborted()) {
						return;
					}

					realmsClient.restoreWorld(this.worldId, this.backup.backupId);
					RealmsTasks.pause(1);
					if (this.aborted()) {
						return;
					}

					Realms.setScreen(this.lastScreen.getNewScreen());
					return;
				} catch (RetryCallException var4) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var4.delaySeconds);
					i++;
				} catch (RealmsServiceException var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't restore backup", (Throwable)var5);
					Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
					return;
				} catch (Exception var6) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't restore backup", (Throwable)var6);
					this.error(var6.getLocalizedMessage());
					return;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SwitchMinigameTask extends LongRunningTask {
		private final long worldId;
		private final WorldTemplate worldTemplate;
		private final RealmsConfigureWorldScreen lastScreen;

		public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen) {
			this.worldId = worldId;
			this.worldTemplate = worldTemplate;
			this.lastScreen = lastScreen;
		}

		public void run() {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			String string = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
			this.setTitle(string);

			for (int i = 0; i < 25; i++) {
				try {
					if (this.aborted()) {
						return;
					}

					if (realmsClient.putIntoMinigameMode(this.worldId, this.worldTemplate.id)) {
						Realms.setScreen(this.lastScreen);
						break;
					}
				} catch (RetryCallException var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var5.delaySeconds);
				} catch (Exception var6) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't start mini game!");
					this.error(var6.toString());
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SwitchSlotTask extends LongRunningTask {
		private final long worldId;
		private final int slot;
		private final RealmsConfirmResultListener listener;
		private final int confirmId;

		public SwitchSlotTask(long worldId, int slot, RealmsConfirmResultListener listener, int confirmId) {
			this.worldId = worldId;
			this.slot = slot;
			this.listener = listener;
			this.confirmId = confirmId;
		}

		public void run() {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();
			String string = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
			this.setTitle(string);

			for (int i = 0; i < 25; i++) {
				try {
					if (this.aborted()) {
						return;
					}

					if (realmsClient.switchSlot(this.worldId, this.slot)) {
						this.listener.confirmResult(true, this.confirmId);
						break;
					}
				} catch (RetryCallException var5) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.pause(var5.delaySeconds);
				} catch (Exception var6) {
					if (this.aborted()) {
						return;
					}

					RealmsTasks.LOGGER.error("Couldn't switch world!");
					this.error(var6.toString());
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TrialCreationTask extends LongRunningTask {
		private final String name;
		private final String motd;
		private final RealmsMainScreen lastScreen;

		public TrialCreationTask(String name, String motd, RealmsMainScreen lastScreen) {
			this.name = name;
			this.motd = motd;
			this.lastScreen = lastScreen;
		}

		public void run() {
			String string = RealmsScreen.getLocalizedString("mco.create.world.wait");
			this.setTitle(string);
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				RealmsServer realmsServer = realmsClient.createTrial(this.name, this.motd);
				if (realmsServer != null) {
					this.lastScreen.setCreatedTrial(true);
					this.lastScreen.closePopup();
					RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
						this.lastScreen,
						realmsServer,
						this.lastScreen.newScreen(),
						RealmsScreen.getLocalizedString("mco.selectServer.create"),
						RealmsScreen.getLocalizedString("mco.create.world.subtitle"),
						10526880,
						RealmsScreen.getLocalizedString("mco.create.world.skip")
					);
					realmsResetWorldScreen.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
					Realms.setScreen(realmsResetWorldScreen);
				} else {
					this.error(RealmsScreen.getLocalizedString("mco.trial.unavailable"));
				}
			} catch (RealmsServiceException var5) {
				RealmsTasks.LOGGER.error("Couldn't create trial");
				this.error(var5.toString());
			} catch (UnsupportedEncodingException var6) {
				RealmsTasks.LOGGER.error("Couldn't create trial");
				this.error(var6.getLocalizedMessage());
			} catch (IOException var7) {
				RealmsTasks.LOGGER.error("Could not parse response creating trial");
				this.error(var7.getLocalizedMessage());
			} catch (Exception var8) {
				RealmsTasks.LOGGER.error("Could not create trial");
				this.error(var8.getLocalizedMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WorldCreationTask extends LongRunningTask {
		private final String name;
		private final String motd;
		private final long worldId;
		private final RealmsScreen lastScreen;

		public WorldCreationTask(long worldId, String name, String motd, RealmsScreen lastScreen) {
			this.worldId = worldId;
			this.name = name;
			this.motd = motd;
			this.lastScreen = lastScreen;
		}

		public void run() {
			String string = RealmsScreen.getLocalizedString("mco.create.world.wait");
			this.setTitle(string);
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				realmsClient.initializeWorld(this.worldId, this.name, this.motd);
				Realms.setScreen(this.lastScreen);
			} catch (RealmsServiceException var4) {
				RealmsTasks.LOGGER.error("Couldn't create world");
				this.error(var4.toString());
			} catch (UnsupportedEncodingException var5) {
				RealmsTasks.LOGGER.error("Couldn't create world");
				this.error(var5.getLocalizedMessage());
			} catch (IOException var6) {
				RealmsTasks.LOGGER.error("Could not parse response creating world");
				this.error(var6.getLocalizedMessage());
			} catch (Exception var7) {
				RealmsTasks.LOGGER.error("Could not create world");
				this.error(var7.getLocalizedMessage());
			}
		}
	}
}
