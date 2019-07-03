package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ServerActivityList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4372 extends RealmsScreen {
	private static final Logger field_19694 = LogManager.getLogger();
	private final RealmsScreen field_19695;
	private final RealmsServer field_19696;
	private volatile List<class_4372.class_4375> field_19697 = new ArrayList();
	private class_4372.class_4378 field_19698;
	private int field_19699;
	private String field_19700;
	private volatile List<class_4372.class_4377> field_19701 = new ArrayList();
	private final List<class_4372.class_4376> field_19702 = Arrays.asList(
		new class_4372.class_4376(79, 243, 29),
		new class_4372.class_4376(243, 175, 29),
		new class_4372.class_4376(243, 29, 190),
		new class_4372.class_4376(29, 165, 243),
		new class_4372.class_4376(29, 243, 130),
		new class_4372.class_4376(243, 29, 64),
		new class_4372.class_4376(29, 74, 243)
	);
	private int field_19703;
	private long field_19704;
	private int field_19705;
	private Boolean field_19706 = false;
	private int field_19707;
	private int field_19708;
	private double field_19709;
	private double field_19710;
	private final int field_19711 = 0;

	public class_4372(RealmsScreen realmsScreen, RealmsServer realmsServer) {
		this.field_19695 = realmsScreen;
		this.field_19696 = realmsServer;
		this.method_21124();
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_19699 = this.width();
		this.field_19698 = new class_4372.class_4378();
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() - 30, 200, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4372.this.field_19695);
			}
		});
		this.addWidget(this.field_19698);
	}

	private class_4372.class_4376 method_21116() {
		if (this.field_19703 > this.field_19702.size() - 1) {
			this.field_19703 = 0;
		}

		return (class_4372.class_4376)this.field_19702.get(this.field_19703++);
	}

	private void method_21124() {
		(new Thread() {
			public void run() {
				class_4341 lv = class_4341.method_20989();

				try {
					ServerActivityList serverActivityList = lv.method_21002(class_4372.this.field_19696.id);
					class_4372.this.field_19697 = class_4372.this.method_21117(serverActivityList);
					List<class_4372.class_4377> list = new ArrayList();

					for (class_4372.class_4375 lv2 : class_4372.this.field_19697) {
						for (class_4372.class_4373 lv3 : lv2.field_19722) {
							String string = new SimpleDateFormat("dd/MM").format(new Date(lv3.field_19715));
							class_4372.class_4377 lv4 = new class_4372.class_4377(string, lv3.field_19715);
							if (!list.contains(lv4)) {
								list.add(lv4);
							}
						}
					}

					Collections.sort(list);

					for (class_4372.class_4375 lv2 : class_4372.this.field_19697) {
						for (class_4372.class_4373 lv3x : lv2.field_19722) {
							String string = new SimpleDateFormat("dd/MM").format(new Date(lv3x.field_19715));
							class_4372.class_4377 lv4 = new class_4372.class_4377(string, lv3x.field_19715);
							lv3x.field_19717 = list.indexOf(lv4) + 1;
						}
					}

					class_4372.this.field_19701 = list;
				} catch (class_4355 var10) {
					var10.printStackTrace();
				}
			}
		}).start();
	}

	private List<class_4372.class_4375> method_21117(ServerActivityList serverActivityList) {
		List<class_4372.class_4375> list = Lists.<class_4372.class_4375>newArrayList();
		this.field_19704 = serverActivityList.periodInMillis;
		long l = System.currentTimeMillis() - serverActivityList.periodInMillis;

		for (ServerActivity serverActivity : serverActivityList.serverActivities) {
			class_4372.class_4375 lv = this.method_21123(serverActivity.profileUuid, list);
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.setTimeInMillis(serverActivity.joinTime);
			Calendar calendar2 = Calendar.getInstance(TimeZone.getDefault());
			calendar2.setTimeInMillis(serverActivity.leaveTime);
			class_4372.class_4373 lv2 = new class_4372.class_4373(l, calendar.getTimeInMillis(), calendar2.getTimeInMillis());
			if (lv == null) {
				String string = "";

				try {
					string = class_4448.method_21568(serverActivity.profileUuid);
				} catch (Exception var13) {
					field_19694.error("Could not get name for " + serverActivity.profileUuid, (Throwable)var13);
					continue;
				}

				lv = new class_4372.class_4375(serverActivity.profileUuid, new ArrayList(), string, serverActivity.profileUuid);
				lv.field_19722.add(lv2);
				list.add(lv);
			} else {
				lv.field_19722.add(lv2);
			}
		}

		Collections.sort(list);

		for (class_4372.class_4375 lv3 : list) {
			lv3.field_19723 = this.method_21116();
			Collections.sort(lv3.field_19722);
		}

		this.field_19706 = list.size() == 0;
		return list;
	}

	private class_4372.class_4375 method_21123(String string, List<class_4372.class_4375> list) {
		for (class_4372.class_4375 lv : list) {
			if (lv.field_19721.equals(string)) {
				return lv;
			}
		}

		return null;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_19695);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_19700 = null;
		this.renderBackground();

		for (class_4372.class_4375 lv : this.field_19697) {
			int k = this.fontWidth(lv.field_19724) + 2;
			if (k > this.field_19705) {
				this.field_19705 = k + 10;
			}
		}

		int l = 25;
		this.field_19707 = this.field_19705 + 25;
		int m = this.field_19699 - this.field_19707 - 10;
		int k = this.field_19701.size() < 1 ? 1 : this.field_19701.size();
		this.field_19708 = m / k;
		this.field_19709 = (double)this.field_19708 / 24.0;
		this.field_19710 = this.field_19709 / 60.0;
		if (this.field_19698 != null) {
			this.field_19698.render(i, j, f);
		}

		if (this.field_19697 != null && this.field_19697.size() > 0) {
			Tezzelator tezzelator = Tezzelator.instance;
			GlStateManager.disableTexture();
			tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
			tezzelator.vertex((double)this.field_19707, (double)(this.height() - 40), 0.0).color(128, 128, 128, 255).endVertex();
			tezzelator.vertex((double)(this.field_19707 + 1), (double)(this.height() - 40), 0.0).color(128, 128, 128, 255).endVertex();
			tezzelator.vertex((double)(this.field_19707 + 1), 30.0, 0.0).color(128, 128, 128, 255).endVertex();
			tezzelator.vertex((double)this.field_19707, 30.0, 0.0).color(128, 128, 128, 255).endVertex();
			tezzelator.end();
			GlStateManager.enableTexture();

			for (class_4372.class_4377 lv2 : this.field_19701) {
				int n = this.field_19701.indexOf(lv2) + 1;
				this.drawString(
					lv2.field_19729,
					this.field_19707 + (n - 1) * this.field_19708 + (this.field_19708 - this.fontWidth(lv2.field_19729)) / 2 + 2,
					this.height() - 52,
					16777215
				);
				GlStateManager.disableTexture();
				tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
				tezzelator.vertex((double)(this.field_19707 + n * this.field_19708), (double)(this.height() - 40), 0.0).color(128, 128, 128, 255).endVertex();
				tezzelator.vertex((double)(this.field_19707 + n * this.field_19708 + 1), (double)(this.height() - 40), 0.0).color(128, 128, 128, 255).endVertex();
				tezzelator.vertex((double)(this.field_19707 + n * this.field_19708 + 1), 30.0, 0.0).color(128, 128, 128, 255).endVertex();
				tezzelator.vertex((double)(this.field_19707 + n * this.field_19708), 30.0, 0.0).color(128, 128, 128, 255).endVertex();
				tezzelator.end();
				GlStateManager.enableTexture();
			}
		}

		super.render(i, j, f);
		this.drawCenteredString(getLocalizedString("mco.activity.title"), this.width() / 2, 10, 16777215);
		if (this.field_19700 != null) {
			this.method_21122(this.field_19700, i, j);
		}

		if (this.field_19706) {
			this.drawCenteredString(
				getLocalizedString("mco.activity.noactivity", new Object[]{TimeUnit.DAYS.convert(this.field_19704, TimeUnit.MILLISECONDS)}),
				this.width() / 2,
				this.height() / 2 - 20,
				16777215
			);
		}
	}

	protected void method_21122(String string, int i, int j) {
		if (string != null) {
			int k = 0;
			int l = 0;

			for (String string2 : string.split("\n")) {
				int m = this.fontWidth(string2);
				if (m > l) {
					l = m;
				}
			}

			int n = i - l - 5;
			int o = j;
			if (n < 0) {
				n = i + 12;
			}

			for (String string3 : string.split("\n")) {
				this.fillGradient(n - 3, o - (k == 0 ? 3 : 0) + k, n + l + 3, o + 8 + 3 + k, -1073741824, -1073741824);
				this.fontDrawShadow(string3, n, o + k, -1);
				k += 10;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4373 implements Comparable<class_4372.class_4373> {
		long field_19714;
		long field_19715;
		long field_19716;
		int field_19717;

		private class_4373(long l, long m, long n) {
			this.field_19714 = l;
			this.field_19715 = m;
			this.field_19716 = n;
		}

		public int method_21133(class_4372.class_4373 arg) {
			return (int)(this.field_19715 - arg.field_19715);
		}

		public int method_21132() {
			String string = new SimpleDateFormat("HH").format(new Date(this.field_19715));
			return Integer.parseInt(string);
		}

		public int method_21134() {
			String string = new SimpleDateFormat("mm").format(new Date(this.field_19715));
			return Integer.parseInt(string);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4374 {
		double field_19718;
		double field_19719;
		String field_19720;

		private class_4374(double d, double e, String string) {
			this.field_19718 = d;
			this.field_19719 = e;
			this.field_19720 = string;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4375 implements Comparable<class_4372.class_4375> {
		String field_19721;
		List<class_4372.class_4373> field_19722;
		class_4372.class_4376 field_19723;
		String field_19724;
		String field_19725;

		public int method_21135(class_4372.class_4375 arg) {
			return this.field_19724.compareTo(arg.field_19724);
		}

		class_4375(String string, List<class_4372.class_4373> list, String string2, String string3) {
			this.field_19721 = string;
			this.field_19722 = list;
			this.field_19724 = string2;
			this.field_19725 = string3;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4376 {
		int field_19726;
		int field_19727;
		int field_19728;

		class_4376(int i, int j, int k) {
			this.field_19726 = i;
			this.field_19727 = j;
			this.field_19728 = k;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4377 implements Comparable<class_4372.class_4377> {
		String field_19729;
		Long field_19730;

		public int method_21136(class_4372.class_4377 arg) {
			return this.field_19730.compareTo(arg.field_19730);
		}

		class_4377(String string, Long long_) {
			this.field_19729 = string;
			this.field_19730 = long_;
		}

		public boolean equals(Object object) {
			if (!(object instanceof class_4372.class_4377)) {
				return false;
			} else {
				class_4372.class_4377 lv = (class_4372.class_4377)object;
				return this.field_19729.equals(lv.field_19729);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4378 extends RealmsScrolledSelectionList {
		public class_4378() {
			super(class_4372.this.width(), class_4372.this.height(), 30, class_4372.this.height() - 40, class_4372.this.fontLineHeight() + 1);
		}

		@Override
		public int getItemCount() {
			return class_4372.this.field_19697.size();
		}

		@Override
		public boolean isSelectedItem(int i) {
			return false;
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * (class_4372.this.fontLineHeight() + 1) + 15;
		}

		@Override
		public void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
			if (class_4372.this.field_19697 != null && class_4372.this.field_19697.size() > i) {
				class_4372.class_4375 lv = (class_4372.class_4375)class_4372.this.field_19697.get(i);
				class_4372.this.drawString(
					lv.field_19724, 20, k, ((class_4372.class_4375)class_4372.this.field_19697.get(i)).field_19725.equals(Realms.getUUID()) ? 8388479 : 16777215
				);
				int o = lv.field_19723.field_19726;
				int p = lv.field_19723.field_19727;
				int q = lv.field_19723.field_19728;
				GlStateManager.disableTexture();
				tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
				tezzelator.vertex((double)(class_4372.this.field_19707 - 8), (double)k + 6.5, 0.0).color(o, p, q, 255).endVertex();
				tezzelator.vertex((double)(class_4372.this.field_19707 - 3), (double)k + 6.5, 0.0).color(o, p, q, 255).endVertex();
				tezzelator.vertex((double)(class_4372.this.field_19707 - 3), (double)k + 1.5, 0.0).color(o, p, q, 255).endVertex();
				tezzelator.vertex((double)(class_4372.this.field_19707 - 8), (double)k + 1.5, 0.0).color(o, p, q, 255).endVertex();
				tezzelator.end();
				GlStateManager.enableTexture();
				List<class_4372.class_4374> list = new ArrayList();

				for (class_4372.class_4373 lv2 : lv.field_19722) {
					int r = lv2.method_21134();
					int s = lv2.method_21132();
					double d = class_4372.this.field_19710 * (double)TimeUnit.MINUTES.convert(lv2.field_19716 - lv2.field_19715, TimeUnit.MILLISECONDS);
					if (d < 3.0) {
						d = 3.0;
					}

					double e = (double)(class_4372.this.field_19707 + (class_4372.this.field_19708 * lv2.field_19717 - class_4372.this.field_19708))
						+ (double)s * class_4372.this.field_19709
						+ (double)r * class_4372.this.field_19710;
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
					Date date = new Date(lv2.field_19715);
					Date date2 = new Date(lv2.field_19716);
					int t = (int)Math.ceil((double)TimeUnit.SECONDS.convert(lv2.field_19716 - lv2.field_19715, TimeUnit.MILLISECONDS) / 60.0);
					if (t < 1) {
						t = 1;
					}

					String string = "[" + simpleDateFormat.format(date) + " - " + simpleDateFormat.format(date2) + "] " + t + (t > 1 ? " minutes" : " minute");
					boolean bl = false;

					for (class_4372.class_4374 lv3 : list) {
						if (lv3.field_19718 + lv3.field_19719 >= e - 0.5) {
							double f = lv3.field_19718 + lv3.field_19719 - e;
							double g = Math.max(0.0, e - (lv3.field_19718 + lv3.field_19719));
							lv3.field_19719 = lv3.field_19719 - Math.max(0.0, f) + d + g;
							lv3.field_19720 = lv3.field_19720 + "\n" + string;
							bl = true;
							break;
						}
					}

					if (!bl) {
						list.add(new class_4372.class_4374(e, d, string));
					}
				}

				for (class_4372.class_4374 lv4 : list) {
					GlStateManager.disableTexture();
					tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
					tezzelator.vertex(lv4.field_19718, (double)k + 6.5, 0.0).color(o, p, q, 255).endVertex();
					tezzelator.vertex(lv4.field_19718 + lv4.field_19719, (double)k + 6.5, 0.0).color(o, p, q, 255).endVertex();
					tezzelator.vertex(lv4.field_19718 + lv4.field_19719, (double)k + 1.5, 0.0).color(o, p, q, 255).endVertex();
					tezzelator.vertex(lv4.field_19718, (double)k + 1.5, 0.0).color(o, p, q, 255).endVertex();
					tezzelator.end();
					GlStateManager.enableTexture();
					if ((double)m >= lv4.field_19718 && (double)m <= lv4.field_19718 + lv4.field_19719 && (double)n >= (double)k + 1.5 && (double)n <= (double)k + 6.5) {
						class_4372.this.field_19700 = lv4.field_19720.trim();
					}
				}

				RealmsScreen.bind("realms:textures/gui/realms/user_icon.png");
				class_4446.method_21559(((class_4372.class_4375)class_4372.this.field_19697.get(i)).field_19725, () -> {
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					RealmsScreen.blit(10, k, 8.0F, 8.0F, 8, 8, 8, 8, 64, 64);
					RealmsScreen.blit(10, k, 40.0F, 8.0F, 8, 8, 8, 8, 64, 64);
				});
			}
		}

		@Override
		public int getScrollbarPosition() {
			return this.width() - 7;
		}
	}
}
