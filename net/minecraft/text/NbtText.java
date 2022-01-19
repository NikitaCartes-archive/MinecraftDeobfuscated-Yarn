/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class NbtText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final boolean interpret;
    protected final Optional<Text> separator;
    protected final String rawPath;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(rawPath));
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public NbtText(String rawPath, boolean interpret, Optional<Text> separator) {
        this(rawPath, NbtText.parsePath(rawPath), interpret, separator);
    }

    protected NbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Optional<Text> separator) {
        this.rawPath = rawPath;
        this.path = path;
        this.interpret = interpret;
        this.separator = separator;
    }

    protected abstract Stream<NbtCompound> toNbt(ServerCommandSource var1) throws CommandSyntaxException;

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null || this.path == null) {
            return new LiteralText("");
        }
        Stream<String> stream = this.toNbt(source).flatMap(nbt -> {
            try {
                return this.path.get((NbtElement)nbt).stream();
            } catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(NbtElement::asString);
        if (this.interpret) {
            Text text2 = DataFixUtils.orElse(Texts.parse(source, this.separator, sender, depth), Texts.DEFAULT_SEPARATOR_TEXT);
            return stream.flatMap(text -> {
                try {
                    MutableText mutableText = Text.Serializer.fromJson(text);
                    return Stream.of(Texts.parse(source, mutableText, sender, depth));
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: {}", text, (Object)exception);
                    return Stream.of(new MutableText[0]);
                }
            }).reduce((accumulator, current) -> accumulator.append(text2).append((Text)current)).orElseGet(() -> new LiteralText(""));
        }
        return Texts.parse(source, this.separator, sender, depth).map(text -> stream.map(string -> new LiteralText((String)string)).reduce((accumulator, current) -> accumulator.append((Text)text).append((Text)current)).orElseGet(() -> new LiteralText(""))).orElseGet(() -> new LiteralText(stream.collect(Collectors.joining(", "))));
    }

    public static class StorageNbtText
    extends NbtText {
        private final Identifier id;

        public StorageNbtText(String rawPath, boolean interpret, Identifier id, Optional<Text> separator) {
            super(rawPath, interpret, separator);
            this.id = id;
        }

        public StorageNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Identifier id, Optional<Text> separator) {
            super(rawPath, path, interpret, separator);
            this.id = id;
        }

        public Identifier getId() {
            return this.id;
        }

        @Override
        public StorageNbtText copy() {
            return new StorageNbtText(this.rawPath, this.path, this.interpret, this.id, this.separator);
        }

        @Override
        protected Stream<NbtCompound> toNbt(ServerCommandSource source) {
            NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
            return Stream.of(nbtCompound);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof StorageNbtText) {
                StorageNbtText storageNbtText = (StorageNbtText)object;
                return Objects.equals(this.id, storageNbtText.id) && Objects.equals(this.rawPath, storageNbtText.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "StorageNbtComponent{id='" + this.id + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }

    public static class BlockNbtText
    extends NbtText {
        private final String rawPos;
        @Nullable
        private final PosArgument pos;

        public BlockNbtText(String rawPath, boolean rawJson, String rawPos, Optional<Text> separator) {
            super(rawPath, rawJson, separator);
            this.rawPos = rawPos;
            this.pos = this.parsePos(this.rawPos);
        }

        @Nullable
        private PosArgument parsePos(String rawPos) {
            try {
                return BlockPosArgumentType.blockPos().parse(new StringReader(rawPos));
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private BlockNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawPos, @Nullable PosArgument pos, Optional<Text> separator) {
            super(rawPath, path, interpret, separator);
            this.rawPos = rawPos;
            this.pos = pos;
        }

        @Nullable
        public String getPos() {
            return this.rawPos;
        }

        @Override
        public BlockNbtText copy() {
            return new BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos, this.separator);
        }

        @Override
        protected Stream<NbtCompound> toNbt(ServerCommandSource source) {
            BlockEntity blockEntity;
            BlockPos blockPos;
            ServerWorld serverWorld;
            if (this.pos != null && (serverWorld = source.getWorld()).canSetBlock(blockPos = this.pos.toAbsoluteBlockPos(source)) && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
                return Stream.of(blockEntity.createNbtWithIdentifyingData());
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BlockNbtText) {
                BlockNbtText blockNbtText = (BlockNbtText)object;
                return Objects.equals(this.rawPos, blockNbtText.rawPos) && Objects.equals(this.rawPath, blockNbtText.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.rawPos + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }

    public static class EntityNbtText
    extends NbtText {
        private final String rawSelector;
        @Nullable
        private final EntitySelector selector;

        public EntityNbtText(String rawPath, boolean interpret, String rawSelector, Optional<Text> separator) {
            super(rawPath, interpret, separator);
            this.rawSelector = rawSelector;
            this.selector = EntityNbtText.parseSelector(rawSelector);
        }

        @Nullable
        private static EntitySelector parseSelector(String rawSelector) {
            try {
                EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
                return entitySelectorReader.read();
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private EntityNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawSelector, @Nullable EntitySelector selector, Optional<Text> separator) {
            super(rawPath, path, interpret, separator);
            this.rawSelector = rawSelector;
            this.selector = selector;
        }

        public String getSelector() {
            return this.rawSelector;
        }

        @Override
        public EntityNbtText copy() {
            return new EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector, this.separator);
        }

        @Override
        protected Stream<NbtCompound> toNbt(ServerCommandSource source) throws CommandSyntaxException {
            if (this.selector != null) {
                List<? extends Entity> list = this.selector.getEntities(source);
                return list.stream().map(NbtPredicate::entityToNbt);
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof EntityNbtText) {
                EntityNbtText entityNbtText = (EntityNbtText)object;
                return Objects.equals(this.rawSelector, entityNbtText.rawSelector) && Objects.equals(this.rawPath, entityNbtText.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.rawSelector + "'path='" + this.rawPath + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }
}

