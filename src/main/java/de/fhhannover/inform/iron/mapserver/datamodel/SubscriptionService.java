package de.fhhannover.inform.iron.mapserver.datamodel;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Link;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataState;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataType;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.datamodel.search.ModifiablePollResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.ModifiableSearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.PollResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchHandler;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Searcher;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchingFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Subscription;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SubscriptionEntry;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SubscriptionState;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyObservedException;
import de.fhhannover.inform.iron.mapserver.exceptions.NoSuchSubscriptionException;
import de.fhhannover.inform.iron.mapserver.exceptions.PollResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.ResponseCreationException;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.messages.SearchResultType;
import de.fhhannover.inform.iron.mapserver.messages.SubPublishRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubSubscribeRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeDelete;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeUpdate;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.trust.TrustService;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Class to handle SubscriptionRequests and unfortunately much more.
 * 
 * @since 0.1.0
 * @author aw
 */
public class SubscriptionService {
	
	private static Logger sLogger = LoggingProvider.getTheLogger();
	private static String sName = "SubscriptionService";
	
	// FIXME
	private static final DataModelServerConfigurationProvider mConf = 
		DataModelService.getServerConfiguration();
	
	private final PublisherRep publisherRep;
	private final GraphElementRepository mGraph;
	private final SearchingFactory mSearchFac;
	
	private SubscriptionObserver mObserver;

	private final Map<Subscription, SubscriptionChangeState> mChangedSubscriptions;
	private final Set<Publisher> mChangedPublishers;
	private final List<MetadataHolder> mChangedMetadata;
	
	private long mLogicalTimeStamp;

	private TrustService mTrustExtensionService;

	SubscriptionService(GraphElementRepository graph, PublisherRep pRep,
			SearchingFactory sFac, TrustService trustService) {
		mGraph = graph;
		publisherRep = pRep;
		mSearchFac = sFac;
		mChangedSubscriptions = 
			CollectionHelper.provideMapFor(Subscription.class, SubscriptionChangeState.class);
		mChangedPublishers = CollectionHelper.provideSetFor(Publisher.class);
		mChangedMetadata = CollectionHelper.provideListFor(MetadataHolder.class);
		
		mLogicalTimeStamp = 0;

		mTrustExtensionService = trustService;
	}
	
	/**
	 * Handling of a {@link SubscriptionRequest} which is forwarded by the
	 * {@link DataModelService}.
	 * Look at all teh {@link SubPublishRequest} objects contained in the
	 * {@link SubscriptionRequest} and dispatch whether it's a update or
	 * a delete.
	 * 
	 * @param subscriptionReq
	 * @throws NoSuchSubscribeException
	 */
	void subscribe(SubscribeRequest subReq) throws NoSuchSubscriptionException {
		NullCheck.check(subReq, "subReq is null");
		
		Publisher pub = publisherRep.getPublisherBySessionId(subReq.getSessionId());
		sLogger.debug(sName + ": subscribe for " + pub.getPublisherId());
		
		for (SubSubscribeRequest ssr : subReq.getSubSubscribeRequests()) {
			if (ssr instanceof SubscribeUpdate)
					processSubscribeUpdate(pub, (SubscribeUpdate)ssr);
			else if (ssr instanceof SubscribeDelete)
					processSubscribeDelete(pub, (SubscribeDelete)ssr);
			else
				throw new SystemErrorException("Unknown SubscribeRequest Impl");
		}
	}

	/**
	 * Delete a {@link Subscription} from a {@link Publisher}
	 * 
	 * @param pub
	 * @param ssr
	 * @throws NoSuchSubscriptionException 
	 */
	private void processSubscribeDelete(Publisher pub, SubscribeDelete ssr)
			throws NoSuchSubscriptionException {
		
		NullCheck.check(pub, "pub is null");
		NullCheck.check(ssr, "ssr is null");
		
		SubscriptionState subState = pub.getSubscriptionState();
		String name = ssr.getName();
		Subscription sub = subState.getSubscription(name);
		
		if (sub != null) {
			// Sanity if mapping worked before
			if (!sub.getName().equals(ssr.getName()))
				throw new SystemErrorException("Bad subscription mapping for "
						+ ssr.getName() + ", got " + sub.getName());
				
			sLogger.debug(sName + ": Deleting subscription \"" + sub.getName()
					+ "\" of " + pub);
			clearContainers(sub);
			subState.removeSubscription(sub);
		} else {
			throw new NoSuchSubscriptionException("Subscription " +
					ssr.getName() + " does not exist");
		}
	}

