/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RestoreTask
extends LongRunningTask {
    private final Backup backup;
    private final long worldId;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup backup, long worldId, RealmsConfigureWorldScreen lastScreen) {
        this.backup = backup;
        this.worldId = worldId;
        this.lastScreen = lastScreen;
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableText("mco.backup.restoring"));
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                realmsClient.restoreWorld(this.worldId, this.backup.backupId);
                RestoreTask.pause(1L);
                if (this.aborted()) {
                    return;
                }
                RestoreTask.setScreen(this.lastScreen.getNewScreen());
                return;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                RestoreTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (RealmsServiceException realmsServiceException) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)realmsServiceException);
                RestoreTask.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)this.lastScreen));
                return;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                return;
            }
        }
    }
}

