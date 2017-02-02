package com.redstoner.modules.nametags;

public enum Rank {
    VISITOR, MEMBER, BUILDER, TRUSTED, TRAININGMOD, MOD, ADMIN;
    
    public String getScoreboardName() {
        switch (this) {
            case VISITOR:
                return "g_visitor";
            case MEMBER:
                return "f_member";
            case BUILDER:
                return "e_builder";
            case TRUSTED:
                return "d_trusted";
            case TRAININGMOD:
                return "c_trainingmod";
            case MOD:
                return "b_mod";
            case ADMIN:
                return "a_admin";
            default:
                return "g_visitor";
        }
    }
    
    public String getPermission() {
        switch (this) {
            case VISITOR:
                return "group.visitor";
            case MEMBER:
                return "group.member";
            case BUILDER:
                return "group.builder";
            case TRUSTED:
                return "group.trusted";
            case TRAININGMOD:
                return "group.trainingmod";
            case MOD:
                return "group.mod";
            case ADMIN:
                return "group.admin";
            default:
                return "group.visitor";
        }
    }
    
    public int getPriority() {
        switch (this) {
            case VISITOR:
                return 1;
            case MEMBER:
                return 2;
            case BUILDER:
                return 3;
            case TRUSTED:
                return 4;
            case TRAININGMOD:
                return 5;
            case MOD:
                return 6;
            case ADMIN:
                return 7;
            default:
                return 1;
        }
    }
    
    public String getColor() {
        switch (this) {
            case VISITOR:
                return "gray";
            case MEMBER:
                return "white";
            case BUILDER:
                return "green";
            case TRUSTED:
                return "dark_aqua";
            case TRAININGMOD:
                return "red";
            case MOD:
                return "red";
            case ADMIN:
                return "dark_red";
            default:
                return "gray";
        }
    }
}
