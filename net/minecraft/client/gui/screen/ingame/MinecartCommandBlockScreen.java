/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public class MinecartCommandBlockScreen
extends AbstractCommandBlockScreen {
    private final CommandBlockExecutor commandExecutor;

    public MinecartCommandBlockScreen(CommandBlockExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    @Override
    int getTrackOutputButtonHeight() {
        return 150;
    }

    @Override
    protected void init() {
        super.init();
        this.trackingOutput = this.getCommandExecutor().isTrackingOutput();
        this.updateTrackedOutput();
        this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
    }

    @Override
    protected void syncSettingsToServer(CommandBlockExecutor commandExecutor) {
        if (commandExecutor instanceof CommandBlockMinecartEntity.CommandExecutor) {
            CommandBlockMinecartEntity.CommandExecutor commandExecutor2 = (CommandBlockMinecartEntity.CommandExecutor)commandExecutor;
            this.minecraft.getNetworkHandler().sendPacket(new UpdateCommandBlockMinecartC2SPacket(commandExecutor2.getMinecart().getEntityId(), this.consoleCommandTextField.getText(), commandExecutor.isTrackingOutput()));
        }
    }
}

