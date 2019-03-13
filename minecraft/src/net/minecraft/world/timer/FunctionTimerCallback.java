package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class FunctionTimerCallback implements TimerCallback<MinecraftServer> {
	private final Identifier field_1304;

	public FunctionTimerCallback(Identifier identifier) {
		this.field_1304 = identifier;
	}

	public void method_967(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.method_3740();
		commandFunctionManager.getFunction(this.field_1304)
			.ifPresent(commandFunction -> commandFunctionManager.execute(commandFunction, commandFunctionManager.getFunctionCommandSource()));
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback> {
		public Serializer() {
			super(new Identifier("function"), FunctionTimerCallback.class);
		}

		public void method_968(CompoundTag compoundTag, FunctionTimerCallback functionTimerCallback) {
			compoundTag.putString("Name", functionTimerCallback.field_1304.toString());
		}

		public FunctionTimerCallback method_969(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new FunctionTimerCallback(identifier);
		}
	}
}
