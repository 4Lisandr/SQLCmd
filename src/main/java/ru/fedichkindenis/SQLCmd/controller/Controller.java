package ru.fedichkindenis.SQLCmd.controller;

import ru.fedichkindenis.SQLCmd.controller.Commands.Command;
import ru.fedichkindenis.SQLCmd.controller.Commands.CommandFactory;
import ru.fedichkindenis.SQLCmd.controller.Commands.ExitException;
import ru.fedichkindenis.SQLCmd.model.DBManager;
import ru.fedichkindenis.SQLCmd.view.ViewDecorator;

/**
 * Класс контролер для управления основной логикой программы
 */
public class Controller {

    private ViewDecorator view;
    private DBManager dbManager;

    public Controller(ViewDecorator view, DBManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
    }

    public void run() {

        view.write("Приветствую тебя пользователь!");
        view.write("Для начала работы с ситемой установи соединение с базой данных с помощью команды: ");
        view.write("connect|host|port|dbName|userName|password");

        while (true) {

            try {

                view.write("");
                view.write("Введите команду (help для справки): ");
                String textCommand = view.read();

                CommandFactory commandFactory = new CommandFactory(dbManager, view, textCommand);

                Command command = commandFactory.createCommand();
                command.execute();
            } catch (ExitException e) {
                break;
            } catch (Exception e) {

                view.write("Произошла ошибка: " + e.getMessage());
            }
        }
    }
}
