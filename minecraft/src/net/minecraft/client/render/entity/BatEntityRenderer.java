package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BatEntityRenderer extends MobEntityRenderer<BatEntity, BatEntityModel> {
	private static final Identifier SKIN = new Identifier("textures/entity/bat.png");

	public BatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BatEntityModel(), 0.25F);
	}

	public Identifier method_3883(BatEntity batEntity) {
		return SKIN;
	}

	protected void method_3884(BatEntity batEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(0.35F, 0.35F, 0.35F);
	}

	protected void method_3882(BatEntity batEntity, MatrixStack matrixStack, float f, float g, float h) {
		if (batEntity.isRoosting()) {
			matrixStack.translate(0.0, -0.1F, 0.0);
		} else {
			matrixStack.translate(0.0, (double)(MathHelper.cos(f * 0.3F) * 0.1F), 0.0);
		}

		super.setupTransforms(batEntity, matrixStack, f, g, h);
	}
}
