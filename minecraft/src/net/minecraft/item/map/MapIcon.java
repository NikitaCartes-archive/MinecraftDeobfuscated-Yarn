package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.MapColor;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;

public record MapIcon(MapIcon.Type type, byte x, byte z, byte rotation, @Nullable Text text) {
	public byte getTypeId() {
		return this.type.getId();
	}

	public boolean isAlwaysRendered() {
		return this.type.isAlwaysRendered();
	}

	public static enum Type implements StringIdentifiable {
		PLAYER("player", false, true),
		FRAME("frame", true, true),
		RED_MARKER("red_marker", false, true),
		BLUE_MARKER("blue_marker", false, true),
		TARGET_X("target_x", true, false),
		TARGET_POINT("target_point", true, false),
		PLAYER_OFF_MAP("player_off_map", false, true),
		PLAYER_OFF_LIMITS("player_off_limits", false, true),
		MANSION("mansion", true, 5393476, false, true),
		MONUMENT("monument", true, 3830373, false, true),
		BANNER_WHITE("banner_white", true, true),
		BANNER_ORANGE("banner_orange", true, true),
		BANNER_MAGENTA("banner_magenta", true, true),
		BANNER_LIGHT_BLUE("banner_light_blue", true, true),
		BANNER_YELLOW("banner_yellow", true, true),
		BANNER_LIME("banner_lime", true, true),
		BANNER_PINK("banner_pink", true, true),
		BANNER_GRAY("banner_gray", true, true),
		BANNER_LIGHT_GRAY("banner_light_gray", true, true),
		BANNER_CYAN("banner_cyan", true, true),
		BANNER_PURPLE("banner_purple", true, true),
		BANNER_BLUE("banner_blue", true, true),
		BANNER_BROWN("banner_brown", true, true),
		BANNER_GREEN("banner_green", true, true),
		BANNER_RED("banner_red", true, true),
		BANNER_BLACK("banner_black", true, true),
		RED_X("red_x", true, false),
		DESERT_VILLAGE("village_desert", true, MapColor.LIGHT_GRAY.color, false, true),
		PLAINS_VILLAGE("village_plains", true, MapColor.LIGHT_GRAY.color, false, true),
		SAVANNA_VILLAGE("village_savanna", true, MapColor.LIGHT_GRAY.color, false, true),
		SNOWY_VILLAGE("village_snowy", true, MapColor.LIGHT_GRAY.color, false, true),
		TAIGA_VILLAGE("village_taiga", true, MapColor.LIGHT_GRAY.color, false, true),
		JUNGLE_TEMPLE("jungle_temple", true, MapColor.LIGHT_GRAY.color, false, true),
		SWAMP_HUT("swamp_hut", true, MapColor.LIGHT_GRAY.color, false, true);

		public static final Codec<MapIcon.Type> CODEC = StringIdentifiable.createCodec(MapIcon.Type::values);
		private final String name;
		private final byte id;
		private final boolean alwaysRender;
		private final int tintColor;
		private final boolean structure;
		private final boolean useIconCountLimit;

		private Type(String name, boolean alwaysRender, boolean useIconCountLimit) {
			this(name, alwaysRender, -1, useIconCountLimit, false);
		}

		private Type(String name, boolean alwaysRender, int tintColor, boolean useIconCountLimit, boolean structure) {
			this.name = name;
			this.useIconCountLimit = useIconCountLimit;
			this.id = (byte)this.ordinal();
			this.alwaysRender = alwaysRender;
			this.tintColor = tintColor;
			this.structure = structure;
		}

		public byte getId() {
			return this.id;
		}

		public boolean isStructure() {
			return this.structure;
		}

		public boolean isAlwaysRendered() {
			return this.alwaysRender;
		}

		public boolean hasTintColor() {
			return this.tintColor >= 0;
		}

		public int getTintColor() {
			return this.tintColor;
		}

		public static MapIcon.Type byId(byte id) {
			return values()[MathHelper.clamp(id, 0, values().length - 1)];
		}

		public boolean shouldUseIconCountLimit() {
			return this.useIconCountLimit;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
