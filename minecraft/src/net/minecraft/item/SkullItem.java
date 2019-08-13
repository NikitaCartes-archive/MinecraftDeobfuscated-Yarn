package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallStandingBlockItem {
	public SkullItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
	}

	@Override
	public Text getName(ItemStack itemStack) {
		if (itemStack.getItem() == Items.PLAYER_HEAD && itemStack.hasTag()) {
			String string = null;
			CompoundTag compoundTag = itemStack.getTag();
			if (compoundTag.containsKey("SkullOwner", 8)) {
				string = compoundTag.getString("SkullOwner");
			} else if (compoundTag.containsKey("SkullOwner", 10)) {
				CompoundTag compoundTag2 = compoundTag.getCompound("SkullOwner");
				if (compoundTag2.containsKey("Name", 8)) {
					string = compoundTag2.getString("Name");
				}
			}

			if (string != null) {
				return new TranslatableText(this.getTranslationKey() + ".named", string);
			}
		}

		return super.getName(itemStack);
	}

	@Override
	public boolean postProcessTag(CompoundTag compoundTag) {
		super.postProcessTag(compoundTag);
		if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			compoundTag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
