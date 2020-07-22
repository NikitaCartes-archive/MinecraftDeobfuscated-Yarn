/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private static final Logger field_25480 = LogManager.getLogger();
    private static final Text field_25898 = new TranslatableText("selectWorld.gameMode");
    private static final Text field_26598 = new TranslatableText("selectWorld.enterSeed");
    private static final Text field_26599 = new TranslatableText("selectWorld.seedInfo");
    private static final Text field_26600 = new TranslatableText("selectWorld.enterName");
    private static final Text field_26601 = new TranslatableText("selectWorld.resultFolder");
    private static final Text field_26602 = new TranslatableText("selectWorld.allowCommands.info");
    private final Screen parent;
    private TextFieldWidget levelNameField;
    private String saveDirectoryName;
    private Mode currentMode = Mode.SURVIVAL;
    @Nullable
    private Mode lastMode;
    private Difficulty field_24289 = Difficulty.NORMAL;
    private Difficulty field_24290 = Difficulty.NORMAL;
    private boolean cheatsEnabled;
    private boolean tweakedCheats;
    public boolean hardcore;
    protected DataPackSettings field_25479;
    @Nullable
    private Path field_25477;
    @Nullable
    private ResourcePackManager field_25792;
    private boolean moreOptionsOpen;
    private ButtonWidget createLevelButton;
    private ButtonWidget gameModeSwitchButton;
    private ButtonWidget field_24286;
    private ButtonWidget moreOptionsButton;
    private ButtonWidget gameRulesButton;
    private ButtonWidget field_25478;
    private ButtonWidget enableCheatsButton;
    private Text firstGameModeDescriptionLine;
    private Text secondGameModeDescriptionLine;
    private String levelName;
    private GameRules gameRules = new GameRules();
    public final MoreOptionsDialog moreOptionsDialog;

    public CreateWorldScreen(@Nullable Screen screen, LevelInfo levelInfo, GeneratorOptions generatorOptions, @Nullable Path path, DataPackSettings dataPackSettings, DynamicRegistryManager.Impl impl) {
        this(screen, dataPackSettings, new MoreOptionsDialog(impl, generatorOptions, GeneratorType.method_29078(generatorOptions), OptionalLong.of(generatorOptions.getSeed())));
        this.levelName = levelInfo.getLevelName();
        this.cheatsEnabled = levelInfo.areCommandsAllowed();
        this.tweakedCheats = true;
        this.field_24290 = this.field_24289 = levelInfo.getDifficulty();
        this.gameRules.setAllValues(levelInfo.getGameRules(), null);
        if (levelInfo.isHardcore()) {
            this.currentMode = Mode.HARDCORE;
        } else if (levelInfo.getGameMode().isSurvivalLike()) {
            this.currentMode = Mode.SURVIVAL;
        } else if (levelInfo.getGameMode().isCreative()) {
            this.currentMode = Mode.CREATIVE;
        }
        this.field_25477 = path;
    }

    public CreateWorldScreen(@Nullable Screen parent) {
        this(parent, DataPackSettings.SAFE_MODE, new MoreOptionsDialog(DynamicRegistryManager.create(), GeneratorOptions.getDefaultOptions(), Optional.of(GeneratorType.DEFAULT), OptionalLong.empty()));
    }

    private CreateWorldScreen(@Nullable Screen screen, DataPackSettings dataPackSettings, MoreOptionsDialog moreOptionsDialog) {
        super(new TranslatableText("selectWorld.create"));
        this.parent = screen;
        this.levelName = I18n.translate("selectWorld.newWorld", new Object[0]);
        this.field_25479 = dataPackSettings;
        this.moreOptionsDialog = moreOptionsDialog;
    }

    @Override
    public void tick() {
        this.levelNameField.tick();
        this.moreOptionsDialog.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.levelNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, (Text)new TranslatableText("selectWorld.enterName")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.saveDirectoryName);
            }
        };
        this.levelNameField.setText(this.levelName);
        this.levelNameField.setChangedListener(string -> {
            this.levelName = string;
            this.createLevelButton.active = !this.levelNameField.getText().isEmpty();
            this.updateSaveFolderName();
        });
        this.children.add(this.levelNameField);
        int i = this.width / 2 - 155;
        int j = this.width / 2 + 5;
        this.gameModeSwitchButton = this.addButton(new ButtonWidget(i, 100, 150, 20, LiteralText.EMPTY, buttonWidget -> {
            switch (this.currentMode) {
                case SURVIVAL: {
                    this.tweakDefaultsTo(Mode.HARDCORE);
                    break;
                }
                case HARDCORE: {
                    this.tweakDefaultsTo(Mode.CREATIVE);
                    break;
                }
                case CREATIVE: {
                    this.tweakDefaultsTo(Mode.SURVIVAL);
                }
            }
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return new TranslatableText("options.generic_value", field_25898, new TranslatableText("selectWorld.gameMode." + CreateWorldScreen.this.currentMode.translationSuffix));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(CreateWorldScreen.this.firstGameModeDescriptionLine).append(" ").append(CreateWorldScreen.this.secondGameModeDescriptionLine);
            }
        });
        this.field_24286 = this.addButton(new ButtonWidget(j, 100, 150, 20, new TranslatableText("options.difficulty"), buttonWidget -> {
            this.field_24290 = this.field_24289 = this.field_24289.cycle();
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
            }
        });
        this.enableCheatsButton = this.addButton(new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), buttonWidget -> {
            this.tweakedCheats = true;
            this.cheatsEnabled = !this.cheatsEnabled;
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return ScreenTexts.method_30619(super.getMessage(), CreateWorldScreen.this.cheatsEnabled && !CreateWorldScreen.this.hardcore);
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.allowCommands.info"));
            }
        });
        this.field_25478 = this.addButton(new ButtonWidget(j, 151, 150, 20, new TranslatableText("selectWorld.dataPacks"), buttonWidget -> this.method_29694()));
        this.gameRulesButton = this.addButton(new ButtonWidget(i, 185, 150, 20, new TranslatableText("selectWorld.gameRules"), buttonWidget -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
            this.client.openScreen(this);
            optional.ifPresent(gameRules -> {
                this.gameRules = gameRules;
            });
        }))));
        this.moreOptionsDialog.method_28092(this, this.client, this.textRenderer);
        this.moreOptionsButton = this.addButton(new ButtonWidget(j, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions()));
        this.createLevelButton = this.addButton(new ButtonWidget(i, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), buttonWidget -> this.createLevel()));
        this.createLevelButton.active = !this.levelName.isEmpty();
        this.addButton(new ButtonWidget(j, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.method_30297()));
        this.setMoreOptionsOpen();
        this.setInitialFocus(this.levelNameField);
        this.tweakDefaultsTo(this.currentMode);
        this.updateSaveFolderName();
    }

    private void updateSettingsLabels() {
        this.firstGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line1");
        this.secondGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line2");
    }

    private void updateSaveFolderName() {
        this.saveDirectoryName = this.levelNameField.getText().trim();
        if (this.saveDirectoryName.isEmpty()) {
            this.saveDirectoryName = "World";
        }
        try {
            this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
        } catch (Exception exception) {
            this.saveDirectoryName = "World";
            try {
                this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
            } catch (Exception exception2) {
                throw new RuntimeException("Could not create save folder", exception2);
            }
        }
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void createLevel() {
        LevelInfo levelInfo;
        this.client.method_29970(new SaveLevelScreen(new TranslatableText("createWorld.preparing")));
        if (!this.method_29696()) {
            return;
        }
        this.method_30298();
        GeneratorOptions generatorOptions = this.moreOptionsDialog.getGeneratorOptions(this.hardcore);
        if (generatorOptions.isDebugWorld()) {
            GameRules gameRules = new GameRules();
            gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            levelInfo = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataPackSettings.SAFE_MODE);
        } else {
            levelInfo = new LevelInfo(this.levelNameField.getText().trim(), this.currentMode.defaultGameMode, this.hardcore, this.field_24290, this.cheatsEnabled && !this.hardcore, this.gameRules, this.field_25479);
        }
        this.client.method_29607(this.saveDirectoryName, levelInfo, this.moreOptionsDialog.method_29700(), generatorOptions);
    }

    private void toggleMoreOptions() {
        this.setMoreOptionsOpen(!this.moreOptionsOpen);
    }

    private void tweakDefaultsTo(Mode mode) {
        if (!this.tweakedCheats) {
            boolean bl = this.cheatsEnabled = mode == Mode.CREATIVE;
        }
        if (mode == Mode.HARDCORE) {
            this.hardcore = true;
            this.enableCheatsButton.active = false;
            this.moreOptionsDialog.bonusItemsButton.active = false;
            this.field_24290 = Difficulty.HARD;
            this.field_24286.active = false;
        } else {
            this.hardcore = false;
            this.enableCheatsButton.active = true;
            this.moreOptionsDialog.bonusItemsButton.active = true;
            this.field_24290 = this.field_24289;
            this.field_24286.active = true;
        }
        this.currentMode = mode;
        this.updateSettingsLabels();
    }

    public void setMoreOptionsOpen() {
        this.setMoreOptionsOpen(this.moreOptionsOpen);
    }

    private void setMoreOptionsOpen(boolean moreOptionsOpen) {
        this.moreOptionsOpen = moreOptionsOpen;
        this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
        boolean bl = this.field_24286.visible = !this.moreOptionsOpen;
        if (this.moreOptionsDialog.isDebugWorld()) {
            this.field_25478.visible = false;
            this.gameModeSwitchButton.active = false;
            if (this.lastMode == null) {
                this.lastMode = this.currentMode;
            }
            this.tweakDefaultsTo(Mode.DEBUG);
            this.enableCheatsButton.visible = false;
        } else {
            this.gameModeSwitchButton.active = true;
            if (this.lastMode != null) {
                this.tweakDefaultsTo(this.lastMode);
            }
            this.enableCheatsButton.visible = !this.moreOptionsOpen;
            this.field_25478.visible = !this.moreOptionsOpen;
        }
        this.moreOptionsDialog.setVisible(this.moreOptionsOpen);
        this.levelNameField.setVisible(!this.moreOptionsOpen);
        if (this.moreOptionsOpen) {
            this.moreOptionsButton.setMessage(ScreenTexts.DONE);
        } else {
            this.moreOptionsButton.setMessage(new TranslatableText("selectWorld.moreWorldOptions"));
        }
        this.gameRulesButton.visible = !this.moreOptionsOpen;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.createLevel();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        if (this.moreOptionsOpen) {
            this.setMoreOptionsOpen(false);
        } else {
            this.method_30297();
        }
    }

    public void method_30297() {
        this.client.openScreen(this.parent);
        this.method_30298();
    }

    private void method_30298() {
        if (this.field_25792 != null) {
            this.field_25792.close();
        }
        this.method_29695();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        CreateWorldScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);
        if (this.moreOptionsOpen) {
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, field_26598, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, field_26599, this.width / 2 - 100, 85, -6250336);
            this.moreOptionsDialog.render(matrices, mouseX, mouseY, delta);
        } else {
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, field_26600, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, new LiteralText("").append(field_26601).append(" ").append(this.saveDirectoryName), this.width / 2 - 100, 85, -6250336);
            this.levelNameField.render(matrices, mouseX, mouseY, delta);
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 150, 122, -6250336);
            CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 150, 134, -6250336);
            if (this.enableCheatsButton.visible) {
                CreateWorldScreen.drawTextWithShadow(matrices, this.textRenderer, field_26602, this.width / 2 - 150, 172, -6250336);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected <T extends Element> T addChild(T child) {
        return super.addChild(child);
    }

    @Override
    protected <T extends AbstractButtonWidget> T addButton(T button) {
        return super.addButton(button);
    }

    @Nullable
    protected Path method_29693() {
        if (this.field_25477 == null) {
            try {
                this.field_25477 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
            } catch (IOException iOException) {
                field_25480.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.addPackCopyFailure(this.client, this.saveDirectoryName);
                this.method_30297();
            }
        }
        return this.field_25477;
    }

    private void method_29694() {
        Pair<File, ResourcePackManager> pair = this.method_30296();
        if (pair != null) {
            this.client.openScreen(new AbstractPackScreen(this, pair.getSecond(), this::method_29682, pair.getFirst(), new TranslatableText("dataPack.title")));
        }
    }

    private void method_29682(ResourcePackManager resourcePackManager) {
        ImmutableList<String> list = ImmutableList.copyOf(resourcePackManager.getEnabledNames());
        List list2 = resourcePackManager.getNames().stream().filter(string -> !list.contains(string)).collect(ImmutableList.toImmutableList());
        DataPackSettings dataPackSettings = new DataPackSettings(list, list2);
        if (list.equals(this.field_25479.getEnabled())) {
            this.field_25479 = dataPackSettings;
            return;
        }
        this.client.send(() -> this.client.openScreen(new SaveLevelScreen(new TranslatableText("dataPack.validation.working"))));
        ServerResourceManager.reload(resourcePackManager.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getMainWorkerExecutor(), this.client).handle((serverResourceManager, throwable) -> {
            if (throwable != null) {
                field_25480.warn("Failed to validate datapack", (Throwable)throwable);
                this.client.send(() -> this.client.openScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        this.method_29694();
                    } else {
                        this.field_25479 = DataPackSettings.SAFE_MODE;
                        this.client.openScreen(this);
                    }
                }, new TranslatableText("dataPack.validation.failed"), LiteralText.EMPTY, new TranslatableText("dataPack.validation.back"), new TranslatableText("dataPack.validation.reset"))));
            } else {
                this.client.send(() -> {
                    this.field_25479 = dataPackSettings;
                    this.moreOptionsDialog.method_30509(DynamicRegistryManager.load(serverResourceManager.getResourceManager()));
                    serverResourceManager.close();
                    this.client.openScreen(this);
                });
            }
            return null;
        });
    }

    private void method_29695() {
        if (this.field_25477 != null) {
            try (Stream<Path> stream = Files.walk(this.field_25477, new FileVisitOption[0]);){
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException iOException) {
                        field_25480.warn("Failed to remove temporary file {}", path, (Object)iOException);
                    }
                });
            } catch (IOException iOException) {
                field_25480.warn("Failed to list temporary dir {}", (Object)this.field_25477);
            }
            this.field_25477 = null;
        }
    }

    private static void method_29687(Path path, Path path2, Path path3) {
        try {
            Util.relativeCopy(path, path2, path3);
        } catch (IOException iOException) {
            field_25480.warn("Failed to copy datapack file from {} to {}", (Object)path3, (Object)path2);
            throw new WorldCreationException(iOException);
        }
    }

    private boolean method_29696() {
        if (this.field_25477 != null) {
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.saveDirectoryName);
                 Stream<Path> stream = Files.walk(this.field_25477, new FileVisitOption[0]);){
                Path path3 = session.getDirectory(WorldSavePath.DATAPACKS);
                Files.createDirectories(path3, new FileAttribute[0]);
                stream.filter(path -> !path.equals(this.field_25477)).forEach(path2 -> CreateWorldScreen.method_29687(this.field_25477, path3, path2));
            } catch (IOException | WorldCreationException exception) {
                field_25480.warn("Failed to copy datapacks to world {}", (Object)this.saveDirectoryName, (Object)exception);
                SystemToast.addPackCopyFailure(this.client, this.saveDirectoryName);
                this.method_30297();
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static Path method_29685(Path path, MinecraftClient minecraftClient) {
        MutableObject mutableObject = new MutableObject();
        try (Stream<Path> stream = Files.walk(path, new FileVisitOption[0]);){
            stream.filter(path2 -> !path2.equals(path)).forEach(path2 -> {
                Path path3 = (Path)mutableObject.getValue();
                if (path3 == null) {
                    try {
                        path3 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
                    } catch (IOException iOException) {
                        field_25480.warn("Failed to create temporary dir");
                        throw new WorldCreationException(iOException);
                    }
                    mutableObject.setValue(path3);
                }
                CreateWorldScreen.method_29687(path, path3, path2);
            });
        } catch (IOException | WorldCreationException exception) {
            field_25480.warn("Failed to copy datapacks from world {}", (Object)path, (Object)exception);
            SystemToast.addPackCopyFailure(minecraftClient, path.toString());
            return null;
        }
        return (Path)mutableObject.getValue();
    }

    @Nullable
    private Pair<File, ResourcePackManager> method_30296() {
        Path path = this.method_29693();
        if (path != null) {
            File file = path.toFile();
            if (this.field_25792 == null) {
                this.field_25792 = new ResourcePackManager(new VanillaDataPackProvider(), new FileResourcePackProvider(file, ResourcePackSource.field_25347));
                this.field_25792.scanPacks();
            }
            this.field_25792.setEnabledProfiles(this.field_25479.getEnabled());
            return Pair.of(file, this.field_25792);
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    static class WorldCreationException
    extends RuntimeException {
        public WorldCreationException(Throwable throwable) {
            super(throwable);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Mode {
        SURVIVAL("survival", GameMode.SURVIVAL),
        HARDCORE("hardcore", GameMode.SURVIVAL),
        CREATIVE("creative", GameMode.CREATIVE),
        DEBUG("spectator", GameMode.SPECTATOR);

        private final String translationSuffix;
        private final GameMode defaultGameMode;

        private Mode(String translationSuffix, GameMode defaultGameMode) {
            this.translationSuffix = translationSuffix;
            this.defaultGameMode = defaultGameMode;
        }
    }
}

