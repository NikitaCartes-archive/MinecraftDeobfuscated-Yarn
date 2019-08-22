package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final StrayEntityModel<T> model = new StrayEntityModel<>(0.25F, true);

	public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4206(T mobEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.getModel().copyStateTo(this.model);
		this.model.method_19689(mobEntity, f, g, h);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(SKIN);
		this.model.method_17088(mobEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
