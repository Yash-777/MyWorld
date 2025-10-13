package com.github.yash777.myworld.api.online;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.yash777.basic.JsonUtil;
import com.sun.management.OperatingSystemMXBean;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that exposes system and network information through HTTP endpoints.
 *
 * <p>This controller provides two key endpoints:</p>
 * <ul>
 *   <li><b>GET /system-info/basic</b> — Returns general system information including CPU, memory, OS, and disk statistics.</li>
 *   <li><b>GET /system-info/network</b> — Returns network details such as hostnames, IPv4/IPv6 addresses, and network interfaces.</li>
 * </ul>
 *
 * <p>Intended primarily for diagnostic or monitoring purposes.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 *   GET /system-info/basic
 *   GET /system-info/network
 * </pre>
 *
 * @author Yash
 * @since 1.0
 */
@Tag(name = "Online Module", description = "Online Module APIs for JSON, XML, Text")
@RestController
@RequestMapping("/online/system-info")
@SpringBootApplication
public class SystemInfoController {
	
	/**
	 * Entry point for standalone testing of system and network information.
	 * <p>This method can be executed directly to print formatted JSON representations
	 * of system properties and network configuration to the console.</p>
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		System.out.println("System Properties Info:\n" + System.getProperties());
		System.out.println("System Properties JSON:\n" + JsonUtil.toPrettyJson(System.getProperties()));
		SystemInfoController obj = new SystemInfoController();
		System.out.println("System Basic Info:\n" + JsonUtil.toPrettyJson(obj.getBasicSystemInfo()));
		System.out.println("System Network Info:\n" + JsonUtil.toPrettyJson(obj.getNetworkInfo()));
	}
	
	/**
	 * Retrieves detailed system information such as CPU, memory, operating system,
	 * Java runtime, and disk storage statistics.
	 *
	 * <p>Internally, this method uses {@link ManagementFactory} and
	 * {@link OperatingSystemMXBean} to gather system-level metrics.</p>
	 *
	 * @return a map containing structured system information, including:
	 *         <ul>
	 *           <li><b>hostname</b> — Local host name</li>
	 *           <li><b>javaVersion</b> — Java runtime version</li>
	 *           <li><b>osName / osVersion / osArch</b> — OS metadata</li>
	 *           <li><b>cpuCores</b> — Number of available processors</li>
	 *           <li><b>heapMemoryMax</b> — JVM heap memory limit</li>
	 *           <li><b>physicalMemoryTotal / Free</b> — System physical memory usage</li>
	 *           <li><b>disks</b> — List of file stores with total and usable space</li>
	 *         </ul>
	 */
	@GetMapping("/basic")
	public Map<String, Object> getBasicSystemInfo() {
		Map<String, Object> info = new LinkedHashMap<>();
		
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			String username = System.getProperty("user.name");
			String javaVersion = System.getProperty("java.version");
			String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			
			Properties props = System.getProperties();
			String osName = props.getProperty("os.name");
			String osArch = props.getProperty("os.arch");
			String osVersion = props.getProperty("os.version");
			
			int cpuCores = Runtime.getRuntime().availableProcessors();
			long heapMax = Runtime.getRuntime().maxMemory();
			
			OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			long totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
			long freePhysicalMemory = osBean.getFreePhysicalMemorySize();
			long diskCache = (long) (totalPhysicalMemory * 0.6);
			
			info.put("hostname", hostname);
			info.put("user", username);
			info.put("pid", pid);
			info.put("javaVersion", javaVersion);
			info.put("osName", osName);
			info.put("osVersion", osVersion);
			info.put("osArch", osArch);
			info.put("cpuCores", cpuCores);
			
			info.put("heapMemoryMax", formatBytes(heapMax));
			info.put("physicalMemoryTotal", formatBytes(totalPhysicalMemory));
			info.put("physicalMemoryFree", formatBytes(freePhysicalMemory));
			info.put("diskCacheAutoConfig", formatBytes(diskCache));
			
			List<Map<String, Object>> disks = new ArrayList<>();
			for (FileStore store : FileSystems.getDefault().getFileStores()) {
				try {
					Map<String, Object> disk = new LinkedHashMap<>();
					disk.put("disk", store.toString());
					disk.put("type", store.type());
					disk.put("total", formatBytesSafe(store.getTotalSpace()));
					disk.put("usable", formatBytesSafe(store.getUsableSpace()));
					disks.add(disk);
				} catch (Exception e) {
					// Some filesystems may not support getUsableSpace
				}
			}
			info.put("disks", disks);
			
		} catch (Exception e) {
			info.put("error", e.getMessage());
		}
		
		return info;
	}
	
	/**
	 * Retrieves network-related information for the local machine.
	 * <p>This includes IPv4 and IPv6 addresses, network interfaces,
	 * and hostname. External ISP details are not resolved here and
	 * are returned as placeholders.</p>
	 *
	 * @return a map containing:
	 *         <ul>
	 *           <li><b>hostname</b> — Local host name</li>
	 *           <li><b>ipv4 / ipv6</b> — Primary IP addresses</li>
	 *           <li><b>ipv4List / ipv6List</b> — All detected IPs</li>
	 *           <li><b>networkInterfaces</b> — List of available interfaces</li>
	 *           <li><b>internetProvider</b> — Placeholder (not resolved)</li>
	 *         </ul>
	 */
	@GetMapping("/network")
	public Map<String, Object> getNetworkInfo() {
		Map<String, Object> netInfo = new LinkedHashMap<>();
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			netInfo.put("hostname", localHost.getHostName());
			netInfo.put("ipv4", localHost.getHostAddress());
			
			List<String> ipv6List = new ArrayList<>();
			List<String> ipv4List = new ArrayList<>();
			
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
				
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if (addr instanceof Inet6Address) {
						ipv6List.add(addr.getHostAddress());
					} else if (addr instanceof Inet4Address) {
						ipv4List.add(addr.getHostAddress());
					}
				}
			}
			
			netInfo.put("ipv4List", ipv4List);
			netInfo.put("ipv6List", ipv6List);
			netInfo.put("networkInterfaces", NetworkInterface.getNetworkInterfaces().toString());
			netInfo.put("internetProvider", "N/A (requires external IP lookup service)");
			
		} catch (Exception e) {
			netInfo.put("error", e.getMessage());
		}
		
		return netInfo;
	}
	
	/**
	 * Converts a byte value into human-readable megabytes (MB) and gigabytes (GB).
	 *
	 * @param bytes the number of bytes
	 * @return a map containing raw bytes and converted MB/GB values
	 */
	private Map<String, Object> formatBytes(long bytes) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("bytes", bytes);
		result.put("MB", bytes / (1024 * 1024));
		result.put("GB", bytes / (1024 * 1024 * 1024));
		return result;
	}
	
	/**
	 * Safe wrapper for {@link #formatBytes(long)} that catches exceptions during formatting.
	 *
	 * @param bytes the number of bytes to format
	 * @return formatted size data or an error map if conversion fails
	 */
	private Map<String, Object> formatBytesSafe(long bytes) {
		try {
			return formatBytes(bytes);
		} catch (Exception e) {
			return Map.of("error", e.getMessage());
		}
	}
}
