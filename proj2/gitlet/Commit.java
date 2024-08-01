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
    private Map<String, String> blobID;
    private Date commitTime;
    private String timeStamp;

    public Commit(String commitMessage, List<String> parents, Map<String, String> blobID, Date date) {
        this.message = commitMessage;
        this.parents = parents;
        this.blobID = blobID;
        this.commitTime = date;
        this.timeStamp = getTimeStamp(date);
        this.ID = sha1(this.timeStamp, commitMessage, parents.toString(), blobID.toString());
    }

    public Commit() {
        this("initial Commit", new LinkedList<>(), new HashMap<>(), new Date(0));
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
}
