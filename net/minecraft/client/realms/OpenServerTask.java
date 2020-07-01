/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class OpenServerTask
extends LongRunningTask {
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final RealmsMainScreen mainScreen;

    public OpenServerTask(RealmsServer realmsServer, Screen returnScreen, RealmsMainScreen mainScreen, boolean join) {
        this.serverData = realmsServer;
        this.returnScreen = returnScreen;
        this.join = join;
        this.mainScreen = mainScreen;
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.configure.world.opening", new Object[0]));
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.open(this.serverData.id);
                if (!bl) continue;
                if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                    ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                }
                this.serverData.state = RealmsServer.State.OPEN;
                if (this.join) {
                    this.mainScreen.play(this.serverData, this.returnScreen);
                    break;
                }
                OpenServerTask.setScreen(this.returnScreen);
                break;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", (Throwable)exception);
                this.error("Failed to open the server");
            }
        }
    }
}

