package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsGetServerDetailsTask extends LongRunningTask {
	private static final Logger field_36356 = LogUtils.getLogger();
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
			realmsServerAddress = this.join();
		} catch (CancellationException var4) {
			field_36356.info("User aborted connecting to realms");
			return;
		} catch (RealmsServiceException var5) {
			switch (var5.getErrorCode(-1)) {
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
					field_36356.error("Couldn't connect to world", (Throwable)var5);
					return;
			}
		} catch (TimeoutException var6) {
			this.error(new TranslatableText("mco.errorMessage.connectionFailure"));
			return;
		} catch (Exception var7) {
			field_36356.error("Couldn't connect to world", (Throwable)var7);
			this.error(var7.getLocalizedMessage());
			return;
		}

		boolean bl2 = realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null;
		Screen screen = (Screen)(bl2
			? this.createResourcePackConfirmationScreen(realmsServerAddress, this::createConnectingScreen)
			: this.createConnectingScreen(realmsServerAddress));
		setScreen(screen);
	}

	private RealmsServerAddress join() throws RealmsServiceException, TimeoutException, CancellationException {
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

	public RealmsLongRunningMcoTaskScreen createConnectingScreen(RealmsServerAddress address) {
		return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, this.server, address));
	}

	private RealmsLongConfirmationScreen createResourcePackConfirmationScreen(
		RealmsServerAddress address, Function<RealmsServerAddress, Screen> connectingScreenCreator
	) {
		BooleanConsumer booleanConsumer = confirmed -> {
			try {
				if (confirmed) {
					this.downloadResourcePack(address).thenRun(() -> setScreen((Screen)connectingScreenCreator.apply(address))).exceptionally(throwable -> {
						MinecraftClient.getInstance().getResourcePackProvider().clear();
						field_36356.error("Failed to download resource pack from {}", address, throwable);
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
			RealmsLongConfirmationScreen.Type.INFO,
			new TranslatableText("mco.configure.world.resourcepack.question.line1"),
			new TranslatableText("mco.configure.world.resourcepack.question.line2"),
			true
		);
	}

	private CompletableFuture<?> downloadResourcePack(RealmsServerAddress address) {
		try {
			return MinecraftClient.getInstance().getResourcePackProvider().download(address.resourcePackUrl, address.resourcePackHash, false);
		} catch (Exception var4) {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			completableFuture.completeExceptionally(var4);
			return completableFuture;
		}
	}
}
