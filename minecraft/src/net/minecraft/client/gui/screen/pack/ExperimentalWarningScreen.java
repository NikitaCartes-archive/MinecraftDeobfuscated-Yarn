package net.minecraft.client.gui.screen.pack;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(EnvType.CLIENT)
public class ExperimentalWarningScreen extends Screen {
	private static final Text TITLE = Text.translatable("selectWorld.experimental.title");
	private static final Text MESSAGE = Text.translatable("selectWorld.experimental.message");
	private static final Text DETAILS = Text.translatable("selectWorld.experimental.details");
	private static final int field_42498 = 10;
	private static final int field_42499 = 100;
	private final BooleanConsumer callback;
	final Collection<ResourcePackProfile> enabledProfiles;
	private final GridWidget grid = new GridWidget().setColumnSpacing(10).setRowSpacing(20);

	public ExperimentalWarningScreen(Collection<ResourcePackProfile> enabledProfiles, BooleanConsumer callback) {
		super(TITLE);
		this.enabledProfiles = enabledProfiles;
		this.callback = callback;
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), MESSAGE);
	}

	@Override
	protected void init() {
		super.init();
		GridWidget.Adder adder = this.grid.createAdder(2);
		Positioner positioner = adder.copyPositioner().alignHorizontalCenter();
		adder.add(new TextWidget(this.title, this.textRenderer), 2, positioner);
		MultilineTextWidget multilineTextWidget = adder.add(new MultilineTextWidget(MESSAGE, this.textRenderer).setCentered(true), 2, positioner);
		multilineTextWidget.setMaxWidth(310);
		adder.add(ButtonWidget.builder(DETAILS, button -> this.client.setScreen(new ExperimentalWarningScreen.DetailsScreen())).width(100).build(), 2, positioner);
		adder.add(ButtonWidget.builder(ScreenTexts.PROCEED, button -> this.callback.accept(true)).build());
		adder.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.callback.accept(false)).build());
		this.grid.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.grid.refreshPositions();
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		SimplePositioningWidget.setPos(this.grid, 0, 0, this.width, this.height, 0.5F, 0.5F);
	}

	@Override
	public void close() {
		this.callback.accept(false);
	}

	@Environment(EnvType.CLIENT)
	class DetailsScreen extends Screen {
		private ExperimentalWarningScreen.DetailsScreen.PackListWidget packList;

		DetailsScreen() {
			super(Text.translatable("selectWorld.experimental.details.title"));
		}

		@Override
		public void close() {
			this.client.setScreen(ExperimentalWarningScreen.this);
		}

		@Override
		protected void init() {
			super.init();
			this.addDrawableChild(
				ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build()
			);
			this.packList = new ExperimentalWarningScreen.DetailsScreen.PackListWidget(this.client, ExperimentalWarningScreen.this.enabledProfiles);
			this.addSelectableChild(this.packList);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			super.render(context, mouseX, mouseY, delta);
			this.packList.render(context, mouseX, mouseY, delta);
			context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 16777215);
		}

		@Environment(EnvType.CLIENT)
		class PackListWidget extends AlwaysSelectedEntryListWidget<ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry> {
			public PackListWidget(MinecraftClient client, Collection<ResourcePackProfile> enabledProfiles) {
				super(client, DetailsScreen.this.width, DetailsScreen.this.height, 32, DetailsScreen.this.height - 64, (9 + 2) * 3);

				for (ResourcePackProfile resourcePackProfile : enabledProfiles) {
					String string = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_FEATURES, resourcePackProfile.getRequestedFeatures());
					if (!string.isEmpty()) {
						Text text = Texts.setStyleIfAbsent(resourcePackProfile.getDisplayName().copy(), Style.EMPTY.withBold(true));
						Text text2 = Text.translatable("selectWorld.experimental.details.entry", string);
						this.addEntry(DetailsScreen.this.new PackListWidgetEntry(text, text2, MultilineText.create(DetailsScreen.this.textRenderer, text2, this.getRowWidth())));
					}
				}
			}

			@Override
			public int getRowWidth() {
				return this.width * 3 / 4;
			}
		}

		@Environment(EnvType.CLIENT)
		class PackListWidgetEntry extends AlwaysSelectedEntryListWidget.Entry<ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry> {
			private final Text displayName;
			private final Text details;
			private final MultilineText multilineDetails;

			PackListWidgetEntry(Text displayName, Text details, MultilineText multilineDetails) {
				this.displayName = displayName;
				this.details = details;
				this.multilineDetails = multilineDetails;
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				context.drawTextWithShadow(DetailsScreen.this.client.textRenderer, this.displayName, x, y, 16777215);
				this.multilineDetails.drawWithShadow(context, x, y + 12, 9, 16777215);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.displayName, this.details));
			}
		}
	}
}
