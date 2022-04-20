/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_7417;
import net.minecraft.class_7419;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtText
implements class_7417 {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean interpret;
    private final Optional<Text> separator;
    private final String rawPath;
    private final class_7419 field_39014;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    public NbtText(String rawPath, boolean interpret, Optional<Text> separator, class_7419 arg) {
        this(rawPath, NbtText.parsePath(rawPath), interpret, separator, arg);
    }

    private NbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Optional<Text> separator, class_7419 arg) {
        this.rawPath = rawPath;
        this.path = path;
        this.interpret = interpret;
        this.separator = separator;
        this.field_39014 = arg;
    }

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(rawPath));
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    public Optional<Text> method_43484() {
        return this.separator;
    }

    public class_7419 method_43485() {
        return this.field_39014;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NbtText)) return false;
        NbtText nbtText = (NbtText)object;
        if (!this.field_39014.equals(nbtText.field_39014)) return false;
        if (!this.separator.equals(nbtText.separator)) return false;
        if (this.interpret != nbtText.interpret) return false;
        if (!this.rawPath.equals(nbtText.rawPath)) return false;
        return true;
    }

    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + (this.interpret ? 1 : 0);
        i = 31 * i + this.separator.hashCode();
        i = 31 * i + this.rawPath.hashCode();
        i = 31 * i + this.field_39014.hashCode();
        return i;
    }

    public String toString() {
        return "nbt{" + this.field_39014 + ", interpreting=" + this.interpret + ", separator=" + this.separator + "}";
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (serverCommandSource == null || this.path == null) {
            return Text.method_43473();
        }
        Stream<String> stream = this.field_39014.toNbt(serverCommandSource).flatMap(nbt -> {
            try {
                return this.path.get((NbtElement)nbt).stream();
            } catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(NbtElement::asString);
        if (this.interpret) {
            Text text2 = DataFixUtils.orElse(Texts.parse(serverCommandSource, this.separator, entity, i), Texts.DEFAULT_SEPARATOR_TEXT);
            return stream.flatMap(text -> {
                try {
                    MutableText mutableText = Text.Serializer.fromJson(text);
                    return Stream.of(Texts.parse(serverCommandSource, mutableText, entity, i));
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: {}", text, (Object)exception);
                    return Stream.of(new MutableText[0]);
                }
            }).reduce((accumulator, current) -> accumulator.append(text2).append((Text)current)).orElseGet(Text::method_43473);
        }
        return Texts.parse(serverCommandSource, this.separator, entity, i).map(text -> stream.map(Text::method_43470).reduce((accumulator, current) -> accumulator.append((Text)text).append((Text)current)).orElseGet(Text::method_43473)).orElseGet(() -> Text.method_43470(stream.collect(Collectors.joining(", "))));
    }
}

