/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsWorldGeneratorType;
import net.minecraft.client.realms.gui.screen.ResetWorldInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsResetNormalWorldScreen
extends RealmsScreen {
    private static final Text RESET_SEED_TEXT = Text.method_43471("mco.reset.world.seed");
    private final Consumer<ResetWorldInfo> callback;
    private TextFieldWidget seedEdit;
    private RealmsWorldGeneratorType generatorType = RealmsWorldGeneratorType.DEFAULT;
    private boolean mapFeatures = true;
    private final Text parentTitle;

    public RealmsResetNormalWorldScreen(Consumer<ResetWorldInfo> callback, Text parentTitle) {
        super(Text.method_43471("mco.reset.world.generate"));
        this.callback = callback;
        this.parentTitle = parentTitle;
    }

    @Override
    public void tick() {
        this.seedEdit.tick();
        super.tick();
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, RealmsResetNormalWorldScreen.row(2), 200, 20, null, Text.method_43471("mco.reset.world.seed"));
        this.seedEdit.setMaxLength(32);
        this.addSelectableChild(this.seedEdit);
        this.setInitialFocus(this.seedEdit);
        this.addDrawableChild(CyclingButtonWidget.builder(RealmsWorldGeneratorType::getText).values((RealmsWorldGeneratorType[])RealmsWorldGeneratorType.values()).initially(this.generatorType).build(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(4), 205, 20, Text.method_43471("selectWorld.mapType"), (button, generatorType) -> {
            this.generatorType = generatorType;
        }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.mapFeatures).build(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(6) - 2, 205, 20, Text.method_43471("selectWorld.mapFeatures"), (button, mapFeatures) -> {
            this.mapFeatures = mapFeatures;
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(12), 97, 20, this.parentTitle, button -> this.callback.accept(new ResetWorldInfo(this.seedEdit.getText(), this.generatorType, this.mapFeatures))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 8, RealmsResetNormalWorldScreen.row(12), 97, 20, ScreenTexts.BACK, button -> this.close()));
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void close() {
        this.callback.accept(null);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsResetNormalWorldScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, RESET_SEED_TEXT, (float)(this.width / 2 - 100), (float)RealmsResetNormalWorldScreen.row(1), 0xA0A0A0);
        this.seedEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

