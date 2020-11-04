package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> implements BlockEntityRenderer<T> {
	private final ModelPart singleChestLid;
	private final ModelPart singleChestBase;
	private final ModelPart singleChestLatch;
	private final ModelPart doubleChestRightLid;
	private final ModelPart doubleChestRightBase;
	private final ModelPart doubleChestRightLatch;
	private final ModelPart doubleChestLeftLid;
	private final ModelPart doubleChestLeftBase;
	private final ModelPart doubleChestLeftLatch;
	private boolean christmas;

	public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.christmas = true;
		}

		ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.CHEST);
		this.singleChestBase = modelPart.method_32086("bottom");
		this.singleChestLid = modelPart.method_32086("lid");
		this.singleChestLatch = modelPart.method_32086("lock");
		ModelPart modelPart2 = context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleChestRightBase = modelPart2.method_32086("bottom");
		this.doubleChestRightLid = modelPart2.method_32086("lid");
		this.doubleChestRightLatch = modelPart2.method_32086("lock");
		ModelPart modelPart3 = context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleChestLeftBase = modelPart3.method_32086("bottom");
		this.doubleChestLeftLid = modelPart3.method_32086("lid");
		this.doubleChestLeftLatch = modelPart3.method_32086("lock");
	}

	public static class_5607 method_32147() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("bottom", class_5606.method_32108().method_32101(0, 19).method_32097(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), class_5603.field_27701);
		lv2.method_32117(
			"lid", class_5606.method_32108().method_32101(0, 0).method_32097(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), class_5603.method_32090(0.0F, 9.0F, 1.0F)
		);
		lv2.method_32117(
			"lock", class_5606.method_32108().method_32101(0, 0).method_32097(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), class_5603.method_32090(0.0F, 8.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public static class_5607 method_32148() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("bottom", class_5606.method_32108().method_32101(0, 19).method_32097(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), class_5603.field_27701);
		lv2.method_32117(
			"lid", class_5606.method_32108().method_32101(0, 0).method_32097(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), class_5603.method_32090(0.0F, 9.0F, 1.0F)
		);
		lv2.method_32117(
			"lock", class_5606.method_32108().method_32101(0, 0).method_32097(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), class_5603.method_32090(0.0F, 8.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public static class_5607 method_32149() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("bottom", class_5606.method_32108().method_32101(0, 19).method_32097(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), class_5603.field_27701);
		lv2.method_32117(
			"lid", class_5606.method_32108().method_32101(0, 0).method_32097(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), class_5603.method_32090(0.0F, 9.0F, 1.0F)
		);
		lv2.method_32117(
			"lock", class_5606.method_32108().method_32101(0, 0).method_32097(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), class_5603.method_32090(0.0F, 8.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = entity.getWorld();
		boolean bl = world != null;
		BlockState blockState = bl ? entity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		Block block = blockState.getBlock();
		if (block instanceof AbstractChestBlock) {
			AbstractChestBlock<?> abstractChestBlock = (AbstractChestBlock<?>)block;
			boolean bl2 = chestType != ChestType.SINGLE;
			matrices.push();
			float f = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
			matrices.translate(0.5, 0.5, 0.5);
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
			matrices.translate(-0.5, -0.5, -0.5);
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
			SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, chestType, this.christmas);
			VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
			if (bl2) {
				if (chestType == ChestType.LEFT) {
					this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
				} else {
					this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
				}
			} else {
				this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, g, i, overlay);
			}

			matrices.pop();
		}
	}

	private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
		lid.pitch = -(openFactor * (float) (Math.PI / 2));
		latch.pitch = lid.pitch;
		lid.render(matrices, vertices, light, overlay);
		latch.render(matrices, vertices, light, overlay);
		base.render(matrices, vertices, light, overlay);
	}
}
