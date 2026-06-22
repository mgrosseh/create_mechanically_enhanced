package com.mirandnyan.cme.content.equipment.mechanical_parts;

import com.mirandnyan.cme.CMETranslations;

public class CMEMaterial {
    CMETranslations.LangEntry lang;
    String name;

    public CMEMaterial(String name, CMETranslations.LangEntry lang) {
        this.name = name;
        this.lang = lang;
    }
}

