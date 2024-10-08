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
        //args = new String[]{"commit", "Main two files"};
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
            case "global-log":
                checkGitlet();
                validateNumArgs(args, 1);
                Repository.globalLogCommand();
                break;
            case "find":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.findCommand(args[1]);
                break;
            case "status":
                checkGitlet();
                validateNumArgs(args, 1);
                Repository.statusCommand();
                break;
            case "checkout":
                checkGitlet();
                switch (args.length) {
                    case 2:
                        Repository.checkoutBranchCommand(args[1]);
                        break;
                    case 3:
                        if (!args[1].equals("--")) {
                            exitFailed("Incorrect operands.");
                        }
                        Repository.checkoutFileCommand(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            exitFailed("Incorrect operands.");
                        }
                        Repository.checkoutFileCommand(args[1], args[3]);
                        break;
                    default:
                        exitFailed("Incorrect operands.");
                        break;
                }
                break;
            case "branch":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.branchCommand(args[1]);
                break;
            case "rm-branch":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.rmbranchCommand(args[1]);
                break;
            case "reset":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.resetCommand(args[1]);
                break;
            case "merge":
                checkGitlet();
                validateNumArgs(args, 2);
                Repository.mergeCommand(args[1]);
                break;
            default:
                exitFailed("No command with that name exists.");
        }
    }

    public static void exitFailed(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private static void checkArgc(String[] args) {
        if (args == null || args.length == 0) {
            exitFailed("Please enter a command.");
        }
    }

    private static void validateNumArgs(String[] args, int num) {
        if (args.length != num) {
            exitFailed("Incorrect operands.");
        }
    }

    private static void init() {
        if (GITLET_DIR.exists()) {
            exitFailed("A Gitlet version-control system already exists in the current directory.");
        } else {
            Repository.setUpPersistence();
        }
    }

    private static void checkGitlet() {
        if (!GITLET_DIR.exists()) {
            exitFailed("Not in an initialized Gitlet directory.");
        }
    }
}
