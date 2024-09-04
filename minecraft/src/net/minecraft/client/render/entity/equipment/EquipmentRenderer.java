package net.minecraft.client.render.entity.equipment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class EquipmentRenderer {
	private static final int field_54178 = 0;
	private final EquipmentModelLoader equipmentModelLoader;
	private final Function<EquipmentRenderer.LayerTextureKey, Identifier> layerTextures;
	private final Function<EquipmentRenderer.TrimSpriteKey, Sprite> trimSprites;

	public EquipmentRenderer(EquipmentModelLoader equipmentModelLoader, SpriteAtlasTexture armorTrimsAtlas) {
		this.equipmentModelLoader = equipmentModelLoader;
		this.layerTextures = Util.memoize((Function<EquipmentRenderer.LayerTextureKey, Identifier>)(key -> key.layer.getFullTextureId(key.layerType)));
		this.trimSprites = Util.memoize((Function<EquipmentRenderer.TrimSpriteKey, Sprite>)(key -> {
			Identifier identifier = key.trim.getTexture(key.layerType, key.equipmentModelId);
			return armorTrimsAtlas.getSprite(identifier);
		}));
	}

	public void render(
		EquipmentModel.LayerType layerType,
		Identifier modelId,
		Model model,
		ItemStack stack,
		Function<Identifier, RenderLayer> renderLayerFunction,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		this.render(layerType, modelId, model, stack, renderLayerFunction, matrices, vertexConsumers, light, null);
	}

	public void render(
		EquipmentModel.LayerType layerType,
		Identifier modelId,
		Model model,
		ItemStack stack,
		Function<Identifier, RenderLayer> renderLayerFunction,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		@Nullable Identifier texture
	) {
		List<EquipmentModel.Layer> list = this.equipmentModelLoader.get(modelId).getLayers(layerType);
		if (!list.isEmpty()) {
			int i = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
			boolean bl = stack.hasGlint();

			for (EquipmentModel.Layer layer : list) {
				int j = getDyeColor(layer, i);
				if (j != 0) {
					Identifier identifier = layer.usePlayerTexture() && texture != null
						? texture
						: (Identifier)this.layerTextures.apply(new EquipmentRenderer.LayerTextureKey(layerType, layer));
					VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, (RenderLayer)renderLayerFunction.apply(identifier), bl);
					model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, j);
					bl = false;
				}
			}

			ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
			if (armorTrim != null) {
				Sprite sprite = (Sprite)this.trimSprites.apply(new EquipmentRenderer.TrimSpriteKey(armorTrim, layerType, modelId));
				VertexConsumer vertexConsumer2 = sprite.getTextureSpecificVertexConsumer(
					vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(armorTrim.pattern().value().decal()))
				);
				model.render(matrices, vertexConsumer2, light, OverlayTexture.DEFAULT_UV);
			}
		}
	}

	private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
		Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
		if (optional.isPresent()) {
			int i = (Integer)((EquipmentModel.Dyeable)optional.get()).colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
			return dyeColor != 0 ? dyeColor : i;
		} else {
			return -1;
		}
	}

	@Environment(EnvType.CLIENT)
	static record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
	}

	@Environment(EnvType.CLIENT)
	static record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, Identifier equipmentModelId) {
	}
}
