package domain;

import java.util.Objects;

/**
 * Generic Tuple class representing a pair of two elements.
 * @param <E1> type of the first element in the tuple
 * @param <E2> type of the second element in the tuple
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /**
     * Constructor for Tuple class.
     * Initializes the tuple with the given elements.
     * @param e1 the first element of the tuple
     * @param e2 the second element of the tuple
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Retrieves the first element (left) of the tuple.
     * @return the first element of the tuple
     */
    public E1 getLeft() {
        return this.e1;
    }

    /**
     * Sets the first element (left) of the tuple.
     * @param e1 the new first element of the tuple
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * Retrieves the second element (right) of the tuple.
     * @return the second element of the tuple
     */
    public E2 getRight() {
        return this.e2;
    }

    /**
     * Sets the second element (right) of the tuple.
     * @param e2 the new second element of the tuple
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /**
     * Provides a string representation of the tuple.
     * @return a string in the format "e1,e2" where e1 and e2 are the elements of the tuple
     */
    public String toString() {
        return this.e1 + "," + this.e2;
    }

    /**
     * Checks if this tuple is equal to another object.
     * Two tuples are equal if both their elements are equal.
     * @param obj the object to compare with this tuple
     * @return true if the object is a tuple with equal elements, false otherwise
     */
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple)obj).e1) && this.e2.equals(((Tuple)obj).e2);
    }

    /**
     * Generates a hash code for the tuple based on its elements.
     * @return a hash code value for this tuple
     */
    public int hashCode() {
        return Objects.hash(this.e1, this.e2);
    }
}
