package top.aimixer.modules.memory.entityscore;

public interface BaseEntityStore {

    String get(String key, String defaultValue);

    void set(String key, String defaultValue);

    void delete(String key);

    boolean exists(String key);

    void clear();
}
