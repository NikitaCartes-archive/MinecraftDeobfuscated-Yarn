package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TaskScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TextifiedException;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ReportScreen<B extends AbuseReport.Builder<?>> extends Screen {
	private static final Text REPORT_SENT_MESSAGE_TEXT = Text.translatable("gui.abuseReport.report_sent_msg");
	private static final Text SENDING_TITLE_TEXT = Text.translatable("gui.abuseReport.sending.title").formatted(Formatting.BOLD);
	private static final Text SENT_TITLE_TEXT = Text.translatable("gui.abuseReport.sent.title").formatted(Formatting.BOLD);
	private static final Text ERROR_TITLE_TEXT = Text.translatable("gui.abuseReport.error.title").formatted(Formatting.BOLD);
	private static final Text GENERIC_ERROR_TEXT = Text.translatable("gui.abuseReport.send.generic_error");
	protected static final Text SEND_TEXT = Text.translatable("gui.abuseReport.send");
	protected static final Text OBSERVED_WHAT_TEXT = Text.translatable("gui.abuseReport.observed_what");
	protected static final Text SELECT_REASON_TEXT = Text.translatable("gui.abuseReport.select_reason");
	private static final Text DESCRIBE_TEXT = Text.translatable("gui.abuseReport.describe");
	protected static final Text MORE_COMMENTS_TEXT = Text.translatable("gui.abuseReport.more_comments");
	private static final Text COMMENTS_TEXT = Text.translatable("gui.abuseReport.comments");
	protected static final int field_46016 = 20;
	protected static final int field_46017 = 280;
	protected static final int field_46018 = 8;
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final Screen parent;
	protected final AbuseReportContext context;
	protected B reportBuilder;

	protected ReportScreen(Text title, Screen parent, AbuseReportContext context, B reportBuilder) {
		super(title);
		this.parent = parent;
		this.context = context;
		this.reportBuilder = reportBuilder;
	}

	protected EditBoxWidget createCommentsBox(int width, int height, Consumer<String> changeListener) {
		AbuseReportLimits abuseReportLimits = this.context.getSender().getLimits();
		EditBoxWidget editBoxWidget = new EditBoxWidget(this.textRenderer, 0, 0, width, height, DESCRIBE_TEXT, COMMENTS_TEXT);
		editBoxWidget.setText(this.reportBuilder.getOpinionComments());
		editBoxWidget.setMaxLength(abuseReportLimits.maxOpinionCommentsLength());
		editBoxWidget.setChangeListener(changeListener);
		return editBoxWidget;
	}

	protected void trySend() {
		this.reportBuilder.build(this.context).ifLeft(reportWithId -> {
			CompletableFuture<?> completableFuture = this.context.getSender().send(reportWithId.id(), reportWithId.reportType(), reportWithId.report());
			this.client.setScreen(TaskScreen.createRunningScreen(SENDING_TITLE_TEXT, ScreenTexts.CANCEL, () -> {
				this.client.setScreen(this);
				completableFuture.cancel(true);
			}));
			completableFuture.handleAsync((v, throwable) -> {
				if (throwable == null) {
					this.onSent();
				} else {
					if (throwable instanceof CancellationException) {
						return null;
					}

					this.onSendError(throwable);
				}

				return null;
			}, this.client);
		}).ifRight(validationError -> this.showError(validationError.message()));
	}

	private void onSent() {
		this.resetDraft();
		this.client.setScreen(TaskScreen.createResultScreen(SENT_TITLE_TEXT, REPORT_SENT_MESSAGE_TEXT, ScreenTexts.DONE, () -> this.client.setScreen(null)));
	}

	private void onSendError(Throwable error) {
		LOGGER.error("Encountered error while sending abuse report", error);
		Text text;
		if (error.getCause() instanceof TextifiedException textifiedException) {
			text = textifiedException.getMessageText();
		} else {
			text = GENERIC_ERROR_TEXT;
		}

		this.showError(text);
	}

	private void showError(Text errorMessage) {
		Text text = errorMessage.copy().formatted(Formatting.RED);
		this.client.setScreen(TaskScreen.createResultScreen(ERROR_TITLE_TEXT, text, ScreenTexts.BACK, () -> this.client.setScreen(this)));
	}

	void saveDraft() {
		if (this.reportBuilder.hasEnoughInfo()) {
			this.context.setDraft(this.reportBuilder.getReport().copy());
		}
	}

	void resetDraft() {
		this.context.setDraft(null);
	}

	@Override
	public void close() {
		if (this.reportBuilder.hasEnoughInfo()) {
			this.client.setScreen(new ReportScreen.DiscardWarningScreen());
		} else {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void removed() {
		this.saveDraft();
		super.removed();
	}

	@Environment(EnvType.CLIENT)
	class DiscardWarningScreen extends WarningScreen {
		private static final int field_46030 = 20;
		private static final Text TITLE = Text.translatable("gui.abuseReport.discard.title").formatted(Formatting.BOLD);
		private static final Text MESSAGE = Text.translatable("gui.abuseReport.discard.content");
		private static final Text RETURN_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.return");
		private static final Text DRAFT_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.draft");
		private static final Text DISCARD_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.discard");

		protected DiscardWarningScreen() {
			super(TITLE, MESSAGE, MESSAGE);
		}

		@Override
		protected void initButtons(int yOffset) {
			this.addDrawableChild(ButtonWidget.builder(RETURN_BUTTON_TEXT, button -> this.close()).position(this.width / 2 - 155, 100 + yOffset).build());
			this.addDrawableChild(ButtonWidget.builder(DRAFT_BUTTON_TEXT, button -> {
				ReportScreen.this.saveDraft();
				this.client.setScreen(ReportScreen.this.parent);
			}).position(this.width / 2 + 5, 100 + yOffset).build());
			this.addDrawableChild(ButtonWidget.builder(DISCARD_BUTTON_TEXT, button -> {
				ReportScreen.this.resetDraft();
				this.client.setScreen(ReportScreen.this.parent);
			}).position(this.width / 2 - 75, 130 + yOffset).build());
		}

		@Override
		public void close() {
			this.client.setScreen(ReportScreen.this);
		}

		@Override
		public boolean shouldCloseOnEsc() {
			return false;
		}

		@Override
		protected void drawTitle(DrawContext context) {
			context.drawTextWithShadow(this.textRenderer, this.title, this.width / 2 - 155, 30, -1);
		}
	}
}
