package net.minecraft.client.gui.widget;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_403;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.menu.BackupLevelScreen;
import net.minecraft.client.gui.menu.BackupPromptScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.menu.WorkingScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public final class LevelSelectEntryWidget extends EntryListWidget.Entry<LevelSelectEntryWidget> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final Identifier field_3243 = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier field_3240 = new Identifier("textures/gui/world_selection.png");
	private final MinecraftClient client;
	private final LevelSelectScreen guiLevelSelect;
	private final LevelSummary levelSummary;
	private final Identifier field_3247;
	private final LevelListWidget field_3241;
	private File iconFile;
	@Nullable
	private final NativeImageBackedTexture field_3244;
	private long field_3248;

	public LevelSelectEntryWidget(LevelListWidget levelListWidget, LevelSummary levelSummary, LevelStorage levelStorage) {
		this.field_3241 = levelListWidget;
		this.guiLevelSelect = levelListWidget.method_2752();
		this.levelSummary = levelSummary;
		this.client = MinecraftClient.getInstance();
		this.field_3247 = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(levelSummary.getName()) + "/icon");
		this.iconFile = levelStorage.resolveFile(levelSummary.getName(), "icon.png");
		if (!this.iconFile.isFile()) {
			this.iconFile = null;
		}

		this.field_3244 = this.method_2758();
	}

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY();
		int n = this.getX();
		String string = this.levelSummary.getDisplayName();
		String string2 = this.levelSummary.getName() + " (" + DATE_FORMAT.format(new Date(this.levelSummary.getLastPlayed())) + ")";
		String string3 = "";
		if (StringUtils.isEmpty(string)) {
			string = I18n.translate("selectWorld.world") + " " + (this.method_1908() + 1);
		}

		if (this.levelSummary.requiresConversion()) {
			string3 = I18n.translate("selectWorld.conversion") + " " + string3;
		} else {
			string3 = I18n.translate("gameMode." + this.levelSummary.getGameMode().getName());
			if (this.levelSummary.isHardcore()) {
				string3 = TextFormat.field_1079 + I18n.translate("gameMode.hardcore") + TextFormat.field_1070;
			}

			if (this.levelSummary.hasCheats()) {
				string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
			}

			String string4 = this.levelSummary.method_258().getFormattedText();
			if (this.levelSummary.isDifferentVersion()) {
				if (this.levelSummary.isFutureLevel()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.field_1061 + string4 + TextFormat.field_1070;
				} else {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.field_1056 + string4 + TextFormat.field_1070;
				}
			} else {
				string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + string4;
			}
		}

		this.client.field_1772.draw(string, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		this.client.field_1772.draw(string2, (float)(n + 32 + 3), (float)(m + 9 + 3), 8421504);
		this.client.field_1772.draw(string3, (float)(n + 32 + 3), (float)(m + 9 + 9 + 3), 8421504);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(this.field_3244 != null ? this.field_3247 : field_3243);
		GlStateManager.enableBlend();
		DrawableHelper.drawTexturedRect(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
		if (this.client.field_1690.touchscreen || bl) {
			this.client.method_1531().method_4618(field_3240);
			DrawableHelper.drawRect(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = o < 32 ? 32 : 0;
			if (this.levelSummary.isDifferentVersion()) {
				DrawableHelper.drawTexturedRect(n, m, 32.0F, (float)p, 32, 32, 256.0F, 256.0F);
				if (this.levelSummary.isLegacyCustomizedWorld()) {
					DrawableHelper.drawTexturedRect(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						TextComponent textComponent = new TranslatableTextComponent("selectWorld.tooltip.unsupported", this.levelSummary.method_258())
							.applyFormat(TextFormat.field_1061);
						this.guiLevelSelect.method_2739(this.client.field_1772.wrapStringToWidth(textComponent.getFormattedText(), 175));
					}
				} else if (this.levelSummary.isFutureLevel()) {
					DrawableHelper.drawTexturedRect(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.guiLevelSelect
							.method_2739(
								TextFormat.field_1061
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
									+ "\n"
									+ TextFormat.field_1061
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
							);
					}
				} else if (!SharedConstants.getGameVersion().isStable()) {
					DrawableHelper.drawTexturedRect(n, m, 64.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.guiLevelSelect
							.method_2739(
								TextFormat.field_1065
									+ I18n.translate("selectWorld.tooltip.snapshot1")
									+ "\n"
									+ TextFormat.field_1065
									+ I18n.translate("selectWorld.tooltip.snapshot2")
							);
					}
				}
			} else {
				DrawableHelper.drawTexturedRect(n, m, 0.0F, (float)p, 32, 32, 256.0F, 256.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_3241.method_2751(this.method_1908());
		if (d - (double)this.getX() <= 32.0) {
			this.loadLevel();
			return true;
		} else if (SystemUtil.getMeasuringTimeMs() - this.field_3248 < 250L) {
			this.loadLevel();
			return true;
		} else {
			this.field_3248 = SystemUtil.getMeasuringTimeMs();
			return false;
		}
	}

	public void loadLevel() {
		if (this.levelSummary.isOutdatedLevel() || this.levelSummary.isLegacyCustomizedWorld()) {
			String string = I18n.translate("selectWorld.backupQuestion");
			String string2 = I18n.translate("selectWorld.backupWarning", this.levelSummary.method_258().getFormattedText(), SharedConstants.getGameVersion().getName());
			if (this.levelSummary.isLegacyCustomizedWorld()) {
				string = I18n.translate("selectWorld.backupQuestion.customized");
				string2 = I18n.translate("selectWorld.backupWarning.customized");
			}

			this.client.method_1507(new BackupPromptScreen(this.guiLevelSelect, bl -> {
				if (bl) {
					String stringx = this.levelSummary.getName();
					BackupLevelScreen.backupLevel(this.client.getLevelStorage(), stringx);
				}

				this.loadLevelInternal();
			}, string, string2));
		} else if (this.levelSummary.isFutureLevel()) {
			this.client
				.method_1507(
					new YesNoScreen(
						(bl, i) -> {
							if (bl) {
								try {
									this.loadLevelInternal();
								} catch (Exception var4) {
									LOGGER.error("Failure to open 'future world'", (Throwable)var4);
									this.client
										.method_1507(
											new class_403(
												() -> this.client.method_1507(this.guiLevelSelect),
												new TranslatableTextComponent("selectWorld.futureworld.error.title"),
												new TranslatableTextComponent("selectWorld.futureworld.error.text")
											)
										);
								}
							} else {
								this.client.method_1507(this.guiLevelSelect);
							}
						},
						I18n.translate("selectWorld.versionQuestion"),
						I18n.translate("selectWorld.versionWarning", this.levelSummary.method_258().getFormattedText()),
						I18n.translate("selectWorld.versionJoinButton"),
						I18n.translate("gui.cancel"),
						0
					)
				);
		} else {
			this.loadLevelInternal();
		}
	}

	public void method_2755() {
		this.client
			.method_1507(
				new YesNoScreen(
					(bl, i) -> {
						if (bl) {
							this.client.method_1507(new WorkingScreen());
							LevelStorage levelStorage = this.client.getLevelStorage();
							levelStorage.deleteLevel(this.levelSummary.getName());
							this.field_3241.filter(() -> this.guiLevelSelect.searchBox.getText(), true);
						}

						this.client.method_1507(this.guiLevelSelect);
					},
					I18n.translate("selectWorld.deleteQuestion"),
					I18n.translate("selectWorld.deleteWarning", this.levelSummary.getDisplayName()),
					I18n.translate("selectWorld.deleteButton"),
					I18n.translate("gui.cancel"),
					0
				)
			);
	}

	public void method_2756() {
		this.client.method_1507(new BackupLevelScreen((bl, i) -> {
			if (bl) {
				this.field_3241.filter(() -> this.guiLevelSelect.searchBox.getText(), true);
			}

			this.client.method_1507(this.guiLevelSelect);
		}, this.levelSummary.getName()));
	}

	public void method_2757() {
		try {
			this.client.method_1507(new WorkingScreen());
			NewLevelScreen newLevelScreen = new NewLevelScreen(this.guiLevelSelect);
			WorldSaveHandler worldSaveHandler = this.client.getLevelStorage().method_242(this.levelSummary.getName(), null);
			LevelProperties levelProperties = worldSaveHandler.readProperties();
			if (levelProperties != null) {
				newLevelScreen.recreateLevel(levelProperties);
				if (this.levelSummary.isLegacyCustomizedWorld()) {
					this.client
						.method_1507(
							new YesNoScreen(
								(bl, i) -> {
									if (bl) {
										this.client.method_1507(newLevelScreen);
									} else {
										this.client.method_1507(this.guiLevelSelect);
									}
								},
								I18n.translate("selectWorld.recreate.customized.title"),
								I18n.translate("selectWorld.recreate.customized.text"),
								I18n.translate("gui.proceed"),
								I18n.translate("gui.cancel"),
								0
							)
						);
				} else {
					this.client.method_1507(newLevelScreen);
				}
			}
		} catch (Exception var4) {
			LOGGER.error("Unable to recreate world", (Throwable)var4);
			this.client
				.method_1507(
					new class_403(
						() -> this.client.method_1507(this.guiLevelSelect),
						new TranslatableTextComponent("selectWorld.recreate.error.title"),
						new TranslatableTextComponent("selectWorld.recreate.error.text")
					)
				);
		}
	}

	private void loadLevelInternal() {
		this.client.method_1483().play(PositionedSoundInstance.method_4758(SoundEvents.field_15015, 1.0F));
		if (this.client.getLevelStorage().levelExists(this.levelSummary.getName())) {
			this.client.startIntegratedServer(this.levelSummary.getName(), this.levelSummary.getDisplayName(), null);
		}
	}

	@Nullable
	private NativeImageBackedTexture method_2758() {
		boolean bl = this.iconFile != null && this.iconFile.isFile();
		if (bl) {
			try {
				InputStream inputStream = new FileInputStream(this.iconFile);
				Throwable var3 = null;

				NativeImageBackedTexture var6;
				try {
					NativeImage nativeImage = NativeImage.fromInputStream(inputStream);
					Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
					Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
					NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
					this.client.method_1531().method_4616(this.field_3247, nativeImageBackedTexture);
					var6 = nativeImageBackedTexture;
				} catch (Throwable var16) {
					var3 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var3 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var3.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var6;
			} catch (Throwable var18) {
				LOGGER.error("Invalid icon for world {}", this.levelSummary.getName(), var18);
				this.iconFile = null;
				return null;
			}
		} else {
			this.client.method_1531().method_4615(this.field_3247);
			return null;
		}
	}

	public void close() {
		if (this.field_3244 != null) {
			this.field_3244.close();
		}
	}

	@Override
	public void method_1904(float f) {
	}
}
