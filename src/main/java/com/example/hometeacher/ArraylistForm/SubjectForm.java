package com.example.hometeacher.ArraylistForm;

public class SubjectForm {

    private String group;
    private String name;
    private boolean isSelected;

    public SubjectForm(String group, String name, boolean isSelected) {
        this.group = group;
        this.name = name;
        this.isSelected = isSelected;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}