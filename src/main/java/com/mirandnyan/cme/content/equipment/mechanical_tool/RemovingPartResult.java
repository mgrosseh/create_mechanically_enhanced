package com.mirandnyan.cme.content.equipment.mechanical_tool;

public class RemovingPartResult {
    public enum Type {
        IMPOSSIBLE,
        SUCCESS,
        IS_PARENT_OF;
    }
    public int reason;
    public Type type;
    private RemovingPartResult(Type type) {
        this.type = type;
    }
    private RemovingPartResult(int reason) {
        type = Type.IS_PARENT_OF;
        this.reason = reason;
    }
    public static RemovingPartResult impossible() {
        return new RemovingPartResult(Type.IMPOSSIBLE);
    }
    public static RemovingPartResult success() {
        return new RemovingPartResult(Type.SUCCESS);
    }
    public static RemovingPartResult isParentOf(int parentId) {
        return new RemovingPartResult(parentId);
    }
}