	/**
	 * Create a new {@link Subscription}, attach it to a {@link Publisher},
	 * run the first search.
	 * 
	 * If a {@link Subscription} with the same name is already there, reset
	 * it and update the {@link SearchResult} objects
	 * 
	 * @param pub
	 * @param ssr
	 */
	private void processSubscribeUpdate(Publisher pub, SubscribeUpdate ssr) {

		NullCheck.check(pub, "pub is null");
		NullCheck.check(ssr, "ssr is null");
		NullCheck.check(ssr.getSearchRequest(), "sreq is null");
		
		SubscriptionState subState = pub.getSubscriptionState();
		Subscription sub = subState.getSubscription(ssr.getName());
		
		sLogger.debug(sName + ": subscribe update \"" + ssr.getName() + "\" for "
				+ pub.getPublisherId());
		
		// TODO: Maybe we should throw something here to notify the MAPC about
		// this problem.
		if (subState.isPollResultsTooBig()) {
			sLogger.warn(sName + ": Ignoring subscribeUpdate as the PollResult "
					+ " grew too big and no notification was sent out yet");
			return;
		}
		
		// Check if subscription was already there and remove it if it was.
		if (sub != null) {
			sLogger.debug(sName + ": subscribe update for existing subscription"
					+ "\"" + sub.getName() + "\", removing existing subscription");
			clearContainers(sub);
			subState.removeSubscription(sub);
		}
			
		sub = mSearchFac.newSubscription(pub, ssr.getName(), ssr.getSearchRequest());
		subState.addSubscription(sub);
	
		try {
			ModifiableSearchResult initResult = runInitialSearch(sub);
			subState.getPollResult().addSearchResult(initResult);
		} catch (SearchResultsTooBigException e) {
			sub.setExceededSize();
			subState.getPollResult().addErrorResult(sub.getName());
			clearContainers(sub);
			sLogger.debug(sName + ": initial search too big: " + e.getReached()
					+ " bytes used, but only " + e.getLimit() + " bytes allowed.");
		}
			
		setHasChanges(sub);
		doResultNotifications();
		cleanup();
	}
	
	/**
	 * If a new {@link Subscription} is created, the first thing to do is to
	 * run a search in the current graph. While doing so, mark all contained
	 * {@link MetadataContainer} with this {@link Subscription}.
	 * Further, put the first {@link SearchResult} of type
	 * {@link SearchResultType#SEARCH} into the {@link PollResult}.
	 * 
	 * @param sub
	 */
	private ModifiableSearchResult runInitialSearch(Subscription sub) throws SearchResultsTooBigException {
		
		Set<GraphElement> visitedGraphElement = CollectionHelper.provideSetFor(GraphElement.class);
		Set<MetadataHolder> newMeta = CollectionHelper.provideSetFor(MetadataHolder.class);
		Set<Node> starters = CollectionHelper.provideSetFor(Node.class);
		ModifiableSearchResult initSres = mSearchFac.newCopySearchResult(sub.getName());
		SearchRequest sr = sub.getSearchRequest();
		int msrs = sr.maxSizeGiven() ? sr.getMaxResultSize() : mConf.getDefaultMaxSearchResultSize();
		
		SearchHandler handler = mSearchFac.newContinueSearchHandler(
				sr.getStartIdentifier(),
				0,
				sub,
				visitedGraphElement,
				newMeta,
				starters);
		
		Searcher searcher = mSearchFac.newSearcher(mGraph, handler);
		searcher.runSearch();

		/*
		 * TrustService
		 * 
		 * TTI - Dieses Set wird für das Hinzufügen der Trust-Token-Identifier
		 * benötigt. Es enthält die besuchten Nodes und garantiert, dass
		 * Identifier immer nur einen Trust-Token haben.
		 */
		Set<Node> visitedNodes = new HashSet<Node>();

		for (GraphElement ge : visitedGraphElement) {
			initSres.addGraphElement(ge);
			
			for (MetadataHolder mh : ge.getSubscriptionEntry(sub).getMetadataHolder()) {
				initSres.addMetadata(ge, mh.getMetadata());

				/*
				 * TrustService
				 * 
				 * Füge der Ergebnismenge, die Trust-Token hinzu.
				 */
				appendTtToResult(sub, initSres, mh, visitedNodes);
			}
		}
		
		// Need to check max-size manually as ContinueSearchHandler does not care
		if (initSres.getByteCount() > msrs)
			throw new SearchResultsTooBigException("SearchResult grew too big",
	 											   msrs, initSres.getByteCount());
		
		// Sanitize check: An initial search never leads to new Starters.
		if (starters.size() > 0)
			throw new SystemErrorException("Initial Subscription Search lead "
					+ " to Starters");
		
		return initSres;
	}
	
