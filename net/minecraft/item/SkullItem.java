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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

public class SkullItem
extends WallStandingBlockItem {
    public static final String SKULL_OWNER_KEY = "SkullOwner";

    public SkullItem(Block block, Block block2, Item.Settings settings) {
        super(block, block2, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.isOf(Items.PLAYER_HEAD) && stack.hasTag()) {
            NbtCompound nbtCompound2;
            String string = null;
            NbtCompound nbtCompound = stack.getTag();
            if (nbtCompound.contains(SKULL_OWNER_KEY, 8)) {
                string = nbtCompound.getString(SKULL_OWNER_KEY);
            } else if (nbtCompound.contains(SKULL_OWNER_KEY, 10) && (nbtCompound2 = nbtCompound.getCompound(SKULL_OWNER_KEY)).contains("Name", 8)) {
                string = nbtCompound2.getString("Name");
            }
            if (string != null) {
                return new TranslatableText(this.getTranslationKey() + ".named", string);
            }
        }
        return super.getName(stack);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);
        if (nbt.contains(SKULL_OWNER_KEY, 8) && !StringUtils.isBlank(nbt.getString(SKULL_OWNER_KEY))) {
            GameProfile gameProfile2 = new GameProfile(null, nbt.getString(SKULL_OWNER_KEY));
            SkullBlockEntity.loadProperties(gameProfile2, gameProfile -> nbt.put(SKULL_OWNER_KEY, NbtHelper.writeGameProfile(new NbtCompound(), gameProfile)));
        }
    }
}

