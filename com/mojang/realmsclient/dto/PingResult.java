/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.dto;

import com.mojang.realmsclient.dto.RegionPingResult;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(value=EnvType.CLIENT)
public class PingResult
extends class_4352 {
    public List<RegionPingResult> pingResults = new ArrayList<RegionPingResult>();
    public List<Long> worldIds = new ArrayList<Long>();
}

