package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TexturedRenderLayers {
	public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = new Identifier("textures/atlas/shulker_boxes.png");
	public static final Identifier BEDS_ATLAS_TEXTURE = new Identifier("textures/atlas/beds.png");
	public static final Identifier BANNER_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/banner_patterns.png");
	public static final Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/shield_patterns.png");
	public static final Identifier SIGNS_ATLAS_TEXTURE = new Identifier("textures/atlas/signs.png");
	public static final Identifier CHEST_ATLAS_TEXTURE = new Identifier("textures/atlas/chest.png");
	public static final Identifier ARMOR_TRIMS_ATLAS_TEXTURE = new Identifier("textures/atlas/armor_trims.png");
	public static final Identifier DECORATED_POT_ATLAS_TEXTURE = new Identifier("textures/atlas/decorated_pot.png");
	private static final RenderLayer SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
	private static final RenderLayer BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
	private static final RenderLayer BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
	private static final RenderLayer SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
	private static final RenderLayer SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
	private static final RenderLayer CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
	private static final RenderLayer ARMOR_TRIMS_RENDER_LAYER = RenderLayer.getArmorCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
	private static final RenderLayer ARMOR_TRIMS_DECAL_RENDER_LAYER = RenderLayer.createArmorDecalCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ITEM_ENTITY_TRANSLUCENT_CULL = RenderLayer.getItemEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_TRANSLUCENT_CULL = RenderLayer.getEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	public static final SpriteIdentifier SHULKER_TEXTURE_ID = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker"));
	public static final List<SpriteIdentifier> COLORED_SHULKER_BOXES_TEXTURES = (List<SpriteIdentifier>)Stream.of(
			"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
		)
		.map(colorName -> new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker_" + colorName)))
		.collect(ImmutableList.toImmutableList());
	public static final Map<WoodType, SpriteIdentifier> SIGN_TYPE_TEXTURES = (Map<WoodType, SpriteIdentifier>)WoodType.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createSignTextureId));
	public static final Map<WoodType, SpriteIdentifier> HANGING_SIGN_TYPE_TEXTURES = (Map<WoodType, SpriteIdentifier>)WoodType.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createHangingSignTextureId));
	public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/banner/base"));
	public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/shield/base"));
	private static final Map<Identifier, SpriteIdentifier> BANNER_PATTERN_TEXTURES = new HashMap();
	private static final Map<Identifier, SpriteIdentifier> SHIELD_PATTERN_TEXTURES = new HashMap();
	public static final Map<RegistryKey<String>, SpriteIdentifier> DECORATED_POT_PATTERN_TEXTURES = (Map<RegistryKey<String>, SpriteIdentifier>)Registries.DECORATED_POT_PATTERN
		.getKeys()
		.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createDecoratedPotPatternTextureId));
	public static final SpriteIdentifier[] BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(color -> new SpriteIdentifier(BEDS_ATLAS_TEXTURE, new Identifier("entity/bed/" + color.getName())))
		.toArray(SpriteIdentifier[]::new);
	public static final SpriteIdentifier TRAPPED = createChestTextureId("trapped");
	public static final SpriteIdentifier TRAPPED_LEFT = createChestTextureId("trapped_left");
	public static final SpriteIdentifier TRAPPED_RIGHT = createChestTextureId("trapped_right");
	public static final SpriteIdentifier CHRISTMAS = createChestTextureId("christmas");
	public static final SpriteIdentifier CHRISTMAS_LEFT = createChestTextureId("christmas_left");
	public static final SpriteIdentifier CHRISTMAS_RIGHT = createChestTextureId("christmas_right");
	public static final SpriteIdentifier NORMAL = createChestTextureId("normal");
	public static final SpriteIdentifier NORMAL_LEFT = createChestTextureId("normal_left");
	public static final SpriteIdentifier NORMAL_RIGHT = createChestTextureId("normal_right");
	public static final SpriteIdentifier ENDER = createChestTextureId("ender");

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

	public static RenderLayer getHangingSign() {
		return SIGN_RENDER_LAYER;
	}

	public static RenderLayer getChest() {
		return CHEST_RENDER_LAYER;
	}

	public static RenderLayer getArmorTrims(boolean decal) {
		return decal ? ARMOR_TRIMS_DECAL_RENDER_LAYER : ARMOR_TRIMS_RENDER_LAYER;
	}

	public static RenderLayer getEntitySolid() {
		return ENTITY_SOLID;
	}

	public static RenderLayer getEntityCutout() {
		return ENTITY_CUTOUT;
	}

	public static RenderLayer getItemEntityTranslucentCull() {
		return ITEM_ENTITY_TRANSLUCENT_CULL;
	}

	public static RenderLayer getEntityTranslucentCull() {
		return ENTITY_TRANSLUCENT_CULL;
	}

	private static SpriteIdentifier createSignTextureId(WoodType type) {
		return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new Identifier("entity/signs/" + type.name()));
	}

	private static SpriteIdentifier createHangingSignTextureId(WoodType type) {
		return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new Identifier("entity/signs/hanging/" + type.name()));
	}

	public static SpriteIdentifier getSignTextureId(WoodType signType) {
		return (SpriteIdentifier)SIGN_TYPE_TEXTURES.get(signType);
	}

	public static SpriteIdentifier getHangingSignTextureId(WoodType signType) {
		return (SpriteIdentifier)HANGING_SIGN_TYPE_TEXTURES.get(signType);
	}

	public static SpriteIdentifier getBannerPatternTextureId(RegistryEntry<BannerPattern> pattern) {
		return (SpriteIdentifier)BANNER_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), id -> {
			Identifier identifier = id.withPrefixedPath("entity/banner/");
			return new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, identifier);
		});
	}

	public static SpriteIdentifier getShieldPatternTextureId(RegistryEntry<BannerPattern> pattern) {
		return (SpriteIdentifier)SHIELD_PATTERN_TEXTURES.computeIfAbsent(pattern.value().assetId(), id -> {
			Identifier identifier = id.withPrefixedPath("entity/shield/");
			return new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, identifier);
		});
	}

	private static SpriteIdentifier createChestTextureId(String variant) {
		return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, new Identifier("entity/chest/" + variant));
	}

	private static SpriteIdentifier createDecoratedPotPatternTextureId(RegistryKey<String> potPatternKey) {
		return new SpriteIdentifier(DECORATED_POT_ATLAS_TEXTURE, DecoratedPotPatterns.getTextureId(potPatternKey));
	}

	@Nullable
	public static SpriteIdentifier getDecoratedPotPatternTextureId(@Nullable RegistryKey<String> potPatternKey) {
		return potPatternKey == null ? null : (SpriteIdentifier)DECORATED_POT_PATTERN_TEXTURES.get(potPatternKey);
	}

	public static SpriteIdentifier getChestTextureId(BlockEntity blockEntity, ChestType type, boolean christmas) {
		if (blockEntity instanceof EnderChestBlockEntity) {
			return ENDER;
		} else if (christmas) {
			return getChestTextureId(type, CHRISTMAS, CHRISTMAS_LEFT, CHRISTMAS_RIGHT);
		} else {
			return blockEntity instanceof TrappedChestBlockEntity
				? getChestTextureId(type, TRAPPED, TRAPPED_LEFT, TRAPPED_RIGHT)
				: getChestTextureId(type, NORMAL, NORMAL_LEFT, NORMAL_RIGHT);
		}
	}

	private static SpriteIdentifier getChestTextureId(ChestType type, SpriteIdentifier single, SpriteIdentifier left, SpriteIdentifier right) {
		switch (type) {
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case SINGLE:
			default:
				return single;
		}
	}
}
