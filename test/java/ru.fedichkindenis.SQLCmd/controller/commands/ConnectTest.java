package ru.fedichkindenis.SQLCmd.controller.commands;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ru.fedichkindenis.SQLCmd.controller.Commands.Command;
import ru.fedichkindenis.SQLCmd.controller.Commands.Connect;
import ru.fedichkindenis.SQLCmd.model.DBManager;
import ru.fedichkindenis.SQLCmd.view.ViewDecorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Денис on 23.07.2016.
 *
 * Тесты для команды connect
 */

public class ConnectTest {

    private DBManager dbManager;
    private ViewDecorator viewDecorator;
    private Command command;

    @Before
    public void setup() {
        dbManager = mock(DBManager.class);
        viewDecorator = mock(ViewDecorator.class);
    }

    @Test
    public void testIncorrectCommandFormat() {
        try {
            command = new Connect(dbManager, viewDecorator, "connect|d|12|fg|ere");
            command.execute();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Указан не верный формат команды", e.getMessage());
        }
    }

    @Test
    public void testCorrectCommandFormat() {

        command = new Connect(dbManager, viewDecorator, "connect|localhost|5433|cmd|postgres|mac");
        command.execute();

        shouldPrintView("[Соединение установлено!]");
    }

    private void shouldPrintView(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(viewDecorator, atLeastOnce()).write(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
