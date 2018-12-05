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
import net.minecraft.class_528;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.menu.BackupLevelGui;
import net.minecraft.client.gui.menu.BackupPromptGui;
import net.minecraft.client.gui.menu.LevelSelectGui;
import net.minecraft.client.gui.menu.NewLevelGui;
import net.minecraft.client.gui.menu.WorkingGui;
import net.minecraft.client.gui.menu.YesNoGui;
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
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public final class LevelEntryWidget extends EntryListWidget.Entry<LevelEntryWidget> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final Identifier field_3243 = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier field_3240 = new Identifier("textures/gui/world_selection.png");
	private final MinecraftClient client;
	private final LevelSelectGui guiLevelSelect;
	private final LevelSummary levelSummary;
	private final Identifier iconLocation;
	private final class_528 field_3241;
	private File iconFile;
	@Nullable
	private final NativeImageBackedTexture field_3244;
	private long field_3248;

	public LevelEntryWidget(class_528 arg, LevelSummary levelSummary, LevelStorage levelStorage) {
		this.field_3241 = arg;
		this.guiLevelSelect = arg.method_2752();
		this.levelSummary = levelSummary;
		this.client = MinecraftClient.getInstance();
		this.iconLocation = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(levelSummary.getName()) + "/icon");
		this.iconFile = levelStorage.resolveFile(levelSummary.getName(), "icon.png");
		if (!this.iconFile.isFile()) {
			this.iconFile = null;
		}

		this.field_3244 = this.method_2758();
	}

	@Override
	public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906();
		int n = this.method_1907();
		String string = this.levelSummary.getDisplayName();
		String string2 = this.levelSummary.getName() + " (" + DATE_FORMAT.format(new Date(this.levelSummary.lastPlayed())) + ")";
		String string3 = "";
		if (StringUtils.isEmpty(string)) {
			string = I18n.translate("selectWorld.world") + " " + (this.method_1908() + 1);
		}

		if (this.levelSummary.requiresConversion()) {
			string3 = I18n.translate("selectWorld.conversion") + " " + string3;
		} else {
			string3 = I18n.translate("gameMode." + this.levelSummary.getGameMode().getName());
			if (this.levelSummary.isHardcore()) {
				string3 = TextFormat.DARK_RED + I18n.translate("gameMode.hardcore") + TextFormat.RESET;
			}

			if (this.levelSummary.areCommandsAllowed()) {
				string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
			}

			String string4 = this.levelSummary.getVersionTextComponent().getFormattedText();
			if (this.levelSummary.method_256()) {
				if (this.levelSummary.method_260()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.RED + string4 + TextFormat.RESET;
				} else {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.ITALIC + string4 + TextFormat.RESET;
				}
			} else {
				string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + string4;
			}
		}

		this.client.fontRenderer.draw(string, (float)(n + 32 + 3), (float)(m + 1), 16777215);
		this.client.fontRenderer.draw(string2, (float)(n + 32 + 3), (float)(m + this.client.fontRenderer.FONT_HEIGHT + 3), 8421504);
		this.client
			.fontRenderer
			.draw(string3, (float)(n + 32 + 3), (float)(m + this.client.fontRenderer.FONT_HEIGHT + this.client.fontRenderer.FONT_HEIGHT + 3), 8421504);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(this.field_3244 != null ? this.iconLocation : field_3243);
		GlStateManager.enableBlend();
		Drawable.drawTexturedRect(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
		if (this.client.options.touchscreen || bl) {
			this.client.getTextureManager().bindTexture(field_3240);
			Drawable.drawRect(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = o < 32 ? 32 : 0;
			if (this.levelSummary.method_256()) {
				Drawable.drawTexturedRect(n, m, 32.0F, (float)p, 32, 32, 256.0F, 256.0F);
				if (this.levelSummary.isLegacyCustomizedWorld()) {
					Drawable.drawTexturedRect(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						TextComponent textComponent = new TranslatableTextComponent("selectWorld.tooltip.unsupported", this.levelSummary.getVersionTextComponent())
							.applyFormat(TextFormat.RED);
						this.guiLevelSelect.method_2739(this.client.fontRenderer.wrapStringToWidth(textComponent.getFormattedText(), 175));
					}
				} else if (this.levelSummary.method_260()) {
					Drawable.drawTexturedRect(n, m, 96.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.guiLevelSelect
							.method_2739(
								TextFormat.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
									+ "\n"
									+ TextFormat.RED
									+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
							);
					}
				} else if (!SharedConstants.getGameVersion().isStable()) {
					Drawable.drawTexturedRect(n, m, 64.0F, (float)p, 32, 32, 256.0F, 256.0F);
					if (o < 32) {
						this.guiLevelSelect
							.method_2739(
								TextFormat.GOLD + I18n.translate("selectWorld.tooltip.snapshot1") + "\n" + TextFormat.GOLD + I18n.translate("selectWorld.tooltip.snapshot2")
							);
					}
				}
			} else {
				Drawable.drawTexturedRect(n, m, 0.0F, (float)p, 32, 32, 256.0F, 256.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_3241.method_2751(this.method_1908());
		if (d - (double)this.method_1907() <= 32.0) {
			this.method_2768();
			return true;
		} else if (SystemUtil.getMeasuringTimeMili() - this.field_3248 < 250L) {
			this.method_2768();
			return true;
		} else {
			this.field_3248 = SystemUtil.getMeasuringTimeMili();
			return false;
		}
	}

	public void method_2768() {
		if (this.levelSummary.method_254() || this.levelSummary.isLegacyCustomizedWorld()) {
			String string = I18n.translate("selectWorld.backupQuestion");
			String string2 = I18n.translate(
				"selectWorld.backupWarning", this.levelSummary.getVersionTextComponent().getFormattedText(), SharedConstants.getGameVersion().getName()
			);
			if (this.levelSummary.isLegacyCustomizedWorld()) {
				string = I18n.translate("selectWorld.backupQuestion.customized");
				string2 = I18n.translate("selectWorld.backupWarning.customized");
			}

			this.client.openGui(new BackupPromptGui(this.guiLevelSelect, bl -> {
				if (bl) {
					String stringx = this.levelSummary.getName();
					BackupLevelGui.backupLevel(this.client.getLevelStorage(), stringx);
				}

				this.method_2767();
			}, string, string2));
		} else if (this.levelSummary.method_260()) {
			this.client
				.openGui(
					new YesNoGui(
						(bl, i) -> {
							if (bl) {
								try {
									this.method_2767();
								} catch (Exception var4) {
									LOGGER.error("Failure to open 'future world'", (Throwable)var4);
									this.client
										.openGui(
											new class_403(
												() -> this.client.openGui(this.guiLevelSelect),
												new TranslatableTextComponent("selectWorld.futureworld.error.title"),
												new TranslatableTextComponent("selectWorld.futureworld.error.text")
											)
										);
								}
							} else {
								this.client.openGui(this.guiLevelSelect);
							}
						},
						I18n.translate("selectWorld.versionQuestion"),
						I18n.translate("selectWorld.versionWarning", this.levelSummary.getVersionTextComponent().getFormattedText()),
						I18n.translate("selectWorld.versionJoinButton"),
						I18n.translate("gui.cancel"),
						0
					)
				);
		} else {
			this.method_2767();
		}
	}

	public void method_2755() {
		this.client
			.openGui(
				new YesNoGui(
					(bl, i) -> {
						if (bl) {
							this.client.openGui(new WorkingGui());
							LevelStorage levelStorage = this.client.getLevelStorage();
							levelStorage.clearAll();
							levelStorage.delete(this.levelSummary.getName());
							this.field_3241.method_2750(() -> this.guiLevelSelect.field_3220.getText(), true);
						}

						this.client.openGui(this.guiLevelSelect);
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
		this.client.openGui(new BackupLevelGui((bl, i) -> {
			if (bl) {
				this.field_3241.method_2750(() -> this.guiLevelSelect.field_3220.getText(), true);
			}

			this.client.openGui(this.guiLevelSelect);
		}, this.levelSummary.getName()));
	}

	public void method_2757() {
		try {
			this.client.openGui(new WorkingGui());
			NewLevelGui newLevelGui = new NewLevelGui(this.guiLevelSelect);
			WorldSaveHandler worldSaveHandler = this.client.getLevelStorage().method_242(this.levelSummary.getName(), null);
			LevelProperties levelProperties = worldSaveHandler.readProperties();
			RegionFileCache.clear();
			if (levelProperties != null) {
				newLevelGui.method_2737(levelProperties);
				if (this.levelSummary.isLegacyCustomizedWorld()) {
					this.client
						.openGui(
							new YesNoGui(
								(bl, i) -> {
									if (bl) {
										this.client.openGui(newLevelGui);
									} else {
										this.client.openGui(this.guiLevelSelect);
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
					this.client.openGui(newLevelGui);
				}
			}
		} catch (Exception var4) {
			LOGGER.error("Unable to recreate world", (Throwable)var4);
			this.client
				.openGui(
					new class_403(
						() -> this.client.openGui(this.guiLevelSelect),
						new TranslatableTextComponent("selectWorld.recreate.error.title"),
						new TranslatableTextComponent("selectWorld.recreate.error.text")
					)
				);
		}
	}

	private void method_2767() {
		this.client.getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
		if (this.client.getLevelStorage().exists(this.levelSummary.getName())) {
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
					this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture);
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
			this.client.getTextureManager().destroyTexture(this.iconLocation);
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
