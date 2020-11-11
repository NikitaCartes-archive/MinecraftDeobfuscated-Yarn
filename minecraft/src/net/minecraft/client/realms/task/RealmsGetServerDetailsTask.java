package net.minecraft.client.realms.task;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsBrokenWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsTermsScreen;
import net.minecraft.text.LiteralText;
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
		this.setTitle(new TranslatableText("mco.connect.connecting"));

		RealmsServerAddress realmsServerAddress;
		try {
			realmsServerAddress = this.method_32516();
		} catch (CancellationException var4) {
			LOGGER.info("User aborted connecting to realms");
			return;
		} catch (RealmsServiceException var5) {
			switch (var5.errorCode) {
				case 6002:
					setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
					return;
				case 6006:
					boolean bl = this.server.ownerUUID.equals(MinecraftClient.getInstance().getSession().getUuid());
					setScreen(
						(Screen)(bl
							? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME)
							: new RealmsGenericErrorScreen(
								new TranslatableText("mco.brokenworld.nonowner.title"), new TranslatableText("mco.brokenworld.nonowner.error"), this.lastScreen
							))
					);
					return;
				default:
					this.error(var5.toString());
					LOGGER.error("Couldn't connect to world", (Throwable)var5);
					return;
			}
		} catch (TimeoutException var6) {
			this.error(new TranslatableText("mco.errorMessage.connectionFailure"));
			return;
		} catch (Exception var7) {
			LOGGER.error("Couldn't connect to world", (Throwable)var7);
			this.error(var7.getLocalizedMessage());
			return;
		}

		boolean bl2 = realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null;
		Screen screen = (Screen)(bl2 ? this.method_32512(realmsServerAddress, this::method_32511) : this.method_32511(realmsServerAddress));
		setScreen(screen);
	}

	private RealmsServerAddress method_32516() throws RealmsServiceException, TimeoutException, CancellationException {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		for (int i = 0; i < 40; i++) {
			if (this.aborted()) {
				throw new CancellationException();
			}

			try {
				return realmsClient.join(this.server.id);
			} catch (RetryCallException var4) {
				pause((long)var4.delaySeconds);
			}
		}

		throw new TimeoutException();
	}

	public RealmsLongRunningMcoTaskScreen method_32511(RealmsServerAddress realmsServerAddress) {
		return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, this.server, realmsServerAddress));
	}

	private RealmsLongConfirmationScreen method_32512(RealmsServerAddress realmsServerAddress, Function<RealmsServerAddress, Screen> function) {
		BooleanConsumer booleanConsumer = bl -> {
			try {
				if (bl) {
					this.method_32515(realmsServerAddress).thenRun(() -> setScreen((Screen)function.apply(realmsServerAddress))).exceptionally(throwable -> {
						MinecraftClient.getInstance().getResourcePackDownloader().clear();
						LOGGER.error(throwable);
						setScreen(new RealmsGenericErrorScreen(new LiteralText("Failed to download resource pack!"), this.lastScreen));
						return null;
					});
					return;
				}

				setScreen(this.lastScreen);
			} finally {
				if (this.connectLock.isHeldByCurrentThread()) {
					this.connectLock.unlock();
				}
			}
		};
		return new RealmsLongConfirmationScreen(
			booleanConsumer,
			RealmsLongConfirmationScreen.Type.Info,
			new TranslatableText("mco.configure.world.resourcepack.question.line1"),
			new TranslatableText("mco.configure.world.resourcepack.question.line2"),
			true
		);
	}

	private CompletableFuture<?> method_32515(RealmsServerAddress realmsServerAddress) {
		try {
			return MinecraftClient.getInstance().getResourcePackDownloader().download(realmsServerAddress.resourcePackUrl, realmsServerAddress.resourcePackHash);
		} catch (Exception var4) {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			completableFuture.completeExceptionally(var4);
			return completableFuture;
		}
	}
}
