/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class SwitchSlotTask
extends LongRunningTask {
    private final long worldId;
    private final int slot;
    private final Runnable callback;

    public SwitchSlotTask(long worldId, int slot, Runnable callback) {
        this.worldId = worldId;
        this.slot = slot;
        this.callback = callback;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        String string = I18n.translate("mco.minigame.world.slot.screen.title", new Object[0]);
        this.setTitle(string);
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!realmsClient.switchSlot(this.worldId, this.slot)) continue;
                this.callback.run();
                break;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                SwitchSlotTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't switch world!");
                this.method_27453(exception.toString());
            }
        }
    }
}

