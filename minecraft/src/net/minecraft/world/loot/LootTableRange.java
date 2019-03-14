package net.minecraft.world.loot;

import java.util.Random;
import net.minecraft.util.Identifier;

public interface LootTableRange {
	Identifier CONSTANT = new Identifier("constant");
	Identifier UNIFORM = new Identifier("uniform");
	Identifier BINOMIAL = new Identifier("binomial");

	int next(Random random);

	Identifier getType();
}
