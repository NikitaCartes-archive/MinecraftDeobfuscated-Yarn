package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
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

@Environment(EnvType.CLIENT)
public class MoreOptionsDialog implements TickableElement, Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text CUSTOM_TEXT = new TranslatableText("generator.custom");
	private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
	private static final Text MAP_FEATURES_INFO_TEXT = new TranslatableText("selectWorld.mapFeatures.info");
	private static final Text field_28000 = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
	private class_5489 field_26605 = class_5489.field_26528;
	private TextRenderer textRenderer;
	private int parentWidth;
	private TextFieldWidget seedTextField;
	private CyclingButtonWidget<Boolean> mapFeaturesButton;
	private CyclingButtonWidget<Boolean> bonusItemsButton;
	private CyclingButtonWidget<GeneratorType> mapTypeButton;
	private ButtonWidget field_28001;
	private ButtonWidget customizeTypeButton;
	private ButtonWidget importOptionsButton;
	private DynamicRegistryManager.Impl registryManager;
	private GeneratorOptions generatorOptions;
	private Optional<GeneratorType> generatorType;
	private OptionalLong seed;

	public MoreOptionsDialog(
		DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions, Optional<GeneratorType> generatorType, OptionalLong seed
	) {
		this.registryManager = registryManager;
		this.generatorOptions = generatorOptions;
		this.generatorType = generatorType;
		this.seed = seed;
	}

	public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.parentWidth = parent.width;
		this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
		this.seedTextField.setText(toSeedText(this.seed));
		this.seedTextField.setChangedListener(string -> this.seed = this.getSeed());
		parent.addChild(this.seedTextField);
		int i = this.parentWidth / 2 - 155;
		int j = this.parentWidth / 2 + 5;
		this.mapFeaturesButton = parent.addButton(
			CyclingButtonWidget.method_32613(this.generatorOptions.shouldGenerateStructures())
				.method_32623(cyclingButtonWidget -> cyclingButtonWidget.method_32611().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info")))
				.build(
					i,
					100,
					150,
					20,
					new TranslatableText("selectWorld.mapFeatures"),
					(cyclingButtonWidget, boolean_) -> this.generatorOptions = this.generatorOptions.toggleGenerateStructures()
				)
		);
		this.mapFeaturesButton.visible = false;
		this.mapTypeButton = parent.addButton(
			CyclingButtonWidget.<GeneratorType>method_32606(GeneratorType::getTranslationKey)
				.method_32621((List<GeneratorType>)GeneratorType.VALUES.stream().filter(GeneratorType::method_32685).collect(Collectors.toList()), GeneratorType.VALUES)
				.method_32623(
					cyclingButtonWidget -> cyclingButtonWidget.getValue() == GeneratorType.AMPLIFIED
							? cyclingButtonWidget.method_32611().append(". ").append(AMPLIFIED_INFO_TEXT)
							: cyclingButtonWidget.method_32611()
				)
				.build(
					j,
					100,
					150,
					20,
					new TranslatableText("selectWorld.mapType"),
					(cyclingButtonWidget, generatorType) -> {
						this.generatorType = Optional.of(generatorType);
						this.generatorOptions = generatorType.createDefaultOptions(
							this.registryManager, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest()
						);
						parent.setMoreOptionsOpen();
					}
				)
		);
		this.generatorType.ifPresent(this.mapTypeButton::method_32605);
		this.mapTypeButton.visible = false;
		this.field_28001 = parent.addButton(
			new ButtonWidget(j, 100, 150, 20, ScreenTexts.method_32700(new TranslatableText("selectWorld.mapType"), CUSTOM_TEXT), buttonWidget -> {
			})
		);
		this.field_28001.active = false;
		this.field_28001.visible = false;
		this.customizeTypeButton = parent.addButton(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
			GeneratorType.ScreenProvider screenProvider = (GeneratorType.ScreenProvider)GeneratorType.SCREEN_PROVIDERS.get(this.generatorType);
			if (screenProvider != null) {
				client.openScreen(screenProvider.createEditScreen(parent, this.generatorOptions));
			}
		}));
		this.customizeTypeButton.visible = false;
		this.bonusItemsButton = parent.addButton(
			CyclingButtonWidget.method_32613(this.generatorOptions.hasBonusChest() && !parent.hardcore)
				.build(
					i,
					151,
					150,
					20,
					new TranslatableText("selectWorld.bonusItems"),
					(cyclingButtonWidget, boolean_) -> this.generatorOptions = this.generatorOptions.toggleBonusChest()
				)
		);
		this.bonusItemsButton.visible = false;
		this.importOptionsButton = parent.addButton(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.import_worldgen_settings"),
				buttonWidget -> {
					String string = TinyFileDialogs.tinyfd_openFileDialog(field_28000.getString(), null, null, null, false);
					if (string != null) {
						DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
						ResourcePackManager resourcePackManager = new ResourcePackManager(
							ResourceType.SERVER_DATA,
							new VanillaDataPackProvider(),
							new FileResourcePackProvider(parent.method_29693().toFile(), ResourcePackSource.PACK_SOURCE_WORLD)
						);

						ServerResourceManager serverResourceManager;
						try {
							MinecraftServer.loadDataPacks(resourcePackManager, parent.field_25479, false);
							CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(
								resourcePackManager.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getMainWorkerExecutor(), client
							);
							client.runTasks(completableFuture::isDone);
							serverResourceManager = (ServerResourceManager)completableFuture.get();
						} catch (ExecutionException | InterruptedException var24) {
							LOGGER.error("Error loading data packs when importing world settings", (Throwable)var24);
							Text text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							Text text2 = new LiteralText(var24.getMessage());
							client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
							resourcePackManager.close();
							return;
						}

						RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
						JsonParser jsonParser = new JsonParser();

						DataResult<GeneratorOptions> dataResult;
						try {
							BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string));
							Throwable string2 = null;

							try {
								JsonElement jsonElement = jsonParser.parse(bufferedReader);
								dataResult = GeneratorOptions.CODEC.parse(registryOps, jsonElement);
							} catch (Throwable var23) {
								string2 = var23;
								throw var23;
							} finally {
								if (bufferedReader != null) {
									if (string2 != null) {
										try {
											bufferedReader.close();
										} catch (Throwable var22) {
											string2.addSuppressed(var22);
										}
									} else {
										bufferedReader.close();
									}
								}
							}
						} catch (JsonIOException | JsonSyntaxException | IOException var26) {
							dataResult = DataResult.error("Failed to parse file: " + var26.getMessage());
						}

						if (dataResult.error().isPresent()) {
							Text text3 = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							String string2 = ((PartialResult)dataResult.error().get()).message();
							LOGGER.error("Error parsing world settings: {}", string2);
							Text text4 = new LiteralText(string2);
							client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text3, text4));
						}

						serverResourceManager.close();
						Lifecycle lifecycle = dataResult.lifecycle();
						dataResult.resultOrPartial(LOGGER::error)
							.ifPresent(
								generatorOptions -> {
									BooleanConsumer booleanConsumer = bl -> {
										client.openScreen(parent);
										if (bl) {
											this.importOptions(impl, generatorOptions);
										}
									};
									if (lifecycle == Lifecycle.stable()) {
										this.importOptions(impl, generatorOptions);
									} else if (lifecycle == Lifecycle.experimental()) {
										client.openScreen(
											new ConfirmScreen(
												booleanConsumer,
												new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"),
												new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")
											)
										);
									} else {
										client.openScreen(
											new ConfirmScreen(
												booleanConsumer,
												new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"),
												new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")
											)
										);
									}
								}
							);
					}
				}
			)
		);
		this.importOptionsButton.visible = false;
		this.field_26605 = class_5489.method_30890(textRenderer, AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
	}

	private void importOptions(DynamicRegistryManager.Impl registryManager, GeneratorOptions generatorOptions) {
		this.registryManager = registryManager;
		this.generatorOptions = generatorOptions;
		this.generatorType = GeneratorType.fromGeneratorOptions(generatorOptions);
		this.method_32683(true);
		this.seed = OptionalLong.of(generatorOptions.getSeed());
		this.seedTextField.setText(toSeedText(this.seed));
	}

	@Override
	public void tick() {
		this.seedTextField.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.mapFeaturesButton.visible) {
			this.textRenderer.drawWithShadow(matrices, MAP_FEATURES_INFO_TEXT, (float)(this.parentWidth / 2 - 150), 122.0F, -6250336);
		}

		this.seedTextField.render(matrices, mouseX, mouseY, delta);
		if (this.generatorType.equals(Optional.of(GeneratorType.AMPLIFIED))) {
			this.field_26605.method_30893(matrices, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, 9, 10526880);
		}
	}

	public void setGeneratorOptions(GeneratorOptions generatorOptions) {
		this.generatorOptions = generatorOptions;
	}

	private static String toSeedText(OptionalLong seed) {
		return seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
	}

	private static OptionalLong tryParseLong(String string) {
		try {
			return OptionalLong.of(Long.parseLong(string));
		} catch (NumberFormatException var2) {
			return OptionalLong.empty();
		}
	}

	public GeneratorOptions getGeneratorOptions(boolean hardcore) {
		OptionalLong optionalLong = this.getSeed();
		return this.generatorOptions.withHardcore(hardcore, optionalLong);
	}

	private OptionalLong getSeed() {
		String string = this.seedTextField.getText();
		OptionalLong optionalLong;
		if (StringUtils.isEmpty(string)) {
			optionalLong = OptionalLong.empty();
		} else {
			OptionalLong optionalLong2 = tryParseLong(string);
			if (optionalLong2.isPresent() && optionalLong2.getAsLong() != 0L) {
				optionalLong = optionalLong2;
			} else {
				optionalLong = OptionalLong.of((long)string.hashCode());
			}
		}

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
			this.importOptionsButton.visible = false;
		} else {
			this.mapFeaturesButton.visible = visible;
			this.bonusItemsButton.visible = visible;
			this.customizeTypeButton.visible = visible && GeneratorType.SCREEN_PROVIDERS.containsKey(this.generatorType);
			this.importOptionsButton.visible = visible;
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
		DataResult<GeneratorOptions> dataResult = GeneratorOptions.CODEC
			.encodeStart(registryReadingOps, this.generatorOptions)
			.flatMap(jsonElement -> GeneratorOptions.CODEC.parse(registryOps, jsonElement));
		dataResult.resultOrPartial(Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error)).ifPresent(generatorOptions -> {
			this.generatorOptions = generatorOptions;
			this.registryManager = impl;
		});
	}

	public void method_32682() {
		this.bonusItemsButton.active = false;
		this.bonusItemsButton.method_32605(false);
	}

	public void method_32684() {
		this.bonusItemsButton.active = true;
		this.bonusItemsButton.method_32605(this.generatorOptions.hasBonusChest());
	}
}
