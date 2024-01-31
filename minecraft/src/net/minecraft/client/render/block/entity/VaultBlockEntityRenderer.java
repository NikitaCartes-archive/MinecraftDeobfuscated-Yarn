package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class VaultBlockEntityRenderer implements BlockEntityRenderer<VaultBlockEntity> {
	private final ItemRenderer itemRenderer;
	private final Random random = Random.create();

	public VaultBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	public void render(VaultBlockEntity vaultBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (VaultBlockEntity.Client.hasDisplayItem(vaultBlockEntity.getSharedData())) {
			World world = vaultBlockEntity.getWorld();
			if (world != null) {
				ItemStack itemStack = vaultBlockEntity.getSharedData().getDisplayItem();
				if (!itemStack.isEmpty()) {
					this.random.setSeed((long)ItemEntityRenderer.getSeed(itemStack));
					VaultClientData vaultClientData = vaultBlockEntity.getClientData();
					renderDisplayItem(
						f,
						world,
						matrixStack,
						vertexConsumerProvider,
						i,
						itemStack,
						this.itemRenderer,
						vaultClientData.getPreviousDisplayRotation(),
						vaultClientData.getDisplayRotation(),
						this.random
					);
				}
			}
		}
	}

	public static void renderDisplayItem(
		float tickDelta,
		World world,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ItemStack stack,
		ItemRenderer itemRenderer,
		float prevRotation,
		float rotation,
		Random random
	) {
		matrices.push();
		matrices.translate(0.5F, 0.4F, 0.5F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerpAngleDegrees(tickDelta, prevRotation, rotation)));
		ItemEntityRenderer.renderStack(itemRenderer, matrices, vertexConsumers, light, stack, random, world);
		matrices.pop();
	}
}
