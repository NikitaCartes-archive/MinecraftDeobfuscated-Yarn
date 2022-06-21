/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CloseServerTask
extends LongRunningTask {
    private static final Logger field_36354 = LogUtils.getLogger();
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen) {
        this.serverData = realmsServer;
        this.configureScreen = configureWorldScreen;
    }

    @Override
    public void run() {
        this.setTitle(Text.translatable("mco.configure.world.closing"));
        RealmsClient realmsClient = RealmsClient.method_44616();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.close(this.serverData.id);
                if (!bl) continue;
                this.configureScreen.stateChanged();
                this.serverData.state = RealmsServer.State.CLOSED;
                CloseServerTask.setScreen(this.configureScreen);
                break;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                CloseServerTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                field_36354.error("Failed to close server", exception);
                this.error("Failed to close the server");
            }
        }
    }
}

