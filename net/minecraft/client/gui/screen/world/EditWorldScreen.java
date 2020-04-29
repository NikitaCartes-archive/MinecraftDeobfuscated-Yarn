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
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    private static final Logger field_23776 = LogManager.getLogger();
    private ButtonWidget saveButton;
    private final BooleanConsumer callback;
    private TextFieldWidget levelNameTextField;
    private final LevelStorage.Session field_23777;

    public EditWorldScreen(BooleanConsumer callback, LevelStorage.Session session) {
        super(new TranslatableText("selectWorld.edit.title"));
        this.callback = callback;
        this.field_23777 = session;
    }

    @Override
    public void tick() {
        this.levelNameTextField.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableText("selectWorld.edit.resetIcon"), buttonWidget -> {
            FileUtils.deleteQuietly(this.field_23777.getIconFile());
            buttonWidget.active = false;
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableText("selectWorld.edit.openFolder"), buttonWidget -> Util.getOperatingSystem().open(this.field_23777.getDirectory(class_5218.field_24188).toFile())));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableText("selectWorld.edit.backup"), buttonWidget -> {
            boolean bl = EditWorldScreen.backupLevel(this.field_23777);
            this.callback.accept(!bl);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableText("selectWorld.edit.backupFolder"), buttonWidget -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            Path path = levelStorage.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path.toFile());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableText("selectWorld.edit.optimize"), buttonWidget -> this.client.openScreen(new BackupPromptScreen(this, (bl, bl2) -> {
            if (bl) {
                EditWorldScreen.backupLevel(this.field_23777);
            }
            this.client.openScreen(OptimizeWorldScreen.method_27031(this.callback, this.client.getDataFixer(), this.field_23777, bl2));
        }, new TranslatableText("optimizeWorld.confirm.title"), new TranslatableText("optimizeWorld.confirm.description"), true))));
        this.saveButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableText("selectWorld.edit.save"), buttonWidget -> this.commit()));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, ScreenTexts.CANCEL, buttonWidget -> this.callback.accept(false)));
        buttonWidget2.active = this.field_23777.getIconFile().isFile();
        class_5219 lv = this.field_23777.readLevelProperties();
        String string2 = lv == null ? "" : lv.getLevelName();
        this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 53, 200, 20, new TranslatableText("selectWorld.enterName"));
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
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void commit() {
        try {
            this.field_23777.save(this.levelNameTextField.getText().trim());
            this.callback.accept(true);
        } catch (IOException iOException) {
            field_23776.error("Failed to access world '{}'", (Object)this.field_23777.getDirectoryName(), (Object)iOException);
            SystemToast.method_27023(this.client, this.field_23777.getDirectoryName());
            this.callback.accept(true);
        }
    }

    public static boolean backupLevel(LevelStorage.Session session) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = session.createBackup();
        } catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            TranslatableText text = new TranslatableText("selectWorld.edit.backupFailed");
            LiteralText text2 = new LiteralText(iOException.getMessage());
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
            return false;
        }
        TranslatableText text = new TranslatableText("selectWorld.edit.backupCreated", session.getDirectoryName());
        TranslatableText text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        this.drawString(matrices, this.textRenderer, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 40, 0xA0A0A0);
        this.levelNameTextField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

