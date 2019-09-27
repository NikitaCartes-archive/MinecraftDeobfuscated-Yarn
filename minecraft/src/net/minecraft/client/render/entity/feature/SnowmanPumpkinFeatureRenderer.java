package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4201(class_4587 arg, class_4597 arg2, int i, SnowGolemEntity snowGolemEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (!snowGolemEntity.isInvisible() && snowGolemEntity.hasPumpkin()) {
			arg.method_22903();
			this.getModel().method_2834().method_22703(arg, 0.0625F);
			float n = 0.625F;
			arg.method_22904(0.0, -0.34375, 0.0);
			arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
			arg.method_22905(0.625F, -0.625F, -0.625F);
			ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
			MinecraftClient.getInstance()
				.getItemRenderer()
				.method_23177(snowGolemEntity, itemStack, ModelTransformation.Type.HEAD, false, arg, arg2, snowGolemEntity.world, snowGolemEntity.getLightmapCoordinates());
			arg.method_22909();
		}
	}
}
