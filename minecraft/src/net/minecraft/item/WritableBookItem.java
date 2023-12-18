package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
	public WritableBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.useBook(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean isValid(@Nullable NbtCompound nbt) {
		if (nbt == null) {
			return false;
		} else if (!nbt.contains("pages", NbtElement.LIST_TYPE)) {
			return false;
		} else {
			NbtList nbtList = nbt.getList("pages", NbtElement.STRING_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				String string = nbtList.getString(i);
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
