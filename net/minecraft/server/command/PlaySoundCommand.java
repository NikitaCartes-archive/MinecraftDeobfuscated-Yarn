/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.playsound.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        RequiredArgumentBuilder<ServerCommandSource, Identifier> requiredArgumentBuilder = CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            requiredArgumentBuilder.then(PlaySoundCommand.makeArgumentsForCategory(soundCategory));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("playsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(requiredArgumentBuilder));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeArgumentsForCategory(SoundCategory soundCategory) {
        return (LiteralArgumentBuilder)CommandManager.literal(soundCategory.getName()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "sound"), soundCategory, ((ServerCommandSource)commandContext.getSource()).getPosition(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "sound"), soundCategory, Vec3ArgumentType.getVec3(commandContext, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("volume", FloatArgumentType.floatArg(0.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "sound"), soundCategory, Vec3ArgumentType.getVec3(commandContext, "pos"), commandContext.getArgument("volume", Float.class).floatValue(), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pitch", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "sound"), soundCategory, Vec3ArgumentType.getVec3(commandContext, "pos"), commandContext.getArgument("volume", Float.class).floatValue(), commandContext.getArgument("pitch", Float.class).floatValue(), 0.0f))).then(CommandManager.argument("minVolume", FloatArgumentType.floatArg(0.0f, 1.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "sound"), soundCategory, Vec3ArgumentType.getVec3(commandContext, "pos"), commandContext.getArgument("volume", Float.class).floatValue(), commandContext.getArgument("pitch", Float.class).floatValue(), commandContext.getArgument("minVolume", Float.class).floatValue())))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Identifier identifier, SoundCategory soundCategory, Vec3d vec3d, float f, float g, float h) throws CommandSyntaxException {
        double d = Math.pow(f > 1.0f ? (double)(f * 16.0f) : 16.0, 2.0);
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            double e = vec3d.x - serverPlayerEntity.x;
            double j = vec3d.y - serverPlayerEntity.y;
            double k = vec3d.z - serverPlayerEntity.z;
            double l = e * e + j * j + k * k;
            Vec3d vec3d2 = vec3d;
            float m = f;
            if (l > d) {
                if (h <= 0.0f) continue;
                double n = MathHelper.sqrt(l);
                vec3d2 = new Vec3d(serverPlayerEntity.x + e / n * 2.0, serverPlayerEntity.y + j / n * 2.0, serverPlayerEntity.z + k / n * 2.0);
                m = h;
            }
            serverPlayerEntity.networkHandler.sendPacket(new PlaySoundIdS2CPacket(identifier, soundCategory, vec3d2, m, g));
            ++i;
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.playsound.success.single", identifier, collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.playsound.success.single", identifier, collection.iterator().next().getDisplayName()), true);
        }
        return i;
    }
}

