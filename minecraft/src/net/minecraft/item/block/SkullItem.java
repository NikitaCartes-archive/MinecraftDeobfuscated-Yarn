package net.minecraft.item.block;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallStandingBlockItem {
	public SkullItem(Block block, Block block2, Item.Settings settings) {
		super(block, block2, settings);
	}

	@Override
	public TextComponent method_7864(ItemStack itemStack) {
		if (itemStack.getItem() == Items.PLAYER_HEAD && itemStack.hasTag()) {
			String string = null;
			CompoundTag compoundTag = itemStack.method_7969();
			if (compoundTag.containsKey("SkullOwner", 8)) {
				string = compoundTag.getString("SkullOwner");
			} else if (compoundTag.containsKey("SkullOwner", 10)) {
				CompoundTag compoundTag2 = compoundTag.getCompound("SkullOwner");
				if (compoundTag2.containsKey("Name", 8)) {
					string = compoundTag2.getString("Name");
				}
			}

			if (string != null) {
				return new TranslatableTextComponent(this.getTranslationKey() + ".named", string);
			}
		}

		return super.method_7864(itemStack);
	}

	@Override
	public boolean method_7860(CompoundTag compoundTag) {
		super.method_7860(compoundTag);
		if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			compoundTag.method_10566("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
