/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.chat;

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
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentWithSelectors;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class NbtComponent
extends BaseComponent
implements ComponentWithSelectors {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final boolean componentJson;
    protected final String path;
    @Nullable
    protected final NbtPathArgumentType.NbtPath parsedPath;

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String string) {
        try {
            return new NbtPathArgumentType().method_9362(new StringReader(string));
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public NbtComponent(String string, boolean bl) {
        this(string, NbtComponent.parsePath(string), bl);
    }

    protected NbtComponent(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl) {
        this.path = string;
        this.parsedPath = nbtPath;
        this.componentJson = bl;
    }

    protected abstract Stream<CompoundTag> resolve(ServerCommandSource var1) throws CommandSyntaxException;

    @Override
    public String getText() {
        return "";
    }

    public String getPath() {
        return this.path;
    }

    public boolean isComponentJson() {
        return this.componentJson;
    }

    @Override
    public Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
        if (serverCommandSource == null || this.parsedPath == null) {
            return new TextComponent("");
        }
        Stream<String> stream = this.resolve(serverCommandSource).flatMap(compoundTag -> {
            try {
                return this.parsedPath.get((Tag)compoundTag).stream();
            } catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(Tag::asString);
        if (this.componentJson) {
            return stream.flatMap(string -> {
                try {
                    Component component = Component.Serializer.fromJsonString(string);
                    return Stream.of(Components.resolveAndStyle(serverCommandSource, component, entity));
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: " + string, (Throwable)exception);
                    return Stream.of(new Component[0]);
                }
            }).reduce((component, component2) -> component.append(", ").append((Component)component2)).orElse(new TextComponent(""));
        }
        return new TextComponent(Joiner.on(", ").join(stream.iterator()));
    }

    public static class BlockPosArgument
    extends NbtComponent {
        private final String pos;
        @Nullable
        private final PosArgument parsedPos;

        public BlockPosArgument(String string, boolean bl, String string2) {
            super(string, bl);
            this.pos = string2;
            this.parsedPos = this.parsePos(this.pos);
        }

        @Nullable
        private PosArgument parsePos(String string) {
            try {
                return BlockPosArgumentType.create().method_9699(new StringReader(string));
            } catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private BlockPosArgument(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable PosArgument posArgument) {
            super(string, nbtPath, bl);
            this.pos = string2;
            this.parsedPos = posArgument;
        }

        @Nullable
        public String getPos() {
            return this.pos;
        }

        @Override
        public Component copyShallow() {
            return new BlockPosArgument(this.path, this.parsedPath, this.componentJson, this.pos, this.parsedPos);
        }

        @Override
        protected Stream<CompoundTag> resolve(ServerCommandSource serverCommandSource) {
            BlockEntity blockEntity;
            BlockPos blockPos;
            ServerWorld serverWorld;
            if (this.parsedPos != null && (serverWorld = serverCommandSource.getWorld()).isHeightValidAndBlockLoaded(blockPos = this.parsedPos.toAbsoluteBlockPos(serverCommandSource)) && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
                return Stream.of(blockEntity.toTag(new CompoundTag()));
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BlockPosArgument) {
                BlockPosArgument blockPosArgument = (BlockPosArgument)object;
                return Objects.equals(this.pos, blockPosArgument.pos) && Objects.equals(this.path, blockPosArgument.path) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.pos + '\'' + "path='" + this.path + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }

    public static class EntityNbtComponent
    extends NbtComponent {
        private final String selector;
        @Nullable
        private final EntitySelector parsedSelector;

        public EntityNbtComponent(String string, boolean bl, String string2) {
            super(string, bl);
            this.selector = string2;
            this.parsedSelector = EntityNbtComponent.parseSelector(string2);
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

        private EntityNbtComponent(String string, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean bl, String string2, @Nullable EntitySelector entitySelector) {
            super(string, nbtPath, bl);
            this.selector = string2;
            this.parsedSelector = entitySelector;
        }

        public String getSelector() {
            return this.selector;
        }

        @Override
        public Component copyShallow() {
            return new EntityNbtComponent(this.path, this.parsedPath, this.componentJson, this.selector, this.parsedSelector);
        }

        @Override
        protected Stream<CompoundTag> resolve(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
            if (this.parsedSelector != null) {
                List<? extends Entity> list = this.parsedSelector.getEntities(serverCommandSource);
                return list.stream().map(NbtPredicate::entityToTag);
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof EntityNbtComponent) {
                EntityNbtComponent entityNbtComponent = (EntityNbtComponent)object;
                return Objects.equals(this.selector, entityNbtComponent.selector) && Objects.equals(this.path, entityNbtComponent.path) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.selector + '\'' + "path='" + this.path + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
}

