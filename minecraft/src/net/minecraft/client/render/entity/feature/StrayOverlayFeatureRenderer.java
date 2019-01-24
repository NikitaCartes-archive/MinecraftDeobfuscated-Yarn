package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends LivingEntity & RangedAttacker, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final StrayEntityModel<T> model = new StrayEntityModel<>(0.25F, true);

	public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4206(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.getModel().method_17081(this.model);
		this.model.method_17086(livingEntity, f, g, h);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(SKIN);
		this.model.method_17088(livingEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
