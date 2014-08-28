/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.12 at 05:23:43 PM CEST
//


package org.trustedcomputinggroup._2010.ifmap._2;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PollResultType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PollResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="searchResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}SearchResultType"/>
 *           &lt;element name="updateResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}UpdateResultType"/>
 *           &lt;element name="deleteResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}DeleteResultType"/>
 *           &lt;element name="notifyResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}NotifyResultType"/>
 *           &lt;element name="errorResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}ErrorResultType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PollResultType", propOrder = {
    "searchResultOrUpdateResultOrDeleteResult"
})
public class PollResultType {

    @XmlElements({
        @XmlElement(name = "deleteResult", type = DeleteResultType.class),
        @XmlElement(name = "notifyResult", type = NotifyResultType.class),
        @XmlElement(name = "errorResult", type = ErrorResultType.class),
        @XmlElement(name = "updateResult", type = UpdateResultType.class),
        @XmlElement(name = "searchResult", type = SearchResultType.class)
    })
    protected List<Object> searchResultOrUpdateResultOrDeleteResult;

    /**
     * Gets the value of the searchResultOrUpdateResultOrDeleteResult property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchResultOrUpdateResultOrDeleteResult property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchResultOrUpdateResultOrDeleteResult().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeleteResultType }
     * {@link NotifyResultType }
     * {@link ErrorResultType }
     * {@link UpdateResultType }
     * {@link SearchResultType }
     *
     *
     */
    public List<Object> getSearchResultOrUpdateResultOrDeleteResult() {
        if (searchResultOrUpdateResultOrDeleteResult == null) {
            searchResultOrUpdateResultOrDeleteResult = new ArrayList<Object>();
        }
        return this.searchResultOrUpdateResultOrDeleteResult;
    }

}
