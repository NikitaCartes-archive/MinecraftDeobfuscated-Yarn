package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class TelemetryEventWidget extends ScrollableWidget {
	private static final int MARGIN_X = 32;
	private static final String REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
	private static final String OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
	private static final String DISABLED_TRANSLATION_KEY = "telemetry.event.optional.disabled";
	private static final Text PROPERTY_TITLE_TEXT = Text.translatable("telemetry_info.property_title").formatted(Formatting.UNDERLINE);
	private final TextRenderer textRenderer;
	private TelemetryEventWidget.Contents contents;
	@Nullable
	private DoubleConsumer scrollConsumer;

	public TelemetryEventWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
		super(x, y, width, height, Text.empty());
		this.textRenderer = textRenderer;
		this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
	}

	public void refresh(boolean optionalTelemetryEnabled) {
		this.contents = this.collectContents(optionalTelemetryEnabled);
		this.setScrollY(this.getScrollY());
	}

	public void initContents() {
		this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
		this.setScrollY(this.getScrollY());
	}

	private TelemetryEventWidget.Contents collectContents(boolean optionalTelemetryEnabled) {
		TelemetryEventWidget.ContentsBuilder contentsBuilder = new TelemetryEventWidget.ContentsBuilder(this.getGridWidth());
		List<TelemetryEventType> list = new ArrayList(TelemetryEventType.getTypes());
		list.sort(Comparator.comparing(TelemetryEventType::isOptional));

		for (int i = 0; i < list.size(); i++) {
			TelemetryEventType telemetryEventType = (TelemetryEventType)list.get(i);
			boolean bl = telemetryEventType.isOptional() && !optionalTelemetryEnabled;
			this.appendEventInfo(contentsBuilder, telemetryEventType, bl);
			if (i < list.size() - 1) {
				contentsBuilder.appendSpace(9);
			}
		}

		return contentsBuilder.build();
	}

	public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
		this.scrollConsumer = scrollConsumer;
	}

	@Override
	protected void setScrollY(double scrollY) {
		super.setScrollY(scrollY);
		if (this.scrollConsumer != null) {
			this.scrollConsumer.accept(this.getScrollY());
		}
	}

	@Override
	protected int getContentsHeight() {
		return this.contents.grid().getHeight();
	}

	@Override
	protected double getDeltaYPerScroll() {
		return 9.0;
	}

	@Override
	protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.getY() + this.getPadding();
		int j = this.getX() + this.getPadding();
		context.getMatrices().push();
		context.getMatrices().translate((double)j, (double)i, 0.0);
		this.contents.grid().forEachChild(widget -> widget.render(context, mouseX, mouseY, delta));
		context.getMatrices().pop();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.contents.narration());
	}

	private Text formatTitleText(Text title, boolean disabled) {
		return (Text)(disabled ? title.copy().formatted(Formatting.GRAY) : title);
	}

	private void appendEventInfo(TelemetryEventWidget.ContentsBuilder builder, TelemetryEventType eventType, boolean disabled) {
		String string = eventType.isOptional() ? (disabled ? "telemetry.event.optional.disabled" : "telemetry.event.optional") : "telemetry.event.required";
		builder.appendText(this.textRenderer, this.formatTitleText(Text.translatable(string, eventType.getTitle()), disabled));
		builder.appendText(this.textRenderer, eventType.getDescription().formatted(Formatting.GRAY));
		builder.appendSpace(9 / 2);
		builder.appendTitle(this.textRenderer, this.formatTitleText(PROPERTY_TITLE_TEXT, disabled), 2);
		this.appendProperties(eventType, builder, disabled);
	}

	private void appendProperties(TelemetryEventType eventType, TelemetryEventWidget.ContentsBuilder builder, boolean disabled) {
		for (TelemetryEventProperty<?> telemetryEventProperty : eventType.getProperties()) {
			builder.appendTitle(this.textRenderer, this.formatTitleText(telemetryEventProperty.getTitle(), disabled));
		}
	}

	private int getGridWidth() {
		return this.width - this.getPaddingDoubled();
	}

	@Environment(EnvType.CLIENT)
	static record Contents(LayoutWidget grid, Text narration) {
	}

	@Environment(EnvType.CLIENT)
	static class ContentsBuilder {
		private final int gridWidth;
		private final DirectionalLayoutWidget layout;
		private final MutableText narration = Text.empty();

		public ContentsBuilder(int gridWidth) {
			this.gridWidth = gridWidth;
			this.layout = DirectionalLayoutWidget.vertical();
			this.layout.getMainPositioner().alignLeft();
			this.layout.add(EmptyWidget.ofWidth(gridWidth));
		}

		public void appendTitle(TextRenderer textRenderer, Text title) {
			this.appendTitle(textRenderer, title, 0);
		}

		public void appendTitle(TextRenderer textRenderer, Text title, int marginBottom) {
			this.layout.add(new MultilineTextWidget(title, textRenderer).setMaxWidth(this.gridWidth), positioner -> positioner.marginBottom(marginBottom));
			this.narration.append(title).append("\n");
		}

		public void appendText(TextRenderer textRenderer, Text text) {
			this.layout
				.add(
					new MultilineTextWidget(text, textRenderer).setMaxWidth(this.gridWidth - 64).setCentered(true),
					positioner -> positioner.alignHorizontalCenter().marginX(32)
				);
			this.narration.append(text).append("\n");
		}

		public void appendSpace(int height) {
			this.layout.add(EmptyWidget.ofHeight(height));
		}

		public TelemetryEventWidget.Contents build() {
			this.layout.refreshPositions();
			return new TelemetryEventWidget.Contents(this.layout, this.narration);
		}
	}
}