	/**
	 * TrustService
	 * 
	 * Diese Methode fügt der Ergebnismenge die Trust-Token hinzu.
	 * 
	 * @param sub
	 * @param res
	 * @param mh
	 * @param visitedNodes
	 */
	private void appendTtToResult(Subscription sub, ModifiableSearchResult res,
			MetadataHolder mh, Set<Node> visitedNodes) {
		Metadata ttm = mTrustExtensionService.getP2TTM(sub
				.getPublisherReference().getSessionId(), mh.getTrustToken(), mh
				.getMetadata().getTrustTokenId());
		res.addMetadata(mh.getGraphElement(), ttm);

		/*
		 * TrustService
		 * 
		 * TTI - Dieser Abschnitt fügt die Trust-Token der Identifier zu der
		 * Ergebnismenge hinzu.
		 */
		/*
		 * if (isNode(mh.getGraphElement()) &&
		 * !visitedNodes.contains(mh.getGraphElement())) { Node n = (Node)
		 * mh.getGraphElement(); visitedNodes.add(n); Metadata tti =
		 * mTrustExtensionService.getP2TTI(sub
		 * .getPublisherReference().getSessionId(), n.getTrustToken());
		 * res.addMetadata(mh.getGraphElement(), tti); } else if
		 * (isLink(mh.getGraphElement())) { Node n1 = ((Link)
		 * mh.getGraphElement()).getNode1(); Node n2 = ((Link)
		 * mh.getGraphElement()).getNode2(); addTti(sub, n1, res, visitedNodes);
		 * addTti(sub, n2, res, visitedNodes); }
		 */
	}

	/**
	 * TrustService
	 * 
	 * TTI - Diese Methode Dieser Abschnitt fügt die Trust-Token der Identifier
	 * zu der Ergebnismenge hinzu. Diese Methode wird von der
	 * {@link SubscriptionService#appendTtToResult(Subscription, ModifiableSearchResult, MetadataHolder, Set)}
	 * Methode benötigt.
	 * 
	 * @param sub
	 * @param n
	 * @param res
	 * @param visitedNodes
	 */
	private void addTti(Subscription sub, Node n, ModifiableSearchResult res,
			Set<Node> visitedNodes) {
		if (!visitedNodes.contains(n))
			res.addMetadata(n, mTrustExtensionService.getP2TTI(sub
					.getPublisherReference().getSessionId(), n.getTrustToken()));
	}

	/**
	 * TODO: for metadata to appear in a notifyResult the state of the
	 *       subscriptions before <b>all</b> changes because of publishUpdate
	 *       or publishDelete is taken. It's been like this some time before,
	 *       but one could think of the following scenario:
	 *       - A publishUpdate introduces a link somewhere, such that the
	 *	 subscription would be active for more identifiers and links.
	 *       - A consecutive notify of metadata would appear in the notifyResult
	 *	 because of the "new state" of the subscription.
	 *       As mentioned above, currently only the old state of subscriptions
	 *       is used.
	 *       
	 * @param changedMetadata
	 * @throws ResponseCreationException 
	 */
	void commitChanges(List<MetadataHolder> changes) {
		
		sLogger.debug(sName + ": Committing changes...");
		
		mChangedMetadata.addAll(changes);
		
		dumpChangedMetadata();
		doNotifyMetadata();
		doUpdateDeleteMetadata();
		doSearchers();
		removeUndeleted();
		buildNewResults();
		updateGraphState();
		increaseLogicalTimeStamp();
		markSubscriptionsGrewTooBig();
		markPublisherPollResultTooBig();
		doResultNotifications();
		cleanup();
		
		if (mConf.isSanityChecksEnabled())
			sanitizeState();
	}

	private void buildNewResults() {
		sLogger.trace(sName + ": Building new results");
		
		for (Entry<Subscription, SubscriptionChangeState> entry :
											mChangedSubscriptions.entrySet()) {
			Subscription sub = entry.getKey();
			SubscriptionChangeState subcs = entry.getValue();
			
			makeResults(sub, subcs.mNotifyMetadataHolders, SearchResultType.NOTIFY);
			makeResults(sub, subcs.mDeletedMetadataHolders, SearchResultType.DELETE);
			makeResults(sub, subcs.mAddedMetadataHolders, SearchResultType.UPDATE);
		}
	}
		
	private void makeResults(Subscription sub,
			Collection<MetadataHolder> results, SearchResultType type) {
		SubscriptionState state = sub.getPublisherReference().getSubscriptionState();
		ModifiableSearchResult sres = mSearchFac.newCopySearchResult(sub.getName(), type);
		
		if (results.size() == 0)
			return;

		/*
		 * TrustService
		 * 
		 * TTI - Dieses Set wird für das Hinzufügen der Trust-Token-Identifier
		 * benötigt. Es enthält die besuchten Nodes und garantiert, dass
		 * Identifier immer nur einen Trust-Token haben.
		 */
		Set<Node> visitedNodes = new HashSet<Node>();

		for (MetadataHolder mh : results) {
			sres.addMetadata(mh.getGraphElement(), mh.getMetadata());

			/*
			 * TrustService
			 * 
			 * Füge der Ergebnismenge, die Trust-Token hinzu.
			 */
			appendTtToResult(sub, sres, mh, visitedNodes);
		}
		state.getPollResult().addSearchResult(sres);

		setHasChanges(sub);
	}
	
