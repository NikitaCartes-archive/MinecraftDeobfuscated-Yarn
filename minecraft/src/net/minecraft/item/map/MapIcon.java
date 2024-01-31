package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.block.MapColor;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;

public record MapIcon(MapIcon.Type type, byte x, byte z, byte rotation, Optional<Text> text) {
	public static final PacketCodec<RegistryByteBuf, MapIcon> CODEC = PacketCodec.tuple(
		MapIcon.Type.PACKET_CODEC,
		MapIcon::type,
		PacketCodecs.BYTE,
		MapIcon::x,
		PacketCodecs.BYTE,
		MapIcon::z,
		PacketCodecs.BYTE,
		MapIcon::rotation,
		TextCodecs.OPTIONAL_PACKET_CODEC,
		MapIcon::text,
		MapIcon::new
	);

	public MapIcon(MapIcon.Type type, byte x, byte z, byte rotation, Optional<Text> text) {
		rotation = (byte)(rotation & 15);
		this.type = type;
		this.x = x;
		this.z = z;
		this.rotation = rotation;
		this.text = text;
	}

	public byte getTypeId() {
		return this.type.getId();
	}

	public boolean isAlwaysRendered() {
		return this.type.isAlwaysRendered();
	}

	public static enum Type implements StringIdentifiable {
		PLAYER(0, "player", false, true),
		FRAME(1, "frame", true, true),
		RED_MARKER(2, "red_marker", false, true),
		BLUE_MARKER(3, "blue_marker", false, true),
		TARGET_X(4, "target_x", true, false),
		TARGET_POINT(5, "target_point", true, false),
		PLAYER_OFF_MAP(6, "player_off_map", false, true),
		PLAYER_OFF_LIMITS(7, "player_off_limits", false, true),
		MANSION(8, "mansion", true, 5393476, false, true),
		MONUMENT(9, "monument", true, 3830373, false, true),
		BANNER_WHITE(10, "banner_white", true, true),
		BANNER_ORANGE(11, "banner_orange", true, true),
		BANNER_MAGENTA(12, "banner_magenta", true, true),
		BANNER_LIGHT_BLUE(13, "banner_light_blue", true, true),
		BANNER_YELLOW(14, "banner_yellow", true, true),
		BANNER_LIME(15, "banner_lime", true, true),
		BANNER_PINK(16, "banner_pink", true, true),
		BANNER_GRAY(17, "banner_gray", true, true),
		BANNER_LIGHT_GRAY(18, "banner_light_gray", true, true),
		BANNER_CYAN(19, "banner_cyan", true, true),
		BANNER_PURPLE(20, "banner_purple", true, true),
		BANNER_BLUE(21, "banner_blue", true, true),
		BANNER_BROWN(22, "banner_brown", true, true),
		BANNER_GREEN(23, "banner_green", true, true),
		BANNER_RED(24, "banner_red", true, true),
		BANNER_BLACK(25, "banner_black", true, true),
		RED_X(26, "red_x", true, false),
		DESERT_VILLAGE(27, "village_desert", true, MapColor.LIGHT_GRAY.color, false, true),
		PLAINS_VILLAGE(28, "village_plains", true, MapColor.LIGHT_GRAY.color, false, true),
		SAVANNA_VILLAGE(29, "village_savanna", true, MapColor.LIGHT_GRAY.color, false, true),
		SNOWY_VILLAGE(30, "village_snowy", true, MapColor.LIGHT_GRAY.color, false, true),
		TAIGA_VILLAGE(31, "village_taiga", true, MapColor.LIGHT_GRAY.color, false, true),
		JUNGLE_TEMPLE(32, "jungle_temple", true, MapColor.LIGHT_GRAY.color, false, true),
		SWAMP_HUT(33, "swamp_hut", true, MapColor.LIGHT_GRAY.color, false, true);

		public static final IntFunction<MapIcon.Type> INDEX_TO_TYPE = ValueLists.createIdToValueFunction(
			MapIcon.Type::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final Codec<MapIcon.Type> CODEC = StringIdentifiable.createCodec(MapIcon.Type::values);
		public static final PacketCodec<ByteBuf, MapIcon.Type> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_TYPE, MapIcon.Type::getIndex);
		private final int index;
		private final String name;
		private final byte id;
		private final boolean alwaysRender;
		private final int tintColor;
		private final boolean structure;
		private final boolean useIconCountLimit;

		private Type(int index, String name, boolean alwaysRender, boolean useIconCountLimit) {
			this(index, name, alwaysRender, -1, useIconCountLimit, false);
		}

		private Type(int index, String name, boolean alwaysRender, int tintColor, boolean useIconCountLimit, boolean structure) {
			this.index = index;
			this.name = name;
			this.useIconCountLimit = useIconCountLimit;
			this.id = (byte)this.ordinal();
			this.alwaysRender = alwaysRender;
			this.tintColor = tintColor;
			this.structure = structure;
		}

		public int getIndex() {
			return this.index;
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
