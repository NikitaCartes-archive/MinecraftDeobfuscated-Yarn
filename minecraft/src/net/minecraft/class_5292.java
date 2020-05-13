package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_5292 implements TickableElement, Drawable {
	private static final Map<class_5285.class_5288, class_5292.class_5293> field_24590 = ImmutableMap.of(
		class_5285.class_5288.field_24553,
		(createWorldScreen, arg) -> new CustomizeFlatLevelScreen(
				createWorldScreen, flatChunkGeneratorConfig -> createWorldScreen.field_24588.method_28086(arg.method_28019(flatChunkGeneratorConfig)), arg.method_28040()
			),
		class_5285.class_5288.field_24555,
		(createWorldScreen, arg) -> new CustomizeBuffetLevelScreen(
				createWorldScreen,
				pair -> createWorldScreen.field_24588.method_28086(arg.method_28012((class_5285.class_5286)pair.getFirst(), (Set<Biome>)pair.getSecond())),
				arg.method_28041()
			)
	);
	private static final Text field_24591 = new TranslatableText("generator.amplified.info");
	private TextRenderer field_24592;
	private int field_24593;
	private TextFieldWidget field_24594;
	private ButtonWidget field_24595;
	public ButtonWidget field_24589;
	private ButtonWidget field_24596;
	private ButtonWidget field_24597;
	private class_5285 field_24598;
	private int field_24599;
	private String field_24600;

	public class_5292() {
		this(class_5285.method_28009(), "");
	}

	public class_5292(class_5285 arg) {
		this(arg, Long.toString(arg.method_28028()));
	}

	private class_5292(class_5285 arg, String string) {
		this.field_24598 = arg;
		this.field_24599 = class_5285.class_5288.field_24556.indexOf(arg.method_28039());
		this.field_24600 = string;
	}

	public void method_28092(CreateWorldScreen createWorldScreen, MinecraftClient minecraftClient, TextRenderer textRenderer) {
		this.field_24592 = textRenderer;
		this.field_24593 = createWorldScreen.width;
		this.field_24594 = new TextFieldWidget(this.field_24592, this.field_24593 / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
		this.field_24594.setText(this.field_24600);
		this.field_24594.setChangedListener(string -> this.field_24600 = this.field_24594.getText());
		createWorldScreen.addChild(this.field_24594);
		this.field_24595 = createWorldScreen.addButton(
			new ButtonWidget(this.field_24593 / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), buttonWidget -> {
				this.field_24598 = this.field_24598.method_28037();
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(class_5292.this.field_24598.method_28029()));
				}

				@Override
				protected MutableText getNarrationMessage() {
					return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
				}
			}
		);
		this.field_24595.visible = false;
		this.field_24596 = createWorldScreen.addButton(
			new ButtonWidget(this.field_24593 / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.mapType"), buttonWidget -> {
				do {
					this.field_24599++;
					if (this.field_24599 >= class_5285.class_5288.field_24556.size()) {
						this.field_24599 = 0;
					}

					this.field_24598 = this.field_24598.method_28015((class_5285.class_5288)class_5285.class_5288.field_24556.get(this.field_24599));
				} while (this.field_24598.method_28033() && !Screen.hasShiftDown());

				createWorldScreen.method_28084();
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage().shallowCopy().append(" ").append(class_5292.this.field_24598.method_28039().method_28049());
				}

				@Override
				protected MutableText getNarrationMessage() {
					return class_5292.this.field_24598.method_28039() == class_5285.class_5288.field_24554
						? super.getNarrationMessage().append(". ").append(class_5292.field_24591)
						: super.getNarrationMessage();
				}
			}
		);
		this.field_24596.visible = false;
		this.field_24597 = createWorldScreen.addButton(
			new ButtonWidget(createWorldScreen.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), buttonWidget -> {
				class_5292.class_5293 lv = (class_5292.class_5293)field_24590.get(this.field_24598.method_28039());
				if (lv != null) {
					minecraftClient.openScreen(lv.createEditScreen(createWorldScreen, this.field_24598));
				}
			})
		);
		this.field_24597.visible = false;
		this.field_24589 = createWorldScreen.addButton(
			new ButtonWidget(createWorldScreen.width / 2 + 5, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), buttonWidget -> {
				this.field_24598 = this.field_24598.method_28038();
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(" ")
						.append(ScreenTexts.getToggleText(class_5292.this.field_24598.method_28030() && !createWorldScreen.hardcore));
				}
			}
		);
		this.field_24589.visible = false;
	}

	@Override
	public void tick() {
		this.field_24594.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.field_24595.visible) {
			this.field_24592.drawWithShadow(matrices, I18n.translate("selectWorld.mapFeatures.info"), (float)(this.field_24593 / 2 - 150), 122.0F, -6250336);
		}

		this.field_24594.render(matrices, mouseX, mouseY, delta);
		if (this.field_24598.method_28039() == class_5285.class_5288.field_24554) {
			this.field_24592.drawTrimmed(field_24591, this.field_24596.x + 2, this.field_24596.y + 22, this.field_24596.getWidth(), 10526880);
		}
	}

	private void method_28086(class_5285 arg) {
		this.field_24598 = arg;
	}

	private static OptionalLong method_28095(String string) {
		try {
			return OptionalLong.of(Long.parseLong(string));
		} catch (NumberFormatException var2) {
			return OptionalLong.empty();
		}
	}

	public class_5285 method_28096(boolean bl) {
		String string = this.field_24594.getText();
		OptionalLong optionalLong;
		if (StringUtils.isEmpty(string)) {
			optionalLong = OptionalLong.empty();
		} else {
			OptionalLong optionalLong2 = method_28095(string);
			if (optionalLong2.isPresent() && optionalLong2.getAsLong() == 0L) {
				optionalLong = OptionalLong.empty();
			} else {
				optionalLong = optionalLong2;
			}
		}

		return this.field_24598.method_28024(bl, optionalLong);
	}

	public boolean method_28085() {
		return this.field_24598.method_28033();
	}

	public void method_28101(boolean bl) {
		this.field_24596.visible = bl;
		if (this.field_24598.method_28033()) {
			this.field_24595.visible = false;
			this.field_24589.visible = false;
			this.field_24597.visible = false;
		} else {
			this.field_24595.visible = bl;
			this.field_24589.visible = bl;
			this.field_24597.visible = bl && field_24590.containsKey(this.field_24598.method_28039());
		}

		this.field_24594.setVisible(bl);
	}

	@Environment(EnvType.CLIENT)
	public interface class_5293 {
		Screen createEditScreen(CreateWorldScreen createWorldScreen, class_5285 arg);
	}
}
