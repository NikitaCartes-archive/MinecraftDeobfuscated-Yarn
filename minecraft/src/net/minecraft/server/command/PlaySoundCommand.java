package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.playsound.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		RequiredArgumentBuilder<ServerCommandSource, Identifier> requiredArgumentBuilder = CommandManager.argument("sound", IdentifierArgumentType.identifier())
			.suggests(SuggestionProviders.AVAILABLE_SOUNDS);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(makeArgumentsForCategory(soundCategory));
		}

		dispatcher.register(CommandManager.literal("playsound").requires(source -> source.hasPermissionLevel(2)).then(requiredArgumentBuilder));
	}

	private static LiteralArgumentBuilder<ServerCommandSource> makeArgumentsForCategory(SoundCategory category) {
		return CommandManager.literal(category.getName())
			.then(
				CommandManager.argument("targets", EntityArgumentType.players())
					.executes(
						context -> execute(
								context.getSource(),
								EntityArgumentType.getPlayers(context, "targets"),
								IdentifierArgumentType.getIdentifier(context, "sound"),
								category,
								context.getSource().getPosition(),
								1.0F,
								1.0F,
								0.0F
							)
					)
					.then(
						CommandManager.argument("pos", Vec3ArgumentType.vec3())
							.executes(
								context -> execute(
										context.getSource(),
										EntityArgumentType.getPlayers(context, "targets"),
										IdentifierArgumentType.getIdentifier(context, "sound"),
										category,
										Vec3ArgumentType.getVec3(context, "pos"),
										1.0F,
										1.0F,
										0.0F
									)
							)
							.then(
								CommandManager.argument("volume", FloatArgumentType.floatArg(0.0F))
									.executes(
										context -> execute(
												context.getSource(),
												EntityArgumentType.getPlayers(context, "targets"),
												IdentifierArgumentType.getIdentifier(context, "sound"),
												category,
												Vec3ArgumentType.getVec3(context, "pos"),
												context.<Float>getArgument("volume", Float.class),
												1.0F,
												0.0F
											)
									)
									.then(
										CommandManager.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
											.executes(
												context -> execute(
														context.getSource(),
														EntityArgumentType.getPlayers(context, "targets"),
														IdentifierArgumentType.getIdentifier(context, "sound"),
														category,
														Vec3ArgumentType.getVec3(context, "pos"),
														context.<Float>getArgument("volume", Float.class),
														context.<Float>getArgument("pitch", Float.class),
														0.0F
													)
											)
											.then(
												CommandManager.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
													.executes(
														context -> execute(
																context.getSource(),
																EntityArgumentType.getPlayers(context, "targets"),
																IdentifierArgumentType.getIdentifier(context, "sound"),
																category,
																Vec3ArgumentType.getVec3(context, "pos"),
																context.<Float>getArgument("volume", Float.class),
																context.<Float>getArgument("pitch", Float.class),
																context.<Float>getArgument("minVolume", Float.class)
															)
													)
											)
									)
							)
					)
			);
	}

	private static int execute(
		ServerCommandSource source,
		Collection<ServerPlayerEntity> targets,
		Identifier sound,
		SoundCategory category,
		Vec3d pos,
		float volume,
		float pitch,
		float minVolume
	) throws CommandSyntaxException {
		RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvent.of(sound));
		double d = (double)MathHelper.square(registryEntry.value().getDistanceToTravel(volume));
		int i = 0;
		long l = source.getWorld().getRandom().nextLong();

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			double e = pos.x - serverPlayerEntity.getX();
			double f = pos.y - serverPlayerEntity.getY();
			double g = pos.z - serverPlayerEntity.getZ();
			double h = e * e + f * f + g * g;
			Vec3d vec3d = pos;
			float j = volume;
			if (h > d) {
				if (minVolume <= 0.0F) {
					continue;
				}

				double k = Math.sqrt(h);
				vec3d = new Vec3d(serverPlayerEntity.getX() + e / k * 2.0, serverPlayerEntity.getY() + f / k * 2.0, serverPlayerEntity.getZ() + g / k * 2.0);
				j = minVolume;
			}

			serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry, category, vec3d.getX(), vec3d.getY(), vec3d.getZ(), j, pitch, l));
			i++;
		}

		if (i == 0) {
			throw FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable("commands.playsound.success.single", sound, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true
				);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.playsound.success.multiple", sound, targets.size()), true);
			}

			return i;
		}
	}
}
