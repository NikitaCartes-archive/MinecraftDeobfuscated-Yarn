package net.minecraft;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1321 extends class_1429 implements class_1312 {
	protected static final class_2940<Byte> field_6322 = class_2945.method_12791(class_1321.class, class_2943.field_13319);
	protected static final class_2940<Optional<UUID>> field_6320 = class_2945.method_12791(class_1321.class, class_2943.field_13313);
	protected class_1386 field_6321;

	protected class_1321(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_6175();
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6322, (byte)0);
		this.field_6011.method_12784(field_6320, Optional.empty());
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.method_6139() == null) {
			arg.method_10582("OwnerUUID", "");
		} else {
			arg.method_10582("OwnerUUID", this.method_6139().toString());
		}

		arg.method_10556("Sitting", this.method_6172());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		String string;
		if (arg.method_10573("OwnerUUID", 8)) {
			string = arg.method_10558("OwnerUUID");
		} else {
			String string2 = arg.method_10558("Owner");
			string = class_3321.method_14546(this.method_5682(), string2);
		}

		if (!string.isEmpty()) {
			try {
				this.method_6174(UUID.fromString(string));
				this.method_6173(true);
			} catch (Throwable var4) {
				this.method_6173(false);
			}
		}

		if (this.field_6321 != null) {
			this.field_6321.method_6311(arg.method_10577("Sitting"));
		}

		this.method_6179(arg.method_10577("Sitting"));
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return !this.method_5934();
	}

	protected void method_6180(boolean bl) {
		class_2394 lv = class_2398.field_11201;
		if (!bl) {
			lv = class_2398.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					lv,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.field_6019),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					d,
					e,
					f
				);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 7) {
			this.method_6180(true);
		} else if (b == 6) {
			this.method_6180(false);
		} else {
			super.method_5711(b);
		}
	}

	public boolean method_6181() {
		return (this.field_6011.method_12789(field_6322) & 4) != 0;
	}

	public void method_6173(boolean bl) {
		byte b = this.field_6011.method_12789(field_6322);
		if (bl) {
			this.field_6011.method_12778(field_6322, (byte)(b | 4));
		} else {
			this.field_6011.method_12778(field_6322, (byte)(b & -5));
		}

		this.method_6175();
	}

	protected void method_6175() {
	}

	public boolean method_6172() {
		return (this.field_6011.method_12789(field_6322) & 1) != 0;
	}

	public void method_6179(boolean bl) {
		byte b = this.field_6011.method_12789(field_6322);
		if (bl) {
			this.field_6011.method_12778(field_6322, (byte)(b | 1));
		} else {
			this.field_6011.method_12778(field_6322, (byte)(b & -2));
		}
	}

	@Nullable
	@Override
	public UUID method_6139() {
		return (UUID)this.field_6011.method_12789(field_6320).orElse(null);
	}

	public void method_6174(@Nullable UUID uUID) {
		this.field_6011.method_12778(field_6320, Optional.ofNullable(uUID));
	}

	public void method_6170(class_1657 arg) {
		this.method_6173(true);
		this.method_6174(arg.method_5667());
		if (arg instanceof class_3222) {
			class_174.field_1201.method_9132((class_3222)arg, this);
		}
	}

	@Nullable
	public class_1309 method_6177() {
		try {
			UUID uUID = this.method_6139();
			return uUID == null ? null : this.field_6002.method_8420(uUID);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean method_6171(class_1309 arg) {
		return arg == this.method_6177();
	}

	public class_1386 method_6176() {
		return this.field_6321;
	}

	public boolean method_6178(class_1309 arg, class_1309 arg2) {
		return true;
	}

	@Override
	public class_270 method_5781() {
		if (this.method_6181()) {
			class_1309 lv = this.method_6177();
			if (lv != null) {
				return lv.method_5781();
			}
		}

		return super.method_5781();
	}

	@Override
	public boolean method_5722(class_1297 arg) {
		if (this.method_6181()) {
			class_1309 lv = this.method_6177();
			if (arg == lv) {
				return true;
			}

			if (lv != null) {
				return lv.method_5722(arg);
			}
		}

		return super.method_5722(arg);
	}

	@Override
	public void method_6078(class_1282 arg) {
		if (!this.field_6002.field_9236 && this.field_6002.method_8450().method_8355("showDeathMessages") && this.method_6177() instanceof class_3222) {
			this.method_6177().method_9203(this.method_6066().method_5548());
		}

		super.method_6078(arg);
	}
}
