package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BlazeEntityRenderer extends MobEntityRenderer<BlazeEntity, BlazeEntityModel<BlazeEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/blaze.png");

	public BlazeEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new BlazeEntityModel<>(arg.method_32167(EntityModelLayers.BLAZE)), 0.5F);
	}

	protected int getBlockLight(BlazeEntity blazeEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(BlazeEntity blazeEntity) {
		return TEXTURE;
	}
}
