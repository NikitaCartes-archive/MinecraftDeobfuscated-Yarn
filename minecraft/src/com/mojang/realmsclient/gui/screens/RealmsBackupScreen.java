package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.DownloadTask;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RestoreTask;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_22686 = new Identifier("realms", "textures/gui/realms/plus_icon.png");
	private static final Identifier field_22687 = new Identifier("realms", "textures/gui/realms/restore_icon.png");
	private static int lastScrollPosition = -1;
	private final RealmsConfigureWorldScreen lastScreen;
	private List<Backup> backups = Collections.emptyList();
	private String toolTip;
	private RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
	private int selectedBackup = -1;
	private final int slotId;
	private ButtonWidget downloadButton;
	private ButtonWidget restoreButton;
	private ButtonWidget changesButton;
	private Boolean noBackups = false;
	private final RealmsServer serverData;
	private RealmsLabel titleLabel;

	public RealmsBackupScreen(RealmsConfigureWorldScreen lastscreen, RealmsServer serverData, int slotId) {
		this.lastScreen = lastscreen;
		this.serverData = serverData;
		this.slotId = slotId;
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
		if (lastScrollPosition != -1) {
			this.backupObjectSelectionList.setScrollAmount((double)lastScrollPosition);
		}

		(new Thread("Realms-fetch-backups") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.createRealmsClient();

				try {
					List<Backup> list = realmsClient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
					RealmsBackupScreen.this.client.execute(() -> {
						RealmsBackupScreen.this.backups = list;
						RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
						RealmsBackupScreen.this.backupObjectSelectionList.clear();

						for (Backup backup : RealmsBackupScreen.this.backups) {
							RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
						}

						RealmsBackupScreen.this.generateChangeList();
					});
				} catch (RealmsServiceException var3) {
					RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)var3);
				}
			}
		}).start();
		this.downloadButton = this.addButton(
			new ButtonWidget(this.width - 135, row(1), 120, 20, I18n.translate("mco.backup.button.download"), buttonWidget -> this.downloadClicked())
		);
		this.restoreButton = this.addButton(
			new ButtonWidget(this.width - 135, row(3), 120, 20, I18n.translate("mco.backup.button.restore"), buttonWidget -> this.restoreClicked(this.selectedBackup))
		);
		this.changesButton = this.addButton(new ButtonWidget(this.width - 135, row(5), 120, 20, I18n.translate("mco.backup.changes.tooltip"), buttonWidget -> {
			this.client.openScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
			this.selectedBackup = -1;
		}));
		this.addButton(
			new ButtonWidget(this.width - 100, this.height - 35, 85, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen))
		);
		this.addChild(this.backupObjectSelectionList);
		this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.backup"), this.width / 2, 12, 16777215));
		this.focusOn(this.backupObjectSelectionList);
		this.updateButtonStates();
		this.narrateLabels();
	}

	private void generateChangeList() {
		if (this.backups.size() > 1) {
			for (int i = 0; i < this.backups.size() - 1; i++) {
				Backup backup = (Backup)this.backups.get(i);
				Backup backup2 = (Backup)this.backups.get(i + 1);
				if (!backup.metadata.isEmpty() && !backup2.metadata.isEmpty()) {
					for (String string : backup.metadata.keySet()) {
						if (!string.contains("Uploaded") && backup2.metadata.containsKey(string)) {
							if (!((String)backup.metadata.get(string)).equals(backup2.metadata.get(string))) {
								this.addToChangeList(backup, string);
							}
						} else {
							this.addToChangeList(backup, string);
						}
					}
				}
			}
		}
	}

	private void addToChangeList(Backup backup, String key) {
		if (key.contains("Uploaded")) {
			String string = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
			backup.changeList.put(key, string);
			backup.setUploadedVersion(true);
		} else {
			backup.changeList.put(key, backup.metadata.get(key));
		}
	}

	private void updateButtonStates() {
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
		if (keyCode == 256) {
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void restoreClicked(int selectedBackup) {
		if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
			this.selectedBackup = selectedBackup;
			Date date = ((Backup)this.backups.get(selectedBackup)).lastModifiedDate;
			String string = DateFormat.getDateTimeInstance(3, 3).format(date);
			String string2 = RealmsUtil.method_25282(date);
			String string3 = I18n.translate("mco.configure.world.restore.question.line1", string, string2);
			String string4 = I18n.translate("mco.configure.world.restore.question.line2");
			this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
				if (bl) {
					this.restore();
				} else {
					this.selectedBackup = -1;
					this.client.openScreen(this);
				}
			}, RealmsLongConfirmationScreen.Type.Warning, string3, string4, true));
		}
	}

	private void downloadClicked() {
		String string = I18n.translate("mco.configure.world.restore.download.question.line1");
		String string2 = I18n.translate("mco.configure.world.restore.download.question.line2");
		this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
			if (bl) {
				this.downloadWorldData();
			} else {
				this.client.openScreen(this);
			}
		}, RealmsLongConfirmationScreen.Type.Info, string, string2, true));
	}

	private void downloadWorldData() {
		this.client
			.openScreen(
				new RealmsLongRunningMcoTaskScreen(
					this.lastScreen.getNewScreen(),
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
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask(backup, this.serverData.id, this.lastScreen)));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.renderBackground();
		this.backupObjectSelectionList.render(mouseX, mouseY, delta);
		this.titleLabel.render(this);
		this.textRenderer.draw(I18n.translate("mco.configure.world.backup"), (float)((this.width - 150) / 2 - 90), 20.0F, 10526880);
		if (this.noBackups) {
			this.textRenderer.draw(I18n.translate("mco.backup.nobackups"), 20.0F, (float)(this.height / 2 - 10), 16777215);
		}

		this.downloadButton.active = !this.noBackups;
		super.render(mouseX, mouseY, delta);
		if (this.toolTip != null) {
			this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
		}
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		if (msg != null) {
			int i = x + 12;
			int j = y - 12;
			int k = this.textRenderer.getStringWidth(msg);
			this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(msg, (float)i, (float)j, 16777215);
		}
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
		public boolean isFocused() {
			return RealmsBackupScreen.this.getFocused() == this;
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public void renderBackground() {
			RealmsBackupScreen.this.renderBackground();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button != 0) {
				return false;
			} else if (mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = this.width / 2 - 92;
				int j = this.width;
				int k = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount();
				int l = k / this.itemHeight;
				if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
					this.setSelected(l);
					this.itemClicked(k, l, mouseX, mouseY, this.width);
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		public int getScrollbarPositionX() {
			return this.width - 5;
		}

		@Override
		public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
			int i = this.width - 35;
			int j = selectionIndex * this.itemHeight + 36 - (int)this.getScrollAmount();
			int k = i + 10;
			int l = j - 3;
			if (mouseX >= (double)i && mouseX <= (double)(i + 9) && mouseY >= (double)j && mouseY <= (double)(j + 9)) {
				if (!((Backup)RealmsBackupScreen.this.backups.get(selectionIndex)).changeList.isEmpty()) {
					RealmsBackupScreen.this.selectedBackup = -1;
					RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
					this.client.openScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(selectionIndex)));
				}
			} else if (mouseX >= (double)k && mouseX < (double)(k + 13) && mouseY >= (double)l && mouseY < (double)(l + 15)) {
				RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
				RealmsBackupScreen.this.restoreClicked(selectionIndex);
			}
		}

		@Override
		public void setSelected(int index) {
			this.setSelectedItem(index);
			if (index != -1) {
				Realms.narrateNow(I18n.translate("narrator.select", ((Backup)RealmsBackupScreen.this.backups.get(index)).lastModifiedDate.toString()));
			}

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
		private final Backup mBackup;

		public BackupObjectSelectionListEntry(Backup backup) {
			this.mBackup = backup;
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.renderBackupItem(this.mBackup, x - 40, y, mouseX, mouseY);
		}

		private void renderBackupItem(Backup backup, int x, int y, int mouseX, int mouseY) {
			int i = backup.isUploadedVersion() ? -8388737 : 16777215;
			RealmsBackupScreen.this.textRenderer.draw("Backup (" + RealmsUtil.method_25282(backup.lastModifiedDate) + ")", (float)(x + 40), (float)(y + 1), i);
			RealmsBackupScreen.this.textRenderer.draw(this.getMediumDatePresentation(backup.lastModifiedDate), (float)(x + 40), (float)(y + 12), 5000268);
			int j = RealmsBackupScreen.this.width - 175;
			int k = -3;
			int l = j - 10;
			int m = 0;
			if (!RealmsBackupScreen.this.serverData.expired) {
				this.drawRestore(j, y + -3, mouseX, mouseY);
			}

			if (!backup.changeList.isEmpty()) {
				this.drawInfo(l, y + 0, mouseX, mouseY);
			}
		}

		private String getMediumDatePresentation(Date lastModifiedDate) {
			return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
		}

		private void drawRestore(int x, int y, int xm, int ym) {
			boolean bl = xm >= x && xm <= x + 12 && ym >= y && ym <= y + 14 && ym < RealmsBackupScreen.this.height - 15 && ym > 32;
			RealmsBackupScreen.this.client.getTextureManager().bindTexture(RealmsBackupScreen.field_22687);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			float f = bl ? 28.0F : 0.0F;
			DrawableHelper.drawTexture(x * 2, y * 2, 0.0F, f, 23, 28, 23, 56);
			RenderSystem.popMatrix();
			if (bl) {
				RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.button.restore");
			}
		}

		private void drawInfo(int x, int y, int xm, int ym) {
			boolean bl = xm >= x && xm <= x + 8 && ym >= y && ym <= y + 8 && ym < RealmsBackupScreen.this.height - 15 && ym > 32;
			RealmsBackupScreen.this.client.getTextureManager().bindTexture(RealmsBackupScreen.field_22686);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			float f = bl ? 15.0F : 0.0F;
			DrawableHelper.drawTexture(x * 2, y * 2, 0.0F, f, 15, 15, 15, 30);
			RenderSystem.popMatrix();
			if (bl) {
				RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.changes.tooltip");
			}
		}
	}
}
