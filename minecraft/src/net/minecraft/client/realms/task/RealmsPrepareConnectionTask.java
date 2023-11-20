package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
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
import net.minecraft.client.realms.gui.screen.RealmsLongRunningTickableTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsTermsScreen;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPrepareConnectionTask extends LongRunningTask {
	private static final Text APPLYING_PACK_TEXT = Text.translatable("multiplayer.applyingPack");
	private static final UUID REALMS_PACK_ID = UUID.fromString("08c3b151-90fb-4c09-b6cf-0548364671bb");
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE = Text.translatable("mco.connect.connecting");
	private final RealmsServer server;
	private final Screen lastScreen;

	public RealmsPrepareConnectionTask(Screen lastScreen, RealmsServer server) {
		this.lastScreen = lastScreen;
		this.server = server;
	}

	public void run() {
		RealmsServerAddress realmsServerAddress;
		try {
			realmsServerAddress = this.join();
		} catch (CancellationException var4) {
			LOGGER.info("User aborted connecting to realms");
			return;
		} catch (RealmsServiceException var5) {
			switch (var5.error.getErrorCode()) {
				case 6002:
					setScreen(new RealmsTermsScreen(this.lastScreen, this.server));
					return;
				case 6006:
					boolean bl = MinecraftClient.getInstance().uuidEquals(this.server.ownerUUID);
					setScreen(
						(Screen)(bl
							? new RealmsBrokenWorldScreen(this.lastScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME)
							: new RealmsGenericErrorScreen(Text.translatable("mco.brokenworld.nonowner.title"), Text.translatable("mco.brokenworld.nonowner.error"), this.lastScreen))
					);
					return;
				default:
					this.error(var5);
					LOGGER.error("Couldn't connect to world", (Throwable)var5);
					return;
			}
		} catch (TimeoutException var6) {
			this.error(Text.translatable("mco.errorMessage.connectionFailure"));
			return;
		} catch (Exception var7) {
			LOGGER.error("Couldn't connect to world", (Throwable)var7);
			this.error(var7);
			return;
		}

		boolean bl2 = realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null;
		Screen screen = (Screen)(bl2
			? this.createResourcePackConfirmationScreen(realmsServerAddress, this::createConnectingScreen)
			: this.createConnectingScreen(realmsServerAddress));
		setScreen(screen);
	}

	@Override
	public Text getTitle() {
		return TITLE;
	}

	private RealmsServerAddress join() throws RealmsServiceException, TimeoutException, CancellationException {
		RealmsClient realmsClient = RealmsClient.create();

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
		return new RealmsLongRunningTickableTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, this.server, address));
	}

	private RealmsLongConfirmationScreen createResourcePackConfirmationScreen(
		RealmsServerAddress address, Function<RealmsServerAddress, Screen> connectingScreenCreator
	) {
		BooleanConsumer booleanConsumer = confirmed -> {
			if (!confirmed) {
				setScreen(this.lastScreen);
			} else {
				setScreen(new MessageScreen(APPLYING_PACK_TEXT));
				this.downloadResourcePack(address).thenRun(() -> setScreen((Screen)connectingScreenCreator.apply(address))).exceptionally(throwable -> {
					MinecraftClient.getInstance().getServerResourcePackProvider().clear();
					LOGGER.error("Failed to download resource pack from {}", address, throwable);
					setScreen(new RealmsGenericErrorScreen(Text.translatable("mco.download.resourcePack.fail"), this.lastScreen));
					return null;
				});
			}
		};
		return new RealmsLongConfirmationScreen(
			booleanConsumer,
			RealmsLongConfirmationScreen.Type.INFO,
			Text.translatable("mco.configure.world.resourcepack.question.line1"),
			Text.translatable("mco.configure.world.resourcepack.question.line2"),
			true
		);
	}

	private CompletableFuture<?> downloadResourcePack(RealmsServerAddress address) {
		try {
			ServerResourcePackLoader serverResourcePackLoader = MinecraftClient.getInstance().getServerResourcePackProvider();
			CompletableFuture<Void> completableFuture = serverResourcePackLoader.getPackLoadFuture(REALMS_PACK_ID);
			serverResourcePackLoader.acceptAll();
			serverResourcePackLoader.addResourcePack(REALMS_PACK_ID, new URL(address.resourcePackUrl), address.resourcePackHash);
			return completableFuture;
		} catch (Exception var4) {
			CompletableFuture<Void> completableFuturex = new CompletableFuture();
			completableFuturex.completeExceptionally(var4);
			return completableFuturex;
		}
	}
}
