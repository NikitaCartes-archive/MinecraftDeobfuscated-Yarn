/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class ResettingWorldTask
extends LongRunningTask {
    private static final Logger field_36358 = LogUtils.getLogger();
    private final long serverId;
    private final Text title;
    private final Runnable callback;

    public ResettingWorldTask(long serverId, Text title, Runnable callback) {
        this.serverId = serverId;
        this.title = title;
        this.callback = callback;
    }

    protected abstract void resetWorld(RealmsClient var1, long var2) throws RealmsServiceException;

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.method_44616();
        this.setTitle(this.title);
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                this.resetWorld(realmsClient, this.serverId);
                if (this.aborted()) {
                    return;
                }
                this.callback.run();
                return;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                ResettingWorldTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                field_36358.error("Couldn't reset world");
                this.error(exception.toString());
                return;
            }
        }
    }
}

