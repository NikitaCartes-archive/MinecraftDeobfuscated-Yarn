/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.TagKey;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class class_7066<T>
implements ArgumentType<class_7068<T>> {
    private static final Collection<String> field_37223 = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    private static final DynamicCommandExceptionType field_37224 = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locatebiome.invalid", object));
    private static final DynamicCommandExceptionType field_37225 = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locate.invalid", object));
    final RegistryKey<? extends Registry<T>> field_37226;

    public class_7066(RegistryKey<? extends Registry<T>> registryKey) {
        this.field_37226 = registryKey;
    }

    public static <T> class_7066<T> method_41170(RegistryKey<? extends Registry<T>> registryKey) {
        return new class_7066<T>(registryKey);
    }

    private static <T> class_7068<T> method_41166(CommandContext<ServerCommandSource> commandContext, String string, RegistryKey<Registry<T>> registryKey, DynamicCommandExceptionType dynamicCommandExceptionType) throws CommandSyntaxException {
        class_7068 lv = commandContext.getArgument(string, class_7068.class);
        Optional<class_7068<T>> optional = lv.method_41175(registryKey);
        return optional.orElseThrow(() -> dynamicCommandExceptionType.create(lv));
    }

    public static class_7068<Biome> method_41165(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return class_7066.method_41166(commandContext, string, Registry.BIOME_KEY, field_37224);
    }

    public static class_7068<ConfiguredStructureFeature<?, ?>> method_41171(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return class_7066.method_41166(commandContext, string, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, field_37225);
    }

    @Override
    public class_7068<T> parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            int i = stringReader.getCursor();
            try {
                stringReader.skip();
                Identifier identifier = Identifier.fromCommandInput(stringReader);
                return new class_7070(TagKey.of(this.field_37226, identifier));
            } catch (CommandSyntaxException commandSyntaxException) {
                stringReader.setCursor(i);
                throw commandSyntaxException;
            }
        }
        Identifier identifier2 = Identifier.fromCommandInput(stringReader);
        return new class_7067(RegistryKey.of(this.field_37226, identifier2));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        S s = commandContext.getSource();
        if (s instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)s;
            commandSource.getRegistryManager().getOptional(this.field_37226).ifPresent(registry -> {
                CommandSource.suggestIdentifiers(registry.streamTags().map(TagKey::id), suggestionsBuilder, "#");
                CommandSource.suggestIdentifiers(registry.getIds(), suggestionsBuilder);
            });
        }
        return suggestionsBuilder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return field_37223;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static interface class_7068<T>
    extends Predicate<RegistryEntry<T>> {
        public Either<RegistryKey<T>, TagKey<T>> method_41173();

        public <E> Optional<class_7068<E>> method_41175(RegistryKey<? extends Registry<E>> var1);

        public String method_41176();
    }

    record class_7070<T>(TagKey<T> key) implements class_7068<T>
    {
        @Override
        public Either<RegistryKey<T>, TagKey<T>> method_41173() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<class_7068<E>> method_41175(RegistryKey<? extends Registry<E>> registryKey) {
            return this.key.method_41008(registryKey).map(class_7070::new);
        }

        @Override
        public boolean test(RegistryEntry<T> registryEntry) {
            return registryEntry.isIn(this.key);
        }

        @Override
        public String method_41176() {
            return "#" + this.key.id();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((RegistryEntry)object);
        }
    }

    record class_7067<T>(RegistryKey<T> key) implements class_7068<T>
    {
        @Override
        public Either<RegistryKey<T>, TagKey<T>> method_41173() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<class_7068<E>> method_41175(RegistryKey<? extends Registry<E>> registryKey) {
            return this.key.tryCast(registryKey).map(class_7067::new);
        }

        @Override
        public boolean test(RegistryEntry<T> registryEntry) {
            return registryEntry.matchesKey(this.key);
        }

        @Override
        public String method_41176() {
            return this.key.getValue().toString();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((RegistryEntry)object);
        }
    }

    public static class class_7069
    implements ArgumentSerializer<class_7066<?>> {
        @Override
        public void toPacket(class_7066<?> arg, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeIdentifier(arg.field_37226.getValue());
        }

        @Override
        public class_7066<?> fromPacket(PacketByteBuf packetByteBuf) {
            Identifier identifier = packetByteBuf.readIdentifier();
            return new class_7066(RegistryKey.ofRegistry(identifier));
        }

        @Override
        public void toJson(class_7066<?> arg, JsonObject jsonObject) {
            jsonObject.addProperty("registry", arg.field_37226.getValue().toString());
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf buf) {
            return this.fromPacket(buf);
        }
    }
}

