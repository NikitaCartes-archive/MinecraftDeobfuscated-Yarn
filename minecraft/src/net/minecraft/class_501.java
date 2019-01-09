package net.minecraft;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_501 extends class_503.class_504 {
	private static final Logger field_3066 = LogManager.getLogger();
	private static final ThreadPoolExecutor field_3069 = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new class_140(field_3066)).build()
	);
	private static final class_2960 field_3060 = new class_2960("textures/misc/unknown_server.png");
	private static final class_2960 field_3059 = new class_2960("textures/gui/server_selection.png");
	private final class_500 field_3068;
	private final class_310 field_3064;
	private final class_642 field_3061;
	private final class_2960 field_3065;
	private String field_3062;
	private class_1043 field_3063;
	private long field_3067;

	protected class_501(class_500 arg, class_642 arg2) {
		this.field_3068 = arg;
		this.field_3061 = arg2;
		this.field_3064 = class_310.method_1551();
		this.field_3065 = new class_2960("servers/" + Hashing.sha1().hashUnencodedChars(arg2.field_3761) + "/icon");
		this.field_3063 = (class_1043)this.field_3064.method_1531().method_4619(this.field_3065);
	}

	@Override
	public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906();
		int n = this.method_1907();
		if (!this.field_3061.field_3754) {
			this.field_3061.field_3754 = true;
			this.field_3061.field_3758 = -2L;
			this.field_3061.field_3757 = "";
			this.field_3061.field_3753 = "";
			field_3069.submit(() -> {
				try {
					this.field_3068.method_2538().method_3003(this.field_3061);
				} catch (UnknownHostException var2) {
					this.field_3061.field_3758 = -1L;
					this.field_3061.field_3757 = class_124.field_1079 + class_1074.method_4662("multiplayer.status.cannot_resolve");
				} catch (Exception var3) {
					this.field_3061.field_3758 = -1L;
					this.field_3061.field_3757 = class_124.field_1079 + class_1074.method_4662("multiplayer.status.cannot_connect");
				}
			});
		}

		boolean bl2 = this.field_3061.field_3756 > class_155.method_16673().getProtocolVersion();
		boolean bl3 = this.field_3061.field_3756 < class_155.method_16673().getProtocolVersion();
		boolean bl4 = bl2 || bl3;
		this.field_3064.field_1772.method_1729(this.field_3061.field_3752, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		List<String> list = this.field_3064.field_1772.method_1728(this.field_3061.field_3757, i - 32 - 2);

		for (int o = 0; o < Math.min(list.size(), 2); o++) {
			this.field_3064.field_1772.method_1729((String)list.get(o), (float)(n + 32 + 3), (float)(m + 12 + 9 * o), 8421504);
		}

		String string = bl4 ? class_124.field_1079 + this.field_3061.field_3760 : this.field_3061.field_3753;
		int p = this.field_3064.field_1772.method_1727(string);
		this.field_3064.field_1772.method_1729(string, (float)(n + i - p - 15 - 2), (float)(m + 1), 8421504);
		int q = 0;
		String string2 = null;
		int r;
		String string3;
		if (bl4) {
			r = 5;
			string3 = class_1074.method_4662(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			string2 = this.field_3061.field_3762;
		} else if (this.field_3061.field_3754 && this.field_3061.field_3758 != -2L) {
			if (this.field_3061.field_3758 < 0L) {
				r = 5;
			} else if (this.field_3061.field_3758 < 150L) {
				r = 0;
			} else if (this.field_3061.field_3758 < 300L) {
				r = 1;
			} else if (this.field_3061.field_3758 < 600L) {
				r = 2;
			} else if (this.field_3061.field_3758 < 1000L) {
				r = 3;
			} else {
				r = 4;
			}

			if (this.field_3061.field_3758 < 0L) {
				string3 = class_1074.method_4662("multiplayer.status.no_connection");
			} else {
				string3 = this.field_3061.field_3758 + "ms";
				string2 = this.field_3061.field_3762;
			}
		} else {
			q = 1;
			r = (int)(class_156.method_658() / 100L + (long)(this.method_1908() * 2) & 7L);
			if (r > 4) {
				r = 8 - r;
			}

			string3 = class_1074.method_4662("multiplayer.status.pinging");
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_3064.method_1531().method_4618(class_332.field_2053);
		class_332.method_1781(n + i - 15, m, (float)(q * 10), (float)(176 + r * 8), 10, 8, 256.0F, 256.0F);
		if (this.field_3061.method_2991() != null && !this.field_3061.method_2991().equals(this.field_3062)) {
			this.field_3062 = this.field_3061.method_2991();
			this.method_2554();
			this.field_3068.method_2529().method_2987();
		}

		if (this.field_3063 != null) {
			this.method_2557(n, m, this.field_3065);
		} else {
			this.method_2557(n, m, field_3060);
		}

		int s = k - n;
		int t = l - m;
		if (s >= i - 15 && s <= i - 5 && t >= 0 && t <= 8) {
			this.field_3068.method_2528(string3);
		} else if (s >= i - p - 15 - 2 && s <= i - 15 - 2 && t >= 0 && t <= 8) {
			this.field_3068.method_2528(string2);
		}

		if (this.field_3064.field_1690.field_1854 || bl) {
			this.field_3064.method_1531().method_4618(field_3059);
			class_332.method_1785(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int u = k - n;
			int v = l - m;
			if (this.method_2558()) {
				if (u < 32 && u > 16) {
					class_332.method_1781(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					class_332.method_1781(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.field_3068.method_2533(this, this.method_1908())) {
				if (u < 16 && v < 16) {
					class_332.method_1781(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					class_332.method_1781(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.field_3068.method_2547(this, this.method_1908())) {
				if (u < 16 && v > 16) {
					class_332.method_1781(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					class_332.method_1781(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}
		}
	}

	protected void method_2557(int i, int j, class_2960 arg) {
		this.field_3064.method_1531().method_4618(arg);
		GlStateManager.enableBlend();
		class_332.method_1781(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private boolean method_2558() {
		return true;
	}

	private void method_2554() {
		String string = this.field_3061.method_2991();
		if (string == null) {
			this.field_3064.method_1531().method_4615(this.field_3065);
			if (this.field_3063 != null && this.field_3063.method_4525() != null) {
				this.field_3063.method_4525().close();
			}

			this.field_3063 = null;
		} else {
			try {
				class_1011 lv = class_1011.method_15990(string);
				Validate.validState(lv.method_4307() == 64, "Must be 64 pixels wide");
				Validate.validState(lv.method_4323() == 64, "Must be 64 pixels high");
				if (this.field_3063 == null) {
					this.field_3063 = new class_1043(lv);
				} else {
					this.field_3063.method_4526(lv);
					this.field_3063.method_4524();
				}

				this.field_3064.method_1531().method_4616(this.field_3065, this.field_3063);
			} catch (Throwable var3) {
				field_3066.error("Invalid icon for server {} ({})", this.field_3061.field_3752, this.field_3061.field_3761, var3);
				this.field_3061.method_2989(null);
			}
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		double f = d - (double)this.method_1907();
		double g = e - (double)this.method_1906();
		if (f <= 32.0) {
			if (f < 32.0 && f > 16.0 && this.method_2558()) {
				this.field_3068.method_2544(this.method_1908());
				this.field_3068.method_2536();
				return true;
			}

			if (f < 16.0 && g < 16.0 && this.field_3068.method_2533(this, this.method_1908())) {
				this.field_3068.method_2531(this, this.method_1908(), class_437.method_2223());
				return true;
			}

			if (f < 16.0 && g > 16.0 && this.field_3068.method_2547(this, this.method_1908())) {
				this.field_3068.method_2553(this, this.method_1908(), class_437.method_2223());
				return true;
			}
		}

		this.field_3068.method_2544(this.method_1908());
		if (class_156.method_658() - this.field_3067 < 250L) {
			this.field_3068.method_2536();
		}

		this.field_3067 = class_156.method_658();
		return false;
	}

	public class_642 method_2556() {
		return this.field_3061;
	}
}
