package duke;

import java.time.LocalDate;

/**
 * Parse an input given to the Duke to a command.
 */
public class Parser {
    private enum CommandType {
        find, list, done, delete, todo, deadline, event, bye
    }

    private static String parseDescription(String[] data, String timeDivider) throws DukeException {

        if (data.length < 1 || data[1].equals(timeDivider)) {
            throw new DukeException("The description can't be blank :(.");
        }

        String description = data[1];
        int idx = 2;
        while (idx < data.length && !data[idx].equals(timeDivider)) {
            description += " " + data[idx];
            idx++;
        }

        return description;
    }

    private static LocalDate parseTime(String[] data, String timeDivider) throws DukeException {
        int idx = 1;
        while (idx < data.length && !data[idx].equals(timeDivider)) {
            idx++;
        }
        idx++;

        if (idx >= data.length) {
            throw new DukeException(
                    "Please specify the deadline date in this format: \"" + timeDivider + " <date>\". ");
        }

        String time = data[idx];
        idx++;
        while (idx < data.length) {
            time += " " + data[idx];
            idx++;
        }

        LocalDate date;

        try {
            date = LocalDate.parse(time);
        } catch (Exception e) {
            throw new DukeException("Please enter the date in yyyy-mm-dd format.");
        }

        return date;
    }

    /**
     * Parse the input string to a command.
     *
     * @param line the string to be parsed.
     * @return command from the parsed string.
     * @throws DukeException input string is an invalid command.
     */
    public static Command parse(String line) throws DukeException {
        String[] commandLine = line.split(" ");
        int idx;

        CommandType commandEnum;
        Command command;

        try {
            commandEnum = CommandType.valueOf(commandLine[0]);
        } catch (Exception e) {
            throw new DukeException("I'm sorry but I don't recognize your commandLine T__T.");
        }

        switch (commandEnum) {
        case find:
            command = new FindCommand(Parser.parseDescription(commandLine, ""));
            break;
        case list:
            command = new ListCommand();
            break;
        case done:
            idx = Integer.parseInt(commandLine[1]);
            command = new DoneCommand(idx);
            break;
        case delete:
            idx = Integer.parseInt(commandLine[1]);
            return new DeleteCommand(idx);
        case todo:
            command = new AddCommand(new Todo(Parser.parseDescription(commandLine, "")));
            break;
        case deadline:
            command = new AddCommand(new Deadline(Parser.parseDescription(commandLine, "/by"),
                    Parser.parseTime(commandLine, "/by")));
            break;
        case event:
            command = new AddCommand(
                    new Event(Parser.parseDescription(commandLine, "/at"), Parser.parseTime(commandLine, "/at")));
            break;
        case bye:
            return new ByeCommand();
        default:
            throw new DukeException("I'm sorry but I don't recognize your commandLine T__T.");
        }
        return command;
    }
}
