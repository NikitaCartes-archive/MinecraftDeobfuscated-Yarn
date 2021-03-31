/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

public enum NetworkSide {
    SERVERBOUND,
    CLIENTBOUND;


    public NetworkSide getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }
}

