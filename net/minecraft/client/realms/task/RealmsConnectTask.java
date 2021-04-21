/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.RealmsServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectTask
extends LongRunningTask {
    private final RealmsConnection realmsConnect;
    private final RealmsServer server;
    private final net.minecraft.client.realms.dto.RealmsServerAddress address;

    public RealmsConnectTask(Screen lastScreen, RealmsServer server, net.minecraft.client.realms.dto.RealmsServerAddress address) {
        this.server = server;
        this.address = address;
        this.realmsConnect = new RealmsConnection(lastScreen);
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableText("mco.connect.connecting"));
        RealmsServerAddress realmsServerAddress = RealmsServerAddress.parseString(this.address.address);
        this.realmsConnect.connect(this.server, realmsServerAddress.getHost(), realmsServerAddress.getPort());
    }

    @Override
    public void abortTask() {
        this.realmsConnect.abort();
        MinecraftClient.getInstance().getResourcePackProvider().clear();
    }

    @Override
    public void tick() {
        this.realmsConnect.tick();
    }
}

