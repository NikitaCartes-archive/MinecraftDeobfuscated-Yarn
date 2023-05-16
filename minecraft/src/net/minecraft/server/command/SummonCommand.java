package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
	private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid"));
	private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.summon.invalidPosition")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("summon")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("entity", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
						.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
						.executes(
							context -> execute(
									context.getSource(), RegistryEntryArgumentType.getSummonableEntityType(context, "entity"), context.getSource().getPosition(), new NbtCompound(), true
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.vec3())
								.executes(
									context -> execute(
											context.getSource(),
											RegistryEntryArgumentType.getSummonableEntityType(context, "entity"),
											Vec3ArgumentType.getVec3(context, "pos"),
											new NbtCompound(),
											true
										)
								)
								.then(
									CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
										.executes(
											context -> execute(
													context.getSource(),
													RegistryEntryArgumentType.getSummonableEntityType(context, "entity"),
													Vec3ArgumentType.getVec3(context, "pos"),
													NbtCompoundArgumentType.getNbtCompound(context, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	public static Entity summon(ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entityType, Vec3d pos, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
		BlockPos blockPos = BlockPos.ofFloored(pos);
		if (!World.isValid(blockPos)) {
			throw INVALID_POSITION_EXCEPTION.create();
		} else {
			NbtCompound nbtCompound = nbt.copy();
			nbtCompound.putString("id", entityType.registryKey().getValue().toString());
			ServerWorld serverWorld = source.getWorld();
			Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> {
				entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.getYaw(), entityx.getPitch());
				return entityx;
			});
			if (entity == null) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (initialize && entity instanceof MobEntity) {
					((MobEntity)entity).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity.getBlockPos()), SpawnReason.COMMAND, null, null);
				}

				if (!serverWorld.spawnNewEntityAndPassengers(entity)) {
					throw FAILED_UUID_EXCEPTION.create();
				} else {
					return entity;
				}
			}
		}
	}

	private static int execute(ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entityType, Vec3d pos, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
		Entity entity = summon(source, entityType, pos, nbt, initialize);
		source.sendFeedback(() -> Text.translatable("commands.summon.success", entity.getDisplayName()), true);
		return 1;
	}
}
