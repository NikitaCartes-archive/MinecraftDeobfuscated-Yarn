package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final RealmsResetWorldScreen lastScreen;
	private final long worldId;
	private final int slotId;
	private ButtonWidget uploadButton;
	private List<LevelSummary> levelList = Lists.<LevelSummary>newArrayList();
	private int selectedWorld = -1;
	private RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
	private String worldLang;
	private String conversionLang;
	private RealmsLabel titleLabel;
	private RealmsLabel subtitleLabel;
	private RealmsLabel field_20063;
	private final Runnable field_22717;

	public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, Runnable runnable) {
		this.lastScreen = lastScreen;
		this.worldId = worldId;
		this.slotId = slotId;
		this.field_22717 = runnable;
	}

	private void loadLevelList() throws Exception {
		this.levelList = (List<LevelSummary>)this.client.getLevelStorage().getLevelList().stream().sorted((levelSummaryx, levelSummary2) -> {
			if (levelSummaryx.getLastPlayed() < levelSummary2.getLastPlayed()) {
				return 1;
			} else {
				return levelSummaryx.getLastPlayed() > levelSummary2.getLastPlayed() ? -1 : levelSummaryx.getName().compareTo(levelSummary2.getName());
			}
		}).collect(Collectors.toList());

		for (LevelSummary levelSummary : this.levelList) {
			this.worldSelectionList.addEntry(levelSummary);
		}
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

		try {
			this.loadLevelList();
		} catch (Exception var2) {
			LOGGER.error("Couldn't load level list", (Throwable)var2);
			this.client.openScreen(new RealmsGenericErrorScreen("Unable to load worlds", var2.getMessage(), this.lastScreen));
			return;
		}

		this.worldLang = I18n.translate("selectWorld.world");
		this.conversionLang = I18n.translate("selectWorld.conversion");
		this.addChild(this.worldSelectionList);
		this.uploadButton = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 32, 153, 20, I18n.translate("mco.upload.button.name"), buttonWidget -> this.upload())
		);
		this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
		this.addButton(
			new ButtonWidget(this.width / 2 + 6, this.height - 32, 153, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen))
		);
		this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.upload.select.world.title"), this.width / 2, 13, 16777215));
		this.subtitleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
		if (this.levelList.isEmpty()) {
			this.field_20063 = this.addChild(new RealmsLabel(I18n.translate("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
		} else {
			this.field_20063 = null;
		}

		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void upload() {
		if (this.selectedWorld != -1 && !((LevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
			LevelSummary levelSummary = (LevelSummary)this.levelList.get(this.selectedWorld);
			this.client.openScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, levelSummary, this.field_22717));
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.worldSelectionList.render(mouseX, mouseY, delta);
		this.titleLabel.render(this);
		this.subtitleLabel.render(this);
		if (this.field_20063 != null) {
			this.field_20063.render(this);
		}

		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private static String method_21400(LevelSummary levelSummary) {
		return levelSummary.getGameMode().getTranslatableName().getString();
	}

	private static String method_21404(LevelSummary levelSummary) {
		return DATE_FORMAT.format(new Date(levelSummary.getLastPlayed()));
	}

	@Environment(EnvType.CLIENT)
	class WorldListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectFileToUploadScreen.WorldListEntry> {
		private final LevelSummary field_22718;

		public WorldListEntry(LevelSummary levelSummary) {
			this.field_22718 = levelSummary;
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.renderItem(this.field_22718, index, x, y);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.field_22718));
			return true;
		}

		protected void renderItem(LevelSummary levelSummary, int i, int x, int y) {
			String string = levelSummary.getDisplayName();
			if (string == null || string.isEmpty()) {
				string = RealmsSelectFileToUploadScreen.this.worldLang + " " + (i + 1);
			}

			String string2 = levelSummary.getName();
			string2 = string2 + " (" + RealmsSelectFileToUploadScreen.method_21404(levelSummary);
			string2 = string2 + ")";
			String string3 = "";
			if (levelSummary.requiresConversion()) {
				string3 = RealmsSelectFileToUploadScreen.this.conversionLang + " " + string3;
			} else {
				string3 = RealmsSelectFileToUploadScreen.method_21400(levelSummary);
				if (levelSummary.isHardcore()) {
					string3 = Formatting.DARK_RED + I18n.translate("mco.upload.hardcore") + Formatting.RESET;
				}

				if (levelSummary.hasCheats()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
				}
			}

			RealmsSelectFileToUploadScreen.this.textRenderer.draw(string, (float)(x + 2), (float)(y + 1), 16777215);
			RealmsSelectFileToUploadScreen.this.textRenderer.draw(string2, (float)(x + 2), (float)(y + 12), 8421504);
			RealmsSelectFileToUploadScreen.this.textRenderer.draw(string3, (float)(x + 2), (float)(y + 12 + 10), 8421504);
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.WorldListEntry> {
		public WorldSelectionList() {
			super(
				RealmsSelectFileToUploadScreen.this.width,
				RealmsSelectFileToUploadScreen.this.height,
				RealmsSelectFileToUploadScreen.row(0),
				RealmsSelectFileToUploadScreen.this.height - 40,
				36
			);
		}

		public void addEntry(LevelSummary levelSummary) {
			this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(levelSummary));
		}

		@Override
		public int getMaxPosition() {
			return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectFileToUploadScreen.this.getFocused() == this;
		}

		@Override
		public void renderBackground() {
			RealmsSelectFileToUploadScreen.this.renderBackground();
		}

		@Override
		public void setSelected(int index) {
			this.setSelectedItem(index);
			if (index != -1) {
				LevelSummary levelSummary = (LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(index);
				String string = I18n.translate("narrator.select.list.position", index + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
				String string2 = Realms.joinNarrations(
					Arrays.asList(
						levelSummary.getDisplayName(),
						RealmsSelectFileToUploadScreen.method_21404(levelSummary),
						RealmsSelectFileToUploadScreen.method_21400(levelSummary),
						string
					)
				);
				Realms.narrateNow(I18n.translate("narrator.select", string2));
			}
		}

		public void setSelected(@Nullable RealmsSelectFileToUploadScreen.WorldListEntry worldListEntry) {
			super.setSelected(worldListEntry);
			RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
			RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0
				&& RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount()
				&& !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
		}
	}
}
