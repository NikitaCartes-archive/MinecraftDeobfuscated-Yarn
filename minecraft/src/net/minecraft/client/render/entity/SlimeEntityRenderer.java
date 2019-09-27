package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderer extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/slime/slime.png");

	public SlimeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SlimeEntityModel<>(16), 0.25F);
		this.addFeature(new SlimeOverlayFeatureRenderer<>(this));
	}

	public void method_4117(SlimeEntity slimeEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		this.field_4673 = 0.25F * (float)slimeEntity.getSize();
		super.method_4072(slimeEntity, d, e, f, g, h, arg, arg2);
	}

	protected void method_4118(SlimeEntity slimeEntity, class_4587 arg, float f) {
		float g = 0.999F;
		arg.method_22905(0.999F, 0.999F, 0.999F);
		arg.method_22904(0.0, 0.001F, 0.0);
		float h = (float)slimeEntity.getSize();
		float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		arg.method_22905(j * h, 1.0F / j * h, j * h);
	}

	public Identifier method_4116(SlimeEntity slimeEntity) {
		return SKIN;
	}
}
