package net.minecraft.client.gui.screen.abusereport;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.RunningTaskScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.network.abusereport.AbuseReportReason;
import net.minecraft.client.network.abusereport.AbuseReporter;
import net.minecraft.client.network.abusereport.ChatAbuseReport;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TextifiedException;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatReportScreen extends Screen {
	private static final int BOTTOM_BUTTON_WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_TOP_MARGIN = 20;
	private static final int BUTTON_BOTTOM_MARGIN = 10;
	private static final int REASON_DESCRIPTION_TOP_MARGIN = 25;
	private static final int REASON_DESCRIPTION_TEXT_WIDTH = 280;
	private static final int SCREEN_HEIGHT = 300;
	private static final Text OBSERVED_WHAT_TEXT = Text.translatable("gui.chatReport.observed_what");
	private static final Text SELECT_REASON_TEXT = Text.translatable("gui.chatReport.select_reason");
	private static final Text MORE_COMMENTS_TEXT = Text.translatable("gui.chatReport.more_comments");
	private static final Text DESCRIBE_TEXT = Text.translatable("gui.chatReport.describe");
	private static final Text REPORT_SENT_MESSAGE_TEXT = Text.translatable("gui.chatReport.report_sent_msg");
	private static final Text SELECT_CHAT_TEXT = Text.translatable("gui.chatReport.select_chat");
	private static final Text SENDING_TEXT = Text.translatable("gui.abuseReport.sending.title");
	private static final Text GENERIC_ERROR_TEXT = Text.translatable("gui.abuseReport.send.generic_error");
	private static final Logger field_39577 = LogUtils.getLogger();
	@Nullable
	final Screen parent;
	private final AbuseReporter reporter;
	@Nullable
	private MultilineText reasonDescription;
	@Nullable
	private EditBoxWidget editBox;
	private ButtonWidget sendButton;
	private ChatAbuseReport report;
	@Nullable
	ChatAbuseReport.ValidationError validationError;

	public ChatReportScreen(Screen parent, AbuseReporter reporter, UUID reportedPlayerUuid) {
		super(Text.translatable("gui.chatReport.title"));
		this.parent = parent;
		this.reporter = reporter;
		this.report = new ChatAbuseReport(reportedPlayerUuid, reporter.sender().getLimits());
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		AbuseReportLimits abuseReportLimits = this.reporter.sender().getLimits();
		int i = this.width / 2;
		AbuseReportReason abuseReportReason = this.report.getReason();
		if (abuseReportReason != null) {
			this.reasonDescription = MultilineText.create(this.textRenderer, abuseReportReason.getDescription(), 280);
		} else {
			this.reasonDescription = null;
		}

		IntSet intSet = this.report.getSelections();
		Text text;
		if (intSet.isEmpty()) {
			text = SELECT_CHAT_TEXT;
		} else {
			text = Text.translatable("gui.chatReport.selected_chat", intSet.size());
		}

		this.addDrawableChild(
			new ButtonWidget(
				this.getWidgetsLeft(),
				this.getSelectionButtonY(),
				280,
				20,
				text,
				button -> this.client.setScreen(new ChatSelectionScreen(this, this.reporter, this.report, report -> {
						this.report = report;
						this.onChange();
					}))
			)
		);
		Text text2 = Util.mapOrElse(abuseReportReason, AbuseReportReason::getText, SELECT_REASON_TEXT);
		this.addDrawableChild(
			new ButtonWidget(
				this.getWidgetsLeft(),
				this.getReasonButtonY(),
				280,
				20,
				text2,
				button -> this.client.setScreen(new AbuseReportReasonScreen(this, this.report.getReason(), reason -> {
						this.report.setReason(reason);
						this.onChange();
					}))
			)
		);
		this.editBox = this.addDrawableChild(
			new EditBoxWidget(
				this.client.textRenderer,
				this.getWidgetsLeft(),
				this.getEditBoxTop(),
				280,
				this.getEditBoxBottom() - this.getEditBoxTop(),
				DESCRIBE_TEXT,
				Text.translatable("gui.chatReport.comments")
			)
		);
		this.editBox.setText(this.report.getOpinionComments());
		this.editBox.setMaxLength(abuseReportLimits.maxOpinionCommentsLength());
		this.editBox.setChangeListener(opinionComments -> {
			this.report.setOpinionComments(opinionComments);
			this.onChange();
		});
		this.addDrawableChild(new ButtonWidget(i - 120, this.getBottomButtonsY(), 120, 20, ScreenTexts.BACK, button -> this.close()));
		this.sendButton = this.addDrawableChild(
			new ButtonWidget(
				i + 10,
				this.getBottomButtonsY(),
				120,
				20,
				Text.translatable("gui.chatReport.send"),
				button -> this.send(),
				new ChatReportScreen.ValidationErrorTooltipSupplier()
			)
		);
		this.onChange();
	}

	private void onChange() {
		this.validationError = this.report.validate();
		this.sendButton.active = this.validationError == null;
	}

	private void send() {
		this.report.finalizeReport(this.reporter).left().ifPresent(report -> {
			CompletableFuture<?> completableFuture = this.reporter.sender().send(report.id(), report.report());
			RunningTaskScreen runningTaskScreen = new RunningTaskScreen(SENDING_TEXT, ScreenTexts.CANCEL, () -> {
				this.client.setScreen(this);
				completableFuture.cancel(true);
			});
			this.client.setScreen(runningTaskScreen);
			completableFuture.handleAsync((unit, throwable) -> {
				if (throwable == null) {
					this.onSubmissionFinished(runningTaskScreen);
				} else {
					if (throwable instanceof CancellationException) {
						return null;
					}

					this.onSubmissionError(runningTaskScreen, throwable);
				}

				return null;
			}, this.client);
		});
	}

	private void onSubmissionFinished(RunningTaskScreen sendingScreen) {
		sendingScreen.setDisplay(REPORT_SENT_MESSAGE_TEXT, ScreenTexts.DONE, () -> this.client.setScreen(null));
	}

	private void onSubmissionError(RunningTaskScreen sendingScreen, Throwable throwable) {
		field_39577.error("Encountered error while sending abuse report", throwable);
		Text text;
		if (throwable.getCause() instanceof TextifiedException textifiedException) {
			text = textifiedException.getMessageText();
		} else {
			text = GENERIC_ERROR_TEXT;
		}

		Text text2 = text.copy().formatted(Formatting.RED);
		sendingScreen.setDisplay(text2, ScreenTexts.BACK, () -> this.client.setScreen(this));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = this.width / 2;
		RenderSystem.disableDepthTest();
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, i, 10, 16777215);
		drawCenteredText(matrices, this.textRenderer, OBSERVED_WHAT_TEXT, i, this.getSelectionButtonY() - 9 - 6, 16777215);
		if (this.reasonDescription != null) {
			this.reasonDescription.drawWithShadow(matrices, this.getWidgetsLeft(), this.getReasonButtonY() + 20 + 5, 9, 16777215);
		}

		drawTextWithShadow(matrices, this.textRenderer, MORE_COMMENTS_TEXT, this.getWidgetsLeft(), this.getEditBoxTop() - 9 - 6, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void tick() {
		this.editBox.tick();
		super.tick();
	}

	@Override
	public void close() {
		if (!this.editBox.getText().isEmpty()) {
			this.client.setScreen(new ChatReportScreen.DiscardWarningScreen());
		} else {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button) ? true : this.editBox.mouseReleased(mouseX, mouseY, button);
	}

	private int getWidgetsLeft() {
		return this.width / 2 - 140;
	}

	private int getWidgetsRight() {
		return this.width / 2 + 140;
	}

	private int getTop() {
		return Math.max((this.height - 300) / 2, 0);
	}

	private int getBottom() {
		return Math.min((this.height + 300) / 2, this.height);
	}

	private int getSelectionButtonY() {
		return this.getTop() + 40;
	}

	private int getReasonButtonY() {
		return this.getSelectionButtonY() + 10 + 20;
	}

	private int getEditBoxTop() {
		int i = this.getReasonButtonY() + 20 + 25;
		if (this.reasonDescription != null) {
			i += (this.reasonDescription.count() + 1) * 9;
		}

		return i;
	}

	private int getEditBoxBottom() {
		return this.getBottomButtonsY() - 20;
	}

	private int getBottomButtonsY() {
		return this.getBottom() - 20 - 10;
	}

	@Environment(EnvType.CLIENT)
	class DiscardWarningScreen extends WarningScreen {
		protected DiscardWarningScreen() {
			super(
				Text.translatable("gui.chatReport.discard.title"), Text.translatable("gui.chatReport.discard.content"), Text.translatable("gui.chatReport.discard.content")
			);
		}

		@Override
		protected void initButtons(int yOffset) {
			this.addDrawableChild(
				new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, Text.translatable("gui.chatReport.discard.return"), button -> this.close())
			);
			this.addDrawableChild(
				new ButtonWidget(
					this.width / 2 + 5,
					100 + yOffset,
					150,
					20,
					Text.translatable("gui.chatReport.discard.discard"),
					button -> this.client.setScreen(ChatReportScreen.this.parent)
				)
			);
		}

		@Override
		public void close() {
			this.client.setScreen(ChatReportScreen.this);
		}

		@Override
		public boolean shouldCloseOnEsc() {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	class ValidationErrorTooltipSupplier implements ButtonWidget.TooltipSupplier {
		@Override
		public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
			if (ChatReportScreen.this.validationError != null) {
				Text text = ChatReportScreen.this.validationError.message();
				ChatReportScreen.this.renderOrderedTooltip(
					matrixStack, ChatReportScreen.this.textRenderer.wrapLines(text, Math.max(ChatReportScreen.this.width / 2 - 43, 170)), i, j
				);
			}
		}
	}
}
