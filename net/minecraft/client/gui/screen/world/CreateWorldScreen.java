/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5219;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private final Screen parent;
    private TextFieldWidget levelNameField;
    private TextFieldWidget seedField;
    private String saveDirectoryName;
    private Mode currentMode = Mode.SURVIVAL;
    @Nullable
    private Mode lastMode;
    private Difficulty field_24289 = Difficulty.NORMAL;
    private Difficulty field_24290 = Difficulty.NORMAL;
    private boolean structures = true;
    private boolean cheatsEnabled;
    private boolean tweakedCheats;
    private boolean bonusChest;
    private boolean hardcore;
    private boolean creatingLevel;
    private boolean moreOptionsOpen;
    private ButtonWidget createLevelButton;
    private ButtonWidget gameModeSwitchButton;
    private ButtonWidget field_24286;
    private ButtonWidget moreOptionsButton;
    private ButtonWidget gameRulesButton;
    private ButtonWidget generateStructuresButton;
    private ButtonWidget generateBonusChestButton;
    private ButtonWidget mapTypeSwitchButton;
    private ButtonWidget enableCheatsButton;
    private ButtonWidget customizeTypeButton;
    private Text firstGameModeDescriptionLine;
    private Text secondGameModeDescriptionLine;
    private String seed;
    private String levelName;
    private GameRules gameRules = new GameRules();
    private int generatorType;
    public LevelGeneratorOptions generatorOptions = LevelGeneratorType.DEFAULT.getDefaultOptions();

    public CreateWorldScreen(@Nullable Screen parent) {
        super(new TranslatableText("selectWorld.create"));
        this.parent = parent;
        this.seed = "";
        this.levelName = I18n.translate("selectWorld.newWorld", new Object[0]);
    }

    @Override
    public void tick() {
        this.levelNameField.tick();
        this.seedField.tick();
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
        this.gameModeSwitchButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 115, 150, 20, new TranslatableText("selectWorld.gameMode"), buttonWidget -> {
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
                return super.getMessage().shallowCopy().append(": ").append(new TranslatableText("selectWorld.gameMode." + CreateWorldScreen.this.currentMode.translationSuffix));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(CreateWorldScreen.this.firstGameModeDescriptionLine).append(" ").append(CreateWorldScreen.this.secondGameModeDescriptionLine);
            }
        });
        this.field_24286 = this.addButton(new ButtonWidget(this.width / 2 + 5, 115, 150, 20, new TranslatableText("options.difficulty"), buttonWidget -> {
            this.field_24290 = this.field_24289 = this.field_24289.method_27297();
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
            }
        });
        this.seedField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.seedField.setText(this.seed);
        this.seedField.setChangedListener(string -> {
            this.seed = this.seedField.getText();
        });
        this.children.add(this.seedField);
        this.generateStructuresButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
            this.structures = !this.structures;
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(CreateWorldScreen.this.structures));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
            }
        });
        this.generateStructuresButton.visible = false;
        this.mapTypeSwitchButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.mapType"), buttonWidget -> {
            ++this.generatorType;
            if (this.generatorType >= LevelGeneratorType.TYPES.length) {
                this.generatorType = 0;
            }
            while (!this.isGeneratorTypeValid()) {
                ++this.generatorType;
                if (this.generatorType < LevelGeneratorType.TYPES.length) continue;
                this.generatorType = 0;
            }
            this.generatorOptions = this.getLevelGeneratorType().getDefaultOptions();
            this.setMoreOptionsOpen(this.moreOptionsOpen);
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(CreateWorldScreen.this.getLevelGeneratorType().getTranslationKey());
            }

            @Override
            protected MutableText getNarrationMessage() {
                LevelGeneratorType levelGeneratorType = CreateWorldScreen.this.getLevelGeneratorType();
                if (levelGeneratorType.hasInfo()) {
                    return super.getNarrationMessage().append(". ").append(levelGeneratorType.getInfoTranslationKey());
                }
                return super.getNarrationMessage();
            }
        });
        this.mapTypeSwitchButton.visible = false;
        this.customizeTypeButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
            if (this.getLevelGeneratorType() == LevelGeneratorType.FLAT) {
                this.client.openScreen(new CustomizeFlatLevelScreen(this, this.generatorOptions));
            }
            if (this.getLevelGeneratorType() == LevelGeneratorType.BUFFET) {
                this.client.openScreen(new CustomizeBuffetLevelScreen(this, this.generatorOptions));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.enableCheatsButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), buttonWidget -> {
            this.tweakedCheats = true;
            this.cheatsEnabled = !this.cheatsEnabled;
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(CreateWorldScreen.this.cheatsEnabled && !CreateWorldScreen.this.hardcore));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.allowCommands.info"));
            }
        });
        this.enableCheatsButton.visible = false;
        this.generateBonusChestButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
            this.bonusChest = !this.bonusChest;
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(CreateWorldScreen.this.bonusChest && !CreateWorldScreen.this.hardcore));
            }
        });
        this.generateBonusChestButton.visible = false;
        this.createLevelButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), buttonWidget -> this.createLevel()));
        this.createLevelButton.active = !this.levelName.isEmpty();
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
        this.moreOptionsButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions()));
        this.gameRulesButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 185, 150, 20, new TranslatableText("selectWorld.gameRules"), buttonWidget -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
            this.client.openScreen(this);
            optional.ifPresent(gameRules -> {
                this.gameRules = gameRules;
            });
        }))));
        this.setMoreOptionsOpen(this.moreOptionsOpen);
        this.setInitialFocus(this.levelNameField);
        this.tweakDefaultsTo(this.currentMode);
        this.updateSaveFolderName();
    }

    private LevelGeneratorType getLevelGeneratorType() {
        return LevelGeneratorType.TYPES[this.generatorType];
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
        this.client.openScreen(null);
        if (this.creatingLevel) {
            return;
        }
        this.creatingLevel = true;
        long l = new Random().nextLong();
        String string = this.seedField.getText();
        if (!StringUtils.isEmpty(string)) {
            try {
                long m = Long.parseLong(string);
                if (m != 0L) {
                    l = m;
                }
            } catch (NumberFormatException numberFormatException) {
                l = string.hashCode();
            }
        }
        if (this.getLevelGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            GameRules gameRules = new GameRules();
            gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            levelInfo = new LevelInfo(this.levelNameField.getText().trim(), l, GameMode.SPECTATOR, false, false, Difficulty.PEACEFUL, this.generatorOptions, gameRules).enableCommands();
        } else {
            levelInfo = new LevelInfo(this.levelNameField.getText().trim(), l, this.currentMode.defaultGameMode, this.structures, this.hardcore, this.field_24290, this.generatorOptions, this.gameRules);
            if (this.bonusChest && !this.hardcore) {
                levelInfo.setBonusChest();
            }
            if (this.cheatsEnabled && !this.hardcore) {
                levelInfo.enableCommands();
            }
        }
        this.client.startIntegratedServer(this.saveDirectoryName, levelInfo);
    }

    private boolean isGeneratorTypeValid() {
        LevelGeneratorType levelGeneratorType = this.getLevelGeneratorType();
        if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
            return false;
        }
        if (levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            return CreateWorldScreen.hasShiftDown();
        }
        return true;
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
            this.generateBonusChestButton.active = false;
            this.field_24290 = Difficulty.HARD;
            this.field_24286.active = false;
        } else {
            this.hardcore = false;
            this.enableCheatsButton.active = true;
            this.generateBonusChestButton.active = true;
            this.field_24290 = this.field_24289;
            this.field_24286.active = true;
        }
        this.currentMode = mode;
        this.updateSettingsLabels();
    }

    private void setMoreOptionsOpen(boolean moreOptionsOpen) {
        this.moreOptionsOpen = moreOptionsOpen;
        this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
        this.field_24286.visible = !this.moreOptionsOpen;
        this.mapTypeSwitchButton.visible = this.moreOptionsOpen;
        if (this.getLevelGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.gameModeSwitchButton.active = false;
            if (this.lastMode == null) {
                this.lastMode = this.currentMode;
            }
            this.tweakDefaultsTo(Mode.DEBUG);
            this.generateStructuresButton.visible = false;
            this.generateBonusChestButton.visible = false;
            this.enableCheatsButton.visible = false;
            this.customizeTypeButton.visible = false;
        } else {
            this.gameModeSwitchButton.active = true;
            if (this.lastMode != null) {
                this.tweakDefaultsTo(this.lastMode);
            }
            this.generateStructuresButton.visible = this.moreOptionsOpen && this.getLevelGeneratorType() != LevelGeneratorType.CUSTOMIZED;
            this.generateBonusChestButton.visible = this.moreOptionsOpen;
            this.enableCheatsButton.visible = this.moreOptionsOpen;
            this.customizeTypeButton.visible = this.moreOptionsOpen && this.getLevelGeneratorType().isCustomizable();
        }
        this.seedField.setVisible(this.moreOptionsOpen);
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
            this.client.openScreen(this.parent);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.method_27534(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);
        if (this.moreOptionsOpen) {
            this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.enterSeed", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.seedInfo", new Object[0]), this.width / 2 - 100, 85, -6250336);
            if (this.generateStructuresButton.visible) {
                this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.mapFeatures.info", new Object[0]), this.width / 2 - 150, 122, -6250336);
            }
            if (this.enableCheatsButton.visible) {
                this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.allowCommands.info", new Object[0]), this.width / 2 - 150, 172, -6250336);
            }
            this.seedField.render(matrices, mouseX, mouseY, delta);
            if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
                this.textRenderer.drawTrimmed(this.getLevelGeneratorType().getInfoTranslationKey(), this.mapTypeSwitchButton.x + 2, this.mapTypeSwitchButton.y + 22, this.mapTypeSwitchButton.getWidth(), 0xA0A0A0);
            }
        } else {
            this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.resultFolder", new Object[0]) + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336);
            this.levelNameField.render(matrices, mouseX, mouseY, delta);
            this.method_27534(matrices, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 155 + 75, 137, -6250336);
            this.method_27534(matrices, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 155 + 75, 149, -6250336);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void recreateLevel(class_5219 arg) {
        LevelInfo levelInfo = arg.method_27433();
        this.levelName = levelInfo.method_27339();
        this.seed = Long.toString(levelInfo.getSeed());
        this.generatorOptions = levelInfo.getGeneratorOptions();
        LevelGeneratorType levelGeneratorType = this.generatorOptions.getType() == LevelGeneratorType.CUSTOMIZED ? LevelGeneratorType.DEFAULT : levelInfo.getGeneratorOptions().getType();
        this.generatorType = levelGeneratorType.getId();
        this.structures = levelInfo.hasStructures();
        this.cheatsEnabled = levelInfo.allowCommands();
        this.tweakedCheats = true;
        this.bonusChest = levelInfo.hasBonusChest();
        this.field_24290 = this.field_24289 = levelInfo.method_27340();
        this.gameRules.setAllValues(arg.getGameRules(), null);
        if (levelInfo.isHardcore()) {
            this.currentMode = Mode.HARDCORE;
        } else if (levelInfo.getGameMode().isSurvivalLike()) {
            this.currentMode = Mode.SURVIVAL;
        } else if (levelInfo.getGameMode().isCreative()) {
            this.currentMode = Mode.CREATIVE;
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

