package com.ecnu.note.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onion
 * @date 2020/2/12 -11:53 上午
 */
@Data
@NoArgsConstructor
public class MindMap{
    private List<MindMap> children;
    private String label;
    private String value;

    public MindMap(String label) {
        this.label = label;
        this.value = label;
        children = new ArrayList<>();
    }

    public MindMap(String label, String value, boolean hasChildren) {
        this.label = label;
        this.value = value;
        if (hasChildren) {
            children = new ArrayList<>();
        }
    }


    public void addComponent(MindMap component) {
        children.add(component);
    }
}
