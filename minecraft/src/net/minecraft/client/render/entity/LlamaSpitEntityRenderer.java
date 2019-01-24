package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier field_4745 = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel<LlamaSpitEntity> field_4744 = new LlamaSpitEntityModel<>();

	public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4061(LlamaSpitEntity llamaSpitEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch), 0.0F, 0.0F, 1.0F);
		this.bindEntityTexture(llamaSpitEntity);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(llamaSpitEntity));
		}

		this.field_4744.render(llamaSpitEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(llamaSpitEntity, d, e, f, g, h);
	}

	protected Identifier method_4062(LlamaSpitEntity llamaSpitEntity) {
		return field_4745;
	}
}
