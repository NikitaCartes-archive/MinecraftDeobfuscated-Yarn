package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallStandingBlockItem {
	public static final String SKULL_OWNER_KEY = "SkullOwner";

	public SkullItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
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
		if (nbt.contains("SkullOwner", NbtElement.STRING_TYPE) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, nbt.getString("SkullOwner"));
			SkullBlockEntity.loadProperties(gameProfile, profile -> nbt.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile)));
		}
	}
}
