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

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.exceptions.StorePublisherIdException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;
import java.io.IOException;
import java.util.List;

/**
 * Provides IF-MAP 2.1 compliant publisher ids 
 * 
 * @author aw
 * @author jk
 *
 */
public class PublisherIdProviderPropImpl implements PublisherIdProvider {
	
	private PropertiesReaderWriter mProperties;

	public PublisherIdProviderPropImpl(ServerConfigurationProvider serverConfig) 
		throws ProviderInitializationException {
		NullCheck.check(serverConfig, "serverConfig is null");
		String fileName = serverConfig.getPublisherIdMapFileName();
		if (fileName == null) {
			throw new ProviderInitializationException("publisher-id mapping file null");
		}
		
		try {
			mProperties = new PropertiesReaderWriter(fileName, true);
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}
	
	@Override
	public String getPublisherIdFor(ClientIdentifier clId) {
		NullCheck.check(clId, "clientIdentifier is null");
		return mProperties.getProperty(clId.getReadablePseudoIdentifier());
	}
	
	@Override
	public void storePublisherIdFor(ClientIdentifier clId,
			String publisherId) throws StorePublisherIdException {
		NullCheck.check(clId, "clientId is null");
		NullCheck.check(publisherId, "publisherId is null");
		try {
			mProperties.storeProperty(clId.getReadablePseudoIdentifier(), publisherId);
		} catch (IOException e) {
			throw new StorePublisherIdException(e.getMessage());
		}
	}

	@Override
	public List<String> getAllPublisherIds() {
		return mProperties.getAllValues();
	}
}
