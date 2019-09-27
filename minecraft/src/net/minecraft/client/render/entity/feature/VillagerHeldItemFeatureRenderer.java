package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, VillagerResemblingModel<T>> {
	public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, VillagerResemblingModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18147(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		arg.method_22903();
		arg.method_22904(0.0, 0.4F, -0.4F);
		arg.method_22907(Vector3f.field_20703.method_23214(180.0F, true));
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Type.GROUND, false, arg, arg2);
		arg.method_22909();
	}
}
