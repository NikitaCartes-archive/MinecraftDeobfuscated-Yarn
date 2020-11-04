package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;

public class CrashReportSection {
	private final CrashReport report;
	private final String title;
	private final List<CrashReportSection.Element> elements = Lists.<CrashReportSection.Element>newArrayList();
	private StackTraceElement[] stackTrace = new StackTraceElement[0];

	public CrashReportSection(CrashReport report, String title) {
		this.report = report;
		this.title = title;
	}

	@Environment(EnvType.CLIENT)
	public static String createPositionString(HeightLimitView heightLimitView, double d, double e, double f) {
		return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, e, f, createPositionString(heightLimitView, new BlockPos(d, e, f)));
	}

	public static String createPositionString(HeightLimitView heightLimitView, BlockPos blockPos) {
		return createPositionString(heightLimitView, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static String createPositionString(HeightLimitView heightLimitView, int i, int j, int k) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
		} catch (Throwable var19) {
			stringBuilder.append("(Error finding world loc)");
		}

		stringBuilder.append(", ");

		try {
			int l = ChunkSectionPos.getSectionCoord(i);
			int m = ChunkSectionPos.getSectionCoord(j);
			int n = ChunkSectionPos.getSectionCoord(k);
			int o = i & 15;
			int p = j & 15;
			int q = k & 15;
			int r = ChunkSectionPos.getBlockCoord(l);
			int s = heightLimitView.getBottomHeightLimit();
			int t = ChunkSectionPos.getBlockCoord(n);
			int u = ChunkSectionPos.getBlockCoord(l + 1) - 1;
			int v = heightLimitView.getTopHeightLimit() - 1;
			int w = ChunkSectionPos.getBlockCoord(n + 1) - 1;
			stringBuilder.append(String.format("Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", o, p, q, l, m, n, r, s, t, u, v, w));
		} catch (Throwable var18) {
			stringBuilder.append("(Error finding chunk loc)");
		}

		stringBuilder.append(", ");

		try {
			int l = i >> 9;
			int m = k >> 9;
			int n = l << 5;
			int o = m << 5;
			int p = (l + 1 << 5) - 1;
			int q = (m + 1 << 5) - 1;
			int r = l << 9;
			int s = heightLimitView.getBottomHeightLimit();
			int t = m << 9;
			int u = (l + 1 << 9) - 1;
			int v = heightLimitView.getTopHeightLimit() - 1;
			int w = (m + 1 << 9) - 1;
			stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", l, m, n, o, p, q, r, s, t, u, v, w));
		} catch (Throwable var17) {
			stringBuilder.append("(Error finding world loc)");
		}

		return stringBuilder.toString();
	}

	public CrashReportSection add(String string, CrashCallable<String> crashCallable) {
		try {
			this.add(string, crashCallable.call());
		} catch (Throwable var4) {
			this.add(string, var4);
		}

		return this;
	}

	public CrashReportSection add(String name, Object object) {
		this.elements.add(new CrashReportSection.Element(name, object));
		return this;
	}

	public void add(String name, Throwable throwable) {
		this.add(name, (Object)throwable);
	}

	public int initStackTrace(int ignoredCallCount) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length <= 0) {
			return 0;
		} else {
			this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - ignoredCallCount];
			System.arraycopy(stackTraceElements, 3 + ignoredCallCount, this.stackTrace, 0, this.stackTrace.length);
			return this.stackTrace.length;
		}
	}

	public boolean method_584(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
		if (this.stackTrace.length != 0 && stackTraceElement != null) {
			StackTraceElement stackTraceElement3 = this.stackTrace[0];
			if (stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod()
				&& stackTraceElement3.getClassName().equals(stackTraceElement.getClassName())
				&& stackTraceElement3.getFileName().equals(stackTraceElement.getFileName())
				&& stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName())) {
				if (stackTraceElement2 != null != this.stackTrace.length > 1) {
					return false;
				} else if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
					return false;
				} else {
					this.stackTrace[0] = stackTraceElement;
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void trimStackTraceEnd(int callCount) {
		StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - callCount];
		System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
		this.stackTrace = stackTraceElements;
	}

	public void addStackTrace(StringBuilder stringBuilder) {
		stringBuilder.append("-- ").append(this.title).append(" --\n");
		stringBuilder.append("Details:");

		for (CrashReportSection.Element element : this.elements) {
			stringBuilder.append("\n\t");
			stringBuilder.append(element.getName());
			stringBuilder.append(": ");
			stringBuilder.append(element.getDetail());
		}

		if (this.stackTrace != null && this.stackTrace.length > 0) {
			stringBuilder.append("\nStacktrace:");

			for (StackTraceElement stackTraceElement : this.stackTrace) {
				stringBuilder.append("\n\tat ");
				stringBuilder.append(stackTraceElement);
			}
		}
	}

	public StackTraceElement[] getStackTrace() {
		return this.stackTrace;
	}

	public static void addBlockInfo(CrashReportSection element, HeightLimitView heightLimitView, BlockPos blockPos, @Nullable BlockState blockState) {
		if (blockState != null) {
			element.add("Block", blockState::toString);
		}

		element.add("Block location", (CrashCallable<String>)(() -> createPositionString(heightLimitView, blockPos)));
	}

	static class Element {
		private final String name;
		private final String detail;

		public Element(String name, @Nullable Object detail) {
			this.name = name;
			if (detail == null) {
				this.detail = "~~NULL~~";
			} else if (detail instanceof Throwable) {
				Throwable throwable = (Throwable)detail;
				this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
			} else {
				this.detail = detail.toString();
			}
		}

		public String getName() {
			return this.name;
		}

		public String getDetail() {
			return this.detail;
		}
	}
}
