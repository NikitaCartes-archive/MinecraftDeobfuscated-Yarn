package net.minecraft.item.map;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;

public class MapIcon {
	private final MapIcon.Direction direction;
	private byte x;
	private byte z;
	private byte type;
	private final TextComponent field_78;

	public MapIcon(MapIcon.Direction direction, byte b, byte c, byte d, @Nullable TextComponent textComponent) {
		this.direction = direction;
		this.x = b;
		this.z = c;
		this.type = d;
		this.field_78 = textComponent;
	}

	@Environment(EnvType.CLIENT)
	public byte getDirection() {
		return this.direction.method_98();
	}

	public MapIcon.Direction method_93() {
		return this.direction;
	}

	public byte getX() {
		return this.x;
	}

	public byte getZ() {
		return this.z;
	}

	public byte getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_94() {
		return this.direction.method_95();
	}

	@Nullable
	public TextComponent method_88() {
		return this.field_78;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof MapIcon)) {
			return false;
		} else {
			MapIcon mapIcon = (MapIcon)object;
			if (this.direction != mapIcon.direction) {
				return false;
			} else if (this.type != mapIcon.type) {
				return false;
			} else if (this.x != mapIcon.x) {
				return false;
			} else {
				return this.z != mapIcon.z ? false : Objects.equals(this.field_78, mapIcon.field_78);
			}
		}
	}

	public int hashCode() {
		int i = this.direction.method_98();
		i = 31 * i + this.x;
		i = 31 * i + this.z;
		i = 31 * i + this.type;
		return 31 * i + Objects.hashCode(this.field_78);
	}

	public static enum Direction {
		field_91(false),
		field_95(true),
		field_89(false),
		field_83(false),
		field_84(true),
		field_85(true),
		field_86(false),
		field_87(false),
		field_88(true, 5393476),
		field_98(true, 3830373),
		field_96(true),
		field_92(true),
		field_97(true),
		field_90(true),
		field_93(true),
		field_94(true),
		field_100(true),
		field_101(true),
		field_107(true),
		field_108(true),
		field_104(true),
		field_105(true),
		field_106(true),
		field_102(true),
		field_99(true),
		field_103(true),
		field_110(true);

		private final byte field_81 = (byte)this.ordinal();
		private final boolean field_111;
		private final int field_82;

		private Direction(boolean bl) {
			this(bl, -1);
		}

		private Direction(boolean bl, int j) {
			this.field_111 = bl;
			this.field_82 = j;
		}

		public byte method_98() {
			return this.field_81;
		}

		@Environment(EnvType.CLIENT)
		public boolean method_95() {
			return this.field_111;
		}

		public boolean method_97() {
			return this.field_82 >= 0;
		}

		public int method_96() {
			return this.field_82;
		}

		public static MapIcon.Direction method_99(byte b) {
			return values()[MathHelper.clamp(b, 0, values().length - 1)];
		}
	}
}
