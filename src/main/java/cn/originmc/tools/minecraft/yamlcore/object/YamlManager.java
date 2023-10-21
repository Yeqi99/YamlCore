package cn.originmc.tools.minecraft.yamlcore.object;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@Setter
@SuppressWarnings("unused")
public class YamlManager {
    private String dirName;
    private List<YamlElement> yamlElements = new ArrayList<>();
    private JavaPlugin plugin;
    private File path;

    /**
     * 构造方法
     *
     * @param plugin  插件实例
     * @param dirName 文件夹名
     */
    public YamlManager(JavaPlugin plugin, String dirName, boolean deep) {
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        setDirName(dirName);
        getData(deep);
    }

    public YamlManager(JavaPlugin plugin, String dirName) {
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        setDirName(dirName);
        getData(false);
    }

    public YamlManager(JavaPlugin plugin, boolean deep) {
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        getData(deep);
    }

    public YamlManager(JavaPlugin plugin) {
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        getData(false);
    }

    /**
     * 构造方法
     *
     * @param plugin  插件实例
     * @param path    路径
     * @param dirName 文件夹名
     */
    public YamlManager(JavaPlugin plugin, String path, String dirName, boolean deep) {
        File file = new File(path);
        setPath(file);
        setPlugin(plugin);
        setDirName(dirName);
        getData(deep);
    }

    public YamlManager(JavaPlugin plugin, String path, String dirName) {
        File file = new File(path);
        setPath(file);
        setPlugin(plugin);
        setDirName(dirName);
        getData(false);
    }

    /**
     * 获取路径下所有文件数据
     *
     * <p>
     * 以 {@link YamlElement} 形式存储为列表
     * </p>
     */
    public void getData(boolean deep) {
        yamlElements.clear();

        File dir = getDirName() == null ?
                new File(path.getPath()) : new File(path, dirName);

        if (dir.exists()) {
            getAllElement(dir, yamlElements, deep);
            return;
        }

        // noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
    }

    /**
     * 递归获取目录下所有文件并读入
     *
     * @param fileInput 目录
     * @param elements  数据元素列表
     */
    public void getAllElement(File fileInput, List<YamlElement> elements) {
        File[] files = fileInput.listFiles();

        if (files == null) {
            return;
        }

        Stream.of(files)
                .forEach(file -> {
                    if (file.isDirectory()) {
                        getAllElement(file, elements);
                        return;
                    }

                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    String fileNameReplaced = file.getName().replace(".yml", "");
                    YamlElement yamlElement = new YamlElement(fileNameReplaced, file, yml);

                    elements.add(yamlElement);
                });
    }

    public void getAllElement(File fileInput, List<YamlElement> elements, boolean deep) {
        File[] files = fileInput.listFiles();

        if (files == null) {
            return;
        }

        Stream.of(files)
                .filter(Objects::nonNull)
                .forEach(file -> {
                    String fileName = file.getName();

                    if (file.isDirectory() && deep) {
                        getAllElement(file, elements, true);
                        return;
                    }

                    if (file.isFile() && fileName.endsWith(".yml")) {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        String fileNameReplaced = fileName.replace(".yml", "");
                        YamlElement yamlElement = new YamlElement(fileNameReplaced, file, yml);

                        elements.add(yamlElement);
                    }
                });
    }

    /**
     * 获取所有数据元素的ID列表
     *
     * @return 数据元素ID列表
     */
    public List<String> getIdList() {
        return yamlElements.stream()
                .map(YamlElement::getId)
                .collect(Collectors.toList());
    }

    /**
     * 获取数据元素数量
     *
     * @return 数量
     */
    public int getAmount() {
        return yamlElements.size();
    }

