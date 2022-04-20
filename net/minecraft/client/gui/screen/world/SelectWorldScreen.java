/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SelectWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Screen parent;
    @Nullable
    private List<OrderedText> tooltip;
    private ButtonWidget deleteButton;
    private ButtonWidget selectButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    protected TextFieldWidget searchBox;
    private WorldListWidget levelList;

    public SelectWorldScreen(Screen parent) {
        super(Text.method_43471("selectWorld.title"));
        this.parent = parent;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.method_43471("selectWorld.search"));
        this.searchBox.setChangedListener(search -> this.levelList.filter((String)search));
        this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, this.method_43450(), this.levelList);
        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.levelList);
        this.selectButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, Text.method_43471("selectWorld.select"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::play)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, Text.method_43471("selectWorld.create"), button -> CreateWorldScreen.create(this.client, this)));
        this.editButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20, Text.method_43471("selectWorld.edit"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::edit)));
        this.deleteButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20, Text.method_43471("selectWorld.delete"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::deleteIfConfirmed)));
        this.recreateButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20, Text.method_43471("selectWorld.recreate"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::recreate)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
        this.worldSelected(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltip = null;
        this.levelList.render(matrices, mouseX, mouseY, delta);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        SelectWorldScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltip != null) {
            this.renderOrderedTooltip(matrices, this.tooltip, mouseX, mouseY);
        }
    }

    public void setTooltip(List<OrderedText> tooltip) {
        this.tooltip = tooltip;
    }

    public void worldSelected(boolean active) {
        this.selectButton.active = active;
        this.deleteButton.active = active;
        this.editButton.active = active;
        this.recreateButton.active = active;
    }

    @Override
    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(WorldListWidget.class_7414::close);
        }
    }

    public Supplier<String> method_43450() {
        return () -> this.searchBox.getText();
    }

    private /* synthetic */ void method_35739(ButtonWidget button) {
        try {
            WorldListWidget.Entry entry;
            WorldListWidget.class_7414 lv;
            String string = "DEBUG world";
            if (!this.levelList.children().isEmpty() && (lv = (WorldListWidget.class_7414)this.levelList.children().get(0)) instanceof WorldListWidget.Entry && (entry = (WorldListWidget.Entry)lv).getLevelDisplayName().equals("DEBUG world")) {
                entry.delete();
            }
            DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.BUILTIN.get();
            GeneratorOptions generatorOptions = WorldPresets.createDefaultOptions(dynamicRegistryManager, "test1".hashCode());
            LevelInfo levelInfo = new LevelInfo("DEBUG world", GameMode.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), DataPackSettings.SAFE_MODE);
            String string2 = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), "DEBUG world", "");
            this.client.method_41735().createAndStart(string2, levelInfo, dynamicRegistryManager, generatorOptions);
        } catch (IOException iOException) {
            LOGGER.error("Failed to recreate the debug world", iOException);
        }
    }
}

