package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AxolotlEntityRenderer extends MobEntityRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {
	private static final Map<AxolotlEntity.Variant, Identifier> TEXTURES = Util.make(Maps.<AxolotlEntity.Variant, Identifier>newHashMap(), hashMap -> {
		for (AxolotlEntity.Variant variant : AxolotlEntity.Variant.VARIANTS) {
			hashMap.put(variant, new Identifier(String.format("textures/entity/axolotl/axolotl_%s.png", variant.getName())));
		}
	});

	public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AxolotlEntityModel<>(context.getPart(EntityModelLayers.AXOLOTL)), 0.5F);
	}

	public Identifier getTexture(AxolotlEntity axolotlEntity) {
		return (Identifier)TEXTURES.get(axolotlEntity.getVariant());
	}
}