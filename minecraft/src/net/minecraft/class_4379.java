package net.minecraft;

import com.mojang.realmsclient.dto.Backup;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

@Environment(EnvType.CLIENT)
public class class_4379 extends RealmsScreen {
	private final RealmsScreen field_19734;
	private final int field_19735 = 0;
	private final Backup field_19736;
	private final List<String> field_19737 = new ArrayList();
	private class_4379.class_4380 field_19738;
	String[] field_19732 = new String[]{
		getLocalizedString("options.difficulty.peaceful"),
		getLocalizedString("options.difficulty.easy"),
		getLocalizedString("options.difficulty.normal"),
		getLocalizedString("options.difficulty.hard")
	};
	String[] field_19733 = new String[]{
		getLocalizedString("selectWorld.gameMode.survival"),
		getLocalizedString("selectWorld.gameMode.creative"),
		getLocalizedString("selectWorld.gameMode.adventure")
	};

	public class_4379(RealmsScreen realmsScreen, Backup backup) {
		this.field_19734 = realmsScreen;
		this.field_19736 = backup;
		if (backup.changeList != null) {
			for (Entry<String, String> entry : backup.changeList.entrySet()) {
				this.field_19737.add(entry.getKey());
			}
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4379.this.field_19734);
			}
		});
		this.field_19738 = new class_4379.class_4380();
		this.addWidget(this.field_19738);
		this.focusOn(this.field_19738);
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_19734);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
		this.field_19738.render(i, j, f);
		super.render(i, j, f);
	}

	private String method_21141(String string, String string2) {
		String string3 = string.toLowerCase(Locale.ROOT);
		if (string3.contains("game") && string3.contains("mode")) {
			return this.method_21143(string2);
		} else {
			return string3.contains("game") && string3.contains("difficulty") ? this.method_21140(string2) : string2;
		}
	}

	private String method_21140(String string) {
		try {
			return this.field_19732[Integer.parseInt(string)];
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	private String method_21143(String string) {
		try {
			return this.field_19733[Integer.parseInt(string)];
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4380 extends RealmsSimpleScrolledSelectionList {
		public class_4380() {
			super(class_4379.this.width(), class_4379.this.height(), 32, class_4379.this.height() - 64, 36);
		}

		@Override
		public int getItemCount() {
			return class_4379.this.field_19736.changeList.size();
		}

		@Override
		public boolean isSelectedItem(int i) {
			return false;
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public void renderBackground() {
		}

		@Override
		public void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
			String string = (String)class_4379.this.field_19737.get(i);
			class_4379.this.drawString(string, this.width() / 2 - 40, k, 10526880);
			String string2 = (String)class_4379.this.field_19736.changeList.get(string);
			class_4379.this.drawString(class_4379.this.method_21141(string, string2), this.width() / 2 - 40, k + 12, 16777215);
		}
	}
}
