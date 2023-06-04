package com.example.hometeacher.ArraylistForm;

public class ColorForm {

    private String color;
    private boolean isSelected;

    public ColorForm(String color, boolean isSelected) {

        this.color = color;
        this.isSelected = isSelected;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}