	private void doSearchers() {
		try { for (Entry<Subscription, SubscriptionChangeState> entry : 
											mChangedSubscriptions.entrySet()) {
			Subscription sub = entry.getKey();
			SubscriptionChangeState subcs = entry.getValue();
			
			sLogger.trace(sName + ": Running DeleteSearchers for " + sub);
			doDeleteSearchers(sub, subcs);

			sLogger.trace(sName + ": Running ContinueSearchers for " + sub);
			doContinueSearchers(sub, subcs);

			sLogger.trace(sName + ": Running CleanupSearchers for " + sub);
			doCleanupSearchers(sub, subcs);
		} } catch (SearchResultsTooBigException e) {
			// This should never happen
			sLogger.error(sName + 
					": SearchResultsTooBig while updating Subscriptions");
			throw new SystemErrorException(
					"SearchResultsTooBig while updating Subscriptions");
		}
	}

	private void doCleanupSearchers(Subscription sub, SubscriptionChangeState subcs)
			throws SearchResultsTooBigException {
	
		for (Node starter : subcs.mDeleteStarters) {
			// might not have to run it anymore
			if (starter.getRemovedSubscriptionEntry(sub) == null)
				continue;
			
			SearchHandler handler = mSearchFac.newCleanupSearchHandler(
					starter.getIdentifier(), sub);
			
			Searcher searcher = mSearchFac.newSearcher(mGraph, handler);
			searcher.runSearch();
		}
	}

	private void doDeleteSearchers(Subscription sub, SubscriptionChangeState subcs) throws SearchResultsTooBigException {
		Set<Node> contStarters = CollectionHelper.provideSetFor(Node.class);
		Set<MetadataHolder> deleted = CollectionHelper.provideSetFor(MetadataHolder.class);
		SearchHandler handler = null;
		Searcher searcher = null;
			
		for (Node starter : subcs.mDeleteStarters) {
			
			// might not have to run it anymore if another delete searcher removed us.
			if (starter.getSubscriptionEntry(sub) == null)
				continue;
			
			handler = mSearchFac.newDeleteSearchHandler(
					starter.getIdentifier(),
					starter.getSubscriptionEntry(sub).getDepth(),
					sub,
					deleted,
					contStarters);
			
			searcher = mSearchFac.newSearcher(mGraph, handler);
			
			searcher.runSearch();
		}
		
		subcs.mDeletedMetadataHolders.addAll(deleted);
		subcs.mContinueStarter.addAll(contStarters);
	}

	private void doContinueSearchers(Subscription sub, SubscriptionChangeState subcs) throws SearchResultsTooBigException {
		Set<Node> curStarters = CollectionHelper.provideSetFor(Node.class);
		Set<MetadataHolder> added = CollectionHelper.provideSetFor(MetadataHolder.class);
		Set<Node> nextStarters = CollectionHelper.provideSetFor(Node.class);
		SearchHandler handler = null;
		Searcher searcher = null;
		// FIXME: unused here!
		Set<GraphElement> unusedVisited = CollectionHelper.provideSetFor(GraphElement.class);
		
	
		// prepare for the first run
		curStarters.addAll(subcs.mContinueStarter);
		
		while (curStarters.size() > 0) {
			for (Node starter : curStarters) {
				
				// If a DeleteSearcher came along and removed the entry, we
				// shouldn't run it again.
				if (starter.getSubscriptionEntry(sub) == null)
					continue;
			
				handler = mSearchFac.newContinueSearchHandler(
						starter.getIdentifier(),
						starter.getSubscriptionEntry(sub).getDepth(),
						sub,
						unusedVisited,
						added,
						nextStarters);
				
				searcher = mSearchFac.newSearcher(mGraph, handler);
				searcher.runSearch();
			}
		
			// We did them all
			curStarters.clear();
			curStarters.addAll(nextStarters);
			nextStarters.clear();
		}
		// the newly found metadata
		subcs.mAddedMetadataHolders.addAll(added);
	}

	private void updateGraphState() {
		sLogger.trace(sName + ": Updating graph state");
		
		for (MetadataHolder mh : mChangedMetadata) {
			switch (mh.getState()) {
			case NEW:
				mh.setState(MetadataState.UNCHANGED);
				break;
			case DELETED:
				// all references should be gone anyway
				break;
			case REPLACED:
				// all references should be gone anyway
				break;
				
			default:
				throw new SystemErrorException("Metadata with bad state " 
						+ mh.getState() + " in changes");
			}
		}
	}

	private void dumpChangedMetadata() {
		if (sLogger.isTraceEnabled()) {
			sLogger.trace(sName + ": Dumping changed metadata:");
			for (MetadataHolder mh : mChangedMetadata) {
				Metadata m = mh.getMetadata();
				sLogger.trace("\t" + m.getPrefixAndElement() 
						+ " [state=" + mh.getState()
						+ " cardinality=" + m.getCardinality()
						+ " lifetime=" + mh.getLifetime()
						+ " size=" + m.getByteCount() + " bytes"
						+ " on " + mh.getGraphElement().dummy() + "]");
			}
		}
	}

