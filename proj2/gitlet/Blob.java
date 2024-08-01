package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.OBJECT_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet blob object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author SMS-Derfflinger
 */
public class Blob implements Serializable {
    private String ID;
    private byte[] blobBytes;
    private File blobFile;

    public Blob(File file) {
        this.blobFile = file;
        this.blobBytes = readContents(file);
        this.ID = sha1(this.blobFile.getPath(), this.blobBytes);
    }

    public void saveBlob() {
        File blobFile = join(OBJECT_DIR, this.ID);
        writeObject(blobFile, this);
    }

    public String getPath() {
        return this.blobFile.getPath();
    }

    public String getID() {
        return ID;
    }
}
