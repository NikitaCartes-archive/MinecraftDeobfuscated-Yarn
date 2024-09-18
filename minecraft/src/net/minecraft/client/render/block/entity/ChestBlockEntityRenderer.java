package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {
	private final ChestBlockModel singleChest;
	private final ChestBlockModel doubleChestLeft;
	private final ChestBlockModel doubleChestRight;
	private boolean christmas;

	public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.christmas = true;
		}

		this.singleChest = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
		this.doubleChestLeft = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
		this.doubleChestRight = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = entity.getWorld();
		boolean bl = world != null;
		BlockState blockState = bl ? entity.getCachedState() : Blocks.CHEST.getDefaultState().with((Property<T>)ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		if (blockState.getBlock() instanceof AbstractChestBlock<?> abstractChestBlock) {
			boolean bl2 = chestType != ChestType.SINGLE;
			matrices.push();
			float f = ((Direction)blockState.get((Property<T>)ChestBlock.FACING)).asRotation();
			matrices.translate(0.5F, 0.5F, 0.5F);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
			matrices.translate(-0.5F, -0.5F, -0.5F);
			DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> propertySource;
			if (bl) {
				propertySource = abstractChestBlock.getBlockEntitySource(blockState, world, entity.getPos(), true);
			} else {
				propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
			}

			float g = propertySource.apply(ChestBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
			g = 1.0F - g;
			g = 1.0F - g * g * g;
			int i = propertySource.apply(new LightmapCoordinatesRetriever<>()).applyAsInt(light);
			SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTextureId(entity, chestType, this.christmas);
			VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
			if (bl2) {
				if (chestType == ChestType.LEFT) {
					this.render(matrices, vertexConsumer, this.doubleChestLeft, g, i, overlay);
				} else {
					this.render(matrices, vertexConsumer, this.doubleChestRight, g, i, overlay);
				}
			} else {
				this.render(matrices, vertexConsumer, this.singleChest, g, i, overlay);
			}

			matrices.pop();
		}
	}

	private void render(MatrixStack matrices, VertexConsumer vertices, ChestBlockModel model, float animationProgress, int light, int overlay) {
		model.setLockAndLidPitch(animationProgress);
		model.render(matrices, vertices, light, overlay);
	}
}
