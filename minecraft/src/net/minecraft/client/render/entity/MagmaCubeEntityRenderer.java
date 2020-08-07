package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/slime/magmacube.png");

	public MagmaCubeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new MagmaCubeEntityModel<>(), 0.25F);
	}

	protected int method_24090(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier method_4001(MagmaCubeEntity magmaCubeEntity) {
		return TEXTURE;
	}

	protected void method_4000(MagmaCubeEntity magmaCubeEntity, MatrixStack matrixStack, float f) {
		int i = magmaCubeEntity.getSize();
		float g = MathHelper.lerp(f, magmaCubeEntity.lastStretch, magmaCubeEntity.stretch) / ((float)i * 0.5F + 1.0F);
		float h = 1.0F / (g + 1.0F);
		matrixStack.scale(h * (float)i, 1.0F / h * (float)i, h * (float)i);
	}
}
