package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer implements BlockEntityRenderer<StructureBlockBlockEntity> {
	public StructureBlockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	public void render(
		StructureBlockBlockEntity structureBlockBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		if (MinecraftClient.getInstance().player.isCreativeLevelTwoOp() || MinecraftClient.getInstance().player.isSpectator()) {
			BlockPos blockPos = structureBlockBlockEntity.getOffset();
			BlockPos blockPos2 = structureBlockBlockEntity.getSize();
			if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
				if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.getMode() == StructureBlockMode.LOAD) {
					double d = (double)blockPos.getX();
					double e = (double)blockPos.getZ();
					double g = (double)blockPos.getY();
					double h = g + (double)blockPos2.getY();
					double k;
					double l;
					switch (structureBlockBlockEntity.getMirror()) {
						case LEFT_RIGHT:
							k = (double)blockPos2.getX();
							l = (double)(-blockPos2.getZ());
							break;
						case FRONT_BACK:
							k = (double)(-blockPos2.getX());
							l = (double)blockPos2.getZ();
							break;
						default:
							k = (double)blockPos2.getX();
							l = (double)blockPos2.getZ();
					}

					double m;
					double n;
					double o;
					double p;
					switch (structureBlockBlockEntity.getRotation()) {
						case CLOCKWISE_90:
							m = l < 0.0 ? d : d + 1.0;
							n = k < 0.0 ? e + 1.0 : e;
							o = m - l;
							p = n + k;
							break;
						case CLOCKWISE_180:
							m = k < 0.0 ? d : d + 1.0;
							n = l < 0.0 ? e : e + 1.0;
							o = m - k;
							p = n - l;
							break;
						case COUNTERCLOCKWISE_90:
							m = l < 0.0 ? d + 1.0 : d;
							n = k < 0.0 ? e : e + 1.0;
							o = m + l;
							p = n - k;
							break;
						default:
							m = k < 0.0 ? d + 1.0 : d;
							n = l < 0.0 ? e + 1.0 : e;
							o = m + k;
							p = n + l;
					}

					float q = 1.0F;
					float r = 0.9F;
					float s = 0.5F;
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
						WorldRenderer.drawBox(matrixStack, vertexConsumer, m, g, n, o, h, p, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
					}

					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
						this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, true, matrixStack);
						this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, false, matrixStack);
					}
				}
			}
		}
	}

	private void renderInvisibleBlocks(StructureBlockBlockEntity entity, VertexConsumer vertices, BlockPos pos, boolean bl, MatrixStack matrices) {
		BlockView blockView = entity.getWorld();
		BlockPos blockPos = entity.getPos();
		BlockPos blockPos2 = blockPos.add(pos);

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(entity.getSize()).add(-1, -1, -1))) {
			BlockState blockState = blockView.getBlockState(blockPos3);
			boolean bl2 = blockState.isAir();
			boolean bl3 = blockState.isOf(Blocks.STRUCTURE_VOID);
			if (bl2 || bl3) {
				float f = bl2 ? 0.05F : 0.0F;
				double d = (double)((float)(blockPos3.getX() - blockPos.getX()) + 0.45F - f);
				double e = (double)((float)(blockPos3.getY() - blockPos.getY()) + 0.45F - f);
				double g = (double)((float)(blockPos3.getZ() - blockPos.getZ()) + 0.45F - f);
				double h = (double)((float)(blockPos3.getX() - blockPos.getX()) + 0.55F + f);
				double i = (double)((float)(blockPos3.getY() - blockPos.getY()) + 0.55F + f);
				double j = (double)((float)(blockPos3.getZ() - blockPos.getZ()) + 0.55F + f);
				if (bl) {
					WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
				} else if (bl2) {
					WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
				} else {
					WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 1.0F, 0.25F, 0.25F, 1.0F, 1.0F, 0.25F, 0.25F);
				}
			}
		}
	}

	public boolean rendersOutsideBoundingBox(StructureBlockBlockEntity structureBlockBlockEntity) {
		return true;
	}

	@Override
	public int method_33893() {
		return 96;
	}
}
