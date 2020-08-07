package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.CompoundTag;
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
		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
			String string = null;
			CompoundTag compoundTag = stack.getTag();
			if (compoundTag.contains("SkullOwner", 8)) {
				string = compoundTag.getString("SkullOwner");
			} else if (compoundTag.contains("SkullOwner", 10)) {
				CompoundTag compoundTag2 = compoundTag.getCompound("SkullOwner");
				if (compoundTag2.contains("Name", 8)) {
					string = compoundTag2.getString("Name");
				}
			}

			if (string != null) {
				return new TranslatableText(this.getTranslationKey() + ".named", string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public boolean postProcessTag(CompoundTag tag) {
		super.postProcessTag(tag);
		if (tag.contains("SkullOwner", 8) && !StringUtils.isBlank(tag.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, tag.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			tag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
