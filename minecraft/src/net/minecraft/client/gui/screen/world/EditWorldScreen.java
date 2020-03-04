package net.minecraft.client.gui.screen.world;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public class EditWorldScreen extends Screen {
	private ButtonWidget saveButton;
	private final BooleanConsumer callback;
	private TextFieldWidget levelNameTextField;
	private final String levelName;

	public EditWorldScreen(BooleanConsumer callback, String levelName) {
		super(new TranslatableText("selectWorld.edit.title"));
		this.callback = callback;
		this.levelName = levelName;
	}

	@Override
	public void tick() {
		this.levelNameTextField.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.translate("selectWorld.edit.resetIcon"), buttonWidgetx -> {
				LevelStorage levelStoragex = this.client.getLevelStorage();
				FileUtils.deleteQuietly(levelStoragex.resolveFile(this.levelName, "icon.png"));
				buttonWidgetx.active = false;
			})
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.translate("selectWorld.edit.openFolder"), buttonWidgetx -> {
			LevelStorage levelStoragex = this.client.getLevelStorage();
			Util.getOperatingSystem().open(levelStoragex.resolveFile(this.levelName, "icon.png").getParentFile());
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.translate("selectWorld.edit.backup"), buttonWidgetx -> {
			LevelStorage levelStoragex = this.client.getLevelStorage();
			backupLevel(levelStoragex, this.levelName);
			this.callback.accept(false);
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.translate("selectWorld.edit.backupFolder"), buttonWidgetx -> {
			LevelStorage levelStoragex = this.client.getLevelStorage();
			Path path = levelStoragex.getBackupsDirectory();

			try {
				Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
			} catch (IOException var5) {
				throw new RuntimeException(var5);
			}

			Util.getOperatingSystem().open(path.toFile());
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 120 + 5,
				200,
				20,
				I18n.translate("selectWorld.edit.optimize"),
				buttonWidgetx -> this.client.openScreen(new BackupPromptScreen(this, (bl, bl2) -> {
						if (bl) {
							backupLevel(this.client.getLevelStorage(), this.levelName);
						}

						this.client.openScreen(new OptimizeWorldScreen(this.callback, this.levelName, this.client.getLevelStorage(), bl2));
					}, new TranslatableText("optimizeWorld.confirm.title"), new TranslatableText("optimizeWorld.confirm.description"), true))
			)
		);
		this.saveButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save"), buttonWidgetx -> this.commit())
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel"), buttonWidgetx -> this.callback.accept(false))
		);
		buttonWidget.active = this.client.getLevelStorage().resolveFile(this.levelName, "icon.png").isFile();
		LevelStorage levelStorage = this.client.getLevelStorage();
		LevelProperties levelProperties = levelStorage.getLevelProperties(this.levelName);
		String string = levelProperties == null ? "" : levelProperties.getLevelName();
		this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 53, 200, 20, I18n.translate("selectWorld.enterName"));
		this.levelNameTextField.setText(string);
		this.levelNameTextField.setChangedListener(stringx -> this.saveButton.active = !stringx.trim().isEmpty());
		this.children.add(this.levelNameTextField);
		this.setInitialFocus(this.levelNameTextField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.levelNameTextField.getText();
		this.init(client, width, height);
		this.levelNameTextField.setText(string);
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void commit() {
		LevelStorage levelStorage = this.client.getLevelStorage();
		levelStorage.renameLevel(this.levelName, this.levelNameTextField.getText().trim());
		this.callback.accept(true);
	}

	public static void backupLevel(LevelStorage level, String name) {
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		long l = 0L;
		IOException iOException = null;

		try {
			l = level.backupLevel(name);
		} catch (IOException var8) {
			iOException = var8;
		}

		Text text;
		Text text2;
		if (iOException != null) {
			text = new TranslatableText("selectWorld.edit.backupFailed");
			text2 = new LiteralText(iOException.getMessage());
		} else {
			text = new TranslatableText("selectWorld.edit.backupCreated", name);
			text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
		}

		toastManager.add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 20, 16777215);
		this.drawString(this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
		this.levelNameTextField.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
}
