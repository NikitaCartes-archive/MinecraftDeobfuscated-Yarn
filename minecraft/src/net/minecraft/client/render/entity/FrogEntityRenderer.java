package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class FrogEntityRenderer extends MobEntityRenderer<FrogEntity, FrogEntityModel<FrogEntity>> {
	private static final Map<FrogEntity.Variant, Identifier> TEXTURES = Util.make(Maps.<FrogEntity.Variant, Identifier>newHashMap(), hashMap -> {
		for (FrogEntity.Variant variant : FrogEntity.Variant.values()) {
			hashMap.put(variant, new Identifier(String.format("textures/entity/frog/%s_frog.png", variant.getName())));
		}
	});

	public FrogEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new FrogEntityModel<>(context.getPart(EntityModelLayers.FROG)), 0.3F);
	}

	public Identifier getTexture(FrogEntity frogEntity) {
		return (Identifier)TEXTURES.get(frogEntity.getVariant());
	}
}