	private void doNotifyMetadata() {
		GraphElement ge;
		Subscription sub;
		SubscriptionChangeState subcs;
		List<MetadataHolder> toRemove = CollectionHelper.provideListFor(MetadataHolder.class);
		
		for (MetadataHolder mh : mChangedMetadata) {
			if (mh.isNotify()) {
				ge = mh.getGraphElement();
				
				for (SubscriptionEntry entry : ge.getSubscriptionEntries()) {
					sub = entry.getSubscription();
					
					if (matchesOnSubscription(ge, mh.getMetadata(), sub)) {
						subcs = getSubChangeState(sub);
						subcs.mNotifyMetadataHolders.add(mh);
					}
				}
				toRemove.add(mh);
				ge.removeMetadataHolder(mh);
				mh.getPublisher().removeMetadataHolder(mh);
			}
		}
		mChangedMetadata.removeAll(toRemove);
	}

	private void doUpdateDeleteMetadata() {
		for (MetadataHolder mh : mChangedMetadata) {
			switch (mh.getState()) {
			case DELETED:
				deleteMetadataHolder(mh);
				break;
			case NEW:
				addMetadataHolder(mh);
				break;
			case REPLACED:
				// Sanity Check: REPLACED is always singleValue
				if (!mh.getMetadata().isSingleValue())
					throw new SystemErrorException("REPLACED not singleValue");
				
				// Sanity Check: If we have replaced metadata, make sure
				// there is metadata in state NEW of the same type on the
				// same graph element.
				if (mConf.isSanityChecksEnabled()) {
					MetadataType type = mh.getMetadata().getType();
					GraphElement ge = mh.getGraphElement();
					List<MetadataHolder> mhs = ge.getMetadataHolder(type);
					List<MetadataHolder> tmp = CollectionHelper.provideListFor(MetadataHolder.class);
					
					for (MetadataHolder mh2 : mhs)
						if (mh2.isNew())
							tmp.add(mh2);
				
					// We checked singleValue before and expect a single
					// NEW one
					if (tmp.size() != 1)
						throw new SystemErrorException("REPLACED metadata, "
								+ "but NEW metadata size=" + tmp.size());
				}

				// Forget about it;
				mh.getGraphElement().removeMetadataHolder(mh);
				mh.getPublisher().removeMetadataHolder(mh);				
					
				break;
			default:
				throw new SystemErrorException("Bad metadata state: " + mh.getState());
			}
		}
	}

	private void addMetadataHolder(MetadataHolder mh) {
		Subscription sub;
		SubscriptionChangeState subcs;
		GraphElement ge = mh.getGraphElement();
		
		// Go through all existing subscriptions on this graph element and
		// check if one happens to match.
		for (SubscriptionEntry entry : ge.getSubscriptionEntries()) {
			sub = entry.getSubscription();
			
			if (matchesOnSubscription(ge, mh.getMetadata(), sub)) {
				subcs = getSubChangeState(sub);
				entry.addMetadataHolder(mh);
				subcs.mAddedMetadataHolders.add(mh);
			}
		}
		
		// check for subscriptions that might have to be extended
		checkForAddedSubGraph(ge, mh);
	}

	private void deleteMetadataHolder(MetadataHolder mh) {
		GraphElement ge = mh.getGraphElement();
		List<MetadataHolder> mhlist;
		Subscription sub;
		SubscriptionChangeState subcs;
		
		for (SubscriptionEntry entry : ge.getSubscriptionEntries()) {
			sub = entry.getSubscription();
			mhlist = entry.getMetadataHolder();
			
			if (mhlist.contains(mh)) {
				subcs = getSubChangeState(sub);
				// remove this MetadataHoder from the entry.
				entry.removeMetadataHolder(mh);
				// put into deleted for this subscription
				subcs.mDeletedMetadataHolders.add(mh);
				checkForDeletedSubGraph(ge, entry, subcs);
			}
		}
		
		// remove it from the graph
		ge.removeMetadataHolder(mh);
		
		// remove reference from publisher
		mh.getPublisher().removeMetadataHolder(mh);
	}
	
