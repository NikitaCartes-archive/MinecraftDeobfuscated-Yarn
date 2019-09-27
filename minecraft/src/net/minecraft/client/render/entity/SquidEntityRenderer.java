package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/squid.png");

	public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SquidEntityModel<>(), 0.7F);
	}

	public Identifier method_4127(SquidEntity squidEntity) {
		return SKIN;
	}

	protected void method_4126(SquidEntity squidEntity, class_4587 arg, float f, float g, float h) {
		float i = MathHelper.lerp(h, squidEntity.field_6905, squidEntity.field_6907);
		float j = MathHelper.lerp(h, squidEntity.field_6906, squidEntity.field_6903);
		arg.method_22904(0.0, 0.5, 0.0);
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F - g, true));
		arg.method_22907(Vector3f.field_20703.method_23214(i, true));
		arg.method_22907(Vector3f.field_20705.method_23214(j, true));
		arg.method_22904(0.0, -1.2F, 0.0);
	}

	protected float method_4125(SquidEntity squidEntity, float f) {
		return MathHelper.lerp(f, squidEntity.field_6900, squidEntity.field_6904);
	}
}
