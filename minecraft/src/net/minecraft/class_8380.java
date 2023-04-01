package net.minecraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8380 {
	private static final long field_43999 = 18000L;
	private final DynamicRegistryManager field_44000;
	private final Map<UUID, class_8376> field_44001 = new HashMap();
	final class_8389 field_44002 = new class_8389();
	private int field_44003;

	public class_8380(DynamicRegistryManager dynamicRegistryManager) {
		this.field_44000 = dynamicRegistryManager;
	}

	public void method_50557(class_8387 arg) {
		this.field_44001.clear();
		this.field_44001.putAll(arg.pending());
		this.field_44003 = arg.totalProposalCount();
		this.field_44002.method_50591(arg.votes());

		for (class_8291 lv : arg.approved()) {
			lv.method_50122(class_8290.APPROVE);
		}
	}

	public void method_50553() {
		this.field_44000.get(RegistryKeys.RULES).stream().forEach(arg -> arg.method_50203(true));
	}

	public class_8387 method_50565() {
		return class_8387.method_50580(this.field_44000.get(RegistryKeys.RULES).streamEntries(), this.field_44001, this.field_44002.method_50585(), this.field_44003);
	}

	public void method_50554(
		long l, MinecraftServer minecraftServer, class_8376.class_8379 arg, Consumer<class_8370> consumer, BiConsumer<UUID, class_8376> biConsumer
	) {
		boolean bl = this.method_50564(minecraftServer, arg.random()) && arg.method_50548();
		boolean bl2 = arg.method_50549();
		int i = 0;
		Set<class_8289> set = new HashSet();
		Iterator<Entry<UUID, class_8376>> iterator = this.field_44001.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<UUID, class_8376> entry = (Entry<UUID, class_8376>)iterator.next();
			UUID uUID = (UUID)entry.getKey();
			class_8376 lv = (class_8376)entry.getValue();
			if (l >= lv.header().method_50461()) {
				iterator.remove();
				class_8374 lv2 = this.field_44002.method_50592(lv, true);
				consumer.accept(new class_8370(uUID, lv, lv2.method_50503()));
			} else if (bl) {
				method_50558(set, lv);
				if (bl2) {
					if (lv.method_50514(class_8290.REPEAL)) {
						i++;
					}
				} else if (lv.method_50514(class_8290.APPROVE)) {
					i++;
				}
			}
		}

		if (bl) {
			if (bl2) {
				int j = arg.maxRepealVoteCount();
				if (i < j) {
					UUID uUID = UUID.randomUUID();
					class_8376.method_50537(uUID, set, minecraftServer, arg).ifPresent(argx -> biConsumer.accept(uUID, argx));
				}
			} else {
				int j = arg.maxApproveVoteCount();
				if (i < j) {
					UUID uUID = UUID.randomUUID();
					class_8376.method_50528(uUID, set, minecraftServer, arg).ifPresent(argx -> biConsumer.accept(uUID, argx));
				}
			}
		}
	}

	private boolean method_50564(MinecraftServer minecraftServer, Random random) {
		long l = minecraftServer.getSaveProperties().getMainWorldProperties().getTime();
		float f = (float)l / 18000.0F;
		if (f > 1.0F) {
			return true;
		} else {
			float g = f * 7.0F;
			float h = (float)this.field_44001.size() - g;
			if (h < 0.0F) {
				return true;
			} else {
				float i = (float)Math.pow(0.1, (double)h);
				return random.nextFloat() < i;
			}
		}
	}

	public int method_50568() {
		return this.field_44003;
	}

	public void method_50560(UUID uUID, class_8376 arg) {
		this.field_44001.put(uUID, arg);
		this.field_44003++;
	}

	@Nullable
	public class_8370 method_50559(UUID uUID) {
		class_8376 lv = (class_8376)this.field_44001.remove(uUID);
		if (lv != null) {
			class_8374 lv2 = this.field_44002.method_50592(lv, true);
			return new class_8370(uUID, lv, lv2.method_50503());
		} else {
			return null;
		}
	}

	public Stream<UUID> method_50569() {
		return this.field_44001.keySet().stream();
	}

	public void method_50562(BiConsumer<UUID, class_8376> biConsumer) {
		this.field_44001.forEach(biConsumer);
	}

	public void method_50561(UUID uUID, BiConsumer<class_8373, class_8388> biConsumer) {
		this.field_44002.method_50597(uUID, biConsumer);
	}

	@Nullable
	public class_8380.class_8381 method_50556(class_8373 arg) {
		class_8376 lv = (class_8376)this.field_44001.get(arg.voteId());
		return lv == null ? null : new class_8380.class_8381(arg, lv);
	}

	public class_8375 method_50566(class_8373 arg) {
		return this.field_44002.method_50590(arg, false);
	}

	public Set<class_8289> method_50570() {
		Set<class_8289> set = new HashSet();

		for (class_8376 lv : this.field_44001.values()) {
			method_50558(set, lv);
		}

		return set;
	}

	private static void method_50558(Set<class_8289> set, class_8376 arg) {
		for (class_8376.class_8378 lv : arg.options().values()) {
			for (class_8376.class_8377 lv2 : lv.changes()) {
				set.add(lv2.change().method_50121());
			}
		}
	}

	public class class_8381 {
		private final class_8373 field_44005;
		private final class_8376 field_44006;

		public class_8381(class_8373 arg2, class_8376 arg3) {
			this.field_44005 = arg2;
			this.field_44006 = arg3;
		}

		public class_8380.class_8382 method_50572(ServerPlayerEntity serverPlayerEntity) {
			List<class_8390.class_8391> list = this.field_44006.header().cost();

			for (class_8390.class_8391 lv : list) {
				if (lv.material() == class_8390.field_44032) {
					int i = class_8380.this.field_44002.method_50595(this.field_44006.options().keySet(), serverPlayerEntity.getUuid());
					if (i >= lv.count()) {
						return class_8380.class_8382.NO_MORE_VOTES;
					}
				} else if (lv.material() == class_8390.field_44033) {
					int i = class_8380.this.field_44002.method_50588(this.field_44005, serverPlayerEntity.getUuid());
					if (i > lv.count()) {
						return class_8380.class_8382.NO_MORE_VOTES;
					}
				} else if (!lv.method_50605(serverPlayerEntity, true)) {
					return class_8380.class_8382.NOT_ENOUGH_RESOURCES;
				}
			}

			for (class_8390.class_8391 lvx : list) {
				if (lvx.material() != class_8390.field_44032 && lvx.material() != class_8390.field_44033) {
					lvx.method_50605(serverPlayerEntity, false);
				}
			}

			return class_8380.class_8382.OK;
		}

		public void method_50573(Entity entity, int i) {
			class_8380.this.field_44002.method_50589(this.field_44005, entity.getUuid(), entity.getName(), i);
		}

		public Text method_50571() {
			return this.field_44006.header().displayName();
		}

		public Text method_50574() {
			class_8376.class_8378 lv = (class_8376.class_8378)this.field_44006.options().get(this.field_44005);
			return (Text)(lv != null ? lv.displayName() : Text.empty());
		}
	}

	public static enum class_8382 {
		OK,
		NO_MORE_VOTES,
		NOT_ENOUGH_RESOURCES;
	}
}
