/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.telemetry.PropertyMap;
import net.minecraft.client.util.telemetry.TelemetryEventType;

@Environment(value=EnvType.CLIENT)
public record SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
    public static final Codec<SentTelemetryEvent> CODEC = TelemetryEventType.CODEC.dispatchStable(SentTelemetryEvent::type, TelemetryEventType::getCodec);

    public SentTelemetryEvent {
        propertyMap.keySet().forEach(property -> {
            if (!telemetryEventType.hasProperty(property)) {
                throw new IllegalArgumentException("Property '" + property.id() + "' not expected for event: '" + telemetryEventType.getId() + "'");
            }
        });
    }

    public TelemetryEvent createEvent(TelemetrySession session) {
        return this.type.createEvent(session, this.properties);
    }
}

