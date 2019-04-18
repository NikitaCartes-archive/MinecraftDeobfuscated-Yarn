package net.minecraft.client.gui.menu;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LevelListWidget extends AlwaysSelectedEntryListWidget<LevelListWidget.LevelItem> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
	private final LevelSelectScreen parent;
	@Nullable
	private List<LevelSummary> levels;

	public LevelListWidget(
		LevelSelectScreen levelSelectScreen,
		MinecraftClient minecraftClient,
		int i,
		int j,
		int k,
		int l,
		int m,
		Supplier<String> supplier,
		@Nullable LevelListWidget levelListWidget
	) {
		super(minecraftClient, i, j, k, l, m);
		this.parent = levelSelectScreen;
		if (levelListWidget != null) {
			this.levels = levelListWidget.levels;
		}

		this.filter(supplier, false);
	}

	public void filter(Supplier<String> supplier, boolean bl) {
		this.clearEntries();
		LevelStorage levelStorage = this.minecraft.getLevelStorage();
		if (this.levels == null || bl) {
			try {
				this.levels = levelStorage.getLevelList();
			} catch (LevelStorageException var7) {
				LOGGER.error("Couldn't load level list", (Throwable)var7);
				this.minecraft.openScreen(new SevereErrorScreen(new TranslatableTextComponent("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.levels);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : this.levels) {
			if (levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) {
				this.addEntry(new LevelListWidget.LevelItem(this, levelSummary, this.minecraft.getLevelStorage()));
			}
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isFocused() {
		return this.parent.getFocused() == this;
	}

	public void method_20157(@Nullable LevelListWidget.LevelItem levelItem) {
		super.setSelected(levelItem);
		if (levelItem != null) {
			LevelSummary levelSummary = levelItem.level;
			NarratorManager.INSTANCE
				.method_19788(
					new TranslatableTextComponent(
							"narrator.select",
							new TranslatableTextComponent(
								"narrator.select.world",
								levelSummary.getDisplayName(),
								new Date(levelSummary.getLastPlayed()),
								levelSummary.isHardcore() ? I18n.translate("gameMode.hardcore") : I18n.translate("gameMode." + levelSummary.getGameMode().getName()),
								levelSummary.hasCheats() ? I18n.translate("selectWorld.cheats") : "",
								levelSummary.getVersionTextComponent()
							)
						)
						.getString()
				);
		}
	}

	@Override
	protected void moveSelection(int i) {
		super.moveSelection(i);
		this.parent.worldSelected(true);
	}

	public Optional<LevelListWidget.LevelItem> method_20159() {
		return Optional.ofNullable(this.getSelected());
	}

	public LevelSelectScreen getParent() {
		return this.parent;
	}

	@Environment(EnvType.CLIENT)
	public final class LevelItem extends AlwaysSelectedEntryListWidget.Entry<LevelListWidget.LevelItem> implements AutoCloseable {
		private final MinecraftClient client;
		private final LevelSelectScreen screen;
		private final LevelSummary level;
		private final Identifier iconLocation;
		private File iconFile;
		@Nullable
		private final NativeImageBackedTexture icon;
		private long time;

		public LevelItem(LevelListWidget levelListWidget2, LevelSummary levelSummary, LevelStorage levelStorage) {
			this.screen = levelListWidget2.getParent();
			this.level = levelSummary;
			this.client = MinecraftClient.getInstance();
			this.iconLocation = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(levelSummary.getName()) + "/icon");
			this.iconFile = levelStorage.resolveFile(levelSummary.getName(), "icon.png");
			if (!this.iconFile.isFile()) {
				this.iconFile = null;
			}

			this.icon = this.getIconTexture();
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			String string = this.level.getDisplayName();
			String string2 = this.level.getName() + " (" + LevelListWidget.DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
			if (StringUtils.isEmpty(string)) {
				string = I18n.translate("selectWorld.world") + " " + (i + 1);
			}

			String string3 = "";
			if (this.level.requiresConversion()) {
				string3 = I18n.translate("selectWorld.conversion") + " " + string3;
			} else {
				string3 = I18n.translate("gameMode." + this.level.getGameMode().getName());
				if (this.level.isHardcore()) {
					string3 = TextFormat.field_1079 + I18n.translate("gameMode.hardcore") + TextFormat.RESET;
				}

				if (this.level.hasCheats()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
				}

				String string4 = this.level.getVersionTextComponent().getFormattedText();
				if (this.level.isDifferentVersion()) {
					if (this.level.isFutureLevel()) {
						string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.field_1061 + string4 + TextFormat.RESET;
					} else {
						string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + TextFormat.field_1056 + string4 + TextFormat.RESET;
					}
				} else {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + string4;
				}
			}

			this.client.textRenderer.draw(string, (float)(k + 32 + 3), (float)(j + 1), 16777215);
			this.client.textRenderer.draw(string2, (float)(k + 32 + 3), (float)(j + 9 + 3), 8421504);
			this.client.textRenderer.draw(string3, (float)(k + 32 + 3), (float)(j + 9 + 9 + 3), 8421504);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : LevelListWidget.UNKNOWN_SERVER_LOCATION);
			GlStateManager.enableBlend();
			DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			GlStateManager.disableBlend();
			if (this.client.options.touchscreen || bl) {
				this.client.getTextureManager().bindTexture(LevelListWidget.WORLD_SELECTION_LOCATION);
				DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = p < 32 ? 32 : 0;
				if (this.level.isDifferentVersion()) {
					DrawableHelper.blit(k, j, 32.0F, (float)q, 32, 32, 256, 256);
					if (this.level.isLegacyCustomizedWorld()) {
						DrawableHelper.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							TextComponent textComponent = new TranslatableTextComponent("selectWorld.tooltip.unsupported", this.level.getVersionTextComponent())
								.applyFormat(TextFormat.field_1061);
							this.screen.setTooltip(this.client.textRenderer.wrapStringToWidth(textComponent.getFormattedText(), 175));
						}
					} else if (this.level.isFutureLevel()) {
						DrawableHelper.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.screen
								.setTooltip(
									TextFormat.field_1061
										+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
										+ "\n"
										+ TextFormat.field_1061
										+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
								);
						}
					} else if (!SharedConstants.getGameVersion().isStable()) {
						DrawableHelper.blit(k, j, 64.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.screen
								.setTooltip(
									TextFormat.field_1065
										+ I18n.translate("selectWorld.tooltip.snapshot1")
										+ "\n"
										+ TextFormat.field_1065
										+ I18n.translate("selectWorld.tooltip.snapshot2")
								);
						}
					}
				} else {
					DrawableHelper.blit(k, j, 0.0F, (float)q, 32, 32, 256, 256);
				}
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			LevelListWidget.this.method_20157(this);
			this.screen.worldSelected(LevelListWidget.this.method_20159().isPresent());
			if (d - (double)LevelListWidget.this.getRowLeft() <= 32.0) {
				this.play();
				return true;
			} else if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
				this.play();
				return true;
			} else {
				this.time = SystemUtil.getMeasuringTimeMs();
				return false;
			}
		}

		public void play() {
			if (this.level.isOutdatedLevel() || this.level.isLegacyCustomizedWorld()) {
				TextComponent textComponent = new TranslatableTextComponent("selectWorld.backupQuestion");
				TextComponent textComponent2 = new TranslatableTextComponent(
					"selectWorld.backupWarning", this.level.getVersionTextComponent().getFormattedText(), SharedConstants.getGameVersion().getName()
				);
				if (this.level.isLegacyCustomizedWorld()) {
					textComponent = new TranslatableTextComponent("selectWorld.backupQuestion.customized");
					textComponent2 = new TranslatableTextComponent("selectWorld.backupWarning.customized");
				}

				this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
					if (bl) {
						String string = this.level.getName();
						EditLevelScreen.backupLevel(this.client.getLevelStorage(), string);
					}

					this.start();
				}, textComponent, textComponent2, false));
			} else if (this.level.isFutureLevel()) {
				this.client
					.openScreen(
						new YesNoScreen(
							bl -> {
								if (bl) {
									try {
										this.start();
									} catch (Exception var3) {
										LevelListWidget.LOGGER.error("Failure to open 'future world'", (Throwable)var3);
										this.client
											.openScreen(
												new NoticeScreen(
													() -> this.client.openScreen(this.screen),
													new TranslatableTextComponent("selectWorld.futureworld.error.title"),
													new TranslatableTextComponent("selectWorld.futureworld.error.text")
												)
											);
									}
								} else {
									this.client.openScreen(this.screen);
								}
							},
							new TranslatableTextComponent("selectWorld.versionQuestion"),
							new TranslatableTextComponent("selectWorld.versionWarning", this.level.getVersionTextComponent().getFormattedText()),
							I18n.translate("selectWorld.versionJoinButton"),
							I18n.translate("gui.cancel")
						)
					);
			} else {
				this.start();
			}
		}

		public void delete() {
			this.client
				.openScreen(
					new YesNoScreen(
						bl -> {
							if (bl) {
								this.client.openScreen(new WorkingScreen());
								LevelStorage levelStorage = this.client.getLevelStorage();
								levelStorage.deleteLevel(this.level.getName());
								LevelListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
							}

							this.client.openScreen(this.screen);
						},
						new TranslatableTextComponent("selectWorld.deleteQuestion"),
						new TranslatableTextComponent("selectWorld.deleteWarning", this.level.getDisplayName()),
						I18n.translate("selectWorld.deleteButton"),
						I18n.translate("gui.cancel")
					)
				);
		}

		public void edit() {
			this.client.openScreen(new EditLevelScreen(bl -> {
				if (bl) {
					LevelListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
				}

				this.client.openScreen(this.screen);
			}, this.level.getName()));
		}

		public void recreate() {
			try {
				this.client.openScreen(new WorkingScreen());
				NewLevelScreen newLevelScreen = new NewLevelScreen(this.screen);
				WorldSaveHandler worldSaveHandler = this.client.getLevelStorage().createSaveHandler(this.level.getName(), null);
				LevelProperties levelProperties = worldSaveHandler.readProperties();
				if (levelProperties != null) {
					newLevelScreen.recreateLevel(levelProperties);
					if (this.level.isLegacyCustomizedWorld()) {
						this.client
							.openScreen(
								new YesNoScreen(
									bl -> this.client.openScreen((Screen)(bl ? newLevelScreen : this.screen)),
									new TranslatableTextComponent("selectWorld.recreate.customized.title"),
									new TranslatableTextComponent("selectWorld.recreate.customized.text"),
									I18n.translate("gui.proceed"),
									I18n.translate("gui.cancel")
								)
							);
					} else {
						this.client.openScreen(newLevelScreen);
					}
				}
			} catch (Exception var4) {
				LevelListWidget.LOGGER.error("Unable to recreate world", (Throwable)var4);
				this.client
					.openScreen(
						new NoticeScreen(
							() -> this.client.openScreen(this.screen),
							new TranslatableTextComponent("selectWorld.recreate.error.title"),
							new TranslatableTextComponent("selectWorld.recreate.error.text")
						)
					);
			}
		}

		private void start() {
			this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
			if (this.client.getLevelStorage().levelExists(this.level.getName())) {
				this.client.startIntegratedServer(this.level.getName(), this.level.getDisplayName(), null);
			}
		}

		@Nullable
		private NativeImageBackedTexture getIconTexture() {
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
					LevelListWidget.LOGGER.error("Invalid icon for world {}", this.level.getName(), var18);
					this.iconFile = null;
					return null;
				}
			} else {
				this.client.getTextureManager().destroyTexture(this.iconLocation);
				return null;
			}
		}

		public void close() {
			if (this.icon != null) {
				this.icon.close();
			}
		}
	}
}
