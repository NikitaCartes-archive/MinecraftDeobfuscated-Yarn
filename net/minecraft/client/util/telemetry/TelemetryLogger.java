/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.telemetry.SentTelemetryEvent;

@Environment(value=EnvType.CLIENT)
public interface TelemetryLogger {
    public void log(SentTelemetryEvent var1);
}

