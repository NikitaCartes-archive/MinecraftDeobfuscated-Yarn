package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
	public static final Identifier BASE_TEX = new Identifier("entity/conduit/base");
	public static final Identifier CAGE_TEX = new Identifier("entity/conduit/cage");
	public static final Identifier WIND_TEX = new Identifier("entity/conduit/wind");
	public static final Identifier WIND_VERTICAL_TEX = new Identifier("entity/conduit/wind_vertical");
	public static final Identifier OPEN_EYE_TEX = new Identifier("entity/conduit/open_eye");
	public static final Identifier CLOSED_EYE_TEX = new Identifier("entity/conduit/closed_eye");
	private final ModelPart field_20823 = new ModelPart(8, 8, 0, 0);
	private final ModelPart field_20824;
	private final ModelPart field_20825;
	private final ModelPart field_20826;

	public ConduitBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20823.addCuboid(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
		this.field_20824 = new ModelPart(64, 32, 0, 0);
		this.field_20824.addCuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
		this.field_20825 = new ModelPart(32, 16, 0, 0);
		this.field_20825.addCuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
		this.field_20826 = new ModelPart(32, 16, 0, 0);
		this.field_20826.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
	}

	public void method_22750(ConduitBlockEntity conduitBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		float h = (float)conduitBlockEntity.ticks + g;
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
		if (!conduitBlockEntity.isActive()) {
			float j = conduitBlockEntity.getRotation(0.0F);
			arg.method_22903();
			arg.method_22904(0.5, 0.5, 0.5);
			arg.method_22907(Vector3f.field_20705.method_23214(j, true));
			this.field_20825.method_22698(arg, lv, 0.0625F, i, this.method_23082(BASE_TEX));
			arg.method_22909();
		} else {
			class_4588 lv2 = arg2.getBuffer(BlockRenderLayer.CUTOUT_MIPPED);
			float k = conduitBlockEntity.getRotation(g) * (180.0F / (float)Math.PI);
			float l = MathHelper.sin(h * 0.1F) / 2.0F + 0.5F;
			l = l * l + l;
			arg.method_22903();
			arg.method_22904(0.5, (double)(0.3F + l * 0.2F), 0.5);
			Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
			vector3f.reciprocal();
			arg.method_22907(new Quaternion(vector3f, k, true));
			this.field_20826.method_22698(arg, lv2, 0.0625F, i, this.method_23082(CAGE_TEX));
			arg.method_22909();
			int m = conduitBlockEntity.ticks / 66 % 3;
			arg.method_22903();
			arg.method_22904(0.5, 0.5, 0.5);
			if (m == 1) {
				arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
			} else if (m == 2) {
				arg.method_22907(Vector3f.field_20707.method_23214(90.0F, true));
			}

			Sprite sprite = this.method_23082(m == 1 ? WIND_VERTICAL_TEX : WIND_TEX);
			this.field_20824.method_22698(arg, lv2, 0.0625F, i, sprite);
			arg.method_22909();
			arg.method_22903();
			arg.method_22904(0.5, 0.5, 0.5);
			arg.method_22905(0.875F, 0.875F, 0.875F);
			arg.method_22907(Vector3f.field_20703.method_23214(180.0F, true));
			arg.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
			this.field_20824.method_22698(arg, lv2, 0.0625F, i, sprite);
			arg.method_22909();
			Camera camera = this.field_20989.cameraEntity;
			arg.method_22903();
			arg.method_22904(0.5, (double)(0.3F + l * 0.2F), 0.5);
			arg.method_22905(0.5F, 0.5F, 0.5F);
			arg.method_22907(Vector3f.field_20705.method_23214(-camera.getYaw(), true));
			arg.method_22907(Vector3f.field_20703.method_23214(camera.getPitch(), true));
			arg.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
			this.field_20823.method_22698(arg, lv2, 0.083333336F, i, this.method_23082(conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEX : CLOSED_EYE_TEX));
			arg.method_22909();
		}
	}
}
