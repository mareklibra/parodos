package com.redhat.parodos.notification.util;

import static java.util.Objects.isNull;

import com.redhat.parodos.notification.enums.SearchCriteria;
import com.redhat.parodos.notification.enums.State;

/**
 * Notification records search util
 *
 * @author Annel Ketcha (Github: anludke)
 */
public class SearchUtil {

	public static SearchCriteria getSearchCriteria(State state, String searchTerm) {
		boolean isStateUnset = isNull(state);
		boolean isSearchTermUnset = isNull(searchTerm) || searchTerm.isEmpty();

		if (isStateUnset && isSearchTermUnset) {
			return SearchCriteria.BY_USERNAME;
		}
		else if (isSearchTermUnset) {
			switch (state) {
				case UNREAD:
					return SearchCriteria.BY_USERNAME_AND_STATE_UNREAD;
				case ARCHIVED:
					return SearchCriteria.BY_USERNAME_AND_STATE_ARCHIVED;
			}
		}
		else if (isStateUnset) {
			return SearchCriteria.BY_USERNAME_AND_SEARCH_TERM;
		}
		throw new RuntimeException("Search by state and search term combined not supported");
	}

}
