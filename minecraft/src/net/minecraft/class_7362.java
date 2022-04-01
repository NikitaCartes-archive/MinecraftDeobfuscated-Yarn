package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class class_7362<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public class_7362(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty() && itemStack.isOf(Items.BARREL)) {
			BlockState blockState = Blocks.BARREL.getDefaultState();
			if (blockState.contains(Properties.OPEN)) {
				boolean var10002;
				label35: {
					if (livingEntity instanceof PlayerEntity playerEntity && playerEntity.isInSneakingPose()) {
						var10002 = true;
						break label35;
					}

					var10002 = false;
				}

				blockState = blockState.with(Properties.OPEN, Boolean.valueOf(!var10002));
			}

			if (blockState.contains(Properties.FACING)) {
				blockState = blockState.with(Properties.FACING, Direction.UP);
			}

			label29: {
				matrixStack.push();
				if (livingEntity instanceof PlayerEntity playerEntity && playerEntity.isInSneakingPose()) {
					matrixStack.scale(1.07F, 1.07F, 1.07F);
					matrixStack.translate(-0.5, 0.28, -0.5);
					break label29;
				}

				matrixStack.translate(-0.5, -0.25, -0.5);
			}

			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
	}
}
