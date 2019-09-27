package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fish/salmon.png");

	public SalmonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SalmonEntityModel<>(), 0.4F);
	}

	public Identifier method_4101(SalmonEntity salmonEntity) {
		return SKIN;
	}

	protected void method_4100(SalmonEntity salmonEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(salmonEntity, arg, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!salmonEntity.isInsideWater()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * MathHelper.sin(j * 0.6F * f);
		arg.method_22907(Vector3f.field_20705.method_23214(k, true));
		arg.method_22904(0.0, 0.0, -0.4F);
		if (!salmonEntity.isInsideWater()) {
			arg.method_22904(0.2F, 0.1F, 0.0);
			arg.method_22907(Vector3f.field_20707.method_23214(90.0F, true));
		}
	}
}
