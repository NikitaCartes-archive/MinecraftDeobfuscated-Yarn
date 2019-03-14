package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.EntitySummonArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SummonCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.summon.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("summon")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("entity", EntitySummonArgumentType.create())
						.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
						.executes(
							commandContext -> method_13694(
									commandContext.getSource(),
									EntitySummonArgumentType.getSummonArgument(commandContext, "entity"),
									commandContext.getSource().getPosition(),
									new CompoundTag(),
									true
								)
						)
						.then(
							ServerCommandManager.argument("pos", Vec3ArgumentType.create())
								.executes(
									commandContext -> method_13694(
											commandContext.getSource(),
											EntitySummonArgumentType.getSummonArgument(commandContext, "entity"),
											Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
											new CompoundTag(),
											true
										)
								)
								.then(
									ServerCommandManager.argument("nbt", NbtCompoundTagArgumentType.create())
										.executes(
											commandContext -> method_13694(
													commandContext.getSource(),
													EntitySummonArgumentType.getSummonArgument(commandContext, "entity"),
													Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
													NbtCompoundTagArgumentType.getCompoundArgument(commandContext, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	private static int method_13694(ServerCommandSource serverCommandSource, Identifier identifier, Vec3d vec3d, CompoundTag compoundTag, boolean bl) throws CommandSyntaxException {
		CompoundTag compoundTag2 = compoundTag.method_10553();
		compoundTag2.putString("id", identifier.toString());
		if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(identifier)) {
			LightningEntity lightningEntity = new LightningEntity(serverCommandSource.getWorld(), vec3d.x, vec3d.y, vec3d.z, false);
			serverCommandSource.getWorld().addLightning(lightningEntity);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.summon.success", lightningEntity.getDisplayName()), true);
			return 1;
		} else {
			ServerWorld serverWorld = serverCommandSource.getWorld();
			Entity entity = EntityType.loadEntityWithPassengers(compoundTag2, serverWorld, entityx -> {
				entityx.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entityx.yaw, entityx.pitch);
				return !serverWorld.method_18768(entityx) ? null : entityx;
			});
			if (entity == null) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (bl && entity instanceof MobEntity) {
					((MobEntity)entity)
						.prepareEntityData(
							serverCommandSource.getWorld(), serverCommandSource.getWorld().getLocalDifficulty(new BlockPos(entity)), SpawnType.field_16462, null, null
						);
				}

				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.summon.success", entity.getDisplayName()), true);
				return 1;
			}
		}
	}
}
