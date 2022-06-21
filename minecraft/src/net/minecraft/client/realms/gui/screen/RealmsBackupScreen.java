package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
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
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.DownloadTask;
import net.minecraft.client.realms.task.RestoreTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.util.math.MatrixStack;
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
	static int lastScrollPosition = -1;
	private final RealmsConfigureWorldScreen parent;
	List<Backup> backups = Collections.emptyList();
	@Nullable
	Text tooltip;
	RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
	int selectedBackup = -1;
	private final int slotId;
	private ButtonWidget downloadButton;
	private ButtonWidget restoreButton;
	private ButtonWidget changesButton;
	Boolean noBackups = false;
	final RealmsServer serverData;
	private static final String UPLOADED = "Uploaded";

	public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
		super(Text.translatable("mco.configure.world.backup"));
		this.parent = parent;
		this.serverData = serverData;
		this.slotId = slotId;
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
		if (lastScrollPosition != -1) {
			this.backupObjectSelectionList.setScrollAmount((double)lastScrollPosition);
		}

		(new Thread("Realms-fetch-backups") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.method_44616();

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
		this.downloadButton = this.addDrawableChild(
			new ButtonWidget(this.width - 135, row(1), 120, 20, Text.translatable("mco.backup.button.download"), button -> this.downloadClicked())
		);
		this.restoreButton = this.addDrawableChild(
			new ButtonWidget(this.width - 135, row(3), 120, 20, Text.translatable("mco.backup.button.restore"), button -> this.restoreClicked(this.selectedBackup))
		);
		this.changesButton = this.addDrawableChild(new ButtonWidget(this.width - 135, row(5), 120, 20, Text.translatable("mco.backup.changes.tooltip"), button -> {
			this.client.setScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
			this.selectedBackup = -1;
		}));
		this.addDrawableChild(new ButtonWidget(this.width - 100, this.height - 35, 85, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
		this.addSelectableChild(this.backupObjectSelectionList);
		this.focusOn(this.backupObjectSelectionList);
		this.updateButtonStates();
	}

	void generateChangeList() {
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
			backup.changeList.put(key, (String)backup.metadata.get(key));
		}
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
			String string2 = RealmsUtil.convertToAgePresentation(date);
			Text text = Text.translatable("mco.configure.world.restore.question.line1", string, string2);
			Text text2 = Text.translatable("mco.configure.world.restore.question.line2");
			this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
				if (confirmed) {
					this.restore();
				} else {
					this.selectedBackup = -1;
					this.client.setScreen(this);
				}
			}, RealmsLongConfirmationScreen.Type.WARNING, text, text2, true));
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltip = null;
		this.renderBackground(matrices);
		this.backupObjectSelectionList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 12, 16777215);
		this.textRenderer.draw(matrices, BACKUPS_TEXT, (float)((this.width - 150) / 2 - 90), 20.0F, 10526880);
		if (this.noBackups) {
			this.textRenderer.draw(matrices, NO_BACKUPS_TEXT, 20.0F, (float)(this.height / 2 - 10), 16777215);
		}

		this.downloadButton.active = !this.noBackups;
		super.render(matrices, mouseX, mouseY, delta);
		if (this.tooltip != null) {
			this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
		}
	}

	protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text text, int mouseX, int mouseY) {
		if (text != null) {
			int i = mouseX + 12;
			int j = mouseY - 12;
			int k = this.textRenderer.getWidth(text);
			this.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, 16777215);
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
			return this.getEntryCount() * 36;
		}

		@Override
		public void renderBackground(MatrixStack matrices) {
			RealmsBackupScreen.this.renderBackground(matrices);
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
				if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount()) {
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
					this.client.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(selectionIndex)));
				}
			} else if (mouseX >= (double)k && mouseX < (double)(k + 13) && mouseY >= (double)l && mouseY < (double)(l + 15)) {
				RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
				RealmsBackupScreen.this.restoreClicked(selectionIndex);
			}
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
		private final Backup mBackup;

		public BackupObjectSelectionListEntry(Backup backup) {
			this.mBackup = backup;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderBackupItem(matrices, this.mBackup, x - 40, y, mouseX, mouseY);
		}

		private void renderBackupItem(MatrixStack matrices, Backup backup, int x, int y, int mouseX, int mouseY) {
			int i = backup.isUploadedVersion() ? -8388737 : 16777215;
			RealmsBackupScreen.this.textRenderer
				.draw(matrices, "Backup (" + RealmsUtil.convertToAgePresentation(backup.lastModifiedDate) + ")", (float)(x + 40), (float)(y + 1), i);
			RealmsBackupScreen.this.textRenderer.draw(matrices, this.getMediumDatePresentation(backup.lastModifiedDate), (float)(x + 40), (float)(y + 12), 5000268);
			int j = RealmsBackupScreen.this.width - 175;
			int k = -3;
			int l = j - 10;
			int m = 0;
			if (!RealmsBackupScreen.this.serverData.expired) {
				this.drawRestore(matrices, j, y + -3, mouseX, mouseY);
			}

			if (!backup.changeList.isEmpty()) {
				this.drawInfo(matrices, l, y + 0, mouseX, mouseY);
			}
		}

		private String getMediumDatePresentation(Date lastModifiedDate) {
			return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
		}

		private void drawRestore(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			boolean bl = mouseX >= x && mouseX <= x + 12 && mouseY >= y && mouseY <= y + 14 && mouseY < RealmsBackupScreen.this.height - 15 && mouseY > 32;
			RenderSystem.setShaderTexture(0, RealmsBackupScreen.RESTORE_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.push();
			matrices.scale(0.5F, 0.5F, 0.5F);
			float f = bl ? 28.0F : 0.0F;
			DrawableHelper.drawTexture(matrices, x * 2, y * 2, 0.0F, f, 23, 28, 23, 56);
			matrices.pop();
			if (bl) {
				RealmsBackupScreen.this.tooltip = RealmsBackupScreen.RESTORE_TEXT;
			}
		}

		private void drawInfo(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			boolean bl = mouseX >= x && mouseX <= x + 8 && mouseY >= y && mouseY <= y + 8 && mouseY < RealmsBackupScreen.this.height - 15 && mouseY > 32;
			RenderSystem.setShaderTexture(0, RealmsBackupScreen.PLUS_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.push();
			matrices.scale(0.5F, 0.5F, 0.5F);
			float f = bl ? 15.0F : 0.0F;
			DrawableHelper.drawTexture(matrices, x * 2, y * 2, 0.0F, f, 15, 15, 15, 30);
			matrices.pop();
			if (bl) {
				RealmsBackupScreen.this.tooltip = RealmsBackupScreen.CHANGES_TOOLTIP;
			}
		}

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.mBackup.lastModifiedDate.toString());
		}
	}
}
