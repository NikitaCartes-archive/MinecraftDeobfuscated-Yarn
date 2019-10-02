package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends BlockEntityRenderer<T> {
	public static final Identifier TRAPPED_DOUBLE_TEX = new Identifier("entity/chest/trapped_double");
	public static final Identifier CHRISTMAS_DOUBLE_TEX = new Identifier("entity/chest/christmas_double");
	public static final Identifier NORMAL_DOUBLE_TEX = new Identifier("entity/chest/normal_double");
	public static final Identifier TRAPPED_TEX = new Identifier("entity/chest/trapped");
	public static final Identifier CHRISTMAS_TEX = new Identifier("entity/chest/christmas");
	public static final Identifier NORMAL_TEX = new Identifier("entity/chest/normal");
	public static final Identifier ENDER_TEX = new Identifier("entity/chest/ender");
	private final ModelPart field_20817;
	private final ModelPart field_20818;
	private final ModelPart field_20819;
	private final ModelPart field_20820;
	private final ModelPart field_20821;
	private final ModelPart field_20822;
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
		this.field_20819.addCuboid(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_20819.pivotY = 9.0F;
		this.field_20821 = new ModelPart(128, 64, 0, 19);
		this.field_20821.addCuboid(1.0F, 0.0F, 1.0F, 30.0F, 10.0F, 14.0F, 0.0F);
		this.field_20820 = new ModelPart(128, 64, 0, 0);
		this.field_20820.addCuboid(1.0F, 0.0F, 0.0F, 30.0F, 5.0F, 14.0F, 0.0F);
		this.field_20820.pivotY = 9.0F;
		this.field_20820.pivotZ = 1.0F;
		this.field_20822 = new ModelPart(128, 64, 0, 0);
		this.field_20822.addCuboid(15.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_20822.pivotY = 9.0F;
	}

	@Override
	public void render(
		T blockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i
	) {
		BlockState blockState = blockEntity.hasWorld() ? blockEntity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		boolean bl = chestType != ChestType.SINGLE;
		Identifier identifier;
		if (this.isChristmas) {
			identifier = bl ? CHRISTMAS_DOUBLE_TEX : CHRISTMAS_TEX;
		} else if (blockEntity instanceof TrappedChestBlockEntity) {
			identifier = bl ? TRAPPED_DOUBLE_TEX : TRAPPED_TEX;
		} else if (blockEntity instanceof EnderChestBlockEntity) {
			identifier = ENDER_TEX;
		} else {
			identifier = bl ? NORMAL_DOUBLE_TEX : NORMAL_TEX;
		}

		matrixStack.push();
		float h = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-h, true));
		matrixStack.translate(-0.5, -0.5, -0.5);
		float j = blockEntity.getAnimationProgress(g);
		j = 1.0F - j;
		j = 1.0F - j * j * j;
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.SOLID);
		Sprite sprite = this.getSprite(identifier);
		if (bl) {
			if (chestType == ChestType.LEFT) {
				matrixStack.translate(-1.0, 0.0, 0.0);
			}

			this.method_22749(matrixStack, vertexConsumer, this.field_20820, this.field_20822, this.field_20821, j, i, sprite);
		} else {
			this.method_22749(matrixStack, vertexConsumer, this.field_20817, this.field_20819, this.field_20818, j, i, sprite);
		}

		matrixStack.pop();
	}

	private void method_22749(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, float f, int i, Sprite sprite
	) {
		modelPart.pitch = -(f * (float) (Math.PI / 2));
		modelPart2.pitch = modelPart.pitch;
		modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		modelPart2.render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		modelPart3.render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
	}
}
