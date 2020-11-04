package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new VexEntityModel(arg.method_32167(EntityModelLayers.VEX)), 0.3F);
	}

	protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
	}

	protected void scale(VexEntity vexEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(0.4F, 0.4F, 0.4F);
	}
}
