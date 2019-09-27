package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
	private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel<>();

	public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3962(EvokerFangsEntity evokerFangsEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		float i = evokerFangsEntity.getAnimationProgress(h);
		if (i != 0.0F) {
			float j = 2.0F;
			if (i > 0.9F) {
				j = (float)((double)j * ((1.0 - (double)i) / 0.1F));
			}

			arg.method_22903();
			arg.method_22907(Vector3f.field_20705.method_23214(90.0F - evokerFangsEntity.yaw, true));
			arg.method_22905(-j, -j, j);
			float k = 0.03125F;
			arg.method_22904(0.0, -0.626F, 0.0);
			int l = evokerFangsEntity.getLightmapCoordinates();
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
			class_4608.method_23211(lv);
			this.model.setAngles(evokerFangsEntity, i, 0.0F, 0.0F, evokerFangsEntity.yaw, evokerFangsEntity.pitch, 0.03125F);
			this.model.method_22957(arg, lv, l);
			lv.method_22923();
			arg.method_22909();
			super.render(evokerFangsEntity, d, e, f, g, h, arg, arg2);
		}
	}

	public Identifier method_3963(EvokerFangsEntity evokerFangsEntity) {
		return SKIN;
	}
}
