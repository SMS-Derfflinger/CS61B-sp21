package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Main.exitFailed;
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
            exitFailed("File does not exist.");
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

    private static Commit getCommitByID(String commitID) {
        File commitFile = join(OBJECT_DIR, commitID);
        return readObject(commitFile, Commit.class);
    }

    private static Commit getCommitByBranch(String branchName) {
        File branchFilePath = join(HEADS_DIR, branchName);
        String commitID = readContentsAsString(branchFilePath);
        return getCommitByID(commitID);
    }

    private static Commit getCurrentCommit() {
        String commitID = getCurrentCommitID();
        return getCommitByID(commitID);
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
            exitFailed("Please enter a commit message.");
        }
    }

    private static void checkStage(Map<String, String> addStageMap, Map<String, String> removeStageMap) {
        if (addStageMap.isEmpty() && removeStageMap.isEmpty()) {
            exitFailed("No changes added to the commit.");
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
        parents.add(0, currentCommit.getID());
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
            exitFailed("No reason to remove the file.");
        }
    }

    private static Commit commitFromFile(String ID) {
        File findCommit = join(OBJECT_DIR, ID);
        if (findCommit.exists()) {
            return readObject(findCommit, Commit.class);
        }
        return null;
    }

    private static void printMessage(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getID());
        System.out.println("Date: " + commit.getTimeStamp());
        System.out.println(commit.getMessage() + "\n");
    }

    /** log command function*/
    public static void logCommand() {
        currentCommit = getCurrentCommit();
        printMessage(currentCommit);

        Commit commit;
        List<String> parents = currentCommit.getParents();
        for (String parent : parents) {
            commit = commitFromFile(parent);
            if (commit != null) {
                printMessage(commit);
            }
        }
    }

    /** global-log command function*/
    public static void globalLogCommand() {
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        Commit commit;
        if (commitList != null) {
            for (String ID : commitList) {
                commit = commitFromFile(ID);
                if (commit != null) {
                    printMessage(commit);
                }
            }
        }
    }

    /** find command function*/
    public static void findCommand(String message) {
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        Commit commit;
        if (commitList != null) {
            for (String ID : commitList) {
                commit = commitFromFile(ID);
                if (commit != null && commit.getMessage().equals(message)) {
                    printMessage(commit);
                }
            }
        }
    }

    private static void printBranch() {
        currentCommit = getCurrentCommit();
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        String currentBranch = readContentsAsString(HEAD_FILE);

        System.out.println("=== Branches ===");
        System.out.println("*" + currentBranch);
        if (branches != null) {
            for (String branch : branches) {
                if (branch.equals(currentBranch)) {
                    continue;
                }
                System.out.println(branch);
            }
        }
        System.out.println();
    }

    private static void printKeys(Map<String, String> blobID) {
        for (String key : blobID.keySet()) {
            key = key.substring(key.lastIndexOf("\\") + 1);
            key = key.substring(key.lastIndexOf("/") + 1);
            System.out.println(key);
        }
        System.out.println();
    }

    private static void printStaged() {
        addStage = stageFromFile(ADD_FILE);
        Map<String, String> blobID = addStage.getBlobID();

        System.out.println("=== Staged Files ===");
        printKeys(blobID);
    }

    private static void printRemoved() {
        removeStage = stageFromFile(REMOVE_FILE);
        Map<String, String> blobID = removeStage.getBlobID();

        System.out.println("=== Removed Files ===");
        printKeys(blobID);
    }

    private static void printModified() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    private static void printUntracked() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** status command function*/
    public static void statusCommand() {
        printBranch();
        printStaged();
        printRemoved();
        printModified();
        printUntracked();
    }

    private static void checkFilePath(Commit commit, String filePath) {
        if (!commit.containPath(filePath)) {
            exitFailed("File does not exist in that commit.");
        }
    }

    private static Blob getBlobByFileName(Commit commit, String filePath) {
        String blobID = commit.getBlobID().get(filePath);
        File BLOB_FILE = join(OBJECT_DIR, blobID);
        return readObject(BLOB_FILE, Blob.class);
    }

    private static void saveBlobToCWD(File targetFile, Blob targetBlob) {
        byte[] bytes = targetBlob.getBlobBytes();
        writeContents(targetFile, new String(bytes, StandardCharsets.UTF_8));
    }

    /** checkout -- [file name] command function*/
    public static void checkoutFileCommand(String fileName) {
        File targetFile = join(CWD, fileName);
        String filePath = targetFile.getPath();
        currentCommit = getCurrentCommit();
        checkFilePath(currentCommit, filePath);

        Blob targetBlob = getBlobByFileName(currentCommit, filePath);
        saveBlobToCWD(targetFile, targetBlob);
    }

    /** checkout [commit id] -- [file name] command function*/
    public static void checkoutFileCommand(String commitID, String fileName) {
        File targetFile = join(CWD, fileName);
        String filePath = targetFile.getPath();
        Commit commit = getCommitByID(commitID);
        checkFilePath(commit, filePath);

        Blob targetBlob = getBlobByFileName(commit, filePath);
        byte[] bytes = targetBlob.getBlobBytes();
        writeContents(targetFile, new String(bytes, StandardCharsets.UTF_8));
    }

    private static void checkBranchExists(String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        if (!allBranches.contains(branchName)) {
            exitFailed("No such branch exists.");
        }
    }

    private static void checkCurrentBranch(String branchName) {
        String currentBranch = getCurrentBranch();
        if (currentBranch.equals(branchName)) {
            exitFailed("No need to checkout the current branch.");
        }
    }

    // file absolute path
    private static Set<String> getBothTrackedFiles(Commit newCommit) {
        Set<String> currentFiles = currentCommit.getBlobID().keySet();
        Set<String> newFiles = newCommit.getBlobID().keySet();
        Set<String> bothFiles = new LinkedHashSet<>();
        for (String file : newFiles) {
            if (currentFiles.contains(file)) {
                bothFiles.add(file);
            }
        }
        return bothFiles;
    }

    private static void replaceFiles(Set<String> bothTrackedFiles, Commit newCommit) {
        if (bothTrackedFiles.isEmpty()) {
            return;
        }
        for (String fileName : bothTrackedFiles) {

            Blob blob = getBlobByFileName(newCommit, fileName);
            saveBlobToCWD(join(CWD, blob.getID()), blob);
        }
    }

    /** Find the files' absolute path that are only tracked in targetCommit*/
    private static Set<String> getOnlyFiles(Commit targetCommit, Commit newCommit) {
        Set<String> currentFiles = targetCommit.getBlobID().keySet();
        Set<String> newFiles = newCommit.getBlobID().keySet();
        for (String filePath : newFiles) {
            currentFiles.remove(filePath);
        }
        return currentFiles;
    }

    private static void deleteFiles(Set<String> onlyCurrentFiles) {
        if (onlyCurrentFiles.isEmpty()) {
            return;
        }
        for (String filePath : onlyCurrentFiles) {
            File file = join(filePath);
            restrictedDelete(file);
        }
    }

    private static void createFiles(Set<String> onlyNewFiles, Commit newCommit) {
        if (onlyNewFiles.isEmpty()) {
            return;
        }
        for (String fileName : onlyNewFiles) {
            File file = join(fileName);
            if (file.exists()) {
                exitFailed("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        replaceFiles(onlyNewFiles, newCommit);
    }

    /** checkout [branchname] command function*/
    public static void checkoutBranchCommand(String branchName) {
        checkBranchExists(branchName);
        checkCurrentBranch(branchName);
        Commit newCommit = getCommitByBranch(branchName);
        currentCommit = getCurrentCommit();

        Set<String> bothTrackedFiles = getBothTrackedFiles(newCommit);
        replaceFiles(bothTrackedFiles, newCommit);

        Set<String> onlyCurrentFiles = getOnlyFiles(currentCommit, newCommit);
        deleteFiles(onlyCurrentFiles);

        Set<String> onlyNewFiles = getOnlyFiles(newCommit, currentCommit);
        createFiles(onlyNewFiles, newCommit);
    }
}
