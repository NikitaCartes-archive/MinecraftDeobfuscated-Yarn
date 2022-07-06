/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.message.MessageSender;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

/**
 * A decoration is a pre-defined set of styling and formatting rules for messages
 * sent by the server. This consists of the translation key, the style, and the parameters
 * usable in the translation. The actual text format needs to be supplied via custom
 * language files in resource packs.
 */
public record Decoration(String translationKey, List<Parameter> parameters, Style style) {
    public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("translation_key")).forGetter(Decoration::translationKey), ((MapCodec)Parameter.CODEC.listOf().fieldOf("parameters")).forGetter(Decoration::parameters), Style.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(Decoration::style)).apply((Applicative<Decoration, ?>)instance, Decoration::new));

    /**
     * {@return the decoration used in chat messages}
     * 
     * @implNote This decoration allows using the sender and the content parameters. It has no style.
     */
    public static Decoration ofChat(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    /**
     * {@return the decoration used in chat messages}
     * 
     * @implNote This decoration allows using the sender and the content parameters. It is gray and italic.
     */
    public static Decoration ofIncomingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), style);
    }

    /**
     * {@return the decoration used in chat messages}
     * 
     * @implNote This decoration allows using the target and the content parameters. It is gray and italic.
     */
    public static Decoration ofOutgoingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.CONTENT), style);
    }

    /**
     * {@return the decoration used in chat messages}
     * 
     * @implNote This decoration allows using the target (team name), the sender, and the
     * content parameters. It has no style.
     */
    public static Decoration ofTeamMessage(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    /**
     * {@return the text obtained by applying the passed values to the decoration}
     * 
     * @param sender the sender passed to parameters, or {@code null} if inapplicable
     * @param content the value of the content parameter
     */
    public Text apply(Text content, MessageSender sender) {
        Object[] objects = this.collectArguments(content, sender);
        return Text.translatable(this.translationKey, objects).fillStyle(this.style);
    }

    /**
     * {@return the arguments passed to {@link Text#translatable(String, Object[])}}
     * 
     * <p>This is collected by supplying {@code content} and {@code sender} to the
     * parameters' {@link Decoration.Parameter#apply} method.
     */
    private Text[] collectArguments(Text content, MessageSender sender) {
        Text[] texts = new Text[this.parameters.size()];
        for (int i = 0; i < texts.length; ++i) {
            Parameter parameter = this.parameters.get(i);
            texts[i] = parameter.apply(content, sender);
        }
        return texts;
    }

    public static enum Parameter implements StringIdentifiable
    {
        SENDER("sender", (content, sender) -> sender.name()),
        TARGET("target", (content, sender) -> sender.targetName()),
        CONTENT("content", (content, sender) -> content);

        public static final Codec<Parameter> CODEC;
        private final String name;
        private final Selector selector;

        private Parameter(String name, Selector selector) {
            this.name = name;
            this.selector = selector;
        }

        public Text apply(Text content, MessageSender sender) {
            Text text = this.selector.select(content, sender);
            return Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Parameter::values);
        }

        public static interface Selector {
            @Nullable
            public Text select(Text var1, MessageSender var2);
        }
    }
}

