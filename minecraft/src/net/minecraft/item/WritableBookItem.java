package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
	public WritableBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		playerEntity.openBookEditor(itemStack, hand);
		playerEntity.incrementStat(Stats.field_15372.method_14956(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	public static boolean method_8047(@Nullable CompoundTag compoundTag) {
		if (compoundTag == null) {
			return false;
		} else if (!compoundTag.containsKey("pages", 9)) {
			return false;
		} else {
			ListTag listTag = compoundTag.getList("pages", 8);

			for (int i = 0; i < listTag.size(); i++) {
				String string = listTag.getString(i);
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
