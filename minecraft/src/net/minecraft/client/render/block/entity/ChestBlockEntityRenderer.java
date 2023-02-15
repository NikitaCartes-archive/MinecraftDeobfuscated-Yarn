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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {
	private static final String BASE = "bottom";
	private static final String LID = "lid";
	private static final String LATCH = "lock";
	private final ModelPart singleChestLid;
	private final ModelPart singleChestBase;
	private final ModelPart singleChestLatch;
	private final ModelPart doubleChestLeftLid;
	private final ModelPart doubleChestLeftBase;
	private final ModelPart doubleChestLeftLatch;
	private final ModelPart doubleChestRightLid;
	private final ModelPart doubleChestRightBase;
	private final ModelPart doubleChestRightLatch;
	private boolean christmas;

	public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.christmas = true;
		}

		ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.CHEST);
		this.singleChestBase = modelPart.getChild("bottom");
		this.singleChestLid = modelPart.getChild("lid");
		this.singleChestLatch = modelPart.getChild("lock");
		ModelPart modelPart2 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleChestLeftBase = modelPart2.getChild("bottom");
		this.doubleChestLeftLid = modelPart2.getChild("lid");
		this.doubleChestLeftLatch = modelPart2.getChild("lock");
		ModelPart modelPart3 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleChestRightBase = modelPart3.getChild("bottom");
		this.doubleChestRightLid = modelPart3.getChild("lid");
		this.doubleChestRightLatch = modelPart3.getChild("lock");
	}

	public static TexturedModelData getSingleTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getRightDoubleTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getLeftDoubleTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = entity.getWorld();
		boolean bl = world != null;
		BlockState blockState = bl ? entity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		if (blockState.getBlock() instanceof AbstractChestBlock<?> abstractChestBlock) {
			boolean bl2 = chestType != ChestType.SINGLE;
			matrices.push();
			float f = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
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
					this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
				} else {
					this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
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
