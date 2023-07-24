package cn.originmc.tools.minecraft.yamlcore.object;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlElement {
    public YamlElement(String id, File file, YamlConfiguration yml){
        setId(id);
        setFile(file);
        setYml(yml);
    }
    private String id;
    private YamlConfiguration yml;
    private File file;
    /**
     * 修改内存中的数据 不会保存
     * @param key 数据键
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
        Object object = yml.get(key, null);
        return object != null;
    }

    /**
     * 获取数据键存储对象
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
     * 将数据保存到文件
     */
    public void save(){
        try {
            getYml().save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public YamlConfiguration getYml() {
        return yml;
    }

    public void setYml(YamlConfiguration yml) {
        this.yml = yml;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
