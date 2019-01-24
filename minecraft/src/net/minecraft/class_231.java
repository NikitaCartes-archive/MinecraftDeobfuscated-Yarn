package net.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class class_231 implements class_234<MinecraftServer> {
	private final Identifier field_1304;

	public class_231(Identifier identifier) {
		this.field_1304 = identifier;
	}

	public void method_967(MinecraftServer minecraftServer, class_236<MinecraftServer> arg, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		commandFunctionManager.getFunction(this.field_1304)
			.ifPresent(commandFunction -> commandFunctionManager.execute(commandFunction, commandFunctionManager.getFunctionCommandSource()));
	}

	public static class class_232 extends class_234.class_235<MinecraftServer, class_231> {
		public class_232() {
			super(new Identifier("function"), class_231.class);
		}

		public void method_968(CompoundTag compoundTag, class_231 arg) {
			compoundTag.putString("Name", arg.field_1304.toString());
		}

		public class_231 method_969(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new class_231(identifier);
		}
	}
}
