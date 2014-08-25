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

import java.util.List;
import java.util.Properties;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.StorePublisherIdException;

/**
 * Provides functionality to map an identified MAPC to a PublisherId.
 * 
 * A MAPC can be identified either by it's username used for basic authentication
 * or by it's distinguished name used in the certificate.
 * This interface provides a mapping between these identifiers and the
 * publisher-id.
 * 
 * 
 * @author aw
 *
 */
public interface PublisherIdProvider {
	
	/**
	 * Returns a PublisherId for the MAPC identified by the
	 * {@link ClientIdentifier}.
	 * 
	 * The easiest implementation should be a {@link Properties} file mapping
	 * username/distinguished name to a PublisherId.
	 * 
	 * @see ClientIdentifier
	 * @param clientId identifies the MAPC.
	 * @return a String representing the publisher-id or null if not found.
	 */
	public String getPublisherIdFor(ClientIdentifier clientId);
	
	/**
	 * Store a mapping of a {@link ClientIdentifier} to a publisher-id.
	 * 
	 * @param clientId
	 * @param publisherId
	 * @throws StorePublisherIdException in case it could not be stored.
	 */
	public void storePublisherIdFor(ClientIdentifier clientId, String publisherId)
		throws StorePublisherIdException;
	
	/**
	 * @return a {@link List} containing all publisher-id's known to
	 * this instance.
	 */
	public List<String> getAllPublisherIds();
	
}
