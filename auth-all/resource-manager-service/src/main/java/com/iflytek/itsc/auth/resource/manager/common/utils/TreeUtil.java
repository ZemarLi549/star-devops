package com.iflytek.itsc.auth.resource.manager.common.utils;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @date 2020/9/23 下午3:13
 */
public class TreeUtil {

    /**
     * 将list构造为树形结构
     *
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> dataList) {
        return buildTree(dataList, "id", "pid");
    }

    /**
     * 将list构造为树形结构
     *
     * @param dataList
     * @param idName
     * @param pidNam
     * @param <T>
     * @return
     */
    public static <T> List<TreeNode<T>> buildTree(List<T> dataList, String idName, String pidNam) {
        List<TreeNode<T>> nodes = dataList.stream().map(
                item -> new TreeNode<>(ReflectionUtils.getFieldValue(item, idName), ReflectionUtils.getFieldValue(item, pidNam), item)
        ).collect(Collectors.toList());
        Map<Object, List<TreeNode<T>>> sub = nodes.stream().filter(node -> node.getPid() != null).collect(Collectors.groupingBy(TreeNode::getPid));
        //nodes.forEach(node -> node.setSub(sub.get(node.getId())));
        List<TreeNode<T>> tree = new ArrayList<>();
        for (TreeNode<T> node : nodes) {
            if (Constant.DEFAULT_PARENT_ID == node.getPid() || sub.containsKey(node.getId())) {
                node.setSub(sub.get(node.getId()));
                tree.add(node);
            }
        }
        return tree;
    }


    public static class TreeNode<T> {
        private Object id;
        private Object pid;
        private T data;
        private List<TreeNode<T>> sub = new ArrayList<>();

        public TreeNode() {
        }

        public TreeNode(Object id, Object pid, T data) {
            this.id = id;
            this.pid = pid;
            this.data = data;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getPid() {
            return pid;
        }

        public void setPid(Object pid) {
            this.pid = pid;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<TreeNode<T>> getSub() {
            return sub;
        }

        public void setSub(List<TreeNode<T>> sub) {
            this.sub = sub;
        }

        @Override
        public String toString() {
            return "TreeNode{" +
                    "id=" + id +
                    ", pid=" + pid +
                    ", data=" + data +
                    ", sub=" + sub +
                    '}';
        }
    }
}
