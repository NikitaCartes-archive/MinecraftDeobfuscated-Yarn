/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.client.util.profiler.SamplingRecorder;
import net.minecraft.util.profiler.MetricSamplerSupplier;
import org.jetbrains.annotations.Nullable;

public class MetricSuppliers {
    public static final MetricSuppliers INSTANCE = new MetricSuppliers();
    private final WeakHashMap<MetricSamplerSupplier, Void> samplers = new WeakHashMap();

    private MetricSuppliers() {
    }

    public void add(MetricSamplerSupplier supplier) {
        this.samplers.put(supplier, null);
    }

    public List<SamplingRecorder> method_37178() {
        Map<String, List<SamplingRecorder>> map = this.samplers.keySet().stream().flatMap(metricSamplerSupplier -> metricSamplerSupplier.getSamplers().stream()).collect(Collectors.groupingBy(SamplingRecorder::method_37171));
        return MetricSuppliers.method_37180(map);
    }

    private static List<SamplingRecorder> method_37180(Map<String, List<SamplingRecorder>> map) {
        return map.entrySet().stream().map(entry -> {
            String string = (String)entry.getKey();
            List list = (List)entry.getValue();
            return list.size() > 1 ? new class_6399(string, list) : (SamplingRecorder)list.get(0);
        }).collect(Collectors.toList());
    }

    static class class_6399
    extends SamplingRecorder {
        private final List<SamplingRecorder> field_33890;

        class_6399(String string, List<SamplingRecorder> list) {
            super(string, list.get(0).method_37172(), () -> class_6399.method_37186(list), () -> class_6399.method_37185(list), class_6399.method_37183(list));
            this.field_33890 = list;
        }

        private static SamplingRecorder.ValueConsumer method_37183(List<SamplingRecorder> list) {
            return d -> list.stream().anyMatch(samplingRecorder -> {
                if (samplingRecorder.writeAction != null) {
                    return samplingRecorder.writeAction.accept(d);
                }
                return false;
            });
        }

        private static void method_37185(List<SamplingRecorder> list) {
            for (SamplingRecorder samplingRecorder : list) {
                samplingRecorder.start();
            }
        }

        private static double method_37186(List<SamplingRecorder> list) {
            double d = 0.0;
            for (SamplingRecorder samplingRecorder : list) {
                d += samplingRecorder.method_37170().getAsDouble();
            }
            return d / (double)list.size();
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
            class_6399 lv = (class_6399)object;
            return this.field_33890.equals(lv.field_33890);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.field_33890);
        }
    }
}

