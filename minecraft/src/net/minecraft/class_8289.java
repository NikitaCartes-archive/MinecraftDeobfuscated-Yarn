package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;

public interface class_8289 {
	Codec<class_8291> method_50120();

	Stream<class_8291> method_50119();

	default Stream<class_8291> method_50204() {
		return this.method_50119();
	}

	Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i);

	default int method_50203(boolean bl) {
		List<class_8291> list = (bl ? this.method_50119() : this.method_50204()).toList();
		list.forEach(arg -> arg.method_50122(class_8290.REPEAL));
		return list.size();
	}

	static <T extends class_8291> Codec<class_8291> method_50202(Codec<T> codec) {
		return codec;
	}
}
