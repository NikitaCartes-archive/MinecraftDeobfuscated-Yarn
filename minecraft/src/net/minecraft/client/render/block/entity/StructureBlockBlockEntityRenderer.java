package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer extends BlockEntityRenderer<StructureBlockBlockEntity> {
	public StructureBlockBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3587(
		StructureBlockBlockEntity structureBlockBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		int j
	) {
		if (MinecraftClient.getInstance().player.isCreativeLevelTwoOp() || MinecraftClient.getInstance().player.isSpectator()) {
			BlockPos blockPos = structureBlockBlockEntity.getOffset();
			BlockPos blockPos2 = structureBlockBlockEntity.getSize();
			if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
				if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.getMode() == StructureBlockMode.LOAD) {
					double h = (double)blockPos.getX();
					double k = (double)blockPos.getZ();
					double l = (double)blockPos.getY();
					double m = l + (double)blockPos2.getY();
					double n;
					double o;
					switch (structureBlockBlockEntity.getMirror()) {
						case LEFT_RIGHT:
							n = (double)blockPos2.getX();
							o = (double)(-blockPos2.getZ());
							break;
						case FRONT_BACK:
							n = (double)(-blockPos2.getX());
							o = (double)blockPos2.getZ();
							break;
						default:
							n = (double)blockPos2.getX();
							o = (double)blockPos2.getZ();
					}

					double p;
					double q;
					double r;
					double s;
					switch (structureBlockBlockEntity.getRotation()) {
						case CLOCKWISE_90:
							p = o < 0.0 ? h : h + 1.0;
							q = n < 0.0 ? k + 1.0 : k;
							r = p - o;
							s = q + n;
							break;
						case CLOCKWISE_180:
							p = n < 0.0 ? h : h + 1.0;
							q = o < 0.0 ? k : k + 1.0;
							r = p - n;
							s = q - o;
							break;
						case COUNTERCLOCKWISE_90:
							p = o < 0.0 ? h + 1.0 : h;
							q = n < 0.0 ? k : k + 1.0;
							r = p + o;
							s = q - n;
							break;
						default:
							p = n < 0.0 ? h + 1.0 : h;
							q = o < 0.0 ? k + 1.0 : k;
							r = p + n;
							s = q + o;
					}

					float t = 1.0F;
					float u = 0.9F;
					float v = 0.5F;
					VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLines());
					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
						WorldRenderer.method_22981(matrixStack, vertexConsumer, p, l, q, r, m, s, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
					}

					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
						this.method_3585(structureBlockBlockEntity, vertexConsumer, blockPos, true, matrixStack);
						this.method_3585(structureBlockBlockEntity, vertexConsumer, blockPos, false, matrixStack);
					}
				}
			}
		}
	}

	private void method_3585(
		StructureBlockBlockEntity structureBlockBlockEntity, VertexConsumer vertexConsumer, BlockPos blockPos, boolean bl, MatrixStack matrixStack
	) {
		BlockView blockView = structureBlockBlockEntity.getWorld();
		BlockPos blockPos2 = structureBlockBlockEntity.getPos();
		BlockPos blockPos3 = blockPos2.add(blockPos);

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockBlockEntity.getSize()).add(-1, -1, -1))) {
			BlockState blockState = blockView.getBlockState(blockPos4);
			boolean bl2 = blockState.isAir();
			boolean bl3 = blockState.getBlock() == Blocks.STRUCTURE_VOID;
			if (bl2 || bl3) {
				float f = bl2 ? 0.05F : 0.0F;
				double d = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.45F - f);
				double e = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.45F - f);
				double g = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.45F - f);
				double h = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.55F + f);
				double i = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.55F + f);
				double j = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.55F + f);
				if (bl) {
					WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
				} else if (bl2) {
					WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
				} else {
					WorldRenderer.method_22981(matrixStack, vertexConsumer, d, e, g, h, i, j, 1.0F, 0.25F, 0.25F, 1.0F, 1.0F, 0.25F, 0.25F);
				}
			}
		}
	}

	public boolean method_3588(StructureBlockBlockEntity structureBlockBlockEntity) {
		return true;
	}
}
