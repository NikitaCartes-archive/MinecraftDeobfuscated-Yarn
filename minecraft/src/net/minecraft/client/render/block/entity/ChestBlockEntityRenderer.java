package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4739;
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
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends BlockEntityRenderer<T> {
	private final ModelPart field_20817;
	private final ModelPart field_20818;
	private final ModelPart field_20819;
	private final ModelPart field_20820;
	private final ModelPart field_20821;
	private final ModelPart field_20822;
	private final ModelPart field_21479;
	private final ModelPart field_21480;
	private final ModelPart field_21481;
	private boolean isChristmas;

	public ChestBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}

		this.field_20818 = new ModelPart(64, 64, 0, 19);
		this.field_20818.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.field_20817 = new ModelPart(64, 64, 0, 0);
		this.field_20817.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.field_20817.pivotY = 9.0F;
		this.field_20817.pivotZ = 1.0F;
		this.field_20819 = new ModelPart(64, 64, 0, 0);
		this.field_20819.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_20819.pivotY = 8.0F;
		this.field_20821 = new ModelPart(64, 64, 0, 19);
		this.field_20821.addCuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_20820 = new ModelPart(64, 64, 0, 0);
		this.field_20820.addCuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_20820.pivotY = 9.0F;
		this.field_20820.pivotZ = 1.0F;
		this.field_20822 = new ModelPart(64, 64, 0, 0);
		this.field_20822.addCuboid(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_20822.pivotY = 8.0F;
		this.field_21480 = new ModelPart(64, 64, 0, 19);
		this.field_21480.addCuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_21479 = new ModelPart(64, 64, 0, 0);
		this.field_21479.addCuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_21479.pivotY = 9.0F;
		this.field_21479.pivotZ = 1.0F;
		this.field_21481 = new ModelPart(64, 64, 0, 0);
		this.field_21481.addCuboid(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_21481.pivotY = 8.0F;
	}

	@Override
	public void render(T blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		World world = blockEntity.getWorld();
		boolean bl = world != null;
		BlockState blockState = bl ? blockEntity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		Block block = blockState.getBlock();
		if (block instanceof class_4739) {
			class_4739<?> lv = (class_4739<?>)block;
			boolean bl2 = chestType != ChestType.SINGLE;
			matrixStack.push();
			float g = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-g));
			matrixStack.translate(-0.5, -0.5, -0.5);
			DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> propertySource;
			if (bl) {
				propertySource = lv.method_24167(blockState, world, blockEntity.getPos(), true);
			} else {
				propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
			}

			float h = propertySource.apply(ChestBlock.method_24166(blockEntity)).get(f);
			h = 1.0F - h;
			h = 1.0F - h * h * h;
			int k = propertySource.apply(new LightmapCoordinatesRetriever<>()).applyAsInt(i);
			SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTexture(blockEntity, chestType, this.isChristmas);
			VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutout);
			if (bl2) {
				if (chestType == ChestType.LEFT) {
					this.method_22749(matrixStack, vertexConsumer, this.field_21479, this.field_21481, this.field_21480, h, k, j);
				} else {
					this.method_22749(matrixStack, vertexConsumer, this.field_20820, this.field_20822, this.field_20821, h, k, j);
				}
			} else {
				this.method_22749(matrixStack, vertexConsumer, this.field_20817, this.field_20819, this.field_20818, h, k, j);
			}

			matrixStack.pop();
		}
	}

	private void method_22749(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, float f, int i, int j
	) {
		modelPart.pitch = -(f * (float) (Math.PI / 2));
		modelPart2.pitch = modelPart.pitch;
		modelPart.render(matrixStack, vertexConsumer, i, j);
		modelPart2.render(matrixStack, vertexConsumer, i, j);
		modelPart3.render(matrixStack, vertexConsumer, i, j);
	}
}
