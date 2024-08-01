package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author SMS-Derfflinger
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory:
     * .gitlet
     * |-- object
     * |-- refs
     * |   +-- heads
     * |-- HEAD
     * |-- addStage
     * +-- removeStage
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "head");
    public static final File ADD_FILE = join(GITLET_DIR, "addStage");
    public static final File REMOVE_FILE = join(GITLET_DIR, "removeStage");

    private static Commit currentCommit;
    private static Stage addStage;
    private static Stage removeStage;

    private static void initCommit() {
        currentCommit = new Commit();
        currentCommit.saveCommit();
    }

    private static void initHEAD() {
        writeContents(HEAD_FILE, "master");
    }

    private static void initHeads() {
        File headFile = join(HEADS_DIR, "master");
        writeContents(headFile, currentCommit.getID());
    }

    /** init command function*/
    public static void setUpPersistence() {
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        initCommit();
        initHEAD();
        initHeads();
    }

    private static Stage stageFromFile(File stageFile) {
        if (!stageFile.exists()) {
            return new Stage();
        } else {
            return readObject(stageFile, Stage.class);
        }
    }

    private static void checkFilePath(File filePath) {
        if (!filePath.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    private static String getCurrentBranch() {
        //System.out.println(readContentsAsString(HEAD_FILE));
        return readContentsAsString(HEAD_FILE);
    }

    private static String getCurrentCommitID() {
        String branch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, branch);
        //System.out.println(readContentsAsString(branchFile));
        return readContentsAsString(branchFile);
    }

    private static Commit getCurrentCommit() {
        String commitID = getCurrentCommitID();
        File commitFile = join(OBJECT_DIR, commitID);
        //System.out.println(commitFile);
        return readObject(commitFile, Commit.class);
    }

    private static void addBlob(Blob blob) {
        // check if the current working version of the file is
        // identical to the version in the current commit.
        if (currentCommit.getBlobID().containsValue(blob.getID())) {
            return;
        }
        if (addStage.containBlob(blob)) {
            return;
        }

        blob.saveBlob();
        addStage.add(blob);
        addStage.saveStage(ADD_FILE);
    }

    /** add command function*/
    public static void addCommand(String fileName) {
        File filePath = join(CWD, fileName);
        checkFilePath(filePath);

        //System.out.println(filePath.getPath());
        addStage = stageFromFile(ADD_FILE);
        removeStage = stageFromFile(REMOVE_FILE);
        currentCommit = getCurrentCommit();
        Blob addBlob = new Blob(filePath);
        addBlob(addBlob);
    }
}
