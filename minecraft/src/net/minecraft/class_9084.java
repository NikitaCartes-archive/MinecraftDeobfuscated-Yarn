package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_9084 extends MobEntityRenderer<class_9069, class_9082> {
	private static final Identifier field_47887 = new Identifier("textures/entity/armadillo.png");

	public class_9084(EntityRendererFactory.Context context) {
		super(context, new class_9082(context.getPart(EntityModelLayers.ARMADILLO)), 0.4F);
	}

	public Identifier getTexture(class_9069 arg) {
		return field_47887;
	}

	protected void setupTransforms(class_9069 arg, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(arg, matrixStack, f, g, h);
	}
}
