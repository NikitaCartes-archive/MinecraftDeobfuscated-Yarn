/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public class RealmsServerAddress {
    private final String host;
    private final int port;

    protected RealmsServerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static RealmsServerAddress parseString(String string) {
        ServerAddress serverAddress = ServerAddress.parse(string);
        return new RealmsServerAddress(serverAddress.getAddress(), serverAddress.getPort());
    }
}

