package net.minecraft.client.render.block.entity;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity> {
	public static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new Identifier("entity/bed/" + dyeColor.getName()))
		.toArray(Identifier[]::new);
	private final ModelPart field_20813;
	private final ModelPart field_20814;
	private final ModelPart[] field_20815 = new ModelPart[4];

	public BedBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20813 = new ModelPart(64, 64, 0, 0);
		this.field_20813.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20814 = new ModelPart(64, 64, 0, 22);
		this.field_20814.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20815[0] = new ModelPart(64, 64, 50, 0);
		this.field_20815[1] = new ModelPart(64, 64, 50, 6);
		this.field_20815[2] = new ModelPart(64, 64, 50, 12);
		this.field_20815[3] = new ModelPart(64, 64, 50, 18);
		this.field_20815[0].addCuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[1].addCuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[2].addCuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[3].addCuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[0].pitch = (float) (Math.PI / 2);
		this.field_20815[1].pitch = (float) (Math.PI / 2);
		this.field_20815[2].pitch = (float) (Math.PI / 2);
		this.field_20815[3].pitch = (float) (Math.PI / 2);
		this.field_20815[0].roll = 0.0F;
		this.field_20815[1].roll = (float) (Math.PI / 2);
		this.field_20815[2].roll = (float) (Math.PI * 3.0 / 2.0);
		this.field_20815[3].roll = (float) Math.PI;
	}

	public void method_3557(BedBlockEntity bedBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		Identifier identifier = TEXTURES[bedBlockEntity.getColor().getId()];
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.getCachedState();
			this.method_3558(arg, lv, blockState.get(BedBlock.PART) == BedPart.HEAD, blockState.get(BedBlock.FACING), identifier, i, false);
		} else {
			this.method_3558(arg, lv, true, Direction.SOUTH, identifier, i, false);
			this.method_3558(arg, lv, false, Direction.SOUTH, identifier, i, true);
		}
	}

	private void method_3558(class_4587 arg, class_4588 arg2, boolean bl, Direction direction, Identifier identifier, int i, boolean bl2) {
		this.field_20813.visible = bl;
		this.field_20814.visible = !bl;
		this.field_20815[0].visible = !bl;
		this.field_20815[1].visible = bl;
		this.field_20815[2].visible = !bl;
		this.field_20815[3].visible = bl;
		arg.method_22903();
		arg.method_22904(0.0, 0.5625, bl2 ? -1.0 : 0.0);
		arg.method_22907(Vector3f.field_20703.method_23214(90.0F, true));
		arg.method_22904(0.5, 0.5, 0.5);
		arg.method_22907(Vector3f.field_20707.method_23214(180.0F + direction.asRotation(), true));
		arg.method_22904(-0.5, -0.5, -0.5);
		Sprite sprite = this.method_23082(identifier);
		this.field_20813.method_22698(arg, arg2, 0.0625F, i, sprite);
		this.field_20814.method_22698(arg, arg2, 0.0625F, i, sprite);
		this.field_20815[0].method_22698(arg, arg2, 0.0625F, i, sprite);
		this.field_20815[1].method_22698(arg, arg2, 0.0625F, i, sprite);
		this.field_20815[2].method_22698(arg, arg2, 0.0625F, i, sprite);
		this.field_20815[3].method_22698(arg, arg2, 0.0625F, i, sprite);
		arg.method_22909();
	}
}
