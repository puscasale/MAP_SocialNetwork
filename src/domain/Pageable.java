package domain;

public class Pageable {
    private int pageNumber;

    private int pageSize;

    /**
     * Constructor to initialize a Pageable object with the given page size and page number.
     *
     * @param pageSize The number of elements per page.
     * @param pageNumber The page number (starting from 0).
     */
    public Pageable(int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Retrieves the page number of the current page in the pagination.
     *
     * @return The page number, starting from 0 for the first page.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Retrieves the page size, i.e., the number of elements per page.
     *
     * @return The number of elements to display per page.
     */
    public int getPageSize() {
        return pageSize;
    }
}
