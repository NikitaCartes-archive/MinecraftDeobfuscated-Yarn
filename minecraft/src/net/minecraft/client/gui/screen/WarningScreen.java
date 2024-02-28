package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class WarningScreen extends Screen {
	private static final int field_49538 = 100;
	private final Text messageText;
	@Nullable
	private final Text checkMessage;
	private final Text narratedText;
	@Nullable
	protected CheckboxWidget checkbox;
	@Nullable
	private NarratedMultilineTextWidget textWidget;
	private final SimplePositioningWidget positioningWidget;

	protected WarningScreen(Text header, Text message, Text narratedText) {
		this(header, message, null, narratedText);
	}

	protected WarningScreen(Text header, Text messageText, @Nullable Text checkMessage, Text narratedText) {
		super(header);
		this.messageText = messageText;
		this.checkMessage = checkMessage;
		this.narratedText = narratedText;
		this.positioningWidget = new SimplePositioningWidget(0, 0, this.width, this.height);
	}

	protected abstract LayoutWidget getLayout();

	@Override
	protected void init() {
		DirectionalLayoutWidget directionalLayoutWidget = this.positioningWidget.add(DirectionalLayoutWidget.vertical().spacing(8));
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
		directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
		this.textWidget = directionalLayoutWidget.add(
			new NarratedMultilineTextWidget(this.width - 100, this.messageText, this.textRenderer, 12), positioner -> positioner.margin(12)
		);
		this.textWidget.setCentered(false);
		DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.vertical().spacing(8));
		directionalLayoutWidget2.getMainPositioner().alignHorizontalCenter();
		if (this.checkMessage != null) {
			this.checkbox = directionalLayoutWidget2.add(CheckboxWidget.builder(this.checkMessage, this.textRenderer).build());
		}

		directionalLayoutWidget2.add(this.getLayout());
		this.positioningWidget.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		if (this.textWidget != null) {
			this.textWidget.setMaxWidth(this.width - 100);
		}

		this.positioningWidget.refreshPositions();
		SimplePositioningWidget.setPos(this.positioningWidget, this.getNavigationFocus());
	}

	@Override
	public Text getNarratedTitle() {
		return this.narratedText;
	}
}
