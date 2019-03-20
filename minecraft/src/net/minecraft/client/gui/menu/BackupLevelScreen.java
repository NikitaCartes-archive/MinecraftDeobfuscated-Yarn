package net.minecraft.client.gui.menu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.UpdateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class BackupLevelScreen extends Screen {
	private ButtonWidget saveButton;
	private final YesNoCallback callback;
	private TextFieldWidget field_3170;
	private final String levelName;

	public BackupLevelScreen(YesNoCallback yesNoCallback, String string) {
		this.callback = yesNoCallback;
		this.levelName = string;
	}

	@Override
	public void update() {
		this.field_3170.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 24 + 5, I18n.translate("selectWorld.edit.resetIcon")) {
				@Override
				public void onPressed() {
					LevelStorage levelStorage = BackupLevelScreen.this.client.getLevelStorage();
					FileUtils.deleteQuietly(levelStorage.resolveFile(BackupLevelScreen.this.levelName, "icon.png"));
					this.active = false;
				}
			}
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 48 + 5, I18n.translate("selectWorld.edit.openFolder")) {
			@Override
			public void onPressed() {
				LevelStorage levelStorage = BackupLevelScreen.this.client.getLevelStorage();
				SystemUtil.getOperatingSystem().open(levelStorage.resolveFile(BackupLevelScreen.this.levelName, "icon.png").getParentFile());
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 72 + 5, I18n.translate("selectWorld.edit.backup")) {
			@Override
			public void onPressed() {
				LevelStorage levelStorage = BackupLevelScreen.this.client.getLevelStorage();
				BackupLevelScreen.backupLevel(levelStorage, BackupLevelScreen.this.levelName);
				BackupLevelScreen.this.callback.confirmResult(false, 0);
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 96 + 5, I18n.translate("selectWorld.edit.backupFolder")) {
			@Override
			public void onPressed() {
				LevelStorage levelStorage = BackupLevelScreen.this.client.getLevelStorage();
				Path path = levelStorage.getBackupsDirectory();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var4) {
					throw new RuntimeException(var4);
				}

				SystemUtil.getOperatingSystem().open(path.toFile());
			}
		});
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 120 + 5, I18n.translate("selectWorld.edit.optimize")) {
				@Override
				public void onPressed() {
					BackupLevelScreen.this.client
						.openScreen(
							new BackupPromptScreen(
								BackupLevelScreen.this,
								bl -> {
									if (bl) {
										BackupLevelScreen.backupLevel(BackupLevelScreen.this.client.getLevelStorage(), BackupLevelScreen.this.levelName);
									}

									BackupLevelScreen.this.client
										.openScreen(new UpdateWorldScreen(BackupLevelScreen.this.callback, BackupLevelScreen.this.levelName, BackupLevelScreen.this.client.getLevelStorage()));
								},
								I18n.translate("optimizeWorld.confirm.title"),
								I18n.translate("optimizeWorld.confirm.description")
							)
						);
				}
			}
		);
		this.saveButton = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save")) {
				@Override
				public void onPressed() {
					BackupLevelScreen.this.commit();
				}
			}
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, this.screenHeight / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed() {
				BackupLevelScreen.this.callback.confirmResult(false, 0);
			}
		});
		buttonWidget.active = this.client.getLevelStorage().resolveFile(this.levelName, "icon.png").isFile();
		LevelStorage levelStorage = this.client.getLevelStorage();
		LevelProperties levelProperties = levelStorage.getLevelProperties(this.levelName);
		String string = levelProperties == null ? "" : levelProperties.getLevelName();
		this.field_3170 = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 53, 200, 20);
		this.field_3170.setText(string);
		this.field_3170.setChangedListener(stringx -> this.saveButton.active = !stringx.trim().isEmpty());
		this.listeners.add(this.field_3170);
		this.focusOn(this.field_3170);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.field_3170.getText();
		this.initialize(minecraftClient, i, j);
		this.field_3170.setText(string);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void commit() {
		LevelStorage levelStorage = this.client.getLevelStorage();
		levelStorage.renameLevel(this.levelName, this.field_3170.getText().trim());
		this.callback.confirmResult(true, 0);
	}

	public static void backupLevel(LevelStorage levelStorage, String string) {
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		long l = 0L;
		IOException iOException = null;

		try {
			l = levelStorage.backupLevel(string);
		} catch (IOException var8) {
			iOException = var8;
		}

		TextComponent textComponent;
		TextComponent textComponent2;
		if (iOException != null) {
			textComponent = new TranslatableTextComponent("selectWorld.edit.backupFailed");
			textComponent2 = new StringTextComponent(iOException.getMessage());
		} else {
			textComponent = new TranslatableTextComponent("selectWorld.edit.backupCreated", string);
			textComponent2 = new TranslatableTextComponent("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
		}

		toastManager.add(new SystemToast(SystemToast.Type.field_2220, textComponent, textComponent2));
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectWorld.edit.title"), this.screenWidth / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterName"), this.screenWidth / 2 - 100, 40, 10526880);
		this.field_3170.render(i, j, f);
		super.render(i, j, f);
	}
}
