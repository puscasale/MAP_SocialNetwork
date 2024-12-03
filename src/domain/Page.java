package domain;

public class Page<E> {
    private Iterable<E> elementsOnPage;

    private int totalElementCount;

    /**
     * Constructor to initialize a page with the given elements and total element count.
     *
     * @param elementsOnPage An iterable collection of elements for this page.
     * @param totalElementCount The total number of elements in the entire dataset.
     */
    public Page(Iterable<E> elementsOnPage, int totalElementCount) {
        this.elementsOnPage = elementsOnPage;
        this.totalElementCount = totalElementCount;
    }

    /**
     * Retrieves the elements that are displayed on the current page.
     *
     * @return An iterable collection of elements for the current page.
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Retrieves the total number of elements available in the entire dataset.
     *
     * @return The total count of elements across all pages.
     */
    public int getTotalElementCount() {
        return totalElementCount;
    }
}
