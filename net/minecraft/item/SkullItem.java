/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

public class SkullItem
extends WallStandingBlockItem {
    public SkullItem(Block block, Block block2, Item.Settings settings) {
        super(block, block2, settings);
    }

    @Override
    public Component getTranslatedNameTrimmed(ItemStack itemStack) {
        if (itemStack.getItem() == Items.PLAYER_HEAD && itemStack.hasTag()) {
            CompoundTag compoundTag2;
            String string = null;
            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag.containsKey("SkullOwner", 8)) {
                string = compoundTag.getString("SkullOwner");
            } else if (compoundTag.containsKey("SkullOwner", 10) && (compoundTag2 = compoundTag.getCompound("SkullOwner")).containsKey("Name", 8)) {
                string = compoundTag2.getString("Name");
            }
            if (string != null) {
                return new TranslatableComponent(this.getTranslationKey() + ".named", string);
            }
        }
        return super.getTranslatedNameTrimmed(itemStack);
    }

    @Override
    public boolean onTagDeserialized(CompoundTag compoundTag) {
        super.onTagDeserialized(compoundTag);
        if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
            GameProfile gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
            gameProfile = SkullBlockEntity.loadProperties(gameProfile);
            compoundTag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
            return true;
        }
        return false;
    }
}

