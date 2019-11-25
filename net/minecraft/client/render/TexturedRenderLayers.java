/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

@Environment(value=EnvType.CLIENT)
public class TexturedRenderLayers {
    public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = new Identifier("textures/atlas/shulker_boxes.png");
    public static final Identifier BEDS_ATLAS_TEXTURE = new Identifier("textures/atlas/beds.png");
    public static final Identifier BANNER_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/banner_patterns.png");
    public static final Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/shield_patterns.png");
    public static final Identifier SIGNS_ATLAS_TEXTURE = new Identifier("textures/atlas/signs.png");
    public static final Identifier CHEST_ATLAS_TEXTURE = new Identifier("textures/atlas/chest.png");
    private static final RenderLayer SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
    private static final RenderLayer BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
    private static final RenderLayer BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
    private static final RenderLayer SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
    private static final RenderLayer SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
    private static final RenderLayer CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
    private static final RenderLayer ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    private static final RenderLayer ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    private static final RenderLayer ENTITY_TRANSLUCENT = RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    private static final RenderLayer ENTITY_TRANSLUCENT_CULL = RenderLayer.getEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    public static final SpriteIdentifier SHULKER_TEXTURE_ID = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker"));
    public static final List<SpriteIdentifier> COLORED_SHULKER_BOXES_TEXTURES = Stream.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").map(string -> new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker_" + string))).collect(ImmutableList.toImmutableList());
    public static final Map<SignType, SpriteIdentifier> WOOD_TYPE_TEXTURES = SignType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::getSignTextureId));
    public static final SpriteIdentifier[] BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(dyeColor -> new SpriteIdentifier(BEDS_ATLAS_TEXTURE, new Identifier("entity/bed/" + dyeColor.getName()))).toArray(SpriteIdentifier[]::new);
    public static final SpriteIdentifier TRAPPED = TexturedRenderLayers.getChestTextureId("trapped");
    public static final SpriteIdentifier TRAPPED_LEFT = TexturedRenderLayers.getChestTextureId("trapped_left");
    public static final SpriteIdentifier TRAPPED_RIGHT = TexturedRenderLayers.getChestTextureId("trapped_right");
    public static final SpriteIdentifier CHRISTMAS = TexturedRenderLayers.getChestTextureId("christmas");
    public static final SpriteIdentifier CHRISTMAS_LEFT = TexturedRenderLayers.getChestTextureId("christmas_left");
    public static final SpriteIdentifier CHRISTMAS_RIGHT = TexturedRenderLayers.getChestTextureId("christmas_right");
    public static final SpriteIdentifier NORMAL = TexturedRenderLayers.getChestTextureId("normal");
    public static final SpriteIdentifier NORMAL_LEFT = TexturedRenderLayers.getChestTextureId("normal_left");
    public static final SpriteIdentifier NORMAL_RIGHT = TexturedRenderLayers.getChestTextureId("normal_right");
    public static final SpriteIdentifier ENDER = TexturedRenderLayers.getChestTextureId("ender");

    public static RenderLayer getBannerPatterns() {
        return BANNER_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getShieldPatterns() {
        return SHIELD_PATTERNS_RENDER_LAYER;
    }

    public static RenderLayer getBeds() {
        return BEDS_RENDER_LAYER;
    }

    public static RenderLayer getShulkerBoxes() {
        return SHULKER_BOXES_RENDER_LAYER;
    }

    public static RenderLayer getSign() {
        return SIGN_RENDER_LAYER;
    }

    public static RenderLayer getChest() {
        return CHEST_RENDER_LAYER;
    }

    public static RenderLayer getEntitySolid() {
        return ENTITY_SOLID;
    }

    public static RenderLayer getEntityCutout() {
        return ENTITY_CUTOUT;
    }

    public static RenderLayer getEntityTranslucent() {
        return ENTITY_TRANSLUCENT;
    }

    public static RenderLayer getEntityTranslucentCull() {
        return ENTITY_TRANSLUCENT_CULL;
    }

    public static void addDefaultTextures(Consumer<SpriteIdentifier> consumer) {
        consumer.accept(SHULKER_TEXTURE_ID);
        COLORED_SHULKER_BOXES_TEXTURES.forEach(consumer);
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            consumer.accept(new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, bannerPattern.getSpriteId(true)));
            consumer.accept(new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, bannerPattern.getSpriteId(false)));
        }
        WOOD_TYPE_TEXTURES.values().forEach(consumer);
        for (SpriteIdentifier spriteIdentifier : BED_TEXTURES) {
            consumer.accept(spriteIdentifier);
        }
        consumer.accept(TRAPPED);
        consumer.accept(TRAPPED_LEFT);
        consumer.accept(TRAPPED_RIGHT);
        consumer.accept(CHRISTMAS);
        consumer.accept(CHRISTMAS_LEFT);
        consumer.accept(CHRISTMAS_RIGHT);
        consumer.accept(NORMAL);
        consumer.accept(NORMAL_LEFT);
        consumer.accept(NORMAL_RIGHT);
        consumer.accept(ENDER);
    }

    public static SpriteIdentifier getSignTextureId(SignType signType) {
        return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new Identifier("entity/signs/" + signType.getName()));
    }

    private static SpriteIdentifier getChestTextureId(String string) {
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, new Identifier("entity/chest/" + string));
    }

    public static SpriteIdentifier getChestTexture(BlockEntity blockEntity, ChestType chestType, boolean bl) {
        if (bl) {
            return TexturedRenderLayers.getChestTexture(chestType, CHRISTMAS, CHRISTMAS_LEFT, CHRISTMAS_RIGHT);
        }
        if (blockEntity instanceof TrappedChestBlockEntity) {
            return TexturedRenderLayers.getChestTexture(chestType, TRAPPED, TRAPPED_LEFT, TRAPPED_RIGHT);
        }
        if (blockEntity instanceof EnderChestBlockEntity) {
            return ENDER;
        }
        return TexturedRenderLayers.getChestTexture(chestType, NORMAL, NORMAL_LEFT, NORMAL_RIGHT);
    }

    private static SpriteIdentifier getChestTexture(ChestType chestType, SpriteIdentifier spriteIdentifier, SpriteIdentifier spriteIdentifier2, SpriteIdentifier spriteIdentifier3) {
        switch (chestType) {
            case LEFT: {
                return spriteIdentifier2;
            }
            case RIGHT: {
                return spriteIdentifier3;
            }
        }
        return spriteIdentifier;
    }
}

