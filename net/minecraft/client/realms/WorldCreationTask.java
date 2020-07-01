/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class WorldCreationTask
extends LongRunningTask {
    private final String name;
    private final String motd;
    private final long worldId;
    private final Screen lastScreen;

    public WorldCreationTask(long worldId, String name, String motd, Screen lastScreen) {
        this.worldId = worldId;
        this.name = name;
        this.motd = motd;
        this.lastScreen = lastScreen;
    }

    @Override
    public void run() {
        String string = I18n.translate("mco.create.world.wait", new Object[0]);
        this.setTitle(string);
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            realmsClient.initializeWorld(this.worldId, this.name, this.motd);
            WorldCreationTask.setScreen(this.lastScreen);
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't create world");
            this.error(realmsServiceException.toString());
        } catch (Exception exception) {
            LOGGER.error("Could not create world");
            this.error(exception.getLocalizedMessage());
        }
    }
}

