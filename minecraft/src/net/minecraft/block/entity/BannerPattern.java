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
import net.minecraft.util.Identifier;
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
	STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
	CROSS("cross", "cr", "# #", " # ", "# #"),
	STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
	TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
	TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
	TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
	TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
	DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
	DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
	DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
	DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
	CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
	RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
	HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
	HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
	HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
	HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
	BORDER("border", "bo", "###", "# #", "###"),
	CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
	GRADIENT("gradient", "gra", "# #", " # ", " # "),
	GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
	BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
	GLOBE("globe", "glb"),
	CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
	SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
	FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
	MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

	public static final int COUNT = values().length;
	public static final int LOOM_APPLICABLE_COUNT = COUNT - 5 - 1;
	private final String name;
	private final String id;
	private final String[] recipePattern = new String[3];
	private ItemStack baseStack = ItemStack.EMPTY;

	private BannerPattern(String name, String id) {
		this.name = name;
		this.id = id;
	}

	private BannerPattern(String name, String id, ItemStack baseStack) {
		this(name, id);
		this.baseStack = baseStack;
	}

	private BannerPattern(String name, String id, String recipePattern0, String recipePattern1, String recipePattern2) {
		this(name, id);
		this.recipePattern[0] = recipePattern0;
		this.recipePattern[1] = recipePattern1;
		this.recipePattern[2] = recipePattern2;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getSpriteId(boolean bl) {
		String string = bl ? "banner" : "shield";
		return new Identifier("entity/" + string + "/" + this.getName());
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
	public static BannerPattern byId(String id) {
		for (BannerPattern bannerPattern : values()) {
			if (bannerPattern.id.equals(id)) {
				return bannerPattern;
			}
		}

		return null;
	}

	public static class Patterns {
		private final List<Pair<BannerPattern, DyeColor>> entries = Lists.<Pair<BannerPattern, DyeColor>>newArrayList();

		public BannerPattern.Patterns add(BannerPattern pattern, DyeColor color) {
			this.entries.add(Pair.of(pattern, color));
			return this;
		}

		public ListTag toTag() {
			ListTag listTag = new ListTag();

			for (Pair<BannerPattern, DyeColor> pair : this.entries) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putString("Pattern", pair.getLeft().id);
				compoundTag.putInt("Color", pair.getRight().getId());
				listTag.add(compoundTag);
			}

			return listTag;
		}
	}
}
