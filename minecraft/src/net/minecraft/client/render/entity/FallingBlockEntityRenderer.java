package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7323;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity> {
	private final ItemRenderer field_38669;

	public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.field_38669 = context.getItemRenderer();
		this.shadowRadius = 0.5F;
	}

	public void render(FallingBlockEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.isOf(Blocks.LAVA)) {
			blockState = class_7323.method_42878(Items.LAVA_BUCKET);
		} else if (blockState.isOf(Blocks.WATER)) {
			blockState = class_7323.method_42878(Items.WATER_BUCKET);
		}

		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getWorld();
			if ((blockState.isOf(Blocks.GENERIC_ITEM_BLOCK) || blockState != world.getBlockState(fallingBlockEntity.getBlockPos()))
				&& blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				matrixStack.push();
				BlockPos blockPos = new BlockPos(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
				Item item = class_7323.method_42881(blockState);
				if (item != null) {
					ItemStack itemStack = new ItemStack(item);
					BakedModel bakedModel = this.field_38669.getModel(itemStack, fallingBlockEntity.world, null, fallingBlockEntity.getId());
					float h = fallingBlockEntity.method_42815(g);
					matrixStack.push();
					matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
					matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(h));
					this.field_38669
						.renderItem(itemStack, ModelTransformation.Mode.FIXED, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
					matrixStack.pop();
				} else {
					matrixStack.translate(-0.5, 0.0, -0.5);
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
					blockRenderManager.getModelRenderer()
						.render(
							world,
							blockRenderManager.getModel(blockState),
							blockState,
							blockPos,
							matrixStack,
							vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
							false,
							new Random(),
							blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos()),
							OverlayTexture.DEFAULT_UV
						);
				}

				matrixStack.pop();
				super.render(fallingBlockEntity, f, g, matrixStack, vertexConsumerProvider, i);
			}
		}
	}

	public Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