	private void checkForAddedSubGraph(GraphElement ge, MetadataHolder mh) {
		
		// Not possible for nodes
		if (isNode(ge))
			return;
		
		if (!isLink(ge))
			throw new SystemErrorException("GraphElement not Link nor Identifier");

		Link l = (Link)ge;
		Node n1 = l.getNode1();
		Node n2 = l.getNode2();
		Set<Subscription> subLinkSet = CollectionHelper.provideSetFor(Subscription.class);
		Set<Subscription> subSetN1 = CollectionHelper.provideSetFor(Subscription.class);
		Set<Subscription> subSetN2 = CollectionHelper.provideSetFor(Subscription.class);
		Set<Subscription> res1 = CollectionHelper.provideSetFor(Subscription.class);
		Set<Subscription> res2 = CollectionHelper.provideSetFor(Subscription.class);
		Set<Subscription> intersection = CollectionHelper.provideSetFor(Subscription.class);
		

		// create the sets
		for (SubscriptionEntry entry : l.getSubscriptionEntries())
			subLinkSet.add(entry.getSubscription());
			
		for (SubscriptionEntry entry : n1.getSubscriptionEntries())
			subSetN1.add(entry.getSubscription());

		for (SubscriptionEntry entry : n2.getSubscriptionEntries())
			subSetN2.add(entry.getSubscription());
		
		res1.addAll(subSetN1);
		res1.removeAll(subLinkSet);
		
		res2.addAll(subSetN2);
		res2.removeAll(subLinkSet);
		
		intersection.addAll(res1);
		intersection.retainAll(res2);
		res1.removeAll(intersection);
		res2.removeAll(intersection);
		
		for (Subscription sub : intersection) {
			SubscriptionEntry e1 = n1.getSubscriptionEntry(sub);
			SubscriptionEntry e2 = n2.getSubscriptionEntry(sub);
			Node lowerDepth = e1.getDepth() < e2.getDepth() ? n1 : n2;
			addToContinueStarterIfMatching(mh, sub, lowerDepth);
		}
		
		for (Subscription sub : res1)
			addToContinueStarterIfMatching(mh, sub, n1);

		for (Subscription sub : res2)
			addToContinueStarterIfMatching(mh, sub, n2);
			
	}
	
	private void addToContinueStarterIfMatching(MetadataHolder mh, Subscription sub, Node n) {
		Filter matchLinksFilter = sub.getSearchRequest().getMatchLinksFilter();
		SubscriptionChangeState subcs = getSubChangeState(sub);
		
		if (mh.getMetadata().matchesFilter(matchLinksFilter))
				subcs.mContinueStarter.add(n);
	}

	private void checkForDeletedSubGraph(GraphElement ge, SubscriptionEntry entry,
			SubscriptionChangeState subcs) {

		// Not possible for nodes
		if (isNode(ge))
			return;
		
		if (!isLink(ge))
			throw new SystemErrorException("GraphElement not Link nor Identifier");

		Link l = (Link)ge;
		Node n1 = l.getNode1();
		Node n2 = l.getNode2();
		Node greaterDepth = null;
		Subscription sub = entry.getSubscription();
		Filter matchLinks = sub.getSearchRequest().getMatchLinksFilter();
		SubscriptionEntry e1 = n1.getSubscriptionEntry(sub);
		SubscriptionEntry e2 = n2.getSubscriptionEntry(sub);
	
		// Sanity
		if (e1 == null && e2 == null)
			throw new SystemErrorException("UNEXPECTED: link had sub, but none "
					+ " of the nodes");

		// There's still metadata on the link for this subscription, so we don't
		// need to worry.
		if (entry.getMetadataHolder().size() > 0)
			return;
	
		// There is NEW metadata which will take over, thank god, we do not
		// delete the subscription in this case.
		if (ge.getMetadataHolderNew(matchLinks).size() > 0)
			return;
	
		// We don't need a rerun if the depth of both is the same, then
		// n1 and n2 are reached on different ways through the graph and
		// we don't have to worry. I guess.
		// Otherwise we start the deleter from the node with the greater
		// depth.
		if (e1 != null && e2 != null && e1.getDepth() != e2.getDepth()) {
			greaterDepth = (e1.getDepth() < e2.getDepth()) ? n2 : n1;
			subcs.mDeleteStarters.add(greaterDepth);
			// remove the entry from the link, so the deleter won't travel it
			l.removeSubscriptionEntry(sub);
			sub.removeGraphElement(l);
		}
	}

	/**
	 * Everything that is in mDeletedMetadataHolders is not valid if they are
	 * contained in mAddedMetadataHolder as well.
	 * 
	 * FIXME: I have no idea what this is good for, honestly!
	 */
	private void removeUndeleted() {
		
					CollectionHelper.provideSetFor(MetadataHolder.class);
		Set<MetadataHolder> intersection = CollectionHelper.provideSetFor(MetadataHolder.class);

		SubscriptionChangeState subcs;
		
		for (Entry<Subscription, SubscriptionChangeState> entry :
												mChangedSubscriptions.entrySet()) {
			subcs = entry.getValue();
		
			// I always sucked at set theory
			intersection.addAll(subcs.mAddedMetadataHolders);
			intersection.retainAll(subcs.mDeletedMetadataHolders);
			subcs.mAddedMetadataHolders.removeAll(intersection);
			subcs.mDeletedMetadataHolders.removeAll(intersection);

			intersection.clear();
		}
	}

	private void cleanup() {
		mChangedSubscriptions.clear();
		mChangedPublishers.clear();
		mChangedMetadata.clear();
	}

	private boolean isLink(GraphElement ge) {
		return ge instanceof Link;
	}

	private boolean isNode(GraphElement ge) {
		return ge instanceof Node;
	}
	
