package net.minecraft.server.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TransformationType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TransformCommand {
	private static final SimpleCommandExceptionType NOT_A_LIVING_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Target is not a living entity"));
	private static final SimpleCommandExceptionType MULTIPLE_PROFILES_EXCEPTION = new SimpleCommandExceptionType(
		Text.literal("Expected only one player for target skin")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("transform")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("into")
						.then(
							CommandManager.argument("entity", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
								.executes(context -> executeInto(context.getSource(), RegistryEntryArgumentType.getRegistryEntry(context, "entity", RegistryKeys.ENTITY_TYPE), null))
								.then(
									CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
										.executes(
											context -> executeInto(
													context.getSource(),
													RegistryEntryArgumentType.getRegistryEntry(context, "entity", RegistryKeys.ENTITY_TYPE),
													NbtCompoundArgumentType.getNbtCompound(context, "nbt")
												)
										)
								)
						)
						.then(
							CommandManager.literal("player")
								.then(
									CommandManager.argument("player", GameProfileArgumentType.gameProfile())
										.executes(context -> executeIntoPlayer(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "player")))
								)
						)
				)
				.then(
					CommandManager.literal("scale")
						.then(
							CommandManager.argument("scale", FloatArgumentType.floatArg(0.1F, 16.0F))
								.executes(context -> executeScale(context.getSource(), FloatArgumentType.getFloat(context, "scale")))
						)
				)
				.then(CommandManager.literal("clear").executes(context -> executeClear(context.getSource())))
		);
	}

	private static int executeInto(ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entity, @Nullable NbtCompound nbt) throws CommandSyntaxException {
		if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
			livingEntity.editTransformation(transformationType -> transformationType.withEntity(entity.value(), Optional.ofNullable(nbt)));
			source.sendFeedback(Text.literal("Transformed into ").append(entity.value().getName()), false);
			return 1;
		} else {
			throw NOT_A_LIVING_ENTITY_EXCEPTION.create();
		}
	}

	private static int executeIntoPlayer(ServerCommandSource source, Collection<GameProfile> profiles) throws CommandSyntaxException {
		if (profiles.size() != 1) {
			throw MULTIPLE_PROFILES_EXCEPTION.create();
		} else {
			ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
			SkullBlockEntity.loadProperties((GameProfile)profiles.iterator().next(), gameProfile -> {
				serverPlayerEntity.editTransformation(transformationType -> transformationType.withSkin(Optional.of((GameProfile)profiles.iterator().next())));
				source.sendFeedback(Text.literal("Applied skin of " + ((GameProfile)profiles.iterator().next()).getName()), false);
			});
			return 1;
		}
	}

	private static int executeScale(ServerCommandSource source, float scale) throws CommandSyntaxException {
		if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
			livingEntity.editTransformation(transformationType -> transformationType.withScale(scale));
			source.sendFeedback(Text.literal("Transformed scale by " + String.format("%.2f", scale) + "x"), false);
			return 1;
		} else {
			throw NOT_A_LIVING_ENTITY_EXCEPTION.create();
		}
	}

	private static int executeClear(ServerCommandSource source) throws CommandSyntaxException {
		if (source.getEntityOrThrow() instanceof LivingEntity livingEntity) {
			livingEntity.setTransformation(TransformationType.EMPTY);
			source.sendFeedback(Text.literal("Cleared transform"), false);
			return 1;
		} else {
			throw NOT_A_LIVING_ENTITY_EXCEPTION.create();
		}
	}
}
