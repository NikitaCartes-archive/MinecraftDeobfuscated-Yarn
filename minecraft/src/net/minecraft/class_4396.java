package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4396 extends RealmsScreen {
	private final class_4396.class_4397 field_19895;
	private final String field_19896;
	private final String field_19897;
	protected final RealmsConfirmResultListener field_19891;
	protected final String field_19892;
	protected final String field_19893;
	private final String field_19898;
	protected final int field_19894;
	private final boolean field_19899;

	public class_4396(RealmsConfirmResultListener realmsConfirmResultListener, class_4396.class_4397 arg, String string, String string2, boolean bl, int i) {
		this.field_19891 = realmsConfirmResultListener;
		this.field_19894 = i;
		this.field_19895 = arg;
		this.field_19896 = string;
		this.field_19897 = string2;
		this.field_19899 = bl;
		this.field_19892 = getLocalizedString("gui.yes");
		this.field_19893 = getLocalizedString("gui.no");
		this.field_19898 = getLocalizedString("mco.gui.ok");
	}

	@Override
	public void init() {
		Realms.narrateNow(this.field_19895.field_19906, this.field_19896, this.field_19897);
		if (this.field_19899) {
			this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, class_4359.method_21072(8), 100, 20, this.field_19892) {
				@Override
				public void onPress() {
					class_4396.this.field_19891.confirmResult(true, class_4396.this.field_19894);
				}
			});
			this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, class_4359.method_21072(8), 100, 20, this.field_19893) {
				@Override
				public void onPress() {
					class_4396.this.field_19891.confirmResult(false, class_4396.this.field_19894);
				}
			});
		} else {
			this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, class_4359.method_21072(8), 100, 20, this.field_19898) {
				@Override
				public void onPress() {
					class_4396.this.field_19891.confirmResult(true, class_4396.this.field_19894);
				}
			});
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.field_19891.confirmResult(false, this.field_19894);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.field_19895.field_19906, this.width() / 2, class_4359.method_21072(2), this.field_19895.field_19905);
		this.drawCenteredString(this.field_19896, this.width() / 2, class_4359.method_21072(4), 16777215);
		this.drawCenteredString(this.field_19897, this.width() / 2, class_4359.method_21072(6), 16777215);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4397 {
		WARNING("Warning!", 16711680),
		INFO("Info!", 8226750);

		public final int field_19905;
		public final String field_19906;

		private class_4397(String string2, int j) {
			this.field_19906 = string2;
			this.field_19905 = j;
		}
	}
}
