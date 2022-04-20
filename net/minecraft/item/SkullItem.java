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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class SkullItem
extends WallStandingBlockItem {
    public static final String SKULL_OWNER_KEY = "SkullOwner";

    public SkullItem(Block block, Block block2, Item.Settings settings) {
        super(block, block2, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.isOf(Items.PLAYER_HEAD) && stack.hasNbt()) {
            NbtCompound nbtCompound2;
            String string = null;
            NbtCompound nbtCompound = stack.getNbt();
            if (nbtCompound.contains(SKULL_OWNER_KEY, NbtElement.STRING_TYPE)) {
                string = nbtCompound.getString(SKULL_OWNER_KEY);
            } else if (nbtCompound.contains(SKULL_OWNER_KEY, NbtElement.COMPOUND_TYPE) && (nbtCompound2 = nbtCompound.getCompound(SKULL_OWNER_KEY)).contains("Name", NbtElement.STRING_TYPE)) {
                string = nbtCompound2.getString("Name");
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
        if (nbt.contains(SKULL_OWNER_KEY, NbtElement.STRING_TYPE) && !StringUtils.isBlank(nbt.getString(SKULL_OWNER_KEY))) {
            GameProfile gameProfile = new GameProfile(null, nbt.getString(SKULL_OWNER_KEY));
            SkullBlockEntity.loadProperties(gameProfile, profile -> nbt.put(SKULL_OWNER_KEY, NbtHelper.writeGameProfile(new NbtCompound(), profile)));
        }
    }
}

