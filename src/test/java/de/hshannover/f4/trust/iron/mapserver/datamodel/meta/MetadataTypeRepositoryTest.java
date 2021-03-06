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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;


import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import junit.framework.TestCase;

public class MetadataTypeRepositoryTest extends TestCase {

	public void testNewType() {

		MetadataTypeRepository rep = MetadataTypeRepositoryImpl.newInstance();
		MetadataType ret1 = null, ret2 = null;

		try {
			ret1 = rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.singleValue);
			ret2 = rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.singleValue);

			assertSame(ret1, ret2);
			assertEquals(ret1, ret2);

			ret1 = rep.getTypeFor("http://example.com", "myns2",
					MetaCardinalityType.multiValue);
			ret2 = rep.getTypeFor("http://example.com", "myns2",
					MetaCardinalityType.multiValue);

			assertSame(ret1, ret2);
			assertEquals(ret1, ret2);


		} catch (InvalidMetadataException e) {
			fail();
		}
	}

	public void testBadNewType() {
		MetadataTypeRepository rep = MetadataTypeRepositoryImpl.newInstance();
		MetadataType ret1 = null, ret2 = null;

		try {
			ret1 = rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.singleValue);
			ret2 = rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.multiValue);
			fail();
		} catch (InvalidMetadataException e) {
			assertNotNull(ret1);
			assertNull(ret2);
		}

		try {
			ret1 = rep.getTypeFor("http://example2.com", "myns1",
					MetaCardinalityType.multiValue);
			ret2 = rep.getTypeFor("http://example2.com", "myns1",
					MetaCardinalityType.singleValue);
			fail();
		} catch (InvalidMetadataException e) {
			assertNotNull(ret1);
			assertNull(ret2);
		}
	}

	public void testContains() {
		MetadataTypeRepository rep = MetadataTypeRepositoryImpl.newInstance();

		assertFalse(rep.contains("http://example.com", "myns1", MetaCardinalityType.singleValue));
		assertFalse(rep.contains("http://example.com", "myns2", MetaCardinalityType.multiValue));

		try {
			rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.singleValue);
			rep.getTypeFor("http://example.com", "myns2",
					MetaCardinalityType.multiValue);
		} catch (InvalidMetadataException e) {
			fail();
		}

		assertTrue(rep.contains("http://example.com", "myns1", MetaCardinalityType.singleValue));
		assertTrue(rep.contains("http://example.com", "myns2", MetaCardinalityType.multiValue));
		assertFalse(rep.contains("http://example.com", "myns2", MetaCardinalityType.singleValue));
		assertFalse(rep.contains("http://example.com", "myns1", MetaCardinalityType.multiValue));
	}

	public void testClear() {
		MetadataTypeRepository rep = MetadataTypeRepositoryImpl.newInstance();
		MetadataType ret1 = null, ret2 = null;

		try {
			ret1 = rep.getTypeFor("http://example.com", "myns1",
					MetaCardinalityType.singleValue);
			ret2 = rep.getTypeFor("http://example.com", "myns2",
					MetaCardinalityType.multiValue);
			assertNotNull(ret1);
			assertNotNull(ret2);
		} catch (InvalidMetadataException e) {
			fail();
		}
		assertTrue(rep.contains("http://example.com", "myns1", MetaCardinalityType.singleValue));
		assertTrue(rep.contains("http://example.com", "myns2", MetaCardinalityType.multiValue));

		rep.clear();

		assertFalse(rep.contains("http://example.com", "myns1", MetaCardinalityType.singleValue));
		assertFalse(rep.contains("http://example.com", "myns2", MetaCardinalityType.multiValue));
	}
}
