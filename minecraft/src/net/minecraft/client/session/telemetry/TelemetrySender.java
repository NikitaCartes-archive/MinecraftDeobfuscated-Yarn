package net.minecraft.client.session.telemetry;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface TelemetrySender {
	TelemetrySender NOOP = (eventType, propertyAdder) -> {
	};

	default TelemetrySender decorate(Consumer<PropertyMap.Builder> decorationAdder) {
		return (eventType, propertyAdder) -> this.send(eventType, builder -> {
				propertyAdder.accept(builder);
				decorationAdder.accept(builder);
			});
	}

	void send(TelemetryEventType eventType, Consumer<PropertyMap.Builder> propertyAdder);
}
