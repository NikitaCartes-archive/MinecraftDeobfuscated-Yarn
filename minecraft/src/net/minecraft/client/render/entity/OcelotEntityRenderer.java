package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OcelotEntityRenderer extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cat/ocelot.png");

	public OcelotEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new OcelotEntityModel<>(arg.method_32167(EntityModelLayers.OCELOT)), 0.4F);
	}

	public Identifier getTexture(OcelotEntity ocelotEntity) {
		return TEXTURE;
	}
}
