package net.minecraft;

import java.util.Arrays;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface class_4001 {
	class_4002 register(Collection<Identifier> collection);

	default class_4002 method_18137(Identifier... identifiers) {
		return this.register(Arrays.asList(identifiers));
	}
}
