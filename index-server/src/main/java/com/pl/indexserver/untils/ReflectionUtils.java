package com.pl.indexserver.untils;

import com.pl.indexserver.config.LogCompareName;
import com.pl.indexserver.model.SpeechcraftModelDto;
import com.pl.model.Speechcraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 反射工具类.
 */
public class ReflectionUtils {

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * 获取两个对象带DataName注解同名属性内容不相同的列表
     *
     * @param oldObj old对象
     * @param newObj new对象
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    public static List<Map<String, Object>> compareTwoClass(Object oldObj, Object newObj) throws ClassNotFoundException, IllegalAccessException {
        List<Map<String, Object>> list = new ArrayList<>();
        //获取对象的class
        Class<?> clazz1 = oldObj.getClass();
        Class<?> clazz2 = newObj.getClass();
        //获取对象的属性列表
        List<Field> field1List = new ArrayList<>();
        while (clazz1 != null) { //当父类为null的时候说明到达了最上层的父类(Object类).
            field1List.addAll(Arrays.asList(clazz1.getDeclaredFields()));
            clazz1 = clazz1.getSuperclass(); //得到父类,然后赋给自己
        }
        List<Field> field2List = new ArrayList<>();
        while (clazz2 != null) {
            field2List.addAll(Arrays.asList(clazz2.getDeclaredFields()));
            clazz2 = clazz2.getSuperclass();
        }
        StringBuilder sb = new StringBuilder();
        //遍历属性列表field1
        for (int i = 0; i < field1List.size(); i++) {
            LogCompareName logCompareName = field1List.get(i).getAnnotation(LogCompareName.class);
            if (logCompareName != null) {
                //遍历属性列表field2
                for (int j = 0; j < field2List.size(); j++) {
                    //如果field1[i]属性名与field2List.get(j)属性名内容相同
                    if (field1List.get(i).getName().equals(field2List.get(j).getName())) {
                        field1List.get(i).setAccessible(true);
                        field2List.get(j).setAccessible(true);
                        // 如果新字段的值为空，跳过比较
                        if (null == field2List.get(j).get(newObj)) {
                            continue;
                        }
                        //如果field1List.get(i)属性值与field2List.get(j)属性值内容不相同
                        if (!compareTwo(field1List.get(i).get(oldObj), field2List.get(j).get(newObj))) {
                            if (logCompareName.isKeywordCompare()) {
                                Map<String, Object> compareMap = ReflectionUtils.compareTwoKeyword(field1List.get(i).get(oldObj), field2List.get(j).get(newObj), logCompareName.separator());
                                if (StringUtils.isEmpty(logCompareName.name())) {
                                    compareMap.put("name", field1List.get(i).getName());
                                } else {
                                    compareMap.put("name", logCompareName.name());
                                }
                                compareMap.put("type", "keyword");
                                list.add(compareMap);
                            } else {
                                Map<String, Object> map2 = new HashMap<>();
                                if (StringUtils.isEmpty(logCompareName.name())) {
                                    map2.put("name", field1List.get(i).getName());
                                } else {
                                    map2.put("name", logCompareName.name());
                                }
                                map2.put("old", field1List.get(i).get(oldObj));
                                map2.put("new", field2List.get(j).get(newObj));
                                map2.put("type", "normal");
                                list.add(map2);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return list;
    }

    /**
     * 对比两个数据是否内容相同
     *
     * @param object1,object2
     * @return boolean类型
     */
    private static boolean compareTwo(Object object1, Object object2) {

        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null && object2 != null) {
            return false;
        }
        return object1.equals(object2);
    }

    /**
     * 通过关键字比对两个对象信息
     *
     * @param oldObj
     * @param newObj
     * @param separator
     * @return
     */
    private static Map<String, Object> compareTwoKeyword(Object oldObj, Object newObj, String separator) {
        String oldStr = "";
        if (!StringUtils.isEmpty(oldObj)) {
            oldStr = oldObj.toString();
        }
        String newStr = "";
        if (!StringUtils.isEmpty(newObj)) {
            newStr = newObj.toString();
        }
        StringBuilder separatorSb = new StringBuilder();
        if (separator.matches("[\\*\\.\\?\\+\\$\\^\\[\\]\\(\\)\\{\\}\\|\\\\/]")) {
            separatorSb.append("\\").append(separator);
        } else {
            separatorSb.append(separator);
        }
        String[] oldStrs = oldStr.split(separatorSb.toString());
        String[] newStrs = newStr.split(separatorSb.toString());
        Map<String, String> oldMap = new HashMap<>();
        for (String str : oldStrs) {
            oldMap.put(str, str);
        }
        Map<String, String> newMap = new HashMap<>();
        for (String str : newStrs) {
            newMap.put(str, str);
        }
        StringBuilder deleteSb = new StringBuilder();
        for (String key : oldMap.keySet()) {
            if (!newMap.containsKey(key)) {
                deleteSb.append(key).append(",");
            }
        }
        String deleteStr = deleteSb.toString();
        if (deleteSb.length() > 0) {
            deleteStr = deleteSb.substring(0, deleteSb.length() - 1);
        }
        StringBuilder addSb = new StringBuilder();
        for (String key : newMap.keySet()) {
            if (!oldMap.containsKey(key)) {
                addSb.append(key).append(",");
            }
        }
        String addStr = addSb.toString();
        if (addSb.length() > 0) {
            addStr = addStr.substring(0, addSb.length() - 1);
        }
        Map<String, Object> compareResult = new HashMap<>();
        compareResult.put("old", deleteStr);
        compareResult.put("new", addStr);
        return compareResult;
    }

    public static void main(String[] args) throws IllegalAccessException, ClassNotFoundException {
        Speechcraft oldObj = new Speechcraft();
        oldObj.setContent("sssss");
        oldObj.setFoucs("你好&不好&非常好&哈哈哈哈&");
        SpeechcraftModelDto newObj = new SpeechcraftModelDto();
        newObj.setContent("234444444");
        newObj.setFoucs("s及世纪大道");
        List<Map<String, Object>> changelist = ReflectionUtils.compareTwoClass(oldObj, newObj);
        StringBuilder updateSb = new StringBuilder();
        for (Map<String, Object> map : changelist) {
            if ("keyword".equals(map.get("type"))) {
                if (StringUtils.isEmpty(map.get("old"))) {
                    updateSb.append(String.format("将[%s]新增[%s];", map.get("name"), map.get("new")));
                } else if (StringUtils.isEmpty(map.get("new"))) {
                    updateSb.append(String.format("将[%s]删除[%s];", map.get("name"), map.get("old")));
                } else {
                    updateSb.append(String.format("将[%s]删除[%s],新增[%s];", map.get("name"), map.get("old"), map.get("new")));
                }
            } else {
                if (StringUtils.isEmpty(map.get("old"))) {
                    updateSb.append(String.format("将[%s]修改为[%s];", map.get("name"), map.get("new")));
                } else {
                    updateSb.append(String.format("将[%s]从[%s]修改为[%s];", map.get("name"), map.get("old"), map.get("new")));
                }
            }
        }
        String updateStr = updateSb.toString();
        if (updateStr.length() > 0) {
            updateStr = updateStr.substring(0, updateStr.length() - 1) + ".";
        }

        System.out.println(updateStr);
    }
}

