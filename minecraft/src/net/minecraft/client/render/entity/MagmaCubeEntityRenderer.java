package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity, SlimeEntityRenderState, MagmaCubeEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/magmacube.png");

	public MagmaCubeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new MagmaCubeEntityModel(context.getPart(EntityModelLayers.MAGMA_CUBE)), 0.25F);
	}

	protected int getBlockLight(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
		return TEXTURE;
	}

	public SlimeEntityRenderState getRenderState() {
		return new SlimeEntityRenderState();
	}

	public void updateRenderState(MagmaCubeEntity magmaCubeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
		super.updateRenderState(magmaCubeEntity, slimeEntityRenderState, f);
		slimeEntityRenderState.stretch = MathHelper.lerp(f, magmaCubeEntity.lastStretch, magmaCubeEntity.stretch);
		slimeEntityRenderState.size = magmaCubeEntity.getSize();
	}

	public void render(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.shadowRadius = 0.25F * (float)slimeEntityRenderState.size;
		super.render(slimeEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
		int i = slimeEntityRenderState.size;
		float f = slimeEntityRenderState.stretch / ((float)i * 0.5F + 1.0F);
		float g = 1.0F / (f + 1.0F);
		matrixStack.scale(g * (float)i, 1.0F / g * (float)i, g * (float)i);
	}
}
