package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier[] SKIN = new Identifier[]{
		new Identifier("textures/entity/boat/oak.png"),
		new Identifier("textures/entity/boat/spruce.png"),
		new Identifier("textures/entity/boat/birch.png"),
		new Identifier("textures/entity/boat/jungle.png"),
		new Identifier("textures/entity/boat/acacia.png"),
		new Identifier("textures/entity/boat/dark_oak.png")
	};
	protected final BoatEntityModel model = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.8F;
	}

	public void method_3888(BoatEntity boatEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22904(0.0, 0.375, 0.0);
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F - g, true));
		float i = (float)boatEntity.getDamageWobbleTicks() - h;
		float j = boatEntity.getDamageWobbleStrength() - h;
		if (j < 0.0F) {
			j = 0.0F;
		}

		if (i > 0.0F) {
			arg.method_22907(Vector3f.field_20703.method_23214(MathHelper.sin(i) * i * j / 10.0F * (float)boatEntity.getDamageWobbleSide(), true));
		}

		float k = boatEntity.interpolateBubbleWobble(h);
		if (!MathHelper.approximatelyEquals(k, 0.0F)) {
			arg.method_22907(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boatEntity.interpolateBubbleWobble(h), true));
		}

		arg.method_22905(-1.0F, -1.0F, 1.0F);
		int l = boatEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.method_3891(boatEntity)));
		class_4608.method_23211(lv);
		arg.method_22907(Vector3f.field_20705.method_23214(90.0F, true));
		this.model.method_22952(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		this.model.method_22957(arg, lv, l);
		class_4588 lv2 = arg2.getBuffer(BlockRenderLayer.WATER_MASK);
		this.model.method_22954().method_22698(arg, lv2, 0.0625F, l, null);
		arg.method_22909();
		lv.method_22923();
		super.render(boatEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_3891(BoatEntity boatEntity) {
		return SKIN[boatEntity.getBoatType().ordinal()];
	}
}
