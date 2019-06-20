package net.minecraft;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.CLIENT)
public class class_442 extends class_437 {
	public static final class_751 field_17774 = new class_751(new class_2960("textures/gui/title/background/panorama"));
	private static final class_2960 field_17775 = new class_2960("textures/gui/title/background/panorama_overlay.png");
	private static final class_2960 field_19102 = new class_2960("textures/gui/accessibility.png");
	private final boolean field_17776;
	@Nullable
	private String field_2586;
	private class_4185 field_2590;
	@Nullable
	private class_442.class_4308 field_19361;
	private static final class_2960 field_2583 = new class_2960("textures/gui/title/minecraft.png");
	private static final class_2960 field_2594 = new class_2960("textures/gui/title/edition.png");
	private boolean field_2599;
	private class_437 field_2592;
	private int field_2584;
	private int field_2606;
	private final class_766 field_2585 = new class_766(field_17774);
	private final boolean field_18222;
	private long field_17772;

	public class_442() {
		this(false);
	}

	public class_442(boolean bl) {
		super(new class_2588("narrator.screen.title"));
		this.field_18222 = bl;
		this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.field_19361 = new class_442.class_4308(
				new class_2588("title.oldgl.eol.line1").method_10854(class_124.field_1061).method_10854(class_124.field_1067),
				new class_2588("title.oldgl.eol.line2").method_10854(class_124.field_1061).method_10854(class_124.field_1067),
				"https://help.mojang.com/customer/portal/articles/325948?ref=game"
			);
		}
	}

	private boolean method_2253() {
		return this.minecraft.field_1690.field_1830 && this.field_2592 != null;
	}

	@Override
	public void tick() {
		if (this.method_2253()) {
			this.field_2592.tick();
		}
	}

	public static CompletableFuture<Void> method_18105(class_1060 arg, Executor executor) {
		return CompletableFuture.allOf(
			arg.method_18168(field_2583, executor),
			arg.method_18168(field_2594, executor),
			arg.method_18168(field_17775, executor),
			field_17774.method_18143(arg, executor)
		);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		if (this.field_2586 == null) {
			this.field_2586 = this.minecraft.method_18095().method_18174();
		}

		this.field_2584 = this.font.method_1727("Copyright Mojang AB. Do not distribute!");
		this.field_2606 = this.width - this.field_2584 - 2;
		int i = 24;
		int j = this.height / 4 + 48;
		if (this.minecraft.method_1530()) {
			this.method_2251(j, 24);
		} else {
			this.method_2249(j, 24);
		}

		this.addButton(
			new class_344(
				this.width / 2 - 124,
				j + 72 + 12,
				20,
				20,
				0,
				106,
				20,
				class_4185.WIDGETS_LOCATION,
				256,
				256,
				arg -> this.minecraft.method_1507(new class_426(this, this.minecraft.field_1690, this.minecraft.method_1526())),
				class_1074.method_4662("narrator.button.language")
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 100,
				j + 72 + 12,
				98,
				20,
				class_1074.method_4662("menu.options"),
				arg -> this.minecraft.method_1507(new class_429(this, this.minecraft.field_1690))
			)
		);
		this.addButton(new class_4185(this.width / 2 + 2, j + 72 + 12, 98, 20, class_1074.method_4662("menu.quit"), arg -> this.minecraft.method_1592()));
		this.addButton(
			new class_344(
				this.width / 2 + 104,
				j + 72 + 12,
				20,
				20,
				0,
				0,
				20,
				field_19102,
				32,
				64,
				arg -> this.minecraft.method_1507(new class_4189(this, this.minecraft.field_1690)),
				class_1074.method_4662("narrator.button.accessibility")
			)
		);
		if (this.field_19361 != null) {
			this.field_19361.method_20715(j);
		}

		this.minecraft.method_1537(false);
		if (this.minecraft.field_1690.field_1830 && !this.field_2599) {
			RealmsBridge realmsBridge = new RealmsBridge();
			this.field_2592 = realmsBridge.getNotificationScreen(this);
			this.field_2599 = true;
		}

		if (this.method_2253()) {
			this.field_2592.init(this.minecraft, this.width, this.height);
		}
	}

	private void method_2249(int i, int j) {
		this.addButton(
			new class_4185(this.width / 2 - 100, i, 200, 20, class_1074.method_4662("menu.singleplayer"), arg -> this.minecraft.method_1507(new class_526(this)))
		);
		this.addButton(
			new class_4185(this.width / 2 - 100, i + j * 1, 200, 20, class_1074.method_4662("menu.multiplayer"), arg -> this.minecraft.method_1507(new class_500(this)))
		);
		this.addButton(new class_4185(this.width / 2 - 100, i + j * 2, 200, 20, class_1074.method_4662("menu.online"), arg -> this.method_2252()));
	}

	private void method_2251(int i, int j) {
		this.addButton(
			new class_4185(
				this.width / 2 - 100,
				i,
				200,
				20,
				class_1074.method_4662("menu.playdemo"),
				arg -> this.minecraft.method_1559("Demo_World", "Demo_World", MinecraftServer.field_17704)
			)
		);
		this.field_2590 = this.addButton(
			new class_4185(
				this.width / 2 - 100,
				i + j * 1,
				200,
				20,
				class_1074.method_4662("menu.resetdemo"),
				arg -> {
					class_32 lvx = this.minecraft.method_1586();
					class_31 lv2x = lvx.method_231("Demo_World");
					if (lv2x != null) {
						this.minecraft
							.method_1507(
								new class_410(
									this::method_20375,
									new class_2588("selectWorld.deleteQuestion"),
									new class_2588("selectWorld.deleteWarning", lv2x.method_150()),
									class_1074.method_4662("selectWorld.deleteButton"),
									class_1074.method_4662("gui.cancel")
								)
							);
					}
				}
			)
		);
		class_32 lv = this.minecraft.method_1586();
		class_31 lv2 = lv.method_231("Demo_World");
		if (lv2 == null) {
			this.field_2590.active = false;
		}
	}

	private void method_2252() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.field_17772 == 0L && this.field_18222) {
			this.field_17772 = class_156.method_658();
		}

		float g = this.field_18222 ? (float)(class_156.method_658() - this.field_17772) / 1000.0F : 1.0F;
		fill(0, 0, this.width, this.height, -1);
		this.field_2585.method_3317(f, class_3532.method_15363(g, 0.0F, 1.0F));
		int k = 274;
		int l = this.width / 2 - 137;
		int m = 30;
		this.minecraft.method_1531().method_4618(field_17775);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.field_18222 ? (float)class_3532.method_15386(class_3532.method_15363(g, 0.0F, 1.0F)) : 1.0F);
		blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float h = this.field_18222 ? class_3532.method_15363(g - 1.0F, 0.0F, 1.0F) : 1.0F;
		int n = class_3532.method_15386(h * 255.0F) << 24;
		if ((n & -67108864) != 0) {
			this.minecraft.method_1531().method_4618(field_2583);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, h);
			if (this.field_17776) {
				this.blit(l + 0, 30, 0, 0, 99, 44);
				this.blit(l + 99, 30, 129, 0, 27, 44);
				this.blit(l + 99 + 26, 30, 126, 0, 3, 44);
				this.blit(l + 99 + 26 + 3, 30, 99, 0, 26, 44);
				this.blit(l + 155, 30, 0, 45, 155, 44);
			} else {
				this.blit(l + 0, 30, 0, 0, 155, 44);
				this.blit(l + 155, 30, 0, 45, 155, 44);
			}

			this.minecraft.method_1531().method_4618(field_2594);
			blit(l + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
			if (this.field_2586 != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
				GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
				float o = 1.8F - class_3532.method_15379(class_3532.method_15374((float)(class_156.method_658() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
				o = o * 100.0F / (float)(this.font.method_1727(this.field_2586) + 32);
				GlStateManager.scalef(o, o, o);
				this.drawCenteredString(this.font, this.field_2586, 0, -8, 16776960 | n);
				GlStateManager.popMatrix();
			}

			String string = "Minecraft " + class_155.method_16673().getName();
			if (this.minecraft.method_1530()) {
				string = string + " Demo";
			} else {
				string = string + ("release".equalsIgnoreCase(this.minecraft.method_1547()) ? "" : "/" + this.minecraft.method_1547());
			}

			this.drawString(this.font, string, 2, this.height - 10, 16777215 | n);
			this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.field_2606, this.height - 10, 16777215 | n);
			if (i > this.field_2606 && i < this.field_2606 + this.field_2584 && j > this.height - 10 && j < this.height) {
				fill(this.field_2606, this.height - 1, this.field_2606 + this.field_2584, this.height, 16777215 | n);
			}

			if (this.field_19361 != null) {
				this.field_19361.method_20717(n);
			}

			for (class_339 lv : this.buttons) {
				lv.setAlpha(h);
			}

			super.render(i, j, f);
			if (this.method_2253() && h >= 1.0F) {
				this.field_2592.render(i, j, f);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else if (this.field_19361 != null && this.field_19361.method_20714(d, e)) {
			return true;
		} else if (this.method_2253() && this.field_2592.mouseClicked(d, e, i)) {
			return true;
		} else {
			if (d > (double)this.field_2606 && d < (double)(this.field_2606 + this.field_2584) && e > (double)(this.height - 10) && e < (double)this.height) {
				this.minecraft.method_1507(new class_445(false, Runnables.doNothing()));
			}

			return false;
		}
	}

	@Override
	public void removed() {
		if (this.field_2592 != null) {
			this.field_2592.removed();
		}
	}

	private void method_20375(boolean bl) {
		if (bl) {
			class_32 lv = this.minecraft.method_1586();
			lv.method_233("Demo_World");
		}

		this.minecraft.method_1507(this);
	}

	@Environment(EnvType.CLIENT)
	class class_4308 {
		private int field_19363;
		private int field_19364;
		private int field_19365;
		private int field_19366;
		private int field_19367;
		private final class_2561 field_19368;
		private final class_2561 field_19369;
		private final String field_19370;

		public class_4308(class_2561 arg2, class_2561 arg3, String string) {
			this.field_19368 = arg2;
			this.field_19369 = arg3;
			this.field_19370 = string;
		}

		public void method_20715(int i) {
			int j = class_442.this.font.method_1727(this.field_19368.getString());
			this.field_19363 = class_442.this.font.method_1727(this.field_19369.getString());
			int k = Math.max(j, this.field_19363);
			this.field_19364 = (class_442.this.width - k) / 2;
			this.field_19365 = i - 24;
			this.field_19366 = this.field_19364 + k;
			this.field_19367 = this.field_19365 + 24;
		}

		public void method_20717(int i) {
			class_332.fill(this.field_19364 - 2, this.field_19365 - 2, this.field_19366 + 2, this.field_19367 - 1, 1428160512);
			class_442.this.drawString(class_442.this.font, this.field_19368.method_10863(), this.field_19364, this.field_19365, 16777215 | i);
			class_442.this.drawString(
				class_442.this.font, this.field_19369.method_10863(), (class_442.this.width - this.field_19363) / 2, this.field_19365 + 12, 16777215 | i
			);
		}

		public boolean method_20714(double d, double e) {
			if (!class_3544.method_15438(this.field_19370)
				&& d >= (double)this.field_19364
				&& d <= (double)this.field_19366
				&& e >= (double)this.field_19365
				&& e <= (double)this.field_19367) {
				class_442.this.minecraft.method_1507(new class_407(bl -> {
					if (bl) {
						class_156.method_668().method_670(this.field_19370);
					}

					class_442.this.minecraft.method_1507(class_442.this);
				}, this.field_19370, true));
				return true;
			} else {
				return false;
			}
		}
	}
}
