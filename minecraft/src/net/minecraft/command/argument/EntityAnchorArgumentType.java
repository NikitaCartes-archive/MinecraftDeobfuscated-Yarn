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
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class EntityAnchorArgumentType implements ArgumentType<EntityAnchorArgumentType.EntityAnchor> {
	private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(
		name -> new TranslatableText("argument.anchor.invalid", name)
	);

	public static EntityAnchorArgumentType.EntityAnchor getEntityAnchor(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, EntityAnchorArgumentType.EntityAnchor.class);
	}

	public static EntityAnchorArgumentType entityAnchor() {
		return new EntityAnchorArgumentType();
	}

	public EntityAnchorArgumentType.EntityAnchor parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		String string = stringReader.readUnquotedString();
		EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.EntityAnchor.fromId(string);
		if (entityAnchor == null) {
			stringReader.setCursor(i);
			throw INVALID_ANCHOR_EXCEPTION.createWithContext(stringReader, string);
		} else {
			return entityAnchor;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(EntityAnchorArgumentType.EntityAnchor.ANCHORS.keySet(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static enum EntityAnchor {
		FEET("feet", (pos, entity) -> pos),
		EYES("eyes", (pos, entity) -> new Vec3d(pos.x, pos.y + (double)entity.getStandingEyeHeight(), pos.z));

		static final Map<String, EntityAnchorArgumentType.EntityAnchor> ANCHORS = Util.make(
			Maps.<String, EntityAnchorArgumentType.EntityAnchor>newHashMap(), map -> {
				for (EntityAnchorArgumentType.EntityAnchor entityAnchor : values()) {
					map.put(entityAnchor.id, entityAnchor);
				}
			}
		);
		private final String id;
		private final BiFunction<Vec3d, Entity, Vec3d> offset;

		private EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
			this.id = id;
			this.offset = offset;
		}

		@Nullable
		public static EntityAnchorArgumentType.EntityAnchor fromId(String id) {
			return (EntityAnchorArgumentType.EntityAnchor)ANCHORS.get(id);
		}

		public Vec3d positionAt(Entity entity) {
			return (Vec3d)this.offset.apply(entity.getPos(), entity);
		}

		public Vec3d positionAt(ServerCommandSource source) {
			Entity entity = source.getEntity();
			return entity == null ? source.getPosition() : (Vec3d)this.offset.apply(source.getPosition(), entity);
		}
	}
}
