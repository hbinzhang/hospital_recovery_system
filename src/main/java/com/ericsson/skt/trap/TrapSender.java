package com.ericsson.skt.trap;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.ericsson.skt.rest.util.InterfaceConsts;

public class TrapSender {

	private Snmp snmp = null;

	private Address targetAddress = null;

	public void initComm() throws IOException {

		targetAddress = GenericAddress.parse("udp:192.168.1.101/162");
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		transport.listen();

	}

	@SuppressWarnings("unchecked")
	public void sendPDU() throws IOException {

		CommunityTarget target = new CommunityTarget();
		target.setAddress(targetAddress);

		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);

		PDU pdu = genTrap(false);

		ResponseEvent respEvnt = snmp.send(pdu, target);

		if (respEvnt != null && respEvnt.getResponse() != null) {
			Vector<VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println(recVB.getOid() + " : " + recVB.getVariable());
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		pdu = genTrap(true);

		respEvnt = snmp.send(pdu, target);

		if (respEvnt != null && respEvnt.getResponse() != null) {
			Vector<VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println(recVB.getOid() + " : " + recVB.getVariable());
			}
		}
	}
	
	private PDU genTrap(boolean clearFlag) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_ADDITIONAL_INFO), new OctetString("0")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_ADDITIONAL_TEXT), new OctetString("Network=storage,Aggregator=storage,EthernetPort=2")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_EVENT_TIME), new OctetString("07:df:0a:1c:01:09:01:00:2b:00:00")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_EVENT_TYPE), new OctetString("2")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_MAJOR), new OctetString("193")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_MINOR), new OctetString("2031681")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_PROBABLE_CAUSE), new OctetString("8")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_SEQUENCE_NO), new OctetString("2708555")));
		if(clearFlag)
			pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_SEVERITY), new OctetString("1.3.6.1.4.1.193.183.4.2.0.7")));
		else
			pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_SEVERITY), new OctetString("1.3.6.1.4.1.193.183.4.2.0.4")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_SOURCE), new OctetString("Region=CMRI,CeeFunction=1,Host=compute-0-6,Network=storage,Aggregator=storage,EthernetPort=2")));
		pdu.add(new VariableBinding(new OID(InterfaceConsts.OID_SPECIFIC_PROBLEM), new OctetString("Ethernet Port Fault")));
		pdu.setType(PDU.TRAP);

		return pdu;
	}

	public static void main(String[] args) {
		try {
			TrapSender sender = new TrapSender();
			sender.initComm();
			sender.sendPDU();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
