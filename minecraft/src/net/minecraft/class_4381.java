package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4381 extends RealmsScreen {
	private static final Logger field_19741 = LogManager.getLogger();
	private static int field_19742 = -1;
	private final class_4388 field_19743;
	private List<Backup> field_19744 = Collections.emptyList();
	private String field_19745;
	private class_4381.class_4382 field_19746;
	private int field_19747 = -1;
	private final int field_19748;
	private RealmsButton field_19749;
	private RealmsButton field_19750;
	private RealmsButton field_19751;
	private Boolean field_19752 = false;
	private final RealmsServer field_19753;
	private RealmsLabel field_19754;

	public class_4381(class_4388 arg, RealmsServer realmsServer, int i) {
		this.field_19743 = arg;
		this.field_19753 = realmsServer;
		this.field_19748 = i;
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_19746 = new class_4381.class_4382();
		if (field_19742 != -1) {
			this.field_19746.scroll(field_19742);
		}

		(new Thread("Realms-fetch-backups") {
			public void run() {
				class_4341 lv = class_4341.method_20989();

				try {
					List<Backup> list = lv.method_21016(class_4381.this.field_19753.id).backups;
					Realms.execute((Runnable)(() -> {
						class_4381.this.field_19744 = list;
						class_4381.this.field_19752 = class_4381.this.field_19744.isEmpty();
						class_4381.this.field_19746.clear();

						for (Backup backup : class_4381.this.field_19744) {
							class_4381.this.field_19746.method_21173(backup);
						}

						class_4381.this.method_21154();
					}));
				} catch (class_4355 var3) {
					class_4381.field_19741.error("Couldn't request backups", (Throwable)var3);
				}
			}
		}).start();
		this.method_21158();
	}

	private void method_21154() {
		if (this.field_19744.size() > 1) {
			for (int i = 0; i < this.field_19744.size() - 1; i++) {
				Backup backup = (Backup)this.field_19744.get(i);
				Backup backup2 = (Backup)this.field_19744.get(i + 1);
				if (!backup.metadata.isEmpty() && !backup2.metadata.isEmpty()) {
					for (String string : backup.metadata.keySet()) {
						if (!string.contains("Uploaded") && backup2.metadata.containsKey(string)) {
							if (!((String)backup.metadata.get(string)).equals(backup2.metadata.get(string))) {
								this.method_21147(backup, string);
							}
						} else {
							this.method_21147(backup, string);
						}
					}
				}
			}
		}
	}

	private void method_21147(Backup backup, String string) {
		if (string.contains("Uploaded")) {
			String string2 = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
			backup.changeList.put(string, string2);
			backup.setUploadedVersion(true);
		} else {
			backup.changeList.put(string, backup.metadata.get(string));
		}
	}

	private void method_21158() {
		this.buttonsAdd(
			this.field_19749 = new RealmsButton(2, this.width() - 135, class_4359.method_21072(1), 120, 20, getLocalizedString("mco.backup.button.download")) {
				@Override
				public void onPress() {
					class_4381.this.method_21166();
				}
			}
		);
		this.buttonsAdd(
			this.field_19750 = new RealmsButton(3, this.width() - 135, class_4359.method_21072(3), 120, 20, getLocalizedString("mco.backup.button.restore")) {
				@Override
				public void onPress() {
					class_4381.this.method_21155(class_4381.this.field_19747);
				}
			}
		);
		this.buttonsAdd(
			this.field_19751 = new RealmsButton(4, this.width() - 135, class_4359.method_21072(5), 120, 20, getLocalizedString("mco.backup.changes.tooltip")) {
				@Override
				public void onPress() {
					Realms.setScreen(new class_4379(class_4381.this, (Backup)class_4381.this.field_19744.get(class_4381.this.field_19747)));
					class_4381.this.field_19747 = -1;
				}
			}
		);
		this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4381.this.field_19743);
			}
		});
		this.addWidget(this.field_19746);
		this.addWidget(this.field_19754 = new RealmsLabel(getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 16777215));
		this.focusOn(this.field_19746);
		this.method_21160();
		this.narrateLabels();
	}

	private void method_21160() {
		this.field_19750.setVisible(this.method_21164());
		this.field_19751.setVisible(this.method_21162());
	}

	private boolean method_21162() {
		return this.field_19747 == -1 ? false : !((Backup)this.field_19744.get(this.field_19747)).changeList.isEmpty();
	}

	private boolean method_21164() {
		return this.field_19747 == -1 ? false : !this.field_19753.expired;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_19743);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void method_21155(int i) {
		if (i >= 0 && i < this.field_19744.size() && !this.field_19753.expired) {
			this.field_19747 = i;
			Date date = ((Backup)this.field_19744.get(i)).lastModifiedDate;
			String string = DateFormat.getDateTimeInstance(3, 3).format(date);
			String string2 = class_4448.method_21567(System.currentTimeMillis() - date.getTime());
			String string3 = getLocalizedString("mco.configure.world.restore.question.line1", new Object[]{string, string2});
			String string4 = getLocalizedString("mco.configure.world.restore.question.line2");
			Realms.setScreen(new class_4396(this, class_4396.class_4397.WARNING, string3, string4, true, 1));
		}
	}

	private void method_21166() {
		String string = getLocalizedString("mco.configure.world.restore.download.question.line1");
		String string2 = getLocalizedString("mco.configure.world.restore.download.question.line2");
		Realms.setScreen(new class_4396(this, class_4396.class_4397.INFO, string, string2, true, 2));
	}

	private void method_21168() {
		class_4434.class_4436 lv = new class_4434.class_4436(
			this.field_19753.id,
			this.field_19748,
			this.field_19753.name + " (" + ((RealmsWorldOptions)this.field_19753.slots.get(this.field_19753.activeSlot)).getSlotName(this.field_19753.activeSlot) + ")",
			this
		);
		class_4398 lv2 = new class_4398(this.field_19743.method_21219(), lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (bl && i == 1) {
			this.method_21170();
		} else if (i == 1) {
			this.field_19747 = -1;
			Realms.setScreen(this);
		} else if (bl && i == 2) {
			this.method_21168();
		} else {
			Realms.setScreen(this);
		}
	}

	private void method_21170() {
		Backup backup = (Backup)this.field_19744.get(this.field_19747);
		this.field_19747 = -1;
		class_4434.class_4441 lv = new class_4434.class_4441(backup, this.field_19753.id, this.field_19743);
		class_4398 lv2 = new class_4398(this.field_19743.method_21219(), lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_19745 = null;
		this.renderBackground();
		this.field_19746.render(i, j, f);
		this.field_19754.render(this);
		this.drawString(getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 10526880);
		if (this.field_19752) {
			this.drawString(getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 16777215);
		}

		this.field_19749.active(!this.field_19752);
		super.render(i, j, f);
		if (this.field_19745 != null) {
			this.method_21153(this.field_19745, i, j);
		}
	}

	protected void method_21153(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4382 extends RealmsObjectSelectionList {
		public class_4382() {
			super(class_4381.this.width() - 150, class_4381.this.height(), 32, class_4381.this.height() - 15, 36);
		}

		public void method_21173(Backup backup) {
			this.addEntry(class_4381.this.new class_4383(backup));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width() * 0.93);
		}

		@Override
		public boolean isFocused() {
			return class_4381.this.isFocused(this);
		}

		@Override
		public int getItemCount() {
			return class_4381.this.field_19744.size();
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public void renderBackground() {
			class_4381.this.renderBackground();
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i != 0) {
				return false;
			} else if (d < (double)this.getScrollbarPosition() && e >= (double)this.y0() && e <= (double)this.y1()) {
				int j = this.width() / 2 - 92;
				int k = this.width();
				int l = (int)Math.floor(e - (double)this.y0()) - this.headerHeight() + this.getScroll();
				int m = l / this.itemHeight();
				if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
					this.selectItem(m);
					this.itemClicked(l, m, d, e, this.width());
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		public int getScrollbarPosition() {
			return this.width() - 5;
		}

		@Override
		public void itemClicked(int i, int j, double d, double e, int k) {
			int l = this.width() - 35;
			int m = j * this.itemHeight() + 36 - this.getScroll();
			int n = l + 10;
			int o = m - 3;
			if (d >= (double)l && d <= (double)(l + 9) && e >= (double)m && e <= (double)(m + 9)) {
				if (!((Backup)class_4381.this.field_19744.get(j)).changeList.isEmpty()) {
					class_4381.this.field_19747 = -1;
					class_4381.field_19742 = this.getScroll();
					Realms.setScreen(new class_4379(class_4381.this, (Backup)class_4381.this.field_19744.get(j)));
				}
			} else if (d >= (double)n && d < (double)(n + 13) && e >= (double)o && e < (double)(o + 15)) {
				class_4381.field_19742 = this.getScroll();
				class_4381.this.method_21155(j);
			}
		}

		@Override
		public void selectItem(int i) {
			this.setSelected(i);
			if (i != -1) {
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((Backup)class_4381.this.field_19744.get(i)).lastModifiedDate.toString()));
			}

			this.method_21172(i);
		}

		public void method_21172(int i) {
			class_4381.this.field_19747 = i;
			class_4381.this.method_21160();
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4383 extends RealmListEntry {
		final Backup field_19761;

		public class_4383(Backup backup) {
			this.field_19761 = backup;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_21175(this.field_19761, k - 40, j, n, o);
		}

		private void method_21175(Backup backup, int i, int j, int k, int l) {
			int m = backup.isUploadedVersion() ? -8388737 : 16777215;
			class_4381.this.drawString("Backup (" + class_4448.method_21567(System.currentTimeMillis() - backup.lastModifiedDate.getTime()) + ")", i + 40, j + 1, m);
			class_4381.this.drawString(this.method_21176(backup.lastModifiedDate), i + 40, j + 12, 5000268);
			int n = class_4381.this.width() - 175;
			int o = -3;
			int p = n - 10;
			int q = 0;
			if (!class_4381.this.field_19753.expired) {
				this.method_21174(n, j + -3, k, l);
			}

			if (!backup.changeList.isEmpty()) {
				this.method_21177(p, j + 0, k, l);
			}
		}

		private String method_21176(Date date) {
			return DateFormat.getDateTimeInstance(3, 3).format(date);
		}

		private void method_21174(int i, int j, int k, int l) {
			boolean bl = k >= i && k <= i + 12 && l >= j && l <= j + 14 && l < class_4381.this.height() - 15 && l > 32;
			RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			RealmsScreen.blit(i * 2, j * 2, 0.0F, bl ? 28.0F : 0.0F, 23, 28, 23, 56);
			GlStateManager.popMatrix();
			if (bl) {
				class_4381.this.field_19745 = RealmsScreen.getLocalizedString("mco.backup.button.restore");
			}
		}

		private void method_21177(int i, int j, int k, int l) {
			boolean bl = k >= i && k <= i + 8 && l >= j && l <= j + 8 && l < class_4381.this.height() - 15 && l > 32;
			RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			RealmsScreen.blit(i * 2, j * 2, 0.0F, bl ? 15.0F : 0.0F, 15, 15, 15, 30);
			GlStateManager.popMatrix();
			if (bl) {
				class_4381.this.field_19745 = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
			}
		}
	}
}
