package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

/** Represents a gitlet stage (addstage/removestage) object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author SMS-Derfflinger
 */
public class Stage implements Serializable {
    private Map<String, String> blobID; // Path to ID

    public Stage() {
        this.blobID = new HashMap<>();
    }

    public boolean containBlob(Blob blob) {
        return this.blobID.containsValue(blob.getID());
    }

    public boolean containPath(String path) {
        return this.blobID.containsKey(path);
    }

    public void delete(String key) {
        this.blobID.remove(key);
    }

    public void delete(Blob blob) {
        this.blobID.remove(blob.getPath(), blob.getID());
    }

    public void add(Blob blob) {
        this.blobID.put(blob.getPath(), blob.getID());
    }

    public void saveStage(File file) {
        writeObject(file, this);
    }

    public Map<String, String> getBlobID() {
        return this.blobID;
    }

    public void reset() {
        this.blobID = new HashMap<>();

    }
}
