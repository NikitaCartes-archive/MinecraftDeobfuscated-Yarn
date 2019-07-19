package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final SkeletonEntityModel<T> model = new SkeletonEntityModel<>(0.25F, true);

	public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	public void render(T mobEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.getContextModel().copyStateTo(this.model);
		this.model.animateModel(mobEntity, f, g, h);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(SKIN);
		this.model.render(mobEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
