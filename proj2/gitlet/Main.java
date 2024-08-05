package gitlet;

import static gitlet.Repository.GITLET_DIR;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author SMS-Derfflinger
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        args = new String[]{"log"};
        checkArgc(args);
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                init();
                break;
            case "add":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.addCommand(args[1]);
                break;
            case "commit":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.commitCommand(args[1]);
                break;
            case "rm":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.rmCommand(args[1]);
                break;
            case "log":
                checkGitlet();
                validateNumArgs(args, 1);
                Repository.logCommand();
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void checkArgc(String[] args) {
        if (args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

    private static void validateNumArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            Repository.setUpPersistence();
        }
    }

    private static void checkGitlet() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
