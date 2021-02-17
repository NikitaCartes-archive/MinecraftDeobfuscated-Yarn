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
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(value=EnvType.CLIENT)
public class MoreOptionsDialog
implements TickableElement,
Drawable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text CUSTOM_TEXT = new TranslatableText("generator.custom");
    private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
    private static final Text MAP_FEATURES_INFO_TEXT = new TranslatableText("selectWorld.mapFeatures.info");
    private static final Text SELECT_SETTINGS_FILE_TEXT = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
    private MultilineText generatorInfoText = MultilineText.EMPTY;
    private TextRenderer textRenderer;
    private int parentWidth;
    private TextFieldWidget seedTextField;
    private CyclingButtonWidget<Boolean> mapFeaturesButton;
    private CyclingButtonWidget<Boolean> bonusItemsButton;
    private CyclingButtonWidget<GeneratorType> mapTypeButton;
    private ButtonWidget field_28001;
    private ButtonWidget customizeTypeButton;
    private ButtonWidget importSettingsButton;
    private DynamicRegistryManager.Impl registryManager;
    private GeneratorOptions generatorOptions;
    private Optional<GeneratorType> generatorType;
    private OptionalLong seed;

    public MoreOptionsDialog(DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions, Optional<GeneratorType> generatorType, OptionalLong seed) {
        this.registryManager = registryManager;
        this.generatorOptions = generatorOptions;
        this.generatorType = generatorType;
        this.seed = seed;
    }

    public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.parentWidth = parent.width;
        this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.seedTextField.setText(MoreOptionsDialog.seedToString(this.seed));
        this.seedTextField.setChangedListener(string -> {
            this.seed = this.getSeed();
        });
        parent.addChild(this.seedTextField);
        int i = this.parentWidth / 2 - 155;
        int j = this.parentWidth / 2 + 5;
        this.mapFeaturesButton = parent.addButton(CyclingButtonWidget.method_32613(this.generatorOptions.shouldGenerateStructures()).method_32623(cyclingButtonWidget -> cyclingButtonWidget.method_32611().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"))).build(i, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), (cyclingButtonWidget, boolean_) -> {
            this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
        }));
        this.mapFeaturesButton.visible = false;
        this.mapTypeButton = parent.addButton(CyclingButtonWidget.method_32606(GeneratorType::getTranslationKey).method_32621(GeneratorType.VALUES.stream().filter(GeneratorType::isNotDebug).collect(Collectors.toList()), GeneratorType.VALUES).method_32623(cyclingButtonWidget -> {
            if (cyclingButtonWidget.getValue() == GeneratorType.AMPLIFIED) {
                return cyclingButtonWidget.method_32611().append(". ").append(AMPLIFIED_INFO_TEXT);
            }
            return cyclingButtonWidget.method_32611();
        }).build(j, 100, 150, 20, new TranslatableText("selectWorld.mapType"), (cyclingButtonWidget, generatorType) -> {
            this.generatorType = Optional.of(generatorType);
            this.generatorOptions = generatorType.createDefaultOptions(this.registryManager, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
            parent.setMoreOptionsOpen();
        }));
        this.generatorType.ifPresent(this.mapTypeButton::setValue);
        this.mapTypeButton.visible = false;
        this.field_28001 = parent.addButton(new ButtonWidget(j, 100, 150, 20, ScreenTexts.composeGenericOptionText(new TranslatableText("selectWorld.mapType"), CUSTOM_TEXT), buttonWidget -> {}));
        this.field_28001.active = false;
        this.field_28001.visible = false;
        this.customizeTypeButton = parent.addButton(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
            GeneratorType.ScreenProvider screenProvider = GeneratorType.SCREEN_PROVIDERS.get(this.generatorType);
            if (screenProvider != null) {
                client.openScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = parent.addButton(CyclingButtonWidget.method_32613(this.generatorOptions.hasBonusChest() && !parent.hardcore).build(i, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), (cyclingButtonWidget, boolean_) -> {
            this.generatorOptions = this.generatorOptions.toggleBonusChest();
        }));
        this.bonusItemsButton.visible = false;
        this.importSettingsButton = parent.addButton(new ButtonWidget(i, 185, 150, 20, new TranslatableText("selectWorld.import_worldgen_settings"), buttonWidget -> {
            DataResult<Object> dataResult;
            ServerResourceManager serverResourceManager;
            String string = TinyFileDialogs.tinyfd_openFileDialog(SELECT_SETTINGS_FILE_TEXT.getString(), null, null, null, false);
            if (string == null) {
                return;
            }
            DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
            ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(parent.getDataPackTempDir().toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            try {
                MinecraftServer.loadDataPacks(resourcePackManager, createWorldScreen.dataPackSettings, false);
                CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(resourcePackManager.createResourcePacks(), impl, CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getMainWorkerExecutor(), client);
                client.runTasks(completableFuture::isDone);
                serverResourceManager = completableFuture.get();
            } catch (InterruptedException | ExecutionException exception) {
                LOGGER.error("Error loading data packs when importing world settings", (Throwable)exception);
                TranslatableText text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
                LiteralText text2 = new LiteralText(exception.getMessage());
                client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
                resourcePackManager.close();
                return;
            }
            RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
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
                LOGGER.error("Error parsing world settings: {}", (Object)string2);
                LiteralText text4 = new LiteralText(string2);
                client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text3, text4));
            }
            serverResourceManager.close();
            Lifecycle lifecycle = dataResult.lifecycle();
            dataResult.resultOrPartial(LOGGER::error).ifPresent(generatorOptions -> {
                BooleanConsumer booleanConsumer = bl -> {
                    client.openScreen(parent);
                    if (bl) {
                        this.importOptions(impl, (GeneratorOptions)generatorOptions);
                    }
                };
                if (lifecycle == Lifecycle.stable()) {
                    this.importOptions(impl, (GeneratorOptions)generatorOptions);
                } else if (lifecycle == Lifecycle.experimental()) {
                    client.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")));
                } else {
                    client.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")));
                }
            });
        }));
        this.importSettingsButton.visible = false;
        this.generatorInfoText = MultilineText.create(textRenderer, (StringVisitable)AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
    }

    private void importOptions(DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions) {
        this.registryManager = registryManager;
        this.generatorOptions = generatorOptions;
        this.generatorType = GeneratorType.fromGeneratorOptions(generatorOptions);
        this.method_32683(true);
        this.seed = OptionalLong.of(generatorOptions.getSeed());
        this.seedTextField.setText(MoreOptionsDialog.seedToString(this.seed));
    }

    @Override
    public void tick() {
        this.seedTextField.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.mapFeaturesButton.visible) {
            this.textRenderer.drawWithShadow(matrices, MAP_FEATURES_INFO_TEXT, (float)(this.parentWidth / 2 - 150), 122.0f, -6250336);
        }
        this.seedTextField.render(matrices, mouseX, mouseY, delta);
        if (this.generatorType.equals(Optional.of(GeneratorType.AMPLIFIED))) {
            this.generatorInfoText.drawWithShadow(matrices, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, this.textRenderer.fontHeight, 0xA0A0A0);
        }
    }

    protected void setGeneratorOptions(GeneratorOptions generatorOptions) {
        this.generatorOptions = generatorOptions;
    }

    private static String seedToString(OptionalLong seed) {
        if (seed.isPresent()) {
            return Long.toString(seed.getAsLong());
        }
        return "";
    }

    private static OptionalLong tryParseLong(String string) {
        try {
            return OptionalLong.of(Long.parseLong(string));
        } catch (NumberFormatException numberFormatException) {
            return OptionalLong.empty();
        }
    }

    public GeneratorOptions getGeneratorOptions(boolean hardcore) {
        OptionalLong optionalLong = this.getSeed();
        return this.generatorOptions.withHardcore(hardcore, optionalLong);
    }

    private OptionalLong getSeed() {
        OptionalLong optionalLong2;
        String string = this.seedTextField.getText();
        OptionalLong optionalLong = StringUtils.isEmpty(string) ? OptionalLong.empty() : ((optionalLong2 = MoreOptionsDialog.tryParseLong(string)).isPresent() && optionalLong2.getAsLong() != 0L ? optionalLong2 : OptionalLong.of(string.hashCode()));
        return optionalLong;
    }

    public boolean isDebugWorld() {
        return this.generatorOptions.isDebugWorld();
    }

    public void setVisible(boolean visible) {
        this.method_32683(visible);
        if (this.generatorOptions.isDebugWorld()) {
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        } else {
            this.mapFeaturesButton.visible = visible;
            this.bonusItemsButton.visible = visible;
            this.customizeTypeButton.visible = visible && GeneratorType.SCREEN_PROVIDERS.containsKey(this.generatorType);
            this.importSettingsButton.visible = visible;
        }
        this.seedTextField.setVisible(visible);
    }

    private void method_32683(boolean bl) {
        if (this.generatorType.isPresent()) {
            this.mapTypeButton.visible = bl;
            this.field_28001.visible = false;
        } else {
            this.mapTypeButton.visible = false;
            this.field_28001.visible = bl;
        }
    }

    public DynamicRegistryManager.Impl getRegistryManager() {
        return this.registryManager;
    }

    void loadDatapacks(ServerResourceManager serverResourceManager) {
        DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
        RegistryReadingOps<JsonElement> registryReadingOps = RegistryReadingOps.of(JsonOps.INSTANCE, this.registryManager);
        RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
        DataResult dataResult = GeneratorOptions.CODEC.encodeStart(registryReadingOps, this.generatorOptions).flatMap(jsonElement -> GeneratorOptions.CODEC.parse(registryOps, jsonElement));
        dataResult.resultOrPartial(Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error)).ifPresent(generatorOptions -> {
            this.generatorOptions = generatorOptions;
            this.registryManager = impl;
        });
    }

    public void disableBonusItems() {
        this.bonusItemsButton.active = false;
        this.bonusItemsButton.setValue(false);
    }

    public void enableBonusItems() {
        this.bonusItemsButton.active = true;
        this.bonusItemsButton.setValue(this.generatorOptions.hasBonusChest());
    }
}

