package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern {
	BASE("base", "b"),
	SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
	SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
	SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
	SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
	STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
	STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
	STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
	STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
	STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
	STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
	STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
	STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
	SMALL_STRIPES("small_stripes", "ss", "# #", "# #", "   "),
	CROSS("cross", "cr", "# #", " # ", "# #"),
	STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
	TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
	TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
	TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
	TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
	DIAGONAL_DOWN_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
	DIAGONAL_UP_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
	DIAGONAL_UP_LEFT("diagonal_up_left", "lud", "   ", "#  ", "## "),
	DIAGONAL_DOWN_RIGHT("diagonal_right", "rud", " ##", "  #", "   "),
	CIRCLE("circle", "mc", "   ", " # ", "   "),
	RHOMBUS("rhombus", "mr", " # ", "# #", " # "),
	HALF_VERTICAL_LEFT("half_vertical", "vh", "## ", "## ", "## "),
	HALF_HORIZONTAL_TOP("half_horizontal", "hh", "###", "###", "   "),
	HALF_VERTICAL_RIGHT("half_vertical_right", "vhr", " ##", " ##", " ##"),
	HALF_HORIZONTAL_BOTTOM("half_horizontal_bottom", "hhb", "   ", "###", "###"),
	BORDER("border", "bo", "###", "# #", "###"),
	CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.field_10597)),
	GRADIENT_DOWN("gradient", "gra", "# #", " # ", " # "),
	GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
	BRICKS("bricks", "bri", new ItemStack(Blocks.field_10104)),
	field_18689("globe", "glb"),
	CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
	SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
	FLOWER("flower", "flo", new ItemStack(Blocks.field_10554)),
	MOJANG("mojang", "moj", new ItemStack(Items.field_8367));

	public static final int COUNT = values().length;
	public static final int field_18283 = COUNT - 5 - 1;
	private final String name;
	private final String id;
	private final String[] recipePattern = new String[3];
	private ItemStack baseStack = ItemStack.EMPTY;

	private BannerPattern(String string2, String string3) {
		this.name = string2;
		this.id = string3;
	}

	private BannerPattern(String string2, String string3, ItemStack itemStack) {
		this(string2, string3);
		this.baseStack = itemStack;
	}

	private BannerPattern(String string2, String string3, String string4, String string5, String string6) {
		this(string2, string3);
		this.recipePattern[0] = string4;
		this.recipePattern[1] = string5;
		this.recipePattern[2] = string6;
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static BannerPattern byId(String string) {
		for (BannerPattern bannerPattern : values()) {
			if (bannerPattern.id.equals(string)) {
				return bannerPattern;
			}
		}

		return null;
	}

	public static class Builder {
		private final List<Pair<BannerPattern, DyeColor>> patterns = Lists.<Pair<BannerPattern, DyeColor>>newArrayList();

		public BannerPattern.Builder with(BannerPattern bannerPattern, DyeColor dyeColor) {
			this.patterns.add(Pair.of(bannerPattern, dyeColor));
			return this;
		}

		public ListTag method_16375() {
			ListTag listTag = new ListTag();

			for (Pair<BannerPattern, DyeColor> pair : this.patterns) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("Pattern", pair.getLeft().id);
				compoundTag.putInt("Color", pair.getRight().getId());
				listTag.add(compoundTag);
			}

			return listTag;
		}
	}
}
