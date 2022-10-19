/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class BannerItem
extends VerticallyAttachableBlockItem {
    private static final String TRANSLATION_KEY_PREFIX = "block.minecraft.banner.";

    public BannerItem(Block bannerBlock, Block wallBannerBlock, Item.Settings settings) {
        super(bannerBlock, wallBannerBlock, settings, Direction.DOWN);
        Validate.isInstanceOf(AbstractBannerBlock.class, bannerBlock);
        Validate.isInstanceOf(AbstractBannerBlock.class, wallBannerBlock);
    }

    public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains("Patterns")) {
            return;
        }
        NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size() && i < 6; ++i) {
            NbtCompound nbtCompound2 = nbtList.getCompound(i);
            DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
            RegistryEntry<BannerPattern> registryEntry = BannerPattern.byId(nbtCompound2.getString("Pattern"));
            if (registryEntry == null) continue;
            registryEntry.getKey().map(key -> key.getValue().toShortTranslationKey()).ifPresent(translationKey -> tooltip.add(Text.translatable(TRANSLATION_KEY_PREFIX + translationKey + "." + dyeColor.getName()).formatted(Formatting.GRAY)));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}