	private boolean matchesOnSubscription(GraphElement ge, Metadata m, Subscription sub) {
		Filter lFilter = sub.getSearchRequest().getMatchLinksFilter();
		Filter rFilter = sub.getSearchRequest().getResultFilter();
		
		if (ge instanceof Node)
			return m.matchesFilter(rFilter);
		else if (ge instanceof Link)
			return (m.matchesFilter(lFilter) && m.matchesFilter(rFilter));
		else
			throw new SystemErrorException("GraphElement not Link nor Identifier");
	}

	/**
	 * If a {@link SubscriptionObserver} calls getPollResultFor() the corresponding
	 * Publisher with the given sessionId is searched.
	 * 
	 * All of it's subscriptions are checked, to see if any of them has changes.
	 * If a subscription has changes either the search result, the update result,
	 * the delete result or the notify result has elements which might be of
	 * interest for the MAPC.
	 * 
	 * A {@link PollResult} is constructed which contains {@link SearchResult}
	 * objects for all the different result types and further for all
	 * subscriptions a client has.
	 * 
	 * thoughts:
	 * 	could this get too big?
	 * 
	 * @param sessionId
	 * @return
	 * @throws PollResultsTooBigException 
	 */
	public PollResult getPollResultFor(String sessionId) throws PollResultsTooBigException {
		Publisher pub = publisherRep.getPublisherBySessionId(sessionId);
		SubscriptionState subState = pub.getSubscriptionState();
		PollResult ret = null;
		
		if (!subState.isNotified())
			throw new SystemErrorException("getPollResultFor() but never notified");
		
		try {
			if (subState.isPollResultsTooBig()) {
				sLogger.debug(sName + ": PollResultsTooBig for "  + pub.getPublisherId());
				subState.resetPollResult();
				throw new PollResultsTooBigException("too big");
			}
			ret = subState.getPollResult();
			subState.resetPollResult();
			
			if (ret.isEmpty())
				throw new SystemErrorException("empty pollResult for poll");

			// Remove subscriptions that lead to an error
			for (Subscription sub : subState.getSubscriptions())
				if (sub.exceededSize())
					subState.removeSubscription(sub);
			
			return ret;
		} finally {
			subState.unsetNotified();
		}
	}

	/**
	 * Set the {@link SubscriptionObserver} to the given one.
	 * 
	 * @param subObs
	 * @throws AlreadyObservedException if another {@link SubscriptionObserver} was
	 *  registered before
	 */
	public void setSubscriptionObserver(SubscriptionObserver subObs) throws AlreadyObservedException {
		if (mObserver != null)
			throw new AlreadyObservedException("Only one SubscriptionObserver is allowed!");
		
		mObserver = subObs;
	}

	/**
	 * Simple helper method which calls pollResultAvailable() on the observer
	 * with the sessionId of the given publisher.
	 * 
	 * @param pub
	 */
	private void notifyNewPollResults(Publisher pub) {
		NullCheck.check(mObserver, "observer is null");
		
		SubscriptionState subState = pub.getSubscriptionState();
			
		// sanity check
		if (pub.getSessionId() == null)
			throw new SystemErrorException("Publisher " + pub.getPublisherId()
					+ " has no session");
		
		if (!subState.isNotified()) {
			sLogger.trace(sName + ": " + pub.getPublisherId() + 
					" has new poll results");
			subState.setNotified();
			mObserver.pollResultAvailable(pub.getSessionId());
		}
	}
	
	
	/**
	 * Helper method to prepare the notification of the observer with all
	 * publishers that have new results available.
	 */
	private void doResultNotifications() {
		sLogger.debug(sName + ": Notify SubscriptionObserver about new results...");
		for (Publisher p : mChangedPublishers) {
			sLogger.trace(sName + ": "+ p.getPublisherId()+ " has changed subscriptions");
			notifyNewPollResults(p);
		}
	}

	/**
	 * Go through all changed subscriptions and check whether one grew too big.
	 * If one did, mark it as such and remove the subscription from the graph.
	 * This way it won't be checked the next time, but will still exist in the
	 * publisher.
	 */
	private void markSubscriptionsGrewTooBig() {
		for (Subscription sub : mChangedSubscriptions.keySet()) {
			Publisher pub = sub.getPublisherReference();
			SubscriptionState subState = pub.getSubscriptionState();
			SearchRequest sreq = sub.getSearchRequest();
			String name = sub.getName();
			
			ModifiablePollResult pollResult = subState.getPollResult();
			int size = pollResult.getByteCountOf(name);
			int maxSize = mConf.getDefaultMaxSearchResultSize();
			
			if (sreq.maxSizeGiven())
				maxSize = sreq.getMaxResultSize();
			
			if (size > maxSize) {
				pollResult.removeResultsOf(name);
				pollResult.addErrorResult(name);
				sub.setExceededSize();
				clearContainers(sub);
				sLogger.trace(sName + ": Subscription " + name + " of " + 
						pub.getPublisherId() + " grew too big");
			}
		}
	}

