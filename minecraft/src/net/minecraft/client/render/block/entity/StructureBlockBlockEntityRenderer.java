package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer extends BlockEntityRenderer<StructureBlockBlockEntity> {
	public StructureBlockBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3587(StructureBlockBlockEntity structureBlockBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		if (MinecraftClient.getInstance().player.isCreativeLevelTwoOp() || MinecraftClient.getInstance().player.isSpectator()) {
			BlockPos blockPos = structureBlockBlockEntity.getOffset();
			BlockPos blockPos2 = structureBlockBlockEntity.getSize();
			if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
				if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.getMode() == StructureBlockMode.LOAD) {
					double h = (double)blockPos.getX();
					double j = (double)blockPos.getZ();
					double k = (double)blockPos.getY();
					double l = k + (double)blockPos2.getY();
					double m;
					double n;
					switch (structureBlockBlockEntity.getMirror()) {
						case LEFT_RIGHT:
							m = (double)blockPos2.getX();
							n = (double)(-blockPos2.getZ());
							break;
						case FRONT_BACK:
							m = (double)(-blockPos2.getX());
							n = (double)blockPos2.getZ();
							break;
						default:
							m = (double)blockPos2.getX();
							n = (double)blockPos2.getZ();
					}

					double o;
					double p;
					double q;
					double r;
					switch (structureBlockBlockEntity.getRotation()) {
						case CLOCKWISE_90:
							o = n < 0.0 ? h : h + 1.0;
							p = m < 0.0 ? j + 1.0 : j;
							q = o - n;
							r = p + m;
							break;
						case CLOCKWISE_180:
							o = m < 0.0 ? h : h + 1.0;
							p = n < 0.0 ? j : j + 1.0;
							q = o - m;
							r = p - n;
							break;
						case COUNTERCLOCKWISE_90:
							o = n < 0.0 ? h + 1.0 : h;
							p = m < 0.0 ? j : j + 1.0;
							q = o + n;
							r = p - m;
							break;
						default:
							o = m < 0.0 ? h + 1.0 : h;
							p = n < 0.0 ? j + 1.0 : j;
							q = o + m;
							r = p + n;
					}

					float s = 1.0F;
					float t = 0.9F;
					float u = 0.5F;
					class_4588 lv = arg2.getBuffer(BlockRenderLayer.LINES);
					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
						WorldRenderer.method_22981(arg, lv, o, k, p, q, l, r, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
					}

					if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
						this.method_3585(structureBlockBlockEntity, lv, blockPos, true, arg);
						this.method_3585(structureBlockBlockEntity, lv, blockPos, false, arg);
					}
				}
			}
		}
	}

	private void method_3585(StructureBlockBlockEntity structureBlockBlockEntity, class_4588 arg, BlockPos blockPos, boolean bl, class_4587 arg2) {
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
					WorldRenderer.method_22981(arg2, arg, d, e, g, h, i, j, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
				} else if (bl2) {
					WorldRenderer.method_22981(arg2, arg, d, e, g, h, i, j, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
				} else {
					WorldRenderer.method_22981(arg2, arg, d, e, g, h, i, j, 1.0F, 0.25F, 0.25F, 1.0F, 1.0F, 0.25F, 0.25F);
				}
			}
		}
	}

	public boolean method_3588(StructureBlockBlockEntity structureBlockBlockEntity) {
		return true;
	}
}
