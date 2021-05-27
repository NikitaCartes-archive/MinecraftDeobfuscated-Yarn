package net.minecraft.util.profiler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.util.profiler.SamplingRecorder;

public class MetricSuppliers {
	public static final MetricSuppliers INSTANCE = new MetricSuppliers();
	private final WeakHashMap<MetricSamplerSupplier, Void> samplers = new WeakHashMap();

	private MetricSuppliers() {
	}

	public void add(MetricSamplerSupplier supplier) {
		this.samplers.put(supplier, null);
	}

	public List<SamplingRecorder> method_37178() {
		Map<String, List<SamplingRecorder>> map = (Map<String, List<SamplingRecorder>>)this.samplers
			.keySet()
			.stream()
			.flatMap(metricSamplerSupplier -> metricSamplerSupplier.getSamplers().stream())
			.collect(Collectors.groupingBy(SamplingRecorder::method_37171));
		return method_37180(map);
	}

	private static List<SamplingRecorder> method_37180(Map<String, List<SamplingRecorder>> map) {
		return (List<SamplingRecorder>)map.entrySet().stream().map(entry -> {
			String string = (String)entry.getKey();
			List<SamplingRecorder> list = (List<SamplingRecorder>)entry.getValue();
			return (SamplingRecorder)(list.size() > 1 ? new MetricSuppliers.class_6399(string, list) : (SamplingRecorder)list.get(0));
		}).collect(Collectors.toList());
	}

	static class class_6399 extends SamplingRecorder {
		private final List<SamplingRecorder> field_33890;

		class_6399(String string, List<SamplingRecorder> list) {
			super(string, ((SamplingRecorder)list.get(0)).method_37172(), () -> method_37186(list), () -> method_37185(list), method_37183(list));
			this.field_33890 = list;
		}

		private static SamplingRecorder.ValueConsumer method_37183(List<SamplingRecorder> list) {
			return d -> list.stream().anyMatch(samplingRecorder -> samplingRecorder.writeAction != null ? samplingRecorder.writeAction.accept(d) : false);
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
			} else if (object == null || this.getClass() != object.getClass()) {
				return false;
			} else if (!super.equals(object)) {
				return false;
			} else {
				MetricSuppliers.class_6399 lv = (MetricSuppliers.class_6399)object;
				return this.field_33890.equals(lv.field_33890);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{super.hashCode(), this.field_33890});
		}
	}
}
