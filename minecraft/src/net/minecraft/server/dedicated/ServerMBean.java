package net.minecraft.server.dedicated;

import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

/**
 * A dynamic management object for a Minecraft Server.
 * 
 * <p>It exposes the average tick time and the historical tick times of the
 * Minecraft Server.
 * 
 * @see javax.management.DynamicMBean
 */
public final class ServerMBean implements DynamicMBean {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftServer server;
	private final MBeanInfo mBeanInfo;
	private final Map<String, ServerMBean.Entry> entries = (Map<String, ServerMBean.Entry>)Stream.of(
			new ServerMBean.Entry("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class),
			new ServerMBean.Entry("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", long.class)
		)
		.collect(Collectors.toMap(entry -> entry.name, Function.identity()));

	private ServerMBean(MinecraftServer server) {
		this.server = server;
		MBeanAttributeInfo[] mBeanAttributeInfos = (MBeanAttributeInfo[])this.entries
			.values()
			.stream()
			.map(ServerMBean.Entry::createInfo)
			.toArray(MBeanAttributeInfo[]::new);
		this.mBeanInfo = new MBeanInfo(
			ServerMBean.class.getSimpleName(), "metrics for dedicated server", mBeanAttributeInfos, null, null, new MBeanNotificationInfo[0]
		);
	}

	/**
	 * Registers a dynamic MBean for a Minecraft Server.
	 * 
	 * @param server the server to have the MBean
	 */
	public static void register(MinecraftServer server) {
		try {
			ManagementFactory.getPlatformMBeanServer().registerMBean(new ServerMBean(server), new ObjectName("net.minecraft.server:type=Server"));
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException var2) {
			LOGGER.warn("Failed to initialise server as JMX bean", (Throwable)var2);
		}
	}

	/**
	 * Returns the server's current average tick time, in milliseconds.
	 */
	private float getAverageTickTime() {
		return this.server.getTickTime();
	}

	/**
	 * Returns the server's historical tick times, in milliseconds.
	 */
	private long[] getTickTimes() {
		return this.server.lastTickLengths;
	}

	@Nullable
	public Object getAttribute(String attribute) {
		ServerMBean.Entry entry = (ServerMBean.Entry)this.entries.get(attribute);
		return entry == null ? null : entry.getter.get();
	}

	public void setAttribute(Attribute attribute) {
	}

	public AttributeList getAttributes(String[] attributes) {
		List<Attribute> list = (List<Attribute>)Arrays.stream(attributes)
			.map(this.entries::get)
			.filter(Objects::nonNull)
			.map(entry -> new Attribute(entry.name, entry.getter.get()))
			.collect(Collectors.toList());
		return new AttributeList(list);
	}

	public AttributeList setAttributes(AttributeList attributes) {
		return new AttributeList();
	}

	@Nullable
	public Object invoke(String actionName, Object[] params, String[] signature) {
		return null;
	}

	public MBeanInfo getMBeanInfo() {
		return this.mBeanInfo;
	}

	/**
	 * Represents a read-only attribute of the server MBean.
	 */
	static final class Entry {
		final String name;
		final Supplier<Object> getter;
		private final String description;
		private final Class<?> type;

		Entry(String name, Supplier<Object> getter, String description, Class<?> type) {
			this.name = name;
			this.getter = getter;
			this.description = description;
			this.type = type;
		}

		private MBeanAttributeInfo createInfo() {
			return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
		}
	}
}
