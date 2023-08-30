package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AbuseReportTypeScreen extends Screen {
	private static final Text TITLE_TEXT = Text.translatable("gui.abuseReport.title");
	private static final Text MESSAGE_TEXT = Text.translatable("gui.abuseReport.message");
	private static final Text CHAT_TYPE_TEXT = Text.translatable("gui.abuseReport.type.chat");
	private static final Text SKIN_TYPE_TEXT = Text.translatable("gui.abuseReport.type.skin");
	private static final Text NAME_TYPE_TEXT = Text.translatable("gui.abuseReport.type.name");
	private static final int field_46046 = 6;
	private final Screen parent;
	private final AbuseReportContext context;
	private final SocialInteractionsPlayerListEntry selectedPlayer;
	private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(6);

	public AbuseReportTypeScreen(Screen parent, AbuseReportContext context, SocialInteractionsPlayerListEntry selectedPlayer) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.context = context;
		this.selectedPlayer = selectedPlayer;
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), MESSAGE_TEXT);
	}

	@Override
	protected void init() {
		this.layout.getMainPositioner().alignHorizontalCenter();
		this.layout.add(new TextWidget(this.title, this.textRenderer), this.layout.copyPositioner().marginBottom(6));
		this.layout.add(new MultilineTextWidget(MESSAGE_TEXT, this.textRenderer).setCentered(true), this.layout.copyPositioner().marginBottom(6));
		ButtonWidget buttonWidget = this.layout
			.add(
				ButtonWidget.builder(CHAT_TYPE_TEXT, button -> this.client.setScreen(new ChatReportScreen(this.parent, this.context, this.selectedPlayer.getUuid())))
					.build()
			);
		if (!this.selectedPlayer.isReportable()) {
			buttonWidget.active = false;
			buttonWidget.setTooltip(Tooltip.of(Text.translatable("gui.socialInteractions.tooltip.report.not_reportable")));
		} else if (!this.selectedPlayer.hasSentMessage()) {
			buttonWidget.active = false;
			buttonWidget.setTooltip(Tooltip.of(Text.translatable("gui.socialInteractions.tooltip.report.no_messages", this.selectedPlayer.getName())));
		}

		this.layout
			.add(
				ButtonWidget.builder(
						SKIN_TYPE_TEXT,
						button -> this.client.setScreen(new SkinReportScreen(this.parent, this.context, this.selectedPlayer.getUuid(), this.selectedPlayer.getSkinSupplier()))
					)
					.build()
			);
		this.layout
			.add(
				ButtonWidget.builder(
						NAME_TYPE_TEXT,
						button -> this.client.setScreen(new UsernameReportScreen(this.parent, this.context, this.selectedPlayer.getUuid(), this.selectedPlayer.getName()))
					)
					.build()
			);
		this.layout.add(EmptyWidget.ofHeight(20));
		this.layout.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
