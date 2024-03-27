package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;

public class SignText {
	private static final Codec<Text[]> MESSAGES_CODEC = TextCodecs.STRINGIFIED_CODEC
		.listOf()
		.comapFlatMap(
			messages -> Util.decodeFixedLengthList(messages, 4).map(list -> new Text[]{(Text)list.get(0), (Text)list.get(1), (Text)list.get(2), (Text)list.get(3)}),
			messages -> List.of(messages[0], messages[1], messages[2], messages[3])
		);
	public static final Codec<SignText> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					MESSAGES_CODEC.fieldOf("messages").forGetter(signText -> signText.messages),
					MESSAGES_CODEC.lenientOptionalFieldOf("filtered_messages").forGetter(SignText::getFilteredMessages),
					DyeColor.CODEC.fieldOf("color").orElse(DyeColor.BLACK).forGetter(signText -> signText.color),
					Codec.BOOL.fieldOf("has_glowing_text").orElse(false).forGetter(signText -> signText.glowing)
				)
				.apply(instance, SignText::create)
	);
	public static final int field_43299 = 4;
	private final Text[] messages;
	private final Text[] filteredMessages;
	private final DyeColor color;
	private final boolean glowing;
	@Nullable
	private OrderedText[] orderedMessages;
	private boolean filtered;

	public SignText() {
		this(getDefaultText(), getDefaultText(), DyeColor.BLACK, false);
	}

	public SignText(Text[] messages, Text[] filteredMessages, DyeColor color, boolean glowing) {
		this.messages = messages;
		this.filteredMessages = filteredMessages;
		this.color = color;
		this.glowing = glowing;
	}

	private static Text[] getDefaultText() {
		return new Text[]{ScreenTexts.EMPTY, ScreenTexts.EMPTY, ScreenTexts.EMPTY, ScreenTexts.EMPTY};
	}

	private static SignText create(Text[] messages, Optional<Text[]> filteredMessages, DyeColor color, boolean glowing) {
		return new SignText(messages, (Text[])filteredMessages.orElse((Text[])Arrays.copyOf(messages, messages.length)), color, glowing);
	}

	public boolean isGlowing() {
		return this.glowing;
	}

	public SignText withGlowing(boolean glowing) {
		return glowing == this.glowing ? this : new SignText(this.messages, this.filteredMessages, this.color, glowing);
	}

	public DyeColor getColor() {
		return this.color;
	}

	public SignText withColor(DyeColor color) {
		return color == this.getColor() ? this : new SignText(this.messages, this.filteredMessages, color, this.glowing);
	}

	public Text getMessage(int line, boolean filtered) {
		return this.getMessages(filtered)[line];
	}

	public SignText withMessage(int line, Text message) {
		return this.withMessage(line, message, message);
	}

	public SignText withMessage(int line, Text message, Text filteredMessage) {
		Text[] texts = (Text[])Arrays.copyOf(this.messages, this.messages.length);
		Text[] texts2 = (Text[])Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
		texts[line] = message;
		texts2[line] = filteredMessage;
		return new SignText(texts, texts2, this.color, this.glowing);
	}

	public boolean hasText(PlayerEntity player) {
		return Arrays.stream(this.getMessages(player.shouldFilterText())).anyMatch(text -> !text.getString().isEmpty());
	}

	public Text[] getMessages(boolean filtered) {
		return filtered ? this.filteredMessages : this.messages;
	}

	public OrderedText[] getOrderedMessages(boolean filtered, Function<Text, OrderedText> messageOrderer) {
		if (this.orderedMessages == null || this.filtered != filtered) {
			this.filtered = filtered;
			this.orderedMessages = new OrderedText[4];

			for (int i = 0; i < 4; i++) {
				this.orderedMessages[i] = (OrderedText)messageOrderer.apply(this.getMessage(i, filtered));
			}
		}

		return this.orderedMessages;
	}

	private Optional<Text[]> getFilteredMessages() {
		for (int i = 0; i < 4; i++) {
			if (!this.filteredMessages[i].equals(this.messages[i])) {
				return Optional.of(this.filteredMessages);
			}
		}

		return Optional.empty();
	}

	public boolean hasRunCommandClickEvent(PlayerEntity player) {
		for (Text text : this.getMessages(player.shouldFilterText())) {
			Style style = text.getStyle();
			ClickEvent clickEvent = style.getClickEvent();
			if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				return true;
			}
		}

		return false;
	}
}
