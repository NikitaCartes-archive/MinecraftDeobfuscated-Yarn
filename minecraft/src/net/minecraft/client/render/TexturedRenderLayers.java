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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

@Environment(EnvType.CLIENT)
public class TexturedRenderLayers {
	public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = new Identifier("textures/atlas/shulker_boxes.png");
	public static final Identifier BEDS_ATLAS_TEXTURE = new Identifier("textures/atlas/beds.png");
	public static final Identifier BANNER_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/banner_patterns.png");
	public static final Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = new Identifier("textures/atlas/shield_patterns.png");
	public static final Identifier SIGNS_ATLAS_TEXTURE = new Identifier("textures/atlas/signs.png");
	public static final Identifier HANGING_SIGNS_ATLAS_TEXTURE = new Identifier("textures/atlas/hanging_signs.png");
	public static final Identifier CHEST_ATLAS_TEXTURE = new Identifier("textures/atlas/chest.png");
	private static final RenderLayer SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
	private static final RenderLayer BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
	private static final RenderLayer BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
	private static final RenderLayer SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
	private static final RenderLayer SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
	private static final RenderLayer HANGING_SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(HANGING_SIGNS_ATLAS_TEXTURE);
	private static final RenderLayer CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ITEM_ENTITY_TRANSLUCENT_CULL = RenderLayer.getItemEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer ENTITY_TRANSLUCENT_CULL = RenderLayer.getEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
	public static final SpriteIdentifier SHULKER_TEXTURE_ID = new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker"));
	public static final List<SpriteIdentifier> COLORED_SHULKER_BOXES_TEXTURES = (List<SpriteIdentifier>)Stream.of(
			"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
		)
		.map(string -> new SpriteIdentifier(SHULKER_BOXES_ATLAS_TEXTURE, new Identifier("entity/shulker/shulker_" + string)))
		.collect(ImmutableList.toImmutableList());
	public static final Map<SignType, SpriteIdentifier> SIGN_TYPE_TEXTURES = (Map<SignType, SpriteIdentifier>)SignType.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createSignTextureId));
	public static final Map<SignType, SpriteIdentifier> HANGING_SIGN_TYPE_TEXTURES = (Map<SignType, SpriteIdentifier>)SignType.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createHangingSignTextureId));
	public static final Map<RegistryKey<BannerPattern>, SpriteIdentifier> BANNER_PATTERN_TEXTURES = (Map<RegistryKey<BannerPattern>, SpriteIdentifier>)Registries.BANNER_PATTERN
		.getKeys()
		.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createBannerPatternTextureId));
	public static final Map<RegistryKey<BannerPattern>, SpriteIdentifier> SHIELD_PATTERN_TEXTURES = (Map<RegistryKey<BannerPattern>, SpriteIdentifier>)Registries.BANNER_PATTERN
		.getKeys()
		.stream()
		.collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createShieldPatternTextureId));
	public static final SpriteIdentifier[] BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new SpriteIdentifier(BEDS_ATLAS_TEXTURE, new Identifier("entity/bed/" + dyeColor.getName())))
		.toArray(SpriteIdentifier[]::new);
	public static final SpriteIdentifier TRAPPED = getChestTextureId("trapped");
	public static final SpriteIdentifier TRAPPED_LEFT = getChestTextureId("trapped_left");
	public static final SpriteIdentifier TRAPPED_RIGHT = getChestTextureId("trapped_right");
	public static final SpriteIdentifier CHRISTMAS = getChestTextureId("christmas");
	public static final SpriteIdentifier CHRISTMAS_LEFT = getChestTextureId("christmas_left");
	public static final SpriteIdentifier CHRISTMAS_RIGHT = getChestTextureId("christmas_right");
	public static final SpriteIdentifier NORMAL = getChestTextureId("normal");
	public static final SpriteIdentifier NORMAL_LEFT = getChestTextureId("normal_left");
	public static final SpriteIdentifier NORMAL_RIGHT = getChestTextureId("normal_right");
	public static final SpriteIdentifier ENDER = getChestTextureId("ender");

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
		return HANGING_SIGN_RENDER_LAYER;
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

	public static RenderLayer getItemEntityTranslucentCull() {
		return ITEM_ENTITY_TRANSLUCENT_CULL;
	}

	public static RenderLayer getEntityTranslucentCull() {
		return ENTITY_TRANSLUCENT_CULL;
	}

	public static void addDefaultTextures(Consumer<SpriteIdentifier> adder) {
		adder.accept(SHULKER_TEXTURE_ID);
		COLORED_SHULKER_BOXES_TEXTURES.forEach(adder);
		BANNER_PATTERN_TEXTURES.values().forEach(adder);
		SHIELD_PATTERN_TEXTURES.values().forEach(adder);
		SIGN_TYPE_TEXTURES.values().forEach(adder);
		HANGING_SIGN_TYPE_TEXTURES.values().forEach(adder);

		for (SpriteIdentifier spriteIdentifier : BED_TEXTURES) {
			adder.accept(spriteIdentifier);
		}

		adder.accept(TRAPPED);
		adder.accept(TRAPPED_LEFT);
		adder.accept(TRAPPED_RIGHT);
		adder.accept(CHRISTMAS);
		adder.accept(CHRISTMAS_LEFT);
		adder.accept(CHRISTMAS_RIGHT);
		adder.accept(NORMAL);
		adder.accept(NORMAL_LEFT);
		adder.accept(NORMAL_RIGHT);
		adder.accept(ENDER);
	}

	private static SpriteIdentifier createSignTextureId(SignType type) {
		return new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new Identifier("entity/signs/" + type.getName()));
	}

	private static SpriteIdentifier createHangingSignTextureId(SignType type) {
		return new SpriteIdentifier(HANGING_SIGNS_ATLAS_TEXTURE, new Identifier("entity/signs/hanging/" + type.getName()));
	}

	public static SpriteIdentifier getSignTextureId(SignType signType) {
		return (SpriteIdentifier)SIGN_TYPE_TEXTURES.get(signType);
	}

	public static SpriteIdentifier getHangingSignTextureId(SignType signType) {
		return (SpriteIdentifier)HANGING_SIGN_TYPE_TEXTURES.get(signType);
	}

	private static SpriteIdentifier createBannerPatternTextureId(RegistryKey<BannerPattern> bannerPattern) {
		return new SpriteIdentifier(BANNER_PATTERNS_ATLAS_TEXTURE, BannerPattern.getSpriteId(bannerPattern, true));
	}

	public static SpriteIdentifier getBannerPatternTextureId(RegistryKey<BannerPattern> bannerPattern) {
		return (SpriteIdentifier)BANNER_PATTERN_TEXTURES.get(bannerPattern);
	}

	private static SpriteIdentifier createShieldPatternTextureId(RegistryKey<BannerPattern> bannerPattern) {
		return new SpriteIdentifier(SHIELD_PATTERNS_ATLAS_TEXTURE, BannerPattern.getSpriteId(bannerPattern, false));
	}

	public static SpriteIdentifier getShieldPatternTextureId(RegistryKey<BannerPattern> bannerPattern) {
		return (SpriteIdentifier)SHIELD_PATTERN_TEXTURES.get(bannerPattern);
	}

	private static SpriteIdentifier getChestTextureId(String variant) {
		return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, new Identifier("entity/chest/" + variant));
	}

	public static SpriteIdentifier getChestTexture(BlockEntity blockEntity, ChestType type, boolean christmas) {
		if (blockEntity instanceof EnderChestBlockEntity) {
			return ENDER;
		} else if (christmas) {
			return getChestTexture(type, CHRISTMAS, CHRISTMAS_LEFT, CHRISTMAS_RIGHT);
		} else {
			return blockEntity instanceof TrappedChestBlockEntity
				? getChestTexture(type, TRAPPED, TRAPPED_LEFT, TRAPPED_RIGHT)
				: getChestTexture(type, NORMAL, NORMAL_LEFT, NORMAL_RIGHT);
		}
	}

	private static SpriteIdentifier getChestTexture(ChestType type, SpriteIdentifier single, SpriteIdentifier left, SpriteIdentifier right) {
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
