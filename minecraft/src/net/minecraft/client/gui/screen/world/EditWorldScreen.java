package net.minecraft.client.gui.screen.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.SaveLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class EditWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
	private static final Text ENTER_NAME_TEXT = new TranslatableText("selectWorld.enterName");
	private ButtonWidget saveButton;
	private final BooleanConsumer callback;
	private TextFieldWidget levelNameTextField;
	private final LevelStorage.Session storageSession;

	public EditWorldScreen(BooleanConsumer callback, LevelStorage.Session storageSession) {
		super(new TranslatableText("selectWorld.edit.title"));
		this.callback = callback;
		this.storageSession = storageSession;
	}

	@Override
	public void tick() {
		this.levelNameTextField.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		ButtonWidget buttonWidget = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableText("selectWorld.edit.resetIcon"), button -> {
				this.storageSession.getIconFile().ifPresent(path -> FileUtils.deleteQuietly(path.toFile()));
				button.active = false;
			})
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 24 + 5,
				200,
				20,
				new TranslatableText("selectWorld.edit.openFolder"),
				button -> Util.getOperatingSystem().open(this.storageSession.getDirectory(WorldSavePath.ROOT).toFile())
			)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableText("selectWorld.edit.backup"), button -> {
			boolean bl = backupLevel(this.storageSession);
			this.callback.accept(!bl);
		}));
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableText("selectWorld.edit.backupFolder"), button -> {
				LevelStorage levelStorage = this.client.getLevelStorage();
				Path path = levelStorage.getBackupsDirectory();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var5) {
					throw new RuntimeException(var5);
				}

				Util.getOperatingSystem().open(path.toFile());
			})
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 96 + 5,
				200,
				20,
				new TranslatableText("selectWorld.edit.optimize"),
				button -> this.client.setScreen(new BackupPromptScreen(this, (backup, eraseCache) -> {
						if (backup) {
							backupLevel(this.storageSession);
						}

						this.client.setScreen(OptimizeWorldScreen.create(this.client, this.callback, this.client.getDataFixer(), this.storageSession, eraseCache));
					}, new TranslatableText("optimizeWorld.confirm.title"), new TranslatableText("optimizeWorld.confirm.description"), true))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 120 + 5,
				200,
				20,
				new TranslatableText("selectWorld.edit.export_worldgen_settings"),
				button -> {
					DataResult<String> dataResult2;
					try (SaveLoader saveLoader = this.client.createSaveLoader(this.storageSession, false)) {
						DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, saveLoader.dynamicRegistryManager());
						DataResult<JsonElement> dataResult = GeneratorOptions.CODEC.encodeStart(dynamicOps, saveLoader.saveProperties().getGeneratorOptions());
						dataResult2 = dataResult.flatMap(json -> {
							Path path = this.storageSession.getDirectory(WorldSavePath.ROOT).resolve("worldgen_settings_export.json");

							try {
								JsonWriter jsonWriter = GSON.newJsonWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));

								try {
									GSON.toJson(json, jsonWriter);
								} catch (Throwable var7) {
									if (jsonWriter != null) {
										try {
											jsonWriter.close();
										} catch (Throwable var6) {
											var7.addSuppressed(var6);
										}
									}

									throw var7;
								}

								if (jsonWriter != null) {
									jsonWriter.close();
								}
							} catch (JsonIOException | IOException var8x) {
								return DataResult.error("Error writing file: " + var8x.getMessage());
							}

							return DataResult.success(path.toString());
						});
					} catch (Exception var8) {
						LOGGER.warn("Could not parse level data", (Throwable)var8);
						dataResult2 = DataResult.error("Could not parse level data: " + var8.getMessage());
					}

					Text text = new LiteralText(dataResult2.get().map(Function.identity(), PartialResult::message));
					Text text2 = new TranslatableText(
						dataResult2.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure"
					);
					dataResult2.error().ifPresent(result -> LOGGER.error("Error exporting world settings: {}", result));
					this.client.getToastManager().add(SystemToast.create(this.client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text2, text));
				}
			)
		);
		this.saveButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableText("selectWorld.edit.save"), button -> this.commit())
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, ScreenTexts.CANCEL, button -> this.callback.accept(false)));
		buttonWidget.active = this.storageSession.getIconFile().filter(path -> Files.isRegularFile(path, new LinkOption[0])).isPresent();
		LevelSummary levelSummary = this.storageSession.getLevelSummary();
		String string = levelSummary == null ? "" : levelSummary.getDisplayName();
		this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 38, 200, 20, new TranslatableText("selectWorld.enterName"));
		this.levelNameTextField.setText(string);
		this.levelNameTextField.setChangedListener(levelName -> this.saveButton.active = !levelName.trim().isEmpty());
		this.addSelectableChild(this.levelNameTextField);
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

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
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
			Text text = new TranslatableText("selectWorld.edit.backupFailed");
			Text text2 = new LiteralText(iOException.getMessage());
			MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
			return false;
		} else {
			Text text = new TranslatableText("selectWorld.edit.backupCreated", storageSession.getDirectoryName());
			Text text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
			MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
			return true;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100, 24, 10526880);
		this.levelNameTextField.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
