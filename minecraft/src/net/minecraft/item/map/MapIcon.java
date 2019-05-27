package net.minecraft.item.map;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.MathHelper;

public class MapIcon {
	private final MapIcon.Type type;
	private byte x;
	private byte z;
	private byte rotation;
	private final Component text;

	public MapIcon(MapIcon.Type type, byte b, byte c, byte d, @Nullable Component component) {
		this.type = type;
		this.x = b;
		this.z = c;
		this.rotation = d;
		this.text = component;
	}

	@Environment(EnvType.CLIENT)
	public byte getTypeId() {
		return this.type.getId();
	}

	public MapIcon.Type getType() {
		return this.type;
	}

	public byte getX() {
		return this.x;
	}

	public byte getZ() {
		return this.z;
	}

	public byte getRotation() {
		return this.rotation;
	}

	@Environment(EnvType.CLIENT)
	public boolean isAlwaysRendered() {
		return this.type.isAlwaysRendered();
	}

	@Nullable
	public Component getText() {
		return this.text;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof MapIcon)) {
			return false;
		} else {
			MapIcon mapIcon = (MapIcon)object;
			if (this.type != mapIcon.type) {
				return false;
			} else if (this.rotation != mapIcon.rotation) {
				return false;
			} else if (this.x != mapIcon.x) {
				return false;
			} else {
				return this.z != mapIcon.z ? false : Objects.equals(this.text, mapIcon.text);
			}
		}
	}

	public int hashCode() {
		int i = this.type.getId();
		i = 31 * i + this.x;
		i = 31 * i + this.z;
		i = 31 * i + this.rotation;
		return 31 * i + Objects.hashCode(this.text);
	}

	public static enum Type {
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

		private final byte id = (byte)this.ordinal();
		private final boolean alwaysRender;
		private final int tintColor;

		private Type(boolean bl) {
			this(bl, -1);
		}

		private Type(boolean bl, int j) {
			this.alwaysRender = bl;
			this.tintColor = j;
		}

		public byte getId() {
			return this.id;
		}

		@Environment(EnvType.CLIENT)
		public boolean isAlwaysRendered() {
			return this.alwaysRender;
		}

		public boolean hasTintColor() {
			return this.tintColor >= 0;
		}

		public int getTintColor() {
			return this.tintColor;
		}

		public static MapIcon.Type byId(byte b) {
			return values()[MathHelper.clamp(b, 0, values().length - 1)];
		}
	}
}
