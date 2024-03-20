package net.minecraft.command.argument.packrat;

public interface Cut {
	Cut NOOP = () -> {
	};

	void cut();
}
