/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BannerPatternTags {
    public static final TagKey<BannerPattern> NO_ITEM_REQUIRED = BannerPatternTags.of("no_item_required");
    public static final TagKey<BannerPattern> FLOWER_PATTERN_ITEM = BannerPatternTags.of("pattern_item/flower");
    public static final TagKey<BannerPattern> CREEPER_PATTERN_ITEM = BannerPatternTags.of("pattern_item/creeper");
    public static final TagKey<BannerPattern> SKULL_PATTERN_ITEM = BannerPatternTags.of("pattern_item/skull");
    public static final TagKey<BannerPattern> MOJANG_PATTERN_ITEM = BannerPatternTags.of("pattern_item/mojang");
    public static final TagKey<BannerPattern> GLOBE_PATTERN_ITEM = BannerPatternTags.of("pattern_item/globe");
    public static final TagKey<BannerPattern> PIGLIN_PATTERN_ITEM = BannerPatternTags.of("pattern_item/piglin");

    private BannerPatternTags() {
    }

    private static TagKey<BannerPattern> of(String id) {
        return TagKey.of(RegistryKeys.BANNER_PATTERN, new Identifier(id));
    }
}

