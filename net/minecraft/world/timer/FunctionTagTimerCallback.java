/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.timer;

import java.util.Collection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;

public class FunctionTagTimerCallback
implements TimerCallback<MinecraftServer> {
    final Identifier name;

    public FunctionTagTimerCallback(Identifier name) {
        this.name = name;
    }

    @Override
    public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
        CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
        Collection<CommandFunction> collection = commandFunctionManager.getTag(this.name);
        for (CommandFunction commandFunction : collection) {
            commandFunctionManager.execute(commandFunction, commandFunctionManager.getScheduledCommandSource());
        }
    }

    @Override
    public /* synthetic */ void call(Object server, Timer events, long time) {
        this.call((MinecraftServer)server, (Timer<MinecraftServer>)events, time);
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
        public Serializer() {
            super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
        }

        @Override
        public void serialize(NbtCompound nbtCompound, FunctionTagTimerCallback functionTagTimerCallback) {
            nbtCompound.putString("Name", functionTagTimerCallback.name.toString());
        }

        @Override
        public FunctionTagTimerCallback deserialize(NbtCompound nbtCompound) {
            Identifier identifier = new Identifier(nbtCompound.getString("Name"));
            return new FunctionTagTimerCallback(identifier);
        }

        @Override
        public /* synthetic */ TimerCallback deserialize(NbtCompound nbt) {
            return this.deserialize(nbt);
        }
    }
}

