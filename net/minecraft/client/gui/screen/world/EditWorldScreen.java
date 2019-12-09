/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;

@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    private ButtonWidget saveButton;
    private final BooleanConsumer callback;
    private TextFieldWidget levelNameTextField;
    private final String levelName;

    public EditWorldScreen(BooleanConsumer callback, String levelName) {
        super(new TranslatableText("selectWorld.edit.title", new Object[0]));
        this.callback = callback;
        this.levelName = levelName;
    }

    @Override
    public void tick() {
        this.levelNameTextField.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.translate("selectWorld.edit.resetIcon", new Object[0]), buttonWidget -> {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            FileUtils.deleteQuietly(levelStorage.resolveFile(this.levelName, "icon.png"));
            buttonWidget.active = false;
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.translate("selectWorld.edit.openFolder", new Object[0]), buttonWidget -> {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            Util.getOperatingSystem().open(levelStorage.resolveFile(this.levelName, "icon.png").getParentFile());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.translate("selectWorld.edit.backup", new Object[0]), buttonWidget -> {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            EditWorldScreen.backupLevel(levelStorage, this.levelName);
            this.callback.accept(false);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.translate("selectWorld.edit.backupFolder", new Object[0]), buttonWidget -> {
            LevelStorage levelStorage = this.minecraft.getLevelStorage();
            Path path = levelStorage.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path.toFile());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.translate("selectWorld.edit.optimize", new Object[0]), buttonWidget -> this.minecraft.openScreen(new BackupPromptScreen(this, (bl, bl2) -> {
            if (bl) {
                EditWorldScreen.backupLevel(this.minecraft.getLevelStorage(), this.levelName);
            }
            this.minecraft.openScreen(new OptimizeWorldScreen(this.callback, this.levelName, this.minecraft.getLevelStorage(), bl2));
        }, new TranslatableText("optimizeWorld.confirm.title", new Object[0]), new TranslatableText("optimizeWorld.confirm.description", new Object[0]), true))));
        this.saveButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save", new Object[0]), buttonWidget -> this.commit()));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.callback.accept(false)));
        buttonWidget2.active = this.minecraft.getLevelStorage().resolveFile(this.levelName, "icon.png").isFile();
        LevelStorage levelStorage = this.minecraft.getLevelStorage();
        LevelProperties levelProperties = levelStorage.getLevelProperties(this.levelName);
        String string2 = levelProperties == null ? "" : levelProperties.getLevelName();
        this.levelNameTextField = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.translate("selectWorld.enterName", new Object[0]));
        this.levelNameTextField.setText(string2);
        this.levelNameTextField.setChangedListener(string -> {
            this.saveButton.active = !string.trim().isEmpty();
        });
        this.children.add(this.levelNameTextField);
        this.setInitialFocus(this.levelNameTextField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.levelNameTextField.getText();
        this.init(client, width, height);
        this.levelNameTextField.setText(string);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    private void commit() {
        LevelStorage levelStorage = this.minecraft.getLevelStorage();
        levelStorage.renameLevel(this.levelName, this.levelNameTextField.getText().trim());
        this.callback.accept(true);
    }

    public static void backupLevel(LevelStorage level, String name) {
        BaseText text2;
        TranslatableText text;
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        long l = 0L;
        IOException iOException = null;
        try {
            l = level.backupLevel(name);
        } catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            text = new TranslatableText("selectWorld.edit.backupFailed", new Object[0]);
            text2 = new LiteralText(iOException.getMessage());
        } else {
            text = new TranslatableText("selectWorld.edit.backupCreated", name);
            text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
        }
        toastManager.add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.font, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 40, 0xA0A0A0);
        this.levelNameTextField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}

