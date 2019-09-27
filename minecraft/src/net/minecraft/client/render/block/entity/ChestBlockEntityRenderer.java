package net.minecraft.client.render.block.entity;

import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

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
	public void render(T blockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
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

		arg.method_22903();
		float h = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
		arg.method_22904(0.5, 0.5, 0.5);
		arg.method_22907(Vector3f.field_20705.method_23214(-h, true));
		arg.method_22904(-0.5, -0.5, -0.5);
		float j = blockEntity.getAnimationProgress(g);
		j = 1.0F - j;
		j = 1.0F - j * j * j;
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
		Sprite sprite = this.method_23082(identifier);
		if (bl) {
			if (chestType == ChestType.LEFT) {
				arg.method_22904(-1.0, 0.0, 0.0);
			}

			this.method_22749(arg, lv, this.field_20820, this.field_20822, this.field_20821, j, i, sprite);
		} else {
			this.method_22749(arg, lv, this.field_20817, this.field_20819, this.field_20818, j, i, sprite);
		}

		arg.method_22909();
	}

	private void method_22749(class_4587 arg, class_4588 arg2, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, float f, int i, Sprite sprite) {
		modelPart.pitch = -(f * (float) (Math.PI / 2));
		modelPart2.pitch = modelPart.pitch;
		modelPart.method_22698(arg, arg2, 0.0625F, i, sprite);
		modelPart2.method_22698(arg, arg2, 0.0625F, i, sprite);
		modelPart3.method_22698(arg, arg2, 0.0625F, i, sprite);
	}
}
