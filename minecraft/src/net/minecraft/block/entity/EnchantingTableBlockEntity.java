package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
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

	public EnchantingTableBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.ENCHANTING_TABLE, blockPos, blockState);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if (this.hasCustomName()) {
			tag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.contains("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
		}
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, EnchantingTableBlockEntity enchantingTableBlockEntity) {
		enchantingTableBlockEntity.pageTurningSpeed = enchantingTableBlockEntity.nextPageTurningSpeed;
		enchantingTableBlockEntity.field_11963 = enchantingTableBlockEntity.field_11964;
		PlayerEntity playerEntity = world.getClosestPlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.getX() - ((double)blockPos.getX() + 0.5);
			double e = playerEntity.getZ() - ((double)blockPos.getZ() + 0.5);
			enchantingTableBlockEntity.field_11962 = (float)MathHelper.atan2(e, d);
			enchantingTableBlockEntity.nextPageTurningSpeed += 0.1F;
			if (enchantingTableBlockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = enchantingTableBlockEntity.field_11969;

				do {
					enchantingTableBlockEntity.field_11969 = enchantingTableBlockEntity.field_11969 + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == enchantingTableBlockEntity.field_11969);
			}
		} else {
			enchantingTableBlockEntity.field_11962 += 0.02F;
			enchantingTableBlockEntity.nextPageTurningSpeed -= 0.1F;
		}

		while (enchantingTableBlockEntity.field_11964 >= (float) Math.PI) {
			enchantingTableBlockEntity.field_11964 -= (float) (Math.PI * 2);
		}

		while (enchantingTableBlockEntity.field_11964 < (float) -Math.PI) {
			enchantingTableBlockEntity.field_11964 += (float) (Math.PI * 2);
		}

		while (enchantingTableBlockEntity.field_11962 >= (float) Math.PI) {
			enchantingTableBlockEntity.field_11962 -= (float) (Math.PI * 2);
		}

		while (enchantingTableBlockEntity.field_11962 < (float) -Math.PI) {
			enchantingTableBlockEntity.field_11962 += (float) (Math.PI * 2);
		}

		float g = enchantingTableBlockEntity.field_11962 - enchantingTableBlockEntity.field_11964;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		enchantingTableBlockEntity.field_11964 += g * 0.4F;
		enchantingTableBlockEntity.nextPageTurningSpeed = MathHelper.clamp(enchantingTableBlockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
		enchantingTableBlockEntity.ticks++;
		enchantingTableBlockEntity.pageAngle = enchantingTableBlockEntity.nextPageAngle;
		float h = (enchantingTableBlockEntity.field_11969 - enchantingTableBlockEntity.nextPageAngle) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		enchantingTableBlockEntity.field_11967 = enchantingTableBlockEntity.field_11967 + (h - enchantingTableBlockEntity.field_11967) * 0.9F;
		enchantingTableBlockEntity.nextPageAngle = enchantingTableBlockEntity.nextPageAngle + enchantingTableBlockEntity.field_11967;
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
