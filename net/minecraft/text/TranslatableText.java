/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

public class TranslatableText
extends BaseText
implements ParsableText {
    private static final Language EMPTY_LANGUAGE = new Language();
    private static final Language LANGUAGE = Language.getInstance();
    private final String key;
    private final Object[] args;
    private final Object lock = new Object();
    private long languageReloadTimestamp = -1L;
    protected final List<Text> translations = Lists.newArrayList();
    public static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TranslatableText(String key, Object ... args) {
        this.key = key;
        this.args = args;
        for (int i = 0; i < args.length; ++i) {
            Object object = args[i];
            if (object instanceof Text) {
                Text text = ((Text)object).deepCopy();
                this.args[i] = text;
                text.getStyle().setParent(this.getStyle());
                continue;
            }
            if (object != null) continue;
            this.args[i] = "null";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    synchronized void updateTranslations() {
        Object object = this.lock;
        synchronized (object) {
            long l = LANGUAGE.getTimeLoaded();
            if (l == this.languageReloadTimestamp) {
                return;
            }
            this.languageReloadTimestamp = l;
            this.translations.clear();
        }
        String string = LANGUAGE.translate(this.key);
        try {
            this.setTranslation(string);
        } catch (TranslationException translationException) {
            this.translations.clear();
            this.translations.add(new LiteralText(string));
        }
    }

    protected void setTranslation(String translation) {
        Matcher matcher = ARG_FORMAT.matcher(translation);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    LiteralText text = new LiteralText(String.format(translation.substring(j, k), new Object[0]));
                    text.getStyle().setParent(this.getStyle());
                    this.translations.add(text);
                }
                String string = matcher.group(2);
                String string2 = translation.substring(k, l);
                if ("%".equals(string) && "%%".equals(string2)) {
                    LiteralText text2 = new LiteralText("%");
                    text2.getStyle().setParent(this.getStyle());
                    this.translations.add(text2);
                } else if ("s".equals(string)) {
                    int m;
                    String string3 = matcher.group(1);
                    int n = m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
                    if (m < this.args.length) {
                        this.translations.add(this.getArg(m));
                    }
                } else {
                    throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
                }
                j = l;
            }
            if (j < translation.length()) {
                LiteralText text3 = new LiteralText(String.format(translation.substring(j), new Object[0]));
                text3.getStyle().setParent(this.getStyle());
                this.translations.add(text3);
            }
        } catch (IllegalFormatException illegalFormatException) {
            throw new TranslationException(this, (Throwable)illegalFormatException);
        }
    }

    private Text getArg(int index) {
        Text text;
        if (index >= this.args.length) {
            throw new TranslationException(this, index);
        }
        Object object = this.args[index];
        if (object instanceof Text) {
            text = (Text)object;
        } else {
            text = new LiteralText(object == null ? "null" : object.toString());
            text.getStyle().setParent(this.getStyle());
        }
        return text;
    }

    @Override
    public Text setStyle(Style style) {
        super.setStyle(style);
        for (Object object : this.args) {
            if (!(object instanceof Text)) continue;
            ((Text)object).getStyle().setParent(this.getStyle());
        }
        if (this.languageReloadTimestamp > -1L) {
            for (Text text : this.translations) {
                text.getStyle().setParent(style);
            }
        }
        return this;
    }

    @Override
    public Stream<Text> stream() {
        this.updateTranslations();
        return Streams.concat(this.translations.stream(), this.siblings.stream()).flatMap(Text::stream);
    }

    @Override
    public String asString() {
        this.updateTranslations();
        StringBuilder stringBuilder = new StringBuilder();
        for (Text text : this.translations) {
            stringBuilder.append(text.asString());
        }
        return stringBuilder.toString();
    }

    @Override
    public TranslatableText copy() {
        Object[] objects = new Object[this.args.length];
        for (int i = 0; i < this.args.length; ++i) {
            objects[i] = this.args[i] instanceof Text ? ((Text)this.args[i]).deepCopy() : this.args[i];
        }
        return new TranslatableText(this.key, objects);
    }

    @Override
    public Text parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];
        for (int i = 0; i < objects.length; ++i) {
            Object object = this.args[i];
            objects[i] = object instanceof Text ? Texts.parse(source, (Text)object, sender, depth) : object;
        }
        return new TranslatableText(this.key, objects);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText)o;
            return Arrays.equals(this.args, translatableText.args) && this.key.equals(translatableText.key) && super.equals(o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.args);
        return i;
    }

    @Override
    public String toString() {
        return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public /* synthetic */ Text copy() {
        return this.copy();
    }
}

