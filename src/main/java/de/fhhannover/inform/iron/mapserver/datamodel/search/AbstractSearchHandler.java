package de.fhhannover.inform.iron.mapserver.datamodel.search;

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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH
 * research group at the Fachhochschule Hannover.
 *
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPep;
import de.fhhannover.inform.iron.mapserver.datamodel.Publisher;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Just some routines for initialization to abstract {@link BasicSearchHandler}
 * and {@link ContinueSearchHandler} implementations.
 *
 * @author aw
 */
abstract class AbstractSearchHandler implements SearchHandler {

	protected static Logger sLogger = LoggingProvider.getTheLogger();
	protected final Identifier mStartIdent;
	protected final Filter mMatchLinksFilter;
	protected final Filter mResultFilter;
	protected final int mMaxDepth;
	protected final TerminalIdentifiers mTermIdentTypes;
	protected final Map<GraphElement, List<MetadataHolder>> mVisitedElements;
	protected int mCurDepth;

	// Needed for authorization
	private final Publisher mPub;
	private final IfmapPep mPep;

	private long mStartTime;
	private long mEndTime;

	AbstractSearchHandler(SearchRequest sreq, Identifier start,
			Map<GraphElement, List<MetadataHolder>> visitedElements,
			int startDepth, Publisher pub, IfmapPep pep) {
		NullCheck.check(sreq, "sreq is null");

		if (start == null) {
			mStartIdent = sreq.getStartIdentifier();
		} else {
			mStartIdent = start;
		}

		NullCheck.check(mStartIdent, "start identifier is null");

		mMatchLinksFilter = sreq.getMatchLinksFilter();
		mResultFilter = sreq.getResultFilter();
		NullCheck.check(mMatchLinksFilter, "match links filter is null");
		NullCheck.check(mResultFilter, "result filter is null");

		mMaxDepth = sreq.getMaxDepth();
		mTermIdentTypes = sreq.getTerminalIdentifiers();
		NullCheck.check(mTermIdentTypes, "terminal identifiers is null");

		if (visitedElements == null) {
			mVisitedElements = new HashMap<GraphElement, List<MetadataHolder>>();
		} else {
			mVisitedElements = visitedElements;
		}

		mCurDepth = startDepth;

		NullCheck.check(pub, "client is null");
		mPub = pub;

		NullCheck.check(pep, "pep is null");
		mPep = pep;
	}

	AbstractSearchHandler(SearchRequest sreq, Publisher pub, IfmapPep pep) {
		this(sreq, null, null, 0, pub, pep);
	}

	@Override
	public void onStart() {
		mStartTime = System.currentTimeMillis();
	}

	@Override
	public void onEnd() {
		mEndTime = System.currentTimeMillis();
	}

	@Override
	public final Identifier getStartIdentifier() {
		return mStartIdent;
	}

	@Override
	public final void nextDepth() {
		mCurDepth++;
	}

	protected final int getCurrentDepth() {
		return mCurDepth;
	}

	protected final Filter getMatchLinksFilter() {
		return mMatchLinksFilter;
	}

	protected final Filter getResultFilter() {
		return mResultFilter;
	}

	protected final int getMaxDepth() {
		return mMaxDepth;
	}

	protected final TerminalIdentifiers getTerminalIdentifiers() {
		return mTermIdentTypes;
	}

	protected List<MetadataHolder> authorized(List<MetadataHolder> mhlist) {
		return mPep.isSearchAuthorized(mPub, mhlist);
	}

	public final Map<GraphElement, List<MetadataHolder>> getVisitedElements() {
		return mVisitedElements;
	}

	public final long getStartTime() {
		return mStartTime;
	}

	public final long getEndTime() {
		return mEndTime;
	}

	public final Set<GraphElement> getVisitedGraphElements() {
		return mVisitedElements.keySet();
	}
}
