/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

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
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityAnchorArgumentType
implements ArgumentType<EntityAnchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.anchor.invalid", object));

    public static EntityAnchor getEntityAnchor(CommandContext<ServerCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, EntityAnchor.class);
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
        return CommandSource.suggestMatching(EntityAnchor.anchors.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static enum EntityAnchor {
        FEET("feet", (vec3d, entity) -> vec3d),
        EYES("eyes", (vec3d, entity) -> new Vec3d(vec3d.x, vec3d.y + (double)entity.getStandingEyeHeight(), vec3d.z));

        private static final Map<String, EntityAnchor> anchors;
        private final String id;
        private final BiFunction<Vec3d, Entity, Vec3d> offset;

        private EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
            this.id = id;
            this.offset = offset;
        }

        @Nullable
        public static EntityAnchor fromId(String id) {
            return anchors.get(id);
        }

        public Vec3d positionAt(Entity entity) {
            return this.offset.apply(entity.getPos(), entity);
        }

        public Vec3d positionAt(ServerCommandSource serverCommandSource) {
            Entity entity = serverCommandSource.getEntity();
            if (entity == null) {
                return serverCommandSource.getPosition();
            }
            return this.offset.apply(serverCommandSource.getPosition(), entity);
        }

        static {
            anchors = Util.make(Maps.newHashMap(), hashMap -> {
                for (EntityAnchor entityAnchor : EntityAnchor.values()) {
                    hashMap.put(entityAnchor.id, entityAnchor);
                }
            });
        }
    }
}

