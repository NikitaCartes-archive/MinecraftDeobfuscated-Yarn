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
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignText {
	private static final Codec<Text[]> MESSAGES_CODEC = Codecs.STRINGIFIED_TEXT
		.listOf()
		.comapFlatMap(
			messages -> Util.toArray(messages, 4).map(list -> new Text[]{(Text)list.get(0), (Text)list.get(1), (Text)list.get(2), (Text)list.get(3)}),
			messages -> List.of(messages[0], messages[1], messages[2], messages[3])
		);
	public static final Codec<SignText> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					MESSAGES_CODEC.fieldOf("messages").forGetter(signText -> signText.messages),
					MESSAGES_CODEC.optionalFieldOf("filtered_messages").forGetter(SignText::getFilteredMessages),
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
		Text[] texts = (Text[])filteredMessages.orElseGet(SignText::getDefaultText);
		copyMessages(messages, texts);
		return new SignText(messages, texts, color, glowing);
	}

	private static void copyMessages(Text[] from, Text[] to) {
		for (int i = 0; i < 4; i++) {
			if (to[i].equals(ScreenTexts.EMPTY)) {
				to[i] = from[i];
			}
		}
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

	private Text[] getMessages(boolean filtered) {
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
		Text[] texts = new Text[4];
		boolean bl = false;

		for (int i = 0; i < 4; i++) {
			Text text = this.filteredMessages[i];
			if (!text.equals(this.messages[i])) {
				texts[i] = text;
				bl = true;
			} else {
				texts[i] = ScreenTexts.EMPTY;
			}
		}

		return bl ? Optional.of(texts) : Optional.empty();
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

	public boolean runCommandClickEvent(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
		boolean bl = false;

		for (Text text : this.getMessages(player.shouldFilterText())) {
			Style style = text.getStyle();
			ClickEvent clickEvent = style.getClickEvent();
			if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				player.getServer().getCommandManager().executeWithPrefix(createCommandSource(player, world, pos), clickEvent.getValue());
				bl = true;
			}
		}

		return bl;
	}

	private static ServerCommandSource createCommandSource(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
		String string = player.getName().getString();
		Text text = player.getDisplayName();
		return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO, world, 2, string, text, world.getServer(), player);
	}
}
