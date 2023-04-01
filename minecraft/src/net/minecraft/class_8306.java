package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public abstract class class_8306<V> extends class_8275<RegistryKey<Block>, V> {
	private final Map<RegistryKey<Block>, V> field_43751 = new HashMap();

	public class_8306(Codec<V> codec) {
		super(RegistryKey.createCodec(RegistryKeys.BLOCK), codec);
	}

	@Nullable
	public V method_50305(Block block) {
		return (V)this.field_43751.get(block.getRegistryEntry().registryKey());
	}

	protected void method_50138(RegistryKey<Block> registryKey, V object) {
		this.field_43751.put(registryKey, object);
	}

	protected void method_50136(RegistryKey<Block> registryKey) {
		this.field_43751.remove(registryKey);
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43751.entrySet().stream().map(entry -> new class_8275.class_8276((RegistryKey)entry.getKey(), entry.getValue()));
	}
}
