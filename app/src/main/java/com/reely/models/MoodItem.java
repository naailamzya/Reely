package com.reely.models;
public class MoodItem {

    private final String key;
    private final String displayName;
    private final String emoji;
    private final int gradientStartRes;
    private final int gradientEndRes;
    private final int accentColorRes;

    private boolean isSelected = false;

    public MoodItem(String key, String displayName, String emoji,
                    int gradientStartRes, int gradientEndRes, int accentColorRes) {
        this.key = key;
        this.displayName = displayName;
        this.emoji = emoji;
        this.gradientStartRes = gradientStartRes;
        this.gradientEndRes = gradientEndRes;
        this.accentColorRes = accentColorRes;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getGradientStartRes() {
        return gradientStartRes;
    }

    public int getGradientEndRes() {
        return gradientEndRes;
    }

    public int getAccentColorRes() {
        return accentColorRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}