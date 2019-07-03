package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4384 extends RealmsScreen {
	private static final Logger field_19763 = LogManager.getLogger();
	private final RealmsScreen field_19764;
	private final class_4325 field_19765;
	private RealmsServer field_19766;
	private final long field_19767;
	private String field_19768 = getLocalizedString("mco.brokenworld.title");
	private final String field_19769 = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
	private int field_19770;
	private int field_19771;
	private final int field_19772 = 80;
	private final int field_19773 = 5;
	private static final List<Integer> field_19774 = Arrays.asList(1, 2, 3);
	private static final List<Integer> field_19775 = Arrays.asList(4, 5, 6);
	private static final List<Integer> field_19776 = Arrays.asList(7, 8, 9);
	private static final List<Integer> field_19777 = Arrays.asList(10, 11, 12);
	private final List<Integer> field_19778 = new ArrayList();
	private int field_19779;

	public class_4384(RealmsScreen realmsScreen, class_4325 arg, long l) {
		this.field_19764 = realmsScreen;
		this.field_19765 = arg;
		this.field_19767 = l;
	}

	public void method_21185(String string) {
		this.field_19768 = string;
	}

	@Override
	public void init() {
		this.field_19770 = this.width() / 2 - 150;
		this.field_19771 = this.width() / 2 + 190;
		this.buttonsAdd(new RealmsButton(0, this.field_19771 - 80 + 8, class_4359.method_21072(13) - 5, 70, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				class_4384.this.method_21194();
			}
		});
		if (this.field_19766 == null) {
			this.method_21181(this.field_19767);
		} else {
			this.method_21178();
		}

		this.setKeyboardHandlerSendRepeatsToGui(true);
	}

	public void method_21178() {
		for (Entry<Integer, RealmsWorldOptions> entry : this.field_19766.slots.entrySet()) {
			RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)entry.getValue();
			boolean bl = (Integer)entry.getKey() != this.field_19766.activeSlot || this.field_19766.worldType.equals(RealmsServer.class_4321.MINIGAME);
			RealmsButton realmsButton;
			if (bl) {
				realmsButton = new class_4384.class_4386(
					(Integer)field_19774.get((Integer)entry.getKey() - 1), this.method_21179((Integer)entry.getKey()), getLocalizedString("mco.brokenworld.play")
				);
			} else {
				realmsButton = new class_4384.class_4385(
					(Integer)field_19776.get((Integer)entry.getKey() - 1), this.method_21179((Integer)entry.getKey()), getLocalizedString("mco.brokenworld.download")
				);
			}

			if (this.field_19778.contains(entry.getKey())) {
				realmsButton.active(false);
				realmsButton.setMessage(getLocalizedString("mco.brokenworld.downloaded"));
			}

			this.buttonsAdd(realmsButton);
			this.buttonsAdd(
				new RealmsButton(
					(Integer)field_19775.get((Integer)entry.getKey() - 1),
					this.method_21179((Integer)entry.getKey()),
					class_4359.method_21072(10),
					80,
					20,
					getLocalizedString("mco.brokenworld.reset")
				) {
					@Override
					public void onPress() {
						int i = class_4384.field_19775.indexOf(this.id()) + 1;
						class_4410 lv = new class_4410(class_4384.this, class_4384.this.field_19766, class_4384.this);
						if (i != class_4384.this.field_19766.activeSlot || class_4384.this.field_19766.worldType.equals(RealmsServer.class_4321.MINIGAME)) {
							lv.method_21379(i);
						}

						lv.method_21369(14);
						Realms.setScreen(lv);
					}
				}
			);
		}
	}

	@Override
	public void tick() {
		this.field_19779++;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.drawCenteredString(this.field_19768, this.width() / 2, 17, 16777215);
		String[] strings = this.field_19769.split("\\\\n");

		for (int k = 0; k < strings.length; k++) {
			this.drawCenteredString(strings[k], this.width() / 2, class_4359.method_21072(-1) + 3 + k * 12, 10526880);
		}

		if (this.field_19766 != null) {
			for (Entry<Integer, RealmsWorldOptions> entry : this.field_19766.slots.entrySet()) {
				if (((RealmsWorldOptions)entry.getValue()).templateImage != null && ((RealmsWorldOptions)entry.getValue()).templateId != -1L) {
					this.method_21180(
						this.method_21179((Integer)entry.getKey()),
						class_4359.method_21072(1) + 5,
						i,
						j,
						this.field_19766.activeSlot == (Integer)entry.getKey() && !this.method_21196(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						((RealmsWorldOptions)entry.getValue()).templateId,
						((RealmsWorldOptions)entry.getValue()).templateImage,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				} else {
					this.method_21180(
						this.method_21179((Integer)entry.getKey()),
						class_4359.method_21072(1) + 5,
						i,
						j,
						this.field_19766.activeSlot == (Integer)entry.getKey() && !this.method_21196(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						-1L,
						null,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				}
			}
		}
	}

	private int method_21179(int i) {
		return this.field_19770 + (i - 1) * 110;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.method_21194();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	private void method_21194() {
		Realms.setScreen(this.field_19764);
	}

	private void method_21181(long l) {
		(new Thread() {
			public void run() {
				class_4341 lv = class_4341.method_20989();

				try {
					class_4384.this.field_19766 = lv.method_20991(l);
					class_4384.this.method_21178();
				} catch (class_4355 var3) {
					class_4384.field_19763.error("Couldn't get own world");
					Realms.setScreen(new class_4394(var3.getMessage(), class_4384.this.field_19764));
				} catch (IOException var4) {
					class_4384.field_19763.error("Couldn't parse response getting own world");
				}
			}
		}).start();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (!bl) {
			Realms.setScreen(this);
		} else {
			if (i != 13 && i != 14) {
				if (field_19776.contains(i)) {
					this.method_21187(field_19776.indexOf(i) + 1);
				} else if (field_19777.contains(i)) {
					this.field_19778.add(field_19777.indexOf(i) + 1);
					this.childrenClear();
					this.method_21178();
				}
			} else {
				(new Thread() {
					public void run() {
						class_4341 lv = class_4341.method_20989();
						if (class_4384.this.field_19766.state.equals(RealmsServer.class_4320.CLOSED)) {
							class_4434.class_4437 lv2 = new class_4434.class_4437(class_4384.this.field_19766, class_4384.this, class_4384.this.field_19764, true);
							class_4398 lv3 = new class_4398(class_4384.this, lv2);
							lv3.method_21288();
							Realms.setScreen(lv3);
						} else {
							try {
								class_4384.this.field_19765.method_20902().method_20853(lv.method_20991(class_4384.this.field_19767), class_4384.this);
							} catch (class_4355 var4) {
								class_4384.field_19763.error("Couldn't get own world");
								Realms.setScreen(class_4384.this.field_19764);
							} catch (IOException var5) {
								class_4384.field_19763.error("Couldn't parse response getting own world");
								Realms.setScreen(class_4384.this.field_19764);
							}
						}
					}
				}).start();
			}
		}
	}

	private void method_21187(int i) {
		class_4341 lv = class_4341.method_20989();

		try {
			WorldDownload worldDownload = lv.method_21003(this.field_19766.id, i);
			class_4392 lv2 = new class_4392(this, worldDownload, this.field_19766.name + " (" + ((RealmsWorldOptions)this.field_19766.slots.get(i)).getSlotName(i) + ")");
			lv2.method_21254((Integer)field_19777.get(i - 1));
			Realms.setScreen(lv2);
		} catch (class_4355 var5) {
			field_19763.error("Couldn't download world data");
			Realms.setScreen(new class_4394(var5, this));
		}
	}

	private boolean method_21196() {
		return this.field_19766 != null && this.field_19766.worldType.equals(RealmsServer.class_4321.MINIGAME);
	}

	private void method_21180(int i, int j, int k, int l, boolean bl, String string, int m, long n, String string2, boolean bl2) {
		if (bl2) {
			bind("realms:textures/gui/realms/empty_frame.png");
		} else if (string2 != null && n != -1L) {
			class_4446.method_21560(String.valueOf(n), string2);
		} else if (m == 1) {
			bind("textures/gui/title/background/panorama_0.png");
		} else if (m == 2) {
			bind("textures/gui/title/background/panorama_2.png");
		} else if (m == 3) {
			bind("textures/gui/title/background/panorama_3.png");
		} else {
			class_4446.method_21560(String.valueOf(this.field_19766.minigameId), this.field_19766.minigameImage);
		}

		if (!bl) {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else if (bl) {
			float f = 0.9F + 0.1F * RealmsMth.cos((float)this.field_19779 * 0.2F);
			GlStateManager.color4f(f, f, f, 1.0F);
		}

		RealmsScreen.blit(i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		bind("realms:textures/gui/realms/slot_frame.png");
		if (bl) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(i, j, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(string, i + 40, j + 66, 16777215);
	}

	private void method_21190(int i) {
		class_4434.class_4443 lv = new class_4434.class_4443(this.field_19766.id, i, this, 13);
		class_4398 lv2 = new class_4398(this.field_19764, lv);
		lv2.method_21288();
		Realms.setScreen(lv2);
	}

	@Environment(EnvType.CLIENT)
	class class_4385 extends RealmsButton {
		public class_4385(int i, int j, String string) {
			super(i, j, class_4359.method_21072(8), 80, 20, string);
		}

		@Override
		public void onPress() {
			String string = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
			String string2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
			Realms.setScreen(new class_4396(class_4384.this, class_4396.class_4397.INFO, string, string2, true, this.id()));
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4386 extends RealmsButton {
		public class_4386(int i, int j, String string) {
			super(i, j, class_4359.method_21072(8), 80, 20, string);
		}

		@Override
		public void onPress() {
			int i = class_4384.field_19774.indexOf(this.id()) + 1;
			if (((RealmsWorldOptions)class_4384.this.field_19766.slots.get(i)).empty) {
				class_4410 lv = new class_4410(
					class_4384.this,
					class_4384.this.field_19766,
					class_4384.this,
					RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"),
					RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"),
					10526880,
					RealmsScreen.getLocalizedString("gui.cancel")
				);
				lv.method_21379(i);
				lv.method_21376(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
				lv.method_21369(14);
				Realms.setScreen(lv);
			} else {
				class_4384.this.method_21190(i);
			}
		}
	}
}
