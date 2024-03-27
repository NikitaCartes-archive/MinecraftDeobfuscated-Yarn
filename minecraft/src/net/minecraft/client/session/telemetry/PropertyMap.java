package net.minecraft.client.session.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PropertyMap {
	final Map<TelemetryEventProperty<?>, Object> backingMap;

	PropertyMap(Map<TelemetryEventProperty<?>, Object> backingMap) {
		this.backingMap = backingMap;
	}

	public static PropertyMap.Builder builder() {
		return new PropertyMap.Builder();
	}

	public static MapCodec<PropertyMap> createCodec(List<TelemetryEventProperty<?>> properties) {
		return new MapCodec<PropertyMap>() {
			public <T> RecordBuilder<T> encode(PropertyMap propertyMap, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
				RecordBuilder<T> recordBuilder2 = recordBuilder;

				for (TelemetryEventProperty<?> telemetryEventProperty : properties) {
					recordBuilder2 = this.encode(propertyMap, recordBuilder2, telemetryEventProperty);
				}

				return recordBuilder2;
			}

			private <T, V> RecordBuilder<T> encode(PropertyMap map, RecordBuilder<T> builder, TelemetryEventProperty<V> property) {
				V object = map.get(property);
				return object != null ? builder.add(property.id(), object, property.codec()) : builder;
			}

			@Override
			public <T> DataResult<PropertyMap> decode(DynamicOps<T> ops, MapLike<T> map) {
				DataResult<PropertyMap.Builder> dataResult = DataResult.success(new PropertyMap.Builder());

				for (TelemetryEventProperty<?> telemetryEventProperty : properties) {
					dataResult = this.decode(dataResult, ops, map, telemetryEventProperty);
				}

				return dataResult.map(PropertyMap.Builder::build);
			}

			private <T, V> DataResult<PropertyMap.Builder> decode(
				DataResult<PropertyMap.Builder> result, DynamicOps<T> ops, MapLike<T> map, TelemetryEventProperty<V> property
			) {
				T object = map.get(property.id());
				if (object != null) {
					DataResult<V> dataResult = property.codec().parse(ops, object);
					return result.apply2stable((mapBuilder, value) -> mapBuilder.put(property, (V)value), dataResult);
				} else {
					return result;
				}
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return properties.stream().map(TelemetryEventProperty::id).map(ops::createString);
			}
		};
	}

	@Nullable
	public <T> T get(TelemetryEventProperty<T> property) {
		return (T)this.backingMap.get(property);
	}

	public String toString() {
		return this.backingMap.toString();
	}

	public Set<TelemetryEventProperty<?>> keySet() {
		return this.backingMap.keySet();
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final Map<TelemetryEventProperty<?>, Object> backingMap = new Reference2ObjectOpenHashMap<>();

		Builder() {
		}

		public <T> PropertyMap.Builder put(TelemetryEventProperty<T> property, T value) {
			this.backingMap.put(property, value);
			return this;
		}

		public <T> PropertyMap.Builder putIfNonNull(TelemetryEventProperty<T> property, @Nullable T value) {
			if (value != null) {
				this.backingMap.put(property, value);
			}

			return this;
		}

		public PropertyMap.Builder putAll(PropertyMap map) {
			this.backingMap.putAll(map.backingMap);
			return this;
		}

		public PropertyMap build() {
			return new PropertyMap(this.backingMap);
		}
	}
}
