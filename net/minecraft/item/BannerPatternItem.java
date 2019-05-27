/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BannerPatternItem
extends Item {
    private final BannerPattern pattern;

    public BannerPatternItem(BannerPattern bannerPattern, Item.Settings settings) {
        super(settings);
        this.pattern = bannerPattern;
    }

    public BannerPattern getPattern() {
        return this.pattern;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        list.add(this.getDescription().applyFormat(ChatFormat.GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public Component getDescription() {
        return new TranslatableComponent(this.getTranslationKey() + ".desc", new Object[0]);
    }
}

