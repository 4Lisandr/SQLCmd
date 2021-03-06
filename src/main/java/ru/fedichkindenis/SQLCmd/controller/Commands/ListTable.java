package ru.fedichkindenis.SQLCmd.controller.Commands;

import ru.fedichkindenis.SQLCmd.model.DBManager;
import ru.fedichkindenis.SQLCmd.util.StringUtil;
import ru.fedichkindenis.SQLCmd.view.AlignWrite;
import ru.fedichkindenis.SQLCmd.view.ViewDecorator;

import java.util.List;

/**
 * Команда для вывода списка таблиц
 * Формат команды: list-table
 * Пример команды: list-table
 */
public class ListTable implements Command {

    private DBManager dbManager;
    private ViewDecorator view;
    private String textCommand;

    public ListTable(DBManager dbManager, ViewDecorator view, String textCommand) {
        this.dbManager = dbManager;
        this.view = view;
        this.textCommand = textCommand;
    }

    @Override
    public void execute() {

        if(!validateCommand()) {
            throw new IllegalArgumentException("Указан не верный формат команды");
        }

        List<String> listTable = dbManager.listTable();
        view.write(listTable, AlignWrite.VERTICAL);
    }

    private boolean validateCommand() {

        return !StringUtil.isEmpty(textCommand) && textCommand.equals("list-table");

    }
}
