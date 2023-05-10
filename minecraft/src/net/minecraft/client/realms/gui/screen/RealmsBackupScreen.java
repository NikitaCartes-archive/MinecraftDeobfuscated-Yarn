package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.DownloadTask;
import net.minecraft.client.realms.task.RestoreTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Identifier PLUS_ICON = new Identifier("realms", "textures/gui/realms/plus_icon.png");
	static final Identifier RESTORE_ICON = new Identifier("realms", "textures/gui/realms/restore_icon.png");
	static final Text RESTORE_TEXT = Text.translatable("mco.backup.button.restore");
	static final Text CHANGES_TOOLTIP = Text.translatable("mco.backup.changes.tooltip");
	private static final Text BACKUPS_TEXT = Text.translatable("mco.configure.world.backup");
	private static final Text NO_BACKUPS_TEXT = Text.translatable("mco.backup.nobackups");
	private final RealmsConfigureWorldScreen parent;
	List<Backup> backups = Collections.emptyList();
	RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
	int selectedBackup = -1;
	private final int slotId;
	private ButtonWidget downloadButton;
	private ButtonWidget restoreButton;
	private ButtonWidget changesButton;
	Boolean noBackups = false;
	final RealmsServer serverData;
	private static final String UPLOADED = "uploaded";

	public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
		super(Text.translatable("mco.configure.world.backup"));
		this.parent = parent;
		this.serverData = serverData;
		this.slotId = slotId;
	}

	@Override
	public void init() {
		this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
		(new Thread("Realms-fetch-backups") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.create();

				try {
					List<Backup> list = realmsClient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
					RealmsBackupScreen.this.client.execute(() -> {
						RealmsBackupScreen.this.backups = list;
						RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
						RealmsBackupScreen.this.backupObjectSelectionList.clear();

						for (Backup backup : RealmsBackupScreen.this.backups) {
							RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
						}
					});
				} catch (RealmsServiceException var3) {
					RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)var3);
				}
			}
		}).start();
		this.downloadButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.backup.button.download"), button -> this.downloadClicked())
				.dimensions(this.width - 135, row(1), 120, 20)
				.build()
		);
		this.restoreButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.backup.button.restore"), button -> this.restoreClicked(this.selectedBackup))
				.dimensions(this.width - 135, row(3), 120, 20)
				.build()
		);
		this.changesButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.backup.changes.tooltip"), button -> {
			this.client.setScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
			this.selectedBackup = -1;
		}).dimensions(this.width - 135, row(5), 120, 20).build());
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width - 100, this.height - 35, 85, 20).build()
		);
		this.addSelectableChild(this.backupObjectSelectionList);
		this.focusOn(this.backupObjectSelectionList);
		this.updateButtonStates();
	}

	void updateButtonStates() {
		this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
		this.changesButton.visible = this.shouldChangesButtonBeVisible();
	}

	private boolean shouldChangesButtonBeVisible() {
		return this.selectedBackup == -1 ? false : !((Backup)this.backups.get(this.selectedBackup)).changeList.isEmpty();
	}

	private boolean shouldRestoreButtonBeVisible() {
		return this.selectedBackup == -1 ? false : !this.serverData.expired;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	void restoreClicked(int selectedBackup) {
		if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
			this.selectedBackup = selectedBackup;
			Date date = ((Backup)this.backups.get(selectedBackup)).lastModifiedDate;
			String string = DateFormat.getDateTimeInstance(3, 3).format(date);
			Text text = RealmsUtil.convertToAgePresentation(date);
			Text text2 = Text.translatable("mco.configure.world.restore.question.line1", string, text);
			Text text3 = Text.translatable("mco.configure.world.restore.question.line2");
			this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
				if (confirmed) {
					this.restore();
				} else {
					this.selectedBackup = -1;
					this.client.setScreen(this);
				}
			}, RealmsLongConfirmationScreen.Type.WARNING, text2, text3, true));
		}
	}

	private void downloadClicked() {
		Text text = Text.translatable("mco.configure.world.restore.download.question.line1");
		Text text2 = Text.translatable("mco.configure.world.restore.download.question.line2");
		this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
			if (confirmed) {
				this.downloadWorldData();
			} else {
				this.client.setScreen(this);
			}
		}, RealmsLongConfirmationScreen.Type.INFO, text, text2, true));
	}

	private void downloadWorldData() {
		this.client
			.setScreen(
				new RealmsLongRunningMcoTaskScreen(
					this.parent.getNewScreen(),
					new DownloadTask(
						this.serverData.id,
						this.slotId,
						this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")",
						this
					)
				)
			);
	}

	private void restore() {
		Backup backup = (Backup)this.backups.get(this.selectedBackup);
		this.selectedBackup = -1;
		this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.parent)));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		this.backupObjectSelectionList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 16777215);
		context.drawText(this.textRenderer, BACKUPS_TEXT, (this.width - 150) / 2 - 90, 20, 10526880, false);
		if (this.noBackups) {
			context.drawText(this.textRenderer, NO_BACKUPS_TEXT, 20, this.height / 2 - 10, 16777215, false);
		}

		this.downloadButton.active = !this.noBackups;
		super.render(context, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class BackupObjectSelectionList extends RealmsObjectSelectionList<RealmsBackupScreen.BackupObjectSelectionListEntry> {
		public BackupObjectSelectionList() {
			super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
		}

		public void addEntry(Backup backup) {
			this.addEntry(RealmsBackupScreen.this.new BackupObjectSelectionListEntry(backup));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width * 0.93);
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * 36;
		}

		@Override
		public void renderBackground(DrawContext context) {
			RealmsBackupScreen.this.renderBackground(context);
		}

		@Override
		public int getScrollbarPositionX() {
			return this.width - 5;
		}

		@Override
		public void setSelected(int index) {
			super.setSelected(index);
			this.selectInviteListItem(index);
		}

		public void selectInviteListItem(int item) {
			RealmsBackupScreen.this.selectedBackup = item;
			RealmsBackupScreen.this.updateButtonStates();
		}

		public void setSelected(@Nullable RealmsBackupScreen.BackupObjectSelectionListEntry backupObjectSelectionListEntry) {
			super.setSelected(backupObjectSelectionListEntry);
			RealmsBackupScreen.this.selectedBackup = this.children().indexOf(backupObjectSelectionListEntry);
			RealmsBackupScreen.this.updateButtonStates();
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsBackupScreen.BackupObjectSelectionListEntry> {
		private static final int field_44525 = 2;
		private static final int field_44526 = 7;
		private final Backup mBackup;
		private final List<ClickableWidget> buttons = new ArrayList();
		@Nullable
		private TexturedButtonWidget restoreButton;
		@Nullable
		private TexturedButtonWidget infoButton;

		public BackupObjectSelectionListEntry(Backup backup) {
			this.mBackup = backup;
			this.updateChangeList(backup);
			if (!backup.changeList.isEmpty()) {
				this.addInfoButton();
			}

			if (!RealmsBackupScreen.this.serverData.expired) {
				this.addRestoreButton();
			}
		}

		private void updateChangeList(Backup backup) {
			int i = RealmsBackupScreen.this.backups.indexOf(backup);
			if (i != RealmsBackupScreen.this.backups.size() - 1) {
				Backup backup2 = (Backup)RealmsBackupScreen.this.backups.get(i + 1);

				for (String string : backup.metadata.keySet()) {
					if (!string.contains("uploaded") && backup2.metadata.containsKey(string)) {
						if (!((String)backup.metadata.get(string)).equals(backup2.metadata.get(string))) {
							this.addChange(string);
						}
					} else {
						this.addChange(string);
					}
				}
			}
		}

		private void addChange(String metadataKey) {
			if (metadataKey.contains("uploaded")) {
				String string = DateFormat.getDateTimeInstance(3, 3).format(this.mBackup.lastModifiedDate);
				this.mBackup.changeList.put(metadataKey, string);
				this.mBackup.setUploadedVersion(true);
			} else {
				this.mBackup.changeList.put(metadataKey, (String)this.mBackup.metadata.get(metadataKey));
			}
		}

		private void addInfoButton() {
			int i = 9;
			int j = 9;
			int k = RealmsBackupScreen.this.backupObjectSelectionList.getRowRight() - 9 - 28;
			int l = RealmsBackupScreen.this.backupObjectSelectionList.getRowTop(RealmsBackupScreen.this.backups.indexOf(this.mBackup)) + 2;
			this.infoButton = new TexturedButtonWidget(
				k,
				l,
				9,
				9,
				0,
				0,
				9,
				RealmsBackupScreen.PLUS_ICON,
				9,
				18,
				button -> RealmsBackupScreen.this.client.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.mBackup))
			);
			this.infoButton.setTooltip(Tooltip.of(RealmsBackupScreen.CHANGES_TOOLTIP));
			this.buttons.add(this.infoButton);
		}

		private void addRestoreButton() {
			int i = 17;
			int j = 10;
			int k = RealmsBackupScreen.this.backupObjectSelectionList.getRowRight() - 17 - 7;
			int l = RealmsBackupScreen.this.backupObjectSelectionList.getRowTop(RealmsBackupScreen.this.backups.indexOf(this.mBackup)) + 2;
			this.restoreButton = new TexturedButtonWidget(
				k,
				l,
				17,
				10,
				0,
				0,
				10,
				RealmsBackupScreen.RESTORE_ICON,
				17,
				20,
				button -> RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.backups.indexOf(this.mBackup))
			);
			this.restoreButton.setTooltip(Tooltip.of(RealmsBackupScreen.RESTORE_TEXT));
			this.buttons.add(this.restoreButton);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.restoreButton != null) {
				this.restoreButton.mouseClicked(mouseX, mouseY, button);
			}

			if (this.infoButton != null) {
				this.infoButton.mouseClicked(mouseX, mouseY, button);
			}

			return true;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = this.mBackup.isUploadedVersion() ? -8388737 : 16777215;
			context.drawText(
				RealmsBackupScreen.this.textRenderer,
				Text.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentation(this.mBackup.lastModifiedDate)),
				x,
				y + 1,
				i,
				false
			);
			context.drawText(RealmsBackupScreen.this.textRenderer, this.getMediumDatePresentation(this.mBackup.lastModifiedDate), x, y + 12, 5000268, false);
			this.buttons.forEach(button -> {
				button.setY(y + 2);
				button.render(context, mouseX, mouseY, tickDelta);
			});
		}

		private String getMediumDatePresentation(Date lastModifiedDate) {
			return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
		}

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.mBackup.lastModifiedDate.toString());
		}
	}
}
