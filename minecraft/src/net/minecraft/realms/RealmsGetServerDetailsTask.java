package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsGetServerDetailsTask extends LongRunningTask {
	private final RealmsServer server;
	private final Screen lastScreen;
	private final RealmsMainScreen mainScreen;
	private final ReentrantLock connectLock;

	public RealmsGetServerDetailsTask(RealmsMainScreen mainScreen, Screen lastScreen, RealmsServer server, ReentrantLock connectLock) {
		this.lastScreen = lastScreen;
		this.mainScreen = mainScreen;
		this.server = server;
		this.connectLock = connectLock;
	}

	public void run() {
		this.setTitle(I18n.translate("mco.connect.connecting"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		boolean bl = false;
		boolean bl2 = false;
		int i = 5;
		com.mojang.realmsclient.dto.RealmsServerAddress realmsServerAddress = null;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int j = 0; j < 40 && !this.aborted(); j++) {
			try {
				realmsServerAddress = realmsClient.join(this.server.id);
				bl = true;
			} catch (RetryCallException var11) {
				i = var11.delaySeconds;
			} catch (RealmsServiceException var12) {
				if (var12.errorCode == 6002) {
					bl3 = true;
				} else if (var12.errorCode == 6006) {
					bl4 = true;
				} else {
					bl2 = true;
					this.method_27453(var12.toString());
					LOGGER.error("Couldn't connect to world", (Throwable)var12);
				}
				break;
			} catch (Exception var13) {
				bl2 = true;
				LOGGER.error("Couldn't connect to world", (Throwable)var13);
				this.method_27453(var13.getLocalizedMessage());
				break;
			}

			if (bl) {
				break;
			}

			this.sleep(i);
		}

		if (bl3) {
			setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
		} else if (bl4) {
			if (this.server.ownerUUID.equals(MinecraftClient.getInstance().getSession().getUuid())) {
				setScreen(new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME));
			} else {
				setScreen(
					new RealmsGenericErrorScreen(
						new TranslatableText("mco.brokenworld.nonowner.title"), new TranslatableText("mco.brokenworld.nonowner.error"), this.lastScreen
					)
				);
			}
		} else if (!this.aborted() && !bl2) {
			if (bl) {
				com.mojang.realmsclient.dto.RealmsServerAddress realmsServerAddress2 = realmsServerAddress;
				if (realmsServerAddress2.resourcePackUrl != null && realmsServerAddress2.resourcePackHash != null) {
					Text text = new TranslatableText("mco.configure.world.resourcepack.question.line1");
					Text text2 = new TranslatableText("mco.configure.world.resourcepack.question.line2");
					setScreen(
						new RealmsLongConfirmationScreen(
							blx -> {
								try {
									if (blx) {
										Function<Throwable, Void> function = throwable -> {
											MinecraftClient.getInstance().getResourcePackDownloader().clear();
											LOGGER.error(throwable);
											setScreen(new RealmsGenericErrorScreen(new LiteralText("Failed to download resource pack!"), this.lastScreen));
											return null;
										};

										try {
											MinecraftClient.getInstance()
												.getResourcePackDownloader()
												.download(realmsServerAddress2.resourcePackUrl, realmsServerAddress2.resourcePackHash)
												.thenRun(() -> this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, realmsServerAddress2))))
												.exceptionally(function);
										} catch (Exception var8x) {
											function.apply(var8x);
										}
									} else {
										setScreen(this.lastScreen);
									}
								} finally {
									if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
										this.connectLock.unlock();
									}
								}
							},
							RealmsLongConfirmationScreen.Type.Info,
							text,
							text2,
							true
						)
					);
				} else {
					this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, realmsServerAddress2)));
				}
			} else {
				this.error(new TranslatableText("mco.errorMessage.connectionFailure"));
			}
		}
	}

	private void sleep(int sleepTimeSeconds) {
		try {
			Thread.sleep((long)(sleepTimeSeconds * 1000));
		} catch (InterruptedException var3) {
			LOGGER.warn(var3.getLocalizedMessage());
		}
	}
}
