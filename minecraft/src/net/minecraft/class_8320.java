package net.minecraft;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8320 extends class_8269<RegistryKey<DamageType>> {
	private final String field_43806;

	public class_8320(String string, int i, int j) {
		super(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE), i, j);
		this.field_43806 = string;
	}

	@Override
	protected Stream<RegistryKey<DamageType>> method_50140(MinecraftServer minecraftServer, Random random) {
		Registry<DamageType> registry = minecraftServer.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);
		return Stream.generate(() -> registry.getRandom(random).map(RegistryEntry.Reference::registryKey)).flatMap(Optional::stream);
	}

	public float method_50345(RegistryEntry<DamageType> registryEntry) {
		Optional<RegistryKey<DamageType>> optional = registryEntry.getKey();
		return optional.isPresent() ? this.method_50142((RegistryKey<DamageType>)optional.get()) : 1.0F;
	}

	protected Text method_50162(RegistryKey<DamageType> registryKey, Integer integer) {
		return Text.translatable(this.field_43806, registryKey.getValue().toShortTranslationKey(), Text.literal(method_50134(integer)));
	}
}
