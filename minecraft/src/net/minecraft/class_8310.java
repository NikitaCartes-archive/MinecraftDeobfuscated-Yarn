package net.minecraft;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

public abstract class class_8310 extends class_8287<Block> {
	protected final Block field_43754;

	public class_8310(Block block) {
		super(RegistryKeys.BLOCK, block.getRegistryEntry().registryKey());
		this.field_43754 = block;
	}

	public Block method_50317() {
		return this.method_50193() == this.method_50200()
			? this.field_43754
			: (Block)Objects.requireNonNullElse(Registries.BLOCK.get(this.method_50193()), this.field_43754);
	}
}
