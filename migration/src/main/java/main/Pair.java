package main;

/**
 * Sometimes a map with a single element is needed. Java has no object like this
 * so I created one.
 */
public class Pair<T1, T2> {
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
