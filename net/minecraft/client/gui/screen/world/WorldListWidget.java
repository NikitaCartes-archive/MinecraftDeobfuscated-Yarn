/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_7413;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.SaveLoader;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<class_7414> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    static final Text FROM_NEWER_VERSION_FIRST_LINE = Text.method_43471("selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED);
    static final Text FROM_NEWER_VERSION_SECOND_LINE = Text.method_43471("selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED);
    static final Text SNAPSHOT_FIRST_LINE = Text.method_43471("selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD);
    static final Text SNAPSHOT_SECOND_LINE = Text.method_43471("selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD);
    static final Text LOCKED_TEXT = Text.method_43471("selectWorld.locked").formatted(Formatting.RED);
    static final Text CONVERSION_TOOLTIP = Text.method_43471("selectWorld.conversion.tooltip").formatted(Formatting.RED);
    private static final Duration field_38995 = Duration.ofMillis(100L);
    private final SelectWorldScreen parent;
    @Nullable
    private CompletableFuture<List<LevelSummary>> field_38996;
    private final class_7415 field_38994;

    public WorldListWidget(SelectWorldScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, Supplier<String> searchFilter, @Nullable WorldListWidget worldListWidget) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.field_38994 = new class_7415(client);
        if (worldListWidget != null) {
            this.field_38996 = worldListWidget.field_38996;
            this.filter(searchFilter.get());
        } else {
            this.method_43458(searchFilter);
        }
    }

    public void method_43458(Supplier<String> supplier) {
        this.field_38996 = this.method_43462();
        List<LevelSummary> list2 = this.method_43457(this.field_38996, field_38995);
        if (list2 != null) {
            this.method_43454(supplier.get(), list2);
        } else {
            this.method_43463();
            this.field_38996.thenAcceptAsync(list -> this.method_43454((String)supplier.get(), (List<LevelSummary>)list), (Executor)this.client);
        }
    }

    public void filter(String string) {
        if (this.field_38996 == null) {
            this.clearEntries();
            return;
        }
        List<LevelSummary> list = this.method_43457(this.field_38996, Duration.ZERO);
        if (list != null) {
            this.method_43454(string, list);
        } else {
            this.method_43463();
        }
    }

    private CompletableFuture<List<LevelSummary>> method_43462() {
        LevelStorage.class_7410 lv;
        try {
            lv = this.client.getLevelStorage().getLevelList();
        } catch (LevelStorageException levelStorageException) {
            LOGGER.error("Couldn't load level list", levelStorageException);
            this.method_43460(levelStorageException.method_43416());
            return CompletableFuture.completedFuture(List.of());
        }
        if (lv.method_43421()) {
            CreateWorldScreen.create(this.client, null);
            return CompletableFuture.completedFuture(List.of());
        }
        return ((CompletableFuture)this.client.getLevelStorage().method_43417(lv).thenApply(list -> {
            Collections.sort(list);
            return list;
        })).exceptionally(throwable -> {
            this.client.setCrashReportSupplier(() -> CrashReport.create(throwable, "Couldn't load level list"));
            return List.of();
        });
    }

    @Nullable
    private List<LevelSummary> method_43457(CompletableFuture<List<LevelSummary>> completableFuture, Duration duration) {
        List<LevelSummary> list = null;
        try {
            list = completableFuture.get(duration.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            // empty catch block
        }
        return list;
    }

    private void method_43454(String string, List<LevelSummary> list) {
        this.clearEntries();
        string = string.toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : list) {
            if (!this.method_43453(string, levelSummary)) continue;
            this.addEntry(new Entry(this, levelSummary));
        }
        this.method_43464();
    }

    private boolean method_43453(String string, LevelSummary levelSummary) {
        return levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getName().toLowerCase(Locale.ROOT).contains(string);
    }

    private void method_43463() {
        this.clearEntries();
        this.addEntry(this.field_38994);
        this.method_43464();
    }

    private void method_43464() {
        this.parent.narrateScreenIfNarrationEnabled(true);
    }

    private void method_43460(Text text) {
        this.client.setScreen(new FatalErrorScreen(Text.method_43471("selectWorld.unable_to_load"), text));
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
    protected boolean isFocused() {
        return this.parent.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable class_7414 arg) {
        super.setSelected(arg);
        this.parent.worldSelected(arg != null && arg.method_43465());
    }

    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, class_7414::method_43465);
    }

    public Optional<Entry> getSelectedAsOptional() {
        class_7414 lv = (class_7414)this.getSelectedOrNull();
        if (lv instanceof Entry) {
            Entry entry = (Entry)lv;
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    public SelectWorldScreen getParent() {
        return this.parent;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.children().contains(this.field_38994)) {
            this.field_38994.appendNarrations(builder);
            return;
        }
        super.appendNarrations(builder);
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_7415
    extends class_7414 {
        private static final Text field_38997 = Text.method_43471("selectWorld.loading_list");
        private final MinecraftClient field_38998;

        public class_7415(MinecraftClient minecraftClient) {
            this.field_38998 = minecraftClient;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int i = (this.field_38998.currentScreen.width - this.field_38998.textRenderer.getWidth(field_38997)) / 2;
            int j = y + (entryHeight - this.field_38998.textRenderer.fontHeight) / 2;
            this.field_38998.textRenderer.draw(matrices, field_38997, (float)i, (float)j, 0xFFFFFF);
            String string = class_7413.method_43449(Util.getMeasuringTimeMs());
            int k = (this.field_38998.currentScreen.width - this.field_38998.textRenderer.getWidth(string)) / 2;
            int l = j + this.field_38998.textRenderer.fontHeight;
            this.field_38998.textRenderer.draw(matrices, string, (float)k, (float)l, 0x808080);
        }

        @Override
        public Text getNarration() {
            return field_38997;
        }

        @Override
        public boolean method_43465() {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public final class Entry
    extends class_7414
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
        private Path iconFile;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;

        public Entry(WorldListWidget levelList, LevelSummary level) {
            this.client = levelList.client;
            this.screen = levelList.getParent();
            this.level = level;
            String string = level.getName();
            this.iconLocation = new Identifier("minecraft", "worlds/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon");
            this.iconFile = level.getFile();
            if (!Files.isRegularFile(this.iconFile, new LinkOption[0])) {
                this.iconFile = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public Text getNarration() {
            MutableText text = Text.method_43469("narrator.select.world", this.level.getDisplayName(), new Date(this.level.getLastPlayed()), this.level.isHardcore() ? Text.method_43471("gameMode.hardcore") : Text.method_43471("gameMode." + this.level.getGameMode().getName()), this.level.hasCheats() ? Text.method_43471("selectWorld.cheats") : ScreenTexts.field_39003, this.level.getVersion());
            MutableText text2 = this.level.isLocked() ? ScreenTexts.joinSentences(text, LOCKED_TEXT) : text;
            return Text.method_43469("narrator.select", text2);
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
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.client.options.getTouchscreen().getValue().booleanValue() || hovered) {
                int j;
                RenderSystem.setShaderTexture(0, WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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
            return false;
        }

        public void play() {
            if (this.level.isUnavailable()) {
                return;
            }
            LevelSummary.ConversionWarning conversionWarning = this.level.getConversionWarning();
            if (conversionWarning.promptsBackup()) {
                String string = "selectWorld.backupQuestion." + conversionWarning.getTranslationKeySuffix();
                String string2 = "selectWorld.backupWarning." + conversionWarning.getTranslationKeySuffix();
                MutableText mutableText = Text.method_43471(string);
                if (conversionWarning.needsBoldRedFormatting()) {
                    mutableText.formatted(Formatting.BOLD, Formatting.RED);
                }
                MutableText text = Text.method_43469(string2, this.level.getVersion(), SharedConstants.getGameVersion().getName());
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
                            this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.method_43471("selectWorld.futureworld.error.title"), Text.method_43471("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.client.setScreen(this.screen);
                    }
                }, Text.method_43471("selectWorld.versionQuestion"), Text.method_43469("selectWorld.versionWarning", this.level.getVersion()), Text.method_43471("selectWorld.versionJoinButton"), ScreenTexts.CANCEL));
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
            }, Text.method_43471("selectWorld.deleteQuestion"), Text.method_43469("selectWorld.deleteWarning", this.level.getDisplayName()), Text.method_43471("selectWorld.deleteButton"), ScreenTexts.CANCEL));
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
            WorldListWidget.this.method_43458(this.screen.method_43450());
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
                        WorldListWidget.this.method_43458(this.screen.method_43450());
                    }
                    this.client.setScreen(this.screen);
                }, session));
            } catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, string);
                LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
                WorldListWidget.this.method_43458(this.screen.method_43450());
            }
        }

        public void recreate() {
            this.openReadingWorldScreen();
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName());
                 SaveLoader saveLoader = this.client.method_41735().createSaveLoader(session, false);){
                GeneratorOptions generatorOptions = saveLoader.saveProperties().getGeneratorOptions();
                Path path = CreateWorldScreen.copyDataPack(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
                if (generatorOptions.isLegacyCustomizedType()) {
                    this.client.setScreen(new ConfirmScreen(bl -> this.client.setScreen(bl ? CreateWorldScreen.create(this.screen, saveLoader, path) : this.screen), Text.method_43471("selectWorld.recreate.customized.title"), Text.method_43471("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
                } else {
                    this.client.setScreen(CreateWorldScreen.create(this.screen, saveLoader, path));
                }
            } catch (Exception exception) {
                LOGGER.error("Unable to recreate world", exception);
                this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.method_43471("selectWorld.recreate.error.title"), Text.method_43471("selectWorld.recreate.error.text")));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.openReadingWorldScreen();
                this.client.method_41735().start(this.screen, this.level.getName());
            }
        }

        private void openReadingWorldScreen() {
            this.client.setScreenAndRender(new MessageScreen(Text.method_43471("selectWorld.data_read")));
        }

        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            boolean bl2 = bl = this.iconFile != null && Files.isRegularFile(this.iconFile, new LinkOption[0]);
            if (bl) {
                NativeImageBackedTexture nativeImageBackedTexture;
                block9: {
                    InputStream inputStream = Files.newInputStream(this.iconFile, new OpenOption[0]);
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
                            this.iconFile = null;
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
        public boolean method_43465() {
            return !this.level.isUnavailable();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class class_7414
    extends AlwaysSelectedEntryListWidget.Entry<class_7414>
    implements AutoCloseable {
        public abstract boolean method_43465();

        @Override
        public void close() {
        }
    }
}

