/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.TextConvertibleArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextArgumentType
implements TextConvertibleArgumentType<Text> {
    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(text -> Text.translatable("argument.component.invalid", text));

    private TextArgumentType() {
    }

    public static Text getTextArgument(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Text.class);
    }

    public static TextArgumentType text() {
        return new TextArgumentType();
    }

    @Override
    public Text parse(StringReader stringReader) throws CommandSyntaxException {
        try {
            MutableText text = Text.Serializer.fromJson(stringReader);
            if (text == null) {
                throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, "empty");
            }
            return text;
        } catch (Exception exception) {
            String string = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, string);
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public Text toText(Text text) {
        return text;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

