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
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Vec3d;

public class EntityAnchorArgumentType implements ArgumentType<EntityAnchorArgumentType.EntityAnchor> {
	private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.anchor.invalid", object)
	);

	public static EntityAnchorArgumentType.EntityAnchor getEntityAnchor(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, EntityAnchorArgumentType.EntityAnchor.class);
	}

	public static EntityAnchorArgumentType create() {
		return new EntityAnchorArgumentType();
	}

	public EntityAnchorArgumentType.EntityAnchor method_9292(StringReader stringReader) throws CommandSyntaxException {
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
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(EntityAnchorArgumentType.EntityAnchor.anchors.keySet(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static enum EntityAnchor {
		field_9853("feet", (vec3d, entity) -> vec3d),
		field_9851("eyes", (vec3d, entity) -> new Vec3d(vec3d.x, vec3d.y + (double)entity.getStandingEyeHeight(), vec3d.z));

		private static final Map<String, EntityAnchorArgumentType.EntityAnchor> anchors = SystemUtil.consume(
			Maps.<String, EntityAnchorArgumentType.EntityAnchor>newHashMap(), hashMap -> {
				for (EntityAnchorArgumentType.EntityAnchor entityAnchor : values()) {
					hashMap.put(entityAnchor.id, entityAnchor);
				}
			}
		);
		private final String id;
		private final BiFunction<Vec3d, Entity, Vec3d> offset;

		private EntityAnchor(String string2, BiFunction<Vec3d, Entity, Vec3d> biFunction) {
			this.id = string2;
			this.offset = biFunction;
		}

		@Nullable
		public static EntityAnchorArgumentType.EntityAnchor fromId(String string) {
			return (EntityAnchorArgumentType.EntityAnchor)anchors.get(string);
		}

		public Vec3d positionAt(Entity entity) {
			return (Vec3d)this.offset.apply(new Vec3d(entity.x, entity.y, entity.z), entity);
		}

		public Vec3d positionAt(ServerCommandSource serverCommandSource) {
			Entity entity = serverCommandSource.getEntity();
			return entity == null ? serverCommandSource.getPosition() : (Vec3d)this.offset.apply(serverCommandSource.getPosition(), entity);
		}
	}
}
