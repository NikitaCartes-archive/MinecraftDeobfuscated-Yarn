package net.minecraft;

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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.WoodType;

@Environment(EnvType.CLIENT)
public class class_4722 {
	public static final Identifier field_21704 = new Identifier("textures/atlas/shulker_boxes.png");
	public static final Identifier field_21705 = new Identifier("textures/atlas/beds.png");
	public static final Identifier field_21706 = new Identifier("textures/atlas/banner_patterns.png");
	public static final Identifier field_21707 = new Identifier("textures/atlas/shield_patterns.png");
	public static final Identifier field_21708 = new Identifier("textures/atlas/signs.png");
	public static final Identifier field_21709 = new Identifier("textures/atlas/chest.png");
	private static final RenderLayer field_21724 = RenderLayer.getEntityCutoutNoCull(field_21704);
	private static final RenderLayer field_21725 = RenderLayer.getEntitySolid(field_21705);
	private static final RenderLayer field_21726 = RenderLayer.getEntityNoOutline(field_21706);
	private static final RenderLayer field_21727 = RenderLayer.getEntityNoOutline(field_21707);
	private static final RenderLayer field_21728 = RenderLayer.getEntityCutoutNoCull(field_21708);
	private static final RenderLayer field_21729 = RenderLayer.getEntityCutout(field_21709);
	private static final RenderLayer field_21700 = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	private static final RenderLayer field_21701 = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	private static final RenderLayer field_21702 = RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	private static final RenderLayer field_21703 = RenderLayer.getEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	public static final class_4730 field_21710 = new class_4730(field_21704, new Identifier("entity/shulker/shulker"));
	public static final List<class_4730> field_21711 = (List<class_4730>)Stream.of(
			"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
		)
		.map(string -> new class_4730(field_21704, new Identifier("entity/shulker/shulker_" + string)))
		.collect(ImmutableList.toImmutableList());
	public static final Map<WoodType, class_4730> field_21712 = (Map<WoodType, class_4730>)WoodType.stream()
		.collect(Collectors.toMap(Function.identity(), class_4722::method_24064));
	public static final class_4730[] field_21713 = (class_4730[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new class_4730(field_21705, new Identifier("entity/bed/" + dyeColor.getName())))
		.toArray(class_4730[]::new);
	public static final class_4730 field_21714 = method_24065("trapped");
	public static final class_4730 field_21715 = method_24065("trapped_left");
	public static final class_4730 field_21716 = method_24065("trapped_right");
	public static final class_4730 field_21717 = method_24065("christmas");
	public static final class_4730 field_21718 = method_24065("christmas_left");
	public static final class_4730 field_21719 = method_24065("christmas_right");
	public static final class_4730 field_21720 = method_24065("normal");
	public static final class_4730 field_21721 = method_24065("normal_left");
	public static final class_4730 field_21722 = method_24065("normal_right");
	public static final class_4730 field_21723 = method_24065("ender");

	public static RenderLayer method_24059() {
		return field_21726;
	}

	public static RenderLayer method_24067() {
		return field_21727;
	}

	public static RenderLayer method_24069() {
		return field_21725;
	}

	public static RenderLayer method_24070() {
		return field_21724;
	}

	public static RenderLayer method_24071() {
		return field_21728;
	}

	public static RenderLayer method_24072() {
		return field_21729;
	}

	public static RenderLayer method_24073() {
		return field_21700;
	}

	public static RenderLayer method_24074() {
		return field_21701;
	}

	public static RenderLayer method_24075() {
		return field_21702;
	}

	public static RenderLayer method_24076() {
		return field_21703;
	}

	public static void method_24066(Consumer<class_4730> consumer) {
		consumer.accept(field_21710);
		field_21711.forEach(consumer);

		for (BannerPattern bannerPattern : BannerPattern.values()) {
			consumer.accept(new class_4730(field_21706, bannerPattern.getSpriteId(true)));
			consumer.accept(new class_4730(field_21707, bannerPattern.getSpriteId(false)));
		}

		field_21712.values().forEach(consumer);

		for (class_4730 lv : field_21713) {
			consumer.accept(lv);
		}

		consumer.accept(field_21714);
		consumer.accept(field_21715);
		consumer.accept(field_21716);
		consumer.accept(field_21717);
		consumer.accept(field_21718);
		consumer.accept(field_21719);
		consumer.accept(field_21720);
		consumer.accept(field_21721);
		consumer.accept(field_21722);
		consumer.accept(field_21723);
	}

	public static class_4730 method_24064(WoodType woodType) {
		return new class_4730(field_21708, new Identifier("entity/signs/" + woodType.getName()));
	}

	private static class_4730 method_24065(String string) {
		return new class_4730(field_21709, new Identifier("entity/chest/" + string));
	}

	public static class_4730 method_24062(BlockEntity blockEntity, ChestType chestType, boolean bl) {
		if (bl) {
			return method_24063(chestType, field_21717, field_21718, field_21719);
		} else if (blockEntity instanceof TrappedChestBlockEntity) {
			return method_24063(chestType, field_21714, field_21715, field_21716);
		} else {
			return blockEntity instanceof EnderChestBlockEntity ? field_21723 : method_24063(chestType, field_21720, field_21721, field_21722);
		}
	}

	private static class_4730 method_24063(ChestType chestType, class_4730 arg, class_4730 arg2, class_4730 arg3) {
		switch (chestType) {
			case LEFT:
				return arg2;
			case RIGHT:
				return arg3;
			case SINGLE:
			default:
				return arg;
		}
	}
}
