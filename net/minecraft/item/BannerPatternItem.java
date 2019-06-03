/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
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
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        list.add(this.getDescription().formatted(Formatting.GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDescription() {
        return new TranslatableText(this.getTranslationKey() + ".desc", new Object[0]);
    }
}

