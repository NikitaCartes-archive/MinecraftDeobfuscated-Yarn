package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;

/**
 * Fetches the hardware and software information to populate crash reports
 * and debug profiles. A custom section can be added by calling {@link
 * #addSection(String, String)}.
 */
public class SystemDetails {
	public static final long MEBI = 1048576L;
	private static final long GIGA = 1000000000L;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String OPERATING_SYSTEM = System.getProperty("os.name")
		+ " ("
		+ System.getProperty("os.arch")
		+ ") version "
		+ System.getProperty("os.version");
	private static final String JAVA_VERSION = System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
	private static final String JVM_VERSION = System.getProperty("java.vm.name")
		+ " ("
		+ System.getProperty("java.vm.info")
		+ "), "
		+ System.getProperty("java.vm.vendor");
	private final Map<String, String> sections = Maps.<String, String>newLinkedHashMap();

	public SystemDetails() {
		this.addSection("Minecraft Version", SharedConstants.getGameVersion().getName());
		this.addSection("Minecraft Version ID", SharedConstants.getGameVersion().getId());
		this.addSection("Operating System", OPERATING_SYSTEM);
		this.addSection("Java Version", JAVA_VERSION);
		this.addSection("Java VM Version", JVM_VERSION);
		this.addSection("Memory", (Supplier<String>)(() -> {
			Runtime runtime = Runtime.getRuntime();
			long l = runtime.maxMemory();
			long m = runtime.totalMemory();
			long n = runtime.freeMemory();
			long o = l / 1048576L;
			long p = m / 1048576L;
			long q = n / 1048576L;
			return n + " bytes (" + q + " MiB) / " + m + " bytes (" + p + " MiB) up to " + l + " bytes (" + o + " MiB)";
		}));
		this.addSection("CPUs", (Supplier<String>)(() -> String.valueOf(Runtime.getRuntime().availableProcessors())));
		this.tryAddGroup("hardware", () -> this.addHardwareGroup(new SystemInfo()));
		this.addSection("JVM Flags", (Supplier<String>)(() -> {
			List<String> list = (List<String>)Util.getJVMFlags().collect(Collectors.toList());
			return String.format(Locale.ROOT, "%d total; %s", list.size(), String.join(" ", list));
		}));
	}

	/**
	 * Adds a section with the given {@code name} and {@code value}.
	 */
	public void addSection(String name, String value) {
		this.sections.put(name, value);
	}

	/**
	 * Adds a section with the given {@code name} and the value supplied by
	 * {@code valueSupplier}. If an exception is thrown while calling the supplier,
	 * {@code ERR} is used as the value.
	 */
	public void addSection(String name, Supplier<String> valueSupplier) {
		try {
			this.addSection(name, (String)valueSupplier.get());
		} catch (Exception var4) {
			LOGGER.warn("Failed to get system info for {}", name, var4);
			this.addSection(name, "ERR");
		}
	}

	private void addHardwareGroup(SystemInfo systemInfo) {
		HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
		this.tryAddGroup("processor", () -> this.addProcessorGroup(hardwareAbstractionLayer.getProcessor()));
		this.tryAddGroup("graphics", () -> this.addGraphicsCardGroup(hardwareAbstractionLayer.getGraphicsCards()));
		this.tryAddGroup("memory", () -> this.addGlobalMemoryGroup(hardwareAbstractionLayer.getMemory()));
	}

	private void tryAddGroup(String name, Runnable adder) {
		try {
			adder.run();
		} catch (Throwable var4) {
			LOGGER.warn("Failed retrieving info for group {}", name, var4);
		}
	}

	private void addPhysicalMemoryGroup(List<PhysicalMemory> memories) {
		int i = 0;

		for (PhysicalMemory physicalMemory : memories) {
			String string = String.format(Locale.ROOT, "Memory slot #%d ", i++);
			this.addSection(string + "capacity (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)physicalMemory.getCapacity() / 1048576.0F)));
			this.addSection(string + "clockSpeed (GHz)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)physicalMemory.getClockSpeed() / 1.0E9F)));
			this.addSection(string + "type", physicalMemory::getMemoryType);
		}
	}

	private void addVirtualMemoryGroup(VirtualMemory virtualMemory) {
		this.addSection("Virtual memory max (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)virtualMemory.getVirtualMax() / 1048576.0F)));
		this.addSection("Virtual memory used (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)virtualMemory.getVirtualInUse() / 1048576.0F)));
		this.addSection("Swap memory total (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)virtualMemory.getSwapTotal() / 1048576.0F)));
		this.addSection("Swap memory used (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)virtualMemory.getSwapUsed() / 1048576.0F)));
	}

	private void addGlobalMemoryGroup(GlobalMemory globalMemory) {
		this.tryAddGroup("physical memory", () -> this.addPhysicalMemoryGroup(globalMemory.getPhysicalMemory()));
		this.tryAddGroup("virtual memory", () -> this.addVirtualMemoryGroup(globalMemory.getVirtualMemory()));
	}

	private void addGraphicsCardGroup(List<GraphicsCard> graphicsCards) {
		int i = 0;

		for (GraphicsCard graphicsCard : graphicsCards) {
			String string = String.format(Locale.ROOT, "Graphics card #%d ", i++);
			this.addSection(string + "name", graphicsCard::getName);
			this.addSection(string + "vendor", graphicsCard::getVendor);
			this.addSection(string + "VRAM (MB)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)graphicsCard.getVRam() / 1048576.0F)));
			this.addSection(string + "deviceId", graphicsCard::getDeviceId);
			this.addSection(string + "versionInfo", graphicsCard::getVersionInfo);
		}
	}

	private void addProcessorGroup(CentralProcessor centralProcessor) {
		ProcessorIdentifier processorIdentifier = centralProcessor.getProcessorIdentifier();
		this.addSection("Processor Vendor", processorIdentifier::getVendor);
		this.addSection("Processor Name", processorIdentifier::getName);
		this.addSection("Identifier", processorIdentifier::getIdentifier);
		this.addSection("Microarchitecture", processorIdentifier::getMicroarchitecture);
		this.addSection("Frequency (GHz)", (Supplier<String>)(() -> String.format(Locale.ROOT, "%.2f", (float)processorIdentifier.getVendorFreq() / 1.0E9F)));
		this.addSection("Number of physical packages", (Supplier<String>)(() -> String.valueOf(centralProcessor.getPhysicalPackageCount())));
		this.addSection("Number of physical CPUs", (Supplier<String>)(() -> String.valueOf(centralProcessor.getPhysicalProcessorCount())));
		this.addSection("Number of logical CPUs", (Supplier<String>)(() -> String.valueOf(centralProcessor.getLogicalProcessorCount())));
	}

	/**
	 * Writes the system details to {@code stringBuilder}.
	 * This writes the header and the sections (indented by one tab).
	 */
	public void writeTo(StringBuilder stringBuilder) {
		stringBuilder.append("-- ").append("System Details").append(" --\n");
		stringBuilder.append("Details:");
		this.sections.forEach((name, value) -> {
			stringBuilder.append("\n\t");
			stringBuilder.append(name);
			stringBuilder.append(": ");
			stringBuilder.append(value);
		});
	}

	/**
	 * {@return a string representation of the system details}
	 * 
	 * <p>Sections are separated by newlines, and each section consists of the name, a colon,
	 * a space, and the value. No indent is added by this method.
	 */
	public String collect() {
		return (String)this.sections
			.entrySet()
			.stream()
			.map(entry -> (String)entry.getKey() + ": " + (String)entry.getValue())
			.collect(Collectors.joining(System.lineSeparator()));
	}
}
