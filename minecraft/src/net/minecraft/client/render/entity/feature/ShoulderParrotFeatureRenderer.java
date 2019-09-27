package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	private final ParrotEntityModel model = new ParrotEntityModel();

	public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4185(class_4587 arg, class_4597 arg2, int i, T playerEntity, float f, float g, float h, float j, float k, float l, float m) {
		this.renderShoulderParrot(arg, arg2, i, playerEntity, f, g, h, k, l, m, true);
		this.renderShoulderParrot(arg, arg2, i, playerEntity, f, g, h, k, l, m, false);
	}

	private void renderShoulderParrot(class_4587 arg, class_4597 arg2, int i, T playerEntity, float f, float g, float h, float j, float k, float l, boolean bl) {
		CompoundTag compoundTag = bl ? playerEntity.getShoulderEntityLeft() : playerEntity.getShoulderEntityRight();
		EntityType.get(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
			arg.method_22903();
			arg.method_22904(bl ? 0.4F : -0.4F, playerEntity.isInSneakingPose() ? -1.3F : -1.5, 0.0);
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(ParrotEntityRenderer.SKINS[compoundTag.getInt("Variant")]));
			class_4608.method_23211(lv);
			this.model.method_17106(arg, lv, i, f, g, j, k, l, playerEntity.age);
			lv.method_22923();
			arg.method_22909();
		});
	}
}
