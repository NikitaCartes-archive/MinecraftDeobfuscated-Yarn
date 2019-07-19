package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextFormat;
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
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsResetWorldScreen lastScreen;
	private final long worldId;
	private final int slotId;
	private RealmsButton uploadButton;
	private final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private List<RealmsLevelSummary> levelList = new ArrayList();
	private int selectedWorld = -1;
	private RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
	private String worldLang;
	private String conversionLang;
	private final String[] gameModesLang = new String[4];
	private RealmsLabel titleLabel;
	private RealmsLabel subtitleLabel;
	private RealmsLabel field_20063;

	public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen) {
		this.lastScreen = lastScreen;
		this.worldId = worldId;
		this.slotId = slotId;
	}

	private void loadLevelList() throws Exception {
		RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource = this.getLevelStorageSource();
		this.levelList = realmsAnvilLevelStorageSource.getLevelList();
		Collections.sort(this.levelList);

		for (RealmsLevelSummary realmsLevelSummary : this.levelList) {
			this.worldSelectionList.addEntry(realmsLevelSummary);
		}
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

		try {
			this.loadLevelList();
		} catch (Exception var2) {
			LOGGER.error("Couldn't load level list", (Throwable)var2);
			Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", var2.getMessage(), this.lastScreen));
			return;
		}

		this.worldLang = getLocalizedString("selectWorld.world");
		this.conversionLang = getLocalizedString("selectWorld.conversion");
		this.gameModesLang[Realms.survivalId()] = getLocalizedString("gameMode.survival");
		this.gameModesLang[Realms.creativeId()] = getLocalizedString("gameMode.creative");
		this.gameModesLang[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
		this.gameModesLang[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
		this.addWidget(this.worldSelectionList);
		this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsSelectFileToUploadScreen.this.lastScreen);
			}
		});
		this.buttonsAdd(this.uploadButton = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")) {
			@Override
			public void onPress() {
				RealmsSelectFileToUploadScreen.this.upload();
			}
		});
		this.uploadButton.active(this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size());
		this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
		this.addWidget(
			this.subtitleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, RealmsConstants.row(-1), 10526880)
		);
		if (this.levelList.isEmpty()) {
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

	private void upload() {
		if (this.selectedWorld != -1 && !((RealmsLevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
			RealmsLevelSummary realmsLevelSummary = (RealmsLevelSummary)this.levelList.get(this.selectedWorld);
			Realms.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, realmsLevelSummary));
		}
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.worldSelectionList.render(xm, ym, a);
		this.titleLabel.render(this);
		this.subtitleLabel.render(this);
		if (this.field_20063 != null) {
			this.field_20063.render(this);
		}

		super.render(xm, ym, a);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			Realms.setScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	private String method_21400(RealmsLevelSummary realmsLevelSummary) {
		return this.gameModesLang[realmsLevelSummary.getGameMode()];
	}

	private String method_21404(RealmsLevelSummary realmsLevelSummary) {
		return this.DATE_FORMAT.format(new Date(realmsLevelSummary.getLastPlayed()));
	}

	@Environment(EnvType.CLIENT)
	class WorldListEntry extends RealmListEntry {
		final RealmsLevelSummary levelSummary;

		public WorldListEntry(RealmsLevelSummary levelSummary) {
			this.levelSummary = levelSummary;
		}

		@Override
		public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float a) {
			this.renderItem(this.levelSummary, index, rowLeft, rowTop, rowHeight, Tezzelator.instance, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double x, double y, int buttonNum) {
			RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
			return true;
		}

		protected void renderItem(RealmsLevelSummary levelSummary, int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
			String string = levelSummary.getLevelName();
			if (string == null || string.isEmpty()) {
				string = RealmsSelectFileToUploadScreen.this.worldLang + " " + (i + 1);
			}

			String string2 = levelSummary.getLevelId();
			string2 = string2 + " (" + RealmsSelectFileToUploadScreen.this.method_21404(levelSummary);
			string2 = string2 + ")";
			String string3 = "";
			if (levelSummary.isRequiresConversion()) {
				string3 = RealmsSelectFileToUploadScreen.this.conversionLang + " " + string3;
			} else {
				string3 = RealmsSelectFileToUploadScreen.this.method_21400(levelSummary);
				if (levelSummary.isHardcore()) {
					string3 = TextFormat.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + TextFormat.RESET;
				}

				if (levelSummary.hasCheats()) {
					string3 = string3 + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
				}
			}

			RealmsSelectFileToUploadScreen.this.drawString(string, x + 2, y + 1, 16777215);
			RealmsSelectFileToUploadScreen.this.drawString(string2, x + 2, y + 12, 8421504);
			RealmsSelectFileToUploadScreen.this.drawString(string3, x + 2, y + 12 + 10, 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldSelectionList extends RealmsObjectSelectionList {
		public WorldSelectionList() {
			super(
				RealmsSelectFileToUploadScreen.this.width(),
				RealmsSelectFileToUploadScreen.this.height(),
				RealmsConstants.row(0),
				RealmsSelectFileToUploadScreen.this.height() - 40,
				36
			);
		}

		public void addEntry(RealmsLevelSummary levelSummary) {
			this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(levelSummary));
		}

		@Override
		public int getItemCount() {
			return RealmsSelectFileToUploadScreen.this.levelList.size();
		}

		@Override
		public int getMaxPosition() {
			return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectFileToUploadScreen.this.isFocused(this);
		}

		@Override
		public void renderBackground() {
			RealmsSelectFileToUploadScreen.this.renderBackground();
		}

		@Override
		public void selectItem(int item) {
			this.setSelected(item);
			if (item != -1) {
				RealmsLevelSummary realmsLevelSummary = (RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(item);
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", item + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
				String string2 = Realms.joinNarrations(
					Arrays.asList(
						realmsLevelSummary.getLevelName(),
						RealmsSelectFileToUploadScreen.this.method_21404(realmsLevelSummary),
						RealmsSelectFileToUploadScreen.this.method_21400(realmsLevelSummary),
						string
					)
				);
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
			}

			RealmsSelectFileToUploadScreen.this.selectedWorld = item;
			RealmsSelectFileToUploadScreen.this.uploadButton
				.active(
					RealmsSelectFileToUploadScreen.this.selectedWorld >= 0
						&& RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount()
						&& !((RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore()
				);
		}
	}
}
