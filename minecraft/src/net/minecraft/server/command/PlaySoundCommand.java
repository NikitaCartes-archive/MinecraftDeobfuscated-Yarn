package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.playsound.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		RequiredArgumentBuilder<ServerCommandSource, Identifier> requiredArgumentBuilder = CommandManager.argument("sound", IdentifierArgumentType.identifier())
			.suggests(SuggestionProviders.AVAILABLE_SOUNDS);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(makeArgumentsForCategory(soundCategory));
		}

		commandDispatcher.register(
			CommandManager.literal("playsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(requiredArgumentBuilder)
		);
	}

	private static LiteralArgumentBuilder<ServerCommandSource> makeArgumentsForCategory(SoundCategory soundCategory) {
		return CommandManager.literal(soundCategory.getName())
			.then(
				CommandManager.argument("targets", EntityArgumentType.players())
					.executes(
						commandContext -> execute(
								commandContext.getSource(),
								EntityArgumentType.getPlayers(commandContext, "targets"),
								IdentifierArgumentType.getIdentifier(commandContext, "sound"),
								soundCategory,
								commandContext.getSource().getPosition(),
								1.0F,
								1.0F,
								0.0F
							)
					)
					.then(
						CommandManager.argument("pos", Vec3ArgumentType.vec3())
							.executes(
								commandContext -> execute(
										commandContext.getSource(),
										EntityArgumentType.getPlayers(commandContext, "targets"),
										IdentifierArgumentType.getIdentifier(commandContext, "sound"),
										soundCategory,
										Vec3ArgumentType.getVec3(commandContext, "pos"),
										1.0F,
										1.0F,
										0.0F
									)
							)
							.then(
								CommandManager.argument("volume", FloatArgumentType.floatArg(0.0F))
									.executes(
										commandContext -> execute(
												commandContext.getSource(),
												EntityArgumentType.getPlayers(commandContext, "targets"),
												IdentifierArgumentType.getIdentifier(commandContext, "sound"),
												soundCategory,
												Vec3ArgumentType.getVec3(commandContext, "pos"),
												commandContext.<Float>getArgument("volume", Float.class),
												1.0F,
												0.0F
											)
									)
									.then(
										CommandManager.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
											.executes(
												commandContext -> execute(
														commandContext.getSource(),
														EntityArgumentType.getPlayers(commandContext, "targets"),
														IdentifierArgumentType.getIdentifier(commandContext, "sound"),
														soundCategory,
														Vec3ArgumentType.getVec3(commandContext, "pos"),
														commandContext.<Float>getArgument("volume", Float.class),
														commandContext.<Float>getArgument("pitch", Float.class),
														0.0F
													)
											)
											.then(
												CommandManager.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
													.executes(
														commandContext -> execute(
																commandContext.getSource(),
																EntityArgumentType.getPlayers(commandContext, "targets"),
																IdentifierArgumentType.getIdentifier(commandContext, "sound"),
																soundCategory,
																Vec3ArgumentType.getVec3(commandContext, "pos"),
																commandContext.<Float>getArgument("volume", Float.class),
																commandContext.<Float>getArgument("pitch", Float.class),
																commandContext.<Float>getArgument("minVolume", Float.class)
															)
													)
											)
									)
							)
					)
			);
	}

	private static int execute(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		Identifier identifier,
		SoundCategory soundCategory,
		Vec3d vec3d,
		float f,
		float g,
		float h
	) throws CommandSyntaxException {
		double d = Math.pow(f > 1.0F ? (double)(f * 16.0F) : 16.0, 2.0);
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			double e = vec3d.x - serverPlayerEntity.x;
			double j = vec3d.y - serverPlayerEntity.y;
			double k = vec3d.z - serverPlayerEntity.z;
			double l = e * e + j * j + k * k;
			Vec3d vec3d2 = vec3d;
			float m = f;
			if (l > d) {
				if (h <= 0.0F) {
					continue;
				}

				double n = (double)MathHelper.sqrt(l);
				vec3d2 = new Vec3d(serverPlayerEntity.x + e / n * 2.0, serverPlayerEntity.y + j / n * 2.0, serverPlayerEntity.z + k / n * 2.0);
				m = h;
			}

			serverPlayerEntity.networkHandler.sendPacket(new PlaySoundIdS2CPacket(identifier, soundCategory, vec3d2, m, g));
			i++;
		}

		if (i == 0) {
			throw FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableText("commands.playsound.success.single", identifier, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableText("commands.playsound.success.single", identifier, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
				);
			}

			return i;
		}
	}
}
