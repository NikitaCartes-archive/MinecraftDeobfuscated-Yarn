/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public class BlockPredicateArgumentType
implements ArgumentType<BlockPredicate> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private final RegistryWrapper<Block> registryWrapper;

    public BlockPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.BLOCK);
    }

    public static BlockPredicateArgumentType blockPredicate(CommandRegistryAccess commandRegistryAccess) {
        return new BlockPredicateArgumentType(commandRegistryAccess);
    }

    @Override
    public BlockPredicate parse(StringReader stringReader) throws CommandSyntaxException {
        return BlockPredicateArgumentType.parse(this.registryWrapper, stringReader);
    }

    public static BlockPredicate parse(RegistryWrapper<Block> registryWrapper, StringReader reader) throws CommandSyntaxException {
        return BlockArgumentParser.blockOrTag(registryWrapper, reader, true).map(result -> new StatePredicate(result.blockState(), result.properties().keySet(), result.nbt()), result -> new TagPredicate(result.tag(), result.vagueProperties(), result.nbt()));
    }

    public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, BlockPredicate.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, true, true);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static interface BlockPredicate
    extends Predicate<CachedBlockPosition> {
        public boolean hasNbt();
    }

    static class TagPredicate
    implements BlockPredicate {
        private final RegistryEntryList<Block> tag;
        @Nullable
        private final NbtCompound nbt;
        private final Map<String, String> properties;

        TagPredicate(RegistryEntryList<Block> tag, Map<String, String> properties, @Nullable NbtCompound nbt) {
            this.tag = tag;
            this.properties = properties;
            this.nbt = nbt;
        }

        @Override
        public boolean test(CachedBlockPosition cachedBlockPosition) {
            BlockState blockState = cachedBlockPosition.getBlockState();
            if (!blockState.isIn(this.tag)) {
                return false;
            }
            for (Map.Entry<String, String> entry : this.properties.entrySet()) {
                Property<?> property = blockState.getBlock().getStateManager().getProperty(entry.getKey());
                if (property == null) {
                    return false;
                }
                Comparable comparable = property.parse(entry.getValue()).orElse(null);
                if (comparable == null) {
                    return false;
                }
                if (blockState.get(property) == comparable) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
                return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(), true);
            }
            return true;
        }

        @Override
        public boolean hasNbt() {
            return this.nbt != null;
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((CachedBlockPosition)context);
        }
    }

    static class StatePredicate
    implements BlockPredicate {
        private final BlockState state;
        private final Set<Property<?>> properties;
        @Nullable
        private final NbtCompound nbt;

        public StatePredicate(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound nbt) {
            this.state = state;
            this.properties = properties;
            this.nbt = nbt;
        }

        @Override
        public boolean test(CachedBlockPosition cachedBlockPosition) {
            BlockState blockState = cachedBlockPosition.getBlockState();
            if (!blockState.isOf(this.state.getBlock())) {
                return false;
            }
            for (Property<?> property : this.properties) {
                if (blockState.get(property) == this.state.get(property)) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
                return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(), true);
            }
            return true;
        }

        @Override
        public boolean hasNbt() {
            return this.nbt != null;
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((CachedBlockPosition)context);
        }
    }
}

