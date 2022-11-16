/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.telemetry.TelemetrySender;

@Environment(value=EnvType.CLIENT)
public interface TelemetryEvent {
    public void send(TelemetrySender var1);
}

