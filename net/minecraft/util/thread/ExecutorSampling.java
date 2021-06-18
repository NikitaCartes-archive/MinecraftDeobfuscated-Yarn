/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.util.profiler.Sampler;
import net.minecraft.util.thread.SampleableExecutor;
import org.jetbrains.annotations.Nullable;

public class ExecutorSampling {
    public static final ExecutorSampling INSTANCE = new ExecutorSampling();
    private final WeakHashMap<SampleableExecutor, Void> activeExecutors = new WeakHashMap();

    private ExecutorSampling() {
    }

    public void add(SampleableExecutor executor) {
        this.activeExecutors.put(executor, null);
    }

    public List<Sampler> createSamplers() {
        Map<String, List<Sampler>> map = this.activeExecutors.keySet().stream().flatMap(executor -> executor.createSamplers().stream()).collect(Collectors.groupingBy(Sampler::getName));
        return ExecutorSampling.mergeSimilarSamplers(map);
    }

    private static List<Sampler> mergeSimilarSamplers(Map<String, List<Sampler>> samplers) {
        return samplers.entrySet().stream().map(entry -> {
            String string = (String)entry.getKey();
            List list = (List)entry.getValue();
            return list.size() > 1 ? new MergedSampler(string, list) : (Sampler)list.get(0);
        }).collect(Collectors.toList());
    }

    static class MergedSampler
    extends Sampler {
        private final List<Sampler> delegates;

        MergedSampler(String id, List<Sampler> delegates) {
            super(id, delegates.get(0).getType(), () -> MergedSampler.averageRetrievers(delegates), () -> MergedSampler.start(delegates), MergedSampler.combineDeviationCheckers(delegates));
            this.delegates = delegates;
        }

        private static Sampler.DeviationChecker combineDeviationCheckers(List<Sampler> delegates) {
            return value -> delegates.stream().anyMatch(sampler -> {
                if (sampler.deviationChecker != null) {
                    return sampler.deviationChecker.check(value);
                }
                return false;
            });
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
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            if (!super.equals(object)) {
                return false;
            }
            MergedSampler mergedSampler = (MergedSampler)object;
            return this.delegates.equals(mergedSampler.delegates);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.delegates);
        }
    }
}

