package ru.fedichkindenis.SQLCmd.model;

/**
 * База для поля таблицы
 */
abstract class Field {

    private String nameFiled;

    Field(String nameFiled) {
        this.nameFiled = nameFiled;
    }

    String getNameField() {
        return nameFiled;
    }

}
