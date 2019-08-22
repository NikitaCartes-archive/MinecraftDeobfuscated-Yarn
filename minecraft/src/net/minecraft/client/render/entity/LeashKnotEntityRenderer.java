package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LeashKnotEntityRenderer extends EntityRenderer<LeadKnotEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
	private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel<>();

	public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4035(LeadKnotEntity leadKnotEntity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		RenderSystem.disableCull();
		RenderSystem.translatef((float)d, (float)e, (float)f);
		float i = 0.0625F;
		RenderSystem.enableRescaleNormal();
		RenderSystem.scalef(-1.0F, -1.0F, 1.0F);
		RenderSystem.enableAlphaTest();
		this.bindEntityTexture(leadKnotEntity);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(leadKnotEntity));
		}

		this.model.render(leadKnotEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		if (this.renderOutlines) {
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		}

		RenderSystem.popMatrix();
		super.render(leadKnotEntity, d, e, f, g, h);
	}

	protected Identifier method_4036(LeadKnotEntity leadKnotEntity) {
		return SKIN;
	}
}
