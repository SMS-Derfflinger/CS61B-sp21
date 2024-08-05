package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

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
        if (currentCommit.containBlob(blob) || addStage.containBlob(blob)) {
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

    private static void checkCommitMessage(String message) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
    }

    private static void checkStage(Map<String, String> addStageMap, Map<String, String> removeStageMap) {
        if (addStageMap.isEmpty() && removeStageMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
    }

    private static Map<String, String> mixBlobID(Map<String, String> blobMap, Map<String, String> addStageMap, Map<String, String> removeStageMap) {
        for (String key : addStageMap.keySet()) {
            blobMap.put(key, addStageMap.get(key));
        }
        for (String key : removeStageMap.keySet()) {
            blobMap.remove(key);
        }
        return blobMap;
    }

    private static List<String> getParents() {
        List<String> parents = currentCommit.getParents();
        parents.add(currentCommit.getID());
        return parents;
    }

    private static Commit createCommit(String message) {
        Map<String, String> addStageMap = addStage.getBlobID();
        Map<String, String> removeStageMap = removeStage.getBlobID();
        Map<String, String> blobID = currentCommit.getBlobID();

        checkStage(addStageMap, removeStageMap);
        Map<String, String> mixBlobID = mixBlobID(blobID, addStageMap, removeStageMap);
        List<String> parents = getParents();
        return new Commit(message, parents, mixBlobID, new Date());
    }

    private static void saveCommit(Commit newCommit) {
        newCommit.saveCommit();
        addStage = new Stage();
        addStage.saveStage(ADD_FILE);
        currentCommit = newCommit;
        String branch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, branch);
        writeContents(branchFile, currentCommit.getID());
    }

    /** commit command function*/
    public static void commitCommand(String message) {
        checkCommitMessage(message);
        addStage = stageFromFile(ADD_FILE);
        removeStage = stageFromFile(REMOVE_FILE);
        currentCommit = getCurrentCommit();
        Commit newCommit = createCommit(message);
        saveCommit(newCommit);
    }

    /** rm command function*/
    public static void rmCommand(String fileName) {
        File removeFile = join(CWD, fileName);
        String filePath = removeFile.getPath();

        addStage = stageFromFile(ADD_FILE);
        removeStage = stageFromFile(REMOVE_FILE);
        currentCommit = getCurrentCommit();
        // Unstage the file if it is currently staged for addition
        if (addStage.containPath(filePath)) {
            addStage.delete(filePath);
            addStage.saveStage(ADD_FILE);
            // stage it for removal and remove the file from the working directory
        } else if (currentCommit.containPath(filePath)) {
            removeStage.add(new Blob(removeFile));
            removeStage.saveStage(REMOVE_FILE);

            if (removeFile.exists()) {
                removeFile.delete();
            }
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
}
