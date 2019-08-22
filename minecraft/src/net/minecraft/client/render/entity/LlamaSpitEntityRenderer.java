package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel<LlamaSpitEntity> model = new LlamaSpitEntityModel<>();

	public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4061(LlamaSpitEntity llamaSpitEntity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)d, (float)e + 0.15F, (float)f);
		RenderSystem.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch), 0.0F, 0.0F, 1.0F);
		this.bindEntityTexture(llamaSpitEntity);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(llamaSpitEntity));
		}

		this.model.render(llamaSpitEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.renderOutlines) {
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		}

		RenderSystem.popMatrix();
		super.render(llamaSpitEntity, d, e, f, g, h);
	}

	protected Identifier method_4062(LlamaSpitEntity llamaSpitEntity) {
		return SKIN;
	}
}
