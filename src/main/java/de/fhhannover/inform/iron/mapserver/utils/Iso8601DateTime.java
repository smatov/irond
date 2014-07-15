package de.fhhannover.inform.iron.mapserver.utils;

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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @since 0.3.0
 */
public class Iso8601DateTime {

	private static final SimpleDateFormat xsdDateTimeFormatter =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat xsdDateTimeTimezoneFormatter =
			new SimpleDateFormat("Z");
	private static final SimpleDateFormat millisecondsFormatter =
			new SimpleDateFormat("S");

	/**
	 * Thanks Ingo!
	 * @param dt
	 * @return
	 */
	public static String formatDate(Date dt) {
		// FIXME hack in order to get good timestamps for xsd:dateTime
		String timezone = xsdDateTimeTimezoneFormatter.format(dt);
		return xsdDateTimeFormatter.format(dt) + timezone.substring(0, 3) + ":"
				+ timezone.substring(3);
	}

	/**
	 * Return the milliseconds fraction of the given {@link Date}.
	 *
	 * @param dt the {@link Date} from with to extract the milliseconds
	 * @return a string containing the milliseconds, e.g. '123' for 0.123 seconds
	 */
	public static String formatMilliseconds(Date dt) {
		return millisecondsFormatter.format(dt);
	}
}
