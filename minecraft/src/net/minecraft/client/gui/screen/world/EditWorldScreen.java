package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class EditWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
	private ButtonWidget saveButton;
	private final BooleanConsumer callback;
	private TextFieldWidget levelNameTextField;
	private final LevelStorage.Session storageSession;

	public EditWorldScreen(BooleanConsumer callback, LevelStorage.Session storageSession) {
		super(Text.translatable("selectWorld.edit.title"));
		this.callback = callback;
		this.storageSession = storageSession;
	}

	@Override
	protected void init() {
		this.saveButton = ButtonWidget.builder(Text.translatable("selectWorld.edit.save"), button -> this.commit())
			.dimensions(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20)
			.build();
		this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 38, 200, 20, Text.translatable("selectWorld.enterName"));
		LevelSummary levelSummary = this.storageSession.getLevelSummary();
		String string = levelSummary == null ? "" : levelSummary.getDisplayName();
		this.levelNameTextField.setText(string);
		this.levelNameTextField.setChangedListener(levelName -> this.saveButton.active = !Util.isBlank(levelName));
		this.addSelectableChild(this.levelNameTextField);
		ButtonWidget buttonWidget = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.edit.resetIcon"), button -> {
			this.storageSession.getIconFile().ifPresent(path -> FileUtils.deleteQuietly(path.toFile()));
			button.active = false;
		}).dimensions(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20).build());
		this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("selectWorld.edit.openFolder"), button -> Util.getOperatingSystem().open(this.storageSession.getDirectory(WorldSavePath.ROOT).toFile())
				)
				.dimensions(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20)
				.build()
		);
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.edit.backup"), button -> {
			boolean bl = backupLevel(this.storageSession);
			this.callback.accept(!bl);
		}).dimensions(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20).build());
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.edit.backupFolder"), button -> {
			LevelStorage levelStorage = this.client.getLevelStorage();
			Path path = levelStorage.getBackupsDirectory();

			try {
				PathUtil.createDirectories(path);
			} catch (IOException var5) {
				throw new RuntimeException(var5);
			}

			Util.getOperatingSystem().open(path.toFile());
		}).dimensions(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20).build());
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectWorld.edit.optimize"), button -> this.client.setScreen(new BackupPromptScreen(this, (backup, eraseCache) -> {
						if (backup) {
							backupLevel(this.storageSession);
						}

						this.client.setScreen(OptimizeWorldScreen.create(this.client, this.callback, this.client.getDataFixer(), this.storageSession, eraseCache));
					}, Text.translatable("optimizeWorld.confirm.title"), Text.translatable("optimizeWorld.confirm.description"), true)))
				.dimensions(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20)
				.build()
		);
		this.addDrawableChild(this.saveButton);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.callback.accept(false)).dimensions(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20).build()
		);
		buttonWidget.active = this.storageSession.getIconFile().filter(path -> Files.isRegularFile(path, new LinkOption[0])).isPresent();
		this.setInitialFocus(this.levelNameTextField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.levelNameTextField.getText();
		this.init(client, width, height);
		this.levelNameTextField.setText(string);
	}

	@Override
	public void close() {
		this.callback.accept(false);
	}

	private void commit() {
		try {
			this.storageSession.save(this.levelNameTextField.getText().trim());
			this.callback.accept(true);
		} catch (IOException var2) {
			LOGGER.error("Failed to access world '{}'", this.storageSession.getDirectoryName(), var2);
			SystemToast.addWorldAccessFailureToast(this.client, this.storageSession.getDirectoryName());
			this.callback.accept(true);
		}
	}

	public static void onBackupConfirm(LevelStorage storage, String levelName) {
		boolean bl = false;

		try (LevelStorage.Session session = storage.createSession(levelName)) {
			bl = true;
			backupLevel(session);
		} catch (IOException var8) {
			if (!bl) {
				SystemToast.addWorldAccessFailureToast(MinecraftClient.getInstance(), levelName);
			}

			LOGGER.warn("Failed to create backup of level {}", levelName, var8);
		} catch (SymlinkValidationException var9) {
			LOGGER.warn("{}", var9.getMessage());
			SystemToast.addWorldAccessFailureToast(MinecraftClient.getInstance(), levelName);
		}
	}

	public static boolean backupLevel(LevelStorage.Session storageSession) {
		long l = 0L;
		IOException iOException = null;

		try {
			l = storageSession.createBackup();
		} catch (IOException var6) {
			iOException = var6;
		}

		if (iOException != null) {
			Text text = Text.translatable("selectWorld.edit.backupFailed");
			Text text2 = Text.literal(iOException.getMessage());
			MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
			return false;
		} else {
			Text text = Text.translatable("selectWorld.edit.backupCreated", storageSession.getDirectoryName());
			Text text2 = Text.translatable("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
			MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
			return true;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
		context.drawTextWithShadow(this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100, 24, 10526880);
		this.levelNameTextField.render(context, mouseX, mouseY, delta);
	}
}
