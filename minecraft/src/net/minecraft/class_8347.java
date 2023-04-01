package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class class_8347 extends class_8294<Identifier> {
	private static final Set<Identifier> field_43921 = Set.of(
		new Identifier("wob"), new Identifier("m_banner_pattern"), new Identifier("string_concatenation"), new Identifier("diamond_drows")
	);

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return field_43921.stream().filter(identifier -> !this.method_50256(identifier)).limit((long)i).map(object -> new class_8294.class_8295(object));
	}

	@Override
	protected Codec<Identifier> method_50185() {
		return Identifier.CODEC;
	}

	public boolean method_50418(Identifier identifier) {
		return this.method_50256(identifier) ? true : !field_43921.contains(identifier);
	}

	protected Text method_50187(Identifier identifier) {
		return Text.translatable(identifier.toTranslationKey("rule.recipe"));
	}
}
