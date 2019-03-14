package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class FunctionTimerCallback implements TimerCallback<MinecraftServer> {
	private final Identifier name;

	public FunctionTimerCallback(Identifier identifier) {
		this.name = identifier;
	}

	public void method_967(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		commandFunctionManager.getFunction(this.name)
			.ifPresent(commandFunction -> commandFunctionManager.execute(commandFunction, commandFunctionManager.getFunctionCommandSource()));
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback> {
		public Serializer() {
			super(new Identifier("function"), FunctionTimerCallback.class);
		}

		public void method_968(CompoundTag compoundTag, FunctionTimerCallback functionTimerCallback) {
			compoundTag.putString("Name", functionTimerCallback.name.toString());
		}

		public FunctionTimerCallback method_969(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new FunctionTimerCallback(identifier);
		}
	}
}
