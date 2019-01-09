package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1308 extends class_1309 {
	private static final class_2940<Byte> field_6193 = class_2945.method_12791(class_1308.class, class_2943.field_13319);
	public int field_6191;
	protected int field_6194;
	protected class_1333 field_6206;
	protected class_1335 field_6207;
	protected class_1334 field_6204;
	private final class_1330 field_6188;
	protected class_1408 field_6189;
	protected final class_1355 field_6201;
	protected final class_1355 field_6185;
	private class_1309 field_6199;
	private final class_1413 field_6190;
	private final class_2371<class_1799> field_6195 = class_2371.method_10213(2, class_1799.field_8037);
	protected final float[] field_6187 = new float[2];
	private final class_2371<class_1799> field_6205 = class_2371.method_10213(4, class_1799.field_8037);
	protected final float[] field_6186 = new float[4];
	private boolean field_6203;
	private boolean field_6200;
	private final Map<class_7, Float> field_6196 = Maps.newEnumMap(class_7.class);
	private class_2960 field_6198;
	private long field_6184;
	private boolean field_6197;
	private class_1297 field_6202;
	private class_2487 field_6192;

	protected class_1308(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6201 = new class_1355(arg2 != null && arg2.method_16107() != null ? arg2.method_16107() : null);
		this.field_6185 = new class_1355(arg2 != null && arg2.method_16107() != null ? arg2.method_16107() : null);
		this.field_6206 = new class_1333(this);
		this.field_6207 = new class_1335(this);
		this.field_6204 = new class_1334(this);
		this.field_6188 = this.method_5963();
		this.field_6189 = this.method_5965(arg2);
		this.field_6190 = new class_1413(this);
		Arrays.fill(this.field_6186, 0.085F);
		Arrays.fill(this.field_6187, 0.085F);
		if (arg2 != null && !arg2.field_9236) {
			this.method_5959();
		}
	}

	protected void method_5959() {
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(class_1612.field_7365).method_6192(16.0);
		this.method_6127().method_6208(class_1612.field_7361);
	}

	protected class_1408 method_5965(class_1937 arg) {
		return new class_1409(this, arg);
	}

	public float method_5944(class_7 arg) {
		Float float_ = (Float)this.field_6196.get(arg);
		return float_ == null ? arg.method_11() : float_;
	}

	public void method_5941(class_7 arg, float f) {
		this.field_6196.put(arg, f);
	}

	protected class_1330 method_5963() {
		return new class_1330(this);
	}

	public class_1333 method_5988() {
		return this.field_6206;
	}

	public class_1335 method_5962() {
		if (this.method_5765() && this.method_5854() instanceof class_1308) {
			class_1308 lv = (class_1308)this.method_5854();
			return lv.method_5962();
		} else {
			return this.field_6207;
		}
	}

	public class_1334 method_5993() {
		return this.field_6204;
	}

	public class_1408 method_5942() {
		if (this.method_5765() && this.method_5854() instanceof class_1308) {
			class_1308 lv = (class_1308)this.method_5854();
			return lv.method_5942();
		} else {
			return this.field_6189;
		}
	}

	public class_1413 method_5985() {
		return this.field_6190;
	}

	@Nullable
	public class_1309 method_5968() {
		return this.field_6199;
	}

	public void method_5980(@Nullable class_1309 arg) {
		this.field_6199 = arg;
	}

	public boolean method_5973(Class<? extends class_1309> class_) {
		return class_ != class_1571.class;
	}

	public void method_5983() {
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6193, (byte)0);
	}

	public int method_5970() {
		return 80;
	}

	public void method_5966() {
		class_3414 lv = this.method_5994();
		if (lv != null) {
			this.method_5783(lv, this.method_6107(), this.method_6017());
		}
	}

	@Override
	public void method_5670() {
		super.method_5670();
		this.field_6002.method_16107().method_15396("mobBaseTick");
		if (this.method_5805() && this.field_5974.nextInt(1000) < this.field_6191++) {
			this.method_5975();
			this.method_5966();
		}

		this.field_6002.method_16107().method_15407();
	}

	@Override
	protected void method_6013(class_1282 arg) {
		this.method_5975();
		super.method_6013(arg);
	}

	private void method_5975() {
		this.field_6191 = -this.method_5970();
	}

	@Override
	protected int method_6110(class_1657 arg) {
		if (this.field_6194 > 0) {
			int i = this.field_6194;

			for (int j = 0; j < this.field_6205.size(); j++) {
				if (!this.field_6205.get(j).method_7960() && this.field_6186[j] <= 1.0F) {
					i += 1 + this.field_5974.nextInt(3);
				}
			}

			for (int jx = 0; jx < this.field_6195.size(); jx++) {
				if (!this.field_6195.get(jx).method_7960() && this.field_6187[jx] <= 1.0F) {
					i += 1 + this.field_5974.nextInt(3);
				}
			}

			return i;
		} else {
			return this.field_6194;
		}
	}

	public void method_5990() {
		if (this.field_6002.field_9236) {
			for (int i = 0; i < 20; i++) {
				double d = this.field_5974.nextGaussian() * 0.02;
				double e = this.field_5974.nextGaussian() * 0.02;
				double f = this.field_5974.nextGaussian() * 0.02;
				double g = 10.0;
				this.field_6002
					.method_8406(
						class_2398.field_11203,
						this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998 - d * 10.0,
						this.field_6010 + (double)(this.field_5974.nextFloat() * this.field_6019) - e * 10.0,
						this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998 - f * 10.0,
						d,
						e,
						f
					);
			}
		} else {
			this.field_6002.method_8421(this, (byte)20);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 20) {
			this.method_5990();
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.field_6002.field_9236) {
			this.method_5995();
			if (this.field_6012 % 5 == 0) {
				boolean bl = !(this.method_5642() instanceof class_1308);
				boolean bl2 = !(this.method_5854() instanceof class_1690);
				this.field_6201.method_6276(1, bl);
				this.field_6201.method_6276(4, bl && bl2);
				this.field_6201.method_6276(2, bl);
			}
		}
	}

	@Override
	protected float method_6031(float f, float g) {
		this.field_6188.method_6224();
		return g;
	}

	@Nullable
	protected class_3414 method_5994() {
		return null;
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("CanPickUpLoot", this.method_5936());
		arg.method_10556("PersistenceRequired", this.field_6200);
		class_2499 lv = new class_2499();

		for (class_1799 lv2 : this.field_6205) {
			class_2487 lv3 = new class_2487();
			if (!lv2.method_7960()) {
				lv2.method_7953(lv3);
			}

			lv.method_10606(lv3);
		}

		arg.method_10566("ArmorItems", lv);
		class_2499 lv4 = new class_2499();

		for (class_1799 lv5 : this.field_6195) {
			class_2487 lv6 = new class_2487();
			if (!lv5.method_7960()) {
				lv5.method_7953(lv6);
			}

			lv4.method_10606(lv6);
		}

		arg.method_10566("HandItems", lv4);
		class_2499 lv7 = new class_2499();

		for (float f : this.field_6186) {
			lv7.method_10606(new class_2494(f));
		}

		arg.method_10566("ArmorDropChances", lv7);
		class_2499 lv8 = new class_2499();

		for (float g : this.field_6187) {
			lv8.method_10606(new class_2494(g));
		}

		arg.method_10566("HandDropChances", lv8);
		arg.method_10556("Leashed", this.field_6197);
		if (this.field_6202 != null) {
			class_2487 lv6 = new class_2487();
			if (this.field_6202 instanceof class_1309) {
				UUID uUID = this.field_6202.method_5667();
				lv6.method_10560("UUID", uUID);
			} else if (this.field_6202 instanceof class_1530) {
				class_2338 lv9 = ((class_1530)this.field_6202).method_6896();
				lv6.method_10569("X", lv9.method_10263());
				lv6.method_10569("Y", lv9.method_10264());
				lv6.method_10569("Z", lv9.method_10260());
			}

			arg.method_10566("Leash", lv6);
		}

		arg.method_10556("LeftHanded", this.method_5961());
		if (this.field_6198 != null) {
			arg.method_10582("DeathLootTable", this.field_6198.toString());
			if (this.field_6184 != 0L) {
				arg.method_10544("DeathLootTableSeed", this.field_6184);
			}
		}

		if (this.method_5987()) {
			arg.method_10556("NoAI", this.method_5987());
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("CanPickUpLoot", 1)) {
			this.method_5952(arg.method_10577("CanPickUpLoot"));
		}

		this.field_6200 = arg.method_10577("PersistenceRequired");
		if (arg.method_10573("ArmorItems", 9)) {
			class_2499 lv = arg.method_10554("ArmorItems", 10);

			for (int i = 0; i < this.field_6205.size(); i++) {
				this.field_6205.set(i, class_1799.method_7915(lv.method_10602(i)));
			}
		}

		if (arg.method_10573("HandItems", 9)) {
			class_2499 lv = arg.method_10554("HandItems", 10);

			for (int i = 0; i < this.field_6195.size(); i++) {
				this.field_6195.set(i, class_1799.method_7915(lv.method_10602(i)));
			}
		}

		if (arg.method_10573("ArmorDropChances", 9)) {
			class_2499 lv = arg.method_10554("ArmorDropChances", 5);

			for (int i = 0; i < lv.size(); i++) {
				this.field_6186[i] = lv.method_10604(i);
			}
		}

		if (arg.method_10573("HandDropChances", 9)) {
			class_2499 lv = arg.method_10554("HandDropChances", 5);

			for (int i = 0; i < lv.size(); i++) {
				this.field_6187[i] = lv.method_10604(i);
			}
		}

		this.field_6197 = arg.method_10577("Leashed");
		if (this.field_6197 && arg.method_10573("Leash", 10)) {
			this.field_6192 = arg.method_10562("Leash");
		}

		this.method_5937(arg.method_10577("LeftHanded"));
		if (arg.method_10573("DeathLootTable", 8)) {
			this.field_6198 = new class_2960(arg.method_10558("DeathLootTable"));
			this.field_6184 = arg.method_10537("DeathLootTableSeed");
		}

		this.method_5977(arg.method_10577("NoAI"));
	}

	@Override
	protected void method_16077(class_1282 arg, boolean bl) {
		super.method_16077(arg, bl);
		this.field_6198 = null;
	}

	@Override
	protected class_47.class_48 method_16079(boolean bl, class_1282 arg) {
		return super.method_16079(bl, arg).method_310(this.field_6184, this.field_5974);
	}

	@Override
	public final class_2960 method_5989() {
		return this.field_6198 == null ? this.method_5991() : this.field_6198;
	}

	protected class_2960 method_5991() {
		return super.method_5989();
	}

	public void method_5930(float f) {
		this.field_6250 = f;
	}

	public void method_5976(float f) {
		this.field_6227 = f;
	}

	public void method_5938(float f) {
		this.field_6212 = f;
	}

	@Override
	public void method_6125(float f) {
		super.method_6125(f);
		this.method_5930(f);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		this.field_6002.method_16107().method_15396("looting");
		if (!this.field_6002.field_9236 && this.method_5936() && !this.field_6272 && this.field_6002.method_8450().method_8355("mobGriefing")) {
			for (class_1542 lv : this.field_6002.method_8403(class_1542.class, this.method_5829().method_1009(1.0, 0.0, 1.0))) {
				if (!lv.field_5988 && !lv.method_6983().method_7960() && !lv.method_6977()) {
					this.method_5949(lv);
				}
			}
		}

		this.field_6002.method_16107().method_15407();
	}

	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		class_1304 lv2 = method_5953(lv);
		class_1799 lv3 = this.method_6118(lv2);
		boolean bl = this.method_5955(lv, lv3, lv2);
		if (bl && this.method_5939(lv)) {
			double d = (double)this.method_5929(lv2);
			if (!lv3.method_7960() && (double)(this.field_5974.nextFloat() - 0.1F) < d) {
				this.method_5775(lv3);
			}

			this.method_5673(lv2, lv);
			switch (lv2.method_5925()) {
				case field_6177:
					this.field_6187[lv2.method_5927()] = 2.0F;
					break;
				case field_6178:
					this.field_6186[lv2.method_5927()] = 2.0F;
			}

			this.field_6200 = true;
			this.method_6103(arg, lv.method_7947());
			arg.method_5650();
		}
	}

	protected boolean method_5955(class_1799 arg, class_1799 arg2, class_1304 arg3) {
		boolean bl = true;
		if (!arg2.method_7960()) {
			if (arg3.method_5925() == class_1304.class_1305.field_6177) {
				if (arg.method_7909() instanceof class_1829 && !(arg2.method_7909() instanceof class_1829)) {
					bl = true;
				} else if (arg.method_7909() instanceof class_1829 && arg2.method_7909() instanceof class_1829) {
					class_1829 lv = (class_1829)arg.method_7909();
					class_1829 lv2 = (class_1829)arg2.method_7909();
					if (lv.method_8020() == lv2.method_8020()) {
						bl = arg.method_7919() < arg2.method_7919() || arg.method_7985() && !arg2.method_7985();
					} else {
						bl = lv.method_8020() > lv2.method_8020();
					}
				} else if (arg.method_7909() instanceof class_1753 && arg2.method_7909() instanceof class_1753) {
					bl = arg.method_7985() && !arg2.method_7985();
				} else {
					bl = false;
				}
			} else if (arg.method_7909() instanceof class_1738 && !(arg2.method_7909() instanceof class_1738)) {
				bl = true;
			} else if (arg.method_7909() instanceof class_1738 && arg2.method_7909() instanceof class_1738 && !class_1890.method_8224(arg2)) {
				class_1738 lv3 = (class_1738)arg.method_7909();
				class_1738 lv4 = (class_1738)arg2.method_7909();
				if (lv3.method_7687() == lv4.method_7687()) {
					bl = arg.method_7919() < arg2.method_7919() || arg.method_7985() && !arg2.method_7985();
				} else {
					bl = lv3.method_7687() > lv4.method_7687();
				}
			} else {
				bl = false;
			}
		}

		return bl;
	}

	protected boolean method_5939(class_1799 arg) {
		return true;
	}

	public boolean method_5974(double d) {
		return true;
	}

	protected boolean method_17326() {
		return false;
	}

	protected void method_5982() {
		if (!this.method_5947() && !this.method_17326()) {
			class_1297 lv = this.field_6002.method_8614(this, -1.0);
			if (lv != null) {
				double d = lv.method_5858(this);
				if (d > 16384.0 && this.method_5974(d)) {
					this.method_5650();
				}

				if (this.field_6278 > 600 && this.field_5974.nextInt(800) == 0 && d > 1024.0 && this.method_5974(d)) {
					this.method_5650();
				} else if (d < 1024.0) {
					this.field_6278 = 0;
				}
			}
		} else {
			this.field_6278 = 0;
		}
	}

	@Override
	protected final void method_6023() {
		this.field_6278++;
		this.field_6002.method_16107().method_15396("checkDespawn");
		this.method_5982();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("sensing");
		this.field_6190.method_6370();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("targetSelector");
		this.field_6185.method_6275();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("goalSelector");
		this.field_6201.method_6275();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("navigation");
		this.field_6189.method_6360();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("mob tick");
		this.method_5958();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15396("controls");
		this.field_6002.method_16107().method_15396("move");
		this.field_6207.method_6240();
		this.field_6002.method_16107().method_15405("look");
		this.field_6206.method_6231();
		this.field_6002.method_16107().method_15405("jump");
		this.field_6204.method_6234();
		this.field_6002.method_16107().method_15407();
		this.field_6002.method_16107().method_15407();
	}

	protected void method_5958() {
	}

	public int method_5978() {
		return 40;
	}

	public int method_5986() {
		return 75;
	}

	public void method_5951(class_1297 arg, float f, float g) {
		double d = arg.field_5987 - this.field_5987;
		double e = arg.field_6035 - this.field_6035;
		double h;
		if (arg instanceof class_1309) {
			class_1309 lv = (class_1309)arg;
			h = lv.field_6010 + (double)lv.method_5751() - (this.field_6010 + (double)this.method_5751());
		} else {
			h = (arg.method_5829().field_1322 + arg.method_5829().field_1325) / 2.0 - (this.field_6010 + (double)this.method_5751());
		}

		double i = (double)class_3532.method_15368(d * d + e * e);
		float j = (float)(class_3532.method_15349(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float k = (float)(-(class_3532.method_15349(h, i) * 180.0F / (float)Math.PI));
		this.field_5965 = this.method_5960(this.field_5965, k, g);
		this.field_6031 = this.method_5960(this.field_6031, j, f);
	}

	private float method_5960(float f, float g, float h) {
		float i = class_3532.method_15393(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		class_2680 lv = arg.method_8320(new class_2338(this).method_10074());
		return lv.method_11611(this);
	}

	public final boolean method_5950() {
		return this.method_5957(this.field_6002);
	}

	public boolean method_5957(class_1941 arg) {
		return !arg.method_8599(this.method_5829()) && arg.method_8587(this, this.method_5829()) && arg.method_8606(this, this.method_5829());
	}

	@Environment(EnvType.CLIENT)
	public float method_5967() {
		return 1.0F;
	}

	public int method_5945() {
		return 4;
	}

	public boolean method_5969(int i) {
		return false;
	}

	@Override
	public int method_5850() {
		if (this.method_5968() == null) {
			return 3;
		} else {
			int i = (int)(this.method_6032() - this.method_6063() * 0.33F);
			i -= (3 - this.field_6002.method_8407().method_5461()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public Iterable<class_1799> method_5877() {
		return this.field_6195;
	}

	@Override
	public Iterable<class_1799> method_5661() {
		return this.field_6205;
	}

	@Override
	public class_1799 method_6118(class_1304 arg) {
		switch (arg.method_5925()) {
			case field_6177:
				return this.field_6195.get(arg.method_5927());
			case field_6178:
				return this.field_6205.get(arg.method_5927());
			default:
				return class_1799.field_8037;
		}
	}

	@Override
	public void method_5673(class_1304 arg, class_1799 arg2) {
		switch (arg.method_5925()) {
			case field_6177:
				this.field_6195.set(arg.method_5927(), arg2);
				break;
			case field_6178:
				this.field_6205.set(arg.method_5927(), arg2);
		}
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);

		for (class_1304 lv : class_1304.values()) {
			class_1799 lv2 = this.method_6118(lv);
			float f = this.method_5929(lv);
			boolean bl2 = f > 1.0F;
			if (!lv2.method_7960() && !class_1890.method_8221(lv2) && (bl || bl2) && this.field_5974.nextFloat() - (float)i * 0.01F < f) {
				if (!bl2 && lv2.method_7963()) {
					lv2.method_7974(lv2.method_7936() - this.field_5974.nextInt(1 + this.field_5974.nextInt(Math.max(lv2.method_7936() - 3, 1))));
				}

				this.method_5775(lv2);
			}
		}
	}

	protected float method_5929(class_1304 arg) {
		float f;
		switch (arg.method_5925()) {
			case field_6177:
				f = this.field_6187[arg.method_5927()];
				break;
			case field_6178:
				f = this.field_6186[arg.method_5927()];
				break;
			default:
				f = 0.0F;
		}

		return f;
	}

	protected void method_5964(class_1266 arg) {
		if (this.field_5974.nextFloat() < 0.15F * arg.method_5458()) {
			int i = this.field_5974.nextInt(2);
			float f = this.field_6002.method_8407() == class_1267.field_5807 ? 0.1F : 0.25F;
			if (this.field_5974.nextFloat() < 0.095F) {
				i++;
			}

			if (this.field_5974.nextFloat() < 0.095F) {
				i++;
			}

			if (this.field_5974.nextFloat() < 0.095F) {
				i++;
			}

			boolean bl = true;

			for (class_1304 lv : class_1304.values()) {
				if (lv.method_5925() == class_1304.class_1305.field_6178) {
					class_1799 lv2 = this.method_6118(lv);
					if (!bl && this.field_5974.nextFloat() < f) {
						break;
					}

					bl = false;
					if (lv2.method_7960()) {
						class_1792 lv3 = method_5948(lv, i);
						if (lv3 != null) {
							this.method_5673(lv, new class_1799(lv3));
						}
					}
				}
			}
		}
	}

	public static class_1304 method_5953(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		if (lv != class_2246.field_10147.method_8389() && (!(lv instanceof class_1747) || !(((class_1747)lv).method_7711() instanceof class_2190))) {
			if (lv instanceof class_1738) {
				return ((class_1738)lv).method_7685();
			} else if (lv == class_1802.field_8833) {
				return class_1304.field_6174;
			} else {
				return lv == class_1802.field_8255 ? class_1304.field_6171 : class_1304.field_6173;
			}
		} else {
			return class_1304.field_6169;
		}
	}

	@Nullable
	public static class_1792 method_5948(class_1304 arg, int i) {
		switch (arg) {
			case field_6169:
				if (i == 0) {
					return class_1802.field_8267;
				} else if (i == 1) {
					return class_1802.field_8862;
				} else if (i == 2) {
					return class_1802.field_8283;
				} else if (i == 3) {
					return class_1802.field_8743;
				} else if (i == 4) {
					return class_1802.field_8805;
				}
			case field_6174:
				if (i == 0) {
					return class_1802.field_8577;
				} else if (i == 1) {
					return class_1802.field_8678;
				} else if (i == 2) {
					return class_1802.field_8873;
				} else if (i == 3) {
					return class_1802.field_8523;
				} else if (i == 4) {
					return class_1802.field_8058;
				}
			case field_6172:
				if (i == 0) {
					return class_1802.field_8570;
				} else if (i == 1) {
					return class_1802.field_8416;
				} else if (i == 2) {
					return class_1802.field_8218;
				} else if (i == 3) {
					return class_1802.field_8396;
				} else if (i == 4) {
					return class_1802.field_8348;
				}
			case field_6166:
				if (i == 0) {
					return class_1802.field_8370;
				} else if (i == 1) {
					return class_1802.field_8753;
				} else if (i == 2) {
					return class_1802.field_8313;
				} else if (i == 3) {
					return class_1802.field_8660;
				} else if (i == 4) {
					return class_1802.field_8285;
				}
			default:
				return null;
		}
	}

	protected void method_5984(class_1266 arg) {
		float f = arg.method_5458();
		if (!this.method_6047().method_7960() && this.field_5974.nextFloat() < 0.25F * f) {
			this.method_5673(
				class_1304.field_6173, class_1890.method_8233(this.field_5974, this.method_6047(), (int)(5.0F + f * (float)this.field_5974.nextInt(18)), false)
			);
		}

		for (class_1304 lv : class_1304.values()) {
			if (lv.method_5925() == class_1304.class_1305.field_6178) {
				class_1799 lv2 = this.method_6118(lv);
				if (!lv2.method_7960() && this.field_5974.nextFloat() < 0.5F * f) {
					this.method_5673(lv, class_1890.method_8233(this.field_5974, lv2, (int)(5.0F + f * (float)this.field_5974.nextInt(18)), false));
				}
			}
		}
	}

	@Nullable
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_5996(class_1612.field_7365)
			.method_6197(new class_1322("Random spawn bonus", this.field_5974.nextGaussian() * 0.05, class_1322.class_1323.field_6330));
		if (this.field_5974.nextFloat() < 0.05F) {
			this.method_5937(true);
		} else {
			this.method_5937(false);
		}

		return arg4;
	}

	public boolean method_5956() {
		return false;
	}

	public void method_5971() {
		this.field_6200 = true;
	}

	public void method_5946(class_1304 arg, float f) {
		switch (arg.method_5925()) {
			case field_6177:
				this.field_6187[arg.method_5927()] = f;
				break;
			case field_6178:
				this.field_6186[arg.method_5927()] = f;
		}
	}

	public boolean method_5936() {
		return this.field_6203;
	}

	public void method_5952(boolean bl) {
		this.field_6203 = bl;
	}

	public boolean method_5947() {
		return this.field_6200;
	}

	@Override
	public final boolean method_5688(class_1657 arg, class_1268 arg2) {
		if (this.method_5934() && this.method_5933() == arg) {
			this.method_5932(true, !arg.field_7503.field_7477);
			return true;
		} else {
			class_1799 lv = arg.method_5998(arg2);
			if (lv.method_7909() == class_1802.field_8719 && this.method_5931(arg)) {
				this.method_5954(arg, true);
				lv.method_7934(1);
				return true;
			} else {
				return this.method_5992(arg, arg2) ? true : super.method_5688(arg, arg2);
			}
		}
	}

	protected boolean method_5992(class_1657 arg, class_1268 arg2) {
		return false;
	}

	protected void method_5995() {
		if (this.field_6192 != null) {
			this.method_5940();
		}

		if (this.field_6197) {
			if (!this.method_5805()) {
				this.method_5932(true, true);
			}

			if (this.field_6202 == null || this.field_6202.field_5988) {
				this.method_5932(true, true);
			}
		}
	}

	public void method_5932(boolean bl, boolean bl2) {
		if (this.field_6197) {
			this.field_6197 = false;
			this.field_6202 = null;
			if (!this.field_6002.field_9236 && bl2) {
				this.method_5706(class_1802.field_8719);
			}

			if (!this.field_6002.field_9236 && bl && this.field_6002 instanceof class_3218) {
				((class_3218)this.field_6002).method_14180().method_14079(this, new class_2740(this, null));
			}
		}
	}

	public boolean method_5931(class_1657 arg) {
		return !this.method_5934() && !(this instanceof class_1569);
	}

	public boolean method_5934() {
		return this.field_6197;
	}

	public class_1297 method_5933() {
		return this.field_6202;
	}

	public void method_5954(class_1297 arg, boolean bl) {
		this.field_6197 = true;
		this.field_6202 = arg;
		if (!this.field_6002.field_9236 && bl && this.field_6002 instanceof class_3218) {
			((class_3218)this.field_6002).method_14180().method_14079(this, new class_2740(this, this.field_6202));
		}

		if (this.method_5765()) {
			this.method_5848();
		}
	}

	@Override
	public boolean method_5873(class_1297 arg, boolean bl) {
		boolean bl2 = super.method_5873(arg, bl);
		if (bl2 && this.method_5934()) {
			this.method_5932(true, true);
		}

		return bl2;
	}

	private void method_5940() {
		if (this.field_6197 && this.field_6192 != null) {
			if (this.field_6192.method_10576("UUID")) {
				UUID uUID = this.field_6192.method_10584("UUID");

				for (class_1309 lv : this.field_6002.method_8403(class_1309.class, this.method_5829().method_1014(10.0))) {
					if (lv.method_5667().equals(uUID)) {
						this.method_5954(lv, true);
						break;
					}
				}
			} else if (this.field_6192.method_10573("X", 99) && this.field_6192.method_10573("Y", 99) && this.field_6192.method_10573("Z", 99)) {
				class_2338 lv2 = new class_2338(this.field_6192.method_10550("X"), this.field_6192.method_10550("Y"), this.field_6192.method_10550("Z"));
				class_1532 lv3 = class_1532.method_6932(this.field_6002, lv2);
				if (lv3 == null) {
					lv3 = class_1532.method_6931(this.field_6002, lv2);
				}

				this.method_5954(lv3, true);
			} else {
				this.method_5932(false, true);
			}
		}

		this.field_6192 = null;
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		class_1304 lv;
		if (i == 98) {
			lv = class_1304.field_6173;
		} else if (i == 99) {
			lv = class_1304.field_6171;
		} else if (i == 100 + class_1304.field_6169.method_5927()) {
			lv = class_1304.field_6169;
		} else if (i == 100 + class_1304.field_6174.method_5927()) {
			lv = class_1304.field_6174;
		} else if (i == 100 + class_1304.field_6172.method_5927()) {
			lv = class_1304.field_6172;
		} else {
			if (i != 100 + class_1304.field_6166.method_5927()) {
				return false;
			}

			lv = class_1304.field_6166;
		}

		if (!arg.method_7960() && !method_5935(lv, arg) && lv != class_1304.field_6169) {
			return false;
		} else {
			this.method_5673(lv, arg);
			return true;
		}
	}

	@Override
	public boolean method_5787() {
		return this.method_5956() && super.method_5787();
	}

	public static boolean method_5935(class_1304 arg, class_1799 arg2) {
		class_1304 lv = method_5953(arg2);
		return lv == arg || lv == class_1304.field_6173 && arg == class_1304.field_6171 || lv == class_1304.field_6171 && arg == class_1304.field_6173;
	}

	@Override
	public boolean method_6034() {
		return super.method_6034() && !this.method_5987();
	}

	public void method_5977(boolean bl) {
		byte b = this.field_6011.method_12789(field_6193);
		this.field_6011.method_12778(field_6193, bl ? (byte)(b | 1) : (byte)(b & -2));
	}

	public void method_5937(boolean bl) {
		byte b = this.field_6011.method_12789(field_6193);
		this.field_6011.method_12778(field_6193, bl ? (byte)(b | 2) : (byte)(b & -3));
	}

	public boolean method_5987() {
		return (this.field_6011.method_12789(field_6193) & 1) != 0;
	}

	public boolean method_5961() {
		return (this.field_6011.method_12789(field_6193) & 2) != 0;
	}

	@Override
	public class_1306 method_6068() {
		return this.method_5961() ? class_1306.field_6182 : class_1306.field_6183;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		float f = (float)this.method_5996(class_1612.field_7363).method_6194();
		float g = (float)this.method_5996(class_1612.field_7361).method_6194();
		if (arg instanceof class_1309) {
			f += class_1890.method_8218(this.method_6047(), ((class_1309)arg).method_6046());
			g += (float)class_1890.method_8205(this);
		}

		boolean bl = arg.method_5643(class_1282.method_5511(this), f);
		if (bl) {
			if (g > 0.0F && arg instanceof class_1309) {
				((class_1309)arg)
					.method_6005(
						this,
						g * 0.5F,
						(double)class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)),
						(double)(-class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)))
					);
				this.field_5967 *= 0.6;
				this.field_6006 *= 0.6;
			}

			int i = class_1890.method_8199(this);
			if (i > 0) {
				arg.method_5639(i * 4);
			}

			if (arg instanceof class_1657) {
				class_1657 lv = (class_1657)arg;
				class_1799 lv2 = this.method_6047();
				class_1799 lv3 = lv.method_6115() ? lv.method_6030() : class_1799.field_8037;
				if (!lv2.method_7960() && !lv3.method_7960() && lv2.method_7909() instanceof class_1743 && lv3.method_7909() == class_1802.field_8255) {
					float h = 0.25F + (float)class_1890.method_8234(this) * 0.05F;
					if (this.field_5974.nextFloat() < h) {
						lv.method_7357().method_7906(class_1802.field_8255, 100);
						this.field_6002.method_8421(lv, (byte)30);
					}
				}
			}

			this.method_5723(this, arg);
		}

		return bl;
	}

	protected boolean method_5972() {
		if (this.field_6002.method_8530() && !this.field_6002.field_9236) {
			float f = this.method_5718();
			class_2338 lv = this.method_5854() instanceof class_1690
				? new class_2338(this.field_5987, (double)Math.round(this.field_6010), this.field_6035).method_10084()
				: new class_2338(this.field_5987, (double)Math.round(this.field_6010), this.field_6035);
			if (f > 0.5F && this.field_5974.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.field_6002.method_8311(lv)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void method_6010(class_3494<class_3611> arg) {
		if (this.method_5942().method_6350()) {
			super.method_6010(arg);
		} else {
			this.field_5984 += 0.3F;
		}
	}
}
