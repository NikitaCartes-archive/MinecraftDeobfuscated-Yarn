package net.minecraft.client.gui.menu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_370;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ingame.UpdateWorldGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
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
public class BackupLevelGui extends Gui {
	private ButtonWidget saveButton;
	private final YesNoCallback callback;
	private TextFieldWidget field_3170;
	private final String levelName;

	public BackupLevelGui(YesNoCallback yesNoCallback, String string) {
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
			new ButtonWidget(3, this.width / 2 - 100, this.height / 4 + 24 + 5, I18n.translate("selectWorld.edit.resetIcon")) {
				@Override
				public void onPressed(double d, double e) {
					LevelStorage levelStorage = BackupLevelGui.this.client.getLevelStorage();
					FileUtils.deleteQuietly(levelStorage.resolveFile(BackupLevelGui.this.levelName, "icon.png"));
					this.enabled = false;
				}
			}
		);
		this.addButton(new ButtonWidget(4, this.width / 2 - 100, this.height / 4 + 48 + 5, I18n.translate("selectWorld.edit.openFolder")) {
			@Override
			public void onPressed(double d, double e) {
				LevelStorage levelStorage = BackupLevelGui.this.client.getLevelStorage();
				SystemUtil.getOperatingSystem().open(levelStorage.resolveFile(BackupLevelGui.this.levelName, "icon.png").getParentFile());
			}
		});
		this.addButton(new ButtonWidget(5, this.width / 2 - 100, this.height / 4 + 72 + 5, I18n.translate("selectWorld.edit.backup")) {
			@Override
			public void onPressed(double d, double e) {
				LevelStorage levelStorage = BackupLevelGui.this.client.getLevelStorage();
				BackupLevelGui.backupLevel(levelStorage, BackupLevelGui.this.levelName);
				BackupLevelGui.this.callback.handle(false, 0);
			}
		});
		this.addButton(new ButtonWidget(6, this.width / 2 - 100, this.height / 4 + 96 + 5, I18n.translate("selectWorld.edit.backupFolder")) {
			@Override
			public void onPressed(double d, double e) {
				LevelStorage levelStorage = BackupLevelGui.this.client.getLevelStorage();
				Path path = levelStorage.method_236();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var8) {
					throw new RuntimeException(var8);
				}

				SystemUtil.getOperatingSystem().open(path.toFile());
			}
		});
		this.addButton(
			new ButtonWidget(7, this.width / 2 - 100, this.height / 4 + 120 + 5, I18n.translate("selectWorld.edit.optimize")) {
				@Override
				public void onPressed(double d, double e) {
					BackupLevelGui.this.client
						.openGui(
							new BackupPromptGui(
								BackupLevelGui.this,
								bl -> {
									if (bl) {
										BackupLevelGui.backupLevel(BackupLevelGui.this.client.getLevelStorage(), BackupLevelGui.this.levelName);
									}

									BackupLevelGui.this.client
										.openGui(new UpdateWorldGui(BackupLevelGui.this.callback, BackupLevelGui.this.levelName, BackupLevelGui.this.client.getLevelStorage()));
								},
								I18n.translate("optimizeWorld.confirm.title"),
								I18n.translate("optimizeWorld.confirm.description")
							)
						);
				}
			}
		);
		this.saveButton = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save")) {
			@Override
			public void onPressed(double d, double e) {
				BackupLevelGui.this.commit();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				BackupLevelGui.this.callback.handle(false, 0);
			}
		});
		buttonWidget.enabled = this.client.getLevelStorage().resolveFile(this.levelName, "icon.png").isFile();
		LevelStorage levelStorage = this.client.getLevelStorage();
		LevelProperties levelProperties = levelStorage.getLevelProperties(this.levelName);
		String string = levelProperties == null ? "" : levelProperties.getLevelName();
		this.field_3170 = new TextFieldWidget(2, this.fontRenderer, this.width / 2 - 100, 53, 200, 20);
		this.field_3170.setFocused(true);
		this.field_3170.setText(string);
		this.listeners.add(this.field_3170);
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
		levelStorage.rename(this.levelName, this.field_3170.getText().trim());
		this.callback.handle(true, 0);
	}

	public static void backupLevel(LevelStorage levelStorage, String string) {
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		long l = 0L;
		IOException iOException = null;

		try {
			l = levelStorage.method_237(string);
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

		toastManager.add(new class_370(class_370.class_371.field_2220, textComponent, textComponent2));
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_3170.charTyped(c, i)) {
			this.saveButton.enabled = !this.field_3170.getText().trim().isEmpty();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_3170.keyPressed(i, j, k)) {
			this.saveButton.enabled = !this.field_3170.getText().trim().isEmpty();
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.commit();
			return true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectWorld.edit.title"), this.width / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
		this.field_3170.render(i, j, f);
		super.draw(i, j, f);
	}
}
