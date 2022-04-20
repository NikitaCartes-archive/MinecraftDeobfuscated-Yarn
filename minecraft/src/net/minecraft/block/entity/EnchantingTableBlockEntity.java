package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.World;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable {
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float field_11969;
	public float field_11967;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float field_11964;
	public float field_11963;
	public float field_11962;
	private static final AbstractRandom RANDOM = AbstractRandom.createAtomic();
	private Text customName;

	public EnchantingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.ENCHANTING_TABLE, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, EnchantingTableBlockEntity blockEntity) {
		blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
		blockEntity.field_11963 = blockEntity.field_11964;
		PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
			double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
			blockEntity.field_11962 = (float)MathHelper.atan2(e, d);
			blockEntity.nextPageTurningSpeed += 0.1F;
			if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = blockEntity.field_11969;

				do {
					blockEntity.field_11969 = blockEntity.field_11969 + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == blockEntity.field_11969);
			}
		} else {
			blockEntity.field_11962 += 0.02F;
			blockEntity.nextPageTurningSpeed -= 0.1F;
		}

		while (blockEntity.field_11964 >= (float) Math.PI) {
			blockEntity.field_11964 -= (float) (Math.PI * 2);
		}

		while (blockEntity.field_11964 < (float) -Math.PI) {
			blockEntity.field_11964 += (float) (Math.PI * 2);
		}

		while (blockEntity.field_11962 >= (float) Math.PI) {
			blockEntity.field_11962 -= (float) (Math.PI * 2);
		}

		while (blockEntity.field_11962 < (float) -Math.PI) {
			blockEntity.field_11962 += (float) (Math.PI * 2);
		}

		float g = blockEntity.field_11962 - blockEntity.field_11964;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		blockEntity.field_11964 += g * 0.4F;
		blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
		blockEntity.ticks++;
		blockEntity.pageAngle = blockEntity.nextPageAngle;
		float h = (blockEntity.field_11969 - blockEntity.nextPageAngle) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		blockEntity.field_11967 = blockEntity.field_11967 + (h - blockEntity.field_11967) * 0.9F;
		blockEntity.nextPageAngle = blockEntity.nextPageAngle + blockEntity.field_11967;
	}

	@Override
	public Text getName() {
		return (Text)(this.customName != null ? this.customName : Text.translatable("container.enchant"));
	}

	public void setCustomName(@Nullable Text customName) {
		this.customName = customName;
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}
}
