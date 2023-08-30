package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
	public static final Codec<SentTelemetryEvent> CODEC = TelemetryEventType.CODEC.dispatchStable(SentTelemetryEvent::type, TelemetryEventType::getCodec);

	public SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
		properties.keySet().forEach(property -> {
			if (!telemetryEventType.hasProperty(property)) {
				throw new IllegalArgumentException("Property '" + property.id() + "' not expected for event: '" + telemetryEventType.getId() + "'");
			}
		});
		this.type = type;
		this.properties = properties;
	}

	public TelemetryEvent createEvent(TelemetrySession session) {
		return this.type.createEvent(session, this.properties);
	}
}
