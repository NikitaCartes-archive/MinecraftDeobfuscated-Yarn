package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class PlayerHeadItem extends VerticallyAttachableBlockItem {
	public static final String SKULL_OWNER_KEY = "SkullOwner";

	public PlayerHeadItem(Block block, Block wallBlock, Item.Settings settings) {
		super(block, wallBlock, settings, Direction.DOWN);
	}

	@Override
	public Text getName(ItemStack stack) {
		if (stack.isOf(Items.PLAYER_HEAD) && stack.hasNbt()) {
			String string = null;
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound.contains("SkullOwner", NbtElement.STRING_TYPE)) {
				string = nbtCompound.getString("SkullOwner");
			} else if (nbtCompound.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("SkullOwner");
				if (nbtCompound2.contains("Name", NbtElement.STRING_TYPE)) {
					string = nbtCompound2.getString("Name");
				}
			}

			if (string != null) {
				return Text.translatable(this.getTranslationKey() + ".named", string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void postProcessNbt(NbtCompound nbt) {
		super.postProcessNbt(nbt);
		SkullBlockEntity.fillSkullOwner(nbt);
	}
}
