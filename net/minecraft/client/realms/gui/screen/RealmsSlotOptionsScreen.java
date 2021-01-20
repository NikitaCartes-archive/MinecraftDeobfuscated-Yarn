/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
    public static final List<GameMode> GAME_MODES = ImmutableList.of(GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE);
    private static final Text EDIT_SLOT_NAME = new TranslatableText("mco.configure.world.edit.slot.name");
    private static final Text SPAWN_PROTECTION = new TranslatableText("mco.configure.world.spawnProtection");
    private TextFieldWidget nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column2_x;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private final int activeSlot;
    private Difficulty field_27943;
    private GameMode gameModeIndex;
    private boolean pvp;
    private boolean spawnNPCs;
    private boolean spawnAnimals;
    private boolean spawnMonsters;
    private int difficultyIndex;
    private boolean commandBlocks;
    private boolean forceGameMode;
    private SettingsSlider spawnProtectionButton;
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
        this.client.keyboard.setRepeatEvents(false);
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

    private static <T> T method_32498(List<T> list, int i, int j) {
        try {
            return list.get(i);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return list.get(j);
        }
    }

    private static <T> int method_32499(List<T> list, T object, int i) {
        int j = list.indexOf(object);
        return j == -1 ? i : j;
    }

    @Override
    public void init() {
        this.column2_x = 170;
        this.column1_x = this.width / 2 - this.column2_x;
        int i = this.width / 2 + 10;
        this.field_27943 = RealmsSlotOptionsScreen.method_32498(DIFFICULTIES, this.options.difficulty, 2);
        this.gameModeIndex = RealmsSlotOptionsScreen.method_32498(GAME_MODES, this.options.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = this.options.pvp;
            this.difficultyIndex = this.options.spawnProtection;
            this.forceGameMode = this.options.forceGameMode;
            this.spawnAnimals = this.options.spawnAnimals;
            this.spawnMonsters = this.options.spawnMonsters;
            this.spawnNPCs = this.options.spawnNPCs;
            this.commandBlocks = this.options.commandBlocks;
        } else {
            TranslatableText text = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? new TranslatableText("mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? new TranslatableText("mco.configure.world.edit.subscreen.inspiration") : new TranslatableText("mco.configure.world.edit.subscreen.experience"));
            this.toastMessage = new RealmsLabel(text, this.width / 2, 26, 0xFF0000);
            this.pvp = true;
            this.difficultyIndex = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, this.column1_x + 2, RealmsSlotOptionsScreen.row(1), this.column2_x - 4, 20, null, new TranslatableText("mco.configure.world.edit.slot.name"));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
        this.focusOn(this.nameEdit);
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = this.addButton(CyclingButtonWidget.method_32613(this.pvp).build(i, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, new TranslatableText("mco.configure.world.pvp"), (cyclingButtonWidget, boolean_) -> {
            this.pvp = boolean_;
        }));
        this.addButton(CyclingButtonWidget.method_32606(GameMode::getSimpleTranslatableName).method_32620(GAME_MODES).value(this.gameModeIndex).build(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, new TranslatableText("selectWorld.gameMode"), (cyclingButtonWidget, gameMode) -> {
            this.gameModeIndex = gameMode;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget22 = this.addButton(CyclingButtonWidget.method_32613(this.spawnAnimals).build(i, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnAnimals"), (cyclingButtonWidget, boolean_) -> {
            this.spawnAnimals = boolean_;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget32 = CyclingButtonWidget.method_32613(this.field_27943 != Difficulty.PEACEFUL && this.spawnMonsters).build(i, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnMonsters"), (cyclingButtonWidget, boolean_) -> {
            this.spawnMonsters = boolean_;
        });
        this.addButton(CyclingButtonWidget.method_32606(Difficulty::getTranslatableName).method_32620(DIFFICULTIES).value(this.field_27943).build(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, new TranslatableText("options.difficulty"), (cyclingButtonWidget2, difficulty) -> {
            this.field_27943 = difficulty;
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                boolean bl;
                cyclingButtonWidget.active = bl = this.field_27943 != Difficulty.PEACEFUL;
                cyclingButtonWidget32.setValue(bl && this.spawnMonsters);
            }
        }));
        this.addButton(cyclingButtonWidget32);
        this.spawnProtectionButton = this.addButton(new SettingsSlider(this.column1_x, RealmsSlotOptionsScreen.row(7), this.column2_x, this.difficultyIndex, 0.0f, 16.0f));
        CyclingButtonWidget<Boolean> cyclingButtonWidget4 = this.addButton(CyclingButtonWidget.method_32613(this.spawnNPCs).build(i, RealmsSlotOptionsScreen.row(7), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnNPCs"), (cyclingButtonWidget, boolean_) -> {
            this.spawnNPCs = boolean_;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget5 = this.addButton(CyclingButtonWidget.method_32613(this.forceGameMode).build(this.column1_x, RealmsSlotOptionsScreen.row(9), this.column2_x, 20, new TranslatableText("mco.configure.world.forceGameMode"), (cyclingButtonWidget, boolean_) -> {
            this.forceGameMode = boolean_;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget6 = this.addButton(CyclingButtonWidget.method_32613(this.commandBlocks).build(i, RealmsSlotOptionsScreen.row(9), this.column2_x, 20, new TranslatableText("mco.configure.world.commandBlocks"), (cyclingButtonWidget, boolean_) -> {
            this.commandBlocks = boolean_;
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            cyclingButtonWidget3.active = false;
            cyclingButtonWidget22.active = false;
            cyclingButtonWidget4.active = false;
            cyclingButtonWidget32.active = false;
            this.spawnProtectionButton.active = false;
            cyclingButtonWidget6.active = false;
            cyclingButtonWidget5.active = false;
        }
        if (this.field_27943 == Difficulty.PEACEFUL) {
            cyclingButtonWidget32.active = false;
        }
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column2_x, 20, new TranslatableText("mco.configure.world.buttons.done"), buttonWidget -> this.saveSettings()));
        this.addButton(new ButtonWidget(i, RealmsSlotOptionsScreen.row(13), this.column2_x, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
        this.addChild(this.nameEdit);
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.buttons.options"), this.width / 2, 17, 0xFFFFFF));
        if (this.toastMessage != null) {
            this.addChild(this.toastMessage);
        }
        this.narrateLabels();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.textRenderer.draw(matrices, EDIT_SLOT_NAME, (float)(this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth(EDIT_SLOT_NAME) / 2), (float)(RealmsSlotOptionsScreen.row(0) - 5), 0xFFFFFF);
        this.titleLabel.render(this, matrices);
        if (this.toastMessage != null) {
            this.toastMessage.render(this, matrices);
        }
        this.nameEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private String getSlotName() {
        if (this.nameEdit.getText().equals(this.options.getDefaultSlotName(this.activeSlot))) {
            return "";
        }
        return this.nameEdit.getText();
    }

    private void saveSettings() {
        int i = RealmsSlotOptionsScreen.method_32499(DIFFICULTIES, this.field_27943, 2);
        int j = RealmsSlotOptionsScreen.method_32499(GAME_MODES, this.gameModeIndex, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, i, j, this.options.forceGameMode, this.getSlotName()));
        } else {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.difficultyIndex, this.commandBlocks, i, j, this.forceGameMode, this.getSlotName()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        private final double min;
        private final double max;

        public SettingsSlider(int x, int y, int width, int value, float min, float max) {
            super(x, y, width, 20, LiteralText.EMPTY, 0.0);
            this.min = min;
            this.max = max;
            this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!((RealmsSlotOptionsScreen)RealmsSlotOptionsScreen.this).spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.difficultyIndex = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ScreenTexts.composeGenericOptionText(SPAWN_PROTECTION, RealmsSlotOptionsScreen.this.difficultyIndex == 0 ? ScreenTexts.OFF : new LiteralText(String.valueOf(RealmsSlotOptionsScreen.this.difficultyIndex))));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
        }
    }
}

