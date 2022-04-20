/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityAnchorArgumentType
implements ArgumentType<EntityAnchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(name -> Text.method_43469("argument.anchor.invalid", name));

    public static EntityAnchor getEntityAnchor(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, EntityAnchor.class);
    }

    public static EntityAnchorArgumentType entityAnchor() {
        return new EntityAnchorArgumentType();
    }

    @Override
    public EntityAnchor parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        EntityAnchor entityAnchor = EntityAnchor.fromId(string);
        if (entityAnchor == null) {
            stringReader.setCursor(i);
            throw INVALID_ANCHOR_EXCEPTION.createWithContext(stringReader, string);
        }
        return entityAnchor;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EntityAnchor.ANCHORS.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static enum EntityAnchor {
        FEET("feet", (pos, entity) -> pos),
        EYES("eyes", (pos, entity) -> new Vec3d(pos.x, pos.y + (double)entity.getStandingEyeHeight(), pos.z));

        static final Map<String, EntityAnchor> ANCHORS;
        private final String id;
        private final BiFunction<Vec3d, Entity, Vec3d> offset;

        private EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
            this.id = id;
            this.offset = offset;
        }

        @Nullable
        public static EntityAnchor fromId(String id) {
            return ANCHORS.get(id);
        }

        public Vec3d positionAt(Entity entity) {
            return this.offset.apply(entity.getPos(), entity);
        }

        public Vec3d positionAt(ServerCommandSource source) {
            Entity entity = source.getEntity();
            if (entity == null) {
                return source.getPosition();
            }
            return this.offset.apply(source.getPosition(), entity);
        }

        static {
            ANCHORS = Util.make(Maps.newHashMap(), map -> {
                for (EntityAnchor entityAnchor : EntityAnchor.values()) {
                    map.put(entityAnchor.id, entityAnchor);
                }
            });
        }
    }
}

