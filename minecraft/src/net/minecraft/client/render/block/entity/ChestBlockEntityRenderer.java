package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends class_4576<T> {
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

	public ChestBlockEntityRenderer() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}

		this.field_20818 = new ModelPart(64, 64, 0, 19);
		this.field_20818.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.field_20817 = new ModelPart(64, 64, 0, 0);
		this.field_20817.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.field_20817.rotationPointY = 9.0F;
		this.field_20817.rotationPointZ = 1.0F;
		this.field_20819 = new ModelPart(64, 64, 0, 0);
		this.field_20819.addCuboid(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_20819.rotationPointY = 9.0F;
		this.field_20821 = new ModelPart(128, 64, 0, 19);
		this.field_20821.addCuboid(1.0F, 0.0F, 1.0F, 30.0F, 10.0F, 14.0F, 0.0F);
		this.field_20820 = new ModelPart(128, 64, 0, 0);
		this.field_20820.addCuboid(1.0F, 0.0F, 0.0F, 30.0F, 5.0F, 14.0F, 0.0F);
		this.field_20820.rotationPointY = 9.0F;
		this.field_20820.rotationPointZ = 1.0F;
		this.field_20822 = new ModelPart(128, 64, 0, 0);
		this.field_20822.addCuboid(15.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_20822.rotationPointY = 9.0F;
	}

	@Override
	protected void method_22738(
		T blockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k
	) {
		BlockState blockState = blockEntity.hasWorld() ? blockEntity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		if (chestType != ChestType.LEFT) {
			boolean bl = chestType != ChestType.SINGLE;
			Identifier identifier;
			if (i >= 0) {
				identifier = (Identifier)ModelLoader.field_20848.get(i);
			} else if (this.isChristmas) {
				identifier = bl ? CHRISTMAS_DOUBLE_TEX : CHRISTMAS_TEX;
			} else if (blockEntity instanceof TrappedChestBlockEntity) {
				identifier = bl ? TRAPPED_DOUBLE_TEX : TRAPPED_TEX;
			} else if (blockEntity instanceof EnderChestBlockEntity) {
				identifier = ENDER_TEX;
			} else {
				identifier = bl ? NORMAL_DOUBLE_TEX : NORMAL_TEX;
			}

			bufferBuilder.method_22629();
			float h = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
			bufferBuilder.method_22626(0.5, 0.5, 0.5);
			bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -h, true));
			bufferBuilder.method_22626(-0.5, -0.5, -0.5);
			float l = blockEntity.getAnimationProgress(g);
			l = 1.0F - l;
			l = 1.0F - l * l * l;
			Sprite sprite = this.method_22739(identifier);
			if (bl) {
				this.method_22749(bufferBuilder, this.field_20820, this.field_20822, this.field_20821, l, j, k, sprite);
			} else {
				this.method_22749(bufferBuilder, this.field_20817, this.field_20819, this.field_20818, l, j, k, sprite);
			}

			bufferBuilder.method_22630();
		}
	}

	private void method_22749(BufferBuilder bufferBuilder, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, float f, int i, int j, Sprite sprite) {
		modelPart.pitch = -(f * (float) (Math.PI / 2));
		modelPart2.pitch = modelPart.pitch;
		modelPart.method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		modelPart2.method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		modelPart3.method_22698(bufferBuilder, 0.0625F, i, j, sprite);
	}
}
