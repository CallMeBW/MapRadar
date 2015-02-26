package de.bwirth.mapradar.model;
/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 22.12.2014            <br></code>
 * Description:                    <br>
 */
public final class YelpSearch {

    public String searchTerm = null, location = null, categoryID;
    public int limit = -1, radius = -1;
    public Sorting sort = Sorting.NONE;

    public enum Sorting {
        NONE(-1), BEST_MATCH(0), DISTANCE(1), RATING(2);
        private final int i;

        public int getIndex() {
            return i;
        }

        Sorting(int i) {
            this.i = i;
        }
    }

    public YelpSearch category(String category) {
        this.categoryID = category;
        return this;
    }

    public YelpSearch(String location) {
        location(location);
    }

    public YelpSearch maxSearchResults(int limit) {
        this.limit = limit;
        return this;
    }

    public YelpSearch location(String location) {
        if (location != null) {
            this.location = location;
        }
        return this;
    }

    public YelpSearch radius(int radius) {
        this.radius = radius;
        return this;
    }

    public YelpSearch searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public YelpSearch sorting(Sorting sort) {
        this.sort = sort;
        return this;
    }
}
