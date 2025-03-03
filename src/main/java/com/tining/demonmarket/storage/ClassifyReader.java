package com.tining.demonmarket.storage;

import com.google.common.collect.ComparisonChain;
import com.tining.demonmarket.common.util.BeanUtils;
import com.tining.demonmarket.storage.bean.Classify;
import com.tining.demonmarket.storage.bean.Group;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassifyReader extends IListReader<Classify>{

    private final static ClassifyReader INSTANCE = new ClassifyReader();

    public static ClassifyReader getInstance(){return INSTANCE;}

    private final static List<Classify> CLASSIFY_LIST = new CopyOnWriteArrayList<>();

    private final static List<Group> GROUP_LIST = new CopyOnWriteArrayList<>();

    private final static Map<Group, List<Classify>> GROUP_LIST_MAP = new HashMap<>();


    @Override
    void addToList(Classify newObj) {

    }

    public void addToGroupList(Group group) {
        for(Group old: GROUP_LIST){
            if(StringUtils.equals(old.getInfo(), group.getInfo())
            && StringUtils.equals(old.getName(), group.getName())
            ){
                old.setInfo(group.getInfo());
                old.setName(group.getName());
                saveAndReload();
                return;
            }
        }
        GROUP_LIST.add(group);
        saveAndReload();
    }

    /**
     * 获取group旗下内容
     * @param group
     * @return
     */
    public List<Classify> getClassifyList(Group group){
        return GROUP_LIST_MAP.get(group);
    }

    /**
     * 删除组
     * @param group
     * @return
     */
    public boolean removeGroup(Group group){
        for (int i = 0; i < GROUP_LIST.size(); i++) {
            Group old = GROUP_LIST.get(i);
            if (StringUtils.equals(old.getInfo(), group.getInfo())
                    && StringUtils.equals(old.getName(), group.getName())) {
                old.setInfo(group.getInfo());
                old.setName(group.getName());
                synchronized (GROUP_LIST) {
                    GROUP_LIST.remove(i);
                    saveAndReload();
                }
                break;
            }
        }
        return false;
    }

    @Override
    void delete(Classify obj) {
        synchronized (CLASSIFY_LIST) {
            for (int i = 0 ; i< CLASSIFY_LIST.size();i++) {
                if(StringUtils.equals(CLASSIFY_LIST.get(i).getType(),obj.getType())
                && StringUtils.equals(CLASSIFY_LIST.get(i).getInfo(),obj.getInfo())
                && StringUtils.equals(CLASSIFY_LIST.get(i).getName(),obj.getName())
                && StringUtils.equals(CLASSIFY_LIST.get(i).getGroup(),obj.getGroup())){
                    CLASSIFY_LIST.remove(i);
                    break;
                }
            }
            saveAndReload();
        }
    }



    @Override
    public List getForList() {
        return CLASSIFY_LIST;
    }

    public List getForGroupList(){
        return GROUP_LIST;
    }

    @Override
    void saveAndReload() {
        FileConfiguration config = ConfigReader.getConfigMap().get(ConfigFileNameEnum.CLASSIFY_DB_NAME.getName());
        String rootSection = ConfigFileNameEnum.CLASSIFY_DB_NAME.getRootSection();
        config.addDefault(rootSection, formatList(CLASSIFY_LIST));
        config.set(rootSection, formatList(CLASSIFY_LIST));

        ConfigReader.saveConfig(ConfigFileNameEnum.CLASSIFY_DB_NAME.getName(), config);


        config = ConfigReader.getConfigMap().get(ConfigFileNameEnum.GROUP_DB_NAME.getName());
        rootSection = ConfigFileNameEnum.GROUP_DB_NAME.getRootSection();
        config.addDefault(rootSection, formatListGroup(GROUP_LIST));
        config.set(rootSection, formatListGroup(GROUP_LIST));

        ConfigReader.saveConfig(ConfigFileNameEnum.GROUP_DB_NAME.getName(), config);
        reload();
    }

    @Override
    void reload() {
        List<Classify> classifyList = new CopyOnWriteArrayList<>();
        List<Group> groupList = new CopyOnWriteArrayList<>();

        List<Map<?,?>> classifySourceList = getSourceList(ConfigFileNameEnum.CLASSIFY_DB_NAME);
        for (Map<?, ?> map : classifySourceList) {
            Classify classify = new Classify();
            classify.setInfo((String) map.get("info"));
            classify.setGroup((String) map.get("group"));
            classify.setType((String) map.get("type"));
            classify.setName((String) map.get("name"));
            classifyList.add(classify);
        }
        List<Map<?,?>> groupSourceList = getSourceList(ConfigFileNameEnum.GROUP_DB_NAME);
        for (Map<?, ?> map : groupSourceList) {
            Group group = new Group();
            group.setInfo((String) map.get("info"));
            group.setName((String) map.get("name"));
            groupList.add(group);
        }

        // 读取文件
        synchronized (GROUP_LIST_MAP){
            synchronized (GROUP_LIST){
                synchronized (CLASSIFY_LIST){
                    CLASSIFY_LIST.clear();
                    CLASSIFY_LIST.addAll(classifyList);

                    GROUP_LIST.clear();
                    GROUP_LIST.addAll(groupList);

                    // 临时的Map，用于辅助分类
                    Map<String, Group> tempGroupMap = new HashMap<>();
                    for (Group group : GROUP_LIST) {
                        tempGroupMap.put(group.getName(), group);
                    }

                    // 首先清空GROUP_LIST_MAP，以防之前的内容干扰
                    GROUP_LIST_MAP.clear();

                    // 将CLASSIFY_LIST中的内容分类到GROUP_LIST_MAP中
                    for (Classify classify : CLASSIFY_LIST) {
                        String groupName = classify.getGroup();
                        Group group = tempGroupMap.get(groupName);
                        if (group != null) {
                            GROUP_LIST_MAP.computeIfAbsent(group, k -> new ArrayList<>()).add(classify);
                        }
                    }

                    for(Map.Entry<Group, List<Classify>> entry : GROUP_LIST_MAP.entrySet()){
                        if(Objects.isNull(entry.getValue())){
                            entry.setValue(new ArrayList<>());
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取资源对象
     * @param configFileNameEnum
     * @return
     */
    @Override
    public List<Map<?,?>> getSourceList(ConfigFileNameEnum configFileNameEnum){
        FileConfiguration config = ConfigReader.getConfigMap().get(configFileNameEnum.getName());
        return config.getMapList(configFileNameEnum.getRootSection());
    }

    @Override
    Classify get(int index) {
        return null;
    }

    @Override
    Classify get(String name) {
        return null;
    }

    @Override
    Classify deepGet(int index) {
        return null;
    }

    @Override
    Classify deepGet(String name) {
        return null;
    }

    @Override
    Classify readFromSource(Map<?, ?> entity) {
        return null;
    }

    protected List<Map<String,String>> formatListGroup(List<Group> list){
        List<Map<String,String>> formatList = new ArrayList<>();

        for (int i = 0 ; i < list.size();i ++){
            formatList.add(BeanUtils.convertClassToMap(list.get(i)));
        }
        return formatList;
    }

}
