/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class NbtText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final boolean interpret;
    protected final String rawPath;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String string) {
        try {
            return new NbtPathArgumentType().method_9362(new StringReader(string));
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public NbtText(String string, boolean bl) {
        this(string, NbtText.parsePath(string), bl);
    }

    protected NbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl) {
        this.rawPath = string;
        this.path = nbtPath;
        this.interpret = bl;
    }

    protected abstract Stream<CompoundTag> toNbt(ServerCommandSource var1) throws CommandSyntaxException;

    @Override
    public String asString() {
        return "";
    }

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    @Override
    public Text parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (serverCommandSource == null || this.path == null) {
            return new LiteralText("");
        }
        Stream<String> stream = this.toNbt(serverCommandSource).flatMap(compoundTag -> {
            try {
                return this.path.get((Tag)compoundTag).stream();
            } catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(Tag::asString);
        if (this.interpret) {
            return stream.flatMap(string -> {
                try {
                    Text text = Text.Serializer.fromJson(string);
                    return Stream.of(Texts.parse(serverCommandSource, text, entity, i));
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: " + string, (Throwable)exception);
                    return Stream.of(new Text[0]);
                }
            }).reduce((text, text2) -> text.append(", ").append((Text)text2)).orElse(new LiteralText(""));
        }
        return new LiteralText(Joiner.on(", ").join(stream.iterator()));
    }

    public static class StorageNbtText
    extends NbtText {
        private final Identifier id;

        public StorageNbtText(String string, boolean bl, Identifier identifier) {
            super(string, bl);
            this.id = identifier;
        }

        public StorageNbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, Identifier identifier) {
            super(string, nbtPath, bl);
            this.id = identifier;
        }

        public Identifier method_23728() {
            return this.id;
        }

        @Override
        public Text copy() {
            return new StorageNbtText(this.rawPath, this.path, this.interpret, this.id);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) {
            CompoundTag compoundTag = serverCommandSource.getMinecraftServer().getDataCommandStorage().get(this.id);
            return Stream.of(compoundTag);
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
            return "StorageNbtComponent{id='" + this.id + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }

    public static class BlockNbtText
    extends NbtText {
        private final String rawPos;
        @Nullable
        private final PosArgument pos;

        public BlockNbtText(String string, boolean bl, String string2) {
            super(string, bl);
            this.rawPos = string2;
            this.pos = this.parsePos(this.rawPos);
        }

        @Nullable
        private PosArgument parsePos(String string) {
            try {
                return BlockPosArgumentType.blockPos().method_9699(new StringReader(string));
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private BlockNbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable PosArgument posArgument) {
            super(string, nbtPath, bl);
            this.rawPos = string2;
            this.pos = posArgument;
        }

        @Nullable
        public String getPos() {
            return this.rawPos;
        }

        @Override
        public Text copy() {
            return new BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) {
            BlockEntity blockEntity;
            BlockPos blockPos;
            ServerWorld serverWorld;
            if (this.pos != null && (serverWorld = serverCommandSource.getWorld()).canSetBlock(blockPos = this.pos.toAbsoluteBlockPos(serverCommandSource)) && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
                return Stream.of(blockEntity.toTag(new CompoundTag()));
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
            return "BlockPosArgument{pos='" + this.rawPos + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }

    public static class EntityNbtText
    extends NbtText {
        private final String rawSelector;
        @Nullable
        private final EntitySelector selector;

        public EntityNbtText(String string, boolean bl, String string2) {
            super(string, bl);
            this.rawSelector = string2;
            this.selector = EntityNbtText.parseSelector(string2);
        }

        @Nullable
        private static EntitySelector parseSelector(String string) {
            try {
                EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
                return entitySelectorReader.read();
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private EntityNbtText(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable EntitySelector entitySelector) {
            super(string, nbtPath, bl);
            this.rawSelector = string2;
            this.selector = entitySelector;
        }

        public String getSelector() {
            return this.rawSelector;
        }

        @Override
        public Text copy() {
            return new EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            if (this.selector != null) {
                List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
                return list.stream().map(NbtPredicate::entityToTag);
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
            return "EntityNbtComponent{selector='" + this.rawSelector + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
}

