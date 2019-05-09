/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.menu;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
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
import net.minecraft.ChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.menu.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.menu.BackupPromptScreen;
import net.minecraft.client.gui.menu.EditLevelScreen;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.NewLevelScreen;
import net.minecraft.client.gui.menu.NoticeScreen;
import net.minecraft.client.gui.menu.SevereErrorScreen;
import net.minecraft.client.gui.menu.WorkingScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvents;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LevelListWidget
extends AlwaysSelectedEntryListWidget<LevelItem> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    private final LevelSelectScreen parent;
    @Nullable
    private List<LevelSummary> levels;

    public LevelListWidget(LevelSelectScreen levelSelectScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable LevelListWidget levelListWidget) {
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
            } catch (LevelStorageException levelStorageException) {
                LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
                this.minecraft.openScreen(new SevereErrorScreen(new TranslatableComponent("selectWorld.unable_to_load", new Object[0]), levelStorageException.getMessage()));
                return;
            }
            Collections.sort(this.levels);
        }
        String string = supplier.get().toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : this.levels) {
            if (!levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) && !levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) continue;
            this.addEntry(new LevelItem(this, levelSummary, this.minecraft.getLevelStorage()));
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

    public void method_20157(@Nullable LevelItem levelItem) {
        super.setSelected(levelItem);
        if (levelItem != null) {
            LevelSummary levelSummary = levelItem.level;
            NarratorManager.INSTANCE.narrate(new TranslatableComponent("narrator.select", new TranslatableComponent("narrator.select.world", levelSummary.getDisplayName(), new Date(levelSummary.getLastPlayed()), levelSummary.isHardcore() ? I18n.translate("gameMode.hardcore", new Object[0]) : I18n.translate("gameMode." + levelSummary.getGameMode().getName(), new Object[0]), levelSummary.hasCheats() ? I18n.translate("selectWorld.cheats", new Object[0]) : "", levelSummary.getVersionTextComponent())).getString());
        }
    }

    @Override
    protected void moveSelection(int i) {
        super.moveSelection(i);
        this.parent.worldSelected(true);
    }

    public Optional<LevelItem> method_20159() {
        return Optional.ofNullable(this.getSelected());
    }

    public LevelSelectScreen getParent() {
        return this.parent;
    }

    @Override
    public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
        this.method_20157((LevelItem)entry);
    }

    @Environment(value=EnvType.CLIENT)
    public final class LevelItem
    extends AlwaysSelectedEntryListWidget.Entry<LevelItem>
    implements AutoCloseable {
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
            String string2 = this.level.getName() + " (" + DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty(string)) {
                string = I18n.translate("selectWorld.world", new Object[0]) + " " + (i + 1);
            }
            String string3 = "";
            if (this.level.requiresConversion()) {
                string3 = I18n.translate("selectWorld.conversion", new Object[0]) + " " + string3;
            } else {
                string3 = I18n.translate("gameMode." + this.level.getGameMode().getName(), new Object[0]);
                if (this.level.isHardcore()) {
                    string3 = (Object)((Object)ChatFormat.DARK_RED) + I18n.translate("gameMode.hardcore", new Object[0]) + (Object)((Object)ChatFormat.RESET);
                }
                if (this.level.hasCheats()) {
                    string3 = string3 + ", " + I18n.translate("selectWorld.cheats", new Object[0]);
                }
                String string4 = this.level.getVersionTextComponent().getFormattedText();
                string3 = this.level.isDifferentVersion() ? (this.level.isFutureLevel() ? string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + (Object)((Object)ChatFormat.RED) + string4 + (Object)((Object)ChatFormat.RESET) : string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + (Object)((Object)ChatFormat.ITALIC) + string4 + (Object)((Object)ChatFormat.RESET)) : string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + string4;
            }
            this.client.textRenderer.draw(string, k + 32 + 3, j + 1, 0xFFFFFF);
            this.client.textRenderer.draw(string2, k + 32 + 3, j + this.client.textRenderer.fontHeight + 3, 0x808080);
            this.client.textRenderer.draw(string3, k + 32 + 3, j + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3, 0x808080);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            GlStateManager.enableBlend();
            DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
            if (this.client.options.touchscreen || bl) {
                int q;
                this.client.getTextureManager().bindTexture(WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = n - k;
                int n2 = q = p < 32 ? 32 : 0;
                if (this.level.isDifferentVersion()) {
                    DrawableHelper.blit(k, j, 32.0f, q, 32, 32, 256, 256);
                    if (this.level.isLegacyCustomizedWorld()) {
                        DrawableHelper.blit(k, j, 96.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            Component component = new TranslatableComponent("selectWorld.tooltip.unsupported", this.level.getVersionTextComponent()).applyFormat(ChatFormat.RED);
                            this.screen.setTooltip(this.client.textRenderer.wrapStringToWidth(component.getFormattedText(), 175));
                        }
                    } else if (this.level.isFutureLevel()) {
                        DrawableHelper.blit(k, j, 96.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            this.screen.setTooltip((Object)((Object)ChatFormat.RED) + I18n.translate("selectWorld.tooltip.fromNewerVersion1", new Object[0]) + "\n" + (Object)((Object)ChatFormat.RED) + I18n.translate("selectWorld.tooltip.fromNewerVersion2", new Object[0]));
                        }
                    } else if (!SharedConstants.getGameVersion().isStable()) {
                        DrawableHelper.blit(k, j, 64.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            this.screen.setTooltip((Object)((Object)ChatFormat.GOLD) + I18n.translate("selectWorld.tooltip.snapshot1", new Object[0]) + "\n" + (Object)((Object)ChatFormat.GOLD) + I18n.translate("selectWorld.tooltip.snapshot2", new Object[0]));
                        }
                    }
                } else {
                    DrawableHelper.blit(k, j, 0.0f, q, 32, 32, 256, 256);
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
            }
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.play();
                return true;
            }
            this.time = SystemUtil.getMeasuringTimeMs();
            return false;
        }

        public void play() {
            if (this.level.isOutdatedLevel() || this.level.isLegacyCustomizedWorld()) {
                TranslatableComponent component = new TranslatableComponent("selectWorld.backupQuestion", new Object[0]);
                TranslatableComponent component2 = new TranslatableComponent("selectWorld.backupWarning", this.level.getVersionTextComponent().getFormattedText(), SharedConstants.getGameVersion().getName());
                if (this.level.isLegacyCustomizedWorld()) {
                    component = new TranslatableComponent("selectWorld.backupQuestion.customized", new Object[0]);
                    component2 = new TranslatableComponent("selectWorld.backupWarning.customized", new Object[0]);
                }
                this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
                    if (bl) {
                        String string = this.level.getName();
                        EditLevelScreen.backupLevel(this.client.getLevelStorage(), string);
                    }
                    this.start();
                }, component, component2, false));
            } else if (this.level.isFutureLevel()) {
                this.client.openScreen(new YesNoScreen(bl -> {
                    if (bl) {
                        try {
                            this.start();
                        } catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                            this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableComponent("selectWorld.futureworld.error.title", new Object[0]), new TranslatableComponent("selectWorld.futureworld.error.text", new Object[0])));
                        }
                    } else {
                        this.client.openScreen(this.screen);
                    }
                }, new TranslatableComponent("selectWorld.versionQuestion", new Object[0]), new TranslatableComponent("selectWorld.versionWarning", this.level.getVersionTextComponent().getFormattedText()), I18n.translate("selectWorld.versionJoinButton", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
            } else {
                this.start();
            }
        }

        public void delete() {
            this.client.openScreen(new YesNoScreen(bl -> {
                if (bl) {
                    this.client.openScreen(new WorkingScreen());
                    LevelStorage levelStorage = this.client.getLevelStorage();
                    levelStorage.deleteLevel(this.level.getName());
                    LevelListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, new TranslatableComponent("selectWorld.deleteQuestion", new Object[0]), new TranslatableComponent("selectWorld.deleteWarning", this.level.getDisplayName()), I18n.translate("selectWorld.deleteButton", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
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
                        this.client.openScreen(new YesNoScreen(bl -> this.client.openScreen(bl ? newLevelScreen : this.screen), new TranslatableComponent("selectWorld.recreate.customized.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.customized.text", new Object[0]), I18n.translate("gui.proceed", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
                    } else {
                        this.client.openScreen(newLevelScreen);
                    }
                }
            } catch (Exception exception) {
                LOGGER.error("Unable to recreate world", (Throwable)exception);
                this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableComponent("selectWorld.recreate.error.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.error.text", new Object[0])));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.client.startIntegratedServer(this.level.getName(), this.level.getDisplayName(), null);
            }
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
                NativeImage nativeImage = NativeImage.fromInputStream(inputStream);
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
    }
}

