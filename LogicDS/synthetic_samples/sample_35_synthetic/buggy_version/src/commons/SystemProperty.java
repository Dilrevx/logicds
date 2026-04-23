package commons;

public class SystemProperty<T> {
    private T value;
    private Class<T> type;

    private SystemProperty(T defaultValue, Class<T> type) {
        this.value = defaultValue;
        this.type = type;
    }

    public T getValue() {
        return value;
    }
    
    public Class<T> getType() {
        return type;
    }

    public static class Builder<T> {
        private T defaultValue;
        private Class<T> type;

        private Builder(Class<T> type) {
            this.type = type;
        }

        public static <E> Builder<E> ofType(Class<E> type) {
            return new Builder<E>(type);
        }

        public Builder<T> setKey(String key) { return this; }
        public Builder<T> setBaseClass(Class<?> baseClass) { return this; }
        
        @SuppressWarnings("unchecked")
        public Builder<T> setDefaultValue(T defaultValue) {
            if (this.type.equals(Class.class) && !(defaultValue instanceof Class)) {
                throw new IllegalArgumentException("Default value must be a Class instance for SystemProperty<Class>");
            }
            this.defaultValue = defaultValue;
            return this;
        }
        
        public interface PropertyListener<T> { void propertyUpdated(T newValue); }
        public Builder<T> addListener(PropertyListener<T> listener) { return this; }
        
        public Builder<T> setDynamic(boolean dynamic) { return this; }

        public SystemProperty<T> build() {
            return new SystemProperty<T>(defaultValue, type);
        }
    }
}