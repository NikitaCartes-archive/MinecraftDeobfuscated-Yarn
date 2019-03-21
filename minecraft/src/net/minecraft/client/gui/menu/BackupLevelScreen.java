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
		super(new TranslatableTextComponent("selectWorld.edit.title"));
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
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 24 + 5, 200, 20, I18n.translate("selectWorld.edit.resetIcon"), buttonWidgetx -> {
				LevelStorage levelStoragex = this.client.getLevelStorage();
				FileUtils.deleteQuietly(levelStoragex.resolveFile(this.levelName, "icon.png"));
				buttonWidgetx.active = false;
			})
		);
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 48 + 5, 200, 20, I18n.translate("selectWorld.edit.openFolder"), buttonWidgetx -> {
				LevelStorage levelStoragex = this.client.getLevelStorage();
				SystemUtil.getOperatingSystem().open(levelStoragex.resolveFile(this.levelName, "icon.png").getParentFile());
			})
		);
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 72 + 5, 200, 20, I18n.translate("selectWorld.edit.backup"), buttonWidgetx -> {
				LevelStorage levelStoragex = this.client.getLevelStorage();
				backupLevel(levelStoragex, this.levelName);
				this.callback.confirmResult(false, 0);
			})
		);
		this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 4 + 96 + 5, 200, 20, I18n.translate("selectWorld.edit.backupFolder"), buttonWidgetx -> {
				LevelStorage levelStoragex = this.client.getLevelStorage();
				Path path = levelStoragex.getBackupsDirectory();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var5) {
					throw new RuntimeException(var5);
				}

				SystemUtil.getOperatingSystem().open(path.toFile());
			})
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100,
				this.screenHeight / 4 + 120 + 5,
				200,
				20,
				I18n.translate("selectWorld.edit.optimize"),
				buttonWidgetx -> this.client.openScreen(new BackupPromptScreen(this, bl -> {
						if (bl) {
							backupLevel(this.client.getLevelStorage(), this.levelName);
						}

						this.client.openScreen(new UpdateWorldScreen(this.callback, this.levelName, this.client.getLevelStorage()));
					}, new TranslatableTextComponent("optimizeWorld.confirm.title"), new TranslatableTextComponent("optimizeWorld.confirm.description")))
			)
		);
		this.saveButton = this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 100, this.screenHeight / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save"), buttonWidgetx -> this.commit()
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 2, this.screenHeight / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel"), buttonWidgetx -> this.callback.confirmResult(false, 0)
			)
		);
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
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 20, 16777215);
		this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterName"), this.screenWidth / 2 - 100, 40, 10526880);
		this.field_3170.render(i, j, f);
		super.render(i, j, f);
	}
}
