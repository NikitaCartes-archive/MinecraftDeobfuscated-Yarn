/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
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
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    static final Text FROM_NEWER_VERSION_FIRST_LINE = Text.translatable("selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED);
    static final Text FROM_NEWER_VERSION_SECOND_LINE = Text.translatable("selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED);
    static final Text SNAPSHOT_FIRST_LINE = Text.translatable("selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD);
    static final Text SNAPSHOT_SECOND_LINE = Text.translatable("selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD);
    static final Text LOCKED_TEXT = Text.translatable("selectWorld.locked").formatted(Formatting.RED);
    static final Text CONVERSION_TOOLTIP = Text.translatable("selectWorld.conversion.tooltip").formatted(Formatting.RED);
    private final SelectWorldScreen parent;
    private CompletableFuture<List<LevelSummary>> levelsFuture;
    @Nullable
    private List<LevelSummary> levels;
    private String search;
    private final LoadingEntry loadingEntry;

    public WorldListWidget(SelectWorldScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, String search, @Nullable WorldListWidget oldWidget) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.loadingEntry = new LoadingEntry(client);
        this.search = search;
        this.levelsFuture = oldWidget != null ? oldWidget.levelsFuture : this.loadLevels();
        this.show(this.tryGet());
    }

    @Nullable
    private List<LevelSummary> tryGet() {
        try {
            return this.levelsFuture.getNow(null);
        } catch (CancellationException | CompletionException runtimeException) {
            return null;
        }
    }

    void load() {
        this.levelsFuture = this.loadLevels();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        List<LevelSummary> list = this.tryGet();
        if (list != this.levels) {
            this.show(list);
        }
        super.render(matrices, mouseX, mouseY, delta);
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
        } catch (LevelStorageException levelStorageException) {
            LOGGER.error("Couldn't load level list", levelStorageException);
            this.showUnableToLoadScreen(levelStorageException.getMessageText());
            return CompletableFuture.completedFuture(List.of());
        }
        if (levelList.isEmpty()) {
            CreateWorldScreen.create(this.client, null);
            return CompletableFuture.completedFuture(List.of());
        }
        return this.client.getLevelStorage().loadSummaries(levelList).exceptionally(throwable -> {
            this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Couldn't load level list"));
            return List.of();
        });
    }

    private void showSummaries(String search, List<LevelSummary> summaries) {
        this.clearEntries();
        search = search.toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : summaries) {
            if (!this.shouldShow(search, levelSummary)) continue;
            this.addEntry(new WorldEntry(this, levelSummary));
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

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.parent.worldSelected(entry != null && entry.isAvailable());
    }

    public Optional<WorldEntry> getSelectedAsOptional() {
        Entry entry = (Entry)this.getSelectedOrNull();
        if (entry instanceof WorldEntry) {
            WorldEntry worldEntry = (WorldEntry)entry;
            return Optional.of(worldEntry);
        }
        return Optional.empty();
    }

    public SelectWorldScreen getParent() {
        return this.parent;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.children().contains(this.loadingEntry)) {
            this.loadingEntry.appendNarrations(builder);
            return;
        }
        super.appendNarrations(builder);
    }

    @Environment(value=EnvType.CLIENT)
    public static class LoadingEntry
    extends Entry {
        private static final Text LOADING_LIST_TEXT = Text.translatable("selectWorld.loading_list");
        private final MinecraftClient client;

        public LoadingEntry(MinecraftClient client) {
            this.client = client;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_LIST_TEXT)) / 2;
            int j = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
            this.client.textRenderer.draw(matrices, LOADING_LIST_TEXT, (float)i, (float)j, 0xFFFFFF);
            String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
            int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
            int l = j + this.client.textRenderer.fontHeight;
            this.client.textRenderer.draw(matrices, string, (float)k, (float)l, 0x808080);
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

    @Environment(value=EnvType.CLIENT)
    public final class WorldEntry
    extends Entry
    implements AutoCloseable {
        private static final int field_32435 = 32;
        private static final int field_32436 = 32;
        private static final int field_32437 = 0;
        private static final int field_32438 = 32;
        private static final int field_32439 = 64;
        private static final int field_32440 = 96;
        private static final int field_32441 = 0;
        private static final int field_32442 = 32;
        private final MinecraftClient client;
        private final SelectWorldScreen screen;
        private final LevelSummary level;
        private final Identifier iconLocation;
        @Nullable
        private Path iconPath;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;

        public WorldEntry(WorldListWidget levelList, LevelSummary level) {
            this.client = levelList.client;
            this.screen = levelList.getParent();
            this.level = level;
            String string = level.getName();
            this.iconLocation = new Identifier("minecraft", "worlds/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon");
            this.iconPath = level.getIconPath();
            if (!Files.isRegularFile(this.iconPath, new LinkOption[0])) {
                this.iconPath = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public Text getNarration() {
            MutableText text = Text.translatable("narrator.select.world", this.level.getDisplayName(), new Date(this.level.getLastPlayed()), this.level.isHardcore() ? Text.translatable("gameMode.hardcore") : Text.translatable("gameMode." + this.level.getGameMode().getName()), this.level.hasCheats() ? Text.translatable("selectWorld.cheats") : ScreenTexts.EMPTY, this.level.getVersion());
            MutableText text2 = this.level.isLocked() ? ScreenTexts.joinSentences(text, LOCKED_TEXT) : text;
            return Text.translatable("narrator.select", text2);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Object string = this.level.getDisplayName();
            String string2 = this.level.getName() + " (" + DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = I18n.translate("selectWorld.world", new Object[0]) + " " + (index + 1);
            }
            Text text = this.level.getDetails();
            this.client.textRenderer.draw(matrices, (String)string, (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
            this.client.textRenderer.draw(matrices, string2, (float)(x + 32 + 3), (float)(y + this.client.textRenderer.fontHeight + 3), 0x808080);
            this.client.textRenderer.draw(matrices, text, (float)(x + 32 + 3), (float)(y + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3), 0x808080);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.client.options.getTouchscreen().getValue().booleanValue() || hovered) {
                int j;
                RenderSystem.setShaderTexture(0, WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                int i = mouseX - x;
                boolean bl = i < 32;
                int n = j = bl ? 32 : 0;
                if (this.level.isLocked()) {
                    DrawableHelper.drawTexture(matrices, x, y, 96.0f, j, 32, 32, 256, 256);
                    if (bl) {
                        this.screen.setTooltip(this.client.textRenderer.wrapLines(LOCKED_TEXT, 175));
                    }
                } else if (this.level.requiresConversion()) {
                    DrawableHelper.drawTexture(matrices, x, y, 96.0f, j, 32, 32, 256, 256);
                    if (bl) {
                        this.screen.setTooltip(this.client.textRenderer.wrapLines(CONVERSION_TOOLTIP, 175));
                    }
                } else if (this.level.isDifferentVersion()) {
                    DrawableHelper.drawTexture(matrices, x, y, 32.0f, j, 32, 32, 256, 256);
                    if (this.level.isFutureLevel()) {
                        DrawableHelper.drawTexture(matrices, x, y, 96.0f, j, 32, 32, 256, 256);
                        if (bl) {
                            this.screen.setTooltip(ImmutableList.of(FROM_NEWER_VERSION_FIRST_LINE.asOrderedText(), FROM_NEWER_VERSION_SECOND_LINE.asOrderedText()));
                        }
                    } else if (!SharedConstants.getGameVersion().isStable()) {
                        DrawableHelper.drawTexture(matrices, x, y, 64.0f, j, 32, 32, 256, 256);
                        if (bl) {
                            this.screen.setTooltip(ImmutableList.of(SNAPSHOT_FIRST_LINE.asOrderedText(), SNAPSHOT_SECOND_LINE.asOrderedText()));
                        }
                    }
                } else {
                    DrawableHelper.drawTexture(matrices, x, y, 0.0f, j, 32, 32, 256, 256);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.level.isUnavailable()) {
                return true;
            }
            WorldListWidget.this.setSelected(this);
            this.screen.worldSelected(WorldListWidget.this.getSelectedAsOptional().isPresent());
            if (mouseX - (double)WorldListWidget.this.getRowLeft() <= 32.0) {
                this.play();
                return true;
            }
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.play();
                return true;
            }
            this.time = Util.getMeasuringTimeMs();
            return true;
        }

        public void play() {
            if (this.level.isUnavailable()) {
                return;
            }
            LevelSummary.ConversionWarning conversionWarning = this.level.getConversionWarning();
            if (conversionWarning.promptsBackup()) {
                String string = "selectWorld.backupQuestion." + conversionWarning.getTranslationKeySuffix();
                String string2 = "selectWorld.backupWarning." + conversionWarning.getTranslationKeySuffix();
                MutableText mutableText = Text.translatable(string);
                if (conversionWarning.needsBoldRedFormatting()) {
                    mutableText.formatted(Formatting.BOLD, Formatting.RED);
                }
                MutableText text = Text.translatable(string2, this.level.getVersion(), SharedConstants.getGameVersion().getName());
                this.client.setScreen(new BackupPromptScreen(this.screen, (backup, eraseCache) -> {
                    if (backup) {
                        String string = this.level.getName();
                        try (LevelStorage.Session session = this.client.getLevelStorage().createSession(string);){
                            EditWorldScreen.backupLevel(session);
                        } catch (IOException iOException) {
                            SystemToast.addWorldAccessFailureToast(this.client, string);
                            LOGGER.error("Failed to backup level {}", (Object)string, (Object)iOException);
                        }
                    }
                    this.start();
                }, mutableText, text, false));
            } else if (this.level.isFutureLevel()) {
                this.client.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        try {
                            this.start();
                        } catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", exception);
                            this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.translatable("selectWorld.futureworld.error.title"), Text.translatable("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.client.setScreen(this.screen);
                    }
                }, Text.translatable("selectWorld.versionQuestion"), Text.translatable("selectWorld.versionWarning", this.level.getVersion()), Text.translatable("selectWorld.versionJoinButton"), ScreenTexts.CANCEL));
            } else {
                this.start();
            }
        }

        public void deleteIfConfirmed() {
            this.client.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    this.client.setScreen(new ProgressScreen(true));
                    this.delete();
                }
                this.client.setScreen(this.screen);
            }, Text.translatable("selectWorld.deleteQuestion"), Text.translatable("selectWorld.deleteWarning", this.level.getDisplayName()), Text.translatable("selectWorld.deleteButton"), ScreenTexts.CANCEL));
        }

        public void delete() {
            LevelStorage levelStorage = this.client.getLevelStorage();
            String string = this.level.getName();
            try (LevelStorage.Session session = levelStorage.createSession(string);){
                session.deleteSessionLock();
            } catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast(this.client, string);
                LOGGER.error("Failed to delete world {}", (Object)string, (Object)iOException);
            }
            WorldListWidget.this.load();
        }

        public void edit() {
            this.openReadingWorldScreen();
            String string = this.level.getName();
            try {
                LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
                this.client.setScreen(new EditWorldScreen(edited -> {
                    try {
                        session.close();
                    } catch (IOException iOException) {
                        LOGGER.error("Failed to unlock level {}", (Object)string, (Object)iOException);
                    }
                    if (edited) {
                        WorldListWidget.this.load();
                    }
                    this.client.setScreen(this.screen);
                }, session));
            } catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, string);
                LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
                WorldListWidget.this.load();
            }
        }

        public void recreate() {
            this.openReadingWorldScreen();
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName());){
                Pair<LevelInfo, GeneratorOptionsHolder> pair = this.client.createIntegratedServerLoader().loadForRecreation(session);
                LevelInfo levelInfo = pair.getFirst();
                GeneratorOptionsHolder generatorOptionsHolder = pair.getSecond();
                Path path = CreateWorldScreen.copyDataPack(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
                if (generatorOptionsHolder.generatorOptions().isLegacyCustomizedType()) {
                    this.client.setScreen(new ConfirmScreen(confirmed -> this.client.setScreen(confirmed ? CreateWorldScreen.create(this.screen, levelInfo, generatorOptionsHolder, path) : this.screen), Text.translatable("selectWorld.recreate.customized.title"), Text.translatable("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
                } else {
                    this.client.setScreen(CreateWorldScreen.create(this.screen, levelInfo, generatorOptionsHolder, path));
                }
            } catch (Exception exception) {
                LOGGER.error("Unable to recreate world", exception);
                this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.translatable("selectWorld.recreate.error.title"), Text.translatable("selectWorld.recreate.error.text")));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.openReadingWorldScreen();
                this.client.createIntegratedServerLoader().start(this.screen, this.level.getName());
            }
        }

        private void openReadingWorldScreen() {
            this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
        }

        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            boolean bl2 = bl = this.iconPath != null && Files.isRegularFile(this.iconPath, new LinkOption[0]);
            if (bl) {
                NativeImageBackedTexture nativeImageBackedTexture;
                block9: {
                    InputStream inputStream = Files.newInputStream(this.iconPath, new OpenOption[0]);
                    try {
                        NativeImage nativeImage = NativeImage.read(inputStream);
                        Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                        Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                        NativeImageBackedTexture nativeImageBackedTexture2 = new NativeImageBackedTexture(nativeImage);
                        this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture2);
                        nativeImageBackedTexture = nativeImageBackedTexture2;
                        if (inputStream == null) break block9;
                    } catch (Throwable throwable) {
                        try {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (Throwable throwable3) {
                            LOGGER.error("Invalid icon for world {}", (Object)this.level.getName(), (Object)throwable3);
                            this.iconPath = null;
                            return null;
                        }
                    }
                    inputStream.close();
                }
                return nativeImageBackedTexture;
            }
            this.client.getTextureManager().destroyTexture(this.iconLocation);
            return null;
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }

        public String getLevelDisplayName() {
            return this.level.getDisplayName();
        }

        @Override
        public boolean isAvailable() {
            return !this.level.isUnavailable();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
    implements AutoCloseable {
        public abstract boolean isAvailable();

        @Override
        public void close() {
        }
    }
}

