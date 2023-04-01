package net.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public record class_8370(UUID id, class_8376 vote, class_8384 results) {
	private static final Comparator<Entry<UUID, class_8388>> field_43982 = Entry.comparingByValue(class_8388.field_44025.reversed())
		.thenComparing(Entry.comparingByKey());
	private static final Comparator<class_8384.class_8385> field_43983 = Comparator.comparingInt(arg -> arg.optionId().index());

	private List<class_8384.class_8385> method_50484() {
		Set<class_8373> set = new HashSet(this.vote.options().keySet());
		List<class_8384.class_8385> list = (List<class_8384.class_8385>)this.results
			.options()
			.stream()
			.peek(arg -> set.remove(arg.optionId()))
			.collect(Collectors.toCollection(ArrayList::new));
		set.stream().map(arg -> new class_8384.class_8385(arg, class_8384.class_8386.field_44022)).forEach(list::add);
		return list;
	}

	@Nullable
	private class_8376.class_8378 method_50467(class_8373 arg) {
		return (class_8376.class_8378)this.vote.options().get(arg);
	}

	private static Text method_50468(@Nullable class_8376.class_8378 arg) {
		return (Text)(arg != null ? arg.displayName() : Text.literal("???"));
	}

	private Text method_50479(class_8373 arg) {
		return method_50468(this.method_50467(arg));
	}

	public void method_50477(Consumer<class_8376.class_8377> consumer, Consumer<Text> consumer2, class_8370.class_8372 arg) {
		consumer2.accept(Text.translatable("vote.finished", this.vote.header().displayName()));
		List<class_8384.class_8385> list = this.method_50484();
		this.method_50473(consumer2, arg, list);
		List<class_8384.class_8385> list2 = this.method_50482(consumer2, arg, list);
		if (list2.isEmpty()) {
			consumer2.accept(Text.translatable("vote.no_option"));
		} else {
			this.method_50478(consumer, consumer2, list2);
		}
	}

	private void method_50473(Consumer<Text> consumer, class_8370.class_8372 arg, List<class_8384.class_8385> list) {
		if (arg.showTotals) {
			class_8384.class_8386 lv = this.results.total();
			method_50472(consumer, arg, lv, Text.translatable("vote.total_count", lv.votesCount(), lv.votersCount()));
		}

		Comparator<class_8384.class_8385> comparator = arg.selectTopVotes ? class_8384.class_8385.field_44021.reversed() : class_8384.class_8385.field_44021;
		list.stream()
			.sorted(comparator.thenComparing(field_43983))
			.forEach(
				arg2 -> {
					class_8384.class_8386 lv = arg2.counts();
					class_8373 lv2 = arg2.optionId();
					Text text = this.method_50479(lv2);
					Text text2 = arg.showTotals
						? Text.translatable("vote.option_count", lv2.index() + 1, text, lv.votesCount(), lv.votersCount())
						: Text.translatable("vote.option_no_count", lv2.index() + 1, text);
					method_50472(consumer, arg, lv, text2);
				}
			);
	}

	private static void method_50472(Consumer<Text> consumer, class_8370.class_8372 arg, class_8384.class_8386 arg2, Text text) {
		if (!arg.selectTopVotes || arg2.votesCount() != 0) {
			if (arg.showTotals) {
				consumer.accept(text);
			}

			if (arg.showVoters) {
				Map<UUID, class_8388> map = arg2.votes().voters();
				if (!map.isEmpty()) {
					consumer.accept(!arg.showTotals ? text : Text.translatable("vote.voters"));
					method_50476(consumer, map);
				}
			}
		}
	}

	private static void method_50476(Consumer<Text> consumer, Map<UUID, class_8388> map) {
		map.entrySet()
			.stream()
			.sorted(field_43982)
			.forEach(entry -> consumer.accept(Text.translatable("vote.voter", ((class_8388)entry.getValue()).displayName(), ((class_8388)entry.getValue()).voteCount())));
	}

	private List<class_8384.class_8385> method_50482(Consumer<Text> consumer, class_8370.class_8372 arg, List<class_8384.class_8385> list) {
		List<class_8384.class_8385> list2;
		if (this.method_50481(consumer, arg)) {
			list2 = this.method_50474(consumer, list, arg);
		} else {
			list2 = List.of();
		}

		if (list2.isEmpty() && this.method_50465(arg)) {
			consumer.accept(Text.translatable("vote.no_option.random"));
			ObjectArrayList<class_8384.class_8385> objectArrayList = (ObjectArrayList<class_8384.class_8385>)list.stream()
				.sorted(field_43983)
				.collect(Collectors.toCollection(ObjectArrayList::new));
			Util.shuffle(objectArrayList, arg.random);
			list2 = objectArrayList.stream().limit((long)arg.maxSelectedOptions).toList();
		}

		return list2;
	}

	private List<class_8384.class_8385> method_50474(Consumer<Text> consumer, List<class_8384.class_8385> list, class_8370.class_8372 arg) {
		Map<Integer, List<class_8384.class_8385>> map = (Map<Integer, List<class_8384.class_8385>>)list.stream()
			.collect(Collectors.groupingBy(argx -> argx.counts().votesCount(), Collectors.toCollection(ArrayList::new)));
		List<class_8384.class_8385> list2 = new ArrayList();
		boolean bl = false;
		int i = this.method_50471(consumer, arg);
		Comparator<Integer> comparator = arg.selectTopVotes ? Comparator.reverseOrder() : Comparator.naturalOrder();

		for (Entry<Integer, List<class_8384.class_8385>> entry : map.entrySet()
			.stream()
			.filter(entryx -> (Integer)entryx.getKey() >= i)
			.sorted(Entry.comparingByKey(comparator))
			.toList()) {
			List<class_8384.class_8385> list4 = (List<class_8384.class_8385>)entry.getValue();
			if (!arg.onlyApplyOptionsWithVotes || (Integer)entry.getKey() != 0) {
				if (list4.size() > 1) {
					class_8383 lv = arg.tieStrategy;
					if (!bl) {
						bl = true;
						consumer.accept(Text.translatable("vote.tie", lv.method_50576()));
					}

					switch (lv) {
						case PICK_LOW:
							list4.stream().min(field_43983).ifPresent(list2::add);
							break;
						case PICK_HIGH:
							list4.stream().max(field_43983).ifPresent(list2::add);
							break;
						case PICK_RANDOM:
							Util.getRandomOrEmpty(list4, arg.random).ifPresent(list2::add);
							break;
						case PICK_ALL:
							list4.stream().sorted(field_43983).forEach(list2::add);
						case PICK_NONE:
						default:
							break;
						case FAIL:
							list2.clear();
							return list2;
					}
				} else {
					list2.addAll(list4);
				}

				if (list2.size() >= arg.maxSelectedOptions) {
					break;
				}
			}
		}

		return list2;
	}

	private int method_50471(Consumer<Text> consumer, class_8370.class_8372 arg) {
		int i = Math.round(arg.absoluteMajorityVotes * (float)this.results.total().votesCount());
		if (i > 0) {
			consumer.accept(Text.translatable("vote.vote_count.minimum", i));
		}

		return i;
	}

	private boolean method_50481(Consumer<Text> consumer, class_8370.class_8372 arg) {
		int i = arg.requireAtLeastOnePlayer ? 1 : 0;
		int j = Math.max(Math.round(arg.quorum * (float)arg.playerCount), i);
		int k = this.results.total().votersCount();
		if (k < j) {
			consumer.accept(Text.translatable("vote.quorum.not_reached", j));
			return false;
		} else {
			consumer.accept(Text.translatable("vote.quorum.passed", k, j));
			return true;
		}
	}

	private boolean method_50465(class_8370.class_8372 arg) {
		return arg.pickRandomOnFail && (!arg.requireAtLeastOnePlayer || this.results.total().votersCount() > 0);
	}

	private void method_50478(Consumer<class_8376.class_8377> consumer, Consumer<Text> consumer2, List<class_8384.class_8385> list) {
		record class_8371(class_8373 id, class_8376.class_8378 option) {
		}

		List<class_8371> list2 = new ArrayList();

		for (class_8384.class_8385 lv : list) {
			class_8373 lv2 = lv.optionId();
			class_8376.class_8378 lv3 = this.method_50467(lv2);
			if (lv3 != null) {
				list2.add(new class_8371(lv2, lv3));
			}
		}

		boolean bl = list2.stream().anyMatch(arg -> !arg.option.changes().isEmpty());
		if (!bl) {
			consumer2.accept(Text.translatable("vote.no_change"));
		} else {
			for (class_8371 lv4 : list2) {
				if (lv4.option.changes().isEmpty()) {
					consumer2.accept(Text.translatable("vote.option_won.no_effect", lv4.id.index() + 1, lv4.option.displayName()).formatted(Formatting.GRAY));
				} else {
					consumer2.accept(Text.translatable("vote.option_won", lv4.id.index() + 1, lv4.option.displayName()).formatted(Formatting.GREEN));

					for (class_8376.class_8377 lv5 : lv4.option.changes()) {
						consumer2.accept(lv5.method_50540());
						consumer.accept(lv5);
					}
				}
			}
		}
	}

	public static record class_8372(
		Random random,
		boolean requireAtLeastOnePlayer,
		int playerCount,
		float quorum,
		float absoluteMajorityVotes,
		boolean showTotals,
		boolean showVoters,
		boolean pickRandomOnFail,
		boolean selectTopVotes,
		boolean onlyApplyOptionsWithVotes,
		int maxSelectedOptions,
		class_8383 tieStrategy
	) {

		public class_8370.class_8372 method_50488(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				bl,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50485(float f) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				f,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50489(float f) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				f,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50490(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				bl,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50491(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				bl,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50492(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				bl,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50493(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				bl,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50494(boolean bl) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				bl,
				this.maxSelectedOptions,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50486(int i) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				i,
				this.tieStrategy
			);
		}

		public class_8370.class_8372 method_50487(class_8383 arg) {
			return new class_8370.class_8372(
				this.random,
				this.requireAtLeastOnePlayer,
				this.playerCount,
				this.quorum,
				this.absoluteMajorityVotes,
				this.showTotals,
				this.showVoters,
				this.pickRandomOnFail,
				this.selectTopVotes,
				this.onlyApplyOptionsWithVotes,
				this.maxSelectedOptions,
				arg
			);
		}
	}
}
