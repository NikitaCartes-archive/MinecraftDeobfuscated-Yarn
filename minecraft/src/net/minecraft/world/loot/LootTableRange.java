package net.minecraft.world.loot;

import java.util.Random;
import net.minecraft.util.Identifier;

public interface LootTableRange {
	Identifier field_968 = new Identifier("constant");
	Identifier field_967 = new Identifier("uniform");
	Identifier field_969 = new Identifier("binomial");

	int next(Random random);

	Identifier method_365();
}
