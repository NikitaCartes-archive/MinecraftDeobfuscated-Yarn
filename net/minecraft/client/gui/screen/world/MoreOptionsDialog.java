/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(value=EnvType.CLIENT)
public class MoreOptionsDialog
implements TickableElement,
Drawable {
    private static final Logger field_25046 = LogManager.getLogger();
    private static final Text field_25047 = new TranslatableText("generator.custom");
    private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
    private TextRenderer textRenderer;
    private int parentWidth;
    private TextFieldWidget seedTextField;
    private ButtonWidget mapFeaturesButton;
    public ButtonWidget bonusItemsButton;
    private ButtonWidget mapTypeButton;
    private ButtonWidget customizeTypeButton;
    private ButtonWidget field_25048;
    private RegistryTracker.Modifiable field_25483;
    private GeneratorOptions generatorOptions;
    private Optional<GeneratorType> field_25049;
    private String seedText;

    public MoreOptionsDialog() {
        this.field_25483 = RegistryTracker.create();
        this.generatorOptions = GeneratorOptions.getDefaultOptions();
        this.field_25049 = Optional.of(GeneratorType.DEFAULT);
        this.seedText = "";
    }

    public MoreOptionsDialog(RegistryTracker.Modifiable modifiable, GeneratorOptions generatorOptions) {
        this.field_25483 = modifiable;
        this.generatorOptions = generatorOptions;
        this.field_25049 = GeneratorType.method_29078(generatorOptions);
        this.seedText = Long.toString(generatorOptions.getSeed());
    }

    public void method_28092(final CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.parentWidth = parent.width;
        this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.seedTextField.setText(this.seedText);
        this.seedTextField.setChangedListener(string -> {
            this.seedText = this.seedTextField.getText();
        });
        parent.addChild(this.seedTextField);
        int i = this.parentWidth / 2 - 155;
        int j = this.parentWidth / 2 + 5;
        this.mapFeaturesButton = parent.addButton(new ButtonWidget(i, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
            this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(MoreOptionsDialog.this.generatorOptions.shouldGenerateStructures()));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
            }
        });
        this.mapFeaturesButton.visible = false;
        this.mapTypeButton = parent.addButton(new ButtonWidget(j, 100, 150, 20, new TranslatableText("selectWorld.mapType"), buttonWidget -> {
            while (this.field_25049.isPresent()) {
                int i = GeneratorType.VALUES.indexOf(this.field_25049.get()) + 1;
                if (i >= GeneratorType.VALUES.size()) {
                    i = 0;
                }
                GeneratorType generatorType = GeneratorType.VALUES.get(i);
                this.field_25049 = Optional.of(generatorType);
                this.generatorOptions = generatorType.method_29077(this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
                if (this.generatorOptions.isDebugWorld() && !Screen.hasShiftDown()) continue;
            }
            parent.setMoreOptionsOpen();
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(MoreOptionsDialog.this.field_25049.map(GeneratorType::getTranslationKey).orElse(field_25047));
            }

            @Override
            protected MutableText getNarrationMessage() {
                if (Objects.equals(MoreOptionsDialog.this.field_25049, Optional.of(GeneratorType.AMPLIFIED))) {
                    return super.getNarrationMessage().append(". ").append(AMPLIFIED_INFO_TEXT);
                }
                return super.getNarrationMessage();
            }
        });
        this.mapTypeButton.visible = false;
        this.mapTypeButton.active = this.field_25049.isPresent();
        this.customizeTypeButton = parent.addButton(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
            GeneratorType.ScreenProvider screenProvider = GeneratorType.field_25053.get(this.field_25049);
            if (screenProvider != null) {
                client.openScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = parent.addButton(new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
            this.generatorOptions = this.generatorOptions.toggleBonusChest();
            buttonWidget.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(MoreOptionsDialog.this.generatorOptions.hasBonusChest() && !parent.hardcore));
            }
        });
        this.bonusItemsButton.visible = false;
        this.field_25048 = parent.addButton(new ButtonWidget(i, 185, 150, 20, new TranslatableText("selectWorld.import_worldgen_settings"), buttonWidget -> {
            DataResult<Object> dataResult;
            ServerResourceManager serverResourceManager;
            TranslatableText translatableText = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
            String string = TinyFileDialogs.tinyfd_openFileDialog(translatableText.getString(), null, null, null, false);
            if (string == null) {
                return;
            }
            RegistryTracker.Modifiable modifiable = RegistryTracker.create();
            ResourcePackManager<ResourcePackProfile> resourcePackManager = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(parent.method_29693().toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            try {
                MinecraftServer.loadDataPacks(resourcePackManager, createWorldScreen.field_25479, false);
                CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(resourcePackManager.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getServerWorkerExecutor(), client);
                client.runTasks(completableFuture::isDone);
                serverResourceManager = completableFuture.get();
            } catch (InterruptedException | ExecutionException exception) {
                field_25046.error("Error loading data packs when importing world settings", (Throwable)exception);
                TranslatableText text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
                LiteralText text2 = new LiteralText(exception.getMessage());
                client.getToastManager().add(SystemToast.method_29047(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
                resourcePackManager.close();
                return;
            }
            RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), modifiable);
            JsonParser jsonParser = new JsonParser();
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string, new String[0]));){
                JsonElement jsonElement = jsonParser.parse(bufferedReader);
                dataResult = GeneratorOptions.CODEC.parse(registryOps, jsonElement);
            } catch (JsonIOException | JsonSyntaxException | IOException exception2) {
                dataResult = DataResult.error("Failed to parse file: " + exception2.getMessage());
            }
            if (dataResult.error().isPresent()) {
                TranslatableText text3 = new TranslatableText("selectWorld.import_worldgen_settings.failure");
                String string2 = dataResult.error().get().message();
                field_25046.error("Error parsing world settings: {}", (Object)string2);
                LiteralText text4 = new LiteralText(string2);
                client.getToastManager().add(SystemToast.method_29047(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text3, text4));
            }
            Lifecycle lifecycle = dataResult.lifecycle();
            dataResult.resultOrPartial(field_25046::error).ifPresent(generatorOptions -> {
                BooleanConsumer booleanConsumer = bl -> {
                    client.openScreen(parent);
                    if (bl) {
                        this.method_29073(modifiable, (GeneratorOptions)generatorOptions);
                    }
                };
                if (lifecycle == Lifecycle.stable()) {
                    this.method_29073(modifiable, (GeneratorOptions)generatorOptions);
                } else if (lifecycle == Lifecycle.experimental()) {
                    client.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")));
                } else {
                    client.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")));
                }
            });
        }));
        this.field_25048.visible = false;
    }

    private void method_29073(RegistryTracker.Modifiable modifiable, GeneratorOptions generatorOptions) {
        this.field_25483 = modifiable;
        this.generatorOptions = generatorOptions;
        this.field_25049 = GeneratorType.method_29078(generatorOptions);
        this.seedText = Long.toString(generatorOptions.getSeed());
        this.seedTextField.setText(this.seedText);
        this.mapTypeButton.active = this.field_25049.isPresent();
    }

    @Override
    public void tick() {
        this.seedTextField.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.mapFeaturesButton.visible) {
            this.textRenderer.drawWithShadow(matrices, I18n.translate("selectWorld.mapFeatures.info", new Object[0]), (float)(this.parentWidth / 2 - 150), 122.0f, -6250336);
        }
        this.seedTextField.render(matrices, mouseX, mouseY, delta);
        if (this.field_25049.equals(Optional.of(GeneratorType.AMPLIFIED))) {
            this.textRenderer.drawTrimmed(AMPLIFIED_INFO_TEXT, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, this.mapTypeButton.getWidth(), 0xA0A0A0);
        }
    }

    protected void setGeneratorOptions(GeneratorOptions generatorOptions) {
        this.generatorOptions = generatorOptions;
    }

    private static OptionalLong tryParseLong(String string) {
        try {
            return OptionalLong.of(Long.parseLong(string));
        } catch (NumberFormatException numberFormatException) {
            return OptionalLong.empty();
        }
    }

    public GeneratorOptions getGeneratorOptions(boolean hardcore) {
        OptionalLong optionalLong2;
        String string = this.seedTextField.getText();
        OptionalLong optionalLong = StringUtils.isEmpty(string) ? OptionalLong.empty() : ((optionalLong2 = MoreOptionsDialog.tryParseLong(string)).isPresent() && optionalLong2.getAsLong() != 0L ? optionalLong2 : OptionalLong.of(string.hashCode()));
        return this.generatorOptions.withHardcore(hardcore, optionalLong);
    }

    public boolean isDebugWorld() {
        return this.generatorOptions.isDebugWorld();
    }

    public void setVisible(boolean visible) {
        this.mapTypeButton.visible = visible;
        if (this.generatorOptions.isDebugWorld()) {
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.field_25048.visible = false;
        } else {
            this.mapFeaturesButton.visible = visible;
            this.bonusItemsButton.visible = visible;
            this.customizeTypeButton.visible = visible && GeneratorType.field_25053.containsKey(this.field_25049);
            this.field_25048.visible = visible;
        }
        this.seedTextField.setVisible(visible);
    }

    public RegistryTracker.Modifiable method_29700() {
        return this.field_25483;
    }
}

