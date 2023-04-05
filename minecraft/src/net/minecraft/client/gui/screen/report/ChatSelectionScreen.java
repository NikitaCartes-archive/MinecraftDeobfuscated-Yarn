package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ChatAbuseReport;
import net.minecraft.client.report.MessagesListAdder;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChatSelectionScreen extends Screen {
	private static final Text TITLE = Text.translatable("gui.chatSelection.title");
	private static final Text CONTEXT_MESSAGE = Text.translatable("gui.chatSelection.context").formatted(Formatting.GRAY);
	@Nullable
	private final Screen parent;
	private final AbuseReportContext reporter;
	private ButtonWidget doneButton;
	private MultilineText contextMessage;
	@Nullable
	private ChatSelectionScreen.SelectionListWidget selectionList;
	final ChatAbuseReport report;
	private final Consumer<ChatAbuseReport> newReportConsumer;
	private MessagesListAdder listAdder;

	public ChatSelectionScreen(@Nullable Screen parent, AbuseReportContext reporter, ChatAbuseReport report, Consumer<ChatAbuseReport> newReportConsumer) {
		super(TITLE);
		this.parent = parent;
		this.reporter = reporter;
		this.report = report.copy();
		this.newReportConsumer = newReportConsumer;
	}

	@Override
	protected void init() {
		this.listAdder = new MessagesListAdder(this.reporter, this::isSentByReportedPlayer);
		this.contextMessage = MultilineText.create(this.textRenderer, CONTEXT_MESSAGE, this.width - 16);
		this.selectionList = new ChatSelectionScreen.SelectionListWidget(this.client, (this.contextMessage.count() + 1) * 9);
		this.selectionList.setRenderBackground(false);
		this.addSelectableChild(this.selectionList);
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 155, this.height - 32, 150, 20).build());
		this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			this.newReportConsumer.accept(this.report);
			this.close();
		}).dimensions(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
		this.setDoneButtonActivation();
		this.addMessages();
		this.selectionList.setScrollAmount((double)this.selectionList.getMaxScroll());
	}

	private boolean isSentByReportedPlayer(ReceivedMessage message) {
		return message.isSentFrom(this.report.getReportedPlayerUuid());
	}

	private void addMessages() {
		int i = this.selectionList.getDisplayedItemCount();
		this.listAdder.add(i, this.selectionList);
	}

	void addMoreMessages() {
		this.addMessages();
	}

	void setDoneButtonActivation() {
		this.doneButton.active = !this.report.getSelections().isEmpty();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.selectionList.render(matrices, mouseX, mouseY, delta);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
		AbuseReportLimits abuseReportLimits = this.reporter.getSender().getLimits();
		int i = this.report.getSelections().size();
		int j = abuseReportLimits.maxReportedMessageCount();
		Text text = Text.translatable("gui.chatSelection.selected", i, j);
		drawCenteredTextWithShadow(matrices, this.textRenderer, text, this.width / 2, 16 + 9 * 3 / 2, 10526880);
		this.contextMessage.drawCenterWithShadow(matrices, this.width / 2, this.selectionList.getContextMessageY());
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), CONTEXT_MESSAGE);
	}

	@Environment(EnvType.CLIENT)
	public class SelectionListWidget
		extends AlwaysSelectedEntryListWidget<ChatSelectionScreen.SelectionListWidget.Entry>
		implements MessagesListAdder.MessagesList {
		@Nullable
		private ChatSelectionScreen.SelectionListWidget.SenderEntryPair lastSenderEntryPair;

		public SelectionListWidget(MinecraftClient client, int contextMessagesHeight) {
			super(client, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height, 40, ChatSelectionScreen.this.height - 40 - contextMessagesHeight, 16);
		}

		@Override
		public void setScrollAmount(double amount) {
			double d = this.getScrollAmount();
			super.setScrollAmount(amount);
			if ((float)this.getMaxScroll() > 1.0E-5F && amount <= 1.0E-5F && !MathHelper.approximatelyEquals(amount, d)) {
				ChatSelectionScreen.this.addMoreMessages();
			}
		}

		@Override
		public void addMessage(int index, ReceivedMessage.ChatMessage message) {
			boolean bl = message.isSentFrom(ChatSelectionScreen.this.report.getReportedPlayerUuid());
			MessageTrustStatus messageTrustStatus = message.trustStatus();
			MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message.message());
			ChatSelectionScreen.SelectionListWidget.Entry entry = new ChatSelectionScreen.SelectionListWidget.MessageEntry(
				index, message.getContent(), message.getNarration(), messageIndicator, bl, true
			);
			this.addEntryToTop(entry);
			this.addSenderEntry(message, bl);
		}

		private void addSenderEntry(ReceivedMessage.ChatMessage message, boolean fromReportedPlayer) {
			ChatSelectionScreen.SelectionListWidget.Entry entry = new ChatSelectionScreen.SelectionListWidget.SenderEntry(
				message.profile(), message.getHeadingText(), fromReportedPlayer
			);
			this.addEntryToTop(entry);
			ChatSelectionScreen.SelectionListWidget.SenderEntryPair senderEntryPair = new ChatSelectionScreen.SelectionListWidget.SenderEntryPair(
				message.getSenderUuid(), entry
			);
			if (this.lastSenderEntryPair != null && this.lastSenderEntryPair.senderEquals(senderEntryPair)) {
				this.removeEntryWithoutScrolling(this.lastSenderEntryPair.entry());
			}

			this.lastSenderEntryPair = senderEntryPair;
		}

		@Override
		public void addText(Text text) {
			this.addEntryToTop(new ChatSelectionScreen.SelectionListWidget.SeparatorEntry());
			this.addEntryToTop(new ChatSelectionScreen.SelectionListWidget.TextEntry(text));
			this.addEntryToTop(new ChatSelectionScreen.SelectionListWidget.SeparatorEntry());
			this.lastSenderEntryPair = null;
		}

		@Override
		protected int getScrollbarPositionX() {
			return (this.width + this.getRowWidth()) / 2;
		}

		@Override
		public int getRowWidth() {
			return Math.min(350, this.width - 50);
		}

		public int getDisplayedItemCount() {
			return MathHelper.ceilDiv(this.bottom - this.top, this.itemHeight);
		}

		@Override
		protected void renderEntry(MatrixStack matrices, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
			ChatSelectionScreen.SelectionListWidget.Entry entry = this.getEntry(index);
			if (this.shouldHighlight(entry)) {
				boolean bl = this.getSelectedOrNull() == entry;
				int i = this.isFocused() && bl ? -1 : -8355712;
				this.drawSelectionHighlight(matrices, y, entryWidth, entryHeight, i, -16777216);
			}

			entry.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, this.getHoveredEntry() == entry, delta);
		}

		private boolean shouldHighlight(ChatSelectionScreen.SelectionListWidget.Entry entry) {
			if (entry.canSelect()) {
				boolean bl = this.getSelectedOrNull() == entry;
				boolean bl2 = this.getSelectedOrNull() == null;
				boolean bl3 = this.getHoveredEntry() == entry;
				return bl || bl2 && bl3 && entry.isHighlightedOnHover();
			} else {
				return false;
			}
		}

		@Nullable
		protected ChatSelectionScreen.SelectionListWidget.Entry getNeighboringEntry(NavigationDirection navigationDirection) {
			return this.getNeighboringEntry(navigationDirection, ChatSelectionScreen.SelectionListWidget.Entry::canSelect);
		}

		public void setSelected(@Nullable ChatSelectionScreen.SelectionListWidget.Entry entry) {
			super.setSelected(entry);
			ChatSelectionScreen.SelectionListWidget.Entry entry2 = this.getNeighboringEntry(NavigationDirection.UP);
			if (entry2 == null) {
				ChatSelectionScreen.this.addMoreMessages();
			}
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			ChatSelectionScreen.SelectionListWidget.Entry entry = this.getSelectedOrNull();
			return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		}

		public int getContextMessageY() {
			return this.bottom + 9;
		}

		@Environment(EnvType.CLIENT)
		public abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<ChatSelectionScreen.SelectionListWidget.Entry> {
			@Override
			public Text getNarration() {
				return ScreenTexts.EMPTY;
			}

			public boolean isSelected() {
				return false;
			}

			public boolean canSelect() {
				return false;
			}

			public boolean isHighlightedOnHover() {
				return this.canSelect();
			}
		}

		@Environment(EnvType.CLIENT)
		public class MessageEntry extends ChatSelectionScreen.SelectionListWidget.Entry {
			private static final Identifier CHECKMARK = new Identifier("minecraft", "textures/gui/checkmark.png");
			private static final int CHECKMARK_WIDTH = 9;
			private static final int CHECKMARK_HEIGHT = 8;
			private static final int CHAT_MESSAGE_LEFT_MARGIN = 11;
			private static final int INDICATOR_LEFT_MARGIN = 4;
			private final int index;
			private final StringVisitable truncatedContent;
			private final Text narration;
			@Nullable
			private final List<OrderedText> fullContent;
			@Nullable
			private final MessageIndicator.Icon indicatorIcon;
			@Nullable
			private final List<OrderedText> originalContent;
			private final boolean fromReportedPlayer;
			private final boolean isChatMessage;

			public MessageEntry(int index, Text message, Text narration, @Nullable MessageIndicator indicator, boolean fromReportedPlayer, boolean isChatMessage) {
				this.index = index;
				this.indicatorIcon = Nullables.map(indicator, MessageIndicator::icon);
				this.originalContent = indicator != null && indicator.text() != null
					? ChatSelectionScreen.this.textRenderer.wrapLines(indicator.text(), SelectionListWidget.this.getRowWidth())
					: null;
				this.fromReportedPlayer = fromReportedPlayer;
				this.isChatMessage = isChatMessage;
				StringVisitable stringVisitable = ChatSelectionScreen.this.textRenderer
					.trimToWidth(message, this.getTextWidth() - ChatSelectionScreen.this.textRenderer.getWidth(ScreenTexts.ELLIPSIS));
				if (message != stringVisitable) {
					this.truncatedContent = StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS);
					this.fullContent = ChatSelectionScreen.this.textRenderer.wrapLines(message, SelectionListWidget.this.getRowWidth());
				} else {
					this.truncatedContent = message;
					this.fullContent = null;
				}

				this.narration = narration;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				if (this.isSelected() && this.fromReportedPlayer) {
					this.drawCheckmark(matrices, y, x, entryHeight);
				}

				int i = x + this.getIndent();
				int j = y + 1 + (entryHeight - 9) / 2;
				DrawableHelper.drawTextWithShadow(
					matrices, ChatSelectionScreen.this.textRenderer, Language.getInstance().reorder(this.truncatedContent), i, j, this.fromReportedPlayer ? -1 : -1593835521
				);
				if (this.fullContent != null && hovered) {
					ChatSelectionScreen.this.setTooltip(this.fullContent);
				}

				int k = ChatSelectionScreen.this.textRenderer.getWidth(this.truncatedContent);
				this.renderIndicator(matrices, i + k + 4, y, entryHeight, mouseX, mouseY);
			}

			private void renderIndicator(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY) {
				if (this.indicatorIcon != null) {
					int i = y + (entryHeight - this.indicatorIcon.height) / 2;
					this.indicatorIcon.draw(matrices, x, i);
					if (this.originalContent != null && mouseX >= x && mouseX <= x + this.indicatorIcon.width && mouseY >= i && mouseY <= i + this.indicatorIcon.height) {
						ChatSelectionScreen.this.setTooltip(this.originalContent);
					}
				}
			}

			private void drawCheckmark(MatrixStack matrices, int y, int x, int entryHeight) {
				int j = y + (entryHeight - 8) / 2;
				RenderSystem.setShaderTexture(0, CHECKMARK);
				RenderSystem.enableBlend();
				DrawableHelper.drawTexture(matrices, x, j, 0.0F, 0.0F, 9, 8, 9, 8);
				RenderSystem.disableBlend();
			}

			private int getTextWidth() {
				int i = this.indicatorIcon != null ? this.indicatorIcon.width + 4 : 0;
				return SelectionListWidget.this.getRowWidth() - this.getIndent() - 4 - i;
			}

			private int getIndent() {
				return this.isChatMessage ? 11 : 0;
			}

			@Override
			public Text getNarration() {
				return (Text)(this.isSelected() ? Text.translatable("narrator.select", this.narration) : this.narration);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					SelectionListWidget.this.setSelected(null);
					return this.toggle();
				} else {
					return false;
				}
			}

			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				return KeyCodes.isToggle(keyCode) ? this.toggle() : false;
			}

			@Override
			public boolean isSelected() {
				return ChatSelectionScreen.this.report.hasSelectedMessage(this.index);
			}

			@Override
			public boolean canSelect() {
				return true;
			}

			@Override
			public boolean isHighlightedOnHover() {
				return this.fromReportedPlayer;
			}

			private boolean toggle() {
				if (this.fromReportedPlayer) {
					ChatSelectionScreen.this.report.toggleMessageSelection(this.index);
					ChatSelectionScreen.this.setDoneButtonActivation();
					return true;
				} else {
					return false;
				}
			}
		}

		@Environment(EnvType.CLIENT)
		public class SenderEntry extends ChatSelectionScreen.SelectionListWidget.Entry {
			private static final int PLAYER_SKIN_SIZE = 12;
			private final Text headingText;
			private final Identifier skinTextureId;
			private final boolean fromReportedPlayer;

			public SenderEntry(GameProfile gameProfile, Text headingText, boolean fromReportedPlayer) {
				this.headingText = headingText;
				this.fromReportedPlayer = fromReportedPlayer;
				this.skinTextureId = SelectionListWidget.this.client.getSkinProvider().loadSkin(gameProfile);
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = x - 12 - 4;
				int j = y + (entryHeight - 12) / 2;
				this.drawSkin(matrices, i, j, this.skinTextureId);
				int k = y + 1 + (entryHeight - 9) / 2;
				DrawableHelper.drawTextWithShadow(matrices, ChatSelectionScreen.this.textRenderer, this.headingText, x, k, this.fromReportedPlayer ? -1 : -1593835521);
			}

			private void drawSkin(MatrixStack matrices, int x, int y, Identifier skinTextureId) {
				RenderSystem.setShaderTexture(0, skinTextureId);
				PlayerSkinDrawer.draw(matrices, x, y, 12);
			}
		}

		@Environment(EnvType.CLIENT)
		static record SenderEntryPair(UUID sender, ChatSelectionScreen.SelectionListWidget.Entry entry) {
			public boolean senderEquals(ChatSelectionScreen.SelectionListWidget.SenderEntryPair pair) {
				return pair.sender.equals(this.sender);
			}
		}

		@Environment(EnvType.CLIENT)
		public class SeparatorEntry extends ChatSelectionScreen.SelectionListWidget.Entry {
			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			}
		}

		@Environment(EnvType.CLIENT)
		public class TextEntry extends ChatSelectionScreen.SelectionListWidget.Entry {
			private static final int TEXT_COLOR = -6250336;
			private final Text text;

			public TextEntry(Text text) {
				this.text = text;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = y + entryHeight / 2;
				int j = x + entryWidth - 8;
				int k = ChatSelectionScreen.this.textRenderer.getWidth(this.text);
				int l = (x + j - k) / 2;
				int m = i - 9 / 2;
				DrawableHelper.drawTextWithShadow(matrices, ChatSelectionScreen.this.textRenderer, this.text, l, m, -6250336);
			}

			@Override
			public Text getNarration() {
				return this.text;
			}
		}
	}
}
