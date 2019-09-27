package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
	private final ModelPart field_21012 = new ModelPart(64, 64, 0, 0);

	public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
		this.field_21012.addCuboid(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
	}

	public void method_4203(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (livingEntity.isUsingRiptide()) {
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(TEXTURE));
			class_4608.method_23211(lv);

			for (int n = 0; n < 3; n++) {
				arg.method_22903();
				arg.method_22907(Vector3f.field_20705.method_23214(j * (float)(-(45 + n * 5)), true));
				float o = 0.75F * (float)n;
				arg.method_22905(o, o, o);
				arg.method_22904(0.0, (double)(-0.2F + 0.6F * (float)n), 0.0);
				this.field_21012.method_22698(arg, lv, m, i, null);
				arg.method_22909();
			}

			lv.method_22923();
		}
	}
}
