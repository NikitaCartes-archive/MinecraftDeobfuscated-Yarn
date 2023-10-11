package net.minecraft.server.function;

import java.util.List;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.util.Identifier;

public interface Procedure<T> {
	Identifier id();

	List<SourcedCommandAction<T>> entries();
}
