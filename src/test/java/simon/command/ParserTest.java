package simon.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import simon.SimonException;
import simon.task.Task;

public class ParserTest {

    @Test
    public void parseAddTask_validToDoInput_todoAdded() throws SimonException {
        String input = "todo Sample Task";
        Task task = Parser.parseAddTask(input, Parser.Command.TODO);
        assertEquals("Sample Task", task.getTaskName());
    }

    @Test
    public void parseAddTask_invalidInput_exceptionThrown() {
        String input = "todo";
        assertThrows(SimonException.class, () -> Parser.parseAddTask(input, Parser.Command.TODO));
    }

    // Assuming the functionality for DEADLINE and EVENT
    @Test
    public void parseAddTask_validDeadlineInput_deadlineAdded() throws SimonException {
        String input = "deadline Sample Task /by 01/01/2023 1800";
        Task task = Parser.parseAddTask(input, Parser.Command.DEADLINE);
        assertEquals("Sample Task", task.getTaskName());
    }

    @Test
    public void parseAddTask_validEventInput_eventAdded() throws SimonException {
        String input = "event Sample Event /from 01/01/2023 1800 /to 02/01/2023 1800";
        Task task = Parser.parseAddTask(input, Parser.Command.EVENT);
        assertEquals("Sample Event", task.getTaskName());
    }

    @Test
    public void parseAddTask_invalidDeadlineFormat_exceptionThrown() {
        String input = "deadline Sample Task";
        assertThrows(SimonException.class, () -> Parser.parseAddTask(input, Parser.Command.DEADLINE));
    }

    @Test
    public void parseAddTask_invalidEventFormat_exceptionThrown() {
        String input = "event Sample Event /from 01/01/2023 1800";
        assertThrows(SimonException.class, () -> Parser.parseAddTask(input, Parser.Command.EVENT));
    }
}
