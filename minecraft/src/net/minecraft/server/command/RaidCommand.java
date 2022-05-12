package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;

public class RaidCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("raid")
				.requires(source -> source.hasPermissionLevel(3))
				.then(
					CommandManager.literal("start")
						.then(
							CommandManager.argument("omenlvl", IntegerArgumentType.integer(0))
								.executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "omenlvl")))
						)
				)
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
				.then(CommandManager.literal("check").executes(context -> executeCheck(context.getSource())))
				.then(
					CommandManager.literal("sound")
						.then(
							CommandManager.argument("type", TextArgumentType.text())
								.executes(context -> executeSound(context.getSource(), TextArgumentType.getTextArgument(context, "type")))
						)
				)
				.then(CommandManager.literal("spawnleader").executes(context -> executeSpawnLeader(context.getSource())))
				.then(
					CommandManager.literal("setomen")
						.then(
							CommandManager.argument("level", IntegerArgumentType.integer(0))
								.executes(context -> executeSetOmen(context.getSource(), IntegerArgumentType.getInteger(context, "level")))
						)
				)
				.then(CommandManager.literal("glow").executes(context -> executeGlow(context.getSource())))
		);
	}

	private static int executeGlow(ServerCommandSource source) throws CommandSyntaxException {
		Raid raid = getRaid(source.getPlayerOrThrow());
		if (raid != null) {
			for (RaiderEntity raiderEntity : raid.getAllRaiders()) {
				raiderEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000, 1));
			}
		}

		return 1;
	}

	private static int executeSetOmen(ServerCommandSource source, int level) throws CommandSyntaxException {
		Raid raid = getRaid(source.getPlayerOrThrow());
		if (raid != null) {
			int i = raid.getMaxAcceptableBadOmenLevel();
			if (level > i) {
				source.sendError(Text.literal("Sorry, the max bad omen level you can set is " + i));
			} else {
				int j = raid.getBadOmenLevel();
				raid.setBadOmenLevel(level);
				source.sendFeedback(Text.literal("Changed village's bad omen level from " + j + " to " + level), false);
			}
		} else {
			source.sendError(Text.literal("No raid found here"));
		}

		return 1;
	}

	private static int executeSpawnLeader(ServerCommandSource source) {
		source.sendFeedback(Text.literal("Spawned a raid captain"), false);
		RaiderEntity raiderEntity = EntityType.PILLAGER.create(source.getWorld());
		raiderEntity.setPatrolLeader(true);
		raiderEntity.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
		raiderEntity.setPosition(source.getPosition().x, source.getPosition().y, source.getPosition().z);
		raiderEntity.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(new BlockPos(source.getPosition())), SpawnReason.COMMAND, null, null);
		source.getWorld().spawnEntityAndPassengers(raiderEntity);
		return 1;
	}

	private static int executeSound(ServerCommandSource source, Text type) {
		if (type != null && type.getString().equals("local")) {
			source.getWorld().playSound(null, new BlockPos(source.getPosition().add(5.0, 0.0, 0.0)), SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
		}

		return 1;
	}

	private static int executeStart(ServerCommandSource source, int level) throws CommandSyntaxException {
		ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
		BlockPos blockPos = serverPlayerEntity.getBlockPos();
		if (serverPlayerEntity.getWorld().hasRaidAt(blockPos)) {
			source.sendError(Text.literal("Raid already started close by"));
			return -1;
		} else {
			RaidManager raidManager = serverPlayerEntity.getWorld().getRaidManager();
			Raid raid = raidManager.startRaid(serverPlayerEntity);
			if (raid != null) {
				raid.setBadOmenLevel(level);
				raidManager.markDirty();
				source.sendFeedback(Text.literal("Created a raid in your local village"), false);
			} else {
				source.sendError(Text.literal("Failed to create a raid in your local village"));
			}

			return 1;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
		BlockPos blockPos = serverPlayerEntity.getBlockPos();
		Raid raid = serverPlayerEntity.getWorld().getRaidAt(blockPos);
		if (raid != null) {
			raid.invalidate();
			source.sendFeedback(Text.literal("Stopped raid"), false);
			return 1;
		} else {
			source.sendError(Text.literal("No raid here"));
			return -1;
		}
	}

	private static int executeCheck(ServerCommandSource source) throws CommandSyntaxException {
		Raid raid = getRaid(source.getPlayerOrThrow());
		if (raid != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Found a started raid! ");
			source.sendFeedback(Text.literal(stringBuilder.toString()), false);
			stringBuilder = new StringBuilder();
			stringBuilder.append("Num groups spawned: ");
			stringBuilder.append(raid.getGroupsSpawned());
			stringBuilder.append(" Bad omen level: ");
			stringBuilder.append(raid.getBadOmenLevel());
			stringBuilder.append(" Num mobs: ");
			stringBuilder.append(raid.getRaiderCount());
			stringBuilder.append(" Raid health: ");
			stringBuilder.append(raid.getCurrentRaiderHealth());
			stringBuilder.append(" / ");
			stringBuilder.append(raid.getTotalHealth());
			source.sendFeedback(Text.literal(stringBuilder.toString()), false);
			return 1;
		} else {
			source.sendError(Text.literal("Found no started raids"));
			return 0;
		}
	}

	@Nullable
	private static Raid getRaid(ServerPlayerEntity player) {
		return player.getWorld().getRaidAt(player.getBlockPos());
	}
}
