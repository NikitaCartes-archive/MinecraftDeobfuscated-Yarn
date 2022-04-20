/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URL;
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
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsConnectTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsGetServerDetailsTask
extends LongRunningTask {
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

    @Override
    public void run() {
        RealmsServerAddress realmsServerAddress;
        this.setTitle(Text.translatable("mco.connect.connecting"));
        try {
            realmsServerAddress = this.join();
        } catch (CancellationException cancellationException) {
            field_36356.info("User aborted connecting to realms");
            return;
        } catch (RealmsServiceException realmsServiceException) {
            switch (realmsServiceException.getErrorCode(-1)) {
                case 6002: {
                    RealmsGetServerDetailsTask.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
                    return;
                }
                case 6006: {
                    boolean bl = this.server.ownerUUID.equals(MinecraftClient.getInstance().getSession().getUuid());
                    RealmsGetServerDetailsTask.setScreen(bl ? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME) : new RealmsGenericErrorScreen(Text.translatable("mco.brokenworld.nonowner.title"), Text.translatable("mco.brokenworld.nonowner.error"), this.lastScreen));
                    return;
                }
            }
            this.error(realmsServiceException.toString());
            field_36356.error("Couldn't connect to world", realmsServiceException);
            return;
        } catch (TimeoutException timeoutException) {
            this.error(Text.translatable("mco.errorMessage.connectionFailure"));
            return;
        } catch (Exception exception) {
            field_36356.error("Couldn't connect to world", exception);
            this.error(exception.getLocalizedMessage());
            return;
        }
        boolean bl2 = realmsServerAddress.resourcePackUrl != null && realmsServerAddress.resourcePackHash != null;
        RealmsLongRunningMcoTaskScreen screen = bl2 ? this.createResourcePackConfirmationScreen(realmsServerAddress, this::createConnectingScreen) : this.createConnectingScreen(realmsServerAddress);
        RealmsGetServerDetailsTask.setScreen(screen);
    }

    private RealmsServerAddress join() throws RealmsServiceException, TimeoutException, CancellationException {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        for (int i = 0; i < 40; ++i) {
            if (this.aborted()) {
                throw new CancellationException();
            }
            try {
                return realmsClient.join(this.server.id);
            } catch (RetryCallException retryCallException) {
                RealmsGetServerDetailsTask.pause(retryCallException.delaySeconds);
                continue;
            }
        }
        throw new TimeoutException();
    }

    public RealmsLongRunningMcoTaskScreen createConnectingScreen(RealmsServerAddress address) {
        return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, this.server, address));
    }

    private RealmsLongConfirmationScreen createResourcePackConfirmationScreen(RealmsServerAddress address, Function<RealmsServerAddress, Screen> connectingScreenCreator) {
        BooleanConsumer booleanConsumer = confirmed -> {
            try {
                if (!confirmed) {
                    RealmsGetServerDetailsTask.setScreen(this.lastScreen);
                    return;
                }
                ((CompletableFuture)this.downloadResourcePack(address).thenRun(() -> RealmsGetServerDetailsTask.setScreen((Screen)connectingScreenCreator.apply(address)))).exceptionally(throwable -> {
                    MinecraftClient.getInstance().getResourcePackProvider().clear();
                    field_36356.error("Failed to download resource pack from {}", (Object)address, throwable);
                    RealmsGetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(Text.literal("Failed to download resource pack!"), this.lastScreen));
                    return null;
                });
            } finally {
                if (this.connectLock.isHeldByCurrentThread()) {
                    this.connectLock.unlock();
                }
            }
        };
        return new RealmsLongConfirmationScreen(booleanConsumer, RealmsLongConfirmationScreen.Type.INFO, Text.translatable("mco.configure.world.resourcepack.question.line1"), Text.translatable("mco.configure.world.resourcepack.question.line2"), true);
    }

    private CompletableFuture<?> downloadResourcePack(RealmsServerAddress address) {
        try {
            return MinecraftClient.getInstance().getResourcePackProvider().download(new URL(address.resourcePackUrl), address.resourcePackHash, false);
        } catch (Exception exception) {
            CompletableFuture completableFuture = new CompletableFuture();
            completableFuture.completeExceptionally(exception);
            return completableFuture;
        }
    }
}

