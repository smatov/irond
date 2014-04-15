package de.fhhannover.inform.iron.mapserver.provider;

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

import de.fhhannover.inform.iron.mapserver.datamodel.DataModelService;
import javax.xml.transform.stream.StreamSource;

/**
 * Configuration options specific for the {@link DataModelService}
 * 
 * @author aw
 *
 */
public interface DataModelServerConfigurationProvider {
	
	/**
	 * @returns whether this identity type is to be treated case sensitive
	 */
	public boolean getIdentityTypeIsCaseSensitive(String identityType);
	
	/**
	 * @return whether the administrative domain is to be treated case sensitive
	 */
	public boolean getAdministrativeDomainIsCaseSensitive();
	
	/**
	 * @return whether the purge publisher operation is restricted
	 */
	public boolean getPurgePublisherIsRestricted();
	
	/**
	 * @return default max poll result size to be used
	 */
	public int getDefaultMaxPollResultSize();
	

	/**
	 * @return the number of bytes used as default max search result size
	 */
	public int getDefaultMaxSearchResultSize();
	
	/**
	 * @return true if certain validations and/or null checks should be enabled. 
	 */
	public boolean isSanityChecksEnabled();
	
	/**
	 * Shall the server provide a normalized distinguished name in search
	 * request even if the client does not handle normalization on its own.
	 * @return true if distinguished names shall be returned in strict
	 * RFC 2253 format.
	 */
	public boolean getStrictDistinguishedName();
	
	/**
	 * Shall the server provide normalized extended identities in search
	 * request even if the client does not handle normalization on its own.
	 * @return true if extended identities shall always be returned in
	 * normalized and canonical format.
	 */
	public boolean getStrictExtendedIdentity();
	
	/**
	 * Shall an extended identifier be schema validated?
	 * @return true if an extended identifier shall be validated against
	 * a XML schema file
	 */
	public boolean getXmlValidationExtendedIdentity();
	
	/**
	 * Shall all metadata be schema validated?
	 * @return true if metadata shall be validated against a XML schema
	 */
	public boolean getXmlValidatationMetadata();

	/**
	 * Is metadata validation in locked-down mode?
	 * Allows only metadata which can be validated against a known XSD
	 * @return true if operating in locked-down mode
	 */
	public boolean getXmlValidationMetadataLockDownMode();

	/**
	 * Is extended identifier validation in locked-down mode?
	 * Allows only extended identifier which can be validated against
	 * a known XSD
	 * @return true if operating in locked-down mode
	 */
	public boolean getXmlValidationExtendedIdentityLockDownMode();

	/**
	 * Returns a @link{StreamSource} for the corresponding schema file
	 * for extended identifiers
	 * @param uri namespace URI of the desired schema file
	 * @return schema file source defined in configuration file for ext. identifiers
	 */
	public StreamSource getExtendedIdentitySchema(String uri);

	/**
	 * Returns a @link{StreamSource} for the corresponding schema file
	 * for metadata
	 * @param uri namespace URI of the desired schema file
	 * @return schema file source deinfed configuration file for metadata
	 */
	public StreamSource getMetadataSchema(String uri);
}
