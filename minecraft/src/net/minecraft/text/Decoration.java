package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.message.MessageSender;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

/**
 * A decoration is a pre-defined set of styling and formatting rules for messages
 * sent by the server. This consists of the translation key, the style, and the parameters
 * usable in the translation. The actual text format needs to be supplied via custom
 * language files in resource packs.
 */
public record Decoration(String translationKey, List<Decoration.Parameter> parameters, Style style) {
	public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.STRING.fieldOf("translation_key").forGetter(Decoration::translationKey),
					Decoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(Decoration::parameters),
					Style.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(Decoration::style)
				)
				.apply(instance, Decoration::new)
	);

	/**
	 * {@return the decoration used in chat messages}
	 * 
	 * @implNote This decoration allows use of the sender and the content parameters. It has no style.
	 */
	public static Decoration ofChat(String translationKey) {
		return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
	}

	/**
	 * {@return the decoration used in {@link net.minecraft.server.command.MessageCommand}}
	 * 
	 * @implNote This decoration allows use of the sender and the content parameters.
	 * The text is colored gray and is displayed in italic.
	 */
	public static Decoration ofDirectMessage(String translationKey) {
		Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
		return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), style);
	}

	/**
	 * {@return the decoration used in chat messages}
	 * 
	 * @implNote This decoration allows use of the team name, the sender, and the
	 * content parameters. It has no style.
	 */
	public static Decoration ofTeamMessage(String translationKey) {
		return new Decoration(translationKey, List.of(Decoration.Parameter.TEAM_NAME, Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
	}

	/**
	 * {@return the text obtained by applying the passed values to the decoration}
	 * 
	 * @param content the value of the content parameter
	 * @param sender the sender passed to parameters, or {@code null} if inapplicable
	 */
	public Text apply(Text content, @Nullable MessageSender sender) {
		Object[] objects = this.collectArguments(content, sender);
		return Text.translatable(this.translationKey, objects).fillStyle(this.style);
	}

	/**
	 * {@return the arguments passed to {@link Text#translatable(String, Object[])}}
	 * 
	 * <p>This is collected by supplying {@code content} and {@code sender} to the
	 * parameters' {@link Decoration.Parameter#apply} method.
	 */
	private Text[] collectArguments(Text content, @Nullable MessageSender sender) {
		Text[] texts = new Text[this.parameters.size()];

		for (int i = 0; i < texts.length; i++) {
			Decoration.Parameter parameter = (Decoration.Parameter)this.parameters.get(i);
			texts[i] = parameter.apply(content, sender);
		}

		return texts;
	}

	/**
	 * Represents a parameter that the decoration uses.
	 */
	public static enum Parameter implements StringIdentifiable {
		SENDER("sender", (content, sender) -> sender != null ? sender.name() : null),
		TEAM_NAME("team_name", (content, sender) -> sender != null ? sender.teamName() : null),
		CONTENT("content", (content, sender) -> content);

		public static final com.mojang.serialization.Codec<Decoration.Parameter> CODEC = StringIdentifiable.createCodec(Decoration.Parameter::values);
		private final String name;
		private final Decoration.Parameter.Selector selector;

		private Parameter(String name, Decoration.Parameter.Selector selector) {
			this.name = name;
			this.selector = selector;
		}

		/**
		 * {@return the text obtained by applying the passed values to the parameter}
		 */
		public Text apply(Text content, @Nullable MessageSender sender) {
			Text text = this.selector.select(content, sender);
			return (Text)Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
		}

		@Override
		public String asString() {
			return this.name;
		}

		/**
		 * A functional interface that selects the text from the passed values.
		 */
		public interface Selector {
			@Nullable
			Text select(Text content, @Nullable MessageSender sender);
		}
	}
}
