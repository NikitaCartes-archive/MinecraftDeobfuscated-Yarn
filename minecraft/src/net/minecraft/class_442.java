package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsBridge;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class class_442 extends class_437 {
	private static final Random field_2588 = new Random();
	private final float field_2605;
	private String field_2586;
	private class_339 field_2602;
	private class_339 field_2590;
	private final Object field_2603 = new Object();
	public static final String field_2587 = "Please click " + class_124.field_1073 + "here" + class_124.field_1070 + " for more information.";
	private int field_2600;
	private int field_2598;
	private int field_2597;
	private int field_2596;
	private int field_2595;
	private int field_2593;
	private String field_2601;
	private String field_2589 = field_2587;
	private String field_2604;
	private static final class_2960 field_2591 = new class_2960("texts/splashes.txt");
	private static final class_2960 field_2583 = new class_2960("textures/gui/title/minecraft.png");
	private static final class_2960 field_2594 = new class_2960("textures/gui/title/edition.png");
	private boolean field_2599;
	private class_437 field_2592;
	private int field_2584;
	private int field_2606;
	private final class_766 field_2585 = new class_766(new class_751(new class_2960("textures/gui/title/background/panorama")));

	public class_442() {
		this.field_2586 = "missingno";
		class_3298 lv = null;

		try {
			List<String> list = Lists.<String>newArrayList();
			lv = class_310.method_1551().method_1478().method_14486(field_2591);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lv.method_14482(), StandardCharsets.UTF_8));

			String string;
			while ((string = bufferedReader.readLine()) != null) {
				string = string.trim();
				if (!string.isEmpty()) {
					list.add(string);
				}
			}

			if (!list.isEmpty()) {
				do {
					this.field_2586 = (String)list.get(field_2588.nextInt(list.size()));
				} while (this.field_2586.hashCode() == 125780783);
			}
		} catch (IOException var8) {
		} finally {
			IOUtils.closeQuietly(lv);
		}

		this.field_2605 = field_2588.nextFloat();
		this.field_2601 = "";
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.field_2601 = class_1074.method_4662("title.oldgl1");
			this.field_2589 = class_1074.method_4662("title.oldgl2");
			this.field_2604 = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean method_2253() {
		return class_310.method_1551().field_1690.method_1628(class_315.class_316.field_1953) && this.field_2592 != null;
	}

	@Override
	public void method_2225() {
		if (this.method_2253()) {
			this.field_2592.method_2225();
		}
	}

	@Override
	public boolean method_2222() {
		return false;
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	protected void method_2224() {
		this.field_2584 = this.field_2554.method_1727("Copyright Mojang AB. Do not distribute!");
		this.field_2606 = this.field_2561 - this.field_2584 - 2;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.field_2586 = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.field_2586 = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.field_2586 = "OOoooOOOoooo! Spooky!";
		}

		int i = 24;
		int j = this.field_2559 / 4 + 48;
		if (this.field_2563.method_1530()) {
			this.method_2251(j, 24);
		} else {
			this.method_2249(j, 24);
		}

		this.field_2602 = this.method_2219(new class_339(0, this.field_2561 / 2 - 100, j + 72 + 12, 98, 20, class_1074.method_4662("menu.options")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1507(new class_429(class_442.this, class_442.this.field_2563.field_1690));
			}
		});
		this.method_2219(new class_339(4, this.field_2561 / 2 + 2, j + 72 + 12, 98, 20, class_1074.method_4662("menu.quit")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1592();
			}
		});
		this.method_2219(new class_346(5, this.field_2561 / 2 - 124, j + 72 + 12) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1507(new class_426(class_442.this, class_442.this.field_2563.field_1690, class_442.this.field_2563.method_1526()));
			}
		});
		synchronized (this.field_2603) {
			this.field_2598 = this.field_2554.method_1727(this.field_2601);
			this.field_2600 = this.field_2554.method_1727(this.field_2589);
			int k = Math.max(this.field_2598, this.field_2600);
			this.field_2597 = (this.field_2561 - k) / 2;
			this.field_2596 = j - 24;
			this.field_2595 = this.field_2597 + k;
			this.field_2593 = this.field_2596 + 24;
		}

		this.field_2563.method_1537(false);
		if (class_310.method_1551().field_1690.method_1628(class_315.class_316.field_1953) && !this.field_2599) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.field_2592 = realmsBridge.getNotificationScreen(this);
			this.field_2599 = true;
		}

		if (this.method_2253()) {
			this.field_2592.method_2233(this.field_2563, this.field_2561, this.field_2559);
		}
	}

	private void method_2249(int i, int j) {
		this.method_2219(new class_339(1, this.field_2561 / 2 - 100, i, class_1074.method_4662("menu.singleplayer")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1507(new class_526(class_442.this));
			}
		});
		this.method_2219(new class_339(2, this.field_2561 / 2 - 100, i + j * 1, class_1074.method_4662("menu.multiplayer")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1507(new class_500(class_442.this));
			}
		});
		this.method_2219(new class_339(14, this.field_2561 / 2 - 100, i + j * 2, class_1074.method_4662("menu.online")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.method_2252();
			}
		});
	}

	private void method_2251(int i, int j) {
		this.method_2219(new class_339(11, this.field_2561 / 2 - 100, i, class_1074.method_4662("menu.playdemo")) {
			@Override
			public void method_1826(double d, double e) {
				class_442.this.field_2563.method_1559("Demo_World", "Demo_World", class_3199.field_13884);
			}
		});
		this.field_2590 = this.method_2219(
			new class_339(12, this.field_2561 / 2 - 100, i + j * 1, class_1074.method_4662("menu.resetdemo")) {
				@Override
				public void method_1826(double d, double e) {
					class_32 lv = class_442.this.field_2563.method_1586();
					class_31 lv2 = lv.method_238("Demo_World");
					if (lv2 != null) {
						class_442.this.field_2563
							.method_1507(
								new class_410(
									class_442.this,
									class_1074.method_4662("selectWorld.deleteQuestion"),
									class_1074.method_4662("selectWorld.deleteWarning", lv2.method_150()),
									class_1074.method_4662("selectWorld.deleteButton"),
									class_1074.method_4662("gui.cancel"),
									12
								)
							);
					}
				}
			}
		);
		class_32 lv = this.field_2563.method_1586();
		class_31 lv2 = lv.method_238("Demo_World");
		if (lv2 == null) {
			this.field_2590.field_2078 = false;
		}
	}

	private void method_2252() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && i == 12) {
			class_32 lv = this.field_2563.method_1586();
			lv.method_245();
			lv.method_233("Demo_World");
			this.field_2563.method_1507(this);
		} else if (i == 12) {
			this.field_2563.method_1507(this);
		} else if (i == 13) {
			if (bl) {
				class_156.method_668().method_670(this.field_2604);
			}

			this.field_2563.method_1507(this);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.field_2585.method_3317(f);
		int k = 274;
		int l = this.field_2561 / 2 - 137;
		int m = 30;
		this.field_2563.method_1531().method_4618(new class_2960("textures/gui/title/background/panorama_overlay.png"));
		method_1786(0, 0, 0.0F, 0.0F, 16, 128, this.field_2561, this.field_2559, 16.0F, 128.0F);
		this.field_2563.method_1531().method_4618(field_2583);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if ((double)this.field_2605 < 1.0E-4) {
			this.method_1788(l + 0, 30, 0, 0, 99, 44);
			this.method_1788(l + 99, 30, 129, 0, 27, 44);
			this.method_1788(l + 99 + 26, 30, 126, 0, 3, 44);
			this.method_1788(l + 99 + 26 + 3, 30, 99, 0, 26, 44);
			this.method_1788(l + 155, 30, 0, 45, 155, 44);
		} else {
			this.method_1788(l + 0, 30, 0, 0, 155, 44);
			this.method_1788(l + 155, 30, 0, 45, 155, 44);
		}

		this.field_2563.method_1531().method_4618(field_2594);
		method_1781(l + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)(this.field_2561 / 2 + 90), 70.0F, 0.0F);
		GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float g = 1.8F - class_3532.method_15379(class_3532.method_15374((float)(class_156.method_658() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
		g = g * 100.0F / (float)(this.field_2554.method_1727(this.field_2586) + 32);
		GlStateManager.scalef(g, g, g);
		this.method_1789(this.field_2554, this.field_2586, 0, -8, -256);
		GlStateManager.popMatrix();
		String string = "Minecraft " + class_155.method_16673().getName();
		if (this.field_2563.method_1530()) {
			string = string + " Demo";
		} else {
			string = string + ("release".equalsIgnoreCase(this.field_2563.method_1547()) ? "" : "/" + this.field_2563.method_1547());
		}

		this.method_1780(this.field_2554, string, 2, this.field_2559 - 10, -1);
		this.method_1780(this.field_2554, "Copyright Mojang AB. Do not distribute!", this.field_2606, this.field_2559 - 10, -1);
		if (i > this.field_2606 && i < this.field_2606 + this.field_2584 && j > this.field_2559 - 10 && j < this.field_2559) {
			method_1785(this.field_2606, this.field_2559 - 1, this.field_2606 + this.field_2584, this.field_2559, -1);
		}

		if (this.field_2601 != null && !this.field_2601.isEmpty()) {
			method_1785(this.field_2597 - 2, this.field_2596 - 2, this.field_2595 + 2, this.field_2593 - 1, 1428160512);
			this.method_1780(this.field_2554, this.field_2601, this.field_2597, this.field_2596, -1);
			this.method_1780(this.field_2554, this.field_2589, (this.field_2561 - this.field_2600) / 2, this.field_2596 + 12, -1);
		}

		super.method_2214(i, j, f);
		if (this.method_2253()) {
			this.field_2592.method_2214(i, j, f);
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (super.method_16807(d, e, i)) {
			return true;
		} else {
			synchronized (this.field_2603) {
				if (!this.field_2601.isEmpty()
					&& !class_3544.method_15438(this.field_2604)
					&& d >= (double)this.field_2597
					&& d <= (double)this.field_2595
					&& e >= (double)this.field_2596
					&& e <= (double)this.field_2593) {
					class_407 lv = new class_407(this, this.field_2604, 13, true);
					this.field_2563.method_1507(lv);
					return true;
				}
			}

			if (this.method_2253() && this.field_2592.method_16807(d, e, i)) {
				return true;
			} else {
				if (d > (double)this.field_2606 && d < (double)(this.field_2606 + this.field_2584) && e > (double)(this.field_2559 - 10) && e < (double)this.field_2559) {
					this.field_2563.method_1507(new class_445(false, Runnables.doNothing()));
				}

				return false;
			}
		}
	}

	@Override
	public void method_2234() {
		if (this.field_2592 != null) {
			this.field_2592.method_2234();
		}
	}
}
