package cn.originmc.tools.minecraft.yamlcore.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class YamlElement {
    private String id;
    private File file;
    private YamlConfiguration yml;

    /**
     * 修改内存中的数据 不会保存
     *
     * @param key   数据键
     * @param value 对应值
     */
    public void set(String key, Object value) {
        yml.set(key, value);
    }

    public void setAndSave(String key, Object value) {
        yml.set(key, value);
        save();
    }

    public boolean has(String key) {
        return yml.get(key, null) != null;
    }

    /**
     * 获取数据键存储对象
     *
     * @param key 数据键
     * @return 存储对象
     */
    public Object get(String key) {
        return yml.get(key, null);
    }

    public Object get(String key, Object defaultObject) {
        return yml.get(key, defaultObject);
    }

    /**
     * 将数据保存到文件。
     */
    @SneakyThrows
    public void save() {
        getYml().save(getFile());
    }
}
