package net.minecraft;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;

public class class_6396 {
	public static final long field_33852 = 1048576L;
	private static final long field_33853 = 1000000000L;
	private static final Logger field_33854 = LogManager.getLogger();
	private static final String field_33855 = System.getProperty("os.name")
		+ " ("
		+ System.getProperty("os.arch")
		+ ") version "
		+ System.getProperty("os.version");
	private static final String field_33856 = System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
	private static final String field_33857 = System.getProperty("java.vm.name")
		+ " ("
		+ System.getProperty("java.vm.info")
		+ "), "
		+ System.getProperty("java.vm.vendor");
	private final Map<String, String> field_33858 = Maps.<String, String>newLinkedHashMap();

	public class_6396() {
		this.method_37122("Minecraft Version", SharedConstants.getGameVersion().getName());
		this.method_37122("Minecraft Version ID", SharedConstants.getGameVersion().getId());
		this.method_37122("Operating System", field_33855);
		this.method_37122("Java Version", field_33856);
		this.method_37122("Java VM Version", field_33857);
		this.method_37123("Memory", () -> {
			Runtime runtime = Runtime.getRuntime();
			long l = runtime.maxMemory();
			long m = runtime.totalMemory();
			long n = runtime.freeMemory();
			long o = l / 1048576L;
			long p = m / 1048576L;
			long q = n / 1048576L;
			return n + " bytes (" + q + " MiB) / " + m + " bytes (" + p + " MiB) up to " + l + " bytes (" + o + " MiB)";
		});
		this.method_37123("CPUs", () -> String.valueOf(Runtime.getRuntime().availableProcessors()));
		this.method_37121("hardware", () -> this.method_37128(new SystemInfo()));
		this.method_37123("JVM Flags", () -> {
			List<String> list = (List<String>)Util.getJVMFlags().collect(Collectors.toList());
			return String.format("%d total; %s", list.size(), String.join(" ", list));
		});
	}

	public void method_37122(String string, String string2) {
		this.field_33858.put(string, string2);
	}

	public void method_37123(String string, Supplier<String> supplier) {
		try {
			this.method_37122(string, (String)supplier.get());
		} catch (Exception var4) {
			field_33854.warn("Failed to get system info for {}", string, var4);
			this.method_37122(string, "ERR");
		}
	}

	private void method_37128(SystemInfo systemInfo) {
		HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
		this.method_37121("processor", () -> this.method_37130(hardwareAbstractionLayer.getProcessor()));
		this.method_37121("graphics", () -> this.method_37137(hardwareAbstractionLayer.getGraphicsCards()));
		this.method_37121("memory", () -> this.method_37131(hardwareAbstractionLayer.getMemory()));
	}

	private void method_37121(String string, Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception var4) {
			field_33854.warn("Failed retrieving info for group {}", string, var4);
		}
	}

	private void method_37126(List<PhysicalMemory> list) {
		int i = 0;

		for (PhysicalMemory physicalMemory : list) {
			String string = String.format("Memory slot #%d ", i++);
			this.method_37123(string + "capacity (MB)", () -> String.format("%.2f", (float)physicalMemory.getCapacity() / 1048576.0F));
			this.method_37123(string + "clockSpeed (GHz)", () -> String.format("%.2f", (float)physicalMemory.getClockSpeed() / 1.0E9F));
			this.method_37123(string + "type", physicalMemory::getMemoryType);
		}
	}

	private void method_37135(VirtualMemory virtualMemory) {
		this.method_37123("Virtual memory max (MB)", () -> String.format("%.2f", (float)virtualMemory.getVirtualMax() / 1048576.0F));
		this.method_37123("Virtual memory used (MB)", () -> String.format("%.2f", (float)virtualMemory.getVirtualInUse() / 1048576.0F));
		this.method_37123("Swap memory total (MB)", () -> String.format("%.2f", (float)virtualMemory.getSwapTotal() / 1048576.0F));
		this.method_37123("Swap memory used (MB)", () -> String.format("%.2f", (float)virtualMemory.getSwapUsed() / 1048576.0F));
	}

	private void method_37131(GlobalMemory globalMemory) {
		this.method_37121("physical memory", () -> this.method_37126(globalMemory.getPhysicalMemory()));
		this.method_37121("virtual memory", () -> this.method_37135(globalMemory.getVirtualMemory()));
	}

	private void method_37137(List<GraphicsCard> list) {
		int i = 0;

		for (GraphicsCard graphicsCard : list) {
			String string = String.format("Graphics card #%d ", i++);
			this.method_37123(string + "name", graphicsCard::getName);
			this.method_37123(string + "vendor", graphicsCard::getVendor);
			this.method_37123(string + "VRAM (MB)", () -> String.format("%.2f", (float)graphicsCard.getVRam() / 1048576.0F));
			this.method_37123(string + "deviceId", graphicsCard::getDeviceId);
			this.method_37123(string + "versionInfo", graphicsCard::getVersionInfo);
		}
	}

	private void method_37130(CentralProcessor centralProcessor) {
		ProcessorIdentifier processorIdentifier = centralProcessor.getProcessorIdentifier();
		this.method_37123("Processor Vendor", processorIdentifier::getVendor);
		this.method_37123("Processor Name", processorIdentifier::getName);
		this.method_37123("Identifier", processorIdentifier::getIdentifier);
		this.method_37123("Microarchitecture", processorIdentifier::getMicroarchitecture);
		this.method_37123("Frequency (GHz)", () -> String.format("%.2f", (float)processorIdentifier.getVendorFreq() / 1.0E9F));
		this.method_37123("Number of physical packages", () -> String.valueOf(centralProcessor.getPhysicalPackageCount()));
		this.method_37123("Number of physical CPUs", () -> String.valueOf(centralProcessor.getPhysicalProcessorCount()));
		this.method_37123("Number of logical CPUs", () -> String.valueOf(centralProcessor.getLogicalProcessorCount()));
	}

	public void method_37124(StringBuilder stringBuilder) {
		stringBuilder.append("-- ").append("System Details").append(" --\n");
		stringBuilder.append("Details:");
		this.field_33858.forEach((string, string2) -> {
			stringBuilder.append("\n\t");
			stringBuilder.append(string);
			stringBuilder.append(": ");
			stringBuilder.append(string2);
		});
	}

	public String method_37120() {
		return (String)this.field_33858
			.entrySet()
			.stream()
			.map(entry -> (String)entry.getKey() + ": " + (String)entry.getValue())
			.collect(Collectors.joining(System.lineSeparator()));
	}
}
