package tn.isg.economics.service;

@FunctionalInterface
public interface DataTransformer<T, R> {
    R transform(T input);
    default <V> DataTransformer<T, V> andThen(DataTransformer<R, V> after) {
        if (after == null) {
            throw new NullPointerException("After transformer cannot be null");
        }
        return (T t) -> after.transform(transform(t));
    }
}