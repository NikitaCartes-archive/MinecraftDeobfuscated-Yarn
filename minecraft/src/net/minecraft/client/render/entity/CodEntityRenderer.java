package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CodEntityModel<>(), 0.3F);
	}

	public Identifier method_3897(CodEntity codEntity) {
		return SKIN;
	}

	protected void method_3896(CodEntity codEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(codEntity, arg, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		arg.method_22907(Vector3f.field_20705.method_23214(i, true));
		if (!codEntity.isInsideWater()) {
			arg.method_22904(0.1F, 0.1F, -0.1F);
			arg.method_22907(Vector3f.field_20707.method_23214(90.0F, true));
		}
	}
}
