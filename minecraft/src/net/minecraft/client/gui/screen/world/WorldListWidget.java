package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldListWidget extends AlwaysSelectedEntryListWidget<WorldListWidget.Entry> {
	static final Identifier ERROR_HIGHLIGHTED_TEXTURE = new Identifier("world_list/error_highlighted");
	static final Identifier ERROR_TEXTURE = new Identifier("world_list/error");
	static final Identifier MARKED_JOIN_HIGHLIGHTED_TEXTURE = new Identifier("world_list/marked_join_highlighted");
	static final Identifier MARKED_JOIN_TEXTURE = new Identifier("world_list/marked_join");
	static final Identifier WARNING_HIGHLIGHTED_TEXTURE = new Identifier("world_list/warning_highlighted");
	static final Identifier WARNING_TEXTURE = new Identifier("world_list/warning");
	static final Identifier JOIN_HIGHLIGHTED_TEXTURE = new Identifier("world_list/join_highlighted");
	static final Identifier JOIN_TEXTURE = new Identifier("world_list/join");
	static final Logger LOGGER = LogUtils.getLogger();
	static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
	static final Text FROM_NEWER_VERSION_FIRST_LINE = Text.translatable("selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED);
	static final Text FROM_NEWER_VERSION_SECOND_LINE = Text.translatable("selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED);
	static final Text SNAPSHOT_FIRST_LINE = Text.translatable("selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD);
	static final Text SNAPSHOT_SECOND_LINE = Text.translatable("selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD);
	static final Text LOCKED_TEXT = Text.translatable("selectWorld.locked").formatted(Formatting.RED);
	static final Text CONVERSION_TOOLTIP = Text.translatable("selectWorld.conversion.tooltip").formatted(Formatting.RED);
	static final Text EXPERIMENTAL_TEXT = Text.translatable("selectWorld.experimental");
	private final SelectWorldScreen parent;
	private CompletableFuture<List<LevelSummary>> levelsFuture;
	@Nullable
	private List<LevelSummary> levels;
	private String search;
	private final WorldListWidget.LoadingEntry loadingEntry;

	public WorldListWidget(
		SelectWorldScreen parent,
		MinecraftClient client,
		int width,
		int height,
		int top,
		int bottom,
		int itemHeight,
		String search,
		@Nullable WorldListWidget oldWidget
	) {
		super(client, width, height, top, bottom, itemHeight);
		this.parent = parent;
		this.loadingEntry = new WorldListWidget.LoadingEntry(client);
		this.search = search;
		if (oldWidget != null) {
			this.levelsFuture = oldWidget.levelsFuture;
		} else {
			this.levelsFuture = this.loadLevels();
		}

		this.show(this.tryGet());
	}

	@Override
	protected void clearEntries() {
		this.children().forEach(WorldListWidget.Entry::close);
		super.clearEntries();
	}

	@Nullable
	private List<LevelSummary> tryGet() {
		try {
			return (List<LevelSummary>)this.levelsFuture.getNow(null);
		} catch (CancellationException | CompletionException var2) {
			return null;
		}
	}

	void load() {
		this.levelsFuture = this.loadLevels();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (KeyCodes.isToggle(keyCode)) {
			Optional<WorldListWidget.WorldEntry> optional = this.getSelectedAsOptional();
			if (optional.isPresent()) {
				((WorldListWidget.WorldEntry)optional.get()).play();
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		List<LevelSummary> list = this.tryGet();
		if (list != this.levels) {
			this.show(list);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	private void show(@Nullable List<LevelSummary> levels) {
		if (levels == null) {
			this.showLoadingScreen();
		} else {
			this.showSummaries(this.search, levels);
		}

		this.levels = levels;
	}

	public void setSearch(String search) {
		if (this.levels != null && !search.equals(this.search)) {
			this.showSummaries(search, this.levels);
		}

		this.search = search;
	}

	private CompletableFuture<List<LevelSummary>> loadLevels() {
		LevelStorage.LevelList levelList;
		try {
			levelList = this.client.getLevelStorage().getLevelList();
		} catch (LevelStorageException var3) {
			LOGGER.error("Couldn't load level list", (Throwable)var3);
			this.showUnableToLoadScreen(var3.getMessageText());
			return CompletableFuture.completedFuture(List.of());
		}

		if (levelList.isEmpty()) {
			CreateWorldScreen.create(this.client, null);
			return CompletableFuture.completedFuture(List.of());
		} else {
			return this.client.getLevelStorage().loadSummaries(levelList).exceptionally(throwable -> {
				this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Couldn't load level list"));
				return List.of();
			});
		}
	}

	private void showSummaries(String search, List<LevelSummary> summaries) {
		this.clearEntries();
		search = search.toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : summaries) {
			if (this.shouldShow(search, levelSummary)) {
				this.addEntry(new WorldListWidget.WorldEntry(this, levelSummary));
			}
		}

		this.narrateScreenIfNarrationEnabled();
	}

	private boolean shouldShow(String search, LevelSummary summary) {
		return summary.getDisplayName().toLowerCase(Locale.ROOT).contains(search) || summary.getName().toLowerCase(Locale.ROOT).contains(search);
	}

	private void showLoadingScreen() {
		this.clearEntries();
		this.addEntry(this.loadingEntry);
		this.narrateScreenIfNarrationEnabled();
	}

	private void narrateScreenIfNarrationEnabled() {
		this.parent.narrateScreenIfNarrationEnabled(true);
	}

	private void showUnableToLoadScreen(Text message) {
		this.client.setScreen(new FatalErrorScreen(Text.translatable("selectWorld.unable_to_load"), message));
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	public void setSelected(@Nullable WorldListWidget.Entry entry) {
		super.setSelected(entry);
		this.parent.worldSelected(entry != null && entry.isAvailable(), entry != null);
	}

	public Optional<WorldListWidget.WorldEntry> getSelectedAsOptional() {
		WorldListWidget.Entry entry = this.getSelectedOrNull();
		return entry instanceof WorldListWidget.WorldEntry worldEntry ? Optional.of(worldEntry) : Optional.empty();
	}

	public SelectWorldScreen getParent() {
		return this.parent;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		if (this.children().contains(this.loadingEntry)) {
			this.loadingEntry.appendNarrations(builder);
		} else {
			super.appendNarrations(builder);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<WorldListWidget.Entry> implements AutoCloseable {
		public abstract boolean isAvailable();

		public void close() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LoadingEntry extends WorldListWidget.Entry {
		private static final Text LOADING_LIST_TEXT = Text.translatable("selectWorld.loading_list");
		private final MinecraftClient client;

		public LoadingEntry(MinecraftClient client) {
			this.client = client;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_LIST_TEXT)) / 2;
			int j = y + (entryHeight - 9) / 2;
			context.drawText(this.client.textRenderer, LOADING_LIST_TEXT, i, j, 16777215, false);
			String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
			int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
			int l = j + 9;
			context.drawText(this.client.textRenderer, string, k, l, -8355712, false);
		}

		@Override
		public Text getNarration() {
			return LOADING_LIST_TEXT;
		}

		@Override
		public boolean isAvailable() {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public final class WorldEntry extends WorldListWidget.Entry implements AutoCloseable {
		private static final int field_32435 = 32;
		private static final int field_32436 = 32;
		private final MinecraftClient client;
		private final SelectWorldScreen screen;
		private final LevelSummary level;
		private final WorldIcon icon;
		@Nullable
		private Path iconPath;
		private long time;

		public WorldEntry(WorldListWidget levelList, LevelSummary level) {
			this.client = levelList.client;
			this.screen = levelList.getParent();
			this.level = level;
			this.icon = WorldIcon.forWorld(this.client.getTextureManager(), level.getName());
			this.iconPath = level.getIconPath();
			this.validateIconPath();
			this.loadIcon();
		}

		private void validateIconPath() {
			if (this.iconPath != null) {
				try {
					BasicFileAttributes basicFileAttributes = Files.readAttributes(this.iconPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					if (basicFileAttributes.isSymbolicLink()) {
						List<SymlinkEntry> list = this.client.getSymlinkFinder().validate(this.iconPath);
						if (!list.isEmpty()) {
							WorldListWidget.LOGGER.warn("{}", SymlinkValidationException.getMessage(this.iconPath, list));
							this.iconPath = null;
						} else {
							basicFileAttributes = Files.readAttributes(this.iconPath, BasicFileAttributes.class);
						}
					}

					if (!basicFileAttributes.isRegularFile()) {
						this.iconPath = null;
					}
				} catch (NoSuchFileException var3) {
					this.iconPath = null;
				} catch (IOException var4) {
					WorldListWidget.LOGGER.error("could not validate symlink", (Throwable)var4);
					this.iconPath = null;
				}
			}
		}

		@Override
		public Text getNarration() {
			Text text = Text.translatable("narrator.select.world_info", this.level.getDisplayName(), new Date(this.level.getLastPlayed()), this.level.getDetails());
			if (this.level.isLocked()) {
				text = ScreenTexts.joinSentences(text, WorldListWidget.LOCKED_TEXT);
			}

			if (this.level.isExperimental()) {
				text = ScreenTexts.joinSentences(text, WorldListWidget.EXPERIMENTAL_TEXT);
			}

			return Text.translatable("narrator.select", text);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			String string = this.level.getDisplayName();
			String string2 = this.level.getName();
			long l = this.level.getLastPlayed();
			if (l != -1L) {
				string2 = string2 + " (" + WorldListWidget.DATE_FORMAT.format(new Date(l)) + ")";
			}

			if (StringUtils.isEmpty(string)) {
				string = I18n.translate("selectWorld.world") + " " + (index + 1);
			}

			Text text = this.level.getDetails();
			context.drawText(this.client.textRenderer, string, x + 32 + 3, y + 1, 16777215, false);
			context.drawText(this.client.textRenderer, string2, x + 32 + 3, y + 9 + 3, -8355712, false);
			context.drawText(this.client.textRenderer, text, x + 32 + 3, y + 9 + 9 + 3, -8355712, false);
			RenderSystem.enableBlend();
			context.drawTexture(this.icon.getTextureId(), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
			if (this.client.options.getTouchscreen().getValue() || hovered) {
				context.fill(x, y, x + 32, y + 32, -1601138544);
				int i = mouseX - x;
				boolean bl = i < 32;
				Identifier identifier = bl ? WorldListWidget.JOIN_HIGHLIGHTED_TEXTURE : WorldListWidget.JOIN_TEXTURE;
				Identifier identifier2 = bl ? WorldListWidget.WARNING_HIGHLIGHTED_TEXTURE : WorldListWidget.WARNING_TEXTURE;
				Identifier identifier3 = bl ? WorldListWidget.ERROR_HIGHLIGHTED_TEXTURE : WorldListWidget.ERROR_TEXTURE;
				Identifier identifier4 = bl ? WorldListWidget.MARKED_JOIN_HIGHLIGHTED_TEXTURE : WorldListWidget.MARKED_JOIN_TEXTURE;
				if (this.level instanceof LevelSummary.SymlinkLevelSummary) {
					context.drawGuiTexture(identifier3, x, y, 32, 32);
					context.drawGuiTexture(identifier4, x, y, 32, 32);
					return;
				}

				if (this.level.isLocked()) {
					context.drawGuiTexture(identifier3, x, y, 32, 32);
					if (bl) {
						this.screen.setTooltip(this.client.textRenderer.wrapLines(WorldListWidget.LOCKED_TEXT, 175));
					}
				} else if (this.level.requiresConversion()) {
					context.drawGuiTexture(identifier3, x, y, 32, 32);
					if (bl) {
						this.screen.setTooltip(this.client.textRenderer.wrapLines(WorldListWidget.CONVERSION_TOOLTIP, 175));
					}
				} else if (this.level.isDifferentVersion()) {
					context.drawGuiTexture(identifier4, x, y, 32, 32);
					if (this.level.isFutureLevel()) {
						context.drawGuiTexture(identifier3, x, y, 32, 32);
						if (bl) {
							this.screen
								.setTooltip(
									ImmutableList.of(WorldListWidget.FROM_NEWER_VERSION_FIRST_LINE.asOrderedText(), WorldListWidget.FROM_NEWER_VERSION_SECOND_LINE.asOrderedText())
								);
						}
					} else if (!SharedConstants.getGameVersion().isStable()) {
						context.drawGuiTexture(identifier2, x, y, 32, 32);
						if (bl) {
							this.screen.setTooltip(ImmutableList.of(WorldListWidget.SNAPSHOT_FIRST_LINE.asOrderedText(), WorldListWidget.SNAPSHOT_SECOND_LINE.asOrderedText()));
						}
					}
				} else {
					context.drawGuiTexture(identifier, x, y, 32, 32);
				}
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.level.isUnavailable()) {
				return true;
			} else {
				WorldListWidget.this.setSelected((WorldListWidget.Entry)this);
				if (mouseX - (double)WorldListWidget.this.getRowLeft() <= 32.0) {
					this.play();
					return true;
				} else if (Util.getMeasuringTimeMs() - this.time < 250L) {
					this.play();
					return true;
				} else {
					this.time = Util.getMeasuringTimeMs();
					return true;
				}
			}
		}

		public void play() {
			if (!this.level.isUnavailable()) {
				if (this.level instanceof LevelSummary.SymlinkLevelSummary) {
					this.client.setScreen(SymlinkWarningScreen.world(this.screen));
				} else {
					LevelSummary.ConversionWarning conversionWarning = this.level.getConversionWarning();
					if (conversionWarning.promptsBackup()) {
						String string = "selectWorld.backupQuestion." + conversionWarning.getTranslationKeySuffix();
						String string2 = "selectWorld.backupWarning." + conversionWarning.getTranslationKeySuffix();
						MutableText mutableText = Text.translatable(string);
						if (conversionWarning.needsBoldRedFormatting()) {
							mutableText.formatted(Formatting.BOLD, Formatting.RED);
						}

						Text text = Text.translatable(string2, this.level.getVersion(), SharedConstants.getGameVersion().getName());
						this.client.setScreen(new BackupPromptScreen(this.screen, (backup, eraseCache) -> {
							if (backup) {
								String stringx = this.level.getName();

								try (LevelStorage.Session session = this.client.getLevelStorage().createSession(stringx)) {
									EditWorldScreen.backupLevel(session);
								} catch (IOException var9) {
									SystemToast.addWorldAccessFailureToast(this.client, stringx);
									WorldListWidget.LOGGER.error("Failed to backup level {}", stringx, var9);
								} catch (SymlinkValidationException var10) {
									WorldListWidget.LOGGER.warn("{}", var10.getMessage());
									this.client.setScreen(SymlinkWarningScreen.world(this.screen));
								}
							}

							this.start();
						}, mutableText, text, false));
					} else if (this.level.isFutureLevel()) {
						this.client
							.setScreen(
								new ConfirmScreen(
									confirmed -> {
										if (confirmed) {
											try {
												this.start();
											} catch (Exception var3x) {
												WorldListWidget.LOGGER.error("Failure to open 'future world'", (Throwable)var3x);
												this.client
													.setScreen(
														new NoticeScreen(
															() -> this.client.setScreen(this.screen),
															Text.translatable("selectWorld.futureworld.error.title"),
															Text.translatable("selectWorld.futureworld.error.text")
														)
													);
											}
										} else {
											this.client.setScreen(this.screen);
										}
									},
									Text.translatable("selectWorld.versionQuestion"),
									Text.translatable("selectWorld.versionWarning", this.level.getVersion()),
									Text.translatable("selectWorld.versionJoinButton"),
									ScreenTexts.CANCEL
								)
							);
					} else {
						this.start();
					}
				}
			}
		}

		public void deleteIfConfirmed() {
			this.client
				.setScreen(
					new ConfirmScreen(
						confirmed -> {
							if (confirmed) {
								this.client.setScreen(new ProgressScreen(true));
								this.delete();
							}

							this.client.setScreen(this.screen);
						},
						Text.translatable("selectWorld.deleteQuestion"),
						Text.translatable("selectWorld.deleteWarning", this.level.getDisplayName()),
						Text.translatable("selectWorld.deleteButton"),
						ScreenTexts.CANCEL
					)
				);
		}

		public void delete() {
			LevelStorage levelStorage = this.client.getLevelStorage();
			String string = this.level.getName();

			try (LevelStorage.Session session = levelStorage.createSessionWithoutSymlinkCheck(string)) {
				session.deleteSessionLock();
			} catch (IOException var8) {
				SystemToast.addWorldDeleteFailureToast(this.client, string);
				WorldListWidget.LOGGER.error("Failed to delete world {}", string, var8);
			}

			WorldListWidget.this.load();
		}

		public void edit() {
			if (this.level instanceof LevelSummary.SymlinkLevelSummary) {
				this.client.setScreen(SymlinkWarningScreen.world(this.screen));
			} else {
				this.openReadingWorldScreen();
				String string = this.level.getName();

				try {
					LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
					this.client.setScreen(new EditWorldScreen(edited -> {
						try {
							session.close();
						} catch (IOException var5) {
							WorldListWidget.LOGGER.error("Failed to unlock level {}", string, var5);
						}

						if (edited) {
							WorldListWidget.this.load();
						}

						this.client.setScreen(this.screen);
					}, session));
				} catch (IOException var3) {
					SystemToast.addWorldAccessFailureToast(this.client, string);
					WorldListWidget.LOGGER.error("Failed to access level {}", string, var3);
					WorldListWidget.this.load();
				} catch (SymlinkValidationException var4) {
					WorldListWidget.LOGGER.warn("{}", var4.getMessage());
					this.client.setScreen(SymlinkWarningScreen.world(this.screen));
				}
			}
		}

		public void recreate() {
			if (this.level instanceof LevelSummary.SymlinkLevelSummary) {
				this.client.setScreen(SymlinkWarningScreen.world(this.screen));
			} else {
				this.openReadingWorldScreen();

				try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName())) {
					Pair<LevelInfo, GeneratorOptionsHolder> pair = this.client.createIntegratedServerLoader().loadForRecreation(session);
					LevelInfo levelInfo = pair.getFirst();
					GeneratorOptionsHolder generatorOptionsHolder = pair.getSecond();
					Path path = CreateWorldScreen.copyDataPack(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
					if (generatorOptionsHolder.generatorOptions().isLegacyCustomizedType()) {
						this.client
							.setScreen(
								new ConfirmScreen(
									confirmed -> this.client
											.setScreen((Screen)(confirmed ? CreateWorldScreen.create(this.client, this.screen, levelInfo, generatorOptionsHolder, path) : this.screen)),
									Text.translatable("selectWorld.recreate.customized.title"),
									Text.translatable("selectWorld.recreate.customized.text"),
									ScreenTexts.PROCEED,
									ScreenTexts.CANCEL
								)
							);
					} else {
						this.client.setScreen(CreateWorldScreen.create(this.client, this.screen, levelInfo, generatorOptionsHolder, path));
					}
				} catch (SymlinkValidationException var8) {
					WorldListWidget.LOGGER.warn("{}", var8.getMessage());
					this.client.setScreen(SymlinkWarningScreen.world(this.screen));
				} catch (Exception var9) {
					WorldListWidget.LOGGER.error("Unable to recreate world", (Throwable)var9);
					this.client
						.setScreen(
							new NoticeScreen(
								() -> this.client.setScreen(this.screen), Text.translatable("selectWorld.recreate.error.title"), Text.translatable("selectWorld.recreate.error.text")
							)
						);
				}
			}
		}

		private void start() {
			this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			if (this.client.getLevelStorage().levelExists(this.level.getName())) {
				this.openReadingWorldScreen();
				this.client.createIntegratedServerLoader().start(this.screen, this.level.getName());
			}
		}

		private void openReadingWorldScreen() {
			this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
		}

		private void loadIcon() {
			boolean bl = this.iconPath != null && Files.isRegularFile(this.iconPath, new LinkOption[0]);
			if (bl) {
				try {
					InputStream inputStream = Files.newInputStream(this.iconPath);

					try {
						this.icon.load(NativeImage.read(inputStream));
					} catch (Throwable var6) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var5) {
								var6.addSuppressed(var5);
							}
						}

						throw var6;
					}

					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Throwable var7) {
					WorldListWidget.LOGGER.error("Invalid icon for world {}", this.level.getName(), var7);
					this.iconPath = null;
				}
			} else {
				this.icon.destroy();
			}
		}

		@Override
		public void close() {
			this.icon.close();
		}

		public String getLevelDisplayName() {
			return this.level.getDisplayName();
		}

		@Override
		public boolean isAvailable() {
			return !this.level.isUnavailable();
		}
	}
}
