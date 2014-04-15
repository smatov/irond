//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.08.12 at 05:23:43 PM CEST 
//


package org.trustedcomputinggroup._2010.ifmap._2;

/*
 * #%L
 * ====================================================
 *   _____                _     ____  _____ _   _ _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \|  ___| | | | | | |
 *    | | | '__| | | / __| __|/ / _` | |_  | |_| | |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _| |  _  |  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_|   |_| |_|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Fachhochschule Hannover 
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.inform.fh-hannover.de/
 * 
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2014 Trust@FHH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="2">
 *           &lt;element name="access-request" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}AccessRequestType"/>
 *           &lt;element name="identity" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}IdentityType"/>
 *           &lt;element name="ip-address" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}IPAddressType"/>
 *           &lt;element name="mac-address" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}MACAddressType"/>
 *           &lt;element name="device" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}DeviceType"/>
 *         &lt;/choice>
 *         &lt;element name="metadata" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}MetadataListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultItemType", propOrder = {
    "accessRequestOrIdentityOrIpAddress",
    "metadata"
})
public class ResultItemType {

    @XmlElements({
        @XmlElement(name = "access-request", type = AccessRequestType.class),
        @XmlElement(name = "identity", type = IdentityType.class),
        @XmlElement(name = "ip-address", type = IPAddressType.class),
        @XmlElement(name = "mac-address", type = MACAddressType.class),
        @XmlElement(name = "device", type = DeviceType.class)
    })
    protected List<Object> accessRequestOrIdentityOrIpAddress;
    protected MetadataListType metadata;

    /**
     * Gets the value of the accessRequestOrIdentityOrIpAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessRequestOrIdentityOrIpAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessRequestOrIdentityOrIpAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccessRequestType }
     * {@link IdentityType }
     * {@link IPAddressType }
     * {@link MACAddressType }
     * {@link DeviceType }
     * 
     * 
     */
    public List<Object> getAccessRequestOrIdentityOrIpAddress() {
        if (accessRequestOrIdentityOrIpAddress == null) {
            accessRequestOrIdentityOrIpAddress = new ArrayList<Object>();
        }
        return this.accessRequestOrIdentityOrIpAddress;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * @return
     *     possible object is
     *     {@link MetadataListType }
     *     
     */
    public MetadataListType getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetadataListType }
     *     
     */
    public void setMetadata(MetadataListType value) {
        this.metadata = value;
    }

}
