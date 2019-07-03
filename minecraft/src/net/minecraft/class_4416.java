package net.minecraft;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4416 extends RealmsScreen {
	private static final Logger field_20049 = LogManager.getLogger();
	private final class_4410 field_20050;
	private final long field_20051;
	private final int field_20052;
	private RealmsButton field_20053;
	private final DateFormat field_20054 = new SimpleDateFormat();
	private List<RealmsLevelSummary> field_20055 = new ArrayList();
	private int field_20056 = -1;
	private class_4416.class_4418 field_20057;
	private String field_20058;
	private String field_20059;
	private final String[] field_20060 = new String[4];
	private RealmsLabel field_20061;
	private RealmsLabel field_20062;
	private RealmsLabel field_20063;

	public class_4416(long l, int i, class_4410 arg) {
		this.field_20050 = arg;
		this.field_20051 = l;
		this.field_20052 = i;
	}

	private void method_21396() throws Exception {
		RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource = this.getLevelStorageSource();
		this.field_20055 = realmsAnvilLevelStorageSource.getLevelList();
		Collections.sort(this.field_20055);

		for (RealmsLevelSummary realmsLevelSummary : this.field_20055) {
			this.field_20057.method_21412(realmsLevelSummary);
		}
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_20057 = new class_4416.class_4418();

		try {
			this.method_21396();
		} catch (Exception var2) {
			field_20049.error("Couldn't load level list", (Throwable)var2);
			Realms.setScreen(new class_4394("Unable to load worlds", var2.getMessage(), this.field_20050));
			return;
		}

		this.field_20058 = getLocalizedString("selectWorld.world");
		this.field_20059 = getLocalizedString("selectWorld.conversion");
		this.field_20060[Realms.survivalId()] = getLocalizedString("gameMode.survival");
		this.field_20060[Realms.creativeId()] = getLocalizedString("gameMode.creative");
		this.field_20060[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
		this.field_20060[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
		this.addWidget(this.field_20057);
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4416.this.field_20050);
			}
		});
		this.buttonsAdd(this.field_20053 = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")) {
			@Override
			public void onPress() {
				class_4416.this.method_21401();
			}
		});
		this.field_20053.active(this.field_20056 >= 0 && this.field_20056 < this.field_20055.size());
		this.addWidget(this.field_20061 = new RealmsLabel(getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
		this.addWidget(
			this.field_20062 = new RealmsLabel(getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, class_4359.method_21072(-1), 10526880)
		);
		if (this.field_20055.isEmpty()) {
			this.addWidget(this.field_20063 = new RealmsLabel(getLocalizedString("mco.upload.select.world.none"), this.width() / 2, this.height() / 2 - 20, 16777215));
		} else {
			this.field_20063 = null;
		}

		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	private void method_21401() {
		if (this.field_20056 != -1 && !((RealmsLevelSummary)this.field_20055.get(this.field_20056)).isHardcore()) {
			RealmsLevelSummary realmsLevelSummary = (RealmsLevelSummary)this.field_20055.get(this.field_20056);
			Realms.setScreen(new class_4427(this.field_20051, this.field_20052, this.field_20050, realmsLevelSummary));
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_20057.render(i, j, f);
		this.field_20061.render(this);
		this.field_20062.render(this);
		if (this.field_20063 != null) {
			this.field_20063.render(this);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_20050);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	private String method_21400(RealmsLevelSummary realmsLevelSummary) {
		return this.field_20060[realmsLevelSummary.getGameMode()];
	}

	private String method_21404(RealmsLevelSummary realmsLevelSummary) {
		return this.field_20054.format(new Date(realmsLevelSummary.getLastPlayed()));
	}

	@Environment(EnvType.CLIENT)
	class class_4417 extends RealmListEntry {
		final RealmsLevelSummary field_20066;

		public class_4417(RealmsLevelSummary realmsLevelSummary) {
			this.field_20066 = realmsLevelSummary;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_21411(this.field_20066, i, k, j, m, Tezzelator.instance, n, o);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			class_4416.this.field_20057.selectItem(class_4416.this.field_20055.indexOf(this.field_20066));
			return true;
		}

		protected void method_21411(RealmsLevelSummary realmsLevelSummary, int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
			String string = realmsLevelSummary.getLevelName();
			if (string == null || string.isEmpty()) {
				string = class_4416.this.field_20058 + " " + (i + 1);
			}

			String string2 = realmsLevelSummary.getLevelId();
			string2 = string2 + " (" + class_4416.this.method_21404(realmsLevelSummary);
			string2 = string2 + ")";
			String string3 = "";
			if (realmsLevelSummary.isRequiresConversion()) {
				string3 = class_4416.this.field_20059 + " " + string3;
			} else {
				string3 = class_4416.this.method_21400(realmsLevelSummary);
				if (realmsLevelSummary.isHardcore()) {
					string3 = class_4357.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + class_4357.RESET;
				}

				if (realmsLevelSummary.hasCheats()) {
					string3 = string3 + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
				}
			}

			class_4416.this.drawString(string, j + 2, k + 1, 16777215);
			class_4416.this.drawString(string2, j + 2, k + 12, 8421504);
			class_4416.this.drawString(string3, j + 2, k + 12 + 10, 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4418 extends RealmsObjectSelectionList {
		public class_4418() {
			super(class_4416.this.width(), class_4416.this.height(), class_4359.method_21072(0), class_4416.this.height() - 40, 36);
		}

		public void method_21412(RealmsLevelSummary realmsLevelSummary) {
			this.addEntry(class_4416.this.new class_4417(realmsLevelSummary));
		}

		@Override
		public int getItemCount() {
			return class_4416.this.field_20055.size();
		}

		@Override
		public int getMaxPosition() {
			return class_4416.this.field_20055.size() * 36;
		}

		@Override
		public boolean isFocused() {
			return class_4416.this.isFocused(this);
		}

		@Override
		public void renderBackground() {
			class_4416.this.renderBackground();
		}

		@Override
		public void selectItem(int i) {
			this.setSelected(i);
			if (i != -1) {
				RealmsLevelSummary realmsLevelSummary = (RealmsLevelSummary)class_4416.this.field_20055.get(i);
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, class_4416.this.field_20055.size());
				String string2 = Realms.joinNarrations(
					Arrays.asList(
						realmsLevelSummary.getLevelName(), class_4416.this.method_21404(realmsLevelSummary), class_4416.this.method_21400(realmsLevelSummary), string
					)
				);
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
			}

			class_4416.this.field_20056 = i;
			class_4416.this.field_20053
				.active(
					class_4416.this.field_20056 >= 0
						&& class_4416.this.field_20056 < this.getItemCount()
						&& !((RealmsLevelSummary)class_4416.this.field_20055.get(class_4416.this.field_20056)).isHardcore()
				);
		}
	}
}
