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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5317;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
	private GeneratorOptions generatorOptions;
	private Optional<class_5317> field_25049;
	private String seedText;

	public MoreOptionsDialog() {
		this.generatorOptions = GeneratorOptions.getDefaultOptions();
		this.field_25049 = Optional.of(class_5317.field_25050);
		this.seedText = "";
	}

	public MoreOptionsDialog(GeneratorOptions generatorOptions) {
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
		this.mapFeaturesButton = parent.addButton(
			new ButtonWidget(this.parentWidth / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
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
			}
		);
		this.mapFeaturesButton.visible = false;
		this.mapTypeButton = parent.addButton(
			new ButtonWidget(
				this.parentWidth / 2 + 5,
				100,
				150,
				20,
				new TranslatableText("selectWorld.mapType"),
				buttonWidget -> {
					while (this.field_25049.isPresent()) {
						int i = class_5317.field_25052.indexOf(this.field_25049.get()) + 1;
						if (i >= class_5317.field_25052.size()) {
							i = 0;
						}

						class_5317 lv = (class_5317)class_5317.field_25052.get(i);
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
		this.customizeTypeButton = parent.addButton(
			new ButtonWidget(parent.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
				class_5317.class_5293 lv = (class_5317.class_5293)class_5317.field_25053.get(this.field_25049);
				if (lv != null) {
					client.openScreen(lv.createEditScreen(parent, this.generatorOptions));
				}
			})
		);
		this.customizeTypeButton.visible = false;
		this.bonusItemsButton = parent.addButton(
			new ButtonWidget(parent.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
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
				this.parentWidth / 2 - 155,
				185,
				150,
				20,
				new TranslatableText("selectWorld.import_worldgen_settings"),
				buttonWidget -> {
					TranslatableText translatableText = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
					String string = TinyFileDialogs.tinyfd_openFileDialog(translatableText.getString(), null, null, null, false);
					if (string != null) {
						JsonParser jsonParser = new JsonParser();

						DataResult<GeneratorOptions> dataResult;
						try {
							BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string));
							Throwable string2 = null;

							try {
								JsonElement jsonElement = jsonParser.parse(bufferedReader);
								dataResult = GeneratorOptions.CODEC.parse(JsonOps.INSTANCE, jsonElement);
							} catch (Throwable var19) {
								string2 = var19;
								throw var19;
							} finally {
								if (bufferedReader != null) {
									if (string2 != null) {
										try {
											bufferedReader.close();
										} catch (Throwable var18) {
											string2.addSuppressed(var18);
										}
									} else {
										bufferedReader.close();
									}
								}
							}
						} catch (JsonIOException | JsonSyntaxException | IOException var21) {
							dataResult = DataResult.error("Failed to parse file: " + var21.getMessage());
						}

						if (dataResult.error().isPresent()) {
							Text text = new TranslatableText("selectWorld.import_worldgen_settings.failure");
							String string2 = ((PartialResult)dataResult.error().get()).message();
							field_25046.error("Error parsing world settings: {}", string2);
							Text text2 = new LiteralText(string2);
							client.getToastManager().add(SystemToast.method_29047(SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
						}

						Lifecycle lifecycle = dataResult.lifecycle();
						dataResult.resultOrPartial(field_25046::error)
							.ifPresent(
								generatorOptions -> {
									BooleanConsumer booleanConsumer = bl -> {
										client.openScreen(parent);
										if (bl) {
											this.method_29073(generatorOptions);
										}
									};
									if (lifecycle == Lifecycle.stable()) {
										this.method_29073(generatorOptions);
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

	private void method_29073(GeneratorOptions generatorOptions) {
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
}
