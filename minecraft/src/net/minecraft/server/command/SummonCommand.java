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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SummonCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.summon.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("summon")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("entity", EntitySummonArgumentType.create())
						.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
						.executes(
							commandContext -> execute(
									commandContext.getSource(),
									EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
									commandContext.getSource().getPosition(),
									new CompoundTag(),
									true
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
											Vec3ArgumentType.getVec3(commandContext, "pos"),
											new CompoundTag(),
											true
										)
								)
								.then(
									CommandManager.argument("nbt", NbtCompoundTagArgumentType.create())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													EntitySummonArgumentType.getEntitySummon(commandContext, "entity"),
													Vec3ArgumentType.getVec3(commandContext, "pos"),
													NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt"),
													false
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Identifier identifier, Vec3d vec3d, CompoundTag compoundTag, boolean bl) throws CommandSyntaxException {
		CompoundTag compoundTag2 = compoundTag.method_10553();
		compoundTag2.putString("id", identifier.toString());
		if (EntityType.getId(EntityType.field_6112).equals(identifier)) {
			LightningEntity lightningEntity = new LightningEntity(serverCommandSource.getWorld(), vec3d.x, vec3d.y, vec3d.z, false);
			serverCommandSource.getWorld().addLightning(lightningEntity);
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.summon.success", lightningEntity.getDisplayName()), true);
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
						.initialize(serverCommandSource.getWorld(), serverCommandSource.getWorld().getLocalDifficulty(new BlockPos(entity)), SpawnType.field_16462, null, null);
				}

				serverCommandSource.sendFeedback(new TranslatableComponent("commands.summon.success", entity.getDisplayName()), true);
				return 1;
			}
		}
	}
}