	/**
	 * If a publisher stores more than its set max-poll-result-size,
	 * he gets marked by us.
	 */
	private void markPublisherPollResultTooBig() {
		for (Publisher pub : mChangedPublishers) {
			SubscriptionState subState = pub.getSubscriptionState();
			PollResult pr = subState.getPollResult();
			
			
			if (subState.isPollResultsTooBig()) {
				sLogger.trace(sName + ": " + pub.getPublisherId() + 
						" PollResult grew too big (was=" + pr.getByteCount() + 
						"bytes allowed=" + subState.getMaxPollSize() + " bytes)");
				
				// Remove all subscriptions from this publisher
				removeSubscriptions(pub);
				subState.setPollResultsTooBig();
			}
		}
	}

	/**
	 * Remove all active subscriptions of the given {@link Publisher} and
	 * further remove the subscriptions from the graph.
	 * 
	 * @param pub
	 */
	void removeSubscriptions(Publisher pub) {
		NullCheck.check(pub, "pub is null");
		SubscriptionState subState = pub.getSubscriptionState();
		
		for (Subscription sub : subState.getSubscriptions())
			clearContainers(sub);
	
		subState.clearSubscriptions();
		subState.unsetNotified();
		subState.resetPollResult();
	}
	
	/**
	 * Go through all {@link MetadataContainer} objects that this subscription
	 * is attached to and remove the reference to the given {@link Subscription}
	 * object.
	 * 
	 * @param sub
	 */
	void clearContainers(Subscription sub) {
		
		for (GraphElement ge : sub.getContainers()) {
			ge.removeSubscriptionEntry(sub);
			sub.removeGraphElement(ge);
		}
		
		// Sanity
		if (sub.getContainers().size() != 0)
			throw new SystemErrorException("Subscription" + sub.getName()
					+ " should not have any containers anymore");
	}

	private void increaseLogicalTimeStamp() {
		
		long old = mLogicalTimeStamp++;
		
		if (mLogicalTimeStamp < old)
			throw new SystemErrorException("timestamp overflow");

		sLogger.trace(sName + ": Logical timestamp: " + mLogicalTimeStamp);
	}
	
	public long getLogicalTimeStamp() {
		return mLogicalTimeStamp;
	}

	private void sanitizeState() {
		sLogger.trace(sName + ": Checking graph state");
		for (GraphElement dummyGe : mGraph.getAllElements()) {
			GraphElement ge = null;
			if (isNode(dummyGe))
				ge = mGraph.getNodeFor(((Node)dummyGe).getIdentifier());
			else if (isLink(dummyGe))
				ge = mGraph.getLinkFor(((Link)dummyGe).getNode1().getIdentifier(),
						((Link)dummyGe).getNode2().getIdentifier());
			else
				throw new SystemErrorException("IMPOSSIBLE");
				
			
			if (ge.getRemovedSubscriptionEntries().size() > 0) {
				sLogger.warn(sName + ": Found removed sub entries on " + dummyGe +
						" for ");
				for (SubscriptionEntry entry : dummyGe.getSubscriptionEntries())
					sLogger.warn(sName + ": " + entry.getSubscription());
			}
			
			for (MetadataHolder mh : ge.getMetadataHolder())
				if (mh.getState() != MetadataState.UNCHANGED)
					sLogger.warn(sName + ": Found metadata with state " +
							mh.getState() + " on " + ge);
		}
	}
	
	private void setHasChanges(Subscription sub) {
		Publisher pub = sub.getPublisherReference();
		
		if (!mChangedPublishers.contains(pub)) {
			sLogger.trace(sName + ": Adding " + pub + " to be notified");
			mChangedPublishers.add(pub);
			
		}

		sub.setChanged();
	}
	
	private SubscriptionChangeState getSubChangeState(Subscription sub) {
		SubscriptionChangeState subcs = mChangedSubscriptions.get(sub);
		
		if (subcs == null) {
			subcs = new SubscriptionChangeState();
			mChangedSubscriptions.put(sub, subcs);
		}
		
		return subcs;
	}
	
	/**
	 * Helper class to encapsulate the state of a changing subscription
	 */
	private class SubscriptionChangeState {
		private final Set<Node> mDeleteStarters;
		private final Set<Node> mContinueStarter;
		private final Set<MetadataHolder> mNotifyMetadataHolders;
		private final Set<MetadataHolder> mDeletedMetadataHolders;
		private final Set<MetadataHolder> mAddedMetadataHolders;
		
		public SubscriptionChangeState() {
			mDeleteStarters =
				CollectionHelper.provideSetFor(Node.class);
			mContinueStarter =
				CollectionHelper.provideSetFor(Node.class);
			mNotifyMetadataHolders = 
				CollectionHelper.provideSetFor(MetadataHolder.class);
			mDeletedMetadataHolders = 
				CollectionHelper.provideSetFor(MetadataHolder.class);
			mAddedMetadataHolders = 
				CollectionHelper.provideSetFor(MetadataHolder.class);
		}
	}
}
