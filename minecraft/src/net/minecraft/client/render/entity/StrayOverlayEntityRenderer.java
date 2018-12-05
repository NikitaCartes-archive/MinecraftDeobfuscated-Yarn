package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayEntityRenderer implements LayerEntityRenderer<StrayEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final LivingEntityRenderer<?> renderer;
	private final StrayEntityModel model = new StrayEntityModel(0.25F, true);

	public StrayOverlayEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.renderer = livingEntityRenderer;
	}

	public void render(StrayEntity strayEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.model.setAttributes(this.renderer.method_4038());
		this.model.animateModel(strayEntity, f, g, h);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderer.bindTexture(SKIN);
		this.model.render(strayEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
