package net.minecraft.util.thread;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiler.Sampler;

public class ExecutorSampling {
	public static final ExecutorSampling INSTANCE = new ExecutorSampling();
	private final WeakHashMap<SampleableExecutor, Void> activeExecutors = new WeakHashMap();

	private ExecutorSampling() {
	}

	public void add(SampleableExecutor executor) {
		this.activeExecutors.put(executor, null);
	}

	public List<Sampler> createSamplers() {
		Map<String, List<Sampler>> map = (Map<String, List<Sampler>>)this.activeExecutors
			.keySet()
			.stream()
			.flatMap(executor -> executor.createSamplers().stream())
			.collect(Collectors.groupingBy(Sampler::getName));
		return mergeSimilarSamplers(map);
	}

	private static List<Sampler> mergeSimilarSamplers(Map<String, List<Sampler>> samplers) {
		return (List<Sampler>)samplers.entrySet().stream().map(entry -> {
			String string = (String)entry.getKey();
			List<Sampler> list = (List<Sampler>)entry.getValue();
			return (Sampler)(list.size() > 1 ? new ExecutorSampling.MergedSampler(string, list) : (Sampler)list.get(0));
		}).collect(Collectors.toList());
	}

	static class MergedSampler extends Sampler {
		private final List<Sampler> delegates;

		MergedSampler(String id, List<Sampler> delegates) {
			super(id, ((Sampler)delegates.get(0)).getType(), () -> averageRetrievers(delegates), () -> start(delegates), combineDeviationCheckers(delegates));
			this.delegates = delegates;
		}

		private static Sampler.DeviationChecker combineDeviationCheckers(List<Sampler> delegates) {
			return value -> delegates.stream().anyMatch(sampler -> sampler.deviationChecker != null ? sampler.deviationChecker.check(value) : false);
		}

		private static void start(List<Sampler> samplers) {
			for (Sampler sampler : samplers) {
				sampler.start();
			}
		}

		private static double averageRetrievers(List<Sampler> samplers) {
			double d = 0.0;

			for (Sampler sampler : samplers) {
				d += sampler.getRetriever().getAsDouble();
			}

			return d / (double)samplers.size();
		}

		@Override
		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else if (object == null || this.getClass() != object.getClass()) {
				return false;
			} else if (!super.equals(object)) {
				return false;
			} else {
				ExecutorSampling.MergedSampler mergedSampler = (ExecutorSampling.MergedSampler)object;
				return this.delegates.equals(mergedSampler.delegates);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{super.hashCode(), this.delegates});
		}
	}
}
