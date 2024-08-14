package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author SMS-Derfflinger
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String ID;
    private String message;
    private List<String> parents;
    private Map<String, String> blobID; // file to blob, absolute path
    private Date commitTime;

    public Commit(String commitMessage, List<String> parents, Map<String, String> blobID, Date date) {
        this.message = commitMessage;
        this.parents = parents;
        this.blobID = blobID;
        this.commitTime = date;
        this.ID = sha1(blobID.toString(), parents.toString(), commitMessage, getTimeStamp(date));
    }

    public Commit() {
        this("initial commit", new LinkedList<>(), new HashMap<>(), new Date(0));
    }

    private static String getTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public void saveCommit() {
        File commitFile = join(OBJECT_DIR, this.ID);
        writeObject(commitFile, this);
    }

    public String getID() {
        return this.ID;
    }

    public Map<String, String> getBlobID() {
        return this.blobID;
    }

    public Set<String> getKeySet() {
        return new HashSet<>(this.blobID.keySet());
    }

    public List<String> getParents() {
        return parents;
    }

    public boolean containBlob(Blob blob) {
        return this.blobID.containsValue(blob.getID());
    }

    public boolean containPath(String filename) {
        return this.blobID.containsKey(filename);
    }

    public String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(this.commitTime);
    }

    public String getMessage() {
        return this.message;
    }
}
