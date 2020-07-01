/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;

@Environment(value=EnvType.CLIENT)
public class RealmsServerPing
extends ValueObject {
    public volatile String nrOfPlayers = "0";
    public volatile String playerList = "";
}

