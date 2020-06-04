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
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5317;
import net.minecraft.class_5352;
import net.minecraft.class_5382;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(EnvType.CLIENT)
public class MoreOptionsDialog implements TickableElement, Drawable {
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
	private DimensionTracker.Modifiable field_25483;
	private GeneratorOptions generatorOptions;
	private Optional<class_5317> field_25049;
	private String seedText;

	public MoreOptionsDialog() {
		this.field_25483 = DimensionTracker.create();
		this.generatorOptions = GeneratorOptions.getDefaultOptions();
		this.field_25049 = Optional.of(class_5317.field_25050);
		this.seedText = "";
	}

	public MoreOptionsDialog(DimensionTracker.Modifiable modifiable, GeneratorOptions generatorOptions) {
		this.field_25483 = modifiable;
		this.generatorOptions = generatorOptions;
		this.field_25049 = class_5317.method_29078(generatorOptions);
		this.seedText = Long.toString(generatorOptions.getSeed());
	}

	public void method_28092(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.parentWidth = parent.width;
		this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
		this.seedTextField.setText(this.seedText);
		this.seedTextField.setChangedListener(string -> this.seedText = this.seedTextField.getText());
		parent.addChild(this.seedTextField);
		int i = this.parentWidth / 2 - 155;
		int j = this.parentWidth / 2 + 5;
		this.mapFeaturesButton = parent.addButton(new ButtonWidget(i, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
			this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
			buttonWidget.queueNarration(250);
		}) {
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
		this.mapTypeButton = parent.addButton(
			new ButtonWidget(
				j,
				100,
				150,
				20,
				new TranslatableText("selectWorld.mapType"),
				buttonWidget -> {
					while (this.field_25049.isPresent()) {
						int ix = class_5317.field_25052.indexOf(this.field_25049.get()) + 1;
						if (ix >= class_5317.field_25052.size()) {
							ix = 0;
						}

						class_5317 lv = (class_5317)class_5317.field_25052.get(ix);
						this.field_25049 = Optional.of(lv);
						this.generatorOptions = lv.method_29077(
							this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest()
						);
						if (!this.generatorOptions.isDebugWorld() || Screen.hasShiftDown()) {
							break;
						}
					}

					parent.setMoreOptionsOpen();
					buttonWidget.queueNarration(250);
				}
			) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(" ")
						.append((Text)MoreOptionsDialog.this.field_25049.map(class_5317::method_29075).orElse(MoreOptionsDialog.field_25047));
				}

				@Override
				protected MutableText getNarrationMessage() {
					return Objects.equals(MoreOptionsDialog.this.field_25049, Optional.of(class_5317.field_25051))
						? super.getNarrationMessage().append(". ").append(MoreOptionsDialog.AMPLIFIED_INFO_TEXT)
						: super.getNarrationMessage();
				}
			}
		);
		this.mapTypeButton.visible = false;
		this.mapTypeButton.active = this.field_25049.isPresent();
		this.customizeTypeButton = parent.addButton(new ButtonWidget(j, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
			class_5317.class_5293 lv = (class_5317.class_5293)class_5317.field_25053.get(this.field_25049);
			if (lv != null) {
				client.openScreen(lv.createEditScreen(parent, this.generatorOptions));
			}
		}));
		this.customizeTypeButton.visible = false;
		this.bonusItemsButton = parent.addButton(
			new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
				this.generatorOptions = this.generatorOptions.toggleBonusChest();
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(" ")
						.append(ScreenTexts.getToggleText(MoreOptionsDialog.this.generatorOptions.hasBonusChest() && !parent.hardcore));
				}
			}
		);
		this.bonusItemsButton.visible = false;
		this.field_25048 = parent.addButton(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.import_worldgen_settings"),
				buttonWidget -> {
					TranslatableText translatableText = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
					String string = TinyFileDialogs.tinyfd_openFileDialog(translatableText.getString(), null, null, null, false);
					if (string != null) {
						DimensionTracker.Modifiable modifiable = DimensionTracker.create();
						ResourcePackManager<ResourcePackProfile> resourcePackManager = new ResourcePackManager<>(
							ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(parent.method_29693().toFile(), class_5352.field_25349)
						);

						ServerResourceManager serverResourceManager;
						try {
							MinecraftServer.method_29736(resourcePackManager, parent.field_25479, false);
							CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(
								resourcePackManager.method_29211(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getServerWorkerExecutor(), client
							);
							client.runTasks(completableFuture::isDone);
							serverResourceManager = (ServerResourceManager)completableFuture.get();
						} catch (ExecutionException | InterruptedException var25) {
							field_25046.error("Error loading data packs when importing world settings", (Throwable)var25);
							Text text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							Text text2 = new LiteralText(var25.getMessage());
							client.getToastManager().add(SystemToast.method_29047(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
							resourcePackManager.close();
							return;
						}

						class_5382<JsonElement> lv = class_5382.method_29753(JsonOps.INSTANCE, serverResourceManager.getResourceManager(), modifiable);
						JsonParser jsonParser = new JsonParser();

						DataResult<GeneratorOptions> dataResult;
						try {
							BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string));
							Throwable string2 = null;

							try {
								JsonElement jsonElement = jsonParser.parse(bufferedReader);
								dataResult = GeneratorOptions.CODEC.parse(lv, jsonElement);
							} catch (Throwable var24) {
								string2 = var24;
								throw var24;
							} finally {
								if (bufferedReader != null) {
									if (string2 != null) {
										try {
											bufferedReader.close();
										} catch (Throwable var23) {
											string2.addSuppressed(var23);
										}
									} else {
										bufferedReader.close();
									}
								}
							}
						} catch (JsonIOException | JsonSyntaxException | IOException var27) {
							dataResult = DataResult.error("Failed to parse file: " + var27.getMessage());
						}

						if (dataResult.error().isPresent()) {
							Text text3 = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							String string2 = ((PartialResult)dataResult.error().get()).message();
							field_25046.error("Error parsing world settings: {}", string2);
							Text text4 = new LiteralText(string2);
							client.getToastManager().add(SystemToast.method_29047(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text3, text4));
						}

						Lifecycle lifecycle = dataResult.lifecycle();
						dataResult.resultOrPartial(field_25046::error)
							.ifPresent(
								generatorOptions -> {
									BooleanConsumer booleanConsumer = bl -> {
										client.openScreen(parent);
										if (bl) {
											this.method_29073(modifiable, generatorOptions);
										}
									};
									if (lifecycle == Lifecycle.stable()) {
										this.method_29073(modifiable, generatorOptions);
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
		this.field_25048.visible = false;
	}

	private void method_29073(DimensionTracker.Modifiable modifiable, GeneratorOptions generatorOptions) {
		this.field_25483 = modifiable;
		this.generatorOptions = generatorOptions;
		this.field_25049 = class_5317.method_29078(generatorOptions);
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
			this.textRenderer.drawWithShadow(matrices, I18n.translate("selectWorld.mapFeatures.info"), (float)(this.parentWidth / 2 - 150), 122.0F, -6250336);
		}

		this.seedTextField.render(matrices, mouseX, mouseY, delta);
		if (this.field_25049.equals(Optional.of(class_5317.field_25051))) {
			this.textRenderer.drawTrimmed(AMPLIFIED_INFO_TEXT, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, this.mapTypeButton.getWidth(), 10526880);
		}
	}

	public void setGeneratorOptions(GeneratorOptions generatorOptions) {
		this.generatorOptions = generatorOptions;
	}

	private static OptionalLong tryParseLong(String string) {
		try {
			return OptionalLong.of(Long.parseLong(string));
		} catch (NumberFormatException var2) {
			return OptionalLong.empty();
		}
	}

	public GeneratorOptions getGeneratorOptions(boolean hardcore) {
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
			this.customizeTypeButton.visible = visible && class_5317.field_25053.containsKey(this.field_25049);
			this.field_25048.visible = visible;
		}

		this.seedTextField.setVisible(visible);
	}

	public DimensionTracker.Modifiable method_29700() {
		return this.field_25483;
	}
}
