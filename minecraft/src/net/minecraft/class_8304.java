package net.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8304 extends class_8306<class_8304.class_8305> {
	public class_8304() {
		super(class_8304.class_8305.field_43745);
	}

	protected Text method_50162(RegistryKey<Block> registryKey, class_8304.class_8305 arg) {
		return Text.translatable(arg.field_43747, Text.translatable(Util.createTranslationKey("block", registryKey.getValue())));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<Block> registry = minecraftServer.getRegistryManager().get(RegistryKeys.BLOCK);
		return registry.getRandom(random).stream().flatMap(reference -> {
			class_8304.class_8305 lv = this.method_50305((Block)reference.value());
			ObjectArrayList<class_8304.class_8305> objectArrayList = new ObjectArrayList<>(class_8304.class_8305.values());
			if (lv != null) {
				objectArrayList.remove(lv);
			}

			Util.shuffle(objectArrayList, random);
			return objectArrayList.stream().map(arg -> new class_8275.class_8276(reference.registryKey(), arg));
		}).limit((long)i).map(arg -> arg);
	}

	public static enum class_8305 implements StringIdentifiable {
		LOW("low", 5, 5),
		MEDIUM("medium", 15, 20),
		HIGH("high", 30, 60),
		VERY_HIGH("very_high", 60, 100);

		public static final com.mojang.serialization.Codec<class_8304.class_8305> field_43745 = StringIdentifiable.createCodec(class_8304.class_8305::values);
		private final String field_43746;
		final String field_43747;
		private final int field_43748;
		private final int field_43749;

		private class_8305(String string2, int j, int k) {
			this.field_43746 = string2;
			this.field_43747 = "rule.flammability." + string2;
			this.field_43748 = j;
			this.field_43749 = k;
		}

		@Override
		public String asString() {
			return this.field_43746;
		}

		public int method_50300() {
			return this.field_43749;
		}

		public int method_50301() {
			return this.field_43748;
		}
	}
}
