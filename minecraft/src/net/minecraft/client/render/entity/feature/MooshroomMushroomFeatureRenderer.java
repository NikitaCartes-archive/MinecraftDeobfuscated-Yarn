package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity> extends FeatureRenderer<T, CowEntityModel<T>> {
	public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4195(class_4587 arg, class_4597 arg2, int i, T mooshroomEntity, float f, float g, float h, float j, float k, float l, float m) {
		if (!mooshroomEntity.isBaby() && !mooshroomEntity.isInvisible()) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			BlockState blockState = mooshroomEntity.getMooshroomType().getMushroomState();
			arg.method_22903();
			arg.method_22905(-1.0F, -1.0F, 1.0F);
			arg.method_22904(-0.2F, 0.35F, 0.5);
			arg.method_22907(Vector3f.field_20705.method_23214(-42.0F, true));
			int n = class_4608.method_23212(mooshroomEntity.hurtTime > 0 || mooshroomEntity.deathTime > 0);
			arg.method_22903();
			arg.method_22904(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
			arg.method_22909();
			arg.method_22903();
			arg.method_22904(-0.1F, 0.0, -0.6F);
			arg.method_22907(Vector3f.field_20705.method_23214(-42.0F, true));
			arg.method_22904(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
			arg.method_22909();
			arg.method_22909();
			arg.method_22903();
			this.getModel().getHead().method_22703(arg, 0.0625F);
			arg.method_22905(-1.0F, -1.0F, 1.0F);
			arg.method_22904(0.0, 0.7F, -0.2F);
			arg.method_22907(Vector3f.field_20705.method_23214(-12.0F, true));
			arg.method_22904(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
			arg.method_22909();
		}
	}
}
