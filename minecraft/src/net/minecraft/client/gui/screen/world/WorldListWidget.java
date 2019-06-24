package net.minecraft.client.gui.screen.world;

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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
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
public class WorldListWidget extends AlwaysSelectedEntryListWidget<WorldListWidget.LevelItem> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
	private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
	private final SelectWorldScreen parent;
	@Nullable
	private List<LevelSummary> levels;

	public WorldListWidget(
		SelectWorldScreen selectWorldScreen,
		MinecraftClient minecraftClient,
		int i,
		int j,
		int k,
		int l,
		int m,
		Supplier<String> supplier,
		@Nullable WorldListWidget worldListWidget
	) {
		super(minecraftClient, i, j, k, l, m);
		this.parent = selectWorldScreen;
		if (worldListWidget != null) {
			this.levels = worldListWidget.levels;
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
				this.minecraft.openScreen(new FatalErrorScreen(new TranslatableText("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.levels);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : this.levels) {
			if (levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) {
				this.addEntry(new WorldListWidget.LevelItem(this, levelSummary, this.minecraft.getLevelStorage()));
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

	public void method_20157(@Nullable WorldListWidget.LevelItem levelItem) {
		super.setSelected(levelItem);
		if (levelItem != null) {
			LevelSummary levelSummary = levelItem.level;
			NarratorManager.INSTANCE
				.narrate(
					new TranslatableText(
							"narrator.select",
							new TranslatableText(
								"narrator.select.world",
								levelSummary.getDisplayName(),
								new Date(levelSummary.getLastPlayed()),
								levelSummary.isHardcore() ? I18n.translate("gameMode.hardcore") : I18n.translate("gameMode." + levelSummary.getGameMode().getName()),
								levelSummary.hasCheats() ? I18n.translate("selectWorld.cheats") : "",
								levelSummary.getVersion()
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

	public Optional<WorldListWidget.LevelItem> method_20159() {
		return Optional.ofNullable(this.getSelected());
	}

	public SelectWorldScreen getParent() {
		return this.parent;
	}

	@Environment(EnvType.CLIENT)
	public final class LevelItem extends AlwaysSelectedEntryListWidget.Entry<WorldListWidget.LevelItem> implements AutoCloseable {
		private final MinecraftClient client;
		private final SelectWorldScreen screen;
		private final LevelSummary level;
		private final Identifier iconLocation;
		private File iconFile;
		@Nullable
		private final NativeImageBackedTexture icon;
		private long time;

		public LevelItem(WorldListWidget worldListWidget2, LevelSummary levelSummary, LevelStorage levelStorage) {
			this.screen = worldListWidget2.getParent();
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
			String string2 = this.level.getName() + " (" + WorldListWidget.DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
			if (StringUtils.isEmpty(string)) {
				string = I18n.translate("selectWorld.world") + " " + (i + 1);
			}

			String string3 = "";
			if (this.level.requiresConversion()) {
				string3 = I18n.translate("selectWorld.conversion") + " " + string3;
			} else {
				string3 = I18n.translate("gameMode." + this.level.getGameMode().getName());
				if (this.level.isHardcore()) {
					string3 = Formatting.DARK_RED + I18n.translate("gameMode.hardcore") + Formatting.RESET;
				}

				if (this.level.hasCheats()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
				}

				String string4 = this.level.getVersion().asFormattedString();
				if (this.level.isDifferentVersion()) {
					if (this.level.isFutureLevel()) {
						string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + Formatting.RED + string4 + Formatting.RESET;
					} else {
						string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + Formatting.ITALIC + string4 + Formatting.RESET;
					}
				} else {
					string3 = string3 + ", " + I18n.translate("selectWorld.version") + " " + string4;
				}
			}

			this.client.textRenderer.draw(string, (float)(k + 32 + 3), (float)(j + 1), 16777215);
			this.client.textRenderer.draw(string2, (float)(k + 32 + 3), (float)(j + 9 + 3), 8421504);
			this.client.textRenderer.draw(string3, (float)(k + 32 + 3), (float)(j + 9 + 9 + 3), 8421504);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : WorldListWidget.UNKNOWN_SERVER_LOCATION);
			GlStateManager.enableBlend();
			DrawableHelper.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			GlStateManager.disableBlend();
			if (this.client.options.touchscreen || bl) {
				this.client.getTextureManager().bindTexture(WorldListWidget.WORLD_SELECTION_LOCATION);
				DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = p < 32 ? 32 : 0;
				if (this.level.isDifferentVersion()) {
					DrawableHelper.blit(k, j, 32.0F, (float)q, 32, 32, 256, 256);
					if (this.level.isLegacyCustomizedWorld()) {
						DrawableHelper.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							Text text = new TranslatableText("selectWorld.tooltip.unsupported", this.level.getVersion()).formatted(Formatting.RED);
							this.screen.setTooltip(this.client.textRenderer.wrapStringToWidth(text.asFormattedString(), 175));
						}
					} else if (this.level.isFutureLevel()) {
						DrawableHelper.blit(k, j, 96.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.screen
								.setTooltip(
									Formatting.RED
										+ I18n.translate("selectWorld.tooltip.fromNewerVersion1")
										+ "\n"
										+ Formatting.RED
										+ I18n.translate("selectWorld.tooltip.fromNewerVersion2")
								);
						}
					} else if (!SharedConstants.getGameVersion().isStable()) {
						DrawableHelper.blit(k, j, 64.0F, (float)q, 32, 32, 256, 256);
						if (p < 32) {
							this.screen
								.setTooltip(
									Formatting.GOLD + I18n.translate("selectWorld.tooltip.snapshot1") + "\n" + Formatting.GOLD + I18n.translate("selectWorld.tooltip.snapshot2")
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
			WorldListWidget.this.method_20157(this);
			this.screen.worldSelected(WorldListWidget.this.method_20159().isPresent());
			if (d - (double)WorldListWidget.this.getRowLeft() <= 32.0) {
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
				Text text = new TranslatableText("selectWorld.backupQuestion");
				Text text2 = new TranslatableText("selectWorld.backupWarning", this.level.getVersion().asFormattedString(), SharedConstants.getGameVersion().getName());
				if (this.level.isLegacyCustomizedWorld()) {
					text = new TranslatableText("selectWorld.backupQuestion.customized");
					text2 = new TranslatableText("selectWorld.backupWarning.customized");
				}

				this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
					if (bl) {
						String string = this.level.getName();
						EditWorldScreen.backupLevel(this.client.getLevelStorage(), string);
					}

					this.start();
				}, text, text2, false));
			} else if (this.level.isFutureLevel()) {
				this.client
					.openScreen(
						new ConfirmScreen(
							bl -> {
								if (bl) {
									try {
										this.start();
									} catch (Exception var3) {
										WorldListWidget.LOGGER.error("Failure to open 'future world'", (Throwable)var3);
										this.client
											.openScreen(
												new NoticeScreen(
													() -> this.client.openScreen(this.screen),
													new TranslatableText("selectWorld.futureworld.error.title"),
													new TranslatableText("selectWorld.futureworld.error.text")
												)
											);
									}
								} else {
									this.client.openScreen(this.screen);
								}
							},
							new TranslatableText("selectWorld.versionQuestion"),
							new TranslatableText("selectWorld.versionWarning", this.level.getVersion().asFormattedString()),
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
					new ConfirmScreen(
						bl -> {
							if (bl) {
								this.client.openScreen(new ProgressScreen());
								LevelStorage levelStorage = this.client.getLevelStorage();
								levelStorage.deleteLevel(this.level.getName());
								WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
							}

							this.client.openScreen(this.screen);
						},
						new TranslatableText("selectWorld.deleteQuestion"),
						new TranslatableText("selectWorld.deleteWarning", this.level.getDisplayName()),
						I18n.translate("selectWorld.deleteButton"),
						I18n.translate("gui.cancel")
					)
				);
		}

		public void edit() {
			this.client.openScreen(new EditWorldScreen(bl -> {
				if (bl) {
					WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
				}

				this.client.openScreen(this.screen);
			}, this.level.getName()));
		}

		public void recreate() {
			try {
				this.client.openScreen(new ProgressScreen());
				CreateWorldScreen createWorldScreen = new CreateWorldScreen(this.screen);
				WorldSaveHandler worldSaveHandler = this.client.getLevelStorage().createSaveHandler(this.level.getName(), null);
				LevelProperties levelProperties = worldSaveHandler.readProperties();
				if (levelProperties != null) {
					createWorldScreen.recreateLevel(levelProperties);
					if (this.level.isLegacyCustomizedWorld()) {
						this.client
							.openScreen(
								new ConfirmScreen(
									bl -> this.client.openScreen((Screen)(bl ? createWorldScreen : this.screen)),
									new TranslatableText("selectWorld.recreate.customized.title"),
									new TranslatableText("selectWorld.recreate.customized.text"),
									I18n.translate("gui.proceed"),
									I18n.translate("gui.cancel")
								)
							);
					} else {
						this.client.openScreen(createWorldScreen);
					}
				}
			} catch (Exception var4) {
				WorldListWidget.LOGGER.error("Unable to recreate world", (Throwable)var4);
				this.client
					.openScreen(
						new NoticeScreen(
							() -> this.client.openScreen(this.screen),
							new TranslatableText("selectWorld.recreate.error.title"),
							new TranslatableText("selectWorld.recreate.error.text")
						)
					);
			}
		}

		private void start() {
			this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
					WorldListWidget.LOGGER.error("Invalid icon for world {}", this.level.getName(), var18);
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
