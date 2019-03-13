package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FunctionTagTimerCallback implements TimerCallback<MinecraftServer> {
	private final Identifier field_1303;

	public FunctionTagTimerCallback(Identifier identifier) {
		this.field_1303 = identifier;
	}

	public void method_962(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.method_3740();
		Tag<CommandFunction> tag = commandFunctionManager.method_12901().getOrCreate(this.field_1303);

		for (CommandFunction commandFunction : tag.values()) {
			commandFunctionManager.execute(commandFunction, commandFunctionManager.getFunctionCommandSource());
		}
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
		public Serializer() {
			super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
		}

		public void method_964(CompoundTag compoundTag, FunctionTagTimerCallback functionTagTimerCallback) {
			compoundTag.putString("Name", functionTagTimerCallback.field_1303.toString());
		}

		public FunctionTagTimerCallback method_965(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new FunctionTagTimerCallback(identifier);
		}
	}
}
