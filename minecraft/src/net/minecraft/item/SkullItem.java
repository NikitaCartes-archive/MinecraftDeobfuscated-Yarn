package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallStandingBlockItem {
	public SkullItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		if (stack.isOf(Items.PLAYER_HEAD) && stack.hasTag()) {
			String string = null;
			NbtCompound nbtCompound = stack.getTag();
			if (nbtCompound.contains("SkullOwner", NbtTypeIds.STRING)) {
				string = nbtCompound.getString("SkullOwner");
			} else if (nbtCompound.contains("SkullOwner", NbtTypeIds.COMPOUND)) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("SkullOwner");
				if (nbtCompound2.contains("Name", NbtTypeIds.STRING)) {
					string = nbtCompound2.getString("Name");
				}
			}

			if (string != null) {
				return new TranslatableText(this.getTranslationKey() + ".named", string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public boolean postProcessTag(NbtCompound tag) {
		super.postProcessTag(tag);
		if (tag.contains("SkullOwner", NbtTypeIds.STRING) && !StringUtils.isBlank(tag.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, tag.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			tag.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
