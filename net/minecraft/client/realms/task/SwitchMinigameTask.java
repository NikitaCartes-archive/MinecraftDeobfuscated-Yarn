/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SwitchMinigameTask
extends LongRunningTask {
    private static final Logger field_36360 = LogUtils.getLogger();
    private final long worldId;
    private final WorldTemplate worldTemplate;
    private final RealmsConfigureWorldScreen lastScreen;

    public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen) {
        this.worldId = worldId;
        this.worldTemplate = worldTemplate;
        this.lastScreen = lastScreen;
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        this.setTitle(new TranslatableText("mco.minigame.world.starting.screen.title"));
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!realmsClient.putIntoMinigameMode(this.worldId, this.worldTemplate.id).booleanValue()) continue;
                SwitchMinigameTask.setScreen(this.lastScreen);
                break;
            } catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                SwitchMinigameTask.pause(retryCallException.delaySeconds);
                continue;
            } catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                field_36360.error("Couldn't start mini game!");
                this.error(exception.toString());
            }
        }
    }
}

