package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public abstract class class_8346 extends class_8294<class_8345> {
	private final List<UUID> field_43919 = new ArrayList();
	private final Set<UUID> field_43920 = new HashSet();

	protected boolean method_50231(class_8345 arg) {
		boolean bl = super.method_50231(arg);
		if (bl) {
			this.field_43920.add(arg.id());
			this.field_43919.add(arg.id());
		}

		return bl;
	}

	@Override
	protected Codec<class_8345> method_50185() {
		return class_8345.field_43918;
	}

	protected boolean method_50258(class_8345 arg) {
		boolean bl = super.method_50258(arg);
		if (bl) {
			this.field_43920.remove(arg.id());
			this.field_43919.remove(arg.id());
		}

		return bl;
	}

	public boolean method_50415(UUID uUID) {
		return this.field_43920.contains(uUID);
	}

	public List<UUID> method_50417() {
		return this.field_43919;
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		ObjectArrayList<ServerPlayerEntity> objectArrayList = new ObjectArrayList<>(minecraftServer.getPlayerManager().getPlayerList());
		Util.shuffle(objectArrayList, random);
		return objectArrayList.stream().limit((long)i).map(serverPlayerEntity -> new class_8294.class_8295(class_8345.method_50412(serverPlayerEntity)));
	}
}
