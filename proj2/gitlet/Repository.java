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

    private static Stage getAddStage() {
        return stageFromFile(ADD_FILE);
    }

    private static Stage getRemoveStage() {
        return stageFromFile(REMOVE_FILE);
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

    private static Commit getCommitByID(String commitID, String message) {
        File commitFile = join(OBJECT_DIR, commitID);
        if (!commitFile.exists()) {
            exitFailed(message);
        }
        return readObject(commitFile, Commit.class);
    }

    private static Commit getCommitByID(String commitID) {
        return getCommitByID(commitID, "");
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
        if (currentCommit.containBlob(blob) && !removeStage.containBlob(blob) || addStage.containBlob(blob)) {
            return;
        }

        if (!removeStage.containBlob(blob)) {
            blob.saveBlob();
            addStage.add(blob);
            addStage.saveStage(ADD_FILE);
        } else {
            removeStage.delete(blob);
            removeStage.saveStage(REMOVE_FILE);
        }
    }

    /** add command function*/
    public static void addCommand(String fileName) {
        File filePath = join(CWD, fileName);
        checkFilePath(filePath);

        addStage = getAddStage();
        removeStage = getRemoveStage();
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
        removeStage = new Stage();
        removeStage.saveStage(REMOVE_FILE);
        currentCommit = newCommit;
        String branch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, branch);
        writeContents(branchFile, currentCommit.getID());
    }

    /** commit command function*/
    public static void commitCommand(String message) {
        checkCommitMessage(message);
        addStage = getAddStage();
        removeStage = getRemoveStage();
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
            Blob targetBlob = getBlobByFileName(currentCommit, filePath);
            removeStage.add(targetBlob);
            removeStage.saveStage(REMOVE_FILE);

            if (removeFile.exists()) {
                removeFile.delete();
            }
        } else {
            exitFailed("No reason to remove the file.");
        }
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
            commit = getCommitByID(parent);
            if (commit != null) {
                printMessage(commit);
            }
        }
    }

    /** global-log command function*/
    public static void globalLogCommand() {
        List<String> blobList = plainFilenamesIn(OBJECT_DIR);
        Commit commit;

        List<Commit> commitList = new LinkedList<>();
        if (blobList != null) {
            for (String ID : blobList) {
                try {
                    commit = getCommitByID(ID);
                    if (commit != null) {
                        printMessage(commit);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void printCommitIDs(List<String> commitIDs) {
        if (commitIDs.isEmpty()) {
            exitFailed("Found no commit with that message.");
        }
        for (String ID : commitIDs) {
            System.out.println(ID);
        }
    }

    /** find command function*/
    public static void findCommand(String message) {
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        Commit commit;
        List<String> commitIDs = new LinkedList<>();
        if (commitList != null) {
            for (String ID : commitList) {
                try {
                    commit = getCommitByID(ID);
                    if (commit != null && commit.getMessage().equals(message)) {
                        commitIDs.add(ID);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        printCommitIDs(commitIDs);
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

    /** check if the given branchname exists, exit if not exists*/
    private static void checkBranchExists(String branchName, String message) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        if (allBranches != null && !allBranches.contains(branchName)) {
            exitFailed(message);
        }
    }

    private static void checkCurrentBranch(String branchName, String message) {
        String currentBranch = getCurrentBranch();
        if (currentBranch.equals(branchName)) {
            exitFailed(message);
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

    private static void fileOperations(Commit newCommit) {
        Set<String> bothTrackedFiles = getBothTrackedFiles(newCommit);
        replaceFiles(bothTrackedFiles, newCommit);

        Set<String> onlyCurrentFiles = getOnlyFiles(currentCommit, newCommit);
        deleteFiles(onlyCurrentFiles);

        Set<String> onlyNewFiles = getOnlyFiles(newCommit, currentCommit);
        createFiles(onlyNewFiles, newCommit);
    }

    private static void resetStages() {
        addStage = new Stage();
        addStage.saveStage(ADD_FILE);
        removeStage = new Stage();
        removeStage.saveStage(REMOVE_FILE);
    }

    /** checkout [branchname] command function*/
    public static void checkoutBranchCommand(String branchName) {
        checkBranchExists(branchName, "No such branch exists.");
        checkCurrentBranch(branchName, "No need to checkout the current branch.");
        Commit newCommit = getCommitByBranch(branchName);
        currentCommit = getCurrentCommit();

        fileOperations(newCommit);
        resetStages();
    }

    /** check if the given branchname not exists, exit if exists*/
    private static void checkBranchNotExists(String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        if (allBranches != null && allBranches.contains(branchName)) {
            exitFailed("A branch with that name already exists.");
        }
    }

    /** branch [branchname] command function*/
    public static void branchCommand(String branchName) {
        checkBranchNotExists(branchName);
        currentCommit = getCurrentCommit();
        File headFile = join(HEADS_DIR, branchName);
        writeContents(headFile, currentCommit.getID());
    }

    /** rm-branch [branchname] command function*/
    public static void rmbranchCommand(String branchName) {
        checkBranchExists(branchName, "A branch with that name does not exist.");
        checkCurrentBranch(branchName, "Cannot remove the current branch.");

        File targetFile = join(HEADS_DIR, branchName);
        restrictedDelete(targetFile);
    }

    /** reset [commitID] command function*/
    public static void resetCommand(String commitID) {
        Commit newCommit = getCommitByID(commitID, "No commit with that id exists.");
        currentCommit = getCurrentCommit();

        fileOperations(newCommit);
        resetStages();
    }
}
