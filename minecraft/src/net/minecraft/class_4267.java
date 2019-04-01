package net.minecraft;

import com.google.common.collect.Lists;
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
public class class_4267 extends class_350<class_4267.class_504> {
	private static final Logger field_19104 = LogManager.getLogger();
	private static final ThreadPoolExecutor field_19105 = new ScheduledThreadPoolExecutor(
		5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new class_140(field_19104)).build()
	);
	private static final class_2960 field_19106 = new class_2960("textures/misc/unknown_server.png");
	private static final class_2960 field_19107 = new class_2960("textures/gui/server_selection.png");
	private final class_500 field_19108;
	private final List<class_4267.class_4270> field_19109 = Lists.<class_4267.class_4270>newArrayList();
	private final class_4267.class_504 field_19110 = new class_4267.class_4268();
	private final List<class_4267.class_4269> field_19111 = Lists.<class_4267.class_4269>newArrayList();

	public class_4267(class_500 arg, class_310 arg2, int i, int j, int k, int l, int m) {
		super(arg2, i, j, k, l, m);
		this.field_19108 = arg;
	}

	private void method_20131() {
		this.method_1902();
		this.field_19109.forEach(this::method_1901);
		this.method_1901(this.field_19110);
		this.field_19111.forEach(this::method_1901);
	}

	public void method_20122(class_4267.class_504 arg) {
		super.method_20062(arg);
		if (this.method_20064() instanceof class_4267.class_4270) {
			class_333.field_2054.method_19788(new class_2588("narrator.select", ((class_4267.class_4270)this.method_20064()).field_19120.field_3752).getString());
		}
	}

	@Override
	protected void method_20069(int i) {
		int j = this.children().indexOf(this.method_20064());
		int k = class_3532.method_15340(j + i, 0, this.method_20073() - 1);
		class_4267.class_504 lv = (class_4267.class_504)this.children().get(k);
		super.method_20062(lv);
		if (lv instanceof class_4267.class_4268) {
			if (i <= 0 || k != this.method_20073() - 1) {
				if (i >= 0 || k != 0) {
					this.method_20069(i);
				}
			}
		} else {
			this.method_20072(lv);
			this.field_19108.method_20121();
		}
	}

	public void method_20125(class_641 arg) {
		this.field_19109.clear();

		for (int i = 0; i < arg.method_2984(); i++) {
			this.field_19109.add(new class_4267.class_4270(this.field_19108, arg.method_2982(i)));
		}

		this.method_20131();
	}

	public void method_20126(List<class_1131> list) {
		this.field_19111.clear();

		for (class_1131 lv : list) {
			this.field_19111.add(new class_4267.class_4269(this.field_19108, lv));
		}

		this.method_20131();
	}

	@Override
	protected int method_20078() {
		return super.method_20078() + 30;
	}

	@Override
	public int method_20053() {
		return super.method_20053() + 85;
	}

	@Override
	protected boolean method_20080() {
		return this.field_19108.getFocused() == this;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4268 extends class_4267.class_504 {
		private final class_310 field_19112 = class_310.method_1551();

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			int p = j + m / 2 - 9 / 2;
			this.field_19112
				.field_1772
				.method_1729(
					class_1074.method_4662("lanServer.scanning"),
					(float)(this.field_19112.field_1755.width / 2 - this.field_19112.field_1772.method_1727(class_1074.method_4662("lanServer.scanning")) / 2),
					(float)p,
					16777215
				);
			String string;
			switch ((int)(class_156.method_658() / 300L % 4L)) {
				case 0:
				default:
					string = "O o o";
					break;
				case 1:
				case 3:
					string = "o O o";
					break;
				case 2:
					string = "o o O";
			}

			this.field_19112
				.field_1772
				.method_1729(string, (float)(this.field_19112.field_1755.width / 2 - this.field_19112.field_1772.method_1727(string) / 2), (float)(p + 9), 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4269 extends class_4267.class_504 {
		private final class_500 field_19115;
		protected final class_310 field_19113;
		protected final class_1131 field_19114;
		private long field_19116;

		protected class_4269(class_500 arg, class_1131 arg2) {
			this.field_19115 = arg;
			this.field_19114 = arg2;
			this.field_19113 = class_310.method_1551();
		}

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.field_19113.field_1772.method_1729(class_1074.method_4662("lanServer.title"), (float)(k + 32 + 3), (float)(j + 1), 16777215);
			this.field_19113.field_1772.method_1729(this.field_19114.method_4813(), (float)(k + 32 + 3), (float)(j + 12), 8421504);
			if (this.field_19113.field_1690.field_1815) {
				this.field_19113.field_1772.method_1729(class_1074.method_4662("selectServer.hiddenAddress"), (float)(k + 32 + 3), (float)(j + 12 + 11), 3158064);
			} else {
				this.field_19113.field_1772.method_1729(this.field_19114.method_4812(), (float)(k + 32 + 3), (float)(j + 12 + 11), 3158064);
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			this.field_19115.method_2531(this);
			if (class_156.method_658() - this.field_19116 < 250L) {
				this.field_19115.method_2536();
			}

			this.field_19116 = class_156.method_658();
			return false;
		}

		public class_1131 method_20132() {
			return this.field_19114;
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_4270 extends class_4267.class_504 {
		private final class_500 field_19118;
		private final class_310 field_19119;
		private final class_642 field_19120;
		private final class_2960 field_19121;
		private String field_19122;
		private class_1043 field_19123;
		private long field_19124;

		protected class_4270(class_500 arg2, class_642 arg3) {
			this.field_19118 = arg2;
			this.field_19120 = arg3;
			this.field_19119 = class_310.method_1551();
			this.field_19121 = new class_2960("servers/" + Hashing.sha1().hashUnencodedChars(arg3.field_3761) + "/icon");
			this.field_19123 = (class_1043)this.field_19119.method_1531().method_4619(this.field_19121);
		}

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			if (!this.field_19120.field_3754) {
				this.field_19120.field_3754 = true;
				this.field_19120.field_3758 = -2L;
				this.field_19120.field_3757 = "";
				this.field_19120.field_3753 = "";
				class_4267.field_19105.submit(() -> {
					try {
						this.field_19118.method_2538().method_3003(this.field_19120);
					} catch (UnknownHostException var2) {
						this.field_19120.field_3758 = -1L;
						this.field_19120.field_3757 = class_124.field_1079 + class_1074.method_4662("multiplayer.status.cannot_resolve");
					} catch (Exception var3) {
						this.field_19120.field_3758 = -1L;
						this.field_19120.field_3757 = class_124.field_1079 + class_1074.method_4662("multiplayer.status.cannot_connect");
					}
				});
			}

			boolean bl2 = this.field_19120.field_3756 > class_155.method_16673().getProtocolVersion();
			boolean bl3 = this.field_19120.field_3756 < class_155.method_16673().getProtocolVersion();
			boolean bl4 = bl2 || bl3;
			this.field_19119.field_1772.method_1729(this.field_19120.field_3752, (float)(k + 32 + 3), (float)(j + 1), 16777215);
			List<String> list = this.field_19119.field_1772.method_1728(this.field_19120.field_3757, l - 32 - 2);

			for (int p = 0; p < Math.min(list.size(), 2); p++) {
				this.field_19119.field_1772.method_1729((String)list.get(p), (float)(k + 32 + 3), (float)(j + 12 + 9 * p), 8421504);
			}

			String string = bl4 ? class_124.field_1079 + this.field_19120.field_3760 : this.field_19120.field_3753;
			int q = this.field_19119.field_1772.method_1727(string);
			this.field_19119.field_1772.method_1729(string, (float)(k + l - q - 15 - 2), (float)(j + 1), 8421504);
			int r = 0;
			String string2 = null;
			int s;
			String string3;
			if (bl4) {
				s = 5;
				string3 = class_1074.method_4662(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
				string2 = this.field_19120.field_3762;
			} else if (this.field_19120.field_3754 && this.field_19120.field_3758 != -2L) {
				if (this.field_19120.field_3758 < 0L) {
					s = 5;
				} else if (this.field_19120.field_3758 < 150L) {
					s = 0;
				} else if (this.field_19120.field_3758 < 300L) {
					s = 1;
				} else if (this.field_19120.field_3758 < 600L) {
					s = 2;
				} else if (this.field_19120.field_3758 < 1000L) {
					s = 3;
				} else {
					s = 4;
				}

				if (this.field_19120.field_3758 < 0L) {
					string3 = class_1074.method_4662("multiplayer.status.no_connection");
				} else {
					string3 = this.field_19120.field_3758 + "ms";
					string2 = this.field_19120.field_3762;
				}
			} else {
				r = 1;
				s = (int)(class_156.method_658() / 100L + (long)(i * 2) & 7L);
				if (s > 4) {
					s = 8 - s;
				}

				string3 = class_1074.method_4662("multiplayer.status.pinging");
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_19119.method_1531().method_4618(class_332.GUI_ICONS_LOCATION);
			class_332.blit(k + l - 15, j, (float)(r * 10), (float)(176 + s * 8), 10, 8, 256.0F, 256.0F);
			if (this.field_19120.method_2991() != null && !this.field_19120.method_2991().equals(this.field_19122)) {
				this.field_19122 = this.field_19120.method_2991();
				this.method_20137();
				this.field_19118.method_2529().method_2987();
			}

			if (this.field_19123 != null) {
				this.method_20134(k, j, this.field_19121);
			} else {
				this.method_20134(k, j, class_4267.field_19106);
			}

			int t = n - k;
			int u = o - j;
			if (t >= l - 15 && t <= l - 5 && u >= 0 && u <= 8) {
				this.field_19118.method_2528(string3);
			} else if (t >= l - q - 15 - 2 && t <= l - 15 - 2 && u >= 0 && u <= 8) {
				this.field_19118.method_2528(string2);
			}

			if (this.field_19119.field_1690.field_1854 || bl) {
				this.field_19119.method_1531().method_4618(class_4267.field_19107);
				class_332.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int v = n - k;
				int w = o - j;
				if (this.method_20136()) {
					if (v < 32 && v > 16) {
						class_332.blit(k, j, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.blit(k, j, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (i > 0) {
					if (v < 16 && w < 16) {
						class_332.blit(k, j, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.blit(k, j, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (i < this.field_19118.method_2529().method_2984() - 1) {
					if (v < 16 && w > 16) {
						class_332.blit(k, j, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.blit(k, j, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		protected void method_20134(int i, int j, class_2960 arg) {
			this.field_19119.method_1531().method_4618(arg);
			GlStateManager.enableBlend();
			class_332.blit(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
			GlStateManager.disableBlend();
		}

		private boolean method_20136() {
			return true;
		}

		private void method_20137() {
			String string = this.field_19120.method_2991();
			if (string == null) {
				this.field_19119.method_1531().method_4615(this.field_19121);
				if (this.field_19123 != null && this.field_19123.method_4525() != null) {
					this.field_19123.method_4525().close();
				}

				this.field_19123 = null;
			} else {
				try {
					class_1011 lv = class_1011.method_15990(string);
					Validate.validState(lv.method_4307() == 64, "Must be 64 pixels wide");
					Validate.validState(lv.method_4323() == 64, "Must be 64 pixels high");
					if (this.field_19123 == null) {
						this.field_19123 = new class_1043(lv);
					} else {
						this.field_19123.method_4526(lv);
						this.field_19123.method_4524();
					}

					this.field_19119.method_1531().method_4616(this.field_19121, this.field_19123);
				} catch (Throwable var3) {
					class_4267.field_19104.error("Invalid icon for server {} ({})", this.field_19120.field_3752, this.field_19120.field_3761, var3);
					this.field_19120.method_2989(null);
				}
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			double f = d - (double)class_4267.this.method_20079();
			double g = e - (double)class_4267.this.method_20071(class_4267.this.children().indexOf(this));
			if (f <= 32.0) {
				if (f < 32.0 && f > 16.0 && this.method_20136()) {
					this.field_19118.method_2531(this);
					this.field_19118.method_2536();
					return true;
				}

				int j = this.field_19118.field_3043.children().indexOf(this);
				if (f < 16.0 && g < 16.0 && j > 0) {
					int k = class_437.hasShiftDown() ? 0 : j - 1;
					this.field_19118.method_2529().method_2985(j, k);
					if (this.field_19118.field_3043.method_20064() == this) {
						this.field_19118.method_2531(this);
					}

					this.field_19118.field_3043.method_20125(this.field_19118.method_2529());
					return true;
				}

				if (f < 16.0 && g > 16.0 && j < this.field_19118.method_2529().method_2984() - 1) {
					class_641 lv = this.field_19118.method_2529();
					int l = class_437.hasShiftDown() ? lv.method_2984() - 1 : j + 1;
					lv.method_2985(j, l);
					if (this.field_19118.field_3043.method_20064() == this) {
						this.field_19118.method_2531(this);
					}

					this.field_19118.field_3043.method_20125(lv);
					return true;
				}
			}

			this.field_19118.method_2531(this);
			if (class_156.method_658() - this.field_19124 < 250L) {
				this.field_19118.method_2536();
			}

			this.field_19124 = class_156.method_658();
			return false;
		}

		public class_642 method_20133() {
			return this.field_19120;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_504 extends class_350.class_351<class_4267.class_504> {
	}
}
