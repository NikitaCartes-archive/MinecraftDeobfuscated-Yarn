package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;

public record class_8376(class_8369 header, Map<class_8373, class_8376.class_8378> options) {
	public static final Codec<class_8376> field_43995 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_8369.field_43981.forGetter(class_8376::header),
					Codec.unboundedMap(class_8373.field_43987, class_8376.class_8378.field_43998).fieldOf("options").forGetter(class_8376::options)
				)
				.apply(instance, class_8376::new)
	);
	public static final Text field_43996 = Text.translatable("rule.nothing");

	public boolean method_50514(class_8290 arg) {
		return this.options.values().stream().anyMatch(arg2 -> arg2.method_50543(arg));
	}

	public class_8367 method_50513() {
		return new class_8367(
			this.header,
			(Map<class_8373, class_8367.class_8368>)this.options
				.entrySet()
				.stream()
				.collect(
					Collectors.toUnmodifiableMap(
						Entry::getKey,
						entry -> new class_8367.class_8368(((class_8376.class_8378)entry.getValue()).displayName(), ((class_8376.class_8378)entry.getValue()).changes.isEmpty())
					)
				)
		);
	}

	public static Optional<class_8376> method_50528(UUID uUID, Set<class_8289> set, MinecraftServer minecraftServer, class_8376.class_8379 arg) {
		return method_50525(set, arg).findAny().flatMap(reference -> method_50529(uUID, minecraftServer, arg, (class_8289)reference.value()));
	}

	private static Stream<RegistryEntry.Reference<class_8289>> method_50525(Set<class_8289> set, class_8376.class_8379 arg) {
		return Stream.generate(() -> class_8293.method_50208(arg.random)).limit(1000L).filter(reference -> !set.contains(reference.value()));
	}

	private static Stream<RegistryEntry.Reference<class_8289>> method_50535(Set<class_8289> set, class_8376.class_8379 arg) {
		return Stream.generate(() -> class_8293.method_50212(arg.random)).limit(1000L).filter(reference -> !set.contains(reference.value()));
	}

	private static class_8376 method_50531(UUID uUID, MinecraftServer minecraftServer, class_8376.class_8379 arg, List<class_8376.class_8378> list) {
		Map<class_8373, class_8376.class_8378> map = new HashMap();

		for (int i = 0; i < list.size(); i++) {
			map.put(new class_8373(uUID, i), (class_8376.class_8378)list.get(i));
		}

		int i = minecraftServer.method_51115().method_50568();
		Text text = Text.translatable("rule.proposal", i + 1);
		long l = minecraftServer.getSaveProperties().getMainWorldProperties().getTime();
		long m = (long)arg.method_50546();
		return new class_8376(new class_8369(text, l, m, arg.voteCost), map);
	}

	public static Optional<class_8376> method_50529(UUID uUID, MinecraftServer minecraftServer, class_8376.class_8379 arg, class_8289 arg2) {
		int i = arg.method_50551();
		List<List<class_8291>> list = new ArrayList(i);
		arg2.method_50118(minecraftServer, arg.random, i).forEach(argx -> {
			List<class_8291> list2x = new ArrayList();
			list2x.add(argx);
			list.add(list2x);
		});
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			Set<class_8289> set = new HashSet();
			set.add(arg2);

			for (int j = 0; j < arg.maxExtraOptions && arg.method_50550(); j++) {
				int k = list.size();
				MutableInt mutableInt = new MutableInt();
				Stream.generate(() -> class_8293.method_50208(arg.random))
					.filter(reference -> !set.contains(reference.value()))
					.flatMap(reference -> ((class_8289)reference.value()).method_50118(minecraftServer, arg.random, k))
					.limit((long)k)
					.forEachOrdered(argx -> {
						set.add(argx.method_50121());
						((List)list.get(mutableInt.getAndIncrement())).add(argx);
					});
			}

			if (arg.alwaysAddOptOutVote() || list.size() == 1) {
				list.add(List.of());
			}

			class_8290 lv = class_8290.APPROVE;
			List<class_8376.class_8378> list2 = list.stream().map(listx -> {
				List<class_8376.class_8377> list2x = listx.stream().map(arg2x -> new class_8376.class_8377(arg2x, lv)).toList();
				return new class_8376.class_8378(method_50522(list2x), list2x);
			}).toList();
			return Optional.of(method_50531(uUID, minecraftServer, arg, list2));
		}
	}

	public static Optional<class_8376> method_50537(UUID uUID, Set<class_8289> set, MinecraftServer minecraftServer, class_8376.class_8379 arg) {
		int i = arg.method_50552();
		List<class_8376.class_8378> list = (List<class_8376.class_8378>)method_50535(set, arg)
			.distinct()
			.flatMap(reference -> ((class_8289)reference.value()).method_50204())
			.limit((long)i)
			.map(argx -> {
				List<class_8376.class_8377> listx = List.of(new class_8376.class_8377(argx, class_8290.REPEAL));
				return new class_8376.class_8378(method_50522(listx), listx);
			})
			.collect(Collectors.toCollection(ArrayList::new));
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			if (arg.alwaysAddOptOutVote() || list.size() == 1) {
				list.add(new class_8376.class_8378(method_50522(List.of()), List.of()));
			}

			return Optional.of(method_50531(uUID, minecraftServer, arg, list));
		}
	}

	private static Text method_50522(List<class_8376.class_8377> list) {
		return (Text)list.stream()
			.map(class_8376.class_8377::method_50540)
			.reduce((text, text2) -> Text.translatable("rule.connector", text, text2))
			.orElse(field_43996);
	}

	public static record class_8377(class_8291 change, class_8290 action) {
		public static final Codec<class_8376.class_8377> field_43997 = RecordCodecBuilder.create(
			instance -> instance.group(
						class_8291.field_43504.fieldOf("change").forGetter(class_8376.class_8377::change),
						class_8290.field_43501.fieldOf("action").forGetter(class_8376.class_8377::action)
					)
					.apply(instance, class_8376.class_8377::new)
		);

		public void method_50542(MinecraftServer minecraftServer) {
			this.change.method_50164(this.action, minecraftServer);
		}

		public Text method_50540() {
			return this.change.method_50130(this.action);
		}
	}

	public static record class_8378(Text displayName, List<class_8376.class_8377> changes) {
		public static final Codec<class_8376.class_8378> field_43998 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.TEXT.fieldOf("display_name").forGetter(class_8376.class_8378::displayName),
						class_8376.class_8377.field_43997.listOf().fieldOf("changes").forGetter(class_8376.class_8378::changes)
					)
					.apply(instance, class_8376.class_8378::new)
		);

		public boolean method_50543(class_8290 arg) {
			return this.changes.stream().anyMatch(arg2 -> arg2.action == arg);
		}
	}

	public static record class_8379(
		Random random,
		float newVoteChancePerTick,
		IntProvider optionsPerApproveVote,
		IntProvider optionsPerRepealVote,
		IntProvider durationMinutes,
		float extraOptionChance,
		int maxExtraOptions,
		List<class_8390.class_8391> voteCost,
		boolean alwaysAddOptOutVote,
		int maxApproveVoteCount,
		int maxRepealVoteCount,
		float repealVoteChance
	) {

		public static class_8376.class_8379 method_50547(Random random) {
			return new class_8376.class_8379(
				random,
				1.0F / (float)((Integer)class_8293.field_43690.method_50171()).intValue(),
				class_8293.field_43691.method_50261(),
				class_8293.field_43692.method_50261(),
				class_8293.field_43693.method_50261(),
				(float)((Integer)class_8293.field_43694.method_50171()).intValue() / 100.0F,
				(Integer)class_8293.field_43695.method_50171(),
				class_8293.field_43700.method_50268(),
				!class_8293.field_43697.method_50116(),
				(Integer)class_8293.field_43698.method_50171(),
				(Integer)class_8293.field_43699.method_50171(),
				(float)((Integer)class_8293.field_43696.method_50171()).intValue() / 100.0F
			);
		}

		public int method_50546() {
			return this.durationMinutes.get(this.random) * 1200;
		}

		public boolean method_50548() {
			return this.random.nextFloat() < this.newVoteChancePerTick;
		}

		public boolean method_50549() {
			return this.random.nextFloat() < this.repealVoteChance;
		}

		public boolean method_50550() {
			return this.random.nextFloat() < this.extraOptionChance;
		}

		public int method_50551() {
			return this.optionsPerApproveVote.get(this.random);
		}

		public int method_50552() {
			return this.optionsPerRepealVote.get(this.random);
		}
	}
}
