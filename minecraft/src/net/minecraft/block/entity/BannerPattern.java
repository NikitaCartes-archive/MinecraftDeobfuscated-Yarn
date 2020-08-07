package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern {
	field_11834("base", "b", false),
	field_11839("square_bottom_left", "bl"),
	field_11806("square_bottom_right", "br"),
	field_11831("square_top_left", "tl"),
	field_11848("square_top_right", "tr"),
	field_11810("stripe_bottom", "bs"),
	field_11829("stripe_top", "ts"),
	field_11837("stripe_left", "ls"),
	field_11813("stripe_right", "rs"),
	field_11819("stripe_center", "cs"),
	field_11838("stripe_middle", "ms"),
	field_11807("stripe_downright", "drs"),
	field_11820("stripe_downleft", "dls"),
	field_11814("small_stripes", "ss"),
	field_11844("cross", "cr"),
	field_11830("straight_cross", "sc"),
	field_11811("triangle_bottom", "bt"),
	field_11849("triangle_top", "tt"),
	field_11822("triangles_bottom", "bts"),
	field_11815("triangles_top", "tts"),
	field_11847("diagonal_left", "ld"),
	field_11835("diagonal_up_right", "rd"),
	field_11817("diagonal_up_left", "lud"),
	field_11842("diagonal_right", "rud"),
	field_11826("circle", "mc"),
	field_11821("rhombus", "mr"),
	field_11828("half_vertical", "vh"),
	field_11843("half_horizontal", "hh"),
	field_11818("half_vertical_right", "vhr"),
	field_11836("half_horizontal_bottom", "hhb"),
	field_11840("border", "bo"),
	field_11816("curly_border", "cbo"),
	field_11827("gradient", "gra"),
	field_11850("gradient_up", "gru"),
	field_11809("bricks", "bri"),
	field_18689("globe", "glb", true),
	field_11823("creeper", "cre", true),
	field_11845("skull", "sku", true),
	field_11812("flower", "flo", true),
	field_11825("mojang", "moj", true),
	field_23882("piglin", "pig", true);

	private static final BannerPattern[] VALUES = values();
	public static final int COUNT = VALUES.length;
	public static final int field_24417 = (int)Arrays.stream(VALUES).filter(bannerPattern -> bannerPattern.field_24419).count();
	public static final int LOOM_APPLICABLE_COUNT = COUNT - field_24417 - 1;
	private final boolean field_24419;
	private final String name;
	private final String id;

	private BannerPattern(String name, String id) {
		this(name, id, false);
	}

	private BannerPattern(String name, String id, boolean bl) {
		this.name = name;
		this.id = id;
		this.field_24419 = bl;
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
