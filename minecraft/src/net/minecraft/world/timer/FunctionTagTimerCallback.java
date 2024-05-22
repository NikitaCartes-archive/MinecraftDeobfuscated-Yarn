package net.minecraft.world.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class FunctionTagTimerCallback implements TimerCallback<MinecraftServer> {
	final Identifier name;

	public FunctionTagTimerCallback(Identifier name) {
		this.name = name;
	}

	public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();

		for (CommandFunction<ServerCommandSource> commandFunction : commandFunctionManager.getTag(this.name)) {
			commandFunctionManager.execute(commandFunction, commandFunctionManager.getScheduledCommandSource());
		}
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
		public Serializer() {
			super(Identifier.ofVanilla("function_tag"), FunctionTagTimerCallback.class);
		}

		public void serialize(NbtCompound nbtCompound, FunctionTagTimerCallback functionTagTimerCallback) {
			nbtCompound.putString("Name", functionTagTimerCallback.name.toString());
		}

		public FunctionTagTimerCallback deserialize(NbtCompound nbtCompound) {
			Identifier identifier = Identifier.of(nbtCompound.getString("Name"));
			return new FunctionTagTimerCallback(identifier);
		}
	}
}
