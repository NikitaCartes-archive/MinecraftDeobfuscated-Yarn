package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	private static final Random RANDOM = new Random();
	private Text customName;

	public EnchantingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.ENCHANTING_TABLE, pos, state);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		if (this.hasCustomName()) {
			tag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		return tag;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		if (tag.contains("CustomName", NbtTypeIds.STRING)) {
			this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
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
		return (Text)(this.customName != null ? this.customName : new TranslatableText("container.enchant"));
	}

	public void setCustomName(@Nullable Text value) {
		this.customName = value;
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}
}
