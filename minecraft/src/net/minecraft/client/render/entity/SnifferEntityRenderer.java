package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnifferEntityModel;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnifferEntityRenderer extends MobEntityRenderer<SnifferEntity, SnifferEntityModel<SnifferEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sniffer/sniffer.png");

	public SnifferEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SnifferEntityModel<>(context.getPart(EntityModelLayers.SNIFFER)), 1.1F);
	}

	public Identifier getTexture(SnifferEntity snifferEntity) {
		return TEXTURE;
	}
}