    /**
     * 获取某个数据元素
     *
     * @param id 数据元素ID
     * @return 对应数据元素实例
     */
    public YamlElement getElement(String id) {
        return yamlElements.stream()
                .filter(e -> e.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 设置数据元素
     *
     * @param ye 数据元素
     * @return 设置结果
     */
    public boolean setElement(YamlElement ye) {
        OptionalInt indexOptional = IntStream.range(0, yamlElements.size())
                .filter(i -> yamlElements.get(i).getId().equalsIgnoreCase(ye.getId()))
                .findFirst();

        if (!indexOptional.isPresent()) {
            return false;
        }

        yamlElements.set(indexOptional.getAsInt(), ye);
        return true;
    }

    /**
     * 移除某个数据元素
     *
     * @param id 数据元素ID
     * @return 移除结果
     */
    public boolean delElement(String id) {
        Optional<YamlElement> elementToRemove = yamlElements.stream()
                .filter(e -> e.getId().equalsIgnoreCase(id))
                .findFirst();

        if (!elementToRemove.isPresent()) {
            return false;
        }

        File file = elementToRemove.get().getFile();

        if (!file.delete()) {
            return false;
        }

        return yamlElements.remove(elementToRemove.get());
    }

    /**
     * 是否存在某个ID的数据元素
     *
     * @param id 数据元素ID
     * @return 结果
     */
    public boolean hasElement(String id) {
        return yamlElements.stream()
                .anyMatch(e -> e.getId().equalsIgnoreCase(id));
    }

    /**
     * 根据 ID 获取 Yaml 文件控制柄
     *
     * @param id 数据元素ID
     * @return Yaml文件控制柄
     */
    public YamlConfiguration getYaml(String id) {
        return getElement(id).getYml();
    }

    /**
     * 设置某个 ID 的数据元素的 Yaml 控制柄
     *
     * @param id  数据元素ID
     * @param yml Yaml 控制柄
     * @return 结果
     */
    public boolean setYaml(String id, YamlConfiguration yml) {
        YamlElement yamlElement = getElement(id);

        if (yamlElement == null) {
            return false;
        }

        yamlElement.setYml(yml);
        return setElement(yamlElement);
    }

    /**
     * 获得某个Key下所有下级key
     *
     * @param id   数据元素ID
     * @param key  上级Key
     * @param deep 是否包含所有子节点Key
     * @return 指定Key下所有子节点Key
     */
    public List<String> getKeyList(String id, String key, boolean deep) {
        YamlElement yamlElement = getElement(id);
        YamlConfiguration yamlConfiguration = yamlElement.getYml();

        ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);

        return configurationSection == null ?
                null : new ArrayList<>(configurationSection.getKeys(deep));
    }

    /**
     * 修改内存中的数据元素 不会保存
     *
     * @param id    数据元素ID
     * @param key   数据键
     * @param value 对应值
     * @return 结果
     */
    public boolean set(String id, String key, Object value) {
        YamlElement yamlElement = getElement(id);
        YamlConfiguration yamlConfiguration = yamlElement.getYml();

        yamlConfiguration.set(key, value);
        yamlElement.setYml(yamlConfiguration);

        return setElement(yamlElement);
    }

    public boolean has(String id, String key) {
        return getElement(id).getYml().get(key, null) != null;
    }

    /**
     * 获取对应 ID 的数据元素中数据键存储对象
     *
     * @param id  数据元素ID
     * @param key 数据键
     * @return 存储对象
     */
    public Object get(String id, String key) {
        return getElement(id).getYml().get(key, null);
    }

    public Object get(String id, String key, Object defaultObject) {
        return getElement(id).getYml().get(key, defaultObject);
    }

    /**
     * 将数据元素内存中的数据保存到文件
     *
     * @param id 数据元素ID
     */
    public void save(String id) {
        getElement(id).save();
    }

    /**
     * 保存所有内存中的数据元素
     */
    public void saveAll() {
        yamlElements.forEach(YamlElement::save);
    }

    /**
     * 新建一个数据元素并创建文件
     *
     * @param id 数据元素ID
     * @return 创建结果
     */
    @SneakyThrows
    public boolean create(String id) {
        if (hasElement(id)) {
            return false;
        }

        File file = new File(
                path, dirName + "/" + id + ".yml"
        );

        if (!file.createNewFile()) {
            return false;
        }

        YamlElement yamlElement = new YamlElement(
                id, file, YamlConfiguration.loadConfiguration(file)
        );

        yamlElements.add(yamlElement);

        return true;
    }

    /**
     * 遍历所有 YamlElement
     *
     * @param consumer 消费者函数
     */
    public void forEach(Consumer<YamlElement> consumer) {
        yamlElements.forEach(consumer);
    }
}
