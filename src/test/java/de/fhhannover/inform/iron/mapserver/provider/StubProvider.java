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
import de.fhhannover.inform.iron.mapserver.exceptions.StorePublisherIdException;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

public class StubProvider {
	
	public static SessionIdProvider getSessionIdProvStub() {
		return new SessionIdProvider() {
			
			private int count = 0;
			
			@Override
			public String getSessionId() {
				return "" + count++;
			}
		};
	}

	public static PublisherIdProvider getPublisherIdProvStub() {
		return new PublisherIdProvider() {
			
			@Override
			public String getPublisherIdFor(ClientIdentifier clientIdentifier) {
				return ""+clientIdentifier.hashCode();
			}

			@Override
			public void storePublisherIdFor(ClientIdentifier clientId,
					String publisherId) throws StorePublisherIdException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<String> getAllPublisherIds() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}


	public static ServerConfigurationProvider getServerConfStub(final int timeout) {
		return getServerConfStub(timeout, null, null, 1000, 1000, true);
	}

	public static ServerConfigurationProvider getServerConfStub(String basicauthmap) {
		return getServerConfStub(180, basicauthmap, null, 1000, 1000, true);
	}
	public static ServerConfigurationProvider getServerConfStub(
			String basicauthFile, String authFile) {
		return getServerConfStub(180, basicauthFile, authFile, 1000, 1000, true);
	}

	public static ServerConfigurationProvider getServerConfStub(boolean xmlValidate) {
		return getServerConfStub(180, null, null, 1000, 1000, xmlValidate);
	}

	public static ServerConfigurationProvider getServerConfStub(final int timeout,
			final String basicAuthFile, final String authorizationFile,
			final int maxsearchsize, final int maxpollsize, final boolean xmlValidate) {
		
		return new ServerConfigurationProvider() {
		
			private String mBasicAuthPropFile = basicAuthFile;
			private String mAuthorizatioPropnFile;
			private int mTimeout = timeout;
			private int mDefMaxSearchResSize = maxsearchsize;
			private int mDefMaxPollResSize = maxpollsize;
			private boolean mXmlValidate = xmlValidate;
			
			@Override
			public String getTrustStorePasswort() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getTrustStoreFileName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPublisherIdMapFileName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getKeyStorePasswort() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getKeyStoreFileName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getDefaultMaxPollResultSize() {
				return mDefMaxPollResSize;
			}
			
			@Override
			public int getCertAuthPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getBasicAuthPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getBasicAuthenticationPropFileName() {
				return mBasicAuthPropFile;
			}

			@Override
			public int getEventProcessorWorkersCount() {
				return 1;
			}

			@Override
			public int getActionProcessorForwardersCount() {
				return 1;
			}

			@Override
			public int getActionProcessorWorkersCount() {
				return 1;
			}

			@Override
			public int getEventProcessorForwardersCount() {
				return 1;
			}

			@Override
			public long getSessionTimeOutMilliSeconds() {
				return mTimeout;
			}

			@Override
			public int getDefaultMaxSearchResultSize() {
				return mDefMaxSearchResSize;
			}

			@Override
			public boolean getIdentityTypeIsCaseSensitive(String identityType) {
				return true;
			}

			@Override
			public boolean getAdministrativeDomainIsCaseSensitive() {
				return true;
			}

			@Override
			public boolean getPurgePublisherIsRestricted() {
				return false;
			}

			@Override
			public String getAuthorizationPropFileName() {
				return mAuthorizatioPropnFile;
			}

			@Override
			public boolean isLogRaw() {
				return false;
			}

			@Override
			public boolean getXmlValidation() {
				return mXmlValidate;
			}

			@Override
			public String[] getSchemaFileNames() {
				return new String[] {"src/main/templates/schema/soap12.xsd"};
			}
			
			@Override
			public boolean getStrictExtendedIdentity() {
				return false;
			}
			
			@Override
			public boolean getStrictDistinguishedName() {
				return false;
			}
			
			@Override
			public boolean getSocketKeepAlive() {
				return false;
			}
			
			@Override
			public int getSocketTimeout() {
				return 0;
			}
			
			@Override
			public boolean getXmlValidatationMetadata() {
				return false;
			}
			
			@Override
			public StreamSource getMetadataSchema(String uri) {
				return null;
			}

			@Override
			public boolean getXmlValidationExtendedIdentity() {
			    return false;
			}

			@Override
			public boolean getXmlValidationMetadataLockDownMode() {
			    return false;
			}

			@Override
			public boolean getXmlValidationExtendedIdentityLockDownMode() {
			    return false;
			}

			@Override
			public StreamSource getExtendedIdentitySchema(String uri) {
			    return null;
			}

			@Override
			public boolean isSanityChecksEnabled() {
				// we are running tests, so this should be fine
				return true;
			}

			@Override
			public boolean isRootLinkEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

		};
	}

	public static PublisherIdGenerator getPublisherIdGenStub() {
		return new PublisherIdGenerator() {

			@Override
			public String generatePublisherIdFor(ClientIdentifier clientId,
					PublisherIdProvider pubIdProv) {
				return clientId.getUsername() + "{hash: " + clientId.hashCode()+ "}";
			}
		};
	}




	public static AuthorizationProvider getAuthorizationProvStub() {
		return new AuthorizationProvider() {
			
			@Override
			public boolean isWriteAllowed(ClientIdentifier clientId) {
				return true;
			}
		};
	}

}
