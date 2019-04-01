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

@Environment(EnvType.CLIENT)
public class class_442 extends class_437 {
	public static final class_751 field_17774 = new class_751(new class_2960("textures/gui/title/background/panorama"));
	private static final class_2960 field_17775 = new class_2960("textures/gui/title/background/panorama_overlay.png");
	private static final class_2960 field_19102 = new class_2960("textures/gui/accessibility.png");
	private final boolean field_17776;
	@Nullable
	private String field_2586;
	private class_4185 field_2602;
	private class_4185 field_2590;
	private final class_1799 field_19252 = new class_1799(class_1802.field_19174);
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
	private static final class_2960 field_2583 = new class_2960("textures/gui/title/minecraft.png");
	private static final class_2960 field_2594 = new class_2960("textures/gui/title/edition.png");
	private boolean field_2599;
	private class_437 field_2592;
	private int field_2584;
	private int field_2606;
	private final class_766 field_2585 = new class_766(field_17774);
	private boolean field_18222;
	private long field_17772;
	private int field_19251 = 0;

	public class_442() {
		this(false);
	}

	public class_442(boolean bl) {
		super(new class_2588("narrator.screen.title"));
		this.field_18222 = bl;
		this.field_17776 = (double)new Random().nextFloat() < 1.0E-4;
		this.field_2601 = "";
		if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
			this.field_2601 = class_1074.method_4662("title.oldgl1");
			this.field_2589 = class_1074.method_4662("title.oldgl2");
			this.field_2604 = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
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
		this.field_2602 = this.addButton(
			new class_4185(
				this.width / 2 - 100,
				j + 72 + 12,
				98,
				20,
				class_1074.method_4662("menu.options"),
				arg -> this.minecraft.method_1507(new class_429(this, this.minecraft.field_1690))
			)
		);
		this.addButton(new class_4185(this.width / 2 + 2, j + 72 + 12, 98, 20, class_1074.method_4662("menu.quit"), arg -> this.minecraft.method_20258()));
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
		synchronized (this.field_2603) {
			this.field_2598 = this.font.method_1727(this.field_2601);
			this.field_2600 = this.font.method_1727(this.field_2589);
			int k = Math.max(this.field_2598, this.field_2600);
			this.field_2597 = (this.width - k) / 2;
			this.field_2596 = j - 24;
			this.field_2595 = this.field_2597 + k;
			this.field_2593 = this.field_2596 + 24;
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
			new class_4185(this.width / 2 - 100, i, 200, 20, class_1074.method_4662("menu.select_episode"), arg -> this.minecraft.method_1507(new class_4285()))
		);
		this.field_2590 = this.addButton(new class_4185(this.width / 2 - 100, i + j * 1, 200, 20, class_1074.method_4662("menu.modem_play"), arg -> {
			if (this.field_19251++ == 0) {
				this.field_2590.setMessage(class_1074.method_4662("menu.modem_played1"));
			} else {
				this.field_2590.setMessage(class_1074.method_4662("menu.modem_played2"));
			}
		}));
	}

	private void method_2252() {
		RealmsBridge realmsBridge = new RealmsBridge();
		realmsBridge.switchToRealms(this);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && i == 12) {
			class_32 lv = this.minecraft.method_1586();
			lv.method_233("Demo_World");
			this.minecraft.method_1507(this);
		} else if (i == 12) {
			this.minecraft.method_1507(this);
		} else if (i == 13) {
			if (bl) {
				class_156.method_668().method_670(this.field_2604);
			}

			this.minecraft.method_1507(this);
		}
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
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.field_18222 ? (float)class_3532.method_15386(class_3532.method_15363(g, 0.0F, 1.0F)) : 1.0F);
		blit(0, 0, 0.0F, 0.0F, 16, 128, this.width, this.height, 16.0F, 128.0F);
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

			GlStateManager.enableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(370.0F, 50.0F, 100.0F);
			GlStateManager.scalef(80.0F, 80.0F, 80.0F);
			long o = class_156.method_658();
			GlStateManager.rotatef((float)(o / 10L % 360L), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(o / 15L % 360L), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef((float)(o / 20L % 360L), 0.0F, 0.0F, 1.0F);
			this.minecraft.method_1480().method_4009(this.field_19252, class_809.class_811.field_4315);
			GlStateManager.popMatrix();
			this.minecraft.method_1531().method_4618(field_2594);
			blit(l + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
			if (this.field_2586 != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
				GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
				float p = 1.8F - class_3532.method_15379(class_3532.method_15374((float)(class_156.method_658() % 1000L) / 1000.0F * (float) (Math.PI * 2)) * 0.1F);
				float var14 = p * 100.0F / (float)(this.font.method_1727(this.field_2586) + 32);
				GlStateManager.scalef(var14, var14, var14);
				this.drawCenteredString(this.font, this.field_2586, 0, -8, 16776960 | n);
				GlStateManager.popMatrix();
			}

			String string = "Minecraft 3D Shareware v1.34";
			this.drawString(this.font, string, 2, this.height - 10, 16777215 | n);
			this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.field_2606, this.height - 10, 16777215 | n);
			if (i > this.field_2606 && i < this.field_2606 + this.field_2584 && j > this.height - 10 && j < this.height) {
				fill(this.field_2606, this.height - 1, this.field_2606 + this.field_2584, this.height, 16777215 | n);
			}

			if (this.field_2601 != null && !this.field_2601.isEmpty()) {
				fill(this.field_2597 - 2, this.field_2596 - 2, this.field_2595 + 2, this.field_2593 - 1, 1428160512);
				this.drawString(this.font, this.field_2601, this.field_2597, this.field_2596, 16777215 | n);
				this.drawString(this.font, this.field_2589, (this.width - this.field_2600) / 2, this.field_2596 + 12, 16777215 | n);
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
		} else {
			synchronized (this.field_2603) {
				if (!this.field_2601.isEmpty()
					&& !class_3544.method_15438(this.field_2604)
					&& d >= (double)this.field_2597
					&& d <= (double)this.field_2595
					&& e >= (double)this.field_2596
					&& e <= (double)this.field_2593) {
					class_407 lv = new class_407(this, this.field_2604, 13, true);
					this.minecraft.method_1507(lv);
					return true;
				}
			}

			if (this.method_2253() && this.field_2592.mouseClicked(d, e, i)) {
				return true;
			} else {
				if (d > (double)this.field_2606 && d < (double)(this.field_2606 + this.field_2584) && e > (double)(this.height - 10) && e < (double)this.height) {
					this.minecraft.method_1507(new class_445(false, Runnables.doNothing()));
				}

				return false;
			}
		}
	}

	@Override
	public void removed() {
		if (this.field_2592 != null) {
			this.field_2592.removed();
		}
	}
}
