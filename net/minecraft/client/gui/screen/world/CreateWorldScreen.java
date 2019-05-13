/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FileNameUtil;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private final Screen parent;
    private TextFieldWidget textFieldLevelName;
    private TextFieldWidget textFieldSeed;
    private String field_3196;
    private String gameMode = "survival";
    private String field_3185;
    private boolean structures = true;
    private boolean commandsAllowed;
    private boolean field_3179;
    private boolean enableBonusItems;
    private boolean field_3178;
    private boolean isCreatingLevel;
    private boolean field_3202;
    private ButtonWidget buttonCreateLevel;
    private ButtonWidget buttonGameModeSwitch;
    private ButtonWidget buttonMoreOptions;
    private ButtonWidget buttonGenerateStructures;
    private ButtonWidget buttonGenerateBonusItems;
    private ButtonWidget buttonMapTypeSwitch;
    private ButtonWidget buttonCommandsAllowed;
    private ButtonWidget buttonCustomizeType;
    private String field_3194;
    private String field_3199;
    private String seed;
    private String levelName;
    private int generatorType;
    public CompoundTag generatorOptionsTag = new CompoundTag();

    public CreateWorldScreen(Screen screen) {
        super(new TranslatableComponent("selectWorld.create", new Object[0]));
        this.parent = screen;
        this.seed = "";
        this.levelName = I18n.translate("selectWorld.newWorld", new Object[0]);
    }

    @Override
    public void tick() {
        this.textFieldLevelName.tick();
        this.textFieldSeed.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.textFieldLevelName = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName", new Object[0]));
        this.textFieldLevelName.setText(this.levelName);
        this.textFieldLevelName.setChangedListener(string -> {
            this.levelName = string;
            this.buttonCreateLevel.active = !this.textFieldLevelName.getText().isEmpty();
            this.method_2727();
        });
        this.children.add(this.textFieldLevelName);
        this.buttonGameModeSwitch = this.addButton(new ButtonWidget(this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode", new Object[0]), buttonWidget -> {
            if ("survival".equals(this.gameMode)) {
                if (!this.field_3179) {
                    this.commandsAllowed = false;
                }
                this.field_3178 = false;
                this.gameMode = "hardcore";
                this.field_3178 = true;
                this.buttonCommandsAllowed.active = false;
                this.buttonGenerateBonusItems.active = false;
                this.method_2722();
            } else if ("hardcore".equals(this.gameMode)) {
                if (!this.field_3179) {
                    this.commandsAllowed = true;
                }
                this.field_3178 = false;
                this.gameMode = "creative";
                this.method_2722();
                this.field_3178 = false;
                this.buttonCommandsAllowed.active = true;
                this.buttonGenerateBonusItems.active = true;
            } else {
                if (!this.field_3179) {
                    this.commandsAllowed = false;
                }
                this.gameMode = "survival";
                this.method_2722();
                this.buttonCommandsAllowed.active = true;
                this.buttonGenerateBonusItems.active = true;
                this.field_3178 = false;
            }
            this.method_2722();
        }));
        this.textFieldSeed = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterSeed", new Object[0]));
        this.textFieldSeed.setText(this.seed);
        this.textFieldSeed.setChangedListener(string -> {
            this.seed = this.textFieldSeed.getText();
        });
        this.children.add(this.textFieldSeed);
        this.buttonGenerateStructures = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures", new Object[0]), buttonWidget -> {
            this.structures = !this.structures;
            this.method_2722();
        }));
        this.buttonGenerateStructures.visible = false;
        this.buttonMapTypeSwitch = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType", new Object[0]), buttonWidget -> {
            ++this.generatorType;
            if (this.generatorType >= LevelGeneratorType.TYPES.length) {
                this.generatorType = 0;
            }
            while (!this.method_2723()) {
                ++this.generatorType;
                if (this.generatorType < LevelGeneratorType.TYPES.length) continue;
                this.generatorType = 0;
            }
            this.generatorOptionsTag = new CompoundTag();
            this.method_2722();
            this.method_2710(this.field_3202);
        }));
        this.buttonMapTypeSwitch.visible = false;
        this.buttonCustomizeType = this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType", new Object[0]), buttonWidget -> {
            if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT) {
                this.minecraft.openScreen(new CustomizeFlatLevelScreen(this, this.generatorOptionsTag));
            }
            if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.BUFFET) {
                this.minecraft.openScreen(new CustomizeBuffetLevelScreen(this, this.generatorOptionsTag));
            }
        }));
        this.buttonCustomizeType.visible = false;
        this.buttonCommandsAllowed = this.addButton(new ButtonWidget(this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands", new Object[0]), buttonWidget -> {
            this.field_3179 = true;
            this.commandsAllowed = !this.commandsAllowed;
            this.method_2722();
        }));
        this.buttonCommandsAllowed.visible = false;
        this.buttonGenerateBonusItems = this.addButton(new ButtonWidget(this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems", new Object[0]), buttonWidget -> {
            this.enableBonusItems = !this.enableBonusItems;
            this.method_2722();
        }));
        this.buttonGenerateBonusItems.visible = false;
        this.buttonMoreOptions = this.addButton(new ButtonWidget(this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions", new Object[0]), buttonWidget -> this.method_2721()));
        this.buttonCreateLevel = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create", new Object[0]), buttonWidget -> this.createLevel()));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.method_2710(this.field_3202);
        this.setInitialFocus(this.textFieldLevelName);
        this.method_2727();
        this.method_2722();
    }

    private void method_2727() {
        this.field_3196 = this.textFieldLevelName.getText().trim();
        if (this.field_3196.length() == 0) {
            this.field_3196 = "World";
        }
        try {
            this.field_3196 = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.field_3196, "");
        } catch (Exception exception) {
            this.field_3196 = "World";
            try {
                this.field_3196 = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.field_3196, "");
            } catch (Exception exception2) {
                throw new RuntimeException("Could not create save folder", exception2);
            }
        }
    }

    private void method_2722() {
        this.buttonGameModeSwitch.setMessage(I18n.translate("selectWorld.gameMode", new Object[0]) + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode, new Object[0]));
        this.field_3194 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line1", new Object[0]);
        this.field_3199 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line2", new Object[0]);
        this.buttonGenerateStructures.setMessage(I18n.translate("selectWorld.mapFeatures", new Object[0]) + ' ' + I18n.translate(this.structures ? "options.on" : "options.off", new Object[0]));
        this.buttonGenerateBonusItems.setMessage(I18n.translate("selectWorld.bonusItems", new Object[0]) + ' ' + I18n.translate(this.enableBonusItems && !this.field_3178 ? "options.on" : "options.off", new Object[0]));
        this.buttonMapTypeSwitch.setMessage(I18n.translate("selectWorld.mapType", new Object[0]) + ' ' + I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey(), new Object[0]));
        this.buttonCommandsAllowed.setMessage(I18n.translate("selectWorld.allowCommands", new Object[0]) + ' ' + I18n.translate(this.commandsAllowed && !this.field_3178 ? "options.on" : "options.off", new Object[0]));
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    private void createLevel() {
        this.minecraft.openScreen(null);
        if (this.isCreatingLevel) {
            return;
        }
        this.isCreatingLevel = true;
        long l = new Random().nextLong();
        String string = this.textFieldSeed.getText();
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
        LevelInfo levelInfo = new LevelInfo(l, GameMode.byName(this.gameMode), this.structures, this.field_3178, LevelGeneratorType.TYPES[this.generatorType]);
        levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.generatorOptionsTag));
        if (this.enableBonusItems && !this.field_3178) {
            levelInfo.setBonusChest();
        }
        if (this.commandsAllowed && !this.field_3178) {
            levelInfo.enableCommands();
        }
        this.minecraft.startIntegratedServer(this.field_3196, this.textFieldLevelName.getText().trim(), levelInfo);
    }

    private boolean method_2723() {
        LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[this.generatorType];
        if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
            return false;
        }
        if (levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            return CreateWorldScreen.hasShiftDown();
        }
        return true;
    }

    private void method_2721() {
        this.method_2710(!this.field_3202);
    }

    private void method_2710(boolean bl) {
        this.field_3202 = bl;
        if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.buttonGameModeSwitch.visible = !this.field_3202;
            this.buttonGameModeSwitch.active = false;
            if (this.field_3185 == null) {
                this.field_3185 = this.gameMode;
            }
            this.gameMode = "spectator";
            this.buttonGenerateStructures.visible = false;
            this.buttonGenerateBonusItems.visible = false;
            this.buttonMapTypeSwitch.visible = this.field_3202;
            this.buttonCommandsAllowed.visible = false;
            this.buttonCustomizeType.visible = false;
        } else {
            this.buttonGameModeSwitch.visible = !this.field_3202;
            this.buttonGameModeSwitch.active = true;
            if (this.field_3185 != null) {
                this.gameMode = this.field_3185;
                this.field_3185 = null;
            }
            this.buttonGenerateStructures.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
            this.buttonGenerateBonusItems.visible = this.field_3202;
            this.buttonMapTypeSwitch.visible = this.field_3202;
            this.buttonCommandsAllowed.visible = this.field_3202;
            this.buttonCustomizeType.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType].isCustomizable();
        }
        this.method_2722();
        this.textFieldSeed.setVisible(this.field_3202);
        this.textFieldLevelName.setVisible(!this.field_3202);
        if (this.field_3202) {
            this.buttonMoreOptions.setMessage(I18n.translate("gui.done", new Object[0]));
        } else {
            this.buttonMoreOptions.setMessage(I18n.translate("selectWorld.moreWorldOptions", new Object[0]));
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 257 || i == 335) {
            this.createLevel();
            return true;
        }
        return false;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, -1);
        if (this.field_3202) {
            this.drawString(this.font, I18n.translate("selectWorld.enterSeed", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.translate("selectWorld.seedInfo", new Object[0]), this.width / 2 - 100, 85, -6250336);
            if (this.buttonGenerateStructures.visible) {
                this.drawString(this.font, I18n.translate("selectWorld.mapFeatures.info", new Object[0]), this.width / 2 - 150, 122, -6250336);
            }
            if (this.buttonCommandsAllowed.visible) {
                this.drawString(this.font, I18n.translate("selectWorld.allowCommands.info", new Object[0]), this.width / 2 - 150, 172, -6250336);
            }
            this.textFieldSeed.render(i, j, f);
            if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
                this.font.drawStringBounded(I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey(), new Object[0]), this.buttonMapTypeSwitch.x + 2, this.buttonMapTypeSwitch.y + 22, this.buttonMapTypeSwitch.getWidth(), 0xA0A0A0);
            }
        } else {
            this.drawString(this.font, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.translate("selectWorld.resultFolder", new Object[0]) + " " + this.field_3196, this.width / 2 - 100, 85, -6250336);
            this.textFieldLevelName.render(i, j, f);
            this.drawCenteredString(this.font, this.field_3194, this.width / 2, 137, -6250336);
            this.drawCenteredString(this.font, this.field_3199, this.width / 2, 149, -6250336);
        }
        super.render(i, j, f);
    }

    public void recreateLevel(LevelProperties levelProperties) {
        this.levelName = levelProperties.getLevelName();
        this.seed = levelProperties.getSeed() + "";
        LevelGeneratorType levelGeneratorType = levelProperties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED ? LevelGeneratorType.DEFAULT : levelProperties.getGeneratorType();
        this.generatorType = levelGeneratorType.getId();
        this.generatorOptionsTag = levelProperties.getGeneratorOptions();
        this.structures = levelProperties.hasStructures();
        this.commandsAllowed = levelProperties.areCommandsAllowed();
        if (levelProperties.isHardcore()) {
            this.gameMode = "hardcore";
        } else if (levelProperties.getGameMode().isSurvivalLike()) {
            this.gameMode = "survival";
        } else if (levelProperties.getGameMode().isCreative()) {
            this.gameMode = "creative";
        }
    }
}

