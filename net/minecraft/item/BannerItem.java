/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class BannerItem
extends WallStandingBlockItem {
    public BannerItem(Block block, Block block2, Item.Settings settings) {
        super(block, block2, settings);
        Validate.isInstanceOf(AbstractBannerBlock.class, block);
        Validate.isInstanceOf(AbstractBannerBlock.class, block2);
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag == null || !compoundTag.contains("Patterns")) {
            return;
        }
        ListTag listTag = compoundTag.getList("Patterns", 10);
        for (int i = 0; i < listTag.size() && i < 6; ++i) {
            CompoundTag compoundTag2 = listTag.getCompound(i);
            DyeColor dyeColor = DyeColor.byId(compoundTag2.getInt("Color"));
            BannerPattern bannerPattern = BannerPattern.byId(compoundTag2.getString("Pattern"));
            if (bannerPattern == null) continue;
            tooltip.add(new TranslatableText("block.minecraft.banner." + bannerPattern.getName() + '.' + dyeColor.getName(), new Object[0]).formatted(Formatting.GRAY));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}

