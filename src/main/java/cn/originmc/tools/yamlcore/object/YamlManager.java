package cn.originmc.tools.yamlcore.object;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class YamlManager {
    private String dirName;
    private List<YamlElement> yamlElements=new ArrayList<>();
    private JavaPlugin plugin;
    private File path;

    /**
     * 构造方法
     * @param plugin 插件实例
     * @param dirName 文件夹名
     */
    public YamlManager(JavaPlugin plugin,String dirName,boolean deep){
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        setDirName(dirName);
        getData(deep);
    }
    public YamlManager(JavaPlugin plugin,String dirName){
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        setDirName(dirName);
        getData(false);
    }
    public YamlManager(JavaPlugin plugin,boolean deep){
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        getData(deep);
    }
    public YamlManager(JavaPlugin plugin){
        setPlugin(plugin);
        setPath(getPlugin().getDataFolder());
        getData(false);
    }
    /**
     * 构造方法
     * @param plugin 插件实例
     * @param path 路径
     * @param dirName 文件夹名
     */
    public YamlManager(JavaPlugin plugin,String path,String dirName,boolean deep){
        File file=new File(path);
        setPath(file);
        setPlugin(plugin);
        setDirName(dirName);
        getData(deep);
    }
    public YamlManager(JavaPlugin plugin,String path,String dirName){
        File file=new File(path);
        setPath(file);
        setPlugin(plugin);
        setDirName(dirName);
        getData(false);
    }

    /**
     * 获取路径下所有文件数据
     * 以YamlElement形式存储为列表
     */
    public void getData(boolean deep){
        yamlElements.clear();
        File dir;
        if (getDirName()==null){
            dir = new File(path.getPath());
        }else {
            dir = new File(path, dirName);
        }
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }
        getAllElement(dir, yamlElements,deep);
    }

    /**
     * 递归获取目录下所有文件并读入
     * @param fileInput 目录
     * @param elements 数据元素列表
     */
    public void getAllElement(File fileInput, List<YamlElement> elements) {
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                getAllElement(file, elements);
            } else {
                //收集配置文件数据
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                YamlElement ye=new YamlElement(file.getName().replace(".yml",""),file,yml);
                elements.add(ye);
            }
        }
    }
    public void getAllElement(File fileInput, List<YamlElement> elements,boolean deep) {
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (deep){
                    getAllElement(file, elements);
                }
            } else {
                //收集配置文件数据
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                YamlElement ye=new YamlElement(file.getName().replace(".yml",""),file,yml);
                elements.add(ye);
            }
        }
    }
    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public List<YamlElement> getYamlElements() {
        return yamlElements;
    }

    public void setYamlElements(List<YamlElement> yamlElements) {
        this.yamlElements = yamlElements;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 获取所有数据元素的ID列表
     * @return 数据元素ID列表
     */
    public List<String> getIdList(){
        List<String> idList=new ArrayList<>();
        for (YamlElement yamlElement : yamlElements) {
            idList.add(yamlElement.getId());
        }
        return idList;
    }

    /**
     * 获取数据元素数量
     * @return 数量
     */
    public int getAmount(){
        return yamlElements.size();
    }

    /**
     * 获取某个数据元素
     * @param id 数据元素ID
     * @return 对应数据元素实例
     */
    public YamlElement getElement(String id){
        for (YamlElement yamlElement : yamlElements) {
            if (yamlElement.getId().equalsIgnoreCase(id)){
                return yamlElement;
            }
        }
        return null;
    }

    /**
     * 设置数据元素
     * @param ye 数据元素
     * @return 设置结果
     */
    public boolean setElement(YamlElement ye){
        for (int i=0;i<yamlElements.size();i++){
            if (yamlElements.get(i).getId().equalsIgnoreCase(ye.getId())){
                yamlElements.set(i,ye);
                return true;
            }
        }
        return false;
    }

    /**
     * 移除某个数据元素
     * @param id 数据元素ID
     * @return 移除结果
     */
    public boolean delElement(String id){
        for (int i=0;i<yamlElements.size();i++){
            if (yamlElements.get(i).getId().equalsIgnoreCase(id)){
                if(!yamlElements.get(i).getFile().delete()){
                    return false;
                }
                yamlElements.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在某个ID的数据元素
     * @param id 数据元素ID
     * @return 结果
     */
    public boolean hasElement(String id){
        for (YamlElement yamlElement : yamlElements) {
            if (yamlElement.getId().equalsIgnoreCase(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据ID获取Yaml文件控制柄
     * @param id 数据元素ID
     * @return Yaml文件控制柄
     */
    public YamlConfiguration getYaml(String id){
        return getElement(id).getYml();
    }

    /**
     * 设置某个ID的数据元素的Yaml控制柄
     * @param id 数据元素ID
     * @param yml Yaml控制柄
     * @return 结果
     */
    public boolean setYaml(String id,YamlConfiguration yml){
        YamlElement ye=getElement(id);
        if (ye==null){
            return false;
        }
        ye.setYml(yml);
        return setElement(ye);
    }

    /**
     * 获得某个Key下所有下级key
     * @param id 数据元素ID
     * @param key 上级Key
     * @param deep 是否包含所有子节点Key
     * @return 指定Key下所有子节点Key
     */
    public List<String> getKeyList(String id,String key,boolean deep){
        YamlElement ye=getElement(id);
        YamlConfiguration ym=ye.getYml();
        if (ym.getConfigurationSection(key)==null){
            return null;
        }
        return new ArrayList<>(ym.getConfigurationSection(key).getKeys(deep));
    }
    /**
     * 修改内存中的数据元素 不会保存
     * @param id 数据元素ID
     * @param key 数据键
     * @param value 对应值
     * @return 结果
     */
    public boolean set(String id,String key,Object value){
        YamlElement ye=getElement(id);
        YamlConfiguration ym=ye.getYml();
        ym.set(key,value);
        ye.setYml(ym);
        return setElement(ye);
    }
    public boolean has(String id,String key){
        Object object= getElement(id).getYml().get(key,null);
        return object!=null;
    }
    /**
     * 获取对应ID的数据元素中数据键存储对象
     * @param id 数据元素ID
     * @param key 数据键
     * @return 存储对象
     */
    public Object get(String id,String key){
        return getElement(id).getYml().get(key,null);
    }
    public Object get(String id,String key,Object defaultObject){
        return getElement(id).getYml().get(key,defaultObject);
    }

    /**
     * 将数据元素内存中的数据保存到文件
     * @param id 数据元素ID
     */
    public void save(String id){
        getElement(id).save();
    }

    /**
     * 保存所有内存中的数据元素
     */
    public void saveAll(){
        for (YamlElement yamlElement : yamlElements) {
            yamlElement.save();
        }
    }

    /**
     * 新建一个数据元素并创建文件
     * @param id 数据元素ID
     * @return 创建结果
     */
    public boolean create(String id){
        if (hasElement(id)){
            return false;
        }
        File file=new File(path,dirName+"/"+id+".yml");
        try {
            if(!file.createNewFile()){
                return false;
            }
            YamlElement ye=new YamlElement(id,file,YamlConfiguration.loadConfiguration(file));
            yamlElements.add(ye);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 遍历所有YamlElement
     * @param consumer 消费者函数
     */
    public void forEach(Consumer<YamlElement> consumer) {
        for (YamlElement yamlElement : yamlElements) {
            consumer.accept(yamlElement);
        }
    }
    public void setPath(File path) {
        this.path = path;
    }
    public String getPath(){
        return path.getPath();
    }
}
