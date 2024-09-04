package net.minecraft.item.equipment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public record EquipmentModel(Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>> layers) {
	private static final Codec<List<EquipmentModel.Layer>> LAYER_LIST_CODEC = Codecs.nonEmptyList(EquipmentModel.Layer.CODEC.listOf());
	public static final Codec<EquipmentModel> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.nonEmptyMap(Codec.unboundedMap(EquipmentModel.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentModel::layers)
				)
				.apply(instance, EquipmentModel::new)
	);

	public static EquipmentModel.Builder builder() {
		return new EquipmentModel.Builder();
	}

	public List<EquipmentModel.Layer> getLayers(EquipmentModel.LayerType layerType) {
		return (List<EquipmentModel.Layer>)this.layers.getOrDefault(layerType, List.of());
	}

	public static class Builder {
		private final Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>> layers = new EnumMap(EquipmentModel.LayerType.class);

		Builder() {
		}

		public EquipmentModel.Builder addHumanoidLayers(Identifier textureId) {
			return this.addHumanoidLayers(textureId, false);
		}

		public EquipmentModel.Builder addHumanoidLayers(Identifier textureId, boolean dyeable) {
			this.addLayers(EquipmentModel.LayerType.HUMANOID_LEGGINGS, EquipmentModel.Layer.createDyeableLeather(textureId, dyeable));
			this.addMainHumanoidLayer(textureId, dyeable);
			return this;
		}

		public EquipmentModel.Builder addMainHumanoidLayer(Identifier textureId, boolean dyeable) {
			return this.addLayers(EquipmentModel.LayerType.HUMANOID, EquipmentModel.Layer.createDyeableLeather(textureId, dyeable));
		}

		public EquipmentModel.Builder addLayers(EquipmentModel.LayerType layerType, EquipmentModel.Layer... layers) {
			Collections.addAll((Collection)this.layers.computeIfAbsent(layerType, layerTypex -> new ArrayList()), layers);
			return this;
		}

		public EquipmentModel build() {
			return new EquipmentModel(
				(Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>>)this.layers
					.entrySet()
					.stream()
					.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> List.copyOf((Collection)entry.getValue())))
			);
		}
	}

	public static record Dyeable(Optional<Integer> colorWhenUndyed) {
		public static final Codec<EquipmentModel.Dyeable> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codecs.RGB.optionalFieldOf("color_when_undyed").forGetter(EquipmentModel.Dyeable::colorWhenUndyed))
					.apply(instance, EquipmentModel.Dyeable::new)
		);
	}

	public static record Layer(Identifier textureId, Optional<EquipmentModel.Dyeable> dyeable, boolean usePlayerTexture) {
		public static final Codec<EquipmentModel.Layer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Identifier.CODEC.fieldOf("texture").forGetter(EquipmentModel.Layer::textureId),
						EquipmentModel.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(EquipmentModel.Layer::dyeable),
						Codec.BOOL.optionalFieldOf("use_player_texture", Boolean.valueOf(false)).forGetter(EquipmentModel.Layer::usePlayerTexture)
					)
					.apply(instance, EquipmentModel.Layer::new)
		);

		public Layer(Identifier textureId) {
			this(textureId, Optional.empty(), false);
		}

		public static EquipmentModel.Layer createDyeableLeather(Identifier textureId, boolean dyeable) {
			return new EquipmentModel.Layer(textureId, dyeable ? Optional.of(new EquipmentModel.Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
		}

		public static EquipmentModel.Layer createDyeable(Identifier textureId, boolean dyeable) {
			return new EquipmentModel.Layer(textureId, dyeable ? Optional.of(new EquipmentModel.Dyeable(Optional.empty())) : Optional.empty(), false);
		}

		public Identifier getFullTextureId(EquipmentModel.LayerType layerType) {
			return this.textureId.withPath((UnaryOperator<String>)(textureName -> "textures/entity/equipment/" + layerType.asString() + "/" + textureName + ".png"));
		}
	}

	public static enum LayerType implements StringIdentifiable {
		HUMANOID("humanoid"),
		HUMANOID_LEGGINGS("humanoid_leggings"),
		WINGS("wings"),
		WOLF_BODY("wolf_body"),
		HORSE_BODY("horse_body"),
		LLAMA_BODY("llama_body");

		public static final Codec<EquipmentModel.LayerType> CODEC = StringIdentifiable.createCodec(EquipmentModel.LayerType::values);
		private final String name;

		private LayerType(final String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
