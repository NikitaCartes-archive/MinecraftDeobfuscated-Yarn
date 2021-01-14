package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed"));
	private static final SimpleCommandExceptionType field_26629 = new SimpleCommandExceptionType(new TranslatableText("commands.summon.failed.uuid"));
	private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.summon.invalidPosition")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("summon")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("entity", EntitySummonArgumentType.entitySummon())
						.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
						.executes(
							commandContext -> execute(
									commandContext.getSource(),
									EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
									commandContext.getSource().getPosition(),
									new NbtCompound(),
									true
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.vec3())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
											Vec3ArgumentType.getVec3(commandContext, "pos"),
											new NbtCompound(),
											true
										)
								)
								.then(
									CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
													Vec3ArgumentType.getVec3(commandContext, "pos"),
													NbtCompoundArgumentType.getNbtCompound(commandContext, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Identifier entity, Vec3d pos, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(pos);
		if (!World.isValid(blockPos)) {
			throw INVALID_POSITION_EXCEPTION.create();
		} else {
			NbtCompound nbtCompound = nbt.copy();
			nbtCompound.putString("id", entity.toString());
			ServerWorld serverWorld = source.getWorld();
			Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> {
				entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.yaw, entityx.pitch);
				return entityx;
			});
			if (entity2 == null) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (initialize && entity2 instanceof MobEntity) {
					((MobEntity)entity2).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity2.getBlockPos()), SpawnReason.COMMAND, null, null);
				}

				if (!serverWorld.shouldCreateNewEntityWithPassenger(entity2)) {
					throw field_26629.create();
				} else {
					source.sendFeedback(new TranslatableText("commands.summon.success", entity2.getDisplayName()), true);
					return 1;
				}
			}
		}
	}
}
