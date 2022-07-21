package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.message.MessageType;
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
	 * @implNote This decoration allows using the sender and the content parameters. It has no style.
	 */
	public static Decoration ofChat(String translationKey) {
		return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
	}

	/**
	 * {@return the decoration used in incoming messages sent with {@link
	 * net.minecraft.server.command.MessageCommand}}
	 * 
	 * @implNote This decoration allows using the sender and the content parameters. It is
	 * italicized and colored gray.
	 */
	public static Decoration ofIncomingMessage(String translationKey) {
		Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
		return new Decoration(translationKey, List.of(Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), style);
	}

	/**
	 * {@return the decoration used in outgoing messages sent with {@link
	 * net.minecraft.server.command.MessageCommand}}
	 * 
	 * @implNote This decoration allows using the target (recipient) and the content parameters.
	 * It is italicized and colored gray.
	 */
	public static Decoration ofOutgoingMessage(String translationKey) {
		Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
		return new Decoration(translationKey, List.of(Decoration.Parameter.TARGET, Decoration.Parameter.CONTENT), style);
	}

	/**
	 * {@return the decoration used in chat messages}
	 * 
	 * @implNote This decoration allows using the target (team name), the sender, and the
	 * content parameters. It has no style.
	 */
	public static Decoration ofTeamMessage(String translationKey) {
		return new Decoration(translationKey, List.of(Decoration.Parameter.TARGET, Decoration.Parameter.SENDER, Decoration.Parameter.CONTENT), Style.EMPTY);
	}

	/**
	 * {@return the text obtained by applying the passed values to the decoration}
	 * 
	 * @param content the value of the content parameter
	 */
	public Text apply(Text content, MessageType.Parameters params) {
		Object[] objects = this.collectArguments(content, params);
		return Text.translatable(this.translationKey, objects).fillStyle(this.style);
	}

	/**
	 * {@return the arguments passed to {@link Text#translatable(String, Object[])}}
	 * 
	 * <p>This is collected by supplying {@code content} and {@code sender} to the
	 * parameters' {@link Decoration.Parameter#apply} method.
	 */
	private Text[] collectArguments(Text content, MessageType.Parameters params) {
		Text[] texts = new Text[this.parameters.size()];

		for (int i = 0; i < texts.length; i++) {
			Decoration.Parameter parameter = (Decoration.Parameter)this.parameters.get(i);
			texts[i] = parameter.apply(content, params);
		}

		return texts;
	}

	/**
	 * Represents a parameter that the decoration uses.
	 */
	public static enum Parameter implements StringIdentifiable {
		SENDER("sender", (content, params) -> params.name()),
		TARGET("target", (content, params) -> params.targetName()),
		CONTENT("content", (content, params) -> content);

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
		public Text apply(Text content, MessageType.Parameters params) {
			Text text = this.selector.select(content, params);
			return (Text)Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
		}

		@Override
		public String asString() {
			return this.name;
		}

		/**
		 * A functional interface that selects the text from the passed parameters.
		 */
		public interface Selector {
			@Nullable
			Text select(Text content, MessageType.Parameters params);
		}
	}
}
