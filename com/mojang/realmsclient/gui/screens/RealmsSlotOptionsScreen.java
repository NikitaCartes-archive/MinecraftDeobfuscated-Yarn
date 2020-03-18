/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    public static final String[] DIFFICULTIES = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
    public static final String[] GAME_MODES = new String[]{"selectWorld.gameMode.survival", "selectWorld.gameMode.creative", "selectWorld.gameMode.adventure"};
    private TextFieldWidget nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column_width;
    private int column2_x;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private final int activeSlot;
    private int difficultyIndex;
    private int gameModeIndex;
    private Boolean pvp;
    private Boolean spawnNPCs;
    private Boolean spawnAnimals;
    private Boolean spawnMonsters;
    private Integer spawnProtection;
    private Boolean commandBlocks;
    private Boolean forceGameMode;
    private ButtonWidget pvpButton;
    private ButtonWidget spawnAnimalsButton;
    private ButtonWidget spawnMonstersButton;
    private ButtonWidget spawnNPCsButton;
    private SettingsSlider spawnProtectionButton;
    private ButtonWidget commandBlocksButton;
    private ButtonWidget gameModeButton;
    private RealmsLabel titleLabel;
    private RealmsLabel toastMessage;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen parent, RealmsWorldOptions options, RealmsServer.WorldType worldType, int activeSlot) {
        this.parent = parent;
        this.options = options;
        this.worldType = worldType;
        this.activeSlot = activeSlot;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void init() {
        this.column_width = 170;
        this.column1_x = this.width / 2 - this.column_width;
        this.column2_x = this.width / 2 + 10;
        this.difficultyIndex = this.options.difficulty;
        this.gameModeIndex = this.options.gameMode;
        if (this.worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = this.options.pvp;
            this.spawnProtection = this.options.spawnProtection;
            this.forceGameMode = this.options.forceGameMode;
            this.spawnAnimals = this.options.spawnAnimals;
            this.spawnMonsters = this.options.spawnMonsters;
            this.spawnNPCs = this.options.spawnNPCs;
            this.commandBlocks = this.options.commandBlocks;
        } else {
            String string = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? I18n.translate("mco.configure.world.edit.subscreen.adventuremap", new Object[0]) : (this.worldType == RealmsServer.WorldType.INSPIRATION ? I18n.translate("mco.configure.world.edit.subscreen.inspiration", new Object[0]) : I18n.translate("mco.configure.world.edit.subscreen.experience", new Object[0]));
            this.toastMessage = new RealmsLabel(string, this.width / 2, 26, 0xFF0000);
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, this.column1_x + 2, RealmsSlotOptionsScreen.row(1), this.column_width - 4, 20, null, I18n.translate("mco.configure.world.edit.slot.name", new Object[0]));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
        this.focusOn(this.nameEdit);
        this.pvpButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(1), this.column_width, 20, this.pvpTitle(), buttonWidget -> {
            this.pvp = this.pvp == false;
            buttonWidget.setMessage(this.pvpTitle());
        }));
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column_width, 20, this.gameModeTitle(), buttonWidget -> {
            this.gameModeIndex = (this.gameModeIndex + 1) % GAME_MODES.length;
            buttonWidget.setMessage(this.gameModeTitle());
        }));
        this.spawnAnimalsButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(3), this.column_width, 20, this.spawnAnimalsTitle(), buttonWidget -> {
            this.spawnAnimals = this.spawnAnimals == false;
            buttonWidget.setMessage(this.spawnAnimalsTitle());
        }));
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column_width, 20, this.difficultyTitle(), buttonWidget -> {
            this.difficultyIndex = (this.difficultyIndex + 1) % DIFFICULTIES.length;
            buttonWidget.setMessage(this.difficultyTitle());
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                this.spawnMonstersButton.active = this.difficultyIndex != 0;
                this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
            }
        }));
        this.spawnMonstersButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(5), this.column_width, 20, this.spawnMonstersTitle(), buttonWidget -> {
            this.spawnMonsters = this.spawnMonsters == false;
            buttonWidget.setMessage(this.spawnMonstersTitle());
        }));
        this.spawnProtectionButton = this.addButton(new SettingsSlider(this.column1_x, RealmsSlotOptionsScreen.row(7), this.column_width, this.spawnProtection, 0.0f, 16.0f));
        this.spawnNPCsButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(7), this.column_width, 20, this.spawnNPCsTitle(), buttonWidget -> {
            this.spawnNPCs = this.spawnNPCs == false;
            buttonWidget.setMessage(this.spawnNPCsTitle());
        }));
        this.gameModeButton = this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(9), this.column_width, 20, this.forceGameModeTitle(), buttonWidget -> {
            this.forceGameMode = this.forceGameMode == false;
            buttonWidget.setMessage(this.forceGameModeTitle());
        }));
        this.commandBlocksButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(9), this.column_width, 20, this.commandBlocksTitle(), buttonWidget -> {
            this.commandBlocks = this.commandBlocks == false;
            buttonWidget.setMessage(this.commandBlocksTitle());
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            this.pvpButton.active = false;
            this.spawnAnimalsButton.active = false;
            this.spawnNPCsButton.active = false;
            this.spawnMonstersButton.active = false;
            this.spawnProtectionButton.active = false;
            this.commandBlocksButton.active = false;
            this.gameModeButton.active = false;
        }
        if (this.difficultyIndex == 0) {
            this.spawnMonstersButton.active = false;
        }
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column_width, 20, I18n.translate("mco.configure.world.buttons.done", new Object[0]), buttonWidget -> this.saveSettings()));
        this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(13), this.column_width, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.client.openScreen(this.parent)));
        this.addChild(this.nameEdit);
        this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.buttons.options", new Object[0]), this.width / 2, 17, 0xFFFFFF));
        if (this.toastMessage != null) {
            this.addChild(this.toastMessage);
        }
        this.narrateLabels();
    }

    private String difficultyTitle() {
        return I18n.translate("options.difficulty", new Object[0]) + ": " + I18n.translate(DIFFICULTIES[this.difficultyIndex], new Object[0]);
    }

    private String gameModeTitle() {
        return I18n.translate("selectWorld.gameMode", new Object[0]) + ": " + I18n.translate(GAME_MODES[this.gameModeIndex], new Object[0]);
    }

    private String pvpTitle() {
        return I18n.translate("mco.configure.world.pvp", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.pvp);
    }

    private String spawnAnimalsTitle() {
        return I18n.translate("mco.configure.world.spawnAnimals", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnAnimals);
    }

    private String spawnMonstersTitle() {
        if (this.difficultyIndex == 0) {
            return I18n.translate("mco.configure.world.spawnMonsters", new Object[0]) + ": " + I18n.translate("mco.configure.world.off", new Object[0]);
        }
        return I18n.translate("mco.configure.world.spawnMonsters", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnMonsters);
    }

    private String spawnNPCsTitle() {
        return I18n.translate("mco.configure.world.spawnNPCs", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnNPCs);
    }

    private String commandBlocksTitle() {
        return I18n.translate("mco.configure.world.commandBlocks", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.commandBlocks);
    }

    private String forceGameModeTitle() {
        return I18n.translate("mco.configure.world.forceGameMode", new Object[0]) + ": " + RealmsSlotOptionsScreen.getWorldConfigureMessage(this.forceGameMode);
    }

    private static String getWorldConfigureMessage(boolean enabled) {
        return I18n.translate(enabled ? "mco.configure.world.on" : "mco.configure.world.off", new Object[0]);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        String string = I18n.translate("mco.configure.world.edit.slot.name", new Object[0]);
        this.textRenderer.draw(string, this.column1_x + this.column_width / 2 - this.textRenderer.getStringWidth(string) / 2, RealmsSlotOptionsScreen.row(0) - 5, 0xFFFFFF);
        this.titleLabel.render(this);
        if (this.toastMessage != null) {
            this.toastMessage.render(this);
        }
        this.nameEdit.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }

    private String getSlotName() {
        if (this.nameEdit.getText().equals(this.options.getDefaultSlotName(this.activeSlot))) {
            return "";
        }
        return this.nameEdit.getText();
    }

    private void saveSettings() {
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.options.forceGameMode, this.getSlotName()));
        } else {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.forceGameMode, this.getSlotName()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        private final double min;
        private final double max;

        public SettingsSlider(int id, int x, int y, int width, float min, float max) {
            super(id, x, y, 20, "", 0.0);
            this.min = min;
            this.max = max;
            this.value = (MathHelper.clamp((float)width, min, max) - min) / (max - min);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!((RealmsSlotOptionsScreen)RealmsSlotOptionsScreen.this).spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(I18n.translate("mco.configure.world.spawnProtection", new Object[0]) + ": " + (RealmsSlotOptionsScreen.this.spawnProtection == 0 ? I18n.translate("mco.configure.world.off", new Object[0]) : RealmsSlotOptionsScreen.this.spawnProtection));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
        }
    }
}

