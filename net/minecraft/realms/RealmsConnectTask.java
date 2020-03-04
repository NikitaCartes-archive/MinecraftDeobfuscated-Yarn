/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.mojang.realmsclient.gui.LongRunningTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsServerAddress;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectTask
extends LongRunningTask {
    private final RealmsConnect realmsConnect;
    private final com.mojang.realmsclient.dto.RealmsServerAddress a;

    public RealmsConnectTask(Screen lastScreen, com.mojang.realmsclient.dto.RealmsServerAddress address) {
        this.a = address;
        this.realmsConnect = new RealmsConnect(lastScreen);
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.connect.connecting", new Object[0]));
        RealmsServerAddress realmsServerAddress = RealmsServerAddress.parseString(this.a.address);
        this.realmsConnect.connect(realmsServerAddress.getHost(), realmsServerAddress.getPort());
    }

    @Override
    public void abortTask() {
        this.realmsConnect.abort();
        MinecraftClient.getInstance().getResourcePackDownloader().clear();
    }

    @Override
    public void tick() {
        this.realmsConnect.tick();
    }
}

