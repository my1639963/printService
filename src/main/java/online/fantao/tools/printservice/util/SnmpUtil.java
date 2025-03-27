package online.fantao.tools.printservice.util;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SnmpUtil {
    private static final String COMMUNITY = "public";
    private static final int TIMEOUT = 300;
    private static final int RETRIES = 3;

    /**
     * 获取打印机IP地址和端口号
     * @param printerName 打印机名称
     * @return 包含IP和端口的信息
     */
    public static Map<String, String> getPrinterNetworkInfo(String printerName) {
        Map<String, String> result = new HashMap<>();
        try {
            // 获取所有网络接口
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            
            // 筛选有效的IPv4网络接口
            List<String> ipPrefixes = interfaces.stream()
                .filter(ni -> {
                    try {
                        return ni.isUp() && !ni.isLoopback() && !ni.isVirtual();
                    } catch (SocketException e) {
                        log.warn("检查网络接口状态失败: {}", e.getMessage());
                        return false;
                    }
                })
                .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                .filter(addr -> addr instanceof java.net.Inet4Address)
                .map(addr -> addr.getHostAddress().substring(0, addr.getHostAddress().lastIndexOf(".") + 1))
                .collect(Collectors.toList());

            // 对每个网段进行扫描
            for (String ipPrefix : ipPrefixes) {
                log.debug("扫描网段: {}", ipPrefix);
                for (int i = 1; i < 255; i++) {
                    String ip = ipPrefix + i;
                    if (isPrinter(ip, printerName)) {
                        result.put("ipAddress", ip);
                        result.put("port", "9100"); // 默认端口
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取打印机网络信息失败", e);
        }
        return result;
    }

    /**
     * 检查指定IP是否为目标打印机
     * @param ip IP地址
     * @param printerName 打印机名称
     * @return 是否为目标打印机
     */
    private static boolean isPrinter(String ip, String printerName) {
        try {
            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
            transport.listen();

            Address targetAddress = GenericAddress.parse("udp:" + ip + "/161");
            CommunityTarget<Address> target = new CommunityTarget<>();
            target.setCommunity(new OctetString(COMMUNITY));
            target.setVersion(org.snmp4j.mp.SnmpConstants.version2c);
            target.setAddress(targetAddress);
            target.setTimeout(TIMEOUT);
            target.setRetries(RETRIES);

            Snmp snmp = new Snmp(transport);
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"))); // sysName
            pdu.setType(PDU.GET);

            ResponseEvent<?> response = snmp.send(pdu, target);
            if (response != null && response.getResponse() != null) {
                String deviceName = response.getResponse().get(0).getVariable().toString();
                return deviceName.contains(printerName);
            }
        } catch (Exception e) {
            log.debug("检查IP {} 失败: {}", ip, e.getMessage());
        }
        return false;
    }
} 