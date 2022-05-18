/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class TranslatableTextContent
implements TextContent {
    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
    private static final StringVisitable LITERAL_PERCENT_SIGN = StringVisitable.plain("%");
    private static final StringVisitable NULL_ARGUMENT = StringVisitable.plain("null");
    private final String key;
    private final Object[] args;
    @Nullable
    private Language languageCache;
    private List<StringVisitable> translations = ImmutableList.of();
    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TranslatableTextContent(String key) {
        this.key = key;
        this.args = EMPTY_ARGUMENTS;
    }

    public TranslatableTextContent(String key, Object ... args) {
        this.key = key;
        this.args = args;
    }

    private void updateTranslations() {
        Language language = Language.getInstance();
        if (language == this.languageCache) {
            return;
        }
        this.languageCache = language;
        String string = language.get(this.key);
        try {
            ImmutableList.Builder builder = ImmutableList.builder();
            this.forEachPart(string, builder::add);
            this.translations = builder.build();
        } catch (TranslationException translationException) {
            this.translations = ImmutableList.of(StringVisitable.plain(string));
        }
    }

    private void forEachPart(String translation, Consumer<StringVisitable> partsConsumer) {
        Matcher matcher = ARG_FORMAT.matcher(translation);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                String string;
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    string = translation.substring(j, k);
                    if (string.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    partsConsumer.accept(StringVisitable.plain(string));
                }
                string = matcher.group(2);
                String string2 = translation.substring(k, l);
                if ("%".equals(string) && "%%".equals(string2)) {
                    partsConsumer.accept(LITERAL_PERCENT_SIGN);
                } else if ("s".equals(string)) {
                    int m;
                    String string3 = matcher.group(1);
                    int n = m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
                    if (m < this.args.length) {
                        partsConsumer.accept(this.getArg(m));
                    }
                } else {
                    throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
                }
                j = l;
            }
            if (j < translation.length()) {
                String string4 = translation.substring(j);
                if (string4.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                partsConsumer.accept(StringVisitable.plain(string4));
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new TranslationException(this, (Throwable)illegalArgumentException);
        }
    }

    private StringVisitable getArg(int index) {
        if (index >= this.args.length) {
            throw new TranslationException(this, index);
        }
        Object object = this.args[index];
        if (object instanceof Text) {
            return (Text)object;
        }
        return object == null ? NULL_ARGUMENT : StringVisitable.plain(object.toString());
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        this.updateTranslations();
        for (StringVisitable stringVisitable : this.translations) {
            Optional<T> optional = stringVisitable.visit(visitor, style);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        this.updateTranslations();
        for (StringVisitable stringVisitable : this.translations) {
            Optional<T> optional = stringVisitable.visit(visitor);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];
        for (int i = 0; i < objects.length; ++i) {
            Object object = this.args[i];
            objects[i] = object instanceof Text ? Texts.parse(source, (Text)object, sender, depth) : object;
        }
        return MutableText.of(new TranslatableTextContent(this.key, objects));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TranslatableTextContent)) return false;
        TranslatableTextContent translatableTextContent = (TranslatableTextContent)o;
        if (!this.key.equals(translatableTextContent.key)) return false;
        if (!Arrays.equals(this.args, translatableTextContent.args)) return false;
        return true;
    }

    public int hashCode() {
        int i = this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.args);
        return i;
    }

    public String toString() {
        return "translation{key='" + this.key + "', args=" + Arrays.toString(this.args) + "}";
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

