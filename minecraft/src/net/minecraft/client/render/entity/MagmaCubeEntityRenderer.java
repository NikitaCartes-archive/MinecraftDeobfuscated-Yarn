package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/magmacube.png");

	public MagmaCubeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new MagmaCubeEntityModel<>(context.getPart(EntityModelLayers.MAGMA_CUBE)), 0.25F);
	}

	protected int getBlockLight(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(MagmaCubeEntity magmaCubeEntity) {
		return TEXTURE;
	}

	public void render(MagmaCubeEntity magmaCubeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.shadowRadius = 0.25F * (float)magmaCubeEntity.getSize();
		super.render(magmaCubeEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected void scale(MagmaCubeEntity magmaCubeEntity, MatrixStack matrixStack, float f) {
		int i = magmaCubeEntity.getSize();
		float g = MathHelper.lerp(f, magmaCubeEntity.lastStretch, magmaCubeEntity.stretch) / ((float)i * 0.5F + 1.0F);
		float h = 1.0F / (g + 1.0F);
		matrixStack.scale(h * (float)i, 1.0F / h * (float)i, h * (float)i);
	}
}
