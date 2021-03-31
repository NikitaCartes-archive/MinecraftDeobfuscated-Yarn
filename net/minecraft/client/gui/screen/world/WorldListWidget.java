/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
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
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
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
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    private static final Text FROM_NEWER_VERSION_FIRST_LINE = new TranslatableText("selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED);
    private static final Text FROM_NEWER_VERSION_SECOND_LINE = new TranslatableText("selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED);
    private static final Text SNAPSHOT_FIRST_LINE = new TranslatableText("selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD);
    private static final Text SNAPSHOT_SECOND_LINE = new TranslatableText("selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD);
    private static final Text LOCKED_TEXT = new TranslatableText("selectWorld.locked").formatted(Formatting.RED);
    private static final Text PRE_WORLDHEIGHT_TEXT = new TranslatableText("selectWorld.pre_worldheight").formatted(Formatting.RED);
    private final SelectWorldScreen parent;
    @Nullable
    private List<LevelSummary> levels;

    public WorldListWidget(SelectWorldScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, Supplier<String> searchFilter, @Nullable WorldListWidget list) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        if (list != null) {
            this.levels = list.levels;
        }
        this.filter(searchFilter, false);
    }

    public void filter(Supplier<String> supplier, boolean load) {
        this.clearEntries();
        LevelStorage levelStorage = this.client.getLevelStorage();
        if (this.levels == null || load) {
            try {
                this.levels = levelStorage.getLevelList();
            } catch (LevelStorageException levelStorageException) {
                LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
                this.client.openScreen(new FatalErrorScreen(new TranslatableText("selectWorld.unable_to_load"), new LiteralText(levelStorageException.getMessage())));
                return;
            }
            Collections.sort(this.levels);
        }
        if (this.levels.isEmpty()) {
            this.client.openScreen(CreateWorldScreen.create(null));
            return;
        }
        String string = supplier.get().toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : this.levels) {
            if (!levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) && !levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) continue;
            this.addEntry(new Entry(this, levelSummary));
        }
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
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (entry != null) {
            LevelSummary levelSummary = entry.level;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", new TranslatableText("narrator.select.world", levelSummary.getDisplayName(), new Date(levelSummary.getLastPlayed()), levelSummary.isHardcore() ? new TranslatableText("gameMode.hardcore") : new TranslatableText("gameMode." + levelSummary.getGameMode().getName()), levelSummary.hasCheats() ? new TranslatableText("selectWorld.cheats") : LiteralText.EMPTY, levelSummary.getVersion())).getString());
        }
        this.parent.worldSelected(entry != null && !entry.level.isUnavailable());
    }

    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> !((Entry)entry).level.isUnavailable());
    }

    public Optional<Entry> getSelectedAsOptional() {
        return Optional.ofNullable(this.getSelected());
    }

    public SelectWorldScreen getParent() {
        return this.parent;
    }

    @Environment(value=EnvType.CLIENT)
    public final class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
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
        private File iconFile;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;

        public Entry(WorldListWidget levelList, LevelSummary level) {
            this.screen = levelList.getParent();
            this.level = level;
            this.client = MinecraftClient.getInstance();
            String string = level.getName();
            this.iconLocation = new Identifier("minecraft", "worlds/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon");
            this.iconFile = level.getFile();
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String string = this.level.getDisplayName();
            String string2 = this.level.getName() + " (" + DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty(string)) {
                string = I18n.translate("selectWorld.world", new Object[0]) + " " + (index + 1);
            }
            Text text = this.level.getDetails();
            this.client.textRenderer.draw(matrices, string, (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
            this.client.textRenderer.draw(matrices, string2, (float)(x + 32 + 3), (float)(y + this.client.textRenderer.fontHeight + 3), 0x808080);
            this.client.textRenderer.draw(matrices, text, (float)(x + 32 + 3), (float)(y + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3), 0x808080);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.client.options.touchscreen || hovered) {
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
                } else if (this.level.isPreWorldHeightChangeVersion()) {
                    DrawableHelper.drawTexture(matrices, x, y, 96.0f, 32.0f, 32, 32, 256, 256);
                    if (bl) {
                        this.screen.setTooltip(this.client.textRenderer.wrapLines(PRE_WORLDHEIGHT_TEXT, 175));
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
                TranslatableText mutableText = new TranslatableText(string);
                if (conversionWarning.needsBoldRedFormatting()) {
                    mutableText.formatted(Formatting.BOLD, Formatting.RED);
                }
                TranslatableText text = new TranslatableText(string2, this.level.getVersion(), SharedConstants.getGameVersion().getName());
                this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
                    if (bl) {
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
                this.client.openScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        try {
                            this.start();
                        } catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                            this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.futureworld.error.title"), new TranslatableText("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.client.openScreen(this.screen);
                    }
                }, new TranslatableText("selectWorld.versionQuestion"), new TranslatableText("selectWorld.versionWarning", this.level.getVersion()), new TranslatableText("selectWorld.versionJoinButton"), ScreenTexts.CANCEL));
            } else {
                this.start();
            }
        }

        public void delete() {
            this.client.openScreen(new ConfirmScreen(bl -> {
                if (bl) {
                    this.client.openScreen(new ProgressScreen());
                    this.method_33685();
                }
                this.client.openScreen(this.screen);
            }, new TranslatableText("selectWorld.deleteQuestion"), new TranslatableText("selectWorld.deleteWarning", this.level.getDisplayName()), new TranslatableText("selectWorld.deleteButton"), ScreenTexts.CANCEL));
        }

        public void method_33685() {
            LevelStorage levelStorage = this.client.getLevelStorage();
            String string = this.level.getName();
            try (LevelStorage.Session session = levelStorage.createSession(string);){
                session.deleteSessionLock();
            } catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast(this.client, string);
                LOGGER.error("Failed to delete world {}", (Object)string, (Object)iOException);
            }
            WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
        }

        public void edit() {
            String string = this.level.getName();
            try {
                LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
                this.client.openScreen(new EditWorldScreen(bl -> {
                    try {
                        session.close();
                    } catch (IOException iOException) {
                        LOGGER.error("Failed to unlock level {}", (Object)string, (Object)iOException);
                    }
                    if (bl) {
                        WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                    }
                    this.client.openScreen(this.screen);
                }, session));
            } catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, string);
                LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
                WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
            }
        }

        public void recreate() {
            this.method_29990();
            DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName());
                 MinecraftClient.IntegratedResourceManager integratedResourceManager = this.client.createIntegratedResourceManager(impl, MinecraftClient::loadDataPackSettings, MinecraftClient::createSaveProperties, false, session);){
                LevelInfo levelInfo = integratedResourceManager.getSaveProperties().getLevelInfo();
                DataPackSettings dataPackSettings = levelInfo.getDataPackSettings();
                GeneratorOptions generatorOptions = integratedResourceManager.getSaveProperties().getGeneratorOptions();
                Path path = CreateWorldScreen.method_29685(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
                if (generatorOptions.isLegacyCustomizedType()) {
                    this.client.openScreen(new ConfirmScreen(bl -> this.client.openScreen(bl ? new CreateWorldScreen(this.screen, levelInfo, generatorOptions, path, dataPackSettings, impl) : this.screen), new TranslatableText("selectWorld.recreate.customized.title"), new TranslatableText("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
                } else {
                    this.client.openScreen(new CreateWorldScreen(this.screen, levelInfo, generatorOptions, path, dataPackSettings, impl));
                }
            } catch (Exception exception) {
                LOGGER.error("Unable to recreate world", (Throwable)exception);
                this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.recreate.error.title"), new TranslatableText("selectWorld.recreate.error.text")));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.method_29990();
                this.client.startIntegratedServer(this.level.getName());
            }
        }

        private void method_29990() {
            this.client.method_29970(new SaveLevelScreen(new TranslatableText("selectWorld.data_read")));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            boolean bl2 = bl = this.iconFile != null && this.iconFile.isFile();
            if (!bl) {
                this.client.getTextureManager().destroyTexture(this.iconLocation);
                return null;
            }
            try (FileInputStream inputStream = new FileInputStream(this.iconFile);){
                NativeImage nativeImage = NativeImage.read(inputStream);
                Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                NativeImageBackedTexture nativeImageBackedTexture2 = new NativeImageBackedTexture(nativeImage);
                this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture2);
                NativeImageBackedTexture nativeImageBackedTexture = nativeImageBackedTexture2;
                return nativeImageBackedTexture;
            } catch (Throwable throwable6) {
                LOGGER.error("Invalid icon for world {}", (Object)this.level.getName(), (Object)throwable6);
                this.iconFile = null;
                return null;
            }
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }

        public String method_35740() {
            return this.level.getDisplayName();
        }
    }
}

