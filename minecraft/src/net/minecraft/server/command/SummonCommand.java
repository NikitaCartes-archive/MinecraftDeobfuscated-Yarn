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
									EntitySummonArgumentType.method_9322(commandContext, "entity"),
									commandContext.getSource().method_9222(),
									new CompoundTag(),
									true
								)
						)
						.then(
							ServerCommandManager.argument("pos", Vec3ArgumentType.create())
								.executes(
									commandContext -> method_13694(
											commandContext.getSource(),
											EntitySummonArgumentType.method_9322(commandContext, "entity"),
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
													EntitySummonArgumentType.method_9322(commandContext, "entity"),
													Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
													NbtCompoundTagArgumentType.method_9285(commandContext, "nbt"),
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
		if (EntityType.method_5890(EntityType.LIGHTNING_BOLT).equals(identifier)) {
			LightningEntity lightningEntity = new LightningEntity(serverCommandSource.method_9225(), vec3d.x, vec3d.y, vec3d.z, false);
			serverCommandSource.method_9225().addLightning(lightningEntity);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.summon.success", lightningEntity.method_5476()), true);
			return 1;
		} else {
			ServerWorld serverWorld = serverCommandSource.method_9225();
			Entity entity = EntityType.method_17842(compoundTag2, serverWorld, entityx -> {
				entityx.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entityx.yaw, entityx.pitch);
				return !serverWorld.method_18768(entityx) ? null : entityx;
			});
			if (entity == null) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (bl && entity instanceof MobEntity) {
					((MobEntity)entity)
						.method_5943(serverCommandSource.method_9225(), serverCommandSource.method_9225().method_8404(new BlockPos(entity)), SpawnType.field_16462, null, null);
				}

				serverCommandSource.method_9226(new TranslatableTextComponent("commands.summon.success", entity.method_5476()), true);
				return 1;
			}
		}
	}
}